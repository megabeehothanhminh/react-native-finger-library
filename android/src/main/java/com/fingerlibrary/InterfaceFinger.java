package com.fingerlibrary;
import com.facebook.react.bridge.Callback;

/**
 * Created by xiesubin on 2017/9/21.
 */

public interface InterfaceFinger {
    public void init(Callback successCallback, Callback errorCallback);
    public void getDeviceList(Callback successCallback, Callback errorCallback);
//
//    public void getDeviceList(Callback successCallback, Callback errorCallback);
//
//    @ReactMethod
//    public void printRawData(String base64Data, Callback errorCallback) ;
//
//    @ReactMethod
//    public void printImageData(String imageUrl, int imageWidth, int imageHeight, Callback errorCallback);
//
//    @ReactMethod
//    public void printImageBase64(String base64, int imageWidth, int imageHeight, Callback errorCallback) ;
}

