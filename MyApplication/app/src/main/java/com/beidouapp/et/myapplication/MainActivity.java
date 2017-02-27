package com.beidouapp.et.myapplication;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
    //对gridVeiw获取文本及图片
    private int[] icon = {R.mipmap.opy42x, R.mipmap.opy2x, R.mipmap.opy42x, R.mipmap.opy22x, R.mipmap.opy32x};
    private int[] iconSelect = {R.mipmap.oard22x, R.mipmap.opy52x, R.mipmap.oard22x, R.mipmap.opy62x, R.mipmap.opy72x};
    private String[] iconName = {"首页", "直播", "申请视频", "比赛", "我的"};
    private Drawable d01,d11,d21,d31,d41,d0,d1,d2,d3,d4;
    private HomePage homePageFragment;
    private LivePage livePageFragment;
    private MatchPage matchPageFragment;
    private MinePage minePageFragment;
    private Fragment mCurrent =  new HomePage();

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
    private void minePage(){
        if (null == minePageFragment)
            minePageFragment = new MinePage();
        changeFragment(minePageFragment);
    }
    /**
     * 切换赛事
     */
    private void matchPage(){
        if (null == matchPageFragment)
            matchPageFragment = new MatchPage();
        changeFragment(matchPageFragment);
    }
    /**
     * 切换直播
     */
    private void livePage(){
        if (null == livePageFragment)
            livePageFragment = new LivePage();
        changeFragment(livePageFragment);
    }
    /**
     * 切换首页
     */
    private void homePage(){
        if (null == homePageFragment)
            homePageFragment = new HomePage();
        changeFragment(homePageFragment);
    }
    /**
     * 隐藏、显示和添加碎片
     * @param to
     */
    private void changeFragment(Fragment to){
        if (mCurrent != to) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            if (!to.isAdded()){ //如果没有被添加
                fragmentTransaction.hide(mCurrent).add(R.id.frameLayout,to).commit();
            }else{
                fragmentTransaction.hide(mCurrent).show(to).commit();
            }
            mCurrent = to;
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
        //选择前
        d01 = this.getResources().getDrawable(icon[0]);
        d11 = this.getResources().getDrawable(icon[1]);
        d21 = this.getResources().getDrawable(icon[2]);
        d31 = this.getResources().getDrawable(icon[3]);
        d41 = this.getResources().getDrawable(icon[4]);
        //选择后
        d0 = this.getResources().getDrawable(iconSelect[0]);
        d1 = this.getResources().getDrawable(iconSelect[1]);
        d2 = this.getResources().getDrawable(iconSelect[2]);
        d3 = this.getResources().getDrawable(iconSelect[3]);
        d4 = this.getResources().getDrawable(iconSelect[4]);
        //初始化图标
        home.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d0, null, null);
        live.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d11, null, null);
        video.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d21, null, null);
        match.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d31, null, null);
        mine.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d41, null, null);
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
                changeNavigation(home,d0);
                homePage();
                break;
            case R.id.live:
                navigation();
                changeNavigation(live,d1);
                livePage();
                break;
            case R.id.match:
                navigation();
                changeNavigation(match,d3);
                matchPage();
                break;
            case R.id.mine:
                navigation();
                changeNavigation(mine,d4);
                minePage();
                break;
            default:
                break;
        }
    }

    private void navigation(){
        int c = this.getResources().getColor(R.color.hui);
        home.setTextColor(c);
        home.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d01, null, null);
        live.setTextColor(c);
        live.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d11, null, null);
        video.setTextColor(c);
        video.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d21, null, null);
        match.setTextColor(c);
        match.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d31, null, null);
        mine.setTextColor(c);
        mine.setCompoundDrawablesRelativeWithIntrinsicBounds(null, d41, null, null);
    }
    /**
     * 改变导航栏图标
     * @param rb
     * @param dra
     */
    private void changeNavigation(RadioButton rb,Drawable dra){
        rb.setTextColor(Color.RED);
        rb.setCompoundDrawablesRelativeWithIntrinsicBounds(null,dra,null,null);
    }
}
