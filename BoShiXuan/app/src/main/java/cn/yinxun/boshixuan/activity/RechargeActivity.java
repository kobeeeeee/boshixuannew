package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.adapter.RechargePayModeAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.event.BaseEvent;
import cn.yinxun.boshixuan.event.FinanceBuyEvent;
import cn.yinxun.boshixuan.network.PayResponse;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.WeChatRechargeResponse;
import cn.yinxun.boshixuan.network.model.AccountBalanceResponse;
import cn.yinxun.boshixuan.network.model.WeChatOrderNoResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.Constants;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.util.NetWorkUtil;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Administrator on 2016/7/14 0014.
 */
public class RechargeActivity extends BaseActivity{
    public static final int TYPE_WEIXIN_PAY = 0;
    public static final int TYPE_ALI_PAY = 1;
    public static final int TYPE_BANK_CARD_PAY = 2;
    public static final int TYPE_STORE_PAY = 3;
    public static RechargeActivity sRechargeActivity;
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
    public JSONObject mJSONObject;
    public String mPutinNum;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sRechargeActivity = this;
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        initHeader();
        getData();
        initListView();
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
//                String invest_money = getIntent().getStringExtra("invest_money");
//                if(Float.valueOf(inputMoney) < Float.valueOf(invest_money)) {
//                    CommonUtil.showToast("充值金额须大于起投金额",RechargeActivity.this);
//                    return;
//                }
                if(!CommonUtil.isWeiXinAvailable(RechargeActivity.this)) {
                    CommonUtil.showToast("您未安装微信",RechargeActivity.this);
                    return;
                }
                switch (RechargeActivity.this.mPosition){
                    case TYPE_WEIXIN_PAY:
                       getOrderNo();
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
    private void getOrderNo() {
        Map<String,Object> map = new HashMap<>();
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();
        map.put("user_id",userId);
        map.put("user_phone",userPhone);
        map.put("wechat_type","1");
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_WECHAT_RECHARGE,map,RechargeActivity.this.mNetWorkCallBack, WeChatOrderNoResponse.class);

    }
    private void payByWeiXin(String orderNo){
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userName = userInfoBean.getUserName();
        String parameters="";
        Map<String,String> map = new HashMap<>();
        map.put("txcode","YX5001");
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeformat = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        map.put("txdate",dateformat.format(date));
        map.put("txtime",timeformat.format(date));
        map.put("txserial",orderNo);
        map.put("version","1111111111111111");
        parameters = map.get("txcode") +  map.get("txdate") +  map.get("txtime") +  map.get("txserial") +  map.get("version");
        DecimalFormat df = new DecimalFormat("0");
        double balance = Double.valueOf(this.mInputMoneyText.getText().toString());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("body","APP支付");
        jsonObject.put("uname",userName);
        jsonObject.put("uid",userId);
        jsonObject.put("spbill_create_ip", NetWorkUtil.getLocalIpAddress(this));
        jsonObject.put("total_fee",df.format(balance*100));
        jsonObject.put("trade_type","APP");
        String stringForSign = "body:" +jsonObject.getString("body") + "&"
                + "spbill_create_ip:" + jsonObject.getString("spbill_create_ip") + "&"
                + "total_fee:" + jsonObject.getString("total_fee") + "&"
                + "trade_type:" + jsonObject.getString("trade_type") + "&"
                + "uid:" + jsonObject.getString("uid") + "&"
        + "uname:" + jsonObject.getString("uname") + "&"
                + "key:" + "A37CAD68626EC4B6AD6116BCF7E76F0E";
        String sign=CommonUtil.md5(stringForSign).toUpperCase();
        jsonObject.put("sign",sign);
        parameters = parameters + String.format("%06d", jsonObject.toString().length()) + jsonObject.toString();

        final String param = parameters;
        new Thread(){
            @Override
            public void run() {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                // 目标地址
                HttpPost httppost = new HttpPost(Config.PAY_URL + Config.PAY_COLON + Config.PAY_PORT);
                // 构造最简单的字符串数据
                StringEntity reqEntity = null;
                try {
                    reqEntity = new StringEntity(URLEncoder.encode(param,"utf-8"));
                    // 设置类型
                    reqEntity.setContentType("application/x-www-form-urlencoded; charset=utf-8");
                    // 设置请求的数据
                    httppost.setEntity(reqEntity);
                    // 执行
                    HttpResponse httpresponse = httpclient.execute(httppost);
                    HttpEntity entity = httpresponse.getEntity();
                    String content = EntityUtils.toString(entity);
                    String body = URLDecoder.decode(content,"utf-8");
                    int left = body.indexOf("{");
                    int right = body.indexOf("}");
                    String json = body.substring(left,right + 1);
                    communicateWithWeiXin(json);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    private void communicateWithWeiXin(String jsonString){
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        this.mJSONObject = jsonObject;
        //实例化
        IWXAPI wxApi = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID,true);
        wxApi.registerApp(Constants.WX_APP_ID);
        PayReq request = new PayReq();
        request.appId = jsonObject.getString("appid");
        request.partnerId = jsonObject.getString("partnerid");
        request.prepayId= jsonObject.getString("prepayid");
        request.packageValue = jsonObject.getString("package");
        request.nonceStr= jsonObject.getString("noncestr");
        request.timeStamp= jsonObject.getString("timestamp");
        request.sign= jsonObject.getString("sign");
        boolean isSuccess = wxApi.sendReq(request);
        if(isSuccess) {

        }
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
            if(object == null) {
                return;
            }
            if (object instanceof AccountBalanceResponse) {
                AccountBalanceResponse accountBalanceResponse = (AccountBalanceResponse)object;
                RechargeActivity.this.mAccountBalanceText.setText(accountBalanceResponse.balance);
            }
            if(object instanceof WeChatOrderNoResponse) {
                WeChatOrderNoResponse weChatOrderNoResponse = (WeChatOrderNoResponse)object;
                String putin_num = weChatOrderNoResponse.putin_num;
                RechargeActivity.this.mPutinNum = putin_num;
                payByWeiXin(putin_num);
            }
            if(object instanceof  WeChatRechargeResponse) {
                Log.i("RechargeActivity","充值成功");
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,RechargeActivity.this);
        }
    }
    @Subscribe
    public void onEvent(final BaseEvent event) {
        Log.i("RegularBuyActivity","event = " + event);
        if(event instanceof FinanceBuyEvent) {
            String money = this.mInputMoneyText.getText().toString();
            commitRechargeOrder(money);
        }
    }
    private void commitRechargeOrder(String money) {
        Intent intent = getIntent();
        String interest_rate = intent.getStringExtra("interest_rate");
        String invest_days = intent.getStringExtra("invest_days");
        String finance_name = intent.getStringExtra("finance_name");
        String product_id = intent.getStringExtra("product_id");
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();

        String rate = interest_rate.substring(0,interest_rate.length() - 1);
        BigDecimal rateBigDecimal = new BigDecimal(rate).setScale(2,BigDecimal.ROUND_HALF_UP);
        Map<String,Object> map = new HashMap<>();
        map.put("wechat_type","2");
        map.put("user_id",userId);
        map.put("user_phone",userPhone);
        map.put("interest_rate",String.valueOf(rateBigDecimal));
        map.put("invest_days",invest_days.substring(0,invest_days.length() - 1));
        map.put("finance_name",finance_name);
        map.put("putin_money",money);
        map.put("product_id",product_id);
        map.put("putin_num",this.mPutinNum);
        LogUtil.i(this,"putin_money = " + money);
        LogUtil.i(this,"putin_num = " + this.mPutinNum);

        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_WECHAT_RECHARGE,map,RechargeActivity.this.mNetWorkCallBack, WeChatRechargeResponse.class);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        sRechargeActivity = null;
    }
}
