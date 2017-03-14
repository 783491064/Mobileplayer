package com.example.bjc.mobileplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.bjc.mobileplayer.R;
import com.example.bjc.mobileplayer.bean.MediaItem;
import com.example.bjc.mobileplayer.utils.CircleTransform;

import java.util.ArrayList;

/**
 * Created by 毕静存 on 2017/1/1.
 */

public class MyNetVideoListAdapter extends BaseAdapter {
    private ArrayList<MediaItem> netVideoItems;
    private Context context;

    public MyNetVideoListAdapter(Context context, ArrayList<MediaItem> netVideoItems) {
        this.netVideoItems = netVideoItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return netVideoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return netVideoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.net_video_item, null);
            holder = new ViewHolder();

            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_item_name);
            holder.tv_durition = (TextView) convertView.findViewById(R.id.tv_item_durition);
            holder.iv_video = (ImageView) convertView.findViewById(R.id.iv_item);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MediaItem mediaItem = netVideoItems.get(position);
        String name = mediaItem.getName();//视频的名字；
        String imUrl = mediaItem.getCoverImg();//列表展示图片

        holder.tv_name.setText(name);
//        x.image().bind(holder.iv_video,imUrl);
        //用glide加载圆角图片
//        Glide.with(context)
//                .load(imUrl)
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .transform(new CornersTransform(context))
//                .placeholder(R.drawable.ic_launcher)
//                .error(R.drawable.ic_launcher)
//                .crossFade()
//                .into(holder.iv_video);

        //用glide加载圆图片
        Glide.with(context)
                .load(imUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new CircleTransform(context))
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .crossFade()
                .into(holder.iv_video);

        return convertView;
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_durition;
        ImageView iv_video;
    }
}
