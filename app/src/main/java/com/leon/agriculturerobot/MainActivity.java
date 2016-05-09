package com.leon.agriculturerobot;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.leon.agriculturerobot.control.RemoteControl;
import com.leon.agriculturerobot.control.RemoteControlInstance;
import com.leon.agriculturerobot.setting.SettingFragment;

public class MainActivity extends AppCompatActivity {

    private Button mButtonMain;
    private RemoteControl mRemoteControl;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.layoutSetting, new SettingFragment(), "SETTING_FRAGMENT");
        fragmentTransaction.commit();

        mRemoteControl = new RemoteControl();

        mButtonMain = (Button) findViewById(R.id.btnMain);
        mButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("com.leon.agriculturerobot_preferences", MODE_PRIVATE);
                String ip = sharedPreferences.getString("IP", "");
                String port = sharedPreferences.getString("PORT", "");

                new AsyncTask<String, Void, Boolean>() {
                    ProgressDialog progressDialog;
                    @Override
                    protected Boolean doInBackground(String... params) {
                        return mRemoteControl.init(params[0],params[1]);
                    }

                    @Override
                    protected void onPreExecute() {
                        progressDialog = ProgressDialog.show(MainActivity.this, "请稍等", "机器人系统正在启动...");
                    }

                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        progressDialog.dismiss();
                        if (aBoolean) {
                            RemoteControlInstance.INSTANCE.setRemoteControl(mRemoteControl);
                            startActivity(new Intent(MainActivity.this, CaptureActivity.class));
                            finish();
                        } else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("抱歉！")
                                    .setMessage("机器人系统启动失败")
                                    .setPositiveButton("确定", null)
                                    .show();
                            mButtonMain.setText("重新启动");
                        }
                    }
                }.execute(ip, port);

            }
        });

//        mLocalControl = new LocalControl();
//        boolean isSuccess = mLocalControl.init();
//        if (isSuccess) {
//            Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
//            return;
//        }
    }

    public void startWeb(View view) {
        startActivity(new Intent(this,WebActivity.class));
    }

}
