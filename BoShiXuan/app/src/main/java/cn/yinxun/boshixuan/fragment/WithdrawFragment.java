package cn.yinxun.boshixuan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;

/**
 * Created by yangln10784 on 2016/7/30.
 */
public class WithdrawFragment extends BaseFragment {
    private View mView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_withdraw, container, false);
        ButterKnife.bind(this, this.mView);
        return this.mView;
    }
}
