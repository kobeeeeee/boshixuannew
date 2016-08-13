package cn.yinxun.boshixuan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by Administrator on 2016/7/13 0013.
 */
public class ModifyPayPswActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.modifyPayPsw)
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
        setContentView(R.layout.activity_modify_pay_psw);
        ButterKnife.bind(this);
        initHeader();
        this.mNetWorkCallBack = new NetWorkCallBack();
        initOnClickListener();
    }
    private void initHeader() {
        this.mHeader.setText("修改支付密码");
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
                    CommonUtil.showToast("请输入原密码",ModifyPayPswActivity.this);
                    return;
                }
                if(!password.equals(oldPsw)) {
                    CommonUtil.showToast("原密码输入有误",ModifyPayPswActivity.this);
                    return;
                }

                if(TextUtils.isEmpty(newPsw)) {
                    CommonUtil.showToast("请输入新密码",ModifyPayPswActivity.this);
                    return;
                }
                if(TextUtils.isEmpty(confirmPassword)) {
                    CommonUtil.showToast("请确认新密码",ModifyPayPswActivity.this);
                    return;
                }
                if(!newPsw.equals(confirmPassword)){
                    CommonUtil.showToast("两次输入不一致",ModifyPayPswActivity.this);
                    return;
                }
                boolean result=confirmPassword.matches("[0-9]+");
                if(!result || confirmPassword.length() != 6) {
                    CommonUtil.showToast("请输入六位纯数字密码",ModifyPayPswActivity.this);
                    return;
                }
                String phoneNo = userInfoBean.getCustMobile();
                //传入参数
                Map<String,Object> map = new HashMap<>();
                map.put("passwd_type","2");
                map.put("modify_type","1");
                map.put("old_passwd",oldPsw);
                map.put("new_passwd",newPsw);
                map.put("user_phone",phoneNo);
                RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_MODIFY_PWD,map,ModifyPayPswActivity.this.mNetWorkCallBack, ModifyPswResponse.class);
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
                Toast toast = Toast.makeText(ModifyPayPswActivity.this,"密码修改成功",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finish();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,ModifyPayPswActivity.this);
        }
    }
}
