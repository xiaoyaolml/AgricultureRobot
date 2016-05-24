package com.leon.agriculturerobot.control;

/**
 * Created by LML on 2016-05-24.
 */
public enum ControlSocketInstance {
    INSTANCE;

    ControlSocketInstance() {
    }

    private ControlSocket mControlSocket;

    public ControlSocket getControlSocket() {
        return mControlSocket;
    }

    public void setControlSocket(ControlSocket controlSocket) {
        this.mControlSocket = controlSocket;
    }
}
