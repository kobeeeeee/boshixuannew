package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RegisterResponse;
import cn.yinxun.boshixuan.network.VerifyCodeResponse;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by yangln on 2016/7/3.
 */
public class RegisterActivity extends BaseActivity {
    public static final int TYPE_SEND_VERIFY_CODE = 1;
    public static final int TYPE_REGISTER = 2;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.inputPhone)
    EditText phoneEditText;
    @Bind(R.id.inputPassword)
    EditText passwordEditText;
    @Bind(R.id.confirmPassword)
    EditText confirmPasswordEditText;
    @Bind(R.id.inputCode)
    EditText CodeEditText;
    @Bind(R.id.buttonCode)
    Button codeButton;
    @Bind(R.id.checkAgree)
    CheckBox checkAgreeBox;
    @Bind(R.id.register)
    ImageView registerButton;
    public NetWorkCallBack mNetWorkCallBack;
    private TimeCount time;
    private String mVerifyCode="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initHeader();
        codeButton.setText("获取验证码");
        codeButton.setTextColor(Color.WHITE);
        codeButton.setTextSize(15);
        time = new TimeCount(60000, 1000);
        codeButton.setOnClickListener(new MyOnClickListener(TYPE_SEND_VERIFY_CODE));
        registerButton.setOnClickListener(new MyOnClickListener(TYPE_REGISTER));
        this.mNetWorkCallBack = new NetWorkCallBack();
    }
    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("注册");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    class MyOnClickListener implements  View.OnClickListener {
        public int mType;
        public MyOnClickListener(int type) {
            this.mType = type;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            String phoneNumber = phoneEditText.getText().toString();
            switch (this.mType) {
                case TYPE_SEND_VERIFY_CODE:
                    LogUtil.i(this,phoneNumber + " = test");
                    Map<String,Object> map = new HashMap<>();
                    map.put("user_phone",phoneNumber);
                    if(TextUtils.isEmpty(phoneNumber)) {
                        CommonUtil.showToast("请输入手机号码",RegisterActivity.this);
                        break;
                    }
                    if(!CommonUtil.checkPhoneNumber(phoneNumber)) {
                        CommonUtil.showToast("无效的手机号码",RegisterActivity.this);
                        break;
                    }
                    RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_VERIFY_CODE,map,RegisterActivity.this.mNetWorkCallBack, VerifyCodeResponse.class);

                    time.start();
                    break;
                case TYPE_REGISTER:
                    String password = passwordEditText.getText().toString();
                    String confirmPassword = confirmPasswordEditText.getText().toString();
                    String code = CodeEditText.getText().toString();
                    LogUtil.i(this,"register");
                    LogUtil.i(this,phoneNumber+"test");
                    LogUtil.i(this,password+"test1");
                    LogUtil.i(this,confirmPassword+"test2");
                    if(TextUtils.isEmpty(phoneNumber)) {
                        CommonUtil.showToast("请输入手机号码",RegisterActivity.this);
                        break;
                    }
                    if(!CommonUtil.checkPhoneNumber(phoneNumber)) {
                        CommonUtil.showToast("无效的手机号码",RegisterActivity.this);
                        break;
                    }
                    if(TextUtils.isEmpty(password)) {
                        CommonUtil.showToast("请输入密码",RegisterActivity.this);
                        break;
                    }
                    if(TextUtils.isEmpty(confirmPassword)) {
                        CommonUtil.showToast("请输入确认密码",RegisterActivity.this);
                        break;
                    }

                    if(TextUtils.isEmpty(code)) {
                        CommonUtil.showToast("验证码不能为空",RegisterActivity.this);
                        break;
                    }
                    if(!CommonUtil.checkPassword(password)){
                        CommonUtil.showToast("密码长度为6-20位字母或有效数字组成",RegisterActivity.this);
                        break;
                    }
                    if(!password.equals(confirmPassword)){
                        CommonUtil.showToast("两次输入密码不一致",RegisterActivity.this);
                        break;
                    }
                    if(!checkAgreeBox.isChecked()){
                        CommonUtil.showToast("请先同意支付协议",RegisterActivity.this);
                        break;
                    }

                    if(!code.equals(RegisterActivity.this.mVerifyCode)) {
                        CommonUtil.showToast("验证码错误，请重新输入",RegisterActivity.this);
                        break;
                    }
                    String encryptPasswd = CommonUtil.md5(password);
                    LogUtil.i(this,"注册加密密码 = " + encryptPasswd);
                    Map<String,Object> mapRegister = new HashMap<>();
                    mapRegister.put("user_phone",phoneNumber);
                    mapRegister.put("user_passwd",encryptPasswd);
                    RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_REGISTER,mapRegister,RegisterActivity.this.mNetWorkCallBack, RegisterResponse.class);
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
            LogUtil.i(this,"test onResponse");
            if(object == null){
                return;
            }
            if (object instanceof VerifyCodeResponse) {
                VerifyCodeResponse verifyCodeResponse = (VerifyCodeResponse)object;
                LogUtil.i(this,"verifyCodeResponse = " + verifyCodeResponse);
                CommonUtil.showToast("获取验证码成功",RegisterActivity.this);
                RegisterActivity.this.mVerifyCode = verifyCodeResponse.verify_code;
            }
            if(object instanceof RegisterResponse) {
                RegisterResponse registerResponse = (RegisterResponse)object;
                LogUtil.i(this,"registerResponse = " + registerResponse);
                String password = passwordEditText.getText().toString();

                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                userInfoBean.setPassword(password);
                CommonUtil.showToast("注册成功",RegisterActivity.this);
                finish();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,RegisterActivity.this);
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            codeButton.setText("获取验证码");
            codeButton.setClickable(true);
            codeButton.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            codeButton.setEnabled(false);
            codeButton.setClickable(false);//防止重复点击
            codeButton.setText(millisUntilFinished / 1000 + "秒");
        }
    }
}

