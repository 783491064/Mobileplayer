package com.example.bjc.mobileplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import com.example.bjc.mobileplayer.R;

public class SplishActivity extends Activity {
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splish);
        jumpToMain();
    }

    /**
     * 跳转到主页面
     */
    private void jumpToMain() {
        mHandler=new Handler();
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startMainActivity();
            }
        },2000);
    }
    private boolean isStartMain = false;
    private void startMainActivity() {
        if(!isStartMain){
            isStartMain = true;
            Intent intent=new Intent(this,MainActiviy.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        mHandler.removeCallbacksAndMessages(null);
        return super.onTouchEvent(event);
    }
}
