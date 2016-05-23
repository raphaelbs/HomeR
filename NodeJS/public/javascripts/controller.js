/**
 * Created by rapha_000 on 17/05/2016.
 */
var app = angular.module('app', []);
var modularSpeed;
var httpUploadTime = 500;

app.controller('app', function($scope, $http){
    $scope.powers = 15;
    $scope.renews = 0;
    $scope.speed = 0;
    var ts = new Date();
    $scope.color = 'rgba(250, 255, 250, 1)';
    $scope.resource = '/images/battery-icon.png';

    var lastpower, lastrenew;
    modularSpeed = function(speed){
        var cut = 255 - speed;
        $scope.$apply(function() {
            $scope.speed = speed * 10;
            $scope.color = 'rgba(' + cut + ', 255, ' + cut + ', 1)';
        });
    };

    setInterval(function(){
        var now = new Date();
        var uptime = now - ts;
        $scope.$apply(function(){
            var val = ($scope.powers * Math.random() * 2).toFixed(2);
            $scope.powers = (val < 4 ? (5 * (Math.random()+1)).toFixed(2) : val);
            $scope.powerh += $scope.powers/3600;
            $scope.time = formatTime(now);
            $scope.uptime = Math.round(uptime / 1000) + 's';
            $scope.renews = $scope.speed/10 || 0;
            $scope.renewh += $scope.renews/3600;

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
        lock = false; console.log(data.data);
        window.onbeforeunload = '';
        window.location = '/desiste.html';
    };
    var succ = function(res){ $scope.total = res.data; lock = false;};
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
    modularSpeed && modularSpeed(Math.round(mean) > 100 ? 100 : Math.round(mean));
    mean = 0;
});