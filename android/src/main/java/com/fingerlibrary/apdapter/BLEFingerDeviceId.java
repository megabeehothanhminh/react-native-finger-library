package com.fingerapp.apdapter;


/**
 * Created by xiesubin on 2017/9/21.
 */

public class BLEFingerDeviceId extends FingerDeviceId {
    private String innerMacAddress;

    public static BLEFingerDeviceId valueOf(String innerMacAddress) {
        return new BLEFingerDeviceId(innerMacAddress);
    }

    private BLEFingerDeviceId(String innerMacAddress) {
        this.innerMacAddress = innerMacAddress;
    }

    public String getInnerMacAddress() {
        return innerMacAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BLEFingerDeviceId that = (BLEFingerDeviceId) o;

        return innerMacAddress.equals(that.innerMacAddress);

    }

    @Override
    public int hashCode() {
        return innerMacAddress.hashCode();
    }
}
