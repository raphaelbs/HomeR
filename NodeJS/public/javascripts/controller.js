/**
 * Created by rapha_000 on 17/05/2016.
 */
var app = angular.module('app', []);
var modularSpeed;

app.controller('app', function($scope, $http){
    $scope.powers = 15;
    $scope.renews = 0;
    $scope.speed = 0;
    var ts = new Date();
    $scope.color = 'rgba(250, 255, 250, 1)';
    $scope.resource = '/images/rede.png';

    var chooseResourceFrom = function(){
        if($scope.total.user.from)
            $scope.resource = '/images/battery.png';
        else
            $scope.resource = '/images/rede.png';

        if($scope.total.user.sw)
            $scope.sw = '/images/battery.png';
        else
            $scope.sw = '/images/rede.png';
    };

    var lastpower, lastrenew;
    modularSpeed = function(speed){
        var cut = 255 - (speed/2.5);
        $scope.$apply(function() {
            $scope.speed = speed;
            $scope.color = 'rgba(' + cut + ', 255, ' + cut + ', 1)';
        });
    };

    setInterval(function(){
        var now = new Date();
        var uptime = now - ts;
        $scope.$apply(function(){
            var val = ($scope.powers * Math.random() * 2).toFixed(2);
            $scope.powers = Math.max(Math.min(val, 100), (5 * (Math.random()+1))).toFixed(2);
            $scope.powerh += $scope.powers/1000;
            $scope.time = formatTime(now);
            $scope.uptime = Math.round(uptime / 1000) + 's';
            $scope.renews = $scope.speed/10 || 0;
            $scope.renewh += $scope.renews/1000;

            upload({
                powerh : $scope.powerh - lastpower,
                renewh : $scope.renewh - lastrenew
            });
            lastpower = $scope.powerh;
            lastrenew = $scope.renewh;
        });
    }, 1000);



    var lock = false;
    var error = function(data){
        $scope.total.error = true;
        lock = false;
        if(data.data){
            window.onbeforeunload = '';
            window.location = '/'+data.data;
        }

    };
    var succ = function(res){
        $scope.total = res.data;
        lock = false;
        chooseResourceFrom();
    };
    var upload = function(data){
        if(lock) return;
        if($scope.total && $scope.total.error){
            data.powerh = $scope.powerh;
            data.renewh = $scope.renewh;
        }
        lock = true;
        $http.post('/values', data).then(succ, error);
    };
});

function formatTime(time){
    return time.getHours() + ':' + time.getMinutes() + ':' + time.getSeconds();
}

var timestamp = null, t2 = null, to = null;
var lastMouseX = null;
var lastMouseY = null;
var mean = 0, rate = 100;
document.body.addEventListener("mousemove", function(e) {
    if (timestamp === null) {
        timestamp = Date.now();
        t2 = timestamp;
        lastMouseX = e.screenX;
        lastMouseY = e.screenY;
        return;
    }

    var now = Date.now();
    var dt =  now - timestamp;
    var dx = e.screenX - lastMouseX;
    var dy = e.screenY - lastMouseY;
    mean = (mean + Math.hypot(Math.round(dx / dt * 100), Math.round(dy / dt * 100)))/2;

    timestamp = now;
    lastMouseX = e.screenX;
    lastMouseY = e.screenY;
    clearTimeout(to);
    to = setTimeout(function(){
        modularSpeed && modularSpeed(0);
    }, rate + 50);
    if(now < t2 + rate) return ;
    t2 = now;
    modularSpeed && modularSpeed(Math.min(Math.round(mean), 250));
    mean = 0;
});