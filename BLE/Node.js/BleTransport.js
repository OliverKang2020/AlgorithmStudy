const bleno = require('@abandonware/bleno');
var BlenoPrimaryService = bleno.PrimaryService;
var MyCharacteristicNotify = require('./notify-characteristic.js');
var MyCharacteristicWrite = require('./write-characteristic.js');
const EventEmitter = require('events');

module.exports = () => {
   let eventEmitter = new EventEmitter();

   const on = (...args) => eventEmitter.on(...args);
   var myCharacter1 = new MyCharacteristicNotify(eventEmitter);
   var myCharacter2 = new MyCharacteristicWrite(eventEmitter, myCharacter1);

   let bleAdvertisingIsReady = false;

   bleno.on('stateChange', (state) => {
      if (state === 'poweredOn') {
         bleAdvertisingIsReady = true;
      } else {
         bleAdvertisingIsReady = false;
         bleno.stopAdvertising();
      }
   });

   bleno.on('advertisingStart', (error) => {
      if (!error) {
         bleno.setServices([
            new BlenoPrimaryService({
               uuid: '5372723EED3154B7E74B82A1A79D57AB',
               characteristics: [
                  myCharacter1,
                  myCharacter2
               ]
            })
         ]);
      }
   });

   bleno.on('servicesSet', (error) => { // eslint-disable-line no-unused-vars
   });

   function myUpdateValueCallback(something) { // eslint-disable-line no-unused-vars
   };

   const startAdvertising = (serviceName, serviceUuids) => {
      if(bleAdvertisingIsReady)
      {
         bleno.startAdvertising(serviceName, serviceUuids); // todo: error callback
      }
      else
      {
         throw new Error('advertising is not available');
      }
   };

   const stopAdvertising = () => {
      bleno.stopAdvertising();
   };

   const sendNotification = (message) => { 
      let bufferedMsg = Buffer.from(message);
      myCharacter1.onWriteRequest(bufferedMsg, 0, 0, myUpdateValueCallback);
   };

   const getBlenoState = () => {
      return bleno.state;
   };

   return {
      on,
      startAdvertising,
      stopAdvertising,
      sendNotification,
      getBlenoState
   };
};
