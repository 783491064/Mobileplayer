package com.example.bjc.mobileplayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.bjc.mobileplayer.R;
import com.example.bjc.mobileplayer.base.BasePager;
import com.example.bjc.mobileplayer.pager.LocalAudioPage;
import com.example.bjc.mobileplayer.pager.LocalVideoPage;
import com.example.bjc.mobileplayer.pager.NetAudioPage;
import com.example.bjc.mobileplayer.pager.NetVideoPage;

import java.util.ArrayList;


public class MainActiviy extends FragmentActivity {

    private RadioGroup  rg_bottom_tag;

    /**
     * 页面的集合
     */
    private ArrayList<BasePager> basePagers;

    /**
     * 选中的位置
     */
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activiy);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.main_rg);

        basePagers = new ArrayList<>();
        basePagers.add(new LocalVideoPage(this));//添加本地视频页面-0
        basePagers.add(new LocalAudioPage(this));//添加本地音乐页面-1
        basePagers.add(new NetVideoPage(this));//添加网络视频页面-2
        basePagers.add(new NetAudioPage(this));//添加网络音频页面-3


        //设置RadioGroup的监听
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.local_video);//默认选中首页

    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId){
                default:
                    position = 0;
                    break;
                case R.id.local_radio://音频
                    position = 1;
                    break;
                case R.id.net_video://网络视频
                    position = 2;
                    break;
                case R.id.net_audio://网络音频
                    position = 3;
                    break;
            }
            setFragment();
        }
    }

    /**
     * 把页面添加到Fragment中
     */
    public void setFragment() {
        //1.得到FragmentManger
        FragmentManager manager = getSupportFragmentManager();
        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();
        //3.替换
        ft.replace(R.id.main_fg,new Fragment(){
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                BasePager basePager = getBasePager();
                if(basePager != null){
                    //各个页面的视图
                    return basePager.rootView;
                }
                return null;
            }
        });
        //4.提交事务
        ft.commit();

    }

    /**
     * 根据位置得到对应的页面
     * @return
     */
    private BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if(basePager != null&&!basePager.isInitData){
            basePager.initData();//联网请求或者绑定数据
            basePager.isInitData = true;
        }
        return basePager;
    }
}
