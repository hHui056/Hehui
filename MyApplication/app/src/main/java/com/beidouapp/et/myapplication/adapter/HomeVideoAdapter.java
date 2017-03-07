package com.beidouapp.et.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidouapp.et.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * 项目名称：MyApplication
 * 创建人：huzy
 * 创建时间:2017/2/28 15:54
 */
public class HomeVideoAdapter extends BaseAdapter {
    public ViewHolder viewHolder = null;
    private List listData = new ArrayList();
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
        Log.e("list", listData.toString());
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.home_video_info, null);
            viewHolder.videoPlayer = (JCVideoPlayer) convertView.findViewById(R.id.video);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.videoPlayer.setUp(listData.get(position).toString(), "试试" + position);
        viewHolder.videoPlayer.ivThumb.setImageResource(R.mipmap.ic_launcher);
        return convertView;
    }

    public class ViewHolder {
        Button btn_guanzhu;
        TextView txt_name, txt_address, txt_watch_info, txt_zan_num, txt_pinlun_num;
        JCVideoPlayer videoPlayer;
        ImageView user_icon, img_zan, img_pinlun, img_share;
    }
}
