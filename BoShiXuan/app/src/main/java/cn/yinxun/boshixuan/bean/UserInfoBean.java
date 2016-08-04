package cn.yinxun.boshixuan.bean;

/**
 * Created by Administrator on 2016/7/3 0003.
 */
public class UserInfoBean {
    private static UserInfoBean userInfoBean = null;
    //商户ID
    private static String custId = "";
    //用户手机号
    private static String custMobile = "";
    //客户端类型
    private static String sysType = "";
    //操作系统版本号
    private static String sysVersion = "";
    //App版本号
    private static String appVersion = "";
    //设备固件号
    private static String sysTerNo = "";
    //交易日期
    private static String txnDate = "";
    //交易时间
    private static String txnTime = "";
    //密码
    private static String password = "";
    //支付密码
    private static String payPsw = "";
    //用户名称
    private static String userName = "";
    //身份证号码
    private static String identity="";

    private static String isVerity ="";
    public static UserInfoBean getUserInfoBeanInstance() {
        if(userInfoBean == null) {
            userInfoBean = new UserInfoBean();
        }
        return userInfoBean;
    }

    public void setCustMobile(String mobile) {
        custMobile = mobile;
    }

    public String getCustMobile() {
        return custMobile;
    }
    public void setCustId(String id) {
        custId = id;
    }

    public String getCustId() {
        return custId;
    }
    public void setSysType(String systemType) {
        sysType = systemType;
    }

    public String getSysType() {
        return sysType;
    }
    public void setSysVersion(String systemVersion) {
        sysVersion = systemVersion;
    }

    public String getSysVersion() {
        return sysVersion;
    }
    public void setSysTerNo(String no) {
        sysTerNo = no;
    }

    public String getSysTerNo() {
        return sysTerNo;
    }
    public void setTxnDate(String date) {
        txnDate = date;
    }

    public String getTxnDate() {
        return txnDate;
    }
    public void setTxnTime(String time) {
        txnTime = time;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setAppVersion(String applicationVersion) {
        appVersion = applicationVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setPassword(String psw) {
        password = psw;
    }
    public String getPassword() {
        return  password;
    }

    public void setPayPsw(String psw) {
        payPsw = psw;
    }
    public String getPayPsw() {
        return payPsw;
    }


    public void setUserName(String name) {
        userName = name;
    }
    public String getUserName() {
        return userName;
    }

    public void setIsVerrity(String is_verify) {
        isVerity = is_verify;
    }
    public String getIsVerity() {
        return isVerity;
    }

    public void setIdentity(String idCard) {
        identity = idCard;
    }
    public String getIdentity() {
        return identity;
    }
}
