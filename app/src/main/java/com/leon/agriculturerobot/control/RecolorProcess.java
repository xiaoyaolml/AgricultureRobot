package com.leon.agriculturerobot.control;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by LML on 2016-05-18.
 * 过绿色彩变换
 */
public class RecolorProcess implements Process {
    private final ArrayList<Mat> mChannels = new ArrayList<>(4);

    @Override
    public int apply(Mat src, Mat dst) {
        Core.split(src, mChannels);
        final Mat r = mChannels.get(0);
        final Mat g = mChannels.get(1);
        final Mat b = mChannels.get(2);
        Core.addWeighted(g, 2, r, -1, 0, dst);
        Core.addWeighted(dst, 1, b, -1, 0, dst);
        r.release();
        g.release();
        b.release();
        return 0;
    }
}
