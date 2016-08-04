package cn.yinxun.boshixuan.network.model;

import java.util.List;

/**
 * Created by Administrator on 2016/7/26 0026.
 */
public class WithdrawRecordModel {
    public int current_page;
    public String page_size;
    public String total_page;
    public String total_record;
    public List<WithdrawRecordDetailModel> data_list;
}
