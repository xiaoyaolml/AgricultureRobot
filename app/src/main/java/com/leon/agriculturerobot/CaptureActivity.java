package com.leon.agriculturerobot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.leon.agriculturerobot.config.Constant;
import com.leon.agriculturerobot.control.ControlSocket;
import com.leon.agriculturerobot.control.ControlSocketInstance;
import com.leon.agriculturerobot.control.FitLineProcess;
import com.leon.agriculturerobot.control.MorphologyProcess;
import com.leon.agriculturerobot.control.RecolorProcess;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class CaptureActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "CaptureActivity";
    private CameraBridgeViewBase mCameraBridgeViewBase;
    public static final int VIEW_MODE_RGBA = 0;
    public static final int VIEW_MODE_GREEN = 1;
    public static final int VIEW_MODE_THRESHOLD = 2;
    public static final int VIEW_MODE_MORPHOLOGY = 3;
    public static final int VIEW_MODE_EDGE = 4;
    public static final int VIEW_MODE_HOUGH = 5;
    public static final int VIEW_MODE_FIT_LINE = 6;
    public static int mViewMode = VIEW_MODE_RGBA;
    private Mat mGreen;
    private Mat mTemp;
    private RecolorProcess mRecolorProcess;
    private Mat mThreshold;
    private MorphologyProcess mMorphologyProcess;
    private Mat mMorphology;
    private Mat mEdge;
    private Mat mHoughLines;
    private FitLineProcess mFitLineProcess;

    private boolean mIsHide;
    private boolean mIsAuto;
    private ControlSocket mControlSocket;
    private Button mButtonForward;
    private Button mButtonBackward;
    private Button mButtonLeft;
    private Button mButtonRight;
    private Button mButtonStop;
    private Button mButtonPhoto;
    private Button mButtonDemo;
    private RadioGroup mRadioGroup;
    private SeekBar mSeekSpeed;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.d(TAG, "OpenCV loaded successfully");
                    mCameraBridgeViewBase.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_capture);

        mCameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mCameraBridgeViewBase.setCvCameraViewListener(this);
        mCameraBridgeViewBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsHide = !mIsHide;
                hideView(mIsHide);
            }
        });

        mControlSocket = ControlSocketInstance.INSTANCE.getControlSocket();

        mButtonForward = (Button) findViewById(R.id.btnForward);
        mButtonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlSocket.goForward();
            }
        });

        mButtonStop = (Button) findViewById(R.id.btnStop);
        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mControlSocket.goStop();
            }
        });

        mButtonBackward = (Button) findViewById(R.id.btnBackward);
        mButtonBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mControlSocket.goBackward();
                        break;
                    case MotionEvent.ACTION_UP:
                        mControlSocket.goStop();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mButtonLeft = (Button) findViewById(R.id.btnLeft);
        mButtonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mControlSocket.goLeft();
                        break;
                    case MotionEvent.ACTION_UP:
                        mControlSocket.goStop();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mButtonRight = (Button) findViewById(R.id.btnRight);
        mButtonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mControlSocket.goRight();
                        break;
                    case MotionEvent.ACTION_UP:
                        mControlSocket.goStop();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mButtonPhoto = (Button) findViewById(R.id.btnPhoto);
        mButtonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2016-06-03  拍照保存
                Toast.makeText(CaptureActivity.this, "拍照已保存", Toast.LENGTH_SHORT).show();
            }
        });

        mButtonDemo = (Button) findViewById(R.id.btnDemo);
        mButtonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CaptureActivity.this, DemoActivity.class));
            }
        });

        final RadioButton radioManual = (RadioButton) findViewById(R.id.rbManual);
        radioManual.setChecked(true);
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbManual:
                        mIsAuto = false;
                        mControlSocket.goStop();
                        Toast.makeText(CaptureActivity.this, "人机交互模式", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rbAuto:
                        Toast.makeText(CaptureActivity.this, "无人驾驶模式", Toast.LENGTH_SHORT).show();
                        mViewMode = VIEW_MODE_FIT_LINE;
                        mIsAuto = true;
                        hideView(true);
                        break;
                    default:
                        break;
                }
            }
        });

        mSeekSpeed = (SeekBar) findViewById(R.id.sbSpeed);
        mSeekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mControlSocket.setSpeed(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void hideView(boolean isHide) {
        if (isHide) {
            mButtonStop.setVisibility(View.INVISIBLE);
            mButtonForward.setVisibility(View.INVISIBLE);
            mButtonBackward.setVisibility(View.INVISIBLE);
            mButtonLeft.setVisibility(View.INVISIBLE);
            mButtonRight.setVisibility(View.INVISIBLE);
            mButtonPhoto.setVisibility(View.INVISIBLE);
            mButtonDemo.setVisibility(View.INVISIBLE);
            mSeekSpeed.setVisibility(View.INVISIBLE);
            mRadioGroup.setVisibility(View.INVISIBLE);
        } else {
            mButtonStop.setVisibility(View.VISIBLE);
            mButtonForward.setVisibility(View.VISIBLE);
            mButtonBackward.setVisibility(View.VISIBLE);
            mButtonLeft.setVisibility(View.VISIBLE);
            mButtonRight.setVisibility(View.VISIBLE);
            mButtonPhoto.setVisibility(View.VISIBLE);
            mButtonDemo.setVisibility(View.VISIBLE);
            mSeekSpeed.setVisibility(View.VISIBLE);
            mRadioGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraBridgeViewBase != null) {
            mCameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraBridgeViewBase != null) {
            mCameraBridgeViewBase.disableView();
        }

        try {
            mControlSocket.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_capture, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rgba:
                mViewMode = VIEW_MODE_RGBA;
                break;
            case R.id.action_green:
                mViewMode = VIEW_MODE_GREEN;
                break;
            case R.id.action_threshold:
                mViewMode = VIEW_MODE_THRESHOLD;
                break;
            case R.id.action_morphology:
                mViewMode = VIEW_MODE_MORPHOLOGY;
                break;
            case R.id.action_edge:
                mViewMode = VIEW_MODE_EDGE;
                break;
            case R.id.action_hough:
                mViewMode = VIEW_MODE_HOUGH;
                break;
            case R.id.action_fit_line:
                mViewMode = VIEW_MODE_FIT_LINE;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mGreen = new Mat();
        mTemp = new Mat();
        mRecolorProcess = new RecolorProcess();
        mThreshold = new Mat();
        mMorphology = new Mat();
        mMorphologyProcess = new MorphologyProcess();
        mEdge = new Mat();
        mHoughLines = new Mat();
        mFitLineProcess = new FitLineProcess();
    }

    @Override
    public void onCameraViewStopped() {
        if (mGreen != null) {
            mGreen.release();
            mGreen = null;
        }
        if (mTemp != null) {
            mTemp.release();
            mTemp = null;
        }
        if (mThreshold != null) {
            mThreshold.release();
            mThreshold = null;
        }
        if (mMorphology != null) {
            mMorphology.release();
            mMorphology = null;
        }
        if (mEdge != null) {
            mEdge.release();
            mEdge = null;
        }
        if (mHoughLines != null) {
            mHoughLines.release();
            mHoughLines = null;
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        int rows = rgba.rows();
        int cols = rgba.cols();
        Mat rgbaROI = rgba.submat(0, rows, cols / 4, cols * 3 / 4);

        switch (mViewMode) {
            case VIEW_MODE_RGBA:
                break;
            case VIEW_MODE_GREEN:
                mRecolorProcess.apply(rgbaROI, mTemp);
                Imgproc.cvtColor(mTemp, rgbaROI, Imgproc.COLOR_GRAY2BGRA, 4);
                break;
            case VIEW_MODE_THRESHOLD:
                mRecolorProcess.apply(rgbaROI, mGreen);
                Imgproc.threshold(mGreen, mTemp, 0, 255, Imgproc.THRESH_OTSU);
                Imgproc.cvtColor(mTemp, rgbaROI, Imgproc.COLOR_GRAY2BGRA, 4);
                break;
            case VIEW_MODE_MORPHOLOGY:
                mRecolorProcess.apply(rgbaROI, mGreen);
                Imgproc.threshold(mGreen, mThreshold, 0, 255, Imgproc.THRESH_OTSU);
                mMorphologyProcess.apply(mThreshold, mTemp);
                Imgproc.cvtColor(mTemp, rgbaROI, Imgproc.COLOR_GRAY2BGRA, 4);
                break;
            case VIEW_MODE_EDGE:
                mRecolorProcess.apply(rgbaROI, mGreen);
                Imgproc.threshold(mGreen, mThreshold, 0, 255, Imgproc.THRESH_OTSU);
                mMorphologyProcess.apply(mThreshold, mMorphology);
                Imgproc.Canny(mMorphology, mTemp, 150, 100, 3, false);
                Imgproc.cvtColor(mTemp, rgbaROI, Imgproc.COLOR_GRAY2BGRA, 4);
                break;
            case VIEW_MODE_HOUGH:
                mRecolorProcess.apply(rgbaROI, mGreen);
                Imgproc.threshold(mGreen, mThreshold, 0, 255, Imgproc.THRESH_OTSU);
                mMorphologyProcess.apply(mThreshold, mMorphology);
                Imgproc.Canny(mMorphology, mEdge, 150, 100, 3, false);
                Imgproc.HoughLinesP(mEdge, mHoughLines, 1, Math.PI / 180, 40, 0, 0);

                for (int i = 0; i < mHoughLines.rows(); i++) {
                    double[] var = mHoughLines.get(i, 0);
                    Imgproc.line(rgbaROI, new Point(var[0], var[1]), new Point(var[2], var[3]), new Scalar(255, 0, 0), 5);
                }
                break;
            case VIEW_MODE_FIT_LINE:
                // 三根辅助线
                Imgproc.line(rgbaROI, new Point(0, 0), new Point(0, rgbaROI.rows()), new Scalar(0, 255, 0), 1);
                Imgproc.line(rgbaROI, new Point(0.5 * rgbaROI.cols(), 0), new Point(0.5 * rgbaROI.cols(), rgbaROI.rows()), new Scalar(0, 255, 0), 1);
                Imgproc.line(rgbaROI, new Point(rgbaROI.cols(), 0), new Point(rgbaROI.cols(), rgbaROI.rows()), new Scalar(0, 255, 0), 2);

                mRecolorProcess.apply(rgbaROI, mGreen);
                Imgproc.threshold(mGreen, mThreshold, 0, 255, Imgproc.THRESH_OTSU);
                mMorphologyProcess.apply(mThreshold, mMorphology);
                Imgproc.Canny(mMorphology, mEdge, 150, 100, 3, false);
                Imgproc.HoughLinesP(mEdge, mHoughLines, 1, Math.PI / 180, 30, 0, 0);
                mFitLineProcess.apply(mHoughLines, rgbaROI);
                break;
        }


        if (mIsAuto) {
            int result = mFitLineProcess.translate();
             /* 无人驾驶模式下的自动控制部分 */
            switch (result) {
                case Constant.GO_FORWARD_CODE:
                    mControlSocket.goForward();
                    break;
                case Constant.GO_BACKWARD_CODE:
                    mControlSocket.goBackward();
                    break;
                case Constant.GO_LEFT_CODE:
                    mControlSocket.goLeft();
                    break;
                case Constant.GO_RIGHT_CODE:
                    mControlSocket.goRight();
                    break;
                case Constant.GO_STOP_CODE:
                    mControlSocket.goStop();
                    break;
                default:
                    mControlSocket.goStop();
                    break;
            }
        }
//        System.gc();
        return rgba;
    }

    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2500) {
                Toast.makeText(getApplicationContext(), "再按一次退出" + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
