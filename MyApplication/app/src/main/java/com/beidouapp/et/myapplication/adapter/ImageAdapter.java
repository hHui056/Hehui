package com.beidouapp.et.myapplication.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by allen on 2017/3/3.
 * <p>
 * 首页轮播图adapter
 */

public class ImageAdapter extends PagerAdapter {

    private ArrayList<ImageView> viewlist;

    public ImageAdapter(ArrayList<ImageView> viewlist) {
        this.viewlist = viewlist;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position %= viewlist.size();
        if (position < 0) {
            position = viewlist.size() + position;
        }
        ImageView view = viewlist.get(position);
        ViewParent vp = view.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(view);
        }
        container.addView(view);
        return view;
    }
}