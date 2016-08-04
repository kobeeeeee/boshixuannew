package cn.yinxun.boshixuan.view.pickerview;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.yinxun.boshixuan.R;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class Picker {
    private Button doneBtn;
    private Button cancelBtn;
    private PickerView pickerView;
    private List<String> mDatas = new ArrayList<>();
    private Context mContext;
    private OnSelectDoneListener mListener;
    private Picker me;
    private LinearLayout picker;
    private PopupWindowFromBottom menuWindow;
    private View mParentView;
    private String selectText = "";

    /***
     * 构造方法
     * @param context
     * @param parentView 父view
     */
    public Picker(Context context, View parentView, List<String> datas) {
        selectText = datas.get(0);
        mDatas = datas;
        me = this;
        mContext = context;
        mParentView = parentView;
        picker = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.picker_content, null);
        doneBtn = (Button)picker.findViewById(R.id.picker_done);
        cancelBtn = (Button)picker.findViewById(R.id.picker_cancel);
        pickerView = (PickerView)picker.findViewById(R.id.picker_view);
        menuWindow = new PopupWindowFromBottom(context, picker);
        bindBtnsEvent();
        refreshData(datas);
    }

    private void bindBtnsEvent() {
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    mListener.onSelectDone(selectText);
                }
                menuWindow.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuWindow.dismiss();
            }
        });
    }

    /***
     * 供外部调用
     * @param listener
     */
    public void setOnSelectDoneListener(OnSelectDoneListener listener){
        mListener = listener;
    }

    private void refreshData(List<String> datas){

        pickerView.setData(datas);
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(String text,int position) {

                selectText = text;
            }
        });
    }
    public void show(){
        if(!menuWindow.isShowing()){
            refreshData(mDatas);
            menuWindow.showAtLocation(mParentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        }
//        if (menuWindow.isShowing()) {
//            menuWindow.dismiss();
//        } else {
//            selectedItemPosition = 0;
//            refreshData(mDatas);
//            menuWindow.showAtLocation(mParentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
//        }
    }

    /***
     *
     * 选择完毕调用的监听器
     * @author zhangda
     */
    public interface OnSelectDoneListener{
        void onSelectDone(String text);
    }
}
