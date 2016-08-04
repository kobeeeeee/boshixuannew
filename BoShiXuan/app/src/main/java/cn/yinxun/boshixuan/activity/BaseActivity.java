package cn.yinxun.boshixuan.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class BaseActivity extends FragmentActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        LogUtil.i(this,"onCreate");
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
