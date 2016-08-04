package cn.yinxun.boshixuan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.adapter.MainTabFragmentAdapter;
import cn.yinxun.boshixuan.R;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class NewsFragment extends BaseFragment{
    private PersonNewsFragment mPersonNewsFragment;
    private SystemNewsFragment mSystemNewsFragment;
    private List<Fragment> mFragmentList = new ArrayList<>();
    MainTabFragmentAdapter mTabFragmentAdapter;
    @Bind(R.id.systemNewsBtn)
    RadioButton mSystemNewsBtn;
    @Bind(R.id.tab_group)
    RadioGroup mTabGroup;
    private View mView;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, this.mView);
        return this.mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initHeader();
        initFragments();
    }
    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("消息");
    }
    private void initFragments() {
        mPersonNewsFragment = new PersonNewsFragment();
        mSystemNewsFragment = new SystemNewsFragment();

        mFragmentList.add(this.mSystemNewsFragment);
        mFragmentList.add(this.mPersonNewsFragment);
        this.mSystemNewsBtn.setChecked(true);
        this.mSystemNewsBtn.setTextColor(getResources().getColor(R.color.white));
        mTabFragmentAdapter = new MainTabFragmentAdapter(this, mFragmentList, R.id.main_tab, this.mTabGroup,3);
    }
}
