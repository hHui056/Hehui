package com.beidouapp.et.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.beidouapp.et.myapplication.R;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * 项目名称：MyApplication
 * 创建人：huzy
 * 创建时间:2017/2/28 15:54
 */
public class HomeVideoAdapter extends BaseAdapter {
    private List listData;
    private Context context;

    public HomeVideoAdapter() {
    }

    public HomeVideoAdapter(List listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("list",listData.toString());
        if (null == convertView){
            convertView = LayoutInflater.from(context).inflate(R.layout.home_video_info,null);
            JCVideoPlayer video = (JCVideoPlayer) convertView.findViewById(R.id.video);
            video.setUp("http://2449.vod.myqcloud.com/2449_43b6f696980311e59ed467f22794e792.f20.mp4",
                    "试试就试试");
        }
        return convertView;
    }
}
