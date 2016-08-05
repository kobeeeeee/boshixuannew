package cn.yinxun.boshixuan.activity;

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
import cn.yinxun.boshixuan.network.VerifyCodeResponse;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.ModifyPswResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class ForgetPayPswActivity extends BaseActivity{
    public static final int TYPE_SEND_VERIFY_CODE = 1;
    public static final int TYPE_MODIFY_PASSWORD = 2;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.phoneNoText)
    EditText mPhoneNoText;
    @Bind(R.id.modifyPayPsw)
    ImageView mModifyBtn;
    @Bind(R.id.verifyCodeText)
    EditText verifyCodeText;
    @Bind(R.id.newPswText)
    EditText mNewPswText;
    @Bind(R.id.confirmPswText)
    EditText mConfirmPswText;
    @Bind(R.id.buttonCode)
    Button mCodeBtn;
    public NetWorkCallBack mNetWorkCallBack;
    private TimeCount time;
    private String mVerifyCode="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pay_psw);
        ButterKnife.bind(this);
        initHeader();
        time = new TimeCount(60000, 1000);
        this.mNetWorkCallBack = new NetWorkCallBack();
        this.mModifyBtn.setOnClickListener(new MyClickListener(TYPE_MODIFY_PASSWORD))
        ;this.mCodeBtn.setOnClickListener(new MyClickListener(TYPE_SEND_VERIFY_CODE));
    }
    private void initHeader() {
        this.mHeader.setText("忘记支付密码");
        this.mHeader.setVisibility(View.VISIBLE);
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void modifyClick() {
        String phoneNo = mPhoneNoText.getText().toString();
        String newPsw = mNewPswText.getText().toString();
        String confirmPassword = mConfirmPswText.getText().toString();
        String verifyCode = verifyCodeText.getText().toString();
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String phoneNumber = userInfoBean.getCustMobile();
        if(TextUtils.isEmpty(phoneNo)) {
            CommonUtil.showToast("请输入手机号",ForgetPayPswActivity.this);
            return;
        }
        if(!phoneNo.equals(phoneNumber)) {
            CommonUtil.showToast("手机号码输入有误",ForgetPayPswActivity.this);
            return;
        }
        if(TextUtils.isEmpty(newPsw)) {
            CommonUtil.showToast("请输入新密码",ForgetPayPswActivity.this);
            return;
        }
        if(TextUtils.isEmpty(confirmPassword)) {
            CommonUtil.showToast("请输入确认密码",ForgetPayPswActivity.this);
            return;
        }
        if(!newPsw.equals(confirmPassword)){
            CommonUtil.showToast("两次输入密码不一致",ForgetPayPswActivity.this);
            return;
        }
        if(TextUtils.isEmpty(verifyCode)) {
            CommonUtil.showToast("请输入验证码",ForgetPayPswActivity.this);
            return;
        }
        boolean result=confirmPassword.matches("[0-9]+");
        if(!result || confirmPassword.length() != 6) {
            CommonUtil.showToast("请输入六位纯数字密码",ForgetPayPswActivity.this);
            return;
        }
        if(!verifyCode.equals(ForgetPayPswActivity.this.mVerifyCode)) {
            CommonUtil.showToast("验证码错误，请重新输入",ForgetPayPswActivity.this);
            return;
        }
        //传入参数
        Map<String,Object> map = new HashMap<>();
        map.put("passwd_type","2");
        map.put("modify_type","2");
        map.put("old_passwd",verifyCode);
        map.put("new_passwd",newPsw);
        map.put("user_phone",phoneNo);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_MODIFY_PWD,map,ForgetPayPswActivity.this.mNetWorkCallBack, ModifyPswResponse.class);

    }
    private void sendVerifyCode(){
        String phoneNo = mPhoneNoText.getText().toString();
        Map<String,Object> map = new HashMap<>();
        map.put("user_phone",phoneNo);
        if(TextUtils.isEmpty(phoneNo)) {
            CommonUtil.showToast("请输入手机号码",ForgetPayPswActivity.this);
            return;
        }
        if(!CommonUtil.checkPhoneNumber(phoneNo)) {
            CommonUtil.showToast("无效的手机号码",ForgetPayPswActivity.this);
            return;
        }
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_VERIFY_CODE,map,ForgetPayPswActivity.this.mNetWorkCallBack, VerifyCodeResponse.class);
        time.start();
    }
    private class NetWorkCallBack implements RequestListener {

        @Override
        public void onBegin() {

        }

        @Override
        public void onResponse(Object object) {
            if(object == null) {
                return;
            }
            if (object instanceof VerifyCodeResponse) {
                VerifyCodeResponse registerResponse = (VerifyCodeResponse)object;
                LogUtil.i(this,"registerResponse = " + registerResponse);
                ForgetPayPswActivity.this.mVerifyCode = registerResponse.verify_code;
            }
            if(object instanceof ModifyPswResponse) {
                ModifyPswResponse modifyPswResponse = (ModifyPswResponse)object;
                LogUtil.i(this,"modifyPswResponse = " + modifyPswResponse);
                CommonUtil.showToast("密码修改成功",ForgetPayPswActivity.this);
                finish();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,ForgetPayPswActivity.this);
        }
    }
    class MyClickListener implements View.OnClickListener {
        private int mType;
        public MyClickListener(int type) {
            this.mType = type;
        }
        @Override
        public void onClick(View view) {
            switch (this.mType) {
                case TYPE_SEND_VERIFY_CODE:
                    sendVerifyCode();
                    break;
                case TYPE_MODIFY_PASSWORD:
                    modifyClick();
                    break;
            }
        }
    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            mCodeBtn.setText("获取验证码");
            mCodeBtn.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            mCodeBtn.setClickable(false);//防止重复点击
            mCodeBtn.setText(millisUntilFinished / 1000 + "秒");
        }
    }
}
