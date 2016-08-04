package cn.yinxun.boshixuan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
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
import cn.yinxun.boshixuan.network.model.CertificationResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/10 0010.
 */
public class CertificationActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.realNameText)
    EditText mRealNameText;
    @Bind(R.id.identityText)
    EditText mIdentityText;
    @Bind(R.id.payPswText)
    EditText mPayPswText;
    @Bind(R.id.confirmBtn)
    ImageView mConfirmBtn;
    private NetWorkCallBack mNetWorkCallBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certication);
        ButterKnife.bind(this);
        initHeader();
        initClick();
    }
    private void initHeader(){
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("实名认证");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initClick() {
        this.mNetWorkCallBack = new NetWorkCallBack();
        this.mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmClick();
            }
        });
    }
    private void confirmClick() {
        String realName = this.mRealNameText.getText().toString();
        String payPsw = this.mPayPswText.getText().toString();
        String identity = this.mIdentityText.getText().toString();
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String name = userInfoBean.getUserName();
        if(TextUtils.isEmpty(realName)) {
            CommonUtil.showToast("请输入真实姓名",CertificationActivity.this);
            return;
        }
        if(TextUtils.isEmpty(identity)) {
            CommonUtil.showToast("请输入身份证号码",CertificationActivity.this);
            return;
        }
        if(identity.length() != 18) {
            CommonUtil.showToast("身份证号码输入不正确",CertificationActivity.this);
            return;
        }
        if(TextUtils.isEmpty(payPsw)) {
            CommonUtil.showToast("请输入支付密码",CertificationActivity.this);
            return;
        }
        boolean result=payPsw.matches("[0-9]+");
        if(!result || payPsw.length() != 6) {
            CommonUtil.showToast("请输入六位纯数字密码",CertificationActivity.this);
            return;
        }
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();

        //传入参数
        Map<String,Object> map = new HashMap<>();
        map.put("user_id",userId);
        map.put("user_name",realName);
        map.put("idcard_num",identity);
        map.put("pay_passwd",payPsw);
        map.put("user_phone",userPhone);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_REAL_NAME,map,CertificationActivity.this.mNetWorkCallBack, CertificationResponse.class);
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
            if(object instanceof CertificationResponse) {
                CertificationResponse certificationResponse = (CertificationResponse)object;
                LogUtil.i(this,"certificationResponse = " + certificationResponse);
                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                userInfoBean.setIsVerrity("2");

                String realName = CertificationActivity.this.mRealNameText.getText().toString();
                userInfoBean.setUserName(realName);
                String identity = CertificationActivity.this.mIdentityText.getText().toString();
                userInfoBean.setIdentity(identity);
                CertificationActivity.this.finish();
                CommonUtil.showToast("实名认证成功",CertificationActivity.this);

            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,CertificationActivity.this);
        }
    }
}
