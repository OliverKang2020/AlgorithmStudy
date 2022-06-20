var util = require('util');
var bleno = require('@abandonware/bleno');
var BlenoCharacteristic = bleno.Characteristic;

var EchoCharacteristic = function(event) {
  EchoCharacteristic.super_.call(this, {
    uuid: '5372723EED3154B7E74B82A1A79D57CD',
    properties: ['notify'],
    // secure: ['notify'],
    // properties: ['read', 'write', 'notify'],
    // secure: ['read', 'write', 'notify'],
    value: null
  });

  this._echoEvent = event;
  this._value = Buffer.alloc(0);
  this._updateValueCallback = null;
};

util.inherits(EchoCharacteristic, BlenoCharacteristic);

EchoCharacteristic.prototype.onReadRequest = function(offset, callback) {
  callback(this.RESULT_SUCCESS, this._value);
};

EchoCharacteristic.prototype.onWriteRequest = function(data, offset, withoutResponse, callback) {

  let _buffer = new Buffer.from([0xAB, 0xDA, ...data]);
  this._value = _buffer;
  console.log('On Write Request!');

  if (this._updateValueCallback) {
    console.log('Notifying!');
    // notifying
    this._updateValueCallback(this._value);
  }

  callback(this.RESULT_SUCCESS);
};

EchoCharacteristic.prototype.onSubscribe = function(maxValueSize, updateValueCallback) {
  console.log('On Subscribed!');
  this._updateValueCallback = updateValueCallback;

  this._notifiedOnInit = true;

  let data = Buffer.from([0xAB, 0xC0]);

  this._value = data;
  this._updateValueCallback(this._value);
};

EchoCharacteristic.prototype.onUnsubscribe = function() {
  this._updateValueCallback = null;
};

module.exports = EchoCharacteristic;
