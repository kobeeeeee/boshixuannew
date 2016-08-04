package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.adapter.RechargePayModeAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.AccountBalanceResponse;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class RechargeActivity extends BaseActivity{
    public static final int TYPE_WEIXIN_PAY = 0;
    public static final int TYPE_ALI_PAY = 1;
    public static final int TYPE_BANK_CARD_PAY = 2;
    public static final int TYPE_STORE_PAY = 3;

    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.accountBalanceText)
    TextView mAccountBalanceText;
    @Bind(R.id.rechargeNextImage)
    ImageView mRechargeNextImage;
    @Bind(R.id.payModeListView)
    ListView mPayModeListView;
    @Bind(R.id.inputMoneyText)
    EditText mInputMoneyText;
    private RechargePayModeAdapter mRechargePayModeAdapter;
    private NetWorkCallBack mNetWorkCallBack = new NetWorkCallBack();
    private List<String> mPayModeTextList;
    private List<Integer> mPayModeImageSrcList;
    private int mPosition = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        initHeader();
        getData();
        initListView();
    }
    private void initListView(){
        initListViewData();
        this.mRechargePayModeAdapter = new RechargePayModeAdapter(this,this.mPayModeTextList,this.mPayModeImageSrcList);
        this.mPayModeListView.setAdapter(this.mRechargePayModeAdapter);
        this.mPayModeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RechargeActivity.this.mPosition = position;
                RechargeActivity.this.mRechargePayModeAdapter.setSelectPosition(position);
                RechargeActivity.this.mRechargePayModeAdapter.notifyDataSetChanged();
            }
        });
    }
    private void initListViewData(){
        this.mPayModeTextList = new ArrayList<>();
        this.mPayModeTextList.add("微信支付");
        this.mPayModeTextList.add("支付宝支付");
        this.mPayModeTextList.add("银行卡支付");
        this.mPayModeTextList.add("门店支付");

        this.mPayModeImageSrcList = new ArrayList<>();
        this.mPayModeImageSrcList.add(R.drawable.recharge_icon_weixin);
        this.mPayModeImageSrcList.add(R.drawable.recharge_icon_alipay);
        this.mPayModeImageSrcList.add(R.drawable.recharge_icon_unionpay);
        this.mPayModeImageSrcList.add(R.drawable.recharge_icon_shop);

    }
    private void initHeader(){
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("充值");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        this.mRechargeNextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputMoney = RechargeActivity.this.mInputMoneyText.getText().toString();
                if(TextUtils.isEmpty(inputMoney)) {
                    CommonUtil.showToast("请输入金额",RechargeActivity.this);
                    return;
                }
                switch (RechargeActivity.this.mPosition){
                    case TYPE_WEIXIN_PAY:
                        CommonUtil.showToast("接口测试中，敬请期待",RechargeActivity.this);
                        break;
                    case TYPE_ALI_PAY:
                        CommonUtil.showToast("暂未开通，敬请期待",RechargeActivity.this);
                        break;
                    case TYPE_BANK_CARD_PAY:
                        CommonUtil.showToast("暂未开通，敬请期待",RechargeActivity.this);
                        break;
                    case TYPE_STORE_PAY:
                        Intent intent = new Intent(RechargeActivity.this,RechargeMoreActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
    private void getData() {
        Map<String,Object> map = new HashMap<>();
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();
        map.put("user_id",userId);
        map.put("user_phone",userPhone);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_BALANCE_STATISTIC,map,RechargeActivity.this.mNetWorkCallBack, AccountBalanceResponse.class);
    }
    private class NetWorkCallBack implements RequestListener {

        @Override
        public void onBegin() {

        }

        @Override
        public void onResponse(Object object) {
            if (object != null && object instanceof AccountBalanceResponse) {
                AccountBalanceResponse accountBalanceResponse = (AccountBalanceResponse)object;
                RechargeActivity.this.mAccountBalanceText.setText(accountBalanceResponse.balance);
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,RechargeActivity.this);
        }
    }
}
