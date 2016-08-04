package cn.yinxun.boshixuan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import cn.yinxun.boshixuan.fragment.CurrentFragment;
import cn.yinxun.boshixuan.fragment.RegularFragment;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class DepositActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.tab_group)
    RadioGroup mainGroup;
    @Bind(R.id.regularBtn)
    RadioButton mRegularBtn;
    private RegularFragment mRegularFragment;
    private CurrentFragment mCurrentFragment;
    private List<Fragment> mFragmentList = new ArrayList<>();
    MainTabFragmentAdapter mTabFragmentAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        ButterKnife.bind(this);
        initHeader();
        initFragments();
    }
    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("押金宝");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initFragments() {
        mRegularFragment = new RegularFragment();
        mCurrentFragment = new CurrentFragment();

        mFragmentList.add(mRegularFragment);
        mFragmentList.add(mCurrentFragment);
        mRegularBtn.setChecked(true);
        mRegularBtn.setTextColor(getResources().getColor(R.color.white));
        mTabFragmentAdapter = new MainTabFragmentAdapter(this, mFragmentList, R.id.main_tab, mainGroup,4);

    }
}
