package com.leon.agriculturerobot.config;

/**
 * Created by LML on 2016-05-06.
 */
public class Constant {
    /* 控制运动参数：前后左右停 */
    public static final int GO_STOP_CODE = 0;
    public static final int GO_FORWARD_CODE = 1;
    public static final int GO_BACKWARD_CODE = 2;
    public static final int GO_LEFT_CODE = 3;
    public static final int GO_RIGHT_CODE = 4;
    public static final byte[] GO_FORWARD = {(byte) 0xff, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0xff};
    public static final byte[] GO_BACKWARD = {(byte) 0xff, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0xff};
    public static final byte[] GO_LEFT = {(byte) 0xff, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0xff};
    public static final byte[] GO_RIGHT = {(byte) 0xff, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0xff};
    public static final byte[] GO_STOP = {(byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff};

    /* 串口设置参数 */
    public static final String SERIAL_PORT_PATH = "/dev/ttySAC1";
    public static final int SERIAL_PORT_BAUDRATE = 115200;

    /* 网络参数设置 */
    public static final String SOCKET_IP = "192.168.1.1";
    public static final String SOCKET_PORT = "2016";
    public static final String GATEWAY= "http://192.168.1.1";



}
