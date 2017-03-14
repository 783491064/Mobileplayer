package com.example.bjc.mobileplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bjc.mobileplayer.R;
import com.example.bjc.mobileplayer.bean.MediaItem;
import com.example.bjc.mobileplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by 毕静存 on 2016/12/30.
 */

public class VitamioVideoActivity extends Activity implements View.OnClickListener {


    private static final int PROCESS = 1;
    private static final int HIDE = 2;
    private static final int FUULSCREEN = 3;
    private static final int DEFULTSCREEN = 4;
    private static final int SHOW_NET_SPEED = 5;
    private int screenSize = 4;
    private VideoView videoview;
    private Uri uri;
    private TextView videoName;
    private ImageView ivBettry;
    private TextView tvTime;
    private Button btVoice;
    private SeekBar sbTop;
    private Button btnSwitch;
    private TextView tvCurrenttime;
    private SeekBar sbBottom;
    private TextView tvTotal;
    private ImageView btnExit;
    private ImageView btnVideoPre;
    private ImageView btnVideoStartPause;
    private ImageView btnVideoNext;
    private ImageView btnVideoSiwchScreen;
    private Utils utils;
    private int videoWidth;
    private int videoHeight;
    private int preProsition;
    private boolean isNo;
    private boolean isSystem = false;
    private String netSpeed;
    private ArrayList<MediaItem> videoList;
    private BetteryReceiver receiver;
    private int position;
    private GestureDetector detector;
    private RelativeLayout contraller;
    private boolean isFullScreen = false;
    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private float startY = -1;
    private float endY;
    private float distanceY;
    private int width;
    private int height;
    private int touchRang;
    private int mVol;
    private boolean result = false;
    private LinearLayout ll_center;
    private LinearLayout ll_loading;
    private TextView tv_ka;
    private TextView tv_loading;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NET_SPEED:
                    netSpeed = utils.getNetSpeed(VitamioVideoActivity.this);
                    tv_ka.setText("缓冲中..." + netSpeed);
                    tv_loading.setText("玩命加载中..." + netSpeed);
                    handler.removeMessages(SHOW_NET_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_NET_SPEED, 2000);
                    break;
                case PROCESS:
                    //更新当前时间
                    long currentPosition = videoview.getCurrentPosition();
                    tvCurrenttime.setText(utils.stringForTime((int) currentPosition));
                    //更新播放的进度
                    sbBottom.setProgress((int) currentPosition);
                    //更新缓冲进度
                    if (result) {
                        int buffer = videoview.getBufferPercentage();
                        int percent = buffer * sbBottom.getMax() / 100;
                        sbBottom.setSecondaryProgress(percent);
                    } else {
                        sbBottom.setSecondaryProgress(0);
                    }
                    //用播放进度交验卡顿
                    if (!isSystem && videoview.isPlaying()) {
                        if (videoview.isPlaying()) {
                            int buffer = (int) (currentPosition - preProsition);
                            if (buffer < 500) {
                                if (result) {//是网络视频资源的时候才显示网速；
                                    ll_center.setVisibility(View.VISIBLE);
                                }
                            } else {
                                ll_center.setVisibility(View.GONE);
                            }
                        } else {
                            ll_center.setVisibility(View.GONE);
                        }
                    }

                    preProsition = (int) currentPosition;
                    //上一个消息移除
                    removeMessages(PROCESS);
                    // 更新播放页面的系统的时间；
                    setSystemTime();
                    //停一秒发送一个消息;
                    sendEmptyMessageDelayed(PROCESS, 1000);
                    break;
                case HIDE:
                    hideContraller();
                    removeMessages(HIDE);
                    break;
            }
        }
    };

    /**
     * 设置系统的时间
     */
    private void setSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        tvTime.setText(format.format(new Date()));
    }

    private void findViews() {
        contraller = (RelativeLayout) findViewById(R.id.contraller);
        videoview = (VideoView) findViewById(R.id.videoview);
        videoName = (TextView) findViewById(R.id.video_name);
        ivBettry = (ImageView) findViewById(R.id.iv_bettry);
        tvTime = (TextView) findViewById(R.id.tv_time);
        btVoice = (Button) findViewById(R.id.bt_voice);
        sbTop = (SeekBar) findViewById(R.id.sb_top);
        btnSwitch = (Button) findViewById(R.id.btn_switch);
        tvCurrenttime = (TextView) findViewById(R.id.tv_currenttime);
        sbBottom = (SeekBar) findViewById(R.id.sb_bottom);
        tvTotal = (TextView) findViewById(R.id.tv_total);
        btnExit = (ImageView) findViewById(R.id.btn_exit);
        btnVideoPre = (ImageView) findViewById(R.id.btn_video_pre);
        btnVideoStartPause = (ImageView) findViewById(R.id.btn_video_start_pause);
        btnVideoNext = (ImageView) findViewById(R.id.btn_video_next);
        btnVideoSiwchScreen = (ImageView) findViewById(R.id.btn_video_siwch_screen);
        ll_center = (LinearLayout) findViewById(R.id.pb_center);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_ka = (TextView) findViewById(R.id.tv_ka);
        tv_loading = (TextView) findViewById(R.id.tv_loading);


        btVoice.setOnClickListener(this);
        btnSwitch.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoStartPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSiwchScreen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btVoice) {//声音按钮

            if (!isNo) {
                isNo = true;//静音
                upVolume(0, isNo);
            } else {
                isNo = false;
                upVolume(currentVolume, isNo);
            }


        } else if (v == btnSwitch) {//切换视频按钮
            showChangeDialog();
        } else if (v == btnExit) { //退出
            finish();
        } else if (v == btnVideoPre) {//上一个按钮
            playPre();
        } else if (v == btnVideoStartPause) {//开始与暂停按钮
            playPauseAndStart();
        } else if (v == btnVideoNext) {//下一个按钮
            playNext();
        } else if (v == btnVideoSiwchScreen) {//改变屏幕大小
            if (!isFullScreen) {
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_default_screen_normal);
                isFullScreen = true;
                screenSize = FUULSCREEN;
            } else {
                btnVideoSiwchScreen.setBackgroundResource(R.drawable.btn_full_screen_normal);
                isFullScreen = false;
                screenSize = DEFULTSCREEN;
            }
            changeSize(screenSize);
        }
        handler.removeMessages(HIDE);
        handler.sendEmptyMessageDelayed(HIDE, 4000);
    }

    /**
     * 手动切换到系统播放器
     */
    private void showChangeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("切换到系统播放器");
        builder.setMessage("万能播放器有问题");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemVideo();
                finish();
            }
        });
        builder.show();
    }

    /**
     * 改变屏幕的大小；
     */
    private void changeSize(int screenSize) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        //屏幕的宽
        width = displayMetrics.widthPixels;
        //屏幕的高
        height = displayMetrics.heightPixels;


        switch (screenSize) {
            case FUULSCREEN:
                videoview.setVideoSize(width, height);
                break;
            case DEFULTSCREEN:
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                videoview.setVideoSize(width, height);
                break;
        }

    }

    /**
     * 播放开始和暂停的控制
     */
    private void playPauseAndStart() {
        if (videoview.isPlaying()) {
            videoview.pause();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_play_normal_selector);
        } else {
            videoview.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_pause_normal_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPre() {
        if (videoList != null && videoList.size() > 0) {
            position--;
            if (position >= 0) {
                MediaItem mediaItem = videoList.get(position);//播放下一个的视频的数据；
                videoName.setText(mediaItem.getName());
                videoview.setVideoURI(Uri.parse(mediaItem.getData()));
                //设置下一个按钮的状态；
                setButtonState();
            } else if (uri != null) {
                netSpeed = utils.getNetSpeed(VitamioVideoActivity.this);
                //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
                setButtonState();
                result = utils.isNetVideoUri(uri.toString());
            }
        }
    }

    /**
     * 播放下一个视频的处理；
     */
    private void playNext() {
        if (videoList != null && videoList.size() > 0) {

            if (position < videoList.size()) {
                position++;
                MediaItem mediaItem = videoList.get(position);//播放下一个的视频的数据；
                videoName.setText(mediaItem.getName());
                videoview.setVideoURI(Uri.parse(mediaItem.getData()));
                //设置下一个按钮的状态；
                setButtonState();
            } else if (uri != null) {
                netSpeed = utils.getNetSpeed(VitamioVideoActivity.this);
                //设置按钮状态-上一个和下一个按钮设置灰色并且不可以点击
                result = utils.isNetVideoUri(uri.toString());
                setButtonState();
            }
        }
    }

    /**
     * 设置按钮的状态；
     */
    private void setButtonState() {
        if (videoList != null) {
            if (videoList.size() == 1) {
                //如果集合中就一个视频，两个按钮全部变灰不可用
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnVideoPre.setEnabled(false);

                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnVideoNext.setEnabled(false);
            } else {
                //集合中有多个视频
                if (videoList.size() == 2) {
                    if (position == 0) {
                        btnVideoPre.setEnabled(false);
                        btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    } else if (position == videoList.size() - 1) {
                        btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                        btnVideoNext.setEnabled(true);

                        btnVideoPre.setBackgroundResource(R.drawable.btn_audio_pre_selector);
                        btnVideoPre.setEnabled(false);
                    }
                } else {

                    if (position == 0) {
                        btnVideoPre.setEnabled(false);
                        btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    } else if (position == videoList.size() - 1) {
                        btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                        btnVideoNext.setEnabled(false);
                    } else {
                        btnVideoPre.setBackgroundResource(R.drawable.btn_audio_pre_selector);
                        btnVideoPre.setEnabled(true);

                        btnVideoNext.setBackgroundResource(R.drawable.btn_audio_next_selector);
                        btnVideoNext.setEnabled(true);
                    }
                }

            }
        } else {
            btnVideoPre.setEnabled(false);
            btnVideoNext.setEnabled(false);
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
        }


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vitamio_video);
        Vitamio.isInitialized(this);
        utils = new Utils();
        initView();
        initData();
    }

    private void initView() {
        findViews();
        detector = new GestureDetector(this, new MyOnGestureListener());//手势识别器
        handler.sendEmptyMessage(SHOW_NET_SPEED);
    }

    private void initData() {
        initPlayer();
        setButtonState();
        setListener();
        registerBettry();
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        //得到系统的最大音量
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //得到系统的当前音量
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sbTop.setMax(maxVolume);//进度条设置最大进度；
        sbTop.setProgress(currentVolume);
    }

    /**
     * 注册电池电量的接受者；
     */
    private void registerBettry() {
        receiver = new BetteryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 电量的广播
     */
    class BetteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            setLevel(level);
        }
    }

    /**
     * 设置电量的变化；
     *
     * @param level
     */
    private void setLevel(int level) {
        if (level <= 0) {
            ivBettry.setBackgroundResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBettry.setBackgroundResource(R.drawable.ic_battery_10);
        } else if (level <= 20) {
            ivBettry.setBackgroundResource(R.drawable.ic_battery_20);
        } else if (level <= 40) {
            ivBettry.setBackgroundResource(R.drawable.ic_battery_40);
        } else if (level <= 60) {
            ivBettry.setBackgroundResource(R.drawable.ic_battery_60);
        } else if (level <= 80) {
            ivBettry.setBackgroundResource(R.drawable.ic_battery_80);
        } else if (level <= 100) {
            ivBettry.setBackgroundResource(R.drawable.ic_battery_100);
        } else {
            ivBettry.setBackgroundResource(R.drawable.ic_battery_100);
        }

    }

    /**
     * 初始化播放器;
     */
    private void initPlayer() {
        uri = getIntent().getData();
        videoList = (ArrayList<MediaItem>) getIntent().getSerializableExtra("medias");//传递过来视频的集合
        //传递过来点击视频的播放位置；
        position = getIntent().getIntExtra("position", 0);
        if (videoList != null && videoList.size() > 0) {
            MediaItem mediaItem = videoList.get(position);
            String uriString = mediaItem.getData();//播放地址
            Uri uri = Uri.parse(uriString);
            videoview.setVideoURI(uri);//已经异步准备好了;
            videoName.setText(mediaItem.getName());
        } else if (uri != null) {//其他界面传递的播放视频的绝对地址; 例如在图库文件中要打开视频播放的软件，可以将我们开发的视频软件给客户展示供选择；
            netSpeed = utils.getNetSpeed(VitamioVideoActivity.this);
            result = utils.isNetVideoUri(uri.toString());
            videoview.setVideoURI(uri);
            videoName.setText(uri.toString());

        }

        handler.sendEmptyMessageDelayed(HIDE, 4000);
    }

    /**
     * 给播放器设置监听器;
     */
    private void setListener() {
        videoview.setOnPreparedListener(new MyOnPreparedListener());//准备的监听
        videoview.setOnErrorListener(new MyOnErrorListener());//播放错误的监听
        videoview.setOnCompletionListener(new MyOnCompletionListener());//播放完成的监听
        sbBottom.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());//设置进度条拖拽的监听
        sbTop.setOnSeekBarChangeListener(new MyTopOnSeekBarChangeListener());
        if (isSystem) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {//因为视频播放卡的监听是在2.3后添加到mediaplayer中的，4.2.2后添加到videoView中的；
                videoview.setOnInfoListener(new MyInfoListener());//播放网络视频的卡的监听；
            }
        }

    }

    /**
     * 网络视频监听卡
     */
    class MyInfoListener implements io.vov.vitamio.MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    ll_center.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    ll_center.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    /**
     * 播放器的准备监听
     */
    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        private long duration;

        @Override
        public void onPrepared(MediaPlayer mp) {
            Toast.makeText(VitamioVideoActivity.this, "播放准备完成了", Toast.LENGTH_SHORT).show();
            duration = videoview.getDuration();//视频的时长
            sbBottom.setMax((int) duration);
            tvTotal.setText(utils.stringForTime((int) duration));//设置视频的总时长
            handler.sendEmptyMessage(PROCESS);
            videoview.start();
            ll_loading.setVisibility(View.GONE);
            videoWidth = mp.getVideoWidth();
            videoHeight = mp.getVideoHeight();

            changeSize(screenSize);

        }
    }

    /**
     * 播放错误的监听;
     */
    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            netSpeed = "0 kb/s";
            handler.removeMessages(SHOW_NET_SPEED);
            //如果万能播放器播放错误，就弹出播放错误的弹框；
            showErroryDialog();
            return true;
        }
    }

    /**
     * 万能播放器不能播放视频的情况下弹出错误对话框；
     */
    private void showErroryDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("万能播放器发生错误提示");
        builder.setMessage("万能播放器发生错误");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    /**
     * 切换到万能播放器；
     */
    private void startSystemVideo() {
        if(videoview!=null){
            videoview.stopPlayback();
        }
        Intent intent = new Intent(this, SystemVideoActivity.class);
        Bundle bundle = new Bundle();
        if(videoList!=null&&videoList.size()>0){
            bundle.putSerializable("medias", videoList);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
        }else{
            intent.setData(uri);
        }
        startActivity(intent);
        finish();

    }

    /**
     * 播放完成的监听;
     */
    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (videoList != null && position != (videoList.size() - 1)) {
                playNext();
            } else {
                btnVideoStartPause.setBackgroundResource(R.drawable.btn_play_normal_selector);
            }

        }
    }

    /**
     * 进度条拖拽的监听
     */
    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoview.seekTo(progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE, 4000);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    boolean isShowContraller = true;

    /**
     * 手势识别器实现类
     */
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isShowContraller) {
                hideContraller();
            } else {
                showContraller();
                handler.sendEmptyMessageDelayed(HIDE, 4000);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Toast.makeText(VitamioVideoActivity.this, "我被双击了", Toast.LENGTH_SHORT).show();
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            playPauseAndStart();
        }
    }

    /**
     * 隐藏控制面板
     */
    private void hideContraller() {
        contraller.setVisibility(View.GONE);
        isShowContraller = false;
    }

    /**
     * 显示控制面板；
     */
    private void showContraller() {
        contraller.setVisibility(View.VISIBLE);
        isShowContraller = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);//手势识别器接受手势进行处理；
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();//点击时的Y轴距离；
                //系统当前的音量
                mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                //screenHeight
                touchRang = Math.min(height, width);
                break;
            case MotionEvent.ACTION_MOVE:
                endY = event.getY();//移动到的Y轴距离；
                distanceY = endY - startY;//移动的距离；
                int delta = (int) ((distanceY / touchRang) * maxVolume);// 滑动时应该改变的音量值；
                ;//改变后的声音应该是原来的声音加上滑动改变的声音；change
                int volume = Math.min(Math.max(delta + mVol, 0), maxVolume);
                if (delta != 0) {
                    upVolume(volume, false);
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE, 4000);
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 声音的进度条的监听
     */
    private class MyTopOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                isNo = false;
                upVolume(progress, isNo);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE, 4000);
        }
    }

    /**
     * 更新音量；
     */
    private void upVolume(int progress, boolean isNo) {
        if (isNo) {
            sbTop.setProgress(0);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        } else {
            currentVolume = progress;
            sbTop.setProgress(currentVolume);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!isShowContraller) {
            showContraller();
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVolume--;
            upVolume(currentVolume, false);

            handler.removeMessages(HIDE);
            handler.sendEmptyMessageDelayed(HIDE, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume++;
            upVolume(currentVolume, false);
            handler.removeMessages(HIDE);
            handler.sendEmptyMessageDelayed(HIDE, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
