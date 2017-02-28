package com.beidouapp.et.myapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beidouapp.et.myapplication.ui.HomePage;
import com.beidouapp.et.myapplication.ui.LivePage;
import com.beidouapp.et.myapplication.ui.MatchPage;
import com.beidouapp.et.myapplication.ui.MinePage;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 首页
 */
public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.home)
    RadioButton home;
    @BindView(R.id.live)
    RadioButton live;
    @BindView(R.id.video)
    RadioButton video;
    @BindView(R.id.match)
    RadioButton match;
    @BindView(R.id.mine)
    RadioButton mine;
    @BindView(R.id.group)
    RadioGroup group;
    @BindView(R.id.mid_text)
    TextView mid_text;
    @BindView(R.id.live_bar)
    RelativeLayout layout_title;
    //对gridVeiw获取文本及图片
    private int[] icon = {R.mipmap.opy42x, R.mipmap.opy2x, R.mipmap.rt2x, R.mipmap.opy22x, R.mipmap.opy32x};
    private int[] iconSelect = {R.mipmap.oard22x, R.mipmap.opy52x, R.mipmap.rt2x, R.mipmap.opy62x, R.mipmap.opy72x};
    private String[] iconName = {"首页", "直播", "申请视频", "比赛", "我的"};
    private Drawable d01, d11, d21, d31, d41, d0, d1, d2, d3, d4;
    private HomePage homePageFragment;
    private LivePage livePageFragment;
    private MatchPage matchPageFragment;
    private MinePage minePageFragment;
    private Fragment mCurrent = new HomePage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
        group.setOnCheckedChangeListener(this);
    }

    /**
     * 切换我的
     */
    private void minePage() {
        if (null == minePageFragment)
            minePageFragment = new MinePage();
        changeFragment(minePageFragment);
        if (layout_title.getVisibility() == View.VISIBLE) {
            layout_title.setVisibility(View.GONE);
        }
    }

    /**
     * 切换赛事
     */
    private void matchPage() {
        if (null == matchPageFragment)
            matchPageFragment = new MatchPage();
        changeFragment(matchPageFragment);
        if (layout_title.getVisibility() == View.GONE) {
            layout_title.setVisibility(View.VISIBLE);
        }
        mid_text.setText("赛事");

    }

    /**
     * 切换直播
     */
    private void livePage() {
        if (null == livePageFragment)
            livePageFragment = new LivePage();
        changeFragment(livePageFragment);
        if (layout_title.getVisibility() == View.GONE) {
            layout_title.setVisibility(View.VISIBLE);
        }
        mid_text.setText("直播");

    }

    /**
     * 切换首页
     */
    private void homePage() {
        if (null == homePageFragment)
            homePageFragment = new HomePage();
        changeFragment(homePageFragment);
        if (layout_title.getVisibility() == View.GONE) {
            layout_title.setVisibility(View.VISIBLE);

        }
        mid_text.setText("广场");
    }

    /**
     * 隐藏、显示和添加碎片
     *
     * @param f
     */
    private void changeFragment(Fragment f) {
        if (mCurrent != f) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if (!f.isAdded()) { //如果没有被添加
                fragmentTransaction.hide(mCurrent).add(R.id.frameLayout, f).commit();
            } else {
                fragmentTransaction.hide(mCurrent).show(f).commit();
            }
            mCurrent = f;
        }
    }

    /**
     * 初始化底部导航栏控件
     */
    private void initView() {
        home.setText(iconName[0]);
        live.setText(iconName[1]);
        video.setText(iconName[2]);
        match.setText(iconName[3]);
        mine.setText(iconName[4]);
        int size = 100;
        //选择前
        d01 = this.getResources().getDrawable(icon[0]);
        d01.setBounds(0, 0, size, size);
        d11 = this.getResources().getDrawable(icon[1]);
        d11.setBounds(0, 0, size, size);
        d21 = this.getResources().getDrawable(icon[2]);
        d21.setBounds(0, 0, size, size);
        d31 = this.getResources().getDrawable(icon[3]);
        d31.setBounds(0, 0, size, size);
        d41 = this.getResources().getDrawable(icon[4]);
        d41.setBounds(0, 0, size, size);
        //选择后
        d0 = this.getResources().getDrawable(iconSelect[0]);
        d0.setBounds(0, 0, size, size);
        d1 = this.getResources().getDrawable(iconSelect[1]);
        d1.setBounds(0, 0, size, size);
        d2 = this.getResources().getDrawable(iconSelect[2]);
        d2.setBounds(0, 0, size, size);
        d3 = this.getResources().getDrawable(iconSelect[3]);
        d3.setBounds(0, 0, size, size);
        d4 = this.getResources().getDrawable(iconSelect[4]);
        d4.setBounds(0, 0, size, size);
        //初始化图标
        home.setCompoundDrawables(null, d0, null, null);
        live.setCompoundDrawables(null, d11, null, null);
        video.setCompoundDrawables(null, d21, null, null);
        match.setCompoundDrawables(null, d31, null, null);
        mine.setCompoundDrawables(null, d41, null, null);
        //TODO 初始化界面
        home.setTextColor(Color.RED);
        homePage();
    }

    /**
     * 底部导航栏的监听事件
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int c = this.getResources().getColor(R.color.hui);
        switch (checkedId) {
            case R.id.home:
                navigation();
                changeNavigation(home, d0);
                homePage();

                break;
            case R.id.live:
                navigation();
                changeNavigation(live, d1);
                livePage();
                break;
            case R.id.match:
                navigation();
                changeNavigation(match, d3);
                matchPage();
                break;
            case R.id.mine:
                navigation();
                changeNavigation(mine, d4);
                minePage();
                break;
            default:
                break;
        }
    }

    /**
     * 导航栏未被选择时
     */
    private void navigation() {
        int c = this.getResources().getColor(R.color.hui);
        home.setTextColor(c);
        home.setCompoundDrawables(null, d01, null, null);
        live.setTextColor(c);
        live.setCompoundDrawables(null, d11, null, null);
        video.setTextColor(c);
        video.setCompoundDrawables(null, d21, null, null);
        match.setTextColor(c);
        match.setCompoundDrawables(null, d31, null, null);
        mine.setTextColor(c);
        mine.setCompoundDrawables(null, d41, null, null);
    }

    /**
     * 改变导航栏图标
     *
     * @param rb
     * @param dra
     */
    private void changeNavigation(RadioButton rb, Drawable dra) {
        rb.setTextColor(Color.RED);
        rb.setCompoundDrawables(null, dra, null, null);
    }
}
