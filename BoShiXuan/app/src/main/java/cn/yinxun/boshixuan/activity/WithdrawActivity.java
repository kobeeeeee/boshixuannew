package cn.yinxun.boshixuan.activity;

import android.content.Intent;
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
import cn.yinxun.boshixuan.network.model.AccountBalanceResponse;
import cn.yinxun.boshixuan.network.model.WithDrawResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.view.PaypswDialog;
//import www.chendanfeng.com.view.PaypswDialog;

/**
 * Created by Administrator on 2016/7/6 0006.
 */
public class WithdrawActivity extends BaseActivity {
    public static final int SELECT_CARD = 100;
    public static final int TYPE_SELECT = 1;
    public static final int TYPE_WITHDRAW = 2;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.arrowLayout)
    RelativeLayout mArrowLayout;
    @Bind(R.id.withdrawBtn)
    ImageView mWithdrawBtn;
    @Bind(R.id.bankNo)
    TextView mBankNo;
    @Bind(R.id.bankName)
    TextView mBankName;
    @Bind(R.id.balanceInput)
    EditText mBalanceInput;
    @Bind(R.id.accountBalanceText)
    TextView mAccountBalanceText;
    private NetWorkCallBack mNetWorkCallBack;
    private String mBankId = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        initHeader();
        initOnClick();
        getData();
    }
    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("提现");

        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initOnClick() {
        this.mBankName.setOnClickListener(new MyOnClickListener(TYPE_SELECT));
        this.mArrowLayout.setOnClickListener(new MyOnClickListener(TYPE_SELECT));
        this.mWithdrawBtn.setOnClickListener(new MyOnClickListener(TYPE_WITHDRAW));
        this.mNetWorkCallBack = new NetWorkCallBack();
    }
    class MyOnClickListener implements  View.OnClickListener{
        private int mType;
        public MyOnClickListener(int type) {
            this.mType = type;
        }
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (this.mType) {
                case TYPE_SELECT:
                    intent = new Intent(WithdrawActivity.this,BankCardSelectActivity.class);
                    intent.putExtra("type",0);
                    startActivityForResult(intent,SELECT_CARD);
                    break;
                case TYPE_WITHDRAW:
                    confirmWithDraw();
                    break;
            }
        }
    }
    private void confirmWithDraw() {
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        final String userId = userInfoBean.getCustId();
        final String userPhone = userInfoBean.getCustMobile();
      //  final String payPsw = userInfoBean.getPayPsw();
        final String fetchMoney = this.mBalanceInput.getText().toString();
        final String cardNumber = this.mBankNo.getText().toString();
        final String bankNumber = this.mBankName.getText().toString();
        if(TextUtils.isEmpty(cardNumber)) {
            CommonUtil.showToast("请选择提现银行卡",WithdrawActivity.this);
            return;
        }
        if(TextUtils.isEmpty(fetchMoney)) {
            CommonUtil.showToast("请输入提现金额",WithdrawActivity.this);
            return;
        }
        float money = Float.valueOf(fetchMoney);
        if(money < 100) {
            CommonUtil.showToast("账户提现最少金额为100元",WithdrawActivity.this);
            return;
        }
        String accountBalance = this.mAccountBalanceText.getText().toString();
        if(Float.valueOf(accountBalance) < money) {
            CommonUtil.showToast("余额不足",WithdrawActivity.this);
            return;
        }
       final PaypswDialog payDialog = new PaypswDialog(WithdrawActivity.this);
        payDialog.setOnPositiveListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              payDialog.dismiss();
              //传入参数
              String payPsw = payDialog.getPassword();
              LogUtil.i(this,"支付密码输入 = " + payPsw);
              Map<String,Object> map = new HashMap<>();
              map.put("fetch_money",fetchMoney);
              map.put("card_number",cardNumber);
              map.put("user_id",userId);
              map.put("user_phone",userPhone);
              map.put("pay_passwd",payPsw);
              map.put("bank_name",cardNumber);
              map.put("bank_name",bankNumber);
              map.put("bank_id",mBankId);
              RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_FETCH_CASH,map,WithdrawActivity.this.mNetWorkCallBack, WithDrawResponse.class);
          }
      });
        payDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payDialog.dismiss();
            }
        });
        payDialog.show();
            //传入参数
//            Map<String,Object> map = new HashMap<>();
//            map.put("fetch_money",fetchMoney);
//            map.put("card_number",cardNumber);
//            map.put("user_id",userId);
//            map.put("user_phone",userPhone);
//            map.put("pay_passwd",payPsw);
//            map.put("bank_name",cardNumber);
//            map.put("bank_name",bankNumber);
//            map.put("bank_id",this.mBankId);
//            RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_FETCH_CASH,map,WithdrawActivity.this.mNetWorkCallBack, WithDrawResponse.class);
    }
    private void getData() {
        Map<String,Object> map = new HashMap<>();
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();
        map.put("user_id",userId);
        map.put("user_phone",userPhone);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_BALANCE_STATISTIC,map,WithdrawActivity.this.mNetWorkCallBack, AccountBalanceResponse.class);
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
            if(object instanceof WithDrawResponse) {
                WithDrawResponse withDrawResponse = (WithDrawResponse)object;
                LogUtil.i(this,"withDrawResponse = " + withDrawResponse);
                Toast toast = Toast.makeText(WithdrawActivity.this,"提现成功",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finish();
            }

            if (object instanceof AccountBalanceResponse) {
                AccountBalanceResponse accountBalanceResponse = (AccountBalanceResponse)object;
                WithdrawActivity.this.mAccountBalanceText.setText(accountBalanceResponse.balance);
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,WithdrawActivity.this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == SELECT_CARD) {
                String bankName = data.getStringExtra("bankName");
                String bankNo = data.getStringExtra("bankNo");
                WithdrawActivity.this.mBankId = data.getStringExtra("bankId");
                WithdrawActivity.this.mBankName.setText(bankName);
                WithdrawActivity.this.mBankNo.setText(bankNo);
            }
        }
    }
}
