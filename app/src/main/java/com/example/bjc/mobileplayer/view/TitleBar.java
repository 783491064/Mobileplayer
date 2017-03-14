package com.example.bjc.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bjc.mobileplayer.R;

/**
 * Created by 毕静存 on 2016/12/12.
 */


public class TitleBar extends LinearLayout implements View.OnClickListener {
    private final Context context;
    private View tv_title_search;
    private View rl_game;
    private View iv_history;

    public TitleBar(Context context) {
        this(context,null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_title_search = getChildAt(1);
        rl_game = getChildAt(2);
        iv_history = getChildAt(3);

        tv_title_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_history.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_title_search:
                Toast.makeText(context,"全网收索",Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game:
                Toast.makeText(context,"游戏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_history:
                Toast.makeText(context,"历史记录",Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
