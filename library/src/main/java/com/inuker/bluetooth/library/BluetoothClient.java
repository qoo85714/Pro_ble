package com.inuker.bluetooth.library;

import android.content.Context;
import android.util.Log;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleReadRssiResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.inuker.bluetooth.library.utils.proxy.ProxyUtils;

import java.lang.reflect.Array;
import java.util.UUID;

import static com.inuker.bluetooth.library.Code.REQUEST_SUCCESS;

/**
 * Created by dingjikerbo on 2016/9/1.
 */
public class BluetoothClient implements IBluetoothClient {

    private IBluetoothClient mClient;

    public BluetoothClient(Context context) {
        if (context == null) {
            throw new NullPointerException("Context null");
        }
        mClient = BluetoothClientImpl.getInstance(context);
    }

    public void connect(String mac, BleConnectResponse response) {
        connect(mac, null, response);
    }

    @Override
    public void connect(String mac, BleConnectOptions options, BleConnectResponse response) {
        BluetoothLog.v(String.format("connect %s", mac));
        response = ProxyUtils.getUIProxy(response);
        mClient.connect(mac, options, response);
    }

    @Override
    public void disconnect(String mac) {
        BluetoothLog.v(String.format("disconnect %s", mac));
        mClient.disconnect(mac);
    }

    @Override
    public void read(String mac, UUID service, UUID character, BleReadResponse response) {
        BluetoothLog.v(String.format("read character for %s: service = %s, character = %s", mac, service, character));

        //response = ProxyUtils.getUIProxy(response);
        mClient.read(mac, service, character, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (code == REQUEST_SUCCESS) {
                    Log.i("brad:","read:REQUEST_SUCCESS");
                    StringBuilder sb = new StringBuilder();
                    for(int i = 0; i < data.length; i++){
                        sb.append(data[i]+" ");
                    }
                    Log.i("brad:",""+sb.toString());
                }
            }
        });
    }




    @Override
    public void write(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("write character for %s: service = %s, character = %s, value = %s",
                mac, service, character, ByteUtils.byteToString(value)));
        byte[] write = hex2Byte("5a d5 05 26 01 02 aa 00 c0 00 00 00 00 aa");

        //response = ProxyUtils.getUIProxy(response);
        mClient.write(mac, service, character, write, new BleWriteResponse() {

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {


                    Log.i("brad:","write:REQUEST_SUCCESS");


                }else {
                    Log.i("brad:","write:REQUEST_FAIL");
                }
            }
        });
    }

    @Override
    public void readDescriptor(String mac, UUID service, UUID character, UUID descriptor, BleReadResponse response) {
        BluetoothLog.v(String.format("readDescriptor for %s: service = %s, character = %s", mac, service, character));
        //response = ProxyUtils.getUIProxy(response);
        mClient.readDescriptor(mac, service, character, descriptor, new BleReadResponse() {
            @Override
            public void onResponse(int code, byte[] data) {
                if (code == REQUEST_SUCCESS) {
                    Log.i("brad:", "readDescriptor:REQUEST_SUCCESS");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < data.length; i++) {
                        sb.append(data[i] + " ");
                    }
                    Log.i("brad:", "" + sb.toString());
                }
            }
        });
    }

    @Override
    public void writeDescriptor(String mac, UUID service, UUID character, UUID descriptor, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("writeDescriptor for %s: service = %s, character = %s", mac, service, character));
        response = ProxyUtils.getUIProxy(response);
        mClient.writeDescriptor(mac, service, character, descriptor, value, response);
    }

    @Override
    public void writeNoRsp(String mac, UUID service, UUID character, byte[] value, BleWriteResponse response) {
        BluetoothLog.v(String.format("writeNoRsp %s: service = %s, character = %s, value = %s", mac, service, character, ByteUtils.byteToString(value)));

        response = ProxyUtils.getUIProxy(response);
        mClient.writeNoRsp(mac, service, character, value, response);
    }

    @Override
    public void notify(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BluetoothLog.v(String.format("notify %s: service = %s, character = %s", mac, service, character));

        //response = ProxyUtils.getUIProxy(response);
        mClient.notify(mac, service, character, new BleNotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.i("brad:", "notifyonResponse:REQUEST_SUCCESS");
                }
            }
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < value.length; i++) {
//                    sb.append(bytesToHexString(value));
//
//                }

                Log.i("brad:", "onnotify:REQUEST_SUCCESS");
                Log.i("brad:value", bytesToHexString(value));
                Log.i("brad:service", ""+ service);
                Log.i("brad:chacter", ""+character);

            }
        });
    }

    @Override
    public void unnotify(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        BluetoothLog.v(String.format("unnotify %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.unnotify(mac, service, character, response);
    }

    @Override
    public void indicate(String mac, UUID service, UUID character, BleNotifyResponse response) {
        BluetoothLog.v(String.format("indicate %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.indicate(mac, service, character, response);
    }

    @Override
    public void unindicate(String mac, UUID service, UUID character, BleUnnotifyResponse response) {
        BluetoothLog.v(String.format("indicate %s: service = %s, character = %s", mac, service, character));

        response = ProxyUtils.getUIProxy(response);
        mClient.unindicate(mac, service, character, response);
    }

    @Override
    public void readRssi(String mac, BleReadRssiResponse response) {
        BluetoothLog.v(String.format("readRssi %s", mac));

        response = ProxyUtils.getUIProxy(response);
        mClient.readRssi(mac, response);
    }

    @Override
    public void search(SearchRequest request, SearchResponse response) {
        BluetoothLog.v(String.format("search %s", request));

        response = ProxyUtils.getUIProxy(response);
        mClient.search(request, response);
    }

    @Override
    public void stopSearch() {
        BluetoothLog.v(String.format("stopSearch"));
        mClient.stopSearch();
    }

    @Override
    public void registerConnectStatusListener(String mac, BleConnectStatusListener listener) {
        mClient.registerConnectStatusListener(mac, listener);
    }

    @Override
    public void unregisterConnectStatusListener(String mac, BleConnectStatusListener listener) {
        mClient.unregisterConnectStatusListener(mac, listener);
    }

    @Override
    public void registerBluetoothStateListener(BluetoothStateListener listener) {
        mClient.registerBluetoothStateListener(listener);
    }

    @Override
    public void unregisterBluetoothStateListener(BluetoothStateListener listener) {
        mClient.unregisterBluetoothStateListener(listener);
    }

    @Override
    public void registerBluetoothBondListener(BluetoothBondListener listener) {
        mClient.registerBluetoothBondListener(listener);
    }

    @Override
    public void unregisterBluetoothBondListener(BluetoothBondListener listener) {
        mClient.unregisterBluetoothBondListener(listener);
    }

    public int getConnectStatus(String mac) {
        return BluetoothUtils.getConnectStatus(mac);
    }

    public boolean isBluetoothOpened() {
        return BluetoothUtils.isBluetoothEnabled();
    }

    public boolean openBluetooth() {
        return BluetoothUtils.openBluetooth();
    }

    public boolean closeBluetooth() {
        return BluetoothUtils.closeBluetooth();
    }

    public boolean isBleSupported() {
        return BluetoothUtils.isBleSupported();
    }

    public int getBondState(String mac) {
        return BluetoothUtils.getBondState(mac);
    }

    @Override
    public void clearRequest(String mac, int type) {
        mClient.clearRequest(mac, type);
    }

    @Override
    public void refreshCache(String mac) {
        mClient.refreshCache(mac);
    }


    public static String bytesToHexString(byte[] value) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (value == null || value.length <= 0) {
            return null;
        }
        for (int i = 0; i < value.length; i++) {
            int v = value[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv+" ");
        }
        return stringBuilder.toString();
    }
    public static String byteArrayToHex(byte[] value) {
            if (value == null || value.length == 0)
                return null;
            StringBuffer sb = new StringBuffer(value.length * 2);
           String hexNumber;
            for (int x = 0; x < value.length; x++)
              {
                  hexNumber = "0" + Integer.toHexString(0xff & value[x]);
                  sb.append(hexNumber.substring(hexNumber.length() - 2));
             }
           return sb.toString();
       }
    public byte[] hex2Byte(String hexString) {
         byte[] bytes = new byte[hexString.length() / 2];
          for (int i=0 ; i<bytes.length ; i++) {
            bytes[i] =
                    (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
            }
        return bytes;
    }

}
