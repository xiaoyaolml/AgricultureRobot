package com.leon.agriculturerobot.control;

/**
 * Created by LML on 2016-05-07.
 */
public enum RemoteControlInstance {
    INSTANCE;

    RemoteControlInstance() {

    }

    private RemoteControl mRemoteControl;

    public RemoteControl getRemoteControl() {
        return mRemoteControl;
    }

    public void setRemoteControl(RemoteControl remoteControl) {
        mRemoteControl = remoteControl;
    }
}
