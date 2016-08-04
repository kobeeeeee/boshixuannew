package cn.yinxun.boshixuan.network;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class HttpRequestOKHttpImpl  implements HttpRequestInterface {
    private static final String TAG = "HttpRequestOKHttpImpl";
    private static HttpRequestOKHttpImpl instance = null;
    private OkHttpClient client;


    public static HttpRequestOKHttpImpl getInstance() {
        if (instance == null) {
            synchronized (HttpRequestOKHttpImpl.class) {
                if (instance == null) {
                    instance = new HttpRequestOKHttpImpl();
                }
            }
        }
        return instance;
    }

    public HttpRequestOKHttpImpl() {
        client = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

    }

    @Override
    public String get(int tag, String url, final Type modelType) {
        final StringBuilder builder = new StringBuilder();
        Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String post(int tag, String url, KeyValuePair param, final Type modelType) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add(param.getKey(), param.getValue());
//        for (KeyValuePair keyValuePair : paramList) {
//            if (keyValuePair.getValue() == null) {
//                continue;
//            }
//            builder.add(keyValuePair.getKey(), keyValuePair.getValue());
//        }

        RequestBody requestBody = builder.build();
        Request request = new Request
                .Builder()
                .url(url)
                .tag(tag)
                .post(requestBody)
                .build();
        try {
            okhttp3.Response response = client.newCall(request).execute();
            String body=response.body().string();
            return body;
        } catch (IOException e) {
            LogUtil.i(getClass().getSimpleName(), "response error");
            e.printStackTrace();
            return null;
        }

    }


    @Override
    public String uploadFile(int tag, String url, byte[] bytes, String mimeType, String fileName) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, RequestBody.create(MediaType.parse(mimeType), bytes))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .tag(tag)
                .post(requestBody)
                .build();

        try {
            okhttp3.Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean downloadFile(String url, String dirPath, String fileName) {
        return false;
    }
}
