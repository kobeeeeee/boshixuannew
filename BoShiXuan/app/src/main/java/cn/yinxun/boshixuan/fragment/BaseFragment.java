package cn.yinxun.boshixuan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class BaseFragment extends Fragment{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(this,"onCreate");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.i(this,"onActivityCreated");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.i(this,"onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(this,"onResume");

    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.i(this,"onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(this,"onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.i(this,"onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(this,"onDestroy");
    }
}
