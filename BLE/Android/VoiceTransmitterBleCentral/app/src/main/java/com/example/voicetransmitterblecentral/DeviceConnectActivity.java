package com.example.voicetransmitterblecentral;

import static itan.com.bluetoothle.Constants.VOICE_TEXT_CHARACTERISTIC_UUID;
import static itan.com.bluetoothle.Constants.VOICE_CONTROL_SERVICE_UUID;
import static itan.com.bluetoothle.Constants.NOTIFY_CHARACTERISTIC_UUID;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceConnectActivity extends AppCompatActivity implements View.OnClickListener  {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    private CentralService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mDeviceServices;
    private BluetoothGattCharacteristic mCharacteristic;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private String mDeviceName;
    private String mDeviceAddress;

    private TextView mConnectionStatus;
    private TextView mConnectedDeviceName;
    private Button mRequestReadCharacteristic;
    private EditText mWriteCharacteristicValueInput;
    private Button mRequestWriteCharacteristic;
    private TextView mDataRead;
    private TextView mConfirmationRequestFromOven;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_connect);

        mDeviceServices = new ArrayList<>();
        mCharacteristic = null;
        mNotifyCharacteristic = null;

        Intent intent = getIntent();
        if (intent != null) {
            mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
            mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        }


        mConnectionStatus = (TextView) findViewById(R.id.connection_status);
        mConnectedDeviceName = (TextView) findViewById(R.id.connected_device_name);
        mRequestReadCharacteristic = (Button) findViewById(R.id.request_read_characteristic);
        mWriteCharacteristicValueInput = (EditText) findViewById(R.id.editTextCharacteristicValueInput);
        mRequestWriteCharacteristic = (Button) findViewById(R.id.request_write_characteristic);
        mDataRead = (TextView) findViewById(R.id.characteristic_value);
        mConfirmationRequestFromOven = (TextView) findViewById(R.id.characteristic_value2);
        mRequestReadCharacteristic.setOnClickListener(this);
        mRequestWriteCharacteristic.setOnClickListener(this);


        if (TextUtils.isEmpty(mDeviceName)) {
            mConnectedDeviceName.setText("");
        } else {
            mConnectedDeviceName.setText(mDeviceName);
        }


        Intent gattServiceIntent = new Intent(this, CentralService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        /*
        updateConnectionState(R.string.connected);
        mRequestReadCharacteristic.setEnabled(true);
        updateInputFromServer(SERVER_MSG_SECOND_STATE);
        */
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_device_connect;
//    }
//
//    @Override
//    protected int getTitleString() {
//        return R.string.central_connection_screen;
//    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.request_read_characteristic:
                requestReadCharacteristic();
                break;

            case R.id.request_write_characteristic:
                requestWriteCharacteristic();
                break;
        }
    }


    /*
    request from the Server the value of the Characteristic.
    this request is asynchronous.
     */
    private void requestReadCharacteristic() {
        if (mBluetoothLeService != null && mCharacteristic != null) {
            mBluetoothLeService.readCharacteristic(mCharacteristic);
        } else {
            Toast.makeText(getApplicationContext(), "ble characteristic not available", Toast.LENGTH_LONG).show();
        }
    }

    private void requestWriteCharacteristic() {
        if (mBluetoothLeService != null && mCharacteristic != null) {
            mCharacteristic.setValue(mWriteCharacteristicValueInput.getText().toString());
            mCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            mBluetoothLeService.writeCharacteristic(mCharacteristic);
        } else {
            Toast.makeText(getApplicationContext(), "ble characteristic not available", Toast.LENGTH_LONG).show();
        }
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBluetoothLeService = ((CentralService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize()) {
                Log.i("onServiceConnected", "Unable to initialize Bluetooth");
                finish();
            }

            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    /*
     Handles various events fired by the Service.
     ACTION_GATT_CONNECTED: connected to a GATT server.
     ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
     ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
     ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read or notification operations.
    */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action == null) {
                return;
            }

            switch (intent.getAction()) {

                case CentralService.ACTION_GATT_CONNECTED:
                    updateConnectionState(R.string.connected);
                    mRequestReadCharacteristic.setEnabled(true);
                    mRequestWriteCharacteristic.setEnabled(true);
                    break;

                case CentralService.ACTION_GATT_DISCONNECTED:
                    updateConnectionState(R.string.disconnected);
//                    mRequestReadCharacteristic.setEnabled(false);
//                    mRequestWriteCharacteristic.setEnabled(false);
//                    finish();
//                    Intent intent = new Intent(this, MainActivity.class);
//                    startActivity(intent);
                    break;


                case CentralService.ACTION_GATT_SERVICES_DISCOVERED:
                    // set all the supported services and characteristics on the user interface.
                    Log.i("CentralService", "ACTION_GATT_SERVICES_DISCOVERED ");
                    setGattServices(mBluetoothLeService.getSupportedGattServices());
                    registerCharacteristic();
                    break;

                case CentralService.ACTION_DATA_AVAILABLE:
                    String msg = intent.getStringExtra(CentralService.EXTRA_DATA);
//                    int msg = intent.getIntExtra(CentralService.EXTRA_DATA, -1);
                    Log.i("CentralService", "ACTION_DATA_AVAILABLE " + msg);
                    updateInputFromServer(msg);
                    break;

                case CentralService.NOTIFICATION_DATA_RECEIVED:
                    String notifiedMsg = intent.getStringExtra(CentralService.EXTRA_DATA);
                    Log.i("CentralService", "NOTIFICATION_DATA_RECEIVED " + notifiedMsg);
                    notificationReceivedFromServer(notifiedMsg);
                    break;
            }
        }
    };


    /*
     This sample demonstrates 'Read' and 'Notify' features.
     See http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
     list of supported characteristic features.
    */
    private void registerCharacteristic() {

        BluetoothGattCharacteristic characteristic = null;

        if (mDeviceServices != null) {

            /* iterate all the Services the connected device offer.
            a Service is a collection of Characteristic.
             */
            for (ArrayList<BluetoothGattCharacteristic> service : mDeviceServices) {

                // iterate all the Characteristic of the Service
                for (BluetoothGattCharacteristic serviceCharacteristic : service) {

                    /* check this characteristic belongs to the Service defined in
                    PeripheralAdvertiseService.buildAdvertiseData() method
                     */
                    if (serviceCharacteristic.getService().getUuid().equals(VOICE_CONTROL_SERVICE_UUID)) {

                        if (serviceCharacteristic.getUuid().equals(VOICE_TEXT_CHARACTERISTIC_UUID)) {
                            characteristic = serviceCharacteristic;
                            mCharacteristic = characteristic;
                        }
                        else if (serviceCharacteristic.getUuid().equals(NOTIFY_CHARACTERISTIC_UUID)) {
                            Log.i("registerCharacteristic", "NOTIFY_CHARACTERISTIC discovered");
                            characteristic = serviceCharacteristic;
                            mNotifyCharacteristic = characteristic;
                        }
                    }
                }
            }

           /*
            int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            */

//            if (mCharacteristic != null) {
//                mBluetoothLeService.readCharacteristic(mCharacteristic);
//            }
            if (characteristic != null){
                Log.i("registerCharacteristic", "setCharacteristicNotification");
                mBluetoothLeService.setCharacteristicNotification(characteristic, true);
            }
        }
    }


    /*
    Demonstrates how to iterate through the supported GATT Services/Characteristics.
    */
    private void setGattServices(List<BluetoothGattService> gattServices) {

        if (gattServices == null) {
            return;
        }

        mDeviceServices = new ArrayList<>();

        // Loops through available GATT Services from the connected device
        for (BluetoothGattService gattService : gattServices) {
            ArrayList<BluetoothGattCharacteristic> characteristic = new ArrayList<>();
            characteristic.addAll(gattService.getCharacteristics()); // each GATT Service can have multiple characteristic
            mDeviceServices.add(characteristic);
        }

    }


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionStatus.setText(resourceId);
            }
        });
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CentralService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(CentralService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(CentralService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(CentralService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(CentralService.NOTIFICATION_DATA_RECEIVED);
        return intentFilter;
    }


    private void updateInputFromServer(String msg) {
        mDataRead.setText(msg);
        Toast.makeText(getApplicationContext(), "data read: " + msg, Toast.LENGTH_LONG).show();
    }

    private void notificationReceivedFromServer(String msg) {
        mConfirmationRequestFromOven.setText(msg);
        Toast.makeText(getApplicationContext(), "confirmation request: " + msg, Toast.LENGTH_LONG).show();
    }
}
