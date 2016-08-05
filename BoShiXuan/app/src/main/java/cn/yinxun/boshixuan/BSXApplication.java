package cn.yinxun.boshixuan;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class BSXApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init(this);
        initUserInfoBean();
    }
    /**
     * 当进入登录画面时，初始化userBean的信息
     */
    public void initUserInfoBean() {
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        userInfoBean.setSysType("1");

        String systemType = Build.VERSION.RELEASE;
        userInfoBean.setSysVersion(systemType);

        PackageManager manager;
        String applicationVersion = "";
        manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(),0);
            applicationVersion = String.valueOf(info.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        userInfoBean.setAppVersion(applicationVersion);

        String no = android.os.Build.VERSION.RELEASE;
        userInfoBean.setSysTerNo(no);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
