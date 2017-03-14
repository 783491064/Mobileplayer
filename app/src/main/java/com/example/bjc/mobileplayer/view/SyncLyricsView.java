package com.example.bjc.mobileplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.bjc.mobileplayer.bean.Lyrics;
import com.example.bjc.mobileplayer.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by 毕静存 on 2017/1/20.
 */

public class SyncLyricsView extends TextView {

    private int weight;
    private int height;
    private int index;
    private int textHeight;
    private ArrayList<Lyrics> list;
    private Paint whitePaint;
    private Paint greenPaint;
    private int tempY;
    private int currentPosition;
    private long sleepTime;
    private long timePoint;


    public SyncLyricsView(Context context) {
        this(context,null);
    }

    public SyncLyricsView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SyncLyricsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        weight = w;
        height = h;
    }

    private void initView() {
        textHeight= DensityUtil.dip2px(getContext(),20);








        list=new ArrayList<>();
        for(int i=0;i<1000;i++){
            Lyrics lyrics=new Lyrics();
            lyrics.setContent("i"+"aaaaaaaaa"+"i");
            lyrics.setTime(1000*i);
            lyrics.setSleepTime(i+1000);
            list.add(lyrics);
        }
        //准备白色的画笔
        greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        greenPaint.setTextSize(16);
        greenPaint.setAntiAlias(true);
        greenPaint.setTextAlign(Paint.Align.CENTER);
        //准备绿色的画笔
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setTextSize(16);
        whitePaint.setAntiAlias(true);
        whitePaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(list!=null&&list.size()>0){
            //绘制中间高亮的歌词
            canvas.drawText(list.get(index).getContent(),weight/2,height/2,greenPaint);
            //绘制上部的歌词
            tempY = height/2;
            for(int i=index-1;i>=0;i--){
                String content=list.get(i).getContent();
                tempY = tempY -textHeight;
                if(tempY <0){
                    break;
                }
                canvas.drawText(content,weight/2, tempY,whitePaint);
            }
            //绘制下部的歌词
            tempY=height/2;
            for(int i=index+1;i<list.size();i++){
                String content=list.get(i).getContent();
                tempY = tempY +textHeight;
                if(tempY >height){
                    break;
                }
                canvas.drawText(content,weight/2, tempY,whitePaint);
            }
        }else{
            canvas.drawText("没有歌词",weight/2,height/2,greenPaint);
        }

    }

    public void setNextLyric(int currentPosition) {
        this.currentPosition=currentPosition;
        if(list==null||list.size()==0){
            return;
        }
        for(int i=1;i<list.size();i++){
            if(currentPosition<list.get(i).getTime()){
                int tempIndex=i-1;
                if(currentPosition>=list.get(tempIndex).getTime()){
                    index=tempIndex;
                    sleepTime = list.get(index).getSleepTime();
                    timePoint = list.get(index).getTime();
                }
            }

        }
        invalidate();
    }
}
