package com.fingerapp.apdapter;

import com.facebook.react.bridge.WritableMap;
public interface FingerDevice {
    public FingerDeviceId getFingerDeviceId();
    public WritableMap toRNWritableMap();
}
