package cn.yinxun.boshixuan.network.model;

import cn.yinxun.boshixuan.network.BaseResponse;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class OrderResponse extends BaseResponse{
    public String productBrand;
    public String productDeposit;
    public String productDayRent;
    public String orderNo;
    public String orderCreateTime;
    public String orderPayTime;
    public OrderModel order_list;
}
