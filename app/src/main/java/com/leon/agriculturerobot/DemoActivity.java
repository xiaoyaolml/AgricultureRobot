package com.leon.agriculturerobot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.leon.agriculturerobot.control.FitLineProcess;
import com.leon.agriculturerobot.control.MorphologyProcess;
import com.leon.agriculturerobot.control.RecolorProcess;
import com.leon.agriculturerobot.utils.MatUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class DemoActivity extends AppCompatActivity {

    private Button mButtonOrigin;
    private Button mButtonGray;
    private Button mButtonThreshold;
    private Button mButtonMorphology;
    private Button mButtonCanny;
    private Button mButtonHough;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Mat mSrc;
    private Mat mGray;
    private Mat mThreshold;
    private Mat mMorphology;
    private Mat mEdge;

    private RecolorProcess mRecolorProcess;
    private MorphologyProcess mMorphologyProcess;
    private FitLineProcess mFitLineProcess;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mSrc = new Mat();
                    mGray = new Mat();
                    mRecolorProcess = new RecolorProcess();
                    mThreshold = new Mat();
                    mMorphology = new Mat();
                    mMorphologyProcess = new MorphologyProcess();
                    mEdge = new Mat();
                    mFitLineProcess = new FitLineProcess();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);


        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seedling15);
        mImageView = (ImageView) findViewById(R.id.ivSeedling);

        mButtonOrigin = (Button) findViewById(R.id.btnOrigin);
        mButtonOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImageView.setImageBitmap(mBitmap);
            }
        });

        mButtonGray = (Button) findViewById(R.id.btnGray);
        mButtonGray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.bitmapToMat(mBitmap,mSrc);
                /** 过绿变换灰度化，处理速度比较慢
                 Mat mat = new Mat();
                 mat.create(src.rows(),src.cols(), CvType.CV_8UC1);
                 for (int i = 0; i < src.rows(); i++) {
                    for (int j = 0; j < src.cols(); j++) {
                        double rgb[] = src.get(i,j);
                        double r = rgb[0];
                        double g = rgb[1];
                        double b = rgb[2];
                        double gray = 2*g-b-r;
                        if (gray<0){
                            gray=0;
                        } else if (gray>255) {
                            gray=255;
                        }
                        mat.put(i,j,gray);
                    }
                 }
                 */
                mRecolorProcess.apply(mSrc,mGray);
                MatUtil.showMatOnImageView(mGray, mImageView);
            }
        });

        mButtonThreshold = (Button) findViewById(R.id.btnThreshold);
        mButtonThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.bitmapToMat(mBitmap,mSrc);
                mRecolorProcess.apply(mSrc,mGray);
                Imgproc.threshold(mGray,mThreshold,0,255,Imgproc.THRESH_OTSU);
                MatUtil.showMatOnImageView(mThreshold, mImageView);
            }
        });

        mButtonMorphology = (Button) findViewById(R.id.btnMorphology);
        mButtonMorphology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.bitmapToMat(mBitmap,mSrc);
                mRecolorProcess.apply(mSrc,mGray);
                Imgproc.threshold(mGray,mThreshold,0,255,Imgproc.THRESH_OTSU);
                mMorphologyProcess.apply(mThreshold,mMorphology);
                MatUtil.showMatOnImageView(mMorphology, mImageView);
            }
        });

        mButtonCanny = (Button) findViewById(R.id.btnCanny);
        mButtonCanny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.bitmapToMat(mBitmap,mSrc);
                mRecolorProcess.apply(mSrc,mGray);
                Imgproc.threshold(mGray,mThreshold,0,255,Imgproc.THRESH_OTSU);
                mMorphologyProcess.apply(mThreshold,mMorphology);
                Imgproc.Canny(mMorphology,mEdge,150,100,3,false);
                MatUtil.showMatOnImageView(mEdge, mImageView);
            }
        });

        mButtonHough = (Button) findViewById(R.id.btnHough);
        mButtonHough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.bitmapToMat(mBitmap,mSrc);
                mRecolorProcess.apply(mSrc,mGray);
                Imgproc.threshold(mGray,mThreshold,0,255,Imgproc.THRESH_OTSU);
                mMorphologyProcess.apply(mThreshold,mMorphology);

                Imgproc.Canny(mMorphology,mEdge,150,100,3,false);

                /**
                 * 累计概率霍夫变换PPHT：采用PPHT找出二值图像中的直线
                 * HoughLinesP(Mat image, Mat lines, double rho, double theta, int threshold, double minLineLength, double maxLineGap)
                 *  参数一：image
                 *      输入图像，8位单通道二进制图像
                 *  参数二：line
                 *      检测出的线条结果：
                 *          2D拟合情况下，(line[0],line[1])和(line[2],line[3])分别为线段上的起始点和结束点
                 *  参数三：rho
                 *      以像素为单位的距离精度，为1
                 *  参数四：theta
                 *      以弧度为单位的角度精度，为PI/180
                 *  参数五：threshold
                 *      累加平面的阈值参数
                 *  参数六：minLineLength
                 *      最低线段的长度，默认为0，低于该长度的线段舍弃
                 *  参数七：maxLineGap
                 *      允许同一行点与点连接的最大距离，默认为0
                 */
                Mat lines = new Mat();
                Imgproc.HoughLinesP(mEdge,lines,1,Math.PI/180,70,0,0);

                Mat bgr = new Mat();
                Imgproc.cvtColor(mEdge,bgr,Imgproc.COLOR_GRAY2BGR);
                Imgproc.line(bgr,new Point(bgr.cols()/2,0),new Point(bgr.cols()/2,bgr.rows()),new Scalar(0,255,0));

//                mFitLineProcess.apply(lines,bgr);

                Imgproc.line(bgr,new Point(bgr.cols()/2,0),new Point(bgr.cols()/2,bgr.rows()),new Scalar(0,255,0));

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
//                    Imgproc.line(bgr, point1, point2, new Scalar(255,0,0),5);

                }

                Imgproc.line(bgr, point3, point4, new Scalar(0,0,255),10);

                MatUtil.showMatOnImageView(bgr, mImageView);
            }
        });



    }
}
