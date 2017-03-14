package com.example.bjc.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.bjc.mobileplayer.IMyAidlInterface;
import com.example.bjc.mobileplayer.R;
import com.example.bjc.mobileplayer.activity.LocalAudioActivity;
import com.example.bjc.mobileplayer.bean.MediaItem;
import com.example.bjc.mobileplayer.utils.CacheUtil;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 毕静存 on 2017/1/3.
 */

public class AidlAudioPlayerService extends Service {
    private ArrayList<MediaItem> audioList;
    private int position;
    private MediaItem mediaItem;
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;
    public static final String ACTION = "com.example.bjc.mobile_AUDIO";
    public static final int NORMAL_MODLE = 1;
    public static final int SINGLE_MODLE = 2;
    public static final int ALL_MODLE = 3;
    public int playModule = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        getDataFromLocal();
    }

    /**
     * 获取本地音乐
     */
    private void getDataFromLocal() {
        audioList = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                ContentResolver contentResolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频的名称
                        MediaStore.Video.Media.DURATION,//视频的时长
                        MediaStore.Video.Media.SIZE,//视频的大小
                        MediaStore.Video.Media.DATA,//视频的绝对路径
                        MediaStore.Video.Media.ARTIST,//作者
                };
                Cursor cursor = contentResolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        audioList.add(mediaItem);

                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String aitist = cursor.getString(4);

                        mediaItem.setName(name);
                        mediaItem.setDurtion(duration);
                        mediaItem.setSize(size);
                        mediaItem.setData(data);
                        mediaItem.setAitist(aitist);
                    }
                }
                cursor.close();
                //发送消息；
            }
        }.start();

    }

    private IMyAidlInterface.Stub stub = new IMyAidlInterface.Stub() {
        @Override
        public void openAudioPlayer(int position) throws RemoteException {
            openAudioPlayer1(position);
        }

        @Override
        public void start() throws RemoteException {
            start1();
        }

        @Override
        public void pause() throws RemoteException {
            pause1();
        }

        @Override
        public void stop() throws RemoteException {
            stop1();
        }

        @Override
        public void pre() throws RemoteException {
            pre1();
        }

        @Override
        public void next() throws RemoteException {
            next1();
        }

        @Override
        public String getAuthor() throws RemoteException {
            return getAuthor1();
        }

        @Override
        public String getName() throws RemoteException {
            return getName1();
        }

        @Override
        public long getDurition() throws RemoteException {
            return getDurition1();
        }

        @Override
        public int getcurrentPosition() throws RemoteException {
            return getcurrentPosition1();
        }

        @Override
        public String getPath() throws RemoteException {
            return getPath1();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            setPlayMode1(mode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return getPlayMode1();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return isPlaying1();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mediaPlayer.seekTo(position);
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }


    /**
     * 根据位置打开播放器
     */
    private void openAudioPlayer1(int position) {
        this.position = position;
        if (audioList != null && audioList.size() > 0) {
            mediaItem = audioList.get(position);
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            if (mediaItem != null) {
                initAudioPlayer();
            }

        } else {
            Toast.makeText(this, "本地没有查找到音乐列表", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化音乐播放器；
     */
    private void initAudioPlayer() {
        try {
            mediaPlayer.setOnPreparedListener(new AudioPreparListener());
            mediaPlayer.setOnCompletionListener(new AudioCompletionListener());
            mediaPlayer.setOnErrorListener(new AudioErrorListener());

            mediaPlayer.setDataSource(mediaItem.getData());
            mediaPlayer.prepareAsync();

            if (playModule == SINGLE_MODLE) {
                mediaPlayer.setLooping(true);
            } else {
                mediaPlayer.setLooping(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 准备播放的监听；
     */
    class AudioPreparListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //当音乐准备播放后，要通知Activity,显示相关的数据；
            noTifyChange(ACTION);
            //开始播放
            start1();
            //添加通知栏
            addNotification();
        }
    }

    /**
     * 添加通知栏
     */
    private void addNotification() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Intent intent = new Intent(this, LocalAudioActivity.class);
            intent.putExtra("Notification", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.login_icon)
                    .setContentTitle("321音乐")
                    .setContentText("正在播放" + getName1())
                    .setContentIntent(pendingIntent)
                    .build();
        }
        manager.notify(1, notification);
    }

    /**
     * 发送准备好的通知
     *
     * @param action
     */
    private void noTifyChange(String action) {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     * 播放完成的监听
     */
    class AudioCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            next1();
        }
    }

    /**
     * 播放错误的监听
     */
    class AudioErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            next1();
            return true;
        }
    }

    /**
     * 开始播放音乐
     */
    private void start1() {
        mediaPlayer.start();
    }

    /**
     * 暂停播放音乐
     */
    private void pause1() {
        mediaPlayer.pause();
        manager.cancel(1);
    }

    /**
     * 停止播放音乐
     */
    private void stop1() {
        mediaPlayer.stop();
    }

    /**
     * 播放上一个音乐
     */
    private void pre1() {
        setPrePosition();
        playpre();
    }

    /**
     * 播放上一个
     */
    private void playpre() {
        int Mode = getPlayMode1();
        if (Mode == NORMAL_MODLE) {
            if (position >= 0) {
                openAudioPlayer1(position);
            } else {
                position = 0;
            }
        } else if (Mode == SINGLE_MODLE) {
            openAudioPlayer1(position);
        } else if (Mode == ALL_MODLE) {
            openAudioPlayer1(position);
        } else {
            if (position >= 0) {
                openAudioPlayer1(position);
            } else {
                position = 0;
            }
        }

    }

    /**
     * 上一个位置的确定
     */
    private void setPrePosition() {
        int Mode = getPlayMode1();
        if (Mode == NORMAL_MODLE) {
            position--;
        } else if (Mode == SINGLE_MODLE) {
            position--;
            if (position < 0) {
                position = audioList.size() - 1;
            }
        } else if (Mode == ALL_MODLE) {
            position--;
            if (position < 0) {
                position = audioList.size() - 1;
            }
        } else {
            position--;
        }

    }

    /**
     * 播放下一个音乐
     */
    private void next1() {
        setPosition();
        playNext();
    }

    /**
     * 播放下一个
     */
    private void playNext() {
        int Mode = getPlayMode1();
        if (Mode == NORMAL_MODLE) {
            if (position < audioList.size()) {
                openAudioPlayer1(position);
            } else {
                position = audioList.size() - 1;
            }
        } else if (Mode == SINGLE_MODLE) {
            openAudioPlayer1(position);
        } else if (Mode == ALL_MODLE) {
            openAudioPlayer1(position);
        } else {
            if (position < audioList.size()) {
                openAudioPlayer1(position);
            } else {
                position = audioList.size() - 1;
            }
        }

    }

    /**
     * 确定播放的位置
     */
    private void setPosition() {
        int Mode = getPlayMode1();
        if (Mode == NORMAL_MODLE) {
            position++;
        } else if (Mode == SINGLE_MODLE) {
            position++;
            if (position >= audioList.size()) {
                position = 0;
            }
        } else if (Mode == ALL_MODLE) {
            position++;
            if (position >= audioList.size()) {
                position = 0;
            }
        } else {
            position++;
        }
    }

    /**
     * 获取音乐的演唱者
     */
    private String getAuthor1() {
        return mediaItem.getAitist();
    }

    /**
     * 获取音乐的名字
     */
    private String getName1() {
        return mediaItem.getName();
    }

    /**
     * 获取音乐的播放时长
     */
    private long getDurition1() {
        return mediaPlayer.getDuration();
    }

    /**
     * 获取音乐的播放进度
     */
    private int getcurrentPosition1() {
        return mediaPlayer.getCurrentPosition();
    }

    /**
     * 得到歌曲的播放路径；
     */
    private String getPath1() {
        return "";
    }

    /**
     * 设置音乐播放的模式
     */
    private void setPlayMode1(int mode) {
        this.playModule = mode;
        CacheUtil.putPlayModule(this, "modle", playModule);//保存播放模式；

        if (playModule == SINGLE_MODLE) {
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.setLooping(false);
        }
    }

    /**
     * 设置音乐播放的模式
     */
    private int getPlayMode1() {
        return playModule;
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying1() {
        return mediaPlayer.isPlaying();
    }
}
