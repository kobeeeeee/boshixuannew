package cn.yinxun.boshixuan.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.VerifyCodeResponse;
import cn.yinxun.boshixuan.network.model.ModifyPswResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by yangln10784 on 2016/7/9.
 */
public class PasswordActivity extends BaseActivity {
    public static final int TYPE_MODIFY = 1;
    public static final int TYPE_GETCODE =2;
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
    @Bind(R.id.modify)
    ImageView modifyButton;
    public NetWorkCallBack mNetWorkCallBack;
    private TimeCount time;
    private String mVerifyCode="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        ButterKnife.bind(this);
        initHeader();
        codeButton.setText("获取验证码");
        codeButton.setTextColor(Color.WHITE);
        codeButton.setTextSize(15);
        time = new TimeCount(60000, 1000);
        modifyButton.setOnClickListener(new MyOnClickListener(TYPE_MODIFY));
        codeButton.setOnClickListener(new MyOnClickListener(TYPE_GETCODE));
        this.mNetWorkCallBack = new NetWorkCallBack();
    }


    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("找回密码");
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
            String phoneNumber = phoneEditText.getText().toString();
            switch (mType){
                case TYPE_MODIFY:
                    String password = passwordEditText.getText().toString();
                    String confirmPassword = confirmPasswordEditText.getText().toString();
                    String code = CodeEditText.getText().toString();
                    if(TextUtils.isEmpty(phoneNumber)) {
                        CommonUtil.showToast("请输入手机号码",PasswordActivity.this);
                        break;
                    }
                    if(!CommonUtil.checkPhoneNumber(phoneNumber)) {
                        CommonUtil.showToast("无效的手机号码",PasswordActivity.this);
                        break;
                    }
                    if(TextUtils.isEmpty(password)) {
                        CommonUtil.showToast("请输入密码",PasswordActivity.this);
                        break;
                    }
                    if(!CommonUtil.checkPassword(password)){
                        CommonUtil.showToast("密码长度为6-20位字母或有效数字组成",PasswordActivity.this);
                        break;
                    }
                    if(TextUtils.isEmpty(confirmPassword)) {
                        CommonUtil.showToast("请输入确认密码",PasswordActivity.this);
                        break;
                    }
                    if(!password.equals(confirmPassword)){
                        CommonUtil.showToast("两次输入密码不一致",PasswordActivity.this);
                        break;
                    }
                    if(TextUtils.isEmpty(code)) {
                        CommonUtil.showToast("验证码不能为空",PasswordActivity.this);
                        break;
                    }

                    if(!code.equals(PasswordActivity.this.mVerifyCode)) {
                        CommonUtil.showToast("验证码错误，请重新输入",PasswordActivity.this);
                        break;
                    }

                    String encryptNewPasswd = CommonUtil.md5(password);
                    LogUtil.i(this,"修改密码加密密码 = " + encryptNewPasswd);

                    UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                    Map<String,Object> mapModify = new HashMap<>();
                    mapModify.put("passwd_type","1");
                    mapModify.put("modify_type","2");
                    mapModify.put("old_passwd",code);
                    mapModify.put("new_passwd",encryptNewPasswd);
                    mapModify.put("user_phone",userInfoBean.getCustMobile());
                    RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_MODIFY_PWD,mapModify,PasswordActivity.this.mNetWorkCallBack, ModifyPswResponse.class);

                case TYPE_GETCODE:

                    if(TextUtils.isEmpty(phoneNumber)) {
                        CommonUtil.showToast("请输入手机号码",PasswordActivity.this);
                        break;
                    }
                    if(!CommonUtil.checkPhoneNumber(phoneNumber)) {
                        CommonUtil.showToast("无效的手机号码",PasswordActivity.this);
                        break;
                    }
                    Map<String,Object> map = new HashMap<>();
                    map.put("user_phone",phoneNumber);
                    RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_VERIFY_CODE,map,PasswordActivity.this.mNetWorkCallBack, VerifyCodeResponse.class);

                    time.start();
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

                CommonUtil.showToast("获取验证码成功",PasswordActivity.this);

                PasswordActivity.this.mVerifyCode = verifyCodeResponse.verify_code;
            }
            else if(object instanceof ModifyPswResponse) {
                ModifyPswResponse modifyPswResponse = (ModifyPswResponse)object;
                LogUtil.i(this,"modifyPswResponse = " + modifyPswResponse);
                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                String password = passwordEditText.getText().toString();
                userInfoBean.setPassword(password);

                CommonUtil.showToast("登录密码修改成功",PasswordActivity.this);
                finish();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,PasswordActivity.this);
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
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            codeButton.setClickable(false);//防止重复点击
            codeButton.setText(millisUntilFinished / 1000 + "秒");
        }
    }
}
