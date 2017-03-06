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
import com.beidouapp.et.myapplication.utils.Constants;
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


        String[] url = {Constants.test_videos[0], Constants.test_videos[1], Constants.test_videos[2]};
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(40, 40);
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

        // 设置自动轮播图片，2s后执行，周期是2s
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
        }, 2000, 2000);
    }

    class ImageTask extends AsyncTask<String, Void, List<CarouselImage>> {
        @Override
        protected List<CarouselImage> doInBackground(String... params) {
            List<CarouselImage> articles = new ArrayList<CarouselImage>();
            articles.add(new CarouselImage("", Constants.test_imgs[0]));
            articles.add(new CarouselImage("", Constants.test_imgs[1]));
            articles.add(new CarouselImage("", Constants.test_imgs[2]));
            articles.add(new CarouselImage("", Constants.test_imgs[3]));
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
