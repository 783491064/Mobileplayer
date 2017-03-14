// IMyAidlInterface.aidl
package com.example.bjc.mobileplayer;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
        /**
         * 根据位置打开播放器
         */
         void openAudioPlayer(int position);
        /**
         * 开始播放音乐
         */
         void start();
        /**
         * 暂停播放音乐
         */
         void pause();

        /**
         * 停止播放音乐
         */
         void stop();

        /**
         * 播放上一个音乐
         */
         void pre();

        /**
         * 播放下一个音乐
         */
         void next();

        /**
         * 获取音乐的演唱者
         */
        String getAuthor();

        /**
         * 获取音乐的名字
         */
         String getName();

        /**
         * 获取音乐的播放时长
         */
         long getDurition();

        /**
         * 获取音乐的播放进度
         */
        int getcurrentPosition();

        /**
         * 得到歌曲的播放路径；
         */
         String getPath();

        /**
         * 设置音乐播放的模式
         */
         void setPlayMode(int mode);

        /**
         * 设置音乐播放的模式
         */
         int getPlayMode();

         /**
           * 是否正在播放
           */
         boolean isPlaying();

         /**
           * 拖动进度条
           */
          void seekTo(int position);
}
