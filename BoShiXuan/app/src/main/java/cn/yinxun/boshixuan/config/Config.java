package cn.yinxun.boshixuan.config;

/**
 * Created by Administrator on 2016/7/4 0004.
 */
public class Config {
    //根路径
    public static String ROOT_URL = "http://218.5.74.172:7890/bosx";
    public static String SLASH = "/";
    //路径
    public static String URL = ROOT_URL + SLASH + "app";
    /**
     * 连接字符串
     */
    public static String QUESTION_MARK = "?";
    public static String JOINER = "&";
    public static String EQUAL = "=";
    //发送短信验证码
    public static String BSX_VERIFY_CODE = "verifycode";
    //商户注册
    public static String BSX_REGISTER = "register";
    //商户登陆
    public static String BSX_USER_LOGIN = "userlogin";
    //商户信息查询
    public static String BSX_USER_INFO_QUERY = "userinfoquery";
    //商户密码修改与找回
    public static String BSX_MODIFY_PWD = "modifypwd";
    //商户实名认证
    public static String BSX_REAL_NAME = "realname";
    //银行卡绑定
    public static String BSX_BANDING_BANK = "bandingbank";
    //公告查询
    public static String BSX_MESSAGE = "message";
    //银行卡解绑
    public static String BSX_UNBANDING_BANK = "unbandingbank";
    //银行卡列表
    public static String BSX_BANK_LIST = "banklist";
    //账户余额查询
    public static String BSX_BALANCE_STATISTIC = "balancestatistic";
    //租赁商品详情
    public static String BSX_RENT_GOODS = "rentgoods";
    //商品下单
    public static String BSX_PUTIN_ORDER = "putinorder";
    //提现
    public static String BSX_FETCH_CASH = "fetchcash";
    //订单
    public static String BSX_QUERY_ORDER = "orderlist ";
    //押金宝定期
    public static String BSX_FINANCE_PRODUCT = "financeproduct";
    //押金宝定期支付
    public static String BSX_PURCHASE_FINANCE = "purchasefinance";
    //订单支付
    public static String BSX_ORDER_PAY = "orderpay";
    //理财订单
    public static String BSX_FINANCE_LIST = "financelist";
    //充值记录
    public static String BSX_PUT_IN_LIST= "putinlist";
    //提现记录
    public static String BSX_FETCH_CASH_LIST = "fetchcashlist";
}
