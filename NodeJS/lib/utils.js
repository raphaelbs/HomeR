/**
 * Created by Raphael on 23/05/2016.
 */
// Transforma decimal em inteiro com casas para direita
exports.normalize = function(val, power){
    val = val.toFixed(2)*power;
    return (val >= 32767) ? 32767 : val;
};

// Realiza operações respeitando timeouts
var time = [];
exports.timeout = function(diff, interval, action, id){
    if(!id) id = 0;
    var now = new Date();
    if(now - time[id] > diff) {
        time[id] = now;
        action(now);
    }else{
        interval(now);
    }
};