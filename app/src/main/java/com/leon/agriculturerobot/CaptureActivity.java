package com.leon.agriculturerobot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.leon.agriculturerobot.control.ControlSocket;
import com.leon.agriculturerobot.control.ControlSocketInstance;
import com.leon.agriculturerobot.control.FitLineProcess;
import com.leon.agriculturerobot.control.ImageProcess;
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

public class CaptureActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "CaptureActivity";
    private CameraBridgeViewBase mCameraBridgeViewBase;
    private boolean mIsHide;
    private boolean mIsAuto;
//    private RemoteControl mRemoteControl;
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
        setContentView(R.layout.activity_capture);

        mCameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.cameraView);
        mCameraBridgeViewBase.setCvCameraViewListener(this);
        mCameraBridgeViewBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsHide = ! mIsHide;
                hideView(mIsHide);
            }
        });

//        mRemoteControl = RemoteControlInstance.INSTANCE.getRemoteControl();
        mControlSocket = ControlSocketInstance.INSTANCE.getControlSocket();

        mButtonForward = (Button) findViewById(R.id.btnForward);
        mButtonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRemoteControl.goForward();
                mControlSocket.goForward();
            }
        });

        mButtonStop = (Button) findViewById(R.id.btnStop);
        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mRemoteControl.goStop();
                mControlSocket.goStop();
            }
        });

        mButtonBackward = (Button) findViewById(R.id.btnBackward);
        mButtonBackward.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        mRemoteControl.goBackward();
                        mControlSocket.goBackward();
                        break;
                    case MotionEvent.ACTION_UP:
//                        mRemoteControl.goStop();
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
//                        mRemoteControl.goLeft();
                        mControlSocket.goLeft();
                        break;
                    case MotionEvent.ACTION_UP:
//                        mRemoteControl.goStop();
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
//                        mRemoteControl.goRight();
                        mControlSocket.goRight();
                        break;
                    case MotionEvent.ACTION_UP:
//                        mRemoteControl.goStop();
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
                Toast.makeText(CaptureActivity.this, "拍照已保存", Toast.LENGTH_SHORT).show();
            }
        });

        mButtonDemo = (Button) findViewById(R.id.btnDemo);
        mButtonDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CaptureActivity.this,DemoActivity.class));
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
//                        mRemoteControl.goStop();
                        mControlSocket.goStop();
                        Toast.makeText(CaptureActivity.this, "人机交互模式", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rbAuto:
                        Toast.makeText(CaptureActivity.this, "无人驾驶模式", Toast.LENGTH_SHORT).show();
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
//                mRemoteControl.setSpeed(progress);
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
//            mRemoteControl.getSocket().close();
            mControlSocket.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        final Mat gray = new Mat();
        ImageProcess imageProcess = new ImageProcess();
        int result = imageProcess.apply(inputFrame.gray(),gray);

        RecolorProcess recolorProcess = new RecolorProcess();
        recolorProcess.apply(inputFrame.rgba(),gray);

        final Mat threshold = new Mat();
        Imgproc.threshold(gray,threshold,0,255,Imgproc.THRESH_OTSU);

        final Mat morphology = new Mat();
        MorphologyProcess morphologyProcess = new MorphologyProcess();
        morphologyProcess.apply(threshold,morphology);

        final Mat edge = new Mat();
        Imgproc.Canny(morphology,edge,150,100,3,false);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(edge,lines,1,Math.PI/180,70,0,0);

//        Mat bgr = new Mat();
//        Imgproc.cvtColor(edge,bgr,Imgproc.COLOR_GRAY2BGR);
        Mat bgr = inputFrame.rgba();
        Imgproc.line(bgr,new Point(bgr.cols()/2,0),new Point(bgr.cols()/2,bgr.rows()),new Scalar(0,255,0));

        FitLineProcess fitLineProcess = new FitLineProcess();
        fitLineProcess.apply(lines,bgr);

        if (mIsAuto) {
             /* 无人驾驶模式下的自动控制部分 */
//        switch (result) {
//            case Constant.GO_FORWARD_CODE:
//                mRemoteControl.goForward();
//                break;
//            case Constant.GO_BACKWARD_CODE:
//                mRemoteControl.goBackward();
//                break;
//            case Constant.GO_LEFT_CODE:
//                mRemoteControl.goLeft();
//                break;
//            case Constant.GO_RIGHT_CODE:
//                mRemoteControl.goRight();
//                break;
//            case Constant.GO_STOP_CODE:
//                mRemoteControl.goStop();
//                break;
//            default:
//                mRemoteControl.goStop();
//                break;
//        }
//            mRemoteControl.goRight();
            mControlSocket.goRight();
        }
        System.gc();
        return bgr;
    }

   private long mExitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2500) {
                Toast.makeText(getApplicationContext(), "再按一次退出"+getString(R.string.app_name), Toast.LENGTH_SHORT).show();
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
