package com.example.bjc.mobileplayer.bean;

import java.io.Serializable;

/**
 * Created by 毕静存 on 2016/12/12.
 */

public class MediaItem implements Serializable {
    public String name;
    public String aitist;
    public String data;
    public long size;
    public long durtion;

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    private String coverImg="";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAitist() {
        return aitist;
    }

    public void setAitist(String aitist) {
        this.aitist = aitist;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDurtion() {
        return durtion;
    }

    public void setDurtion(long durtion) {
        this.durtion = durtion;
    }

}
