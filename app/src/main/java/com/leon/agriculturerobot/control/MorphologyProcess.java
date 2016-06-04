package com.leon.agriculturerobot.control;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Created by LML on 2016-05-18.
 */
public class MorphologyProcess implements Process {
    private final Mat mErodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
    private final Mat mDilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));

    @Override
    public int apply(Mat threshold, Mat morphology) {
        Mat erode = new Mat();
        Imgproc.erode(threshold, erode, mErodeKernel);
        Imgproc.dilate(erode, morphology, mDilateKernel);
        erode.release();
        return 0;
    }
}
