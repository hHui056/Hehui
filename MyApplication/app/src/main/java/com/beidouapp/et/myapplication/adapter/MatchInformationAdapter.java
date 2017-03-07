package com.beidouapp.et.myapplication.adapter;

import android.content.Context;
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

/**
 * 项目名称：MyApplication
 * 创建人：huzy
 * 创建时间:2017/3/7 13:50
 */
public class MatchInformationAdapter extends BaseAdapter {
    private List listData = new ArrayList();
    private Context context;

    public MatchInformationAdapter(List listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    public MatchInformationAdapter(){}

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
        ViewHolder viewHolder = null;
        if (null == convertView){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.match_information,null);
            viewHolder.btn_show = (Button) convertView.findViewById(R.id.btn_show);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.match_title_iv);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //测试数据
        if (position == 0) {
            viewHolder.btn_show.setVisibility(View.VISIBLE);
            viewHolder.imageView.setImageResource(R.mipmap.th00);
        }
        if (position == 1){
            viewHolder.btn_show.setVisibility(View.GONE);
            viewHolder.imageView.setImageResource(R.mipmap.th01);
        }
        if (position == 2){
            viewHolder.btn_show.setVisibility(View.GONE);
            viewHolder.imageView.setImageResource(R.mipmap.th03);
        }
        if (position == 3){
            viewHolder.btn_show.setVisibility(View.GONE);
            viewHolder.imageView.setImageResource(R.mipmap.th02);
        }
        if (position == 4){
            viewHolder.btn_show.setVisibility(View.GONE);
            viewHolder.imageView.setImageResource(R.mipmap.th04);
        }


        return convertView;
    }

    class ViewHolder{
        ImageView imageView;
        Button btn_show,btn_apply;
        TextView name_time,match_time,match_address,name_price,match_rule,bonus;
    }
}
