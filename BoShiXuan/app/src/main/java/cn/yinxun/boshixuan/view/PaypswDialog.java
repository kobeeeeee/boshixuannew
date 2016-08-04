package cn.yinxun.boshixuan.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.yinxun.boshixuan.R;

public class PaypswDialog extends Dialog {
    private EditText editText;
    private Button positiveButton, negativeButton;
    private TextView title;

    public PaypswDialog(Context context) {
        super(context, R.style.Dialog);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_pay_psw, null);
        title = (TextView) mView.findViewById(R.id.textView83);
        editText = (EditText) mView.findViewById(R.id.editText);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);
    }

    public View getEditText(){
        return editText;
    }

    public String getText() { return  editText.getText().toString();}

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
        positiveButton.setOnClickListener(listener);
    }
    /**
     * 取消键监听器
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener);
    }
}