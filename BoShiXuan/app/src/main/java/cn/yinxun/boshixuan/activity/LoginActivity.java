package cn.yinxun.boshixuan.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.LoginResponse;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

public class LoginActivity extends BaseActivity {
    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_FORGET = 2;
    public static final int TYPE_REGISTER = 3;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    @Bind(R.id.input_user)
    EditText userEditText;
    @Bind(R.id.input_password)
    EditText passwordEditText;
    @Bind(R.id.login_press)
    ImageView loginButton;
    @Bind(R.id.login_forget)
    TextView forgetTextView;
    @Bind(R.id.login_register)
    TextView registerTextView;
    public NetWorkCallBack mNetWorkCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initUserInfoBean();
        LogUtil.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        preferences = getSharedPreferences("firststart", Context.MODE_PRIVATE);
        if(preferences.getBoolean("firststart",true))
        {
            LogUtil.i(this,"logintest1");
            editor = preferences.edit();
            editor.putBoolean("firststart",false);
            editor.commit();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),StartupActivity.class);
            startActivity(intent);
            this.finish();
        }
        else
        {
            LogUtil.i(this,"logintest2");
            UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
            String mobile = userInfoBean.getCustMobile();
            LogUtil.i(this,"login phone = " + mobile);
            userEditText.setText(mobile);
        }
        registerTextView.setOnClickListener(new MyOnClickListener(TYPE_REGISTER));
        loginButton.setOnClickListener(new MyOnClickListener(TYPE_LOGIN));
        forgetTextView.setOnClickListener(new MyOnClickListener(TYPE_FORGET));
       this.mNetWorkCallBack = new NetWorkCallBack();
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
    class MyOnClickListener implements  View.OnClickListener {
        public int mType;
        public MyOnClickListener(int type) {
            this.mType = type;
        }
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (this.mType) {
                case TYPE_LOGIN:
                    String phoneNumber = userEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    if(TextUtils.isEmpty(phoneNumber)) {
                        CommonUtil.showToast("请输入手机号码",LoginActivity.this);
                        break;
                    }
                    if(!CommonUtil.checkPhoneNumber(phoneNumber)) {
                        CommonUtil.showToast("无效的手机号码",LoginActivity.this);
                        break;
                    }
                    if(TextUtils.isEmpty(password)) {
                        CommonUtil.showToast("请输入密码",LoginActivity.this);
                        break;
                    }
                    if(!CommonUtil.checkPassword(password)){
                        CommonUtil.showToast("密码长度为6-20位字母或有效数字组成",LoginActivity.this);
                        break;
                    }
                    String encryptPasswd = CommonUtil.md5(password);
                    LogUtil.i(this,"登录加密密码 = " + encryptPasswd);
                    //根据输入的用户名、密码调用接口校验用户名密码是否正确
                    Map<String,Object> map = new HashMap<>();
                    map.put("user_phone",phoneNumber);
                    map.put("user_passwd",encryptPasswd);
                    RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_USER_LOGIN,map,LoginActivity.this.mNetWorkCallBack,LoginResponse.class);

//                    //TODO 测试登录用
//                    intent = new Intent(LoginActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    LoginActivity.this.finish();
                    break;
                case TYPE_FORGET:
                    intent = new Intent(LoginActivity.this,PasswordActivity.class);
                    startActivity(intent);
                    break;
                case TYPE_REGISTER:
                    intent = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private class NetWorkCallBack implements RequestListener {

        @Override
        public void onBegin() {

        }

        @Override
        public void onResponse(Object object) {
            if(object == null){
                return;
            }
            if (object instanceof LoginResponse) {
                LoginResponse loginResponse = (LoginResponse)object;
                LogUtil.i(this,"loginResponse = " + loginResponse);
                CommonUtil.showToast("登录成功",LoginActivity.this);
                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                userInfoBean.setCustId(loginResponse.user_id);
                userInfoBean.setCustMobile(loginResponse.user_phone);
                userInfoBean.setUserName(loginResponse.user_name);
                LogUtil.i(this,"userphone = " + loginResponse.user_phone);
                String password = passwordEditText.getText().toString();
                userInfoBean.setPassword(password);
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,LoginActivity.this);
        }
    }

}
