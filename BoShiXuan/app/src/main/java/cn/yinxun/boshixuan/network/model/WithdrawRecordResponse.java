package cn.yinxun.boshixuan.network.model;

import cn.yinxun.boshixuan.network.BaseResponse;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class WithdrawRecordResponse extends BaseResponse{
    public String withdrawMoney;
    public String withdrawTime;
    public String withdrawBankCard;
    public String withdrawBankName;
    public WithdrawRecordModel fetchcash_list;
}
