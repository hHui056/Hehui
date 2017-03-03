package com.beidouapp.et.myapplication.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.beidouapp.et.myapplication.R;
import com.beidouapp.et.myapplication.adapter.HomeVideoAdapter;
import com.beidouapp.et.myapplication.view.AdapterScrollListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * 首页
 * 项目名称：MyApplication
 * 创建人：huzy
 * 创建时间:2017/2/27 10:15
 */
public class HomePage extends Fragment {

    @BindView(R.id.list_live)
    AdapterScrollListView listLive;
    @BindView(R.id.scrollViews)
    ScrollView scrollViews;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, null);
        ButterKnife.bind(this, view);

        initView();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    /**
     * 初始化视频列表
     */
    private void initView() {


        String[] url = {"http://2449.vod.myqcloud.com/2449_43b6f696980311e59ed467f22794e792.f20.mp4","http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4","http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"};
        /**
         * 给ListView添加数据条数
         */
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Log.e("dddd",url[i]);
            list.add(url[i]);
        }
        listLive.setAdapter(new HomeVideoAdapter(list, getActivity()));
        listLive.setFocusable(false);
    }
}
