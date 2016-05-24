/**
 * Created by Raphael on 23/05/2016.
 */
// Transforma decimal em inteiro com casas para direita
exports.normalize = function(val, power){
    if(!val) return 0;
    val = val.toFixed((new String(power)).length - 1)*power;
    return (val >= 32767) ? 32767 : val;
};

// Realiza operações respeitando timeouts
var time = [];
exports.timeout = function(diff, interval, action, id){
    if(!id) id = 0;
    if(!time[id]) time[id]=0;
    var now = new Date();
    if(now - time[id] > diff) {
        time[id] = now;
        action(now);
    }else{
        interval(now);
    }
};