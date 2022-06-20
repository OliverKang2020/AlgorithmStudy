package itan.com.bluetoothle;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.ParcelUuid;

import java.util.UUID;

/**
 * Created by itanbarpeled on 28/01/2018.
 */

public class Constants {


    public static final int SERVER_MSG_FIRST_STATE = 1;
    public static final int SERVER_MSG_SECOND_STATE = 2;

    /*
    TODO bluetooth
    better to use different Bluetooth Service,
    instead of Heart Rate Service:
    https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.heart_rate.xml.

    maybe Object Transfer Service is more suitable:
    https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.object_transfer.xml
     */
    public static final UUID VOICE_CONTROL_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-001020fb34fb");
    public static final UUID VOICE_TEXT_CHARACTERISTIC_UUID = UUID.fromString("00002A38-0000-1000-8000-001020fb34fb");
    public static final UUID NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("00005050-0000-1000-8000-001020fb34fb");
    public static final UUID NOTIFY_CHARACTERISTIC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    private static UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }
}