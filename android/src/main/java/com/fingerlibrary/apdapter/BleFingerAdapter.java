package com.fingerapp.apdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.widget.ArrayAdapter;


import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.fpreader.fpdevice.BluetoothReader;

public class BleFingerAdapter implements FingerAdapter {

    private static BleFingerAdapter mInstance;
    public static final int REQUEST_CONNECT_DEVICE = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    public  int worktype=0;
    //ref
    public byte mRefData[] =new byte[512];
    public int mRefSize=0;
    //match
    public byte mMatData[]=new byte[512];
    public int mMatSize=0;

    public byte mBat[]=new byte[2];
//    public byte mMatData[]=new byte[512];


    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    protected BluetoothReader mBluetoothReader;

    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    public ReactApplicationContext mContext;
    public Handler mHandler;
    private BleFingerAdapter() {
    }

    public static BleFingerAdapter getInstance() {
        if (mInstance == null) {
            mInstance = new BleFingerAdapter();

        }
        return mInstance;
    }

    @Override
    public void init(ReactApplicationContext reactContext, Callback successCallback, Callback errorCallback) {
        this.mContext = reactContext;
        BluetoothAdapter bluetoothAdapter = getBTAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            errorCallback.invoke("bluetooth adapter is not enabled");
            return;
        } else {
            successCallback.invoke();
            getDirectory();
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                WritableMap params = Arguments.createMap();

                switch (msg.what) {
                    case BluetoothReader.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothReader.STATE_CONNECTED:
                                params.putInt("typeStatus", BluetoothReader.STATE_CONNECTED);
                                sendEvent(reactContext, params);
                                break;
                            case BluetoothReader.STATE_CONNECTING:
                                params.putInt("typeStatus", BluetoothReader.STATE_CONNECTING);
                                sendEvent(reactContext, params);
                                break;
                            case BluetoothReader.STATE_LISTEN:
                            case BluetoothReader.STATE_NONE:

                                params.putInt("typeStatus", BluetoothReader.STATE_NONE);
                                sendEvent(reactContext, params);
                                break;
                        }
                        break;
                    case BluetoothReader.MESSAGE_WRITE:
                        break;
                    case BluetoothReader.MESSAGE_READ:
                        break;
                    case BluetoothReader.MESSAGE_DEVICE_NAME:
                        params.putInt("typeStatus", BluetoothReader.MESSAGE_DEVICE_NAME);
                        params.putString("mConnectedDeviceName", msg.getData().getString(BluetoothReader.DEVICE_NAME));
                        sendEvent(reactContext, params);
                        break;
                    case BluetoothReader.MESSAGE_TOAST:
                        switch(msg.getData().getInt(BluetoothReader.MSGVAL)) {
                            case BluetoothReader.MSG_UNABLE:
                                params.putInt("typeStatus", BluetoothReader.MSG_UNABLE);
                                params.putString("msgErrol", "Unable to connect device");
                                sendEvent(reactContext, params);
                                break;
                            case BluetoothReader.MSG_LOST:
                                params.putInt("typeStatus", BluetoothReader.MSG_LOST);
                                params.putString("msgErrol", "Device connection was lost");
                                sendEvent(reactContext, params);
                                break;
                        }
                        break;
                    case BluetoothReader.CMD_GETDEVTYPE:

                        break;
                    case BluetoothReader.CMD_CHKPIN:

                        break;
                    case BluetoothReader.CMD_GETDEVINFO:
                        break;

                    case BluetoothReader.CMD_GETSTDIMAGE:
                        params.putInt("typeStatus", BluetoothReader.CMD_GETSTDIMAGE);
                        sendEvent(reactContext, params);
                        break;
                    case BluetoothReader.CMD_GETRESIMAGE:
                        if(msg.arg1==1){
                            byte[] bmpdata = null;
                            switch(msg.arg2){
                                case BluetoothReader.IMAGESIZE_152_200:
                                    bmpdata=mBluetoothReader.getFingerprintImage((byte[]) msg.obj,152,200,0);
                                    break;
                                case BluetoothReader.IMAGESIZE_256_288:
                                    bmpdata=mBluetoothReader.getFingerprintImage((byte[]) msg.obj,256,288,0);
                                    break;
                                case BluetoothReader.IMAGESIZE_256_360:
                                    bmpdata=mBluetoothReader.getFingerprintImage((byte[]) msg.obj,256,360,0);
                                    break;
                            }
                            Bitmap image = BitmapFactory.decodeByteArray(bmpdata, 0,bmpdata.length);
                            String uri =  SaveImage(image);
                            if(uri != "") {
                                params.putInt("typeStatus", BluetoothReader.CMD_GETRESIMAGE);
                                params.putString("uri", uri);
                                sendEvent(reactContext, params);
                            }
                        }
                        break;
                    case BluetoothReader.CMD_GETSTDCHAR:
                    case BluetoothReader.CMD_GETRESCHAR:
                        if(msg.arg1==1){
                            if(worktype==1){
                                mBluetoothReader.memcpy(mMatData,0,(byte[]) msg.obj,0,msg.arg2);
                                int score = mBluetoothReader.MatchTemplate(mRefData, mMatData);
                                params.putInt("typeStatus", BluetoothReader.CMD_GETRESCHAR);
                                params.putString("worktype", "1");
                                params.putString("msgMatch", "Match Score: " + String.valueOf(score));
                                sendEvent(reactContext, params);
                                mMatData = new byte[512];
                                mRefData = new byte[512];
                            }else{
                                mBluetoothReader.memcpy(mRefData,0,(byte[]) msg.obj,0,msg.arg2);
                                params.putInt("typeStatus", BluetoothReader.CMD_GETRESCHAR);
                                params.putString("worktype", "0");
                                params.putString("mRefData", Arrays.toString(mRefData));
                                params.putString("msgEnrol", "Enrol Data Succeed");
                                sendEvent(reactContext, params);
                                mRefData = new byte[512];
                            }
                        }else {
                            if(worktype==1){
                                params.putString("msgError", "Capture Data Fail");
                            }else{
                                params.putString("msgError", "Enrol Data Fail");
                            }
                        }
                        break;
                    case BluetoothReader.CMD_ENROLHOST:
                        break;
                    case BluetoothReader.CMD_CAPTUREHOST:

                        break;
                    case BluetoothReader.CMD_GETSN:
                        break;
                    case BluetoothReader.CMD_GETBAT:

                        break;
                    case BluetoothReader.CMD_SHUTDOWNDEVICE: {
                    }
                    break;
                }
            }
        };
        this.mBluetoothReader = new BluetoothReader(mContext, mHandler);
        if (mBluetoothReader != null) {
            if (mBluetoothReader.getState() == mBluetoothReader.STATE_NONE) {
                mBluetoothReader.start();
            }
        }
    }

    @Override
    public List<FingerDevice> getDeviceList(Callback errorCallback) {
        BluetoothAdapter bluetoothAdapter = getBTAdapter();
        List<FingerDevice> printerDevices = new ArrayList<FingerDevice>();
        if (bluetoothAdapter == null) {
            errorCallback.invoke("No bluetooth adapter available");
            return printerDevices;
        }
        if (!bluetoothAdapter.isEnabled()) {
            errorCallback.invoke("bluetooth is not enabled");
            return printerDevices;
        }
        Set<BluetoothDevice> pairedDevices = getBTAdapter().getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            printerDevices.add(new BLEFingerDevice(device));
        }
        return printerDevices;
    }


    @Override
    public void selectDevice(String printerDeviceId) {
        BluetoothAdapter bluetoothAdapter = getBTAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(printerDeviceId);
        this.mBluetoothReader.connect(device);
    }

    @Override
    public void enrolTemplate() {
        if(this.mBluetoothReader.getState()==BluetoothReader.STATE_CONNECTED) {
            worktype=0;
            mBluetoothReader.GetImageAndDate();
        }
    }

    @Override
    public void matchTemplate(String mRef) {
        String[] byteValues = mRef.substring(1, mRef.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];
        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        mRefData = bytes;
        if(this.mBluetoothReader.getState()==BluetoothReader.STATE_CONNECTED) {
            worktype=1;
            mBluetoothReader.GetImageAndDate();
        }
    }

    private static BluetoothAdapter getBTAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }
    //

    public static String getDataTimeForID(){
        Time t=new Time();
        t.setToNow();
        int year = t.year;
        int month = t.month+1;
        int date = t.monthDay;
        int hour = t.hour;
        int minute = t.minute;
        int second = t.second;
        return String.format("%d%02d%02d%02d%02d%02d",year,month,date,hour,minute,second);
    }
    public void getDirectory(){
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "finger");
        if (!file.exists()) {
            file.mkdirs();
        }
        else {
            file.delete();
            file.mkdirs();
        }
    }

    private String SaveImage(Bitmap image){
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "finger/"+ getDataTimeForID().toString()+".png");
        if (f.exists())
        {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return f.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    protected void sendEvent(ReactContext reactContext, WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("EventFinger", params);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }
    @Override
    public void cleanFolder() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "finger");
            if (file.exists()) {
                deleteRecursive(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


}
