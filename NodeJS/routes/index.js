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

module.exports = router;
