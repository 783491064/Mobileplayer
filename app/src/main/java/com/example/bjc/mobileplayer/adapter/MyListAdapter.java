package com.example.bjc.mobileplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bjc.mobileplayer.R;
import com.example.bjc.mobileplayer.bean.MediaItem;
import com.example.bjc.mobileplayer.utils.Utils;

import java.util.ArrayList;

/**
 * Created by 毕静存 on 2016/12/13.
 */

public class MyListAdapter extends BaseAdapter {
    private final Context context;
    private  boolean isAudio;
    private ArrayList<MediaItem> videoList;
    private final Utils utils;

    public MyListAdapter(Context context, ArrayList<MediaItem> videoList,boolean isAudio) {
        super();
        this.context=context;
        this.videoList=videoList;
        this.isAudio=isAudio;
        utils = new Utils();
    }

    @Override
    public int getCount() {
        return videoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.video_item,null);
            holder=new ViewHolder();

            holder.tv_name= (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.tv_durition= (TextView) convertView.findViewById(R.id.tv_item_durition);
            holder.tv_size= (TextView) convertView.findViewById(R.id.tv_item_size);
            holder.iv_item= (ImageView) convertView.findViewById(R.id.iv_item);

            convertView.setTag(holder);

        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        MediaItem mediaItem = videoList.get(position);
        String name=mediaItem.getName();
        long durition =mediaItem.getDurtion();
        Long size = videoList.get(position).getSize();

        holder.tv_name.setText(name);
        holder.tv_durition.setText(utils.stringForTime((int) durition));
        holder.tv_size.setText(Formatter.formatFileSize(context,mediaItem.getSize()));
        if(isAudio){
            holder.iv_item.setImageResource(R.drawable.music_default_bg);
        }

        return convertView;
    }

    static class ViewHolder{
    TextView tv_name;
    TextView tv_durition;
    TextView tv_size;
        ImageView iv_item;
   }

 }
