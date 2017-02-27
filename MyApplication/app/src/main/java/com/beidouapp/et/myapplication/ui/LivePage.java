package com.beidouapp.et.myapplication.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidouapp.et.myapplication.R;
import com.beidouapp.et.myapplication.adapter.LiveShowAdapter;
import com.beidouapp.et.myapplication.bean.LiveShowInfo;
import com.beidouapp.et.myapplication.view.AutoListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 直播
 * 项目名称：MyApplication
 * 创建人：huzy
 * 创建时间:2017/2/27 10:16
 */
public class LivePage extends Fragment implements AutoListView.OnLoadListener, AutoListView.OnRefreshListener {
    @BindView(R.id.list_liveshow)
    AutoListView list_liveshow;
    ArrayList<LiveShowInfo> infos = new ArrayList<LiveShowInfo>();
    LiveShowAdapter adapter;
    /**
     * 当前页数
     */
    private int nowPage = 1;
    /**
     * handler具体处理请求过后的数据
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            ArrayList<LiveShowInfo> result = (ArrayList<LiveShowInfo>) msg.obj;
            switch (msg.what) {
                case AutoListView.REFRESH:
                    list_liveshow.onRefreshComplete();
                    infos.clear();
                    infos.addAll(result);
                    break;
                case AutoListView.LOAD:
                    list_liveshow.onLoadComplete();
                    infos.addAll(result);
                    break;
                default:
                    break;
            }
            list_liveshow.setResultSize(result.size());
            adapter.notifyDataSetChanged();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live, null);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();

    }

    void initData() {
        list_liveshow.setOnLoadListener(this);
        list_liveshow.setOnRefreshListener(this);

        adapter = new LiveShowAdapter(this.getActivity(), infos);
        list_liveshow.setAdapter(adapter);


        getLiveShowInfo(1, AutoListView.REFRESH);
    }

    /**
     * 获取直播视频信息
     *
     * @param page 页数
     * @param type 加载更多/刷新
     */
    void getLiveShowInfo(int page, final int type) {

        ArrayList<LiveShowInfo> newInfos = new ArrayList<LiveShowInfo>();

        for (int i = 0; i < 5; i++) {
            LiveShowInfo info = new LiveShowInfo();
            newInfos.add(info);
        }

        Message msg = handler.obtainMessage();
        msg.what = type;
        msg.obj = newInfos;
        handler.sendMessage(msg);
        nowPage++;
    }

    @Override
    public void onLoad() {
        getLiveShowInfo(nowPage, AutoListView.LOAD);
    }

    @Override
    public void onRefresh() {
        nowPage = 1;
        getLiveShowInfo(1, AutoListView.REFRESH);
    }
}
