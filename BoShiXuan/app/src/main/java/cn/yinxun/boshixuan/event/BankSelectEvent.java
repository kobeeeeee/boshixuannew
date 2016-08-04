package cn.yinxun.boshixuan.event;

import cn.yinxun.boshixuan.network.model.BankDetailModel;

/**
 * Created by Administrator on 2016/7/26 0026.
 */
public class BankSelectEvent extends BaseEvent{
    public BankDetailModel mBankDetailModel;
    public BankSelectEvent(BankDetailModel model) {
        this.mBankDetailModel = model;
    }
}
