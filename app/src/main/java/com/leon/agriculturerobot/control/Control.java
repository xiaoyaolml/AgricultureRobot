package com.leon.agriculturerobot.control;

/**
 * Created by LML on 2016-05-06.
 */
public interface Control {
    boolean init(String... args);
    void goForward();
    void goBackward();
    void goLeft();
    void goRight();
    void goStop();

    void setSpeed(int speed);
    void setRelay(int which,int mode);
}
