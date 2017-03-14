package com.example.bjc.mobileplayer.bean;

import java.io.Serializable;

/**
 * Created by 毕静存 on 2017/1/20.
 */

public class Lyrics implements Serializable {
    private String content;
    private long time;
    private long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyrics{" +
                "content='" + content + '\'' +
                ", time=" + time +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
