package cn.yinxun.boshixuan.wxapi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.activity.RechargeActivity;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.event.FinanceBuyEvent;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.Constants;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.util.NetWorkUtil;
import cn.yinxun.boshixuan.view.CustomDialog;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Administrator on 2016/8/8 0008.
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
    private IWXAPI api;
    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            String code = (String) msg.obj;
            String message = "";
            switch (code){
                case "SUCCESS":
                    message = "支付成功";
                    EventBus.getDefault().post(new FinanceBuyEvent());
                    break;
                case "REFUND":
                    message = "转入退款";
                    break;
                case "NOTPAY":
                    message = "未支付";
                    break;
                case "CLOSED":
                    message = "已关闭";
                    break;
                case "REVOKED":
                    message = "已撤销（刷卡支付）";
                    break;
                case "USERPAYING":
                    message = "用户支付中";
                    break;
                case "PAYERROR":
                    message = "支付失败(其他原因，如银行返回失败)";
                    break;
                default:
                    message = "未知错误";
                    break;
            }
            CustomDialog.Builder builder=new CustomDialog.Builder(WXPayEntryActivity.this);
            builder.setTitle("提示");
            builder.setMessage(getString(R.string.pay_result_callback_msg,message));
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    WXPayEntryActivity.this.finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(final BaseResp resp) {
        LogUtil.i(this,"onResp");
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            String message;
            switch (resp.errCode){
                case 0:
                    message="支付成功";
                    break;
                case -1:
                    message="支付异常";
                    break;
                case -2:
                    message="支付取消";
                    break;
                default:
                    message="支付异常";
                    break;
            }
            if(message.equals("支付成功")) {
                communicateWithBackStage();
            } else{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("提示");
                builder.setMessage(getString(R.string.pay_result_callback_msg,message));
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        WXPayEntryActivity.this.finish();
                    }
                });
                builder.create().show();
            }
        }
    }
    private void communicateWithBackStage(){
        JSONObject json = RechargeActivity.sRechargeActivity.mJSONObject;
        LogUtil.i(this,"json = " + json);
        String parameters="";
        Map<String,String> map = new HashMap<>();
        map.put("txcode","YXC001");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        map.put("txdate",dateFormat.format(date));
        map.put("txtime",timeFormat.format(date));
        map.put("txserial","12345678901234567890");
        map.put("version","1111111111111111");
        parameters = map.get("txcode") +  map.get("txdate") +  map.get("txtime") +  map.get("txserial") +  map.get("version");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("body","APP查询");
        jsonObject.put("transaction_id",json.getString("prepayid"));
        jsonObject.put("out_trade_no",json.getString("out_trade_no"));
        jsonObject.put("spbill_create_ip", NetWorkUtil.getLocalIpAddress(this));
        jsonObject.put("trade_type","APP");
        String stringForSign = "body:" +jsonObject.getString("body") + "&"
                + "out_trade_no:" + jsonObject.getString("out_trade_no") + "&"
                + "spbill_create_ip:" + jsonObject.getString("spbill_create_ip") + "&"
                + "trade_type:" + jsonObject.getString("trade_type") + "&"
                + "transaction_id:" + jsonObject.getString("transaction_id") + "&"
                + "key:" + "A37CAD68626EC4B6AD6116BCF7E76F0E";
        String sign=CommonUtil.md5(stringForSign).toUpperCase();
        jsonObject.put("sign",sign);
        parameters = parameters + String.format("%06d", jsonObject.toString().length()) + jsonObject.toString();
        LogUtil.i(this,"parameters = " + parameters);
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
                    LogUtil.i(WXPayEntryActivity.this,"body = " + body);
                    int left = body.indexOf("{");
                    int right = body.indexOf("}");
                    String json = body.substring(left,right + 1);
                    LogUtil.i(WXPayEntryActivity.this,json);
                    JSONObject jsonObject1 = JSONObject.parseObject(json);
                    String code = jsonObject1.getString("return_code");
                    Message msg = new Message();
                    msg.obj = code;
                    WXPayEntryActivity.this.mHandler.sendMessage(msg);
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
}
