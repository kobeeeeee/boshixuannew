package cn.yinxun.boshixuan.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.activity.AboutActivity;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.activity.UsehelpActivity;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.Constants;
import cn.yinxun.boshixuan.view.CustomDialog;
import cn.yinxun.boshixuan.view.SharePopupWindow;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class SettingFragment extends BaseFragment implements IWXAPIEventHandler {
    private View mView;
    public static final int TYPE_ABOUT = 1;
    public static final int TYPE_SHARE = 2;
    public static final int TYPE_PHONE = 3;
    public static final int TYPE_LOGOUT = 4;
    public static final int TYPE_USEHELP = 5;
    public static final int TYPE_CLEAN = 6;
    public static final int CALL_PHONE_REQUEST_CODE = 10;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.shareLayout)
    RelativeLayout share;
    @Bind(R.id.aboutLayout)
    RelativeLayout aboutus;
    @Bind(R.id.phoneLayout)
    RelativeLayout phone;
    @Bind(R.id.phone)
    TextView call;
    @Bind(R.id.logout)
    ImageView logoutButton;
    @Bind(R.id.usehelpLayout)
    RelativeLayout usehelp;

    public String phoneNum ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, this.mView);
        initHeader();
        phoneNum =  call.getText().toString();
        share.setOnClickListener(new MyOnClickListener(TYPE_SHARE));
        aboutus.setOnClickListener(new MyOnClickListener(TYPE_ABOUT));
        phone.setOnClickListener(new MyOnClickListener(TYPE_PHONE));
        logoutButton.setOnClickListener(new MyOnClickListener(TYPE_LOGOUT));
        usehelp.setOnClickListener(new MyOnClickListener(TYPE_USEHELP));
        return this.mView;
    }

    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("设置");
    }
    private View.OnClickListener wechatClicker = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            shareToWeiXin(1);
        }
    };
    private View.OnClickListener freindClicker = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            shareToWeiXin(0);
        }
    };
//    private void popShareWindows() {
//        ShareSDK.initSDK(getActivity());
//        OnekeyShare oks = new OnekeyShare();
//        //关闭sso授权
////        oks.disableSSOWhenAuthorize();
//
//        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
//        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
//        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//        oks.setTitle("分享");
//        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
////        oks.setTitleUrl("http://sharesdk.cn");
//        // text是分享文本，所有平台都需要这个字段
//        oks.setText("哈喽，我正在使用博时轩奢侈品租赁，这里总有你想要的，一起来玩吧");
//        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
//        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://www.boshixuan.cn/");
//        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
////        oks.setComment("我是测试评论文本");
//        // site是分享此内容的网站名称，仅在QQ空间使用
////        oks.setSite(getString(R.string.app_name));
//        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
////        oks.setSiteUrl("http://sharesdk.cn");
//
//        // 启动分享GUI
//        oks.show(getActivity());
//    }

    /**
     * 分享微信
     * @param flag 0：朋友圈，1：微信好友
     */
    public void shareToWeiXin(int flag){
        //实例化
        IWXAPI wxApi = WXAPIFactory.createWXAPI(getActivity(), Constants.WX_APP_ID,true);
        wxApi.registerApp(Constants.WX_APP_ID);

        String text = "哈喽，我正在使用博时轩奢侈品租赁，这里总有你想要的，一起来玩吧" +
                "点击→http://www.boshixuan.cn/";
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = "http://www.boshixuan.cn/"; // 点击跳转的地址。

        WXMediaMessage msg = new WXMediaMessage(webPage);
        try{
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.launcher_icon);
            Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
            bmp.recycle();
            msg.setThumbImage(thumbBmp);
        }
            catch (Exception e)
        {
            e.printStackTrace();
        }
        msg.title = "分享"; // 链接标题
        msg.mediaObject = textObject;
        msg.description = text;


        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        // 第一个是分享大盘朋友圈，后面是分享给好友
        if(flag == 0) {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        }
        wxApi.sendReq(req);
    }
    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        String result;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = "分享成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = "分享取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = "分享被拒绝";
                break;
            default:
                result = "分享返回";
                break;
        }
        CommonUtil.showToast(result,getActivity());
    }

    class MyOnClickListener implements  View.OnClickListener {
        public int mType;
        public MyOnClickListener(int type) {
            this.mType = type;
        }
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (this.mType) {
                case TYPE_ABOUT:
                    intent = new Intent(getActivity(),AboutActivity.class);
                    startActivity(intent);
                    break;
                case TYPE_SHARE:
                    SharePopupWindow popupwindow = new SharePopupWindow(getActivity(),wechatClicker,freindClicker);
                    popupwindow.showAtLocation(getActivity().findViewById(R.id.shareLayout), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
                    break;
                case TYPE_PHONE:
                  //  final String phoneNum =  call.getText().toString();
                    StringBuffer message = new StringBuffer();
                    message.append("确定要拨打").append(phoneNum).append("么？");
                    CustomDialog.Builder builder=new CustomDialog.Builder(getActivity());
                    builder.setTitle("温馨提示");
                    builder.setMessage(message.toString());
                    builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });
                    builder.setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE},CALL_PHONE_REQUEST_CODE);
                                }
                                else {
                                    callPhone();
                                }
                            }
                            else{
                                callPhone();
                            }

                        }
                    });
                    builder.create().show();
                    break;
                case TYPE_LOGOUT:
                    CustomDialog.Builder buildeLogout=new CustomDialog.Builder(getActivity());
                    buildeLogout.setTitle("提示");
                    buildeLogout.setMessage("真的要退出吗，亲");
                    buildeLogout.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    buildeLogout.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    });
                    buildeLogout.create().show();
                    break;
                case TYPE_USEHELP:
                    intent = new Intent(getActivity(),UsehelpActivity.class);
                    startActivity(intent);
                    break;
                case TYPE_CLEAN:
                    break;
            }
        }
    }

    public void callPhone()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNum));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,
                                           int[] grantResults) {
        if (requestCode == CALL_PHONE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhone();
            } else
            {
                // Permission Denied
                CommonUtil.showToast("Permission Denied",getActivity());
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
