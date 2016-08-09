package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.os.Build;
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

import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

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
import cn.yinxun.boshixuan.network.PayResponse;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.AccountBalanceResponse;
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sRechargeActivity = this;
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
                        payByWeiXin();
//                        CommonUtil.showToast("接口测试中，敬请期待",RechargeActivity.this);
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
    private void payByWeiXin(){
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
        map.put("txserial","12345678901234567890");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        sRechargeActivity = null;
    }
}
