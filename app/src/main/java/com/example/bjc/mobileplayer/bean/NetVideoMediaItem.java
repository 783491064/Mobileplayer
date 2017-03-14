package com.example.bjc.mobileplayer.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 毕静存 on 2016/12/12.
 */

public class NetVideoMediaItem implements Serializable {

    private String coverImg="";
    private String hightUrl="";
    private String id="";
    private String movieId="";
    private String movieName="";
    private String rating="";
    private String summary="";
    private String data="";
    private String videoLength="";
    private String videoTitle="";
    private ArrayList<String> typeList=new ArrayList<>();

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public void setHightUrl(String hightUrl) {
        this.hightUrl = hightUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setVideoLength(String videoLength) {
        this.videoLength = videoLength;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public void setTypeList(ArrayList<String> typeList) {
        this.typeList = typeList;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public String getHightUrl() {
        return hightUrl;
    }

    public String getId() {
        return id;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getRating() {
        return rating;
    }

    public String getSummary() {
        return summary;
    }

    public String getData() {
        return data;
    }

    public String getVideoLength() {
        return videoLength;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public ArrayList<String> getTypeList() {
        return typeList;
    }

    @Override
    public String toString() {
        return "NetVideoMediaItem{" +
                "coverImg='" + coverImg + '\'' +
                ", hightUrl='" + hightUrl + '\'' +
                ", id='" + id + '\'' +
                ", movieId='" + movieId + '\'' +
                ", movieName='" + movieName + '\'' +
                ", rating='" + rating + '\'' +
                ", summary='" + summary + '\'' +
                ", data='" + data + '\'' +
                ", videoLength='" + videoLength + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", typeList=" + typeList +
                '}';
    }
}
