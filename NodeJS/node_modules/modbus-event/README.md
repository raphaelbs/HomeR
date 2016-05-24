# modbus-event 

Modbus-event is a TCP/IP Master, event-driven, implementation for modbus protocol.

## Installation

```javascript
var modbusEvent = require('modbus-event');
```

---

## Usage

```javascript
var modbusEvent = require('modbus-event');
// All available options
var options = {
	debug : true, // default: false
    ip : '192.168.1.1', // default: '127.0.0.1'
    port : 777, // default: 502
    id : 2 // default: 1
};
var me = modbusEvent(options);

// Executes some function in between the reading stage
me.callee(function(client, datas, next){
    client.writeCoil(1, 1).then(next);
});

// Assign a listener event
me.on('update', function(type, address, from, to){
    console.log(type, address, from, to);
});
```

---

## Reference

### Constructor argument: Options

key | description | type | default
--- | --- | --- | ---
debug | Enable verbosity for debuggin (very handy) | boolean | false
ip | The listenning IP of your Slave Modbus | string | '127.0.0.1'
port | The listenning port of yout Slave Modbus | number | 502
id | The SlaveID of your Slave Modbus | number | 1
address | Reading address range | _Object_ { init : _initial address_, length : _address range_ } | { init : 0, length : 10 }
***

### require('modbus-event') 
#### _return type function(options)_
Main function of [modbus-event](https://www.npmjs.com/package/modbus-event)

***

### require('modbus-event')(options) 
#### _return type Object { callee : fn, on : fn }_

The constructor of [modbus-event](https://www.npmjs.com/package/modbus-event).
Return the following objects:

key | value
--- | ---
callee | function(client, data, next)
on | function(event, callback)

___

#### callee 
##### _return type function(callback)_

Executes arbitrary code when the serial channel is available.
**callback** is a function with the following arguments:
function(client, data, next)

argument | description
--- | ---
client | an instance of [modbus-serial](https://www.npmjs.com/package/modbus-serial)
data | array contain addresses and values
next | a function that you NEED TO CALL when done

___

#### on 
##### _return type Function_
_function(event, callback)_

Assign an event and the respective callback.
Where **event** is one of the followings:
* update

---

## Dependencies

modbus-serial

---

## License

MIT
