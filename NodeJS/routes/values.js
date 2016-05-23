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

var modbus = require('modbus-event')({ debug : true });


router.get('/', function(req, res){
    var ntotal = JSON.parse(JSON.stringify(total));
    ntotal.users = users;
    return res.json(ntotal);
});

var batteryMax = 1;
router.post('/', function(req, res){
    if(stack[req.cookies.index] == req.cookies.key || stack[req.cookies.index] == false){
        if(stack[req.cookies.index] == false) stack[req.cookies.index] = req.cookies.key;

        // Atualiza o gasto do usuário
        users[req.cookies.index].powerh += req.body.powerh;
        users[req.cookies.index].renewh += req.body.renewh;

        // Deriva o tempo para contar o gasto e geração por (2) segundo
        utils.timeout(2000, function(){
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
        if(total.batt + req.body.renewh > batteryMax){
            total.batt = batteryMax;
            total.powerh = total.powerh - req.body.renewh;
        }else
            total.batt += req.body.renewh;

        // Salva o consumo liquido
        modbus.callee(function(client, data, next){
            client.writeRegister(ADDR.CONSUMO_LIQUIDO,
                utils.normalize(total.powerh, 100)).then(next);
        });

        // Salva o gerado bruto
        modbus.callee(function(client, data, next){
            client.writeRegister(ADDR.RENOVAVEL_BRUTO,
                utils.normalize(total.renewh, 100)).then(next);
        });

        // Retorna os totais
        return res.json(total);
    }
    return res.status(402).end('');
});

module.exports = router;