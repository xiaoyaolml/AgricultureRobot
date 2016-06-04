package com.leon.agriculturerobot.control;

import com.leon.agriculturerobot.utils.SocketPort;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by LML on 2016-05-24.
 */
public class ControlSocket extends ControlAdapter {
    private Socket mSocket;

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public boolean init(String... args) {
        boolean isSuccess = false;
        try {
            SocketPort socketPort = new SocketPort(args[0], args[1]);
            mSocket = socketPort.getSocket();
            super.setOutputStream(mSocket.getOutputStream());
            isSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }
}
