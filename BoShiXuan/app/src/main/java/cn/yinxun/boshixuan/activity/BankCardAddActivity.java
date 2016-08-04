package cn.yinxun.boshixuan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.BankAddResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.view.pickerview.Picker;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class BankCardAddActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.userName)
    TextView mUserName;
    @Bind(R.id.bankNo)
    EditText mBankNo;
    @Bind(R.id.bankName)
    EditText mBankName;
    @Bind(R.id.BankAddBtn)
    ImageView mBankAddBtn;
    @Bind(R.id.expansionLayout)
    RelativeLayout mExpansionLayout;
    private Picker mPicker;
    private NetWorkCallBack mNetWorkCallBack;
    private List<String> bankList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_add);
        ButterKnife.bind(this);
        initHeader();
        initLayout();
    }
    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("添加银行卡");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initLayout() {
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userName = userInfoBean.getUserName();
        this.mUserName.setText(userName);

        this.mBankAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        this.mExpansionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BankCardAddActivity.this.mPicker.show();
                BankCardAddActivity.this.mPicker.setOnSelectDoneListener(new Picker.OnSelectDoneListener() {
                    @Override
                    public void onSelectDone(String text) {
                        mBankName.setText(text);
                    }
                });
            }
        });
        putBankIntoList();
        View parentView = LayoutInflater.from(BankCardAddActivity.this).inflate(R.layout.activity_bank_add,null);
        this.mPicker = new Picker(this, parentView, bankList);
    }
    private void getData() {
        this.mNetWorkCallBack = new NetWorkCallBack();
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String bankNo = this.mBankNo.getText().toString();
        String bankName = this.mBankName.getText().toString();
        String userName = this.mUserName.getText().toString();
        if(bankNo.length() == 0) {
            CommonUtil.showToast("请输入正确的银行卡号",BankCardAddActivity.this);
            return;
        }
        //传入参数
        Map<String,Object> map = new HashMap<>();
        map.put("card_number",bankNo);
        map.put("bank_name",bankName);
        map.put("user_name",userName);
        map.put("user_id",userId);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_BANDING_BANK,map,BankCardAddActivity.this.mNetWorkCallBack, BankAddResponse.class);


    }
    private void putBankIntoList() {
        bankList.add("中国银行");
        bankList.add("中国农业银行");
        bankList.add("中国工商银行");
        bankList.add("中国建设银行");
        bankList.add("中国民生银行");
        bankList.add("招商银行");
        bankList.add("中国邮政储蓄银行");
        bankList.add("农村信用社");
        bankList.add("平安银行");
        bankList.add("交通银行");
        bankList.add("兴业银行");
        bankList.add("上海浦东发展银行");
        bankList.add("中信银行");
        bankList.add("华夏银行");
        bankList.add("中国光大银行");
        bankList.add("农村商业银行");
        bankList.add("华一银行");
        bankList.add("南洋商业银行");
        bankList.add("厦门国际银行");
        bankList.add("城市信用社");
        bankList.add("城市商业银行");
        bankList.add("广东发展银行");
        bankList.add("恒生银行");
        bankList.add("渣打银行");
        bankList.add("渤海银行");
        bankList.add("花旗银行");
        bankList.add("香港上海汇丰银行");
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
            if(object instanceof BankAddResponse) {
                BankAddResponse bankAddResponse = (BankAddResponse)object;
                LogUtil.i(this,"bankListResponse = " + bankAddResponse);
                CommonUtil.showToast("银行卡绑定成功",BankCardAddActivity.this);
                finish();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,BankCardAddActivity.this);
        }
    }
}
