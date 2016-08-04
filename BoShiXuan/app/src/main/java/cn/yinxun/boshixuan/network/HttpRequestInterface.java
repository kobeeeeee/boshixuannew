package cn.yinxun.boshixuan.network;


import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public interface HttpRequestInterface {
    String get(int tag, String url, Type modelType);

    String post(int tag, String url, KeyValuePair keyValuePair,Type modelType);

    String uploadFile(int tag, String url, byte[] bytes, String mimeType, String fileName);

    boolean downloadFile(String url, String dirPath, String fileName);
}
