/**
 * Created by Raphael on 18/05/2016.
 */
var express = require('express');
var router = express.Router();

var users = require('../lib/users');
var stack = require('../lib/stack')(users);
var error = require('../lib/error');
var total = require('../lib/total');
var utils = require('../lib/utils');
var ADDR = require('../lib/address');
total.users = users;

var modbus = require('modbus-event')({ debug : false });

modbus.on('update', function(type, address, from, to){
    if(type === 'coils' && address == ADDR.BATTERY_STATUS)
        battery.state = to;
    if(type === 'coils' && address == ADDR.SW.QUARTO)
        users[0].sw = to;
    if(type === 'coils' && address == ADDR.SW.SALA)
        users[1].sw = to;
    if(type === 'coils' && address == ADDR.SW.COZINHA)
        users[2].sw = to;
    if(type === 'coils' && address == ADDR.SW.AREA)
        users[3].sw = to;
});

function updateBatteryStatus(status){
    // Salva o estado da bateria = 1
    modbus.callee(function(client, data, next){
        client.writeCoil(ADDR.BATTERY_STATUS, status).then(next);
    });
}

router.get('/', function(req, res){
    var ntotal = JSON.parse(JSON.stringify(total));
    ntotal.users = users;
    return res.json(ntotal);
});

var battery = { max : 1, min : 0.1, state : 0 };// 0 carregando, 1 descarregando
router.post('/', function(req, res){
    if(!req.cookies.index || !req.cookies.key) return res.status(402).end('ops.html');
    if(stack[req.cookies.index] == req.cookies.key || stack[req.cookies.index] == false){
        if(stack[req.cookies.index] == false) stack[req.cookies.index] = req.cookies.key;

        // Atualiza o gasto do usuário
        users[req.cookies.index].powerd = req.body.powerh;
        users[req.cookies.index].powerh += req.body.powerh;
        users[req.cookies.index].renewh += req.body.renewh;

        // Deriva o tempo para contar o gasto e geração por (2) segundo
        utils.timeout(1200, function(){
                total.powerd += req.body.powerh; // gasto por segundo
                total.renewd += req.body.renewh; // geração por segundo
            },
            function(){
                modbus.callee(function(client, data, next){
                    // Salva o gasto por segundo
                    client.writeRegister(ADDR.CONSUMO_DERIVADO,
                        utils.normalize(total.powerd, 1000))
                        .then(function(){
                            total.powerd = 0;
                            next();
                        });
                });
                modbus.callee(function(client, data, next){
                    // Salva o gerado por segundo
                    client.writeRegister(ADDR.RENOVAVEL_DERIVADO,
                        utils.normalize(total.renewd, 1000))
                        .then(function(){
                            total.renewd = 0;
                            next();
                        });
                });
            });

        // Atualiza o gasto e consumo total
        total.powerh += req.body.powerh; // consumo liquido total
        total.powerb += req.body.powerh; // consumo bruto total
        total.renewh += req.body.renewh; // gerado bruto total

        // Atualiza bateria
        if(battery.state){ // 1 - descarregando
            if(total.batt + req.body.renewh - req.body.powerh < battery.min){
                updateBatteryStatus(0);
                total.powerh += (battery.min - total.batt - req.body.renewh);
                total.batt = battery.min;
            }else{
                if(users[req.cookies.index].sw){
                    total.batt -= Math.max(req.body.powerh - req.body.renewh, 0);
                    total.powerh -= req.body.powerh;
                    users[req.cookies.index].powerd -= req.body.powerh;
                }
            }
        }else{ // 0 - carregando
            if(total.batt + req.body.renewh > battery.max){
                updateBatteryStatus(1);
                total.powerh -= (total.batt + req.body.renewh - battery.max);
                total.batt = battery.max;
            }else
                total.batt += req.body.renewh;
        }


        // Salva o consumo liquido
        modbus.callee(function(client, data, next){
            client.writeRegister(ADDR.CONSUMO_LIQUIDO,
                utils.normalize(total.powerh, 100)).then(next);
        });

        // Salva o consumo bruto
        modbus.callee(function(client, data, next){
            client.writeRegister(ADDR.CONSUMO_BRUTO,
                utils.normalize(total.powerb, 100)).then(next);
        });

        // Salva a bateria
        modbus.callee(function(client, data, next){
            client.writeRegister(ADDR.BATERIA,
                utils.normalize(total.batt, 100)).then(next);
        });

        updateIndividualConsume();
    }
    // Retorna os totais
    if(req.cookies.index){
        var ntotal = JSON.parse(JSON.stringify(total));
        delete ntotal.users;
        ntotal.user = users[req.cookies.index];
        return res.json(ntotal);
    }else{
        return res.json(total);
    }
});

function updateIndividualConsume(){
    modbus.callee(function(client, data, next){
        client.writeRegister(ADDR.CONSUMO.QUARTO,
            utils.normalize(users[0].powerd, 1000)).then(next);
    });
    modbus.callee(function(client, data, next){
        client.writeRegister(ADDR.CONSUMO.SALA,
            utils.normalize(users[1].powerd, 1000)).then(next);
    });
    modbus.callee(function(client, data, next){
        client.writeRegister(ADDR.CONSUMO.COZINHA,
            utils.normalize(users[2].powerd, 1000)).then(next);
    });
    modbus.callee(function(client, data, next){
        client.writeRegister(ADDR.CONSUMO.AREA,
            utils.normalize(users[3].powerd, 1000)).then(next);
    });
}

module.exports = router;