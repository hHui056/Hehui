package com.beidouapp.et.myapplication.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.beidouapp.et.myapplication.R;
import com.beidouapp.et.myapplication.adapter.MatchInformationAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 比赛
 * 项目名称：MyApplication
 * 创建人：huzy
 * 创建时间:2017/2/27 10:17
 */
public class MatchPage extends Fragment {

    @BindView(R.id.matchList)
    ListView matchList;
    private List<String> listDate = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.match, null);
        ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView(){
        getDate();
        matchList.setAdapter(new MatchInformationAdapter(listDate,getActivity()));
    }

    private List getDate(){
        for (int i = 0; i < 5; i++) {
            listDate.add(""+i);
        }
        return listDate;
    }
}
