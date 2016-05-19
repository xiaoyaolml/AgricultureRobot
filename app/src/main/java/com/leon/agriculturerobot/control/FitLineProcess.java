package com.leon.agriculturerobot.control;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by LML on 2016-05-19.
 */
public class FitLineProcess implements Process {
    @Override
    public int apply(Mat lines, Mat bgr) {

        Point point1 = new Point();
        Point point2 = new Point();
        Point point3 = new Point();
        Point point4 = new Point();

        double maxValue = 0;

        int count = 0;


        for (int i = 0; i < lines.rows(); i++) {
            double[] var = lines.get(i, 0);
            point1.x = var[0];
            point1.y = var[1];
            point2.x = var[2];
            point2.y = var[3];

            // 横线，忽略
            if (point1.y - point2.y == 0) {
                continue;
            }

            // 竖线，忽略
            if (point1.x-point2.x == 0) {
                continue;
            }

            // 中间区域(中心线-100,中心线+100)之外的点，忽略
            if ((Math.abs(point1.x-0.5*bgr.cols())>100)||(Math.abs(point2.x-0.5*bgr.cols()))>100) {
                continue;
            }

            // 有效点处理
            {
                System.out.println("point1(x,y)="+point1.x+","+point1.y+";point2(x,y)="+point2.x+","+point2.y);
                double k = (point2.y-point1.y)/(point2.x-point1.x);
                double b = point2.y - point2.x*k;
                // 将两点延长至直线
                point1.y = bgr.rows();
                point1.x = (point1.y-b)/k;
                point2.y = 0;
                point2.x = -b/k;


                System.out.println("k="+k+";b="+b+";count"+(++count));
                Imgproc.line(bgr, point1, point2, new Scalar(255,0,0),1);

                double value = Math.abs(k);
                if (value>=maxValue) {
                    maxValue = value;
                    point3.x = point1.x;
                    point3.y = point1.y;
                    point4.x = point2.x;
                    point4.y = point2.y;
                    System.out.println("minValue="+maxValue);
                }
            }
        }

        Imgproc.line(bgr, point3, point4, new Scalar(0,0,255),10);
        return 0;
    }
}
