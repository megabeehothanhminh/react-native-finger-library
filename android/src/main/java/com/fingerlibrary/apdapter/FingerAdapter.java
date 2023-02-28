package com.fingerapp.apdapter;


import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;

import java.util.List;


public interface FingerAdapter {

    public void init(ReactApplicationContext reactContext, Callback successCallback, Callback errorCallback);
    public List<FingerDevice> getDeviceList(Callback errorCallback);
    public void selectDevice(String printerDeviceId);
    public void enrolTemplate();
    public void matchTemplate(String mRef);
    public void cleanFolder();

}
