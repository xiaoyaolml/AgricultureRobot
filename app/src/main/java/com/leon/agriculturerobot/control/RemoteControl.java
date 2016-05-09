package com.leon.agriculturerobot.control;

import com.leon.agriculturerobot.config.Constant;
import com.leon.agriculturerobot.utils.SocketPort;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by LML on 2016-05-06.
 */
public class RemoteControl implements Control {
    private Socket mSocket;
    private OutputStream mOutputStream;

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public boolean init(String... args) {
        boolean isSuccess = false;
        try {
            SocketPort socketPort = new SocketPort(args[0],args[1]);
            mSocket = socketPort.getSocket();
            mOutputStream = mSocket.getOutputStream();
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
        byte[] commands = {(byte) 0xff, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0xff};
        commands[3] =  (byte) speed;
        sendCommand(commands);
    }

    @Override
    public void setRelay(int which, int mode) {
        byte[] commands = {(byte) 0xff, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0xff};
        commands[2] =  (byte) which;
        commands[3] =  (byte) mode;
        sendCommand(commands);
    }

    private void sendCommand(byte[] commands) {
        try {
            mOutputStream.write(commands);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
