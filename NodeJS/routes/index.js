var express = require('express');
var router = express.Router();

var users = require('../lib/users');
var stack = require('../lib/stack')(users);
var error = require('../lib/error');

var getFirstVoid = function(arr){
    for(var key in arr){
        if(!arr[key]) return key;
    }
    return null;
};

/* GET home page. */
router.get('/', function(req, res) {
    var selectedNow = getFirstVoid(stack);
    if(selectedNow >= users.length || !selectedNow) return res.render('error', error);
    stack[selectedNow] = Math.random() * 99885577;
    res.cookie('key', stack[selectedNow]);
    res.cookie('index', selectedNow);
    res.render('index', users[selectedNow]);
});

router.delete('/', function(req, res){
    if(!req.cookies.index || !req.cookies.key) return res.status(404).end('Num deu :/');
    if(stack[req.cookies.index] == req.cookies.key){
        stack[req.cookies.index] = false;
        console.error(req.cookies.index + ' removed');
        res.clearCookie('key');
        res.clearCookie('index');
        return res.end('');
    }
    return res.status(401).end('Senha errada ;)');
});

router.post('/set', function(req, res){
    if(!req.body.key || req.body.key !== '57430b208a82e75c071daae1')
        return res.status(401).end('Não autorizado!');
    if(!req.body.userId || !req.body.value)
        return res.status(400).end('Faltando parâmetros');
    users[req.body.userId].sw = req.body.value;
    return res.end(users[req.body.userId].sw);
});

router.get('/set/:key/:userId/:value', function(req, res){
    if(!req.params.key || req.params.key !== '57430b208a82e75c071daae1')
        return res.status(401).end('Não autorizado!');
    if(!req.params.userId || !req.params.value)
        return res.status(400).end('Faltando parâmetros');
    users[req.params.userId].sw = req.params.value;
    return res.end(users[req.params.userId].sw);
});

module.exports = router;
