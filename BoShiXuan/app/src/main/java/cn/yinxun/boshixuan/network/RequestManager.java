package cn.yinxun.boshixuan.network;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class RequestManager {
    private static RequestManager instance = null;
    public HttpRequestInterface httpRequest;
    public AsyncTask mAsyncTask;
//    public NetFailMessage netFailMessage;
//    public ActionFailMessage failMessage;

    private final static String TAG = "NETWORK";

    public static RequestManager getInstance() {
        if (instance == null) {
            synchronized (RequestManager.class) {
                if (instance == null) {
                    instance = new RequestManager();
                }
            }
        }
        return instance;
    }

    private HttpRequestInterface getHttpClient() {
        return HttpRequestOKHttpImpl.getInstance();
    }

    public RequestManager() {
        httpRequest = getHttpClient();
//        netFailMessage = new NetFailMessage();
//        failMessage = new ActionFailMessage();
    }

    /**
     * 加密form
     *
     * @param action
     * @param map
     * @return
     */
    private KeyValuePair prepareParams(String action, Map<String, String> map) {
         map = CommonUtil.putBaseFieldIntoMap(map);
        JSONObject jsonObject = CommonUtil.pullMapToJson(action, map);
        JSONObject obj = new JSONObject();
        obj.put("REQ_BODY",jsonObject);
        String objString = obj.toJSONString();
        KeyValuePair keyValuePair = new KeyValuePair("REQ_MESSAGE",objString);
        return keyValuePair;
    }

    public void post(final int tag, final String url, final String action, final Map<String, String> map, final RequestListener listener, final Type modelType) {
        mAsyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LogUtil.i(TAG, String.format("url:%s aciton:%s ", url, action));
                for (String key : map.keySet()) {
                    LogUtil.i(TAG, "key= " + key + "  value= " + map.get(key));
                }
                listener.onBegin();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                LogUtil.i(TAG, url.toString());
                KeyValuePair keyValuePair = prepareParams(action, map);
                String response = "";
                try {
                    response = httpRequest.post(tag, url + action, keyValuePair, modelType);
                    LogUtil.i(TAG, "" + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                String response = (String) o;
                LogUtil.i(getClass().getSimpleName(), "response" + response);
                if (response != null) {
                    try {
                        LogUtil.d(this, response);
                        JSONObject jsonobject = JSON.parseObject(response);
                        JSONObject body = jsonobject.getJSONObject("REP_BODY");
                        String rspCode = body.getString("RSPCOD");
                        if(rspCode.equals("000000")) {
                            listener.onResponse(JSON.parseObject(body.toJSONString(),modelType));
                        } else {
                            listener.onFailure(body.getString("RSPMSG"));
                        }
                    } catch (Exception e) {
                        listener.onFailure("程序发生意外情况");
                        e.printStackTrace();
                    }

                } else {
                    listener.onFailure("网络连接异常");
                }
            }
        };
        mAsyncTask.execute();
    }


    public void post(final int tag, final String url, final String action, final KeyValuePair keyValuePair, final RequestListener listener, final Type modelType) {
//        if (!NetUtil.isConnected(FansApplication.context)) {
//            Toast.makeText(FansApplication.context, Message.NET_MESSAGE_REQUEST_ERROR, Toast.LENGTH_SHORT).show();
//            return;
//        }
        mAsyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                LogUtil.i(TAG, String.format("url:%s aciton:%s ", url, action));
                listener.onBegin();
            }


            @Override
            protected Object doInBackground(Object[] params) {
                LogUtil.i(this, url.toString());
                String response = null;
                try {
                    response = httpRequest.post(tag, url + action, keyValuePair, modelType);
                    LogUtil.i(this, ""+response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                String response = (String) o;
                LogUtil.i(getClass().getSimpleName(), "response" + response);
                if (response != null) {
                    try {
                        LogUtil.d(this, response);
                        JSONObject jsonobject = JSON.parseObject(response);
                        JSONObject body = jsonobject.getJSONObject("REP_BODY");
                        String rspCode = body.getString("RSPCOD");
                        if(rspCode.equals("000000")) {
                            listener.onResponse(body);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                }
            }
        };
        mAsyncTask.execute();
    }
    public void post(final int tag,final String url, final String parameter, final RequestListener listener,final Type modelType) {
        mAsyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listener.onBegin();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                LogUtil.i(TAG, url.toString());
                LogUtil.i(TAG, parameter.toString());
                String response = "";
                try {
                    response = httpRequest.post(tag, url, parameter, modelType);
                    LogUtil.i(TAG, "" + response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                String response = (String) o;
                LogUtil.i(getClass().getSimpleName(), "response" + response);
                if (response != null) {
                    try {
                        LogUtil.d(this, response);

                    } catch (Exception e) {
                        listener.onFailure("程序发生意外情况");
                        e.printStackTrace();
                    }

                } else {
                    listener.onFailure("网络连接异常");
                }
            }
        };
        mAsyncTask.execute();
    }
    public void get(final int tag, final String url, final RequestListener listener, final Type modelType) {
//        if (!NetUtil.isConnected(FansApplication.context)) {
//            Toast.makeText(FansApplication.context, Message.NET_MESSAGE_ERROR, Toast.LENGTH_SHORT).show();
//            return;
//        }
        mAsyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listener.onBegin();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                String response = httpRequest.get(tag, url, modelType);
                return response;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                String response = (String) o;
                LogUtil.i(getClass().getSimpleName(), "response" + response);
                if (response != null) {
                    try {
                        LogUtil.d(this, response);
                        JSONObject jsonobject = JSON.parseObject(response);
                        JSONObject body = jsonobject.getJSONObject("REP_BODY");
                        String rspCode = body.getString("RSPCOD");
                        if(rspCode.equals("000000")) {
                            listener.onResponse(body);
                        } else {

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                }
            }
        };
        mAsyncTask.execute();
    }

    public void uploadFile(final byte[] bytes, final String url, final String mimeType, final String fileName, final RequestListener listener, final Type modelType) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listener.onBegin();
            }

            @Override
            protected Object doInBackground(Object[] params) {

                String response = null;
                try {
                    response = httpRequest.uploadFile(0, url, bytes, mimeType, fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (response == null) {
                    //listener.onFailure();
                }
                return response;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                String response = (String) o;
                LogUtil.i(getClass().getSimpleName(), "response" + response);
                if (response != null) {
                    try {
                        Object object = JSON.parseObject(response, modelType);
                        listener.onResponse(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //listener.onFailure();
                    }
                }
            }
        };
        asyncTask.execute();
    }



    public void post(String url, String action, Map map, RequestListener listener, Type modelType) {
        post(0, url, action, map, listener, modelType);
    }

    public void post(String url, String action, KeyValuePair valuePair, RequestListener listener, Type modelType) {
        post(0, url, action, valuePair, listener, modelType);
    }

    public void post(String action, RequestListener listener, Type modelType) {
        //post(0, "", action, null, listener, modelType);
    }
    public void post(String url,String parameter, RequestListener listener, Type modelType){
        post(0, url, parameter, listener, modelType);
    }
}
