package com.leon.agriculturerobot.control;

import android.util.Log;

import com.leon.agriculturerobot.config.Constant;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LML on 2016-05-19.
 */
public class FitLineProcess implements Process {
    private static final String TAG = "FitLineProcess";
    private List<Point> mPoints = new ArrayList<>();
    private double mSlope;
    public static final double SLOPE_VALUE = 5.0;

    @Override
    public void apply(Mat src, Mat dst) {
        int rows = dst.rows();
        for (int i = 0; i < src.rows(); i++) {
            double[] var = src.get(i, 0);
            Imgproc.line(dst, new Point(var[0], var[1]), new Point(var[0], var[1]), new Scalar(255, 0, 0), 5);
            Imgproc.line(dst, new Point(var[2], var[3]), new Point(var[2], var[3]), new Scalar(255, 0, 0), 5);
            mPoints.add(new Point(var[0], var[1]));
            mPoints.add(new Point(var[2], var[3]));
        }

        if (mPoints.size() > 0) {
            MatOfPoint matOfPoint = new MatOfPoint();
            matOfPoint.fromList(mPoints);
            Mat line = new Mat();
            Imgproc.fitLine(matOfPoint, line, Imgproc.CV_DIST_L2, 0, 0.01, 0.01);

            ArrayList<Double> vars = new ArrayList<>();
            for (int i = 0; i < line.rows(); i++) {
                double[] var = line.get(i, 0);
                vars.add(var[0]);
            }
//            double k = vars.get(1) / vars.get(0);
            mSlope = vars.get(1) / vars.get(0);
            double b = vars.get(3) - vars.get(2) * mSlope;
            Imgproc.line(dst, new Point((rows - b) / mSlope, rows), new Point(-b / mSlope, 0), new Scalar(0, 0, 255), 5);

            matOfPoint.release();
            line.release();
        } else {
            Log.d(TAG, "no point fit");
            mSlope = 0;
        }
        mPoints.clear();
    }

    public int translate() {
        int result;
        if (Math.abs(mSlope) > 5) {
            // 前进
            Log.i(TAG, mSlope + "：前进");
            result = Constant.GO_FORWARD_CODE;
        } else if ((mSlope > 0) && (mSlope <= SLOPE_VALUE)) {
            // 左转
            Log.i(TAG, mSlope + "：左转");
            result = Constant.GO_LEFT_CODE;
        } else if ((mSlope >= -SLOPE_VALUE) && (mSlope < 0)) {
            // 右转
            result = Constant.GO_RIGHT_CODE;
            Log.i(TAG, mSlope + "：右转");
        } else {
            // 停止
            result = Constant.GO_STOP_CODE;
            Log.i(TAG, mSlope + "：停止");
        }

        return result;
    }

//    @Override
//    public int apply(Mat lines, Mat bgr) {
//
//        Point point1 = new Point();
//        Point point2 = new Point();
//        Point point3 = new Point();
//        Point point4 = new Point();
//
//        double maxValue = 0;
//
//        int count = 0;
//
//
//        for (int i = 0; i < lines.rows(); i++) {
//            double[] var = lines.get(i, 0);
//            point1.x = var[0];
//            point1.y = var[1];
//            point2.x = var[2];
//            point2.y = var[3];
//
//            // 横线，忽略
//            if (point1.y - point2.y == 0) {
//                continue;
//            }
//
//            // 竖线，忽略
//            if (point1.x-point2.x == 0) {
//                continue;
//            }
//
//            // 中间区域(中心线-100,中心线+100)之外的点，忽略
//            if ((Math.abs(point1.x-0.5*bgr.cols())>100)||(Math.abs(point2.x-0.5*bgr.cols()))>100) {
//                continue;
//            }
//
//            // 有效点处理
//            {
//                System.out.println("point1(x,y)="+point1.x+","+point1.y+";point2(x,y)="+point2.x+","+point2.y);
//                double k = (point2.y-point1.y)/(point2.x-point1.x);
//                double b = point2.y - point2.x*k;
//                // 将两点延长至直线
//                point1.y = bgr.rows();
//                point1.x = (point1.y-b)/k;
//                point2.y = 0;
//                point2.x = -b/k;
//
//
//                System.out.println("k="+k+";b="+b+";count"+(++count));
//                Imgproc.line(bgr, point1, point2, new Scalar(255,0,0),1);
//
//                double value = Math.abs(k);
//                if (value>=maxValue) {
//                    maxValue = value;
//                    point3.x = point1.x;
//                    point3.y = point1.y;
//                    point4.x = point2.x;
//                    point4.y = point2.y;
//                    System.out.println("minValue="+maxValue);
//                }
//            }
//        }
//
//        Imgproc.line(bgr, point3, point4, new Scalar(0,0,255),10);
//        return 0;
//    }
}
