package com.leon.agriculturerobot.utils;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by LML on 2016-05-07.
 */
public class SocketPort {

    private Socket mSocket;

    public Socket getSocket() {
        return mSocket;
    }

    public SocketPort(String ip, String port) throws IOException {
//        mSocket = new Socket(InetAddress.getByName(ip), Integer.parseInt(port));
        mSocket = new Socket(ip,Integer.parseInt(port));
    }

    public void close() {
        if (mSocket!=null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
