package com.leon.agriculturerobot.control;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by LML on 2016-05-18.
 */
public class MorphologyProcess implements Process {
    @Override
    public int apply(Mat threshold, Mat morphology) {
        final Mat erode = new Mat();
        final Mat kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3));
        final Mat kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(2,2));
        Imgproc.erode(threshold,erode,kernel1);
        Imgproc.dilate(erode,morphology,kernel2);
        return 0;
    }
}
