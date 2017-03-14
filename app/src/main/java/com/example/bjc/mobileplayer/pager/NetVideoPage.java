package com.example.bjc.mobileplayer.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bjc.mobileplayer.R;
import com.example.bjc.mobileplayer.activity.SystemVideoActivity;
import com.example.bjc.mobileplayer.adapter.MyNetVideoListAdapter;
import com.example.bjc.mobileplayer.base.BasePager;
import com.example.bjc.mobileplayer.bean.MediaItem;
import com.example.bjc.mobileplayer.utils.CacheUtil;
import com.example.bjc.mobileplayer.utils.Contansts;
import com.example.bjc.mobileplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by 毕静存 on 2016/12/11.
 */

public class NetVideoPage extends BasePager {

    private View view;
    private static final String TAG = "NetVideoPage.class";
    private ArrayList<MediaItem> netVideoItems;
    private ArrayList<MediaItem> netVideoMedias;
    private ArrayList<MediaItem> netVideoMediasMore;
    boolean isLoadMore = false;
    private MyNetVideoListAdapter adapter;

    public NetVideoPage(Context context) {
        super(context);
    }

    private XListView netlLv;
    private TextView tvLocal;
    private ProgressBar pbLocal;
    private boolean b = false;

    private void findViews() {
        netlLv = (XListView) view.findViewById(R.id.local_lv);
        tvLocal = (TextView) view.findViewById(R.id.tv_local);
        pbLocal = (ProgressBar) view.findViewById(R.id.pb_local);
        netlLv.setPullLoadEnable(true);
        netlLv.setXListViewListener(new MyXlistViewListener());
        netlLv.setOnItemClickListener(new MyOnItemClickListener());

    }

    @Override
    public View initRootView() {
        view = View.inflate(context, R.layout.net_video_page, null);
        findViews();
        return view;
    }

    @Override
    public void initData() {
        if (!TextUtils.isEmpty(CacheUtil.getString(context, "atguigu"))) {
            processData(CacheUtil.getString(context, "atguigu"));
        }
        gatDataFromNet();

    }

    /**
     * 从网络获取数据；
     */
    public void gatDataFromNet() {
        tvLocal.setVisibility(View.GONE);
        RequestParams params = new RequestParams(Contansts.net_video_url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:====" + result);
                if (result != null && result != "") {
                    CacheUtil.putString(context, "atguigu", result);
                    processData(result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onSuccess:====" + ex.getMessage());
                showData();

            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onSuccess:====" + cex.getMessage());
                tvLocal.setVisibility(View.VISIBLE);
                tvLocal.setText("访问网络取消");
            }

            @Override
            public void onFinished() {
                Log.e(TAG, "申请数据完成");
                pbLocal.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 数据的解析
     * <p>
     * 解析json数据 两种方式
     * 1.系统的手动解析
     * 2.用第三方 GSON ,fastJson;
     */
    private void processData(String result) {
        if (!isLoadMore) {
            b = true;
            netVideoMedias = pareJson(result);
            showData();
        } else {
            isLoadMore = true;
            netVideoMediasMore = pareJson(result);
            netVideoMedias.addAll(netVideoMediasMore);
            adapter.notifyDataSetChanged();
            netlLv.stopLoadMore();
        }
    }

    private void showData() {
        if (netVideoMedias != null && netVideoMedias.size() > 0) {
            adapter = new MyNetVideoListAdapter(context, netVideoMedias);
            netlLv.setAdapter(adapter);
            netlLv.stopRefresh(b);
            b = !b;
        } else {
            tvLocal.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 解析数据
     *
     * @param result
     */
    private ArrayList<MediaItem> pareJson(String result) {
        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = json.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {
                netVideoItems = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                    if (jsonObjectItem != null) {
                        String coverImg = jsonObjectItem.optString("coverImg");
                        String movieName = jsonObjectItem.optString("movieName");
                        String url = jsonObjectItem.optString("url");
                        String videoLength = jsonObjectItem.optString("videoLength");

                        MediaItem netVideoMediaItem = new MediaItem();
                        netVideoMediaItem.setCoverImg(coverImg);
                        netVideoMediaItem.setName(movieName);
                        netVideoMediaItem.setData(url);

                        netVideoItems.add(netVideoMediaItem);
                    }

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return netVideoItems;

    }

    class MyXlistViewListener implements XListView.IXListViewListener {

        @Override
        public void onRefresh() {
            gatDataFromNet();
        }

        @Override
        public void onLoadMore() {
            gatDataMoreFromNet();
        }


    }

    /**
     * 获取更多的数据；
     */
    private void gatDataMoreFromNet() {
        tvLocal.setVisibility(View.GONE);
        RequestParams params = new RequestParams(Contansts.net_video_url);
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess:====" + result);
                isLoadMore = true;
                if (result != null && result != "") {
                    processData(result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e(TAG, "onSuccess:====" + ex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e(TAG, "onSuccess:====" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                Log.e(TAG, "申请数据完成");
                isLoadMore = false;
            }
        });


    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, SystemVideoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("medias", netVideoMedias);
            intent.putExtras(bundle);
            intent.putExtra("position", position - 1);
            context.startActivity(intent);
        }
    }
}
