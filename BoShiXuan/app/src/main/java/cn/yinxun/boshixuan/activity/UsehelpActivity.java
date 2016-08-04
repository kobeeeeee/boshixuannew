package cn.yinxun.boshixuan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.adapter.MainTabFragmentAdapter;
import cn.yinxun.boshixuan.fragment.ChargeFragment;
import cn.yinxun.boshixuan.fragment.FinancingFragment;
import cn.yinxun.boshixuan.fragment.WithdrawFragment;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by yangln10784 on 2016/7/30.
 */
public class UsehelpActivity extends BaseActivity {
    @Bind(R.id.tab_use)
    RadioGroup useGroup;
    @Bind(R.id.tab_charge)
    RadioButton chargeTab;
    @Bind(R.id.tab_financing)
    RadioButton financingTab;
    @Bind(R.id.tab_withdraw)
    RadioButton withdrawTab;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;

    private FragmentManager mFragmentManager;
    private ChargeFragment mChargeFragment;
    private FinancingFragment mFinancingFragment;
    private WithdrawFragment mWithdrawFragment;
    private List<Fragment> mFragmentList = new ArrayList<>();
    MainTabFragmentAdapter mTabFragmentAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usehelp);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        initFragments();
        initHeader();
    }
    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("使用帮助");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initFragments(){
        mChargeFragment = new ChargeFragment();
        mFinancingFragment = new FinancingFragment();
        mWithdrawFragment = new WithdrawFragment();

        mFragmentList.add(mChargeFragment);
        mFragmentList.add(mFinancingFragment);
        mFragmentList.add(mWithdrawFragment);
        chargeTab.setChecked(true);
        chargeTab.setTextColor(getResources().getColor(R.color.white));
        mTabFragmentAdapter = new MainTabFragmentAdapter(this, mFragmentList, R.id.main_tab, useGroup,5);

    }
}
