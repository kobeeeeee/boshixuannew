package cn.yinxun.boshixuan.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.adapter.MainTabFragmentAdapter;
import cn.yinxun.boshixuan.fragment.HomeFragment;
import cn.yinxun.boshixuan.fragment.NewsFragment;
import cn.yinxun.boshixuan.fragment.SettingFragment;
import cn.yinxun.boshixuan.fragment.WalletFragment;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

public class MainActivity extends BaseActivity {
    @Bind(R.id.tab_group)
    RadioGroup mainGroup;
    @Bind(R.id.tab_home)
    RadioButton homeTab;
    @Bind(R.id.tab_news)
    RadioButton newsTab;
    @Bind(R.id.tab_wallet)
    RadioButton walletTab;
    @Bind(R.id.tab_setting)
    RadioButton settingTab;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private HomeFragment mHomeFragment;
    private NewsFragment mNewsFragment;
    private SettingFragment mSettingFragment;
    private WalletFragment mWalletFragment;
    MainTabFragmentAdapter mTabFragmentAdapter;
    private long time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        initFragments();
    }
    private void initFragments() {
        mHomeFragment = new HomeFragment();
        mNewsFragment = new NewsFragment();
        mSettingFragment = new SettingFragment();
        mWalletFragment = new WalletFragment();

        mFragmentList.add(mHomeFragment);
        mFragmentList.add(mNewsFragment);
        mFragmentList.add(mWalletFragment);
        mFragmentList.add(mSettingFragment);
        homeTab.setChecked(true);
        homeTab.setTextColor(getResources().getColor(R.color.coffee));
        mTabFragmentAdapter = new MainTabFragmentAdapter(this, mFragmentList, R.id.main_tab, mainGroup,1);

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            if (System.currentTimeMillis() - time < 3000) {
                finish();
                return false;
            } else {
                time = System.currentTimeMillis();
                CommonUtil.showToast("再按一次退出程序",MainActivity.this);
            }
        } else {
            return false;
        }
        return true;
    }
}
