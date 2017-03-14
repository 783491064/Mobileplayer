package com.example.bjc.startnetvideo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt_start = (Button) findViewById(R.id.bt_start);
        bt_start.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.bt_start){
            Intent intent=new Intent();
//            intent.setDataAndType(Uri.parse("http://192.168.17.93:8080/oppo.mp4"),"video/*");
            intent.setDataAndType(Uri.parse("http://192.168.17.93:8080/rmvb.rmvb"),"video/*");
//            intent.setDataAndType(Uri.parse("http://vfx.mtime.cn/Video/2016/12/27/mp4/161227204134063682_480.mp4"),"video/*");
            startActivity(intent);
        }
    }
}
