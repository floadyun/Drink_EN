package com.iwinad.drink.seriaport;

import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPort;

/**
 * Created by yko on 2018/1/10.
 */

public class DataSerialPort extends BaseSerialPort<MixDrinkInfo, SerialPortResponse> {
    private final int LOADING = 1;
    private final int SUCCESS = 2;
    private final int SUCCESS_OVER_MIX_DRINK = 3;
    private final int FAIL = 4;

    private SerialPort mSerialPort = null;
    private InputStream in = null;
    private OutputStream out = null;

    Handler handler = new Handler();
    Boolean isBusy = false;
    WeakReference<ICommonResult<SerialPortResponse>> reference = null;
    MixDrinkInfo data;
    SerialPortThread serialPortThread;
    @Override
    public void init() {
        try {
            mSerialPort = new SerialPort(new File("/dev/ttyS1"), 9600, 0);
            in = mSerialPort.getInputStream();
            out = mSerialPort.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if(null != reference){
            reference.clear();
        }
        reference = null;
    }

    @Override
    public void write(MixDrinkInfo data, ICommonResult<SerialPortResponse> result) {
        synchronized (isBusy) {
            if (isBusy) {
                SerialPortResponse serialPortResult = new SerialPortResponse();
                serialPortResult.errorCode = SerialPortResponse.ERR_BUSY;
                if(result != null) {
                    result.callback(serialPortResult);
                }
                return;
            }
        }
        this.data = data;
        reference = new WeakReference<>(result);
        byte[] commend = getCommend();
        serialPortThread = new SerialPortThread(commend, data.timeOut);
        serialPortThread.start();
    }

    class SerialPortThread extends Thread{
        int left = 0;
        byte[] commend;
        ICommonResult<SerialPortResponse> result = null;
        SerialPortThread(byte[] commend, int left){
            this.commend = commend;
            this.left = left * 2;
        }

        @Override
        public void run() {
            try {
                cleanBuffer();

                synchronized (isBusy) {
                    isBusy = true;
                }
                try {
                    out.write(commend);
                    Log.d("demo", ByteUtils.bytes2HexString(commend));
                } catch (IOException e) {
                    e.printStackTrace();
                    synchronized (isBusy) {
                        isBusy = false;
                    }
                    final SerialPortResponse serialPortResult2 = new SerialPortResponse();
                    serialPortResult2.errorCode = SerialPortResponse.ERR_FAILED;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            result = reference.get();
                            if (result != null) {
                                result.callback(serialPortResult2);
                            }
                        }
                    });
                    return;
                }
                final SerialPortResponse serialPortResult = new SerialPortResponse();
                serialPortResult.errorCode = SerialPortResponse.ERR_START;
                serialPortResult.timeLeft = data.timeOut;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        result = reference.get();
                        if (result != null) {
                            result.callback(serialPortResult);
                        }
                    }
                });
                while (left > 0) {
                    left--;
                    try {
                        Thread.sleep(500);
                        if (left % 2 == 0) {
                            final SerialPortResponse serialPortResult1 = new SerialPortResponse();
                            serialPortResult1.errorCode = SerialPortResponse.ERR_WAITING;
                            serialPortResult1.timeLeft = left / 2;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    result = reference.get();
                                    if (result != null) {
                                        result.callback(serialPortResult1);
                                    }
                                }
                            });
                        }
                        if (processReadResult()) {
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                final SerialPortResponse serialPortResult2 = new SerialPortResponse();
                serialPortResult2.errorCode = SerialPortResponse.ERR_TIME_OUT;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        result = reference.get();
                        if (result != null) {
                            result.callback(serialPortResult2);
                        }
                    }
                });
                synchronized (isBusy) {
                    isBusy = false;
                }
            } catch (Exception e){
                final SerialPortResponse serialPortResult = new SerialPortResponse();
                serialPortResult.errorCode = SerialPortResponse.ERR_FAILED;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        result = reference.get();
                        if (result != null) {
                            result.callback(serialPortResult);
                        }
                    }
                });
                synchronized (isBusy) {
                    isBusy = false;
                }
            }
        }
    }

    private byte[] getCommend(){
        List<Byte> bList = new ArrayList<>();
        bList.add((byte)0x0A);
        bList.add((byte)0);
        bList.add((byte)0x01);
        bList.add((byte)data.type);
        bList.add((byte)0);

        byte verify = (byte)0x01;
        verify += (byte)data.type;
        int num = data.bottles.length;
        int realNum = 0;
        for(int i = 0; i < num; i++){
//            if(realNum > 10){
//                break;
//            }
            int capacity = data.formulaCapacitys[i];
            for(; ;){
                realNum++;
                if(capacity > 250){
                    bList.add((byte)data.bottles[i]);
                    bList.add((byte)0xFA);
                    verify += (byte)data.bottles[i];
                    verify += (byte)0xFA;
                    capacity -= 250;
                } else {
                    bList.add((byte)data.bottles[i]);
                    bList.add((byte)capacity);
                    verify += (byte)data.bottles[i];
                    verify += (byte)capacity;
                    break;
                }
//                if(realNum > 10){
//                    break;
//                }
            }
        }
        verify += realNum;
        int count = realNum * 2 + 3;
        bList.set(1, (byte)count);
        bList.set(4, (byte)realNum);
        bList.add(verify);
        Byte[] commend = new Byte[bList.size()];
        bList.toArray(commend);
        return toPrimitives(commend);
    }

    private byte[] toPrimitives(Byte[] oBytes){
        byte[] bytes = new byte[oBytes.length];

        for(int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }

    private void cleanBuffer(){
        int len = 0;
        try {
            len = in.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(len <= 0){
            return;
        }

        byte[] buffer = new byte[len];
        try {
            in.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean processReadResult(){
//        int type = data.type;
//        if(data.formulaCapacitys != null) {
//            type = getType(data.type);
//        }
//        byte cmd;
        int count;

        int len = 0;
        try {
            len = in.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(len <= 0){
            return false;
        }

        byte[] buffer = new byte[len];
        try {
            in.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try{
            for(int i = 0; i < len; i++){
                if(buffer[i] == 0x0A){      // 头
                    count = buffer[i + 1];  // 字节总数
//                    cmd = buffer[i + 2];    // 操作命令
//                    if(cmd != type){
//                        continue;
//                    }
                    if(count > 0){
                        byte[] data = new byte[count-1];   // 数据
                        byte verify = buffer[i + 2];    // 校验码
                        for(int j = 0; j < count-1; j++){
                            data[j] = buffer[i + 3 + j];
                            verify += data[j];
                        }
                        if(verify == buffer[i + 2 + count]){
                            if(data[0] == LOADING) {
                                final SerialPortResponse serialPortResult1 = new SerialPortResponse();
                                serialPortResult1.errorCode = SerialPortResponse.ERR_BUSY;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ICommonResult result = reference.get();
                                        if(null != result) {
                                            result.callback(serialPortResult1);
                                        }
                                    }
                                });
                            } else if(data[0] == SUCCESS){
                                final SerialPortResponse serialPortResult1 = new SerialPortResponse();
                                serialPortResult1.errorCode = SerialPortResponse.ERR_SUCCEED;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ICommonResult result = reference.get();
                                        if(null != result) {
                                            result.callback(serialPortResult1);
                                        }
                                    }
                                });
                                synchronized (isBusy) {
                                    isBusy = false;
                                }
                                return true;
                            } else if(data[0] == SUCCESS_OVER_MIX_DRINK){
                                final SerialPortResponse serialPortResult1 = new SerialPortResponse();
                                serialPortResult1.errorCode = SerialPortResponse.ERR_SUCCEED;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ICommonResult result = reference.get();
                                        if(null != result) {
                                            result.callback(serialPortResult1);
                                        }
                                    }
                                });
                                synchronized (isBusy) {
                                    isBusy = false;
                                }
                                return true;
                            } else {    // FAILED
                                final SerialPortResponse serialPortResult1 = new SerialPortResponse();
                                serialPortResult1.errorCode = SerialPortResponse.ERR_FAILED;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ICommonResult result = reference.get();
                                        if(null != result) {
                                            result.callback(serialPortResult1);
                                        }
                                    }
                                });
                                synchronized (isBusy) {
                                    isBusy = false;
                                }
                                return true;
                            }
                        } else {
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return false;
    }
}
