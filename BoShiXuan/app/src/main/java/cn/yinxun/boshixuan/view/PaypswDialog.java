package cn.yinxun.boshixuan.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.util.SecurityPasswordUtil;

public class PaypswDialog extends Dialog {
    private EditText editText;
    private Button positiveButton, negativeButton;
    private TextView title;
    private Activity mContext;
    SecurityPasswordUtil mUtil;

    public PaypswDialog(Activity context) {
        super(context, R.style.Dialog);
        this.mContext = context;
        setCustomDialog();
    }

    private void setCustomDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pay_psw, null);
        positiveButton = (Button) view.findViewById(R.id.positiveButton);
        negativeButton = (Button) view.findViewById(R.id.negativeButton);
        this.mUtil = new SecurityPasswordUtil(this.mContext,view);
        this.mUtil.setOnEditTextListener(new SecurityPasswordUtil.OnEditTextListener() {
            @Override
            public void inputComplete(int state, String password) {
                LogUtil.i(this,"输入完成");
            }
        });
        super.setContentView(view);
    }


    public String getPassword() {
        String text = "";
        if(this.mUtil != null) {
            text = this.mUtil.getEditNumber();
        }
        return  text;
    }

    @Override
    public void setContentView(int layoutResID) {
    }

    public void setContentView(View view, RelativeLayout.LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener){
        if(positiveButton != null) {
            positiveButton.setOnClickListener(listener);
        }
    }
    /**
     * 取消键监听器
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener){
        if(negativeButton != null) {
            negativeButton.setOnClickListener(listener);
        }
    }
}