package cn.yinxun.boshixuan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.yinxun.boshixuan.R;

/**
 * Created by Administrator on 2016/7/29 0029.
 */
public class RechargePayModeAdapter extends BaseAdapter{
    private int mSelectPosition = 0;
    public List<Integer> mPayModeImageList;
    public List<String> mPayModeTextList;
    public Context mContext;
    public RechargePayModeAdapter(Context context,List<String> payModeTextList,List<Integer> payModeImageList) {
        this.mContext = context;
        this.mPayModeTextList = payModeTextList;
        this.mPayModeImageList = payModeImageList;
    }
    public void setSelectPosition(int position) {
        this.mSelectPosition = position;
    }
    public void setList(List<String> walletTextList) {
        this.mPayModeTextList = walletTextList;
    }
    @Override
    public int getCount() {
        return this.mPayModeTextList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mPayModeTextList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.mContext);
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_pay_mode,null);
            holder = new ViewHolder();
            holder.payModeText = (TextView) convertView.findViewById(R.id.payModeText);
            holder.payModeImage = (ImageView) convertView.findViewById(R.id.payModeImage);
            holder.payModeSelect = (ImageView) convertView.findViewById(R.id.payModeSelect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(this.mSelectPosition == position) {
            holder.payModeSelect.setVisibility(View.VISIBLE);
        } else {
            holder.payModeSelect.setVisibility(View.GONE);
        }
        holder.payModeText.setText(this.mPayModeTextList.get(position));
        holder.payModeImage.setImageResource(this.mPayModeImageList.get(position));
        return convertView;
    }
    class ViewHolder{
        public TextView payModeText;
        public ImageView payModeImage;
        public ImageView payModeSelect;
    }
}
