package cn.yinxun.boshixuan.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import cn.yinxun.boshixuan.R;

/**
 * 自定义密码输入框，仿微信支付密码输入框
 * Created by Administrator on 2016/8/4 0004.
 *
 */
public class SecurityPasswordUtil {

    private EditText one_pwd;
    private EditText two_pwd;
    private EditText three_pwd;
    private EditText five_pwd;
    private EditText four_pwd;
    private EditText six_pwd;
    private TextWatcher tw_pwd;
    private AsteriskPasswordTransformationMethod asteriskPassword;
    private onFocusListeners onfocuslistener;
    private String inputnumber = "";
    private onKeyListeners onkeylistener;
    private static final int POP_SOFT_KEYBOARD_TIME = 200;

    private OnEditTextListener onEditTextListener;



    public OnEditTextListener getOnEditTextListener() {
        return onEditTextListener;
    }

    public void setOnEditTextListener(OnEditTextListener onEditTextListener) {
        this.onEditTextListener = onEditTextListener;
    }

    public interface OnEditTextListener{
        public void  inputComplete(int state,String password);
    }

    public SecurityPasswordUtil(Context context,View view) {
        init(context,view);
    }


    private void init(final Context context, View view) {

        one_pwd= (EditText)view.findViewById(R.id.firstEditText);
        two_pwd= (EditText)view.findViewById(R.id.secondEditText);
        three_pwd= (EditText)view.findViewById(R.id.thirdEditText);
        four_pwd= (EditText)view.findViewById(R.id.forthEditText);
        five_pwd= (EditText)view.findViewById(R.id.fifthEditText);
        six_pwd= (EditText)view.findViewById(R.id.sixthEditText);
        asteriskPassword =  new AsteriskPasswordTransformationMethod();
        onfocuslistener = new onFocusListeners();
        onkeylistener  =  new onKeyListeners();
        editPwdWatcher(context);
        //设置更改默认密码样式
        one_pwd.setTransformationMethod(asteriskPassword);
        two_pwd.setTransformationMethod(asteriskPassword);
        three_pwd.setTransformationMethod(asteriskPassword);
        four_pwd.setTransformationMethod(asteriskPassword);
        five_pwd.setTransformationMethod(asteriskPassword);
        six_pwd.setTransformationMethod(asteriskPassword);
        //设置字符改变监听
        one_pwd.addTextChangedListener(tw_pwd);
        two_pwd.addTextChangedListener(tw_pwd);
        three_pwd.addTextChangedListener(tw_pwd);
        four_pwd.addTextChangedListener(tw_pwd);
        five_pwd.addTextChangedListener(tw_pwd);
        six_pwd.addTextChangedListener(tw_pwd);
        //焦点监听
        one_pwd.setOnFocusChangeListener(onfocuslistener);
        two_pwd.setOnFocusChangeListener(onfocuslistener);
        three_pwd.setOnFocusChangeListener(onfocuslistener);
        four_pwd.setOnFocusChangeListener(onfocuslistener);
        five_pwd.setOnFocusChangeListener(onfocuslistener);
        six_pwd.setOnFocusChangeListener(onfocuslistener);
        //删除按钮监听
        one_pwd.setOnKeyListener(onkeylistener);
        two_pwd.setOnKeyListener(onkeylistener);
        three_pwd.setOnKeyListener(onkeylistener);
        four_pwd.setOnKeyListener(onkeylistener);
        five_pwd.setOnKeyListener(onkeylistener);
        six_pwd.setOnKeyListener(onkeylistener);

        Timer timer = new Timer();
        timer.schedule(
                new TimerTask()
                       {

                           public void run()
                           {
                               //当出现支付窗口时弹出软键盘
                               one_pwd.requestFocus();
                               InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                               imm.showSoftInput(one_pwd,0);
//                             imm.showSoftInputFromInputMethod(one_pwd.getWindowToken(),0);
                           }

                       },
                POP_SOFT_KEYBOARD_TIME);

    }
    /**
     * 字符改变监听
     * @param context
     */
    private void editPwdWatcher(final Context context){
        tw_pwd = new TextWatcher() {


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1) {
                    if (one_pwd.isFocused()) {
                        one_pwd.clearFocus();
                        two_pwd.requestFocus();
                    }else if (two_pwd.isFocused()) {
                        two_pwd.clearFocus();
                        three_pwd.requestFocus();
                    }else if(three_pwd.isFocused()){
                        three_pwd.clearFocus();
                        four_pwd.requestFocus();
                    }else if(four_pwd.isFocused()){
                        four_pwd.clearFocus();
                        five_pwd.requestFocus();
                    }else if(five_pwd.isFocused()){
                        five_pwd.clearFocus();
                        six_pwd.requestFocus();
                    }else if(six_pwd.isFocused()){
//                        six_pwd.clearFocus();
//                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(six_pwd.getWindowToken(), 0);
                        inputnumber = getEditNumber();
                        if (onEditTextListener != null) {
                            onEditTextListener.inputComplete(1, inputnumber);
                        }
                    }
                }
            }
        };
    }
    /**
     * 更改密码默认替代字符,系统默认的字符太小了
     * @author hezenan
     *
     */
    class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {

        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }
        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source;
            }
            @Override
            public int length() {
                return mSource.length();
            }

            @Override
            public char charAt(int index) {
                return '●';
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end);
            }

        }
    }
    class onFocusListeners implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.firstEditText:
                    if(hasFocus&&one_pwd.getText().length()==1&&inputnumber.length()==6){
                        one_pwd.clearFocus();
                        two_pwd.requestFocus();
                    }
                    break;
                case R.id.secondEditText:
                    if(hasFocus&&two_pwd.getText().length()==1&&inputnumber.length()==6){
                        two_pwd.clearFocus();
                        three_pwd.requestFocus();
                    }
                    break;
                case R.id.thirdEditText:
                    if(hasFocus&&three_pwd.getText().length()==1&&inputnumber.length()==6){
                        three_pwd.clearFocus();
                        four_pwd.requestFocus();
                    }
                    break;
                case R.id.forthEditText:
                    if(hasFocus&&four_pwd.getText().length()==1&&inputnumber.length()==6){
                        four_pwd.clearFocus();
                        five_pwd.requestFocus();
                    }
                    break;
                case R.id.fifthEditText:
                    if(hasFocus&&five_pwd.getText().length()==1&&inputnumber.length()==6){
                        five_pwd.clearFocus();
                        six_pwd.requestFocus();
                    }
                    break;
            }
        }

    }

    private int count = 0;
    /**
     * 删除按钮监听
     * @author hezenan
     *
     */
    class onKeyListeners implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_DEL) {
                //不知道不知道什么原因，点击一次删除按钮会调两次这个方法，所有处理一下，两次当一次
                count++;
                if (count < 2) {
                    return false;
                }
                count=0;
                inputnumber = "";
                if(six_pwd.isFocused()){
                    six_pwd.clearFocus();
                    five_pwd.requestFocus();
                }else if (five_pwd.isFocused()) {
                    five_pwd.clearFocus();
                    four_pwd.requestFocus();
                }else if (four_pwd.isFocused()) {
                    four_pwd.clearFocus();
                    three_pwd.requestFocus();
                }else if(three_pwd.isFocused()){
                    three_pwd.clearFocus();
                    two_pwd.requestFocus();
                }else if(two_pwd.isFocused()){
                    two_pwd.clearFocus();
                    one_pwd.requestFocus();
                }

            }
            return false;
        }

    }

    public String getEditNumber(){
        String number = one_pwd.getText().toString();
        number+=two_pwd.getText().toString();
        number+=three_pwd.getText().toString();
        number+=four_pwd.getText().toString();
        number+=five_pwd.getText().toString();
        number+=six_pwd.getText().toString();
        return number;
    }

}
