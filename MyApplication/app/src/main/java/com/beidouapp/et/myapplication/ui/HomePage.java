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

    /**
     * 初始化视频列表
     */
    private void initView() {
        /**
         * 给ListView添加数据条数
         */
        List list = new ArrayList();
        for (int i = 0; i < 10; i++) {
            list.add("" + i);
        }

        listLive.setAdapter(new HomeVideoAdapter(list, getActivity()));
        //将scrollView滑动到最顶端，默认是listView顶端；
        scrollViews.smoothScrollTo(0,0);
    }
}
