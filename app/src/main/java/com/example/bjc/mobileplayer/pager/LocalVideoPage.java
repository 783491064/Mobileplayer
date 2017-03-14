package com.example.bjc.mobileplayer.pager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bjc.mobileplayer.R;
import com.example.bjc.mobileplayer.activity.SystemVideoActivity;
import com.example.bjc.mobileplayer.adapter.MyListAdapter;
import com.example.bjc.mobileplayer.base.BasePager;
import com.example.bjc.mobileplayer.bean.MediaItem;
import com.example.bjc.mobileplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by 毕静存 on 2016/12/11.
 */

public class LocalVideoPage extends BasePager {
    public View view;
    private ListView local_lv;
    private TextView tv_local;
    private ProgressBar pb_local;
    private ArrayList<MediaItem> videoList;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (videoList != null && videoList.size() > 0) {
                //有数据
                tv_local.setVisibility(View.GONE);
                //适配数据展示；
                local_lv.setAdapter(new MyListAdapter(context, videoList,false));
            } else {
                //没有数据；
                tv_local.setVisibility(View.VISIBLE);
            }
            pb_local.setVisibility(View.GONE);
        }
    };


    public LocalVideoPage(Context context) {
        super(context);

    }


    @Override
    public View initRootView() {
        view = View.inflate(context, R.layout.local_page, null);
        local_lv = (ListView) view.findViewById(R.id.local_lv);
        tv_local = (TextView) view.findViewById(R.id.tv_local);
        pb_local = (ProgressBar) view.findViewById(R.id.pb_local);
        local_lv.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频的页面被初始化了");
        getDataFromLocal();
    }

    /**
     * 获取本地的视频资源；
     */
    public void getDataFromLocal() {
        videoList = new ArrayList<>();
        new Thread() {
            @Override
            public void run() {
                super.run();
                isGrantExternalRW((Activity) context);
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
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
                        videoList.add(mediaItem);

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
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }

    /**
     * listView条目的点击事件；
     */
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent intent=new Intent(context, SystemVideoActivity.class);
//            intent.setDataAndType(Uri.parse(videoList.get(position).getData()),"video/*");
            Intent intent = new Intent(context, SystemVideoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("medias", videoList);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            context.startActivity(intent);
        }
    }
}
