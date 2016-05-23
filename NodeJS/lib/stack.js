/**
 * Created by Raphael on 18/05/2016.
 */
var arr = [];
module.exports = function(users){
    if(arr.length > 0) return arr;
    for(var i in users)
        arr.push(false);
    return arr;
};