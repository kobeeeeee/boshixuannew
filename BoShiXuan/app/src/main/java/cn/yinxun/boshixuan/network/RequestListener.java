package cn.yinxun.boshixuan.network;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public interface RequestListener {
    void onBegin();
    void onResponse(Object object);
    void onFailure(Object message);
}
