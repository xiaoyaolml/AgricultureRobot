package com.leon.agriculturerobot.control;

import com.leon.agriculturerobot.config.Constant;

import org.opencv.core.Mat;

/**
 * Created by LML on 2016-05-06.
 */
public class ImageProcess implements Process {
    @Override
    public int apply(final Mat src, final Mat dst) {
        int result = 0;
        int flag = 0;
        /* 对图像进行一系列处理，根据图像处理结果返回对应的控制动作码 */
        // TODO: 2016-05-09 图像处理过程
        src.copyTo(dst);
//        System.gc();
        switch (flag) {
            case 0: // 特殊情况一，停止
                result = Constant.GO_STOP_CODE;
                break;
            case 1: // 检测为直线，前进
                result = Constant.GO_FORWARD_CODE;
                break;
            case 2: // 特殊情况二，后退
                result = Constant.GO_BACKWARD_CODE;
                break;
            case 3: // 检测为偏左，左转
                result = Constant.GO_LEFT_CODE;
                break;
            case 4: // 检测为偏右，右转
                result = Constant.GO_RIGHT_CODE;
                break;
            default:
                break;
        }

        return result;
    }
}
