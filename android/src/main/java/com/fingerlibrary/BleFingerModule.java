package com.fingerlibrary;

import android.widget.Toast;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.fingerapp.apdapter.BleFingerAdapter;
import com.fingerapp.apdapter.FingerAdapter;
import com.fingerapp.apdapter.FingerDevice;
import java.util.List;




public class BleFingerModule extends ReactContextBaseJavaModule implements InterfaceFinger {
    protected ReactApplicationContext reactContext;
    //Bluetooth
    private String TAG = "BleFingerModule";
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    protected FingerAdapter adapter;
    BleFingerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "BleFingerModule";
    }

    @ReactMethod
    @Override
    public void init(Callback successCallback, Callback errorCallback) {
        this.adapter = BleFingerAdapter.getInstance();
        this.adapter.init(reactContext, successCallback, errorCallback);
    }

    @ReactMethod
    @Override
    public void getDeviceList(Callback successCallback, Callback errorCallback) {
        List<FingerDevice> printerDevices = this.adapter.getDeviceList(errorCallback);
        WritableArray pairedDeviceList = Arguments.createArray();
        if (printerDevices.size() > 0) {
            for (FingerDevice printerDevice : printerDevices) {
                pairedDeviceList.pushMap(printerDevice.toRNWritableMap());
            }
            successCallback.invoke(pairedDeviceList);
        } else {
            errorCallback.invoke("No Device Found");
        }
    }



    @ReactMethod
    public void connectPrinter(String innerAddress) {
        this.adapter.selectDevice(innerAddress);
    }

    @ReactMethod
    public void enrolTemplate() {
        this.adapter.enrolTemplate();
    }

    @ReactMethod
    public void matchTemplate(String mRef) {
        this.adapter.matchTemplate(mRef);
    }

    @ReactMethod
    public void cleanFolder() {
        this.adapter.cleanFolder();
    }

    public void toast(String content){
        Toast.makeText(reactContext.getApplicationContext(),content,Toast.LENGTH_SHORT).show();
    }
}