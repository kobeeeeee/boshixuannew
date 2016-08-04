package cn.yinxun.boshixuan.network.model;

import cn.yinxun.boshixuan.network.BaseResponse;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class NewsResponse extends BaseResponse{
    public String newsContent;
    public String newsTitle;
    public String newsTime;
    public NewsModel msg_list;
}
