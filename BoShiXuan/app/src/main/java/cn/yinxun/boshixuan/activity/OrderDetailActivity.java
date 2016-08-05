package cn.yinxun.boshixuan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
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
import cn.yinxun.boshixuan.network.model.OrderPayResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.view.CustomDialog;
import cn.yinxun.boshixuan.view.PaypswDialog;

/**
 * Created by Administrator on 2016/8/2 0002.
 */
public class OrderDetailActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.order_no)
    TextView mOrderNo;
    @Bind(R.id.deposit_money)
    TextView mDepositMoney;
    @Bind(R.id.day_rent_money)
    TextView mDayRentMoney;
    @Bind(R.id.confirmBtn)
    ImageView mConfirmBtn;
    String mOrderId = "";
    private NetWorkCallBack mNetWorkCallBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        initHeader();
        getData();
        initClick();
    }
    private void initClick() {
        this.mNetWorkCallBack = new NetWorkCallBack();
        this.mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                String isVerify = userInfoBean.getIsVerity();
                if(!isVerify.equals("2")) {
                    CommonUtil.showCertificationDialog(OrderDetailActivity.this,"支付订单");
                    return;
                }
                final PaypswDialog payDialog = new PaypswDialog(OrderDetailActivity.this);
                payDialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payDialog.dismiss();
                        String userId = userInfoBean.getCustId();
                        String userPhone = userInfoBean.getCustMobile();
                        //传入参数
                        String payPsw = payDialog.getPassword();
                        LogUtil.i(this,"支付密码输入 = " + payPsw);
                        Map<String,Object> map = new HashMap<>();
                        map.put("goods_order_id",OrderDetailActivity.this.mOrderId);
                        map.put("rent_price",OrderDetailActivity.this.mDayRentMoney.getText().toString());
                        map.put("user_id",userId);
                        map.put("user_phone",userPhone);
                        map.put("pay_passwd",payPsw);
                        map.put("deposit_price",OrderDetailActivity.this.mDepositMoney.getText().toString());
                        map.put("order_number",OrderDetailActivity.this.mOrderNo.getText().toString());
                        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_FETCH_CASH,map,OrderDetailActivity.this.mNetWorkCallBack, OrderPayResponse.class);
                    }
                });
                payDialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payDialog.dismiss();
                    }
                });
                payDialog.show();
            }
        });
    }
    private void getData() {
        Intent intent = getIntent();
        this.mOrderId= intent.getStringExtra("orderId");
        String rentPrice = intent.getStringExtra("rentPrice");
        String depositPrice = intent.getStringExtra("depositPrice");
        String orderNumber = intent.getStringExtra("orderNumber");
        this.mDepositMoney.setText(depositPrice);
        this.mDayRentMoney.setText(rentPrice);
        this.mOrderNo.setText(orderNumber);
    }


    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("订单支付");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
            if(object instanceof OrderPayResponse) {
                OrderPayResponse orderPayResponse = (OrderPayResponse)object;
                LogUtil.i(this,"orderPayResponse = " + orderPayResponse);
                StringBuffer message = new StringBuffer();
                message.append("亲，请到订单页面确认信息");
                CustomDialog.Builder builder=new CustomDialog.Builder(OrderDetailActivity.this);
                builder.setTitle("创建订单成功");
                builder.setMessage(message.toString());
                builder.setNegativeButton("我再逛逛",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        OrderDetailActivity.this.finish();
                    }
                });
                builder.setPositiveButton("现在就去", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(OrderDetailActivity.this,MyOrderActivity.class);
                        OrderDetailActivity.this.startActivity(intent);
                        OrderDetailActivity.this.finish();
                    }
                });
                builder.create().show();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,OrderDetailActivity.this);
        }
    }
}
