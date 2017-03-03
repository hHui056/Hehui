package com.beidouapp.et.myapplication.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.beidouapp.et.myapplication.R;
import com.beidouapp.et.myapplication.adapter.HomeVideoAdapter;
import com.beidouapp.et.myapplication.view.AdapterScrollListView;

import java.lang.ref.WeakReference;
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
    @BindView(R.id.vp_img)
    ViewPager viewPager;
    private ImageHandler handler = new ImageHandler(new WeakReference<HomePage>(this));

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


        String[] url = {"http://2449.vod.myqcloud.com/2449_43b6f696980311e59ed467f22794e792.f20.mp4", "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4", "http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4"};
        /**
         * 给ListView添加数据条数
         */
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Log.e("dddd", url[i]);
            list.add(url[i]);
        }
        listLive.setAdapter(new HomeVideoAdapter(list, getActivity()));
        listLive.setFocusable(false);
    }

    void addImgData() {
    }

    /**
     * 用于图片轮播的handler
     */
    private static class ImageHandler extends Handler {

        /**
         * 请求更新显示的View。
         */
        protected static final int MSG_UPDATE_IMAGE = 1;
        /**
         * 请求暂停轮播。
         */
        protected static final int MSG_KEEP_SILENT = 2;
        /**
         * 请求恢复轮播。
         */
        protected static final int MSG_BREAK_SILENT = 3;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         */
        protected static final int MSG_PAGE_CHANGED = 4;

        //轮播间隔时间
        protected static final long MSG_DELAY = 1000;

        private WeakReference<HomePage> weakReference;
        private int currentItem = 0;

        protected ImageHandler(WeakReference<HomePage> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HomePage activity = weakReference.get();
            if (activity == null) {
                return;
            }
            if (activity.handler.hasMessages(MSG_UPDATE_IMAGE)) {
                activity.handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    activity.viewPager.setCurrentItem(currentItem);
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    break;
                case MSG_BREAK_SILENT:
                    activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }
}
