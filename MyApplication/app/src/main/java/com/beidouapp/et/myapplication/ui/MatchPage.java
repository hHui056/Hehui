package com.beidouapp.et.myapplication.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beidouapp.et.myapplication.R;

/**
 * 比赛
 * 项目名称：MyApplication
 * 创建人：huzy
 * 创建时间:2017/2/27 10:17
 */
public class MatchPage extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.match,null);
    }
}
