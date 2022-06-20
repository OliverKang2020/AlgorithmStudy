package com.example.voicetransmitterblecentral;

import static itan.com.bluetoothle.Constants.VOICE_CONTROL_SERVICE_UUID;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ActivityResultLauncher<Intent> resultLauncher;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bleGatt;
    private boolean _connected = false;
    private static final int ACCESS_FINE_LOCATION_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // do something
                    }
                }
        );

        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "bluetooth not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "bluetooth disabled", Toast.LENGTH_LONG).show();
                Log.d("myTag", "This is my message");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                resultLauncher.launch(enableBtIntent);
                finish();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "bluetooth enabled", Toast.LENGTH_LONG).show();
            }
        }

        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bleScanner == null) {
            Toast.makeText(getApplicationContext(), "ble scanner not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Button buttonScan = (Button) findViewById(R.id.button);
        buttonScan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION_PERMISSION_CODE);
    }

    private void startScan() {
        Toast.makeText(getApplicationContext(), "scan started", Toast.LENGTH_LONG).show();
        bleScanner.startScan(buildScanFilters(), buildScanSettings(), mScanCallback);
//        bleScanner.startScan(mScanCallback);
    }

    /**
     * Return a List of {@link ScanFilter} objects to filter by Service UUID.
     */
    private List<ScanFilter> buildScanFilters() {

        List<ScanFilter> scanFilters = new ArrayList<>();

        ScanFilter.Builder builder = new ScanFilter.Builder();
        // Comment out the below line to see all BLE devices around you
        builder.setServiceUuid(ParcelUuid.fromString(VOICE_CONTROL_SERVICE_UUID.toString()));
        scanFilters.add(builder.build());

        return scanFilters;
    }

    /**
     * Return a {@link ScanSettings} object set to use low power (to preserve battery life).
     */
    private ScanSettings buildScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        return builder.build();
    }

    private boolean deviceNameIsNote(String devName)
    {
        String noteName = new String("Galaxy Note10 5G");
        if (devName == null) {
            return false;
        }

        if(devName.equals(noteName)) {
            Log.i("uuidMatches id", "detected: Galaxy Note10 5G");
            return true;
        }
        return false;
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String devAddress = device.getAddress();
            String devName = device.getName();
            Log.i("onScanResult",  "|111111111111111|" + devName + "|" + devAddress);

            // if("78:9E:F5:61:B1:13".equals(devAddress))
            //54:6D:45:86:51:80
            // 71:82:AE:35:5F:A9
            // 75:5C:43:18:62:54
            // 47:A5:4D:72:25:D3
            // There is a issue that ble mac address changes. so instead, I decided to use UUID.
//            if(deviceNameIsNote(devName))
//            {
                Log.i( "onScanResult", "connecting device: " + devAddress );
//                if(!_connected) {
                connectDevice(device);
//                }
//            }
        }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results)
            {
                Log.i("onBatchScanResults", "|33333333333333|" + result.getDevice().getName() + "|" + result.getDevice().getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.i("onScanFailed", "|2222222222222|" + errorCode);
        }
    };

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == ACCESS_FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Access Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Access Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
    }

    private void connectDevice( BluetoothDevice _device ) {
        Log.i( "connectDevice", "connecting device..." );

        bleScanner.stopScan(mScanCallback);

        Intent intent = new Intent(this, DeviceConnectActivity.class);
        intent.putExtra(DeviceConnectActivity.EXTRAS_DEVICE_NAME, _device.getName());
        intent.putExtra(DeviceConnectActivity.EXTRAS_DEVICE_ADDRESS, _device.getAddress());
        startActivity(intent);

        _connected = true;
    }

    public void disconnectGattServer() {
        Log.i( "disconnectGattServer", "Closing Gatt connection" );
        // reset the connection flag
        _connected= false;
        // disconnect and close the gatt
        if( bleGatt != null ) {
            bleGatt.disconnect();
            bleGatt.close();
        }
    }
}