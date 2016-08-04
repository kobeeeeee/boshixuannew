package cn.yinxun.boshixuan.view;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import cn.yinxun.boshixuan.R;

/**
 * Created by yangln10784 on 2016/8/1.
 */
public class SharePopupWindow extends PopupWindow {
    private View shareView;
    public SharePopupWindow(Activity context, View.OnClickListener wechatClick,View.OnClickListener friendClick){
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        shareView = inflater.inflate(R.layout.activity_share, null);
        ImageView wechatView = (ImageView)shareView.findViewById(R.id.weixin);
        ImageView friendView = (ImageView)shareView.findViewById(R.id.friend);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setContentView(shareView);
        this.setBackgroundDrawable(new ColorDrawable());
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.ShareAnimation);
        shareView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = shareView.findViewById(R.id.popLayout).getTop();
                int y = (int)event.getY();
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(y < height){
                        dismiss();
                    }
                }
                return true;
            }
        });
        wechatView.setOnClickListener(wechatClick);
        friendView.setOnClickListener(friendClick);
    }
}
