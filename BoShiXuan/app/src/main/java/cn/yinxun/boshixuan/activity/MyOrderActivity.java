package cn.yinxun.boshixuan.activity;

import android.os.Bundle;
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
import cn.yinxun.boshixuan.fragment.PayOrderFragment;
import cn.yinxun.boshixuan.fragment.UnPayOrderFragment;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class MyOrderActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.tab_group)
    RadioGroup mainGroup;
    @Bind(R.id.unOrderBtn)
    RadioButton mUnOrderBtn;
    private UnPayOrderFragment mUnPayOrderFragment;
    private PayOrderFragment mPayOrderFragment;
    private List<Fragment> mFragmentList = new ArrayList<>();
    MainTabFragmentAdapter mTabFragmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);
        initHeader();
        initFragments();
    }

    private void initHeader(){
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("我的订单");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initFragments() {
        mUnPayOrderFragment = new UnPayOrderFragment();
        mPayOrderFragment = new PayOrderFragment();

        mFragmentList.add(mUnPayOrderFragment);
        mFragmentList.add(mPayOrderFragment);
        mUnOrderBtn.setChecked(true);
        mUnOrderBtn.setTextColor(getResources().getColor(R.color.white));
        mTabFragmentAdapter = new MainTabFragmentAdapter(this, mFragmentList, R.id.main_tab, mainGroup,2);

    }
}
