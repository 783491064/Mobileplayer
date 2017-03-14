package com.example.bjc.mobileplayer.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.example.bjc.mobileplayer.base.BasePager;
import com.example.bjc.mobileplayer.utils.LogUtil;

/**
 * Created by 毕静存 on 2016/12/11.
 */

public class NetAudioPage extends BasePager {

    private TextView textView;

    public NetAudioPage(Context context) {
        super(context);
    }
    

    @Override
    public View initRootView() {
        textView = new TextView(context);
        textView.setTextSize(40);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("网络音频的页面被初始化了");
        textView.setText("网络音频");
    }
}
