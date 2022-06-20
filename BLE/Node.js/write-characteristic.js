
var util = require('util');
var bleno = require('@abandonware/bleno');
var BlenoCharacteristic = bleno.Characteristic;

var EchoWriteCharacteristic = function(event, notiCharacteristic) {
   EchoWriteCharacteristic.super_.call(this, {
     uuid: '5372723EED3154B7E74B82A1A79D57EF',
     properties: ['write'],
    //  secure: ['write'],
   //   properties: ['read', 'write'],
   //   secure: ['read', 'write'],
     value: null
   });
 
   this._echoEvent = event;
   this._value = Buffer.alloc(0);
   this._notiCharacteristic = notiCharacteristic;
 };

util.inherits(EchoWriteCharacteristic, BlenoCharacteristic);

EchoWriteCharacteristic.prototype.onReadRequest = function(offset, callback) {
   callback(this.RESULT_SUCCESS, this._value);
 };

EchoWriteCharacteristic.prototype.onWriteRequest = function(data, offset, withoutResponse, callback) {
  console.log("Write_Received!");
   let isHandShake = (data[0] == 0xAB) && (data[1] == 0xC0);
   let isData = (data[0] == 0xAB) && (data[1] == 0xDA);

   if(isHandShake)
   {
      let startSendingData = setInterval(function() {
         this._notiCharacteristic._updateValueCallback(Buffer.from([0xAB, 0xDA, 0x12, 0x34]));
      }, 500)
   }
   this._value = data;

  // test is done with
  // 0x3B 0xE4 0x07 0xC0 0xf1 0x01 0x12 0x34 0x01 0x56
  this._echoEvent.emit('messageReceived', [...this._value]);

   callback(this.RESULT_SUCCESS);
 };
 
 module.exports = EchoWriteCharacteristic;
