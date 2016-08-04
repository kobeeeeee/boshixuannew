package cn.yinxun.boshixuan.util;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Xml;
import android.view.Gravity;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.activity.CertificationActivity;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.view.CustomDialog;

/**
 * Created by Administrator on 2016/7/3 0003.
 */
public class CommonUtil {
    /**
     * 共通处理，把Map转换成JSONObject。
     * @param actId
     * @param map
     */
    public static JSONObject pullMapToJson(String actId, Map<String, String> map) {
        JSONObject jsonObj = new JSONObject();


        Iterator<?> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = map.get(key);
            jsonObj.put(key, value);
        }

        return jsonObj;
    }
    public static JSONObject toJson(String actId, Map<String, Object> map) {
        JSONObject jsonObj = new JSONObject();


        Iterator<?> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = map.get(key).toString();
            jsonObj.put(key, value);
        }

        return jsonObj;
    }
    /**
     * XML解析
     *
     * @param responseBody
     */
    public static String pullXMLtoJson(byte[] responseBody) {
        // TODO Auto-generated method stub

        String jsonStr = "";

        ByteArrayInputStream inputStringStream = new ByteArrayInputStream(
                responseBody);

        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(inputStringStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                        // persons = new ArrayList<Person>();
                        break;
                    case XmlPullParser.START_TAG:// 开始元素事件
                        String name = parser.getName();
                        if (name.equalsIgnoreCase("return")) {
                            jsonStr = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:// 结束元素事件
                        break;
                }
                eventType = parser.next();
            }
            inputStringStream.close();
            // return persons;
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonStr;
    }
    public static String getHttpRequestPath(HashMap<String,Object> map,String actId) {
        int count = 0;
        String path = Config.URL + Config.SLASH + actId;
        Iterator<?> iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = map.get(key).toString();
            if(count == 0) {
                path = path + Config.QUESTION_MARK ;
            } else {
                path = path + Config.JOINER;
            }
            path = path + key + Config.EQUAL + value;
            count ++;
        }
        return path;
    };
    public static Map<String,String> putBaseFieldIntoMap(Map<String,String> map) {
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        map.put("sysType",userInfoBean.getSysType());
        map.put("sysVersion",userInfoBean.getSysVersion());
        map.put("appVersion",userInfoBean.getAppVersion());
        map.put("sysTerNo",userInfoBean.getSysTerNo());
        if(!TextUtils.isEmpty(userInfoBean.getCustId())) {
            map.put("user_id",userInfoBean.getCustId());
        }
        if(!TextUtils.isEmpty(userInfoBean.getCustMobile())) {
            map.put("user_phone",userInfoBean.getCustMobile());
        }
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String systemDate = dateFormat.format(date);
        String systemTime = timeFormat.format(date);
        map.put("txnDate",systemDate);
        map.put("txnTime",systemTime);
        return map;
    }
    public static  boolean checkPhoneNumber(String phoneNumber){
        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static  boolean checkPassword(String password){
        Pattern pattern = Pattern.compile(".*[a-zA-Z].*[0-9]|.*[0-9].*[a-zA-Z]");
        //Pattern pattern = Pattern.compile("^[A-Za-z0-9]{6,20}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public static String formatTime(String time) {
        long longTime = Long.valueOf(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(longTime);
        return formatter.format(date);
    }

    /**
     * 显示Toast
     * @param message toast信息
     * @param context context
     */
    public static void showToast(String message,Context context) {
        Toast toast = Toast.makeText(context,message,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    public static String formatBankNo(String bankNo) {
        String cardNo = bankNo.substring(0,4) + "***********" + bankNo.substring(15,19);
        return cardNo;
    }

    /**
     * md5加密
     * @param string
     * @return
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    public static boolean checkIfInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 实名认证提示框
     * @param activity
     * @param action
     */
    public static void showCertificationDialog(final Activity activity, String action) {
        StringBuffer message = new StringBuffer();
        message.append("亲，").append(action).append("前请先实名认证");
        CustomDialog.Builder builder=new CustomDialog.Builder(activity);
        builder.setTitle("尚未实名认证");
        builder.setMessage(message.toString());
        builder.setNegativeButton("返回",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("现在就去", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(activity,CertificationActivity.class);
                activity.startActivity(intent);
            }
        });
        builder.create().show();
    }
}
