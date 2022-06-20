package com.example.voicereceiverbleperipheral;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button btn_startAdvertising;
    private Button btn_stopAdvertising;
    private Button btn_requestConfirmation;

    private BluetoothGattService sampleService;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic notifyCharacteristic;

    private HashSet<BluetoothDevice> bluetoothDevices;
    private BluetoothDevice connectedDevice;
    private BluetoothGattServer gattServer;
    private BluetoothManager bluetoothManager;

    public static final UUID VOICE_CONTROL_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-001020fb34fb");
    public static final UUID VOICE_TEXT_CHARACTERISTIC_UUID = UUID.fromString("00002A38-0000-1000-8000-001020fb34fb");
    public static final UUID NOTIFY_CHARACTERISTIC_UUID = UUID.fromString("00005050-0000-1000-8000-001020fb34fb");
    public static final UUID NOTIFY_CHARACTERISTIC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_startAdvertising = (Button) findViewById(R.id.button);
        btn_startAdvertising.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAdvertising();
            }
        });

        btn_stopAdvertising = (Button) findViewById(R.id.button2);
        btn_stopAdvertising.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAdvertising();
            }
        });

        btn_requestConfirmation = (Button) findViewById(R.id.button3);
        btn_requestConfirmation.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification("Confirmation Requested");
            }
        });

        setGattServer();
        setBluetoothService();

        startAdvertising();
    }

    private void startAdvertising() {
        Log.i("onConnectionStateChange", "startAdvertising called");
        Toast.makeText(this.getApplicationContext(),"advertising started", Toast.LENGTH_SHORT).show();
        startService(getServiceIntent(this));
    }

    private void stopAdvertising() {
        Log.i("onConnectionStateChange", "stopAdvertising called");
        Toast.makeText(this.getApplicationContext(),"advertising stopped", Toast.LENGTH_SHORT).show();
        stopService(getServiceIntent(this));
    }

    /**
     * Returns Intent addressed to the {@code PeripheralAdvertiseService} class.
     */
    private Intent getServiceIntent(Context context) {
        return new Intent(context, PeripheralAdvertiseService.class);
    }

    private void setGattServer() {
        bluetoothDevices = new HashSet<>();
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager != null) {
            gattServer = bluetoothManager.openGattServer(this, gattServerCallback);
        } else {
            Toast.makeText(this.getApplicationContext(),"bluetooth manager not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setBluetoothService() {

        // create the Service
        sampleService = new BluetoothGattService(VOICE_CONTROL_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        /*
        create the Characteristic.
        we need to grant to the Client permission to read (for when the user clicks the "Request Characteristic" button).
        no need for notify permission as this is an action the Server initiate.
         */
        writeCharacteristic = new BluetoothGattCharacteristic(VOICE_TEXT_CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_READ | BluetoothGattCharacteristic.PERMISSION_WRITE);
        notifyCharacteristic = new BluetoothGattCharacteristic(NOTIFY_CHARACTERISTIC_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
//        notifyDescriptorCharacteristic = new BluetoothGattCharacteristic(NOTIFY_CHARACTERISTIC_DESCRIPTOR_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_WRITE, BluetoothGattCharacteristic.PERMISSION_WRITE);
        notifyCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

//        notifyCharacteristic.addDescriptor(new BluetoothGattDescriptor(NOTIFY_CHARACTERISTIC_DESCRIPTOR_UUID, BluetoothGattCharacteristic.PROPERTY_NOTIFY | BluetoothGattCharacteristic.PROPERTY_WRITE));

//        setCharacteristics(""); // set initial state

        // add the Characteristic to the Service
        sampleService.addCharacteristic(writeCharacteristic);
        sampleService.addCharacteristic(notifyCharacteristic);

        // add the Service to the Server/Peripheral
        if (gattServer != null) {
            gattServer.addService(sampleService);
        }
        else
        {
            Toast.makeText(this.getApplicationContext(),"gatt server not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void setCharacteristics(String value) {
        writeCharacteristic.setValue(getValue(value));
        notifyCharacteristic.setValue(getValue(value));

    }

    private void sendNotification(String value) {
//        writeCharacteristic.setValue(getValue(value));
//        gattServer.notifyCharacteristicChanged(connectedDevice, notifyCharacteristic, true);
        notifyCharacteristic.setValue(getValue(value));
        gattServer.notifyCharacteristicChanged(connectedDevice, notifyCharacteristic, true);
//        notifyCharacteristic.
//        notifyCharacteristic.notify();
//        gattServerCallback.onCharacteristicWriteRequest(connectedDevice, 0, notifyCharacteristic, true, true, 0, getValue(value));
//        notifyCharacteristic.setValue(getValue(value));
//        gattServer.sendResponse(connectedDevice, 10, BluetoothGatt.GATT_SUCCESS, 0, value);
    }

    private byte[] getValue(int value) {
        return new byte[]{(byte) value};
    }
    private byte[] getValue(String str) {
        return str.getBytes();
    }

    private final BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, final int status, int newState) {

            super.onConnectionStateChange(device, status, newState);

            String msg;

            if (status == BluetoothGatt.GATT_SUCCESS) {

                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    connectedDevice = device;
                    bluetoothDevices.add(device);

                    msg = "Connected to device: " + device.getAddress();
                    Log.i("onConnectionStateChange", msg);



                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {

                    bluetoothDevices.remove(device);

                    msg = "Disconnected from device";
                    Log.i("onConnectionStateChange", msg);
                }

            } else {
                bluetoothDevices.remove(device);

                msg = "Error during connection" + ": " + status;
                Log.i("onConnectionStateChange", msg);
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
            Log.i("onNotificationSent", "Notification sent. Status: " + status);
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {

            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

            if (gattServer == null) {
                return;
            }

            Log.i("onCharReadRequest", "Device tried to read characteristic: " + characteristic.getUuid());
            Log.i("onCharReadRequest", "Value: " + Arrays.toString(characteristic.getValue()));

            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }


        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {

            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);

            Log.i("onCharWriteRequest", "Characteristic Write request: " + Arrays.toString(value));

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run()
                {
                    Toast.makeText(MainActivity.this, "data received: " + new String(value), Toast.LENGTH_SHORT).show();
                }
            }, 0);

//            Toast.makeText(getApplicationContext(),"data received: " + Arrays.toString(value), Toast.LENGTH_SHORT).show();
            if(characteristic.getUuid().equals(VOICE_TEXT_CHARACTERISTIC_UUID) || characteristic.getUuid().equals(NOTIFY_CHARACTERISTIC_UUID)) {
//                Toast.makeText(getApplicationContext(),"data received: " + Arrays.toString(value), Toast.LENGTH_SHORT).show();
                characteristic.setValue(value);
            }
//            writeCharacteristic.setValue(value);

            if (responseNeeded) {
                gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value);
            }

        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {

            super.onDescriptorReadRequest(device, requestId, offset, descriptor);

            if (gattServer == null) {
                return;
            }

            Log.i("onDescriptorReadRequest", "Device tried to read descriptor: " + descriptor.getUuid());
            Log.i("onDescriptorReadRequest", "Value: " + Arrays.toString(descriptor.getValue()));

            gattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, descriptor.getValue());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded,
                                             int offset,
                                             byte[] value) {

            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);

            Log.i("onDescWriteRequest", "Descriptor Write Request " + descriptor.getUuid() + " " + Arrays.toString(value));
        }
    };
}