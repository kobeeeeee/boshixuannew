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
import cn.yinxun.boshixuan.network.model.ModifyPswResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class ModifyLoginPswActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.modifyLoginPsw)
    ImageView mModifyBtn;
    @Bind(R.id.oldPswText)
    EditText mOldPswText;
    @Bind(R.id.newPswText)
    EditText mNewPswText;
    @Bind(R.id.confirmPswText)
    EditText mConfirmPswText;
    public NetWorkCallBack mNetWorkCallBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_login_psw);
        ButterKnife.bind(this);
        initHeader();
        this.mNetWorkCallBack = new NetWorkCallBack();
        initOnClickListener();
    }
    private void initHeader() {
        this.mHeader.setText("修改登录密码");
        this.mHeader.setVisibility(View.VISIBLE);
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initOnClickListener() {
        this.mModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPsw = mOldPswText.getText().toString();
                String newPsw = mNewPswText.getText().toString();
                String confirmPassword = mConfirmPswText.getText().toString();
                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                String password = userInfoBean.getPassword();
                if(TextUtils.isEmpty(oldPsw)) {
                    CommonUtil.showToast("请输入原密码",ModifyLoginPswActivity.this);
                    return;
                }
                if(!password.equals(oldPsw)) {
                    CommonUtil.showToast("原密码输入有误",ModifyLoginPswActivity.this);
                    return;
                }

                if(TextUtils.isEmpty(newPsw)) {
                    CommonUtil.showToast("请输入新密码",ModifyLoginPswActivity.this);
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)) {
                    CommonUtil.showToast("请输入确认密码",ModifyLoginPswActivity.this);
                    return;
                }
                if(!newPsw.equals(confirmPassword)){
                    CommonUtil.showToast("两次输入密码不一致",ModifyLoginPswActivity.this);
                    return;
                }
//                if(!CommonUtil.checkPassword(confirmPassword)){
//                    CommonUtil.showToast("密码长度为6-20位字母或有效数字组成",ModifyLoginPswActivity.this);
//                    return;
//                }

                String phoneNo = userInfoBean.getCustMobile();
                //传入参数
                Map<String,Object> map = new HashMap<>();
                map.put("passwd_type","1");
                map.put("modify_type","1");
                map.put("old_passwd",CommonUtil.md5(oldPsw));
                map.put("new_passwd",CommonUtil.md5(newPsw));
                map.put("user_phone",phoneNo);
                RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_MODIFY_PWD,map,ModifyLoginPswActivity.this.mNetWorkCallBack, ModifyPswResponse.class);
            }
        });
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
            if(object instanceof ModifyPswResponse) {
                ModifyPswResponse modifyPswResponse = (ModifyPswResponse)object;
                LogUtil.i(this,"modifyPswResponse = " + modifyPswResponse);
                CommonUtil.showToast("密码修改成功",ModifyLoginPswActivity.this);
                finish();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,ModifyLoginPswActivity.this);
        }
    }
}
