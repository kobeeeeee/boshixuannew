package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.PutInOrderResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/10 0010.
 */
public class ProductDetailActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.productImage)
    ImageView mProductImage;
    @Bind(R.id.productName)
    TextView mProductName;
    @Bind(R.id.productNo)
    TextView mProductNo;
    @Bind(R.id.depositText)
    EditText mDepositText;
    @Bind(R.id.dayRentText)
    EditText mDayRentText;
    @Bind(R.id.commitOrder)
    ImageView mCommitOrder;
    private NetWorkCallBack mNetWorkCallBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);
        initHeader();
        initClick();
    }
    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("租赁信息");
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

        Intent intent = getIntent();
        final String productId = intent.getStringExtra("productId");
        final String productNo = intent.getStringExtra("productNo");
        String productName = intent.getStringExtra("productName");
        String productImage = intent.getStringExtra("productImage");
        this.mProductName.setText(productName);
        this.mProductNo.setText(productNo);
        Picasso.with(this).load(Config.ROOT_URL + productImage).error(getResources().getDrawable(R.drawable.product_detail_default_image)).into(this.mProductImage);
        this.mCommitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                String userId = userInfoBean.getCustId();
                String userPhone = userInfoBean.getCustMobile();
                final String depositPrice = ProductDetailActivity.this.mDepositText.getText().toString();
                final String rentPrice = ProductDetailActivity.this.mDayRentText.getText().toString();
                if(!userInfoBean.getIsVerity().equals("2")) {
                    CommonUtil.showToast("尚未实名认证",ProductDetailActivity.this);
                    return;
                }

                if(TextUtils.isEmpty(depositPrice)) {
                    CommonUtil.showToast("请输入押金",ProductDetailActivity.this);
                    return;
                }

                if(TextUtils.isEmpty(rentPrice)) {
                    CommonUtil.showToast("请输入日租金",ProductDetailActivity.this);
                    return;
                }

                if(Float.valueOf(depositPrice) > Float.valueOf(rentPrice)) {
                    CommonUtil.showToast("押金不能大于日租金",ProductDetailActivity.this);
                    return;
                }
                //传入参数
                Map<String,Object> map = new HashMap<>();
                map.put("user_id",userId);
                map.put("user_phone",userPhone);
                map.put("goods_number",productNo);
                map.put("deposit_price",depositPrice);
                map.put("rent_price",rentPrice);
                map.put("goods_Id",productId);
                RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_PUTIN_ORDER,map,ProductDetailActivity.this.mNetWorkCallBack, PutInOrderResponse.class);
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
            if(object instanceof PutInOrderResponse) {
                PutInOrderResponse putInOrderResponse = (PutInOrderResponse)object;
                LogUtil.i(this,"putInOrderResponse = " + putInOrderResponse);
                CommonUtil.showToast("订单创建成功",ProductDetailActivity.this);
                finish();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,ProductDetailActivity.this);
        }
    }
}
