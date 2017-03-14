package com.example.bjc.mobileplayer.base;

import android.content.Context;
import android.view.View;


/**
 * Created by 毕静存 on 2016/12/11.
 */

public abstract class BasePager {

    public final Context context;
    public View rootView;
    public boolean isInitData;

    public BasePager(Context context) {
        this.context = context;
        rootView = initRootView();
    }

    //子页面实现自己的布局；
    public abstract View initRootView();

    //子页面实现自己的数据；
    public void initData() {

    }
}
