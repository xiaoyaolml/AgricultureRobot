package com.leon.agriculturerobot.control;

import org.opencv.core.Mat;

/**
 * Created by LML on 2016-05-06.
 */
public interface Process {
    int apply(final Mat src , final Mat dst);
}
