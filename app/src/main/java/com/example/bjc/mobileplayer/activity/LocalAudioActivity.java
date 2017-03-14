package com.example.bjc.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bjc.mobileplayer.IMyAidlInterface;
import com.example.bjc.mobileplayer.R;
import com.example.bjc.mobileplayer.service.AidlAudioPlayerService;
import com.example.bjc.mobileplayer.utils.CacheUtil;
import com.example.bjc.mobileplayer.utils.Utils;
import com.example.bjc.mobileplayer.view.SyncLyricsView;

/**
 * Created by 毕静存 on 2017/1/3.
 */

public class LocalAudioActivity extends Activity implements View.OnClickListener {
    private ImageView ivIcon;
    private TextView tvAthor, tv_name;
    private TextView tvTime;
    private SeekBar sbBottom;
    private ImageView btnMoudle;
    private ImageView btnAudioPre;
    private ImageView btnAudioStartPause;
    private ImageView btnAudioNext;
    private ImageView btnAudioLyrc;
    private int position;
    private IMyAidlInterface service;
    private Intent intent;
    private int durition;
    private int currentPosition;
    private AudioReceiver receiver;
    private static final int PROCESS = 1;
    private static final int HIDE = 2;
    private static final int SYNC_LYRIC = 3;
    private Utils utils;
    private boolean notification;
    private int playModule = 1;
    private SyncLyricsView sy_view;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder ibinder) {
            service = IMyAidlInterface.Stub.asInterface(ibinder);
            if (service != null) {
                try {
                    Log.e("AudioActivity.class", "onServiceConnected: " + "绑定服务成功了");
                    if (!notification) {
                        service.openAudioPlayer(position);
                    } else {
                        showData();
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (service != null) {
                try {
                    service.stop();
                    service = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            Log.e("AudioActivity.class", "onServiceConnected: " + "绑定服务失败了");
        }
    };

    private void findViews() {
        setContentView(R.layout.activity_audio);

        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        ivIcon.setImageResource(R.drawable.ivicon_bg);
        AnimationDrawable drawable = (AnimationDrawable) ivIcon.getDrawable();
        drawable.start();

        tvAthor = (TextView) findViewById(R.id.tv_athor);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        sbBottom = (SeekBar) findViewById(R.id.sb_bottom);
        btnMoudle = (ImageView) findViewById(R.id.btn_moudle);
        btnAudioPre = (ImageView) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (ImageView) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (ImageView) findViewById(R.id.btn_audio_next);
        btnAudioLyrc = (ImageView) findViewById(R.id.btn_audio_lyrc);
        sy_view = (SyncLyricsView) findViewById(R.id.sy_view);

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //1进度更新
            switch (msg.what) {
                case PROCESS:
                    try {
                        currentPosition = service.getcurrentPosition();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(durition));
                    sbBottom.setProgress(currentPosition);
                    removeMessages(PROCESS);
                    sendEmptyMessageDelayed(PROCESS, 1000);
                    break;
                case SYNC_LYRIC:

                    try {
                        //得到当前的播放进度
                        currentPosition = service.getcurrentPosition();
                        //将播放的进度传递给歌词VIEW
                        sy_view.setNextLyric(currentPosition);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }


                    handler.removeMessages(SYNC_LYRIC);
                    handler.sendEmptyMessage(SYNC_LYRIC);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setWindowAnimations(R.style.mystyle);
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(R.style.mystyle);
        findViews();
        initData();
        bindAndStartService();
        setListener();
    }

    /**
     * 控制音乐播放的控制按钮点击事件
     */
    private void setListener() {
        btnMoudle.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnAudioLyrc.setOnClickListener(this);
        sbBottom.setOnSeekBarChangeListener(new MySeekBarChangeListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_moudle:
                showModle();
                break;
            case R.id.btn_audio_pre://上一个
                if (service != null) {
                    try {
                        service.pre();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.btn_audio_start_pause://暂停和继续
                if (service != null) {
                    try {
                        if (service.isPlaying()) {
                            service.pause();
                            btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                        } else {
                            service.start();
                            btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case R.id.btn_audio_next://下一个
                if (service != null) {
                    try {
                        service.next();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }

                break;
            case R.id.btn_audio_lyrc:
                break;
        }
    }

    /**
     * 切换播放模式
     */
    private void showModle() {
        try {
            playModule = service.getPlayMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (playModule == AidlAudioPlayerService.NORMAL_MODLE) {
            playModule = AidlAudioPlayerService.SINGLE_MODLE;
            Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
            btnMoudle.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);

        } else if (playModule == AidlAudioPlayerService.SINGLE_MODLE) {
            playModule = AidlAudioPlayerService.ALL_MODLE;
            Toast.makeText(this, "全部循环", Toast.LENGTH_SHORT).show();
            btnMoudle.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
        } else if (playModule == AidlAudioPlayerService.ALL_MODLE) {
            playModule = AidlAudioPlayerService.NORMAL_MODLE;
            Toast.makeText(this, "默认模式", Toast.LENGTH_SHORT).show();
            btnMoudle.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
        } else {
            playModule = AidlAudioPlayerService.NORMAL_MODLE;
            Toast.makeText(this, "默认模式", Toast.LENGTH_SHORT).show();
            btnMoudle.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
        }

        try {
            service.setPlayMode(playModule);//服务中 播放模式
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 綁定和开启服务
     */
    private void bindAndStartService() {
        intent = new Intent(LocalAudioActivity.this, AidlAudioPlayerService.class);
        intent.setAction("com.example.bjc.mobileplayer.service.AidlAudioPlayerService_ACTION");
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 获取播放的数据；
     */
    private void initData() {
        utils = new Utils();
        notification = getIntent().getBooleanExtra("Notification", false);//从状态栏点击进入的；
        if (!notification) {//不是点击状态栏进入界面的情况
            position = getIntent().getIntExtra("position", 0);//获取传递过来的的播放音频的条目位置；
        }
        regist();
    }

    /**
     * 检查播放模式 并显示
     */
    private void checkModle() {
        playModule = CacheUtil.getPlayModule(this, "modle");
        if (playModule == AidlAudioPlayerService.NORMAL_MODLE) {
            btnMoudle.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);

        } else if (playModule == AidlAudioPlayerService.SINGLE_MODLE) {
            btnMoudle.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);
        } else if (playModule == AidlAudioPlayerService.ALL_MODLE) {
            btnMoudle.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);
        } else {
            btnMoudle.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
        }

        try {
            service.setPlayMode(playModule);//服务中 播放模式

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册接受者
     */
    private void regist() {
        receiver = new AudioReceiver();
        IntentFilter intentFillter = new IntentFilter();
        intentFillter.addAction(AidlAudioPlayerService.ACTION);
        registerReceiver(receiver, intentFillter);
    }

    /**
     * 广播接受者
     */
    class AudioReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showData();
        }
    }

    /**
     * 展示数据
     */
    private void showData() {
        //发消息同步歌词
        handler.sendEmptyMessage(SYNC_LYRIC);
        try {
            checkModle();
            tvAthor.setText(service.getAuthor());
            tv_name.setText(service.getName());
            durition = (int) service.getDurition();
            sbBottom.setMax(durition);
            handler.sendEmptyMessage(PROCESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    private class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
