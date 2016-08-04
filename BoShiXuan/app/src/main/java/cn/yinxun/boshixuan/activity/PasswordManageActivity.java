package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/10 0010.
 */
public class PasswordManageActivity extends BaseActivity{
    public static final int TYPE_MODIFY_LOGIN_PSW = 0;
    public static final int TYPE_MODIFY_PAY_PSW = 1;
    public static final int TYPE_FORGET_PAY_PSW = 2;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.modifyLoginPswLayout)
    RelativeLayout mModifyLoginPswLayout;
    @Bind(R.id.modifyPayPswLayout)
    RelativeLayout mModifyPayPswLayout;
    @Bind(R.id.forgetPayPswLayout)
    RelativeLayout mForgetPayPswLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manage);
        ButterKnife.bind(this);
        initHeader();
        initOnClick();
    }
    private void initHeader(){
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("修改密码");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initOnClick() {
        this.mModifyLoginPswLayout.setOnTouchListener(new MyOnTouchListener(TYPE_MODIFY_LOGIN_PSW));
        this.mModifyPayPswLayout.setOnTouchListener(new MyOnTouchListener(TYPE_MODIFY_PAY_PSW));
        this.mForgetPayPswLayout.setOnTouchListener(new MyOnTouchListener(TYPE_FORGET_PAY_PSW));
    }
    class MyOnTouchListener implements View.OnTouchListener {
        private int mType;
        public MyOnTouchListener(int type) {
            this.mType = type;
        }
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    setLayoutBg();
                    break;
                case MotionEvent.ACTION_UP:
                    PasswordManageActivity.this.mModifyLoginPswLayout.setBackgroundResource(R.color.grid_gray);
                    PasswordManageActivity.this.mModifyPayPswLayout.setBackgroundResource(R.color.grid_gray);
                    PasswordManageActivity.this.mForgetPayPswLayout.setBackgroundResource(R.color.grid_gray);

                    onClickEvent();
                    break;
            }
            return true;
        }

        private void onClickEvent() {
            Intent intent;
            UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
            String is_verify = userInfoBean.getIsVerity();
            switch (this.mType) {
                case TYPE_MODIFY_LOGIN_PSW:
                    intent = new Intent(PasswordManageActivity.this,ModifyLoginPswActivity.class);
                    startActivity(intent);
                    break;
                case TYPE_MODIFY_PAY_PSW:
                    if(is_verify.equals("0")) {
                        CommonUtil.showToast("请先实名认证",PasswordManageActivity.this);
                        break;
                    }
                    intent = new Intent(PasswordManageActivity.this,ModifyPayPswActivity.class);
                    startActivity(intent);
                    break;
                case TYPE_FORGET_PAY_PSW:
                    if(is_verify.equals("0")) {
                        CommonUtil.showToast("请先实名认证",PasswordManageActivity.this);
                        break;
                    }
                    intent = new Intent(PasswordManageActivity.this,ForgetPayPswActivity.class);
                    startActivity(intent);
                    break;
            }
        }

        private void setLayoutBg() {
            switch (this.mType) {
                    case TYPE_MODIFY_LOGIN_PSW:
                        PasswordManageActivity.this.mModifyLoginPswLayout.setBackgroundResource(R.color.bank_add_gray);
                        PasswordManageActivity.this.mModifyPayPswLayout.setBackgroundResource(R.color.grid_gray);
                        PasswordManageActivity.this.mForgetPayPswLayout.setBackgroundResource(R.color.grid_gray);
                        break;
                    case TYPE_MODIFY_PAY_PSW:
                        PasswordManageActivity.this.mModifyLoginPswLayout.setBackgroundResource(R.color.grid_gray);
                        PasswordManageActivity.this.mModifyPayPswLayout.setBackgroundResource(R.color.bank_add_gray);
                        PasswordManageActivity.this.mForgetPayPswLayout.setBackgroundResource(R.color.grid_gray);
                        break;
                    case TYPE_FORGET_PAY_PSW:
                        PasswordManageActivity.this.mModifyLoginPswLayout.setBackgroundResource(R.color.grid_gray);
                        PasswordManageActivity.this.mModifyPayPswLayout.setBackgroundResource(R.color.grid_gray);
                        PasswordManageActivity.this.mForgetPayPswLayout.setBackgroundResource(R.color.bank_add_gray);
                        break;
            }
        }

    }
}
