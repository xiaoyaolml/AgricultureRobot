package com.leon.agriculturerobot.control;

import com.leon.agriculturerobot.config.Constant;
import com.leon.agriculturerobot.utils.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by LML on 2016-05-06.
 */
public class LocalControl implements Control {
    private SerialPort mSerialPort;
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public SerialPort getSerialPort() {
        return mSerialPort;
    }

    @Override
    public boolean init(String... args) {
        boolean isSuccess = false;
        try {
            mSerialPort = new SerialPort(new File(Constant.SERIAL_PORT_PATH),Constant.SERIAL_PORT_BAUDRATE,0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    @Override
    public void goForward() {
        sendCommand(Constant.GO_FORWARD);
    }

    @Override
    public void goBackward() {
        sendCommand(Constant.GO_BACKWARD);
    }

    @Override
    public void goLeft() {
        sendCommand(Constant.GO_LEFT);
    }

    @Override
    public void goRight() {
        sendCommand(Constant.GO_RIGHT);
    }

    @Override
    public void goStop() {
        sendCommand(Constant.GO_STOP);
    }

    @Override
    public void setSpeed(int speed) {

    }

    @Override
    public void setRelay(int which, int mode) {

    }

    private void sendCommand(byte[] commands) {
        try {
            mOutputStream.write(commands);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onDataReceived(byte[] buffer, int size) {

    }

    class  RecvThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                byte[] buffer = new byte[64];
                if (mInputStream == null) {
                    return;
                }
                try {
                    size = mInputStream.read(buffer);
                    if (size>0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
