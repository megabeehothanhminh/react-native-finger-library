package com.fingerapp.apdapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;




public class BLEFingerDevice implements FingerDevice {
    private BluetoothDevice mBluetoothDevice;
    private BLEFingerDeviceId mFingerDeviceId;

    public BLEFingerDevice(BluetoothDevice bluetoothDevice) {
        this.mBluetoothDevice = bluetoothDevice;
        this.mFingerDeviceId = BLEFingerDeviceId.valueOf(bluetoothDevice.getAddress());
    }

    @Override
    public FingerDeviceId getFingerDeviceId() {
        return this.mFingerDeviceId;
    }

    @SuppressLint("MissingPermission")
    @Override
    public WritableMap toRNWritableMap() {
        WritableMap deviceMap = Arguments.createMap();
        deviceMap.putString("inner_mac_address", this.mFingerDeviceId.getInnerMacAddress());
        deviceMap.putString("device_name", this.mBluetoothDevice.getName());
        return deviceMap;
    }
}
