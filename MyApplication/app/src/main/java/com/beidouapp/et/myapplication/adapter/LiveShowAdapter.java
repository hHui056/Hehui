package com.beidouapp.et.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidouapp.et.myapplication.R;
import com.beidouapp.et.myapplication.bean.LiveShowInfo;

import java.util.ArrayList;

/**
 * Created by allen on 2017/2/27.
 */

public class LiveShowAdapter extends BaseAdapter {
    ViewHolder viewHolder = null;
    private Context context;
    private ArrayList<LiveShowInfo> infos;

    public LiveShowAdapter(Context context, ArrayList<LiveShowInfo> infos) {
        this.context = context;
        this.infos = infos;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int i) {
        return infos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.liveshow_layout,
                    parent, false);
            viewHolder.txt_starttime = (TextView) convertView.findViewById(R.id.txt_starttime);
            viewHolder.txt_matchname = (TextView) convertView.findViewById(R.id.txt_matchname);
            viewHolder.txt_matchaddress = (TextView) convertView.findViewById(R.id.txt_matchaddress);
            viewHolder.img_match = (ImageView) convertView.findViewById(R.id.img_match);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private class ViewHolder {
        ImageView img_match;
        TextView txt_starttime, txt_matchname, txt_matchaddress;
    }

}
