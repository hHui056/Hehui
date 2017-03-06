package com.beidouapp.et.myapplication.ui;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.beidouapp.et.myapplication.R;
import com.beidouapp.et.myapplication.adapter.HeaderAdapter;
import com.beidouapp.et.myapplication.adapter.HomeVideoAdapter;
import com.beidouapp.et.myapplication.bean.CarouselImage;
import com.beidouapp.et.myapplication.view.AdapterScrollListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

    private static final int UPTATE_VIEWPAGER = 0;
    @BindView(R.id.list_live)
    AdapterScrollListView listLive;
    @BindView(R.id.scrollViews)
    ScrollView scrollViews;
    @BindView(R.id.vp_hot)
    ViewPager vp_hot;
    @BindView(R.id.ll_hottest_indicator)
    LinearLayout llHottestIndicator;
    //设置当前 第几个图片 被选中
    private int autoCurrIndex = 0;

    private ImageView[] mBottomImages;//底部只是当前页面的小圆点

    private Timer timer = new Timer(); //为了方便取消定时轮播，将 Timer 设为全局

    //定时轮播图片，需要在主线程里面修改 UI
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPTATE_VIEWPAGER:
                    if (msg.arg1 != 0) {
                        vp_hot.setCurrentItem(msg.arg1);
                    } else {
                        //false 当从末页调到首页是，不显示翻页动画效果，
                        vp_hot.setCurrentItem(msg.arg1, false);
                    }
                    break;
            }
        }
    };

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * 加载图片
         */
        new ImageTask().execute();
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

    private void setUpViewPager(final List<CarouselImage> headerArticles) {
        HeaderAdapter imageAdapter = new HeaderAdapter(getActivity(), headerArticles);
        vp_hot.setAdapter(imageAdapter);

        //创建底部指示位置的导航栏
        mBottomImages = new ImageView[headerArticles.size()];

        for (int i = 0; i < mBottomImages.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
            params.setMargins(5, 0, 5, 0);
            imageView.setLayoutParams(params);
            if (i == 0) {
                imageView.setBackgroundResource(R.drawable.indicator_select);
            } else {
                imageView.setBackgroundResource(R.drawable.indicator_not_select);
            }

            mBottomImages[i] = imageView;
            //把指示作用的原点图片加入底部的视图中
            llHottestIndicator.addView(mBottomImages[i]);

        }

        vp_hot.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                                           //图片左右滑动时候，将当前页的圆点图片设为选中状态
                                           @Override
                                           public void onPageSelected(int position) {
                                               // 一定几个图片，几个圆点，但注意是从0开始的
                                               int total = mBottomImages.length;
                                               for (int j = 0; j < total; j++) {
                                                   if (j == position) {
                                                       mBottomImages[j].setBackgroundResource(R.drawable.indicator_select);
                                                   } else {
                                                       mBottomImages[j].setBackgroundResource(R.drawable.indicator_not_select);
                                                   }
                                               }

                                               //设置全局变量，currentIndex为选中图标的 index
                                               autoCurrIndex = position;
                                           }

                                           @Override
                                           public void onPageScrolled(int i, float v, int i1) {
                                           }

                                           @Override
                                           public void onPageScrollStateChanged(int state) {
                                           }
                                       }
        );

        // 设置自动轮播图片，1s后执行，周期是1s
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPTATE_VIEWPAGER;
                if (autoCurrIndex == headerArticles.size() - 1) {
                    autoCurrIndex = -1;
                }
                message.arg1 = autoCurrIndex + 1;
                mHandler.sendMessage(message);
            }
        }, 1000, 1000);
    }

    class ImageTask extends AsyncTask<String, Void, List<CarouselImage>> {
        @Override
        protected List<CarouselImage> doInBackground(String... params) {
            List<CarouselImage> articles = new ArrayList<CarouselImage>();
            articles.add(new CarouselImage("", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488778555559&di=9149ea0d0faa2ee483079f623cea571a&imgtype=0&src=http%3A%2F%2Fupload.art.ifeng.com%2F2015%2F0811%2F1439260959533.jpg"));
            articles.add(new CarouselImage("", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488778598074&di=9c67a6ea1d4711aaf1906b7a77f3da5d&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F6a600c338744ebf8bd33cee3dcf9d72a6159a7a8.jpg"));
            articles.add(new CarouselImage("", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488778598073&di=674a806e9ac75ea7c64220ac17255c3b&imgtype=0&src=http%3A%2F%2Fa.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F37d12f2eb9389b50af364a8e8035e5dde6116e25.jpg"));
            articles.add(new CarouselImage("", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1488778598073&di=dd430d2985e0e95dba52d6fb9bb2ae27&imgtype=0&src=http%3A%2F%2Fe.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2Ff11f3a292df5e0fe6dd123dd596034a85fdf7225.jpg"));
            return articles;
        }

        @Override
        protected void onPostExecute(List<CarouselImage> articles) {
            //这儿的 是 url 的集合
            super.onPostExecute(articles);
            setUpViewPager(articles);
        }
    }
}
