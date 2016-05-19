package com.leon.agriculturerobot.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by LML on 2016-04-08.
 */
public class MatUtil {
    public static void showPixel(Mat mat) {
        System.out.println(mat.rows() + "行" + mat.cols() + "列");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                for (double p : mat.get(i, j)) {
                    System.out.printf("%f ", p);
                }
                System.out.print(", ");
            }
            System.out.println();
        }
        System.out.println("用时：" + (System.currentTimeMillis() - startTime) + "毫秒");
    }


    public static void showInfo(Mat mat) {
        System.out.println("Mat:" + mat);
        System.out.println("Mat cols:" + mat.cols());// 列数，宽
        System.out.println("Mat rows:" + mat.rows());// 行数，高
        System.out.println("Mat size:" + mat.size());
        System.out.println("Mat type:" + mat.type());
        System.out.println("Mat depth:" + mat.depth());
        System.out.println("Mat channels:" + mat.channels());
        System.out.println("Mat dims:" + mat.dims());
    }



    /**
     * 将 Mat图像显示在ImageView上
     * @param mat       源图像
     * @param imageView 目的ImageView
     */
    public static void showMatOnImageView(Mat mat, ImageView imageView) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mat,bitmap);
        imageView.setImageBitmap(bitmap);
    }



}
