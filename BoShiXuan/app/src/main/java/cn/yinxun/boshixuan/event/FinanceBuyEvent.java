package cn.yinxun.boshixuan.event;

/**
 * Created by Administrator on 2016/8/9 0009.
 */
public class FinanceBuyEvent extends BaseEvent{
    public String money;
    public FinanceBuyEvent(String sum) {
        money = sum;
    }
}
