package cn.yinxun.boshixuan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.activity.BankCardSelectActivity;
import cn.yinxun.boshixuan.activity.CertificationActivity;
import cn.yinxun.boshixuan.activity.MatterRecordActivity;
import cn.yinxun.boshixuan.activity.PasswordManageActivity;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.activity.RechargeRecordActivity;
import cn.yinxun.boshixuan.activity.WithdrawActivity;
import cn.yinxun.boshixuan.activity.WithdrawRecordActivity;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/4 0004.
 */
public class WalletListAdapter extends BaseAdapter{
    public static final int TYPE_WITHDRAW = 0;
    public static final int TYPE_MODIFY_PASSWORD = 1;
    public static final int TYPE_CERTIFICATION = 2;
    public static final int TYPE_BANK_LIST = 3;
    public static final int TYPE_WITHDRAW_RECORD = 4;
    public static final int TYPE_MATTERS_RECORD = 5;
    public static final int TYPE_RECHARGE_RECORD = 6;
    public List<Integer> mWalletImageList;
    public List<String> mWalletTextList;
    public Context mContext;
    public WalletListAdapter(Context context,List<String> walletTextList,List<Integer> walletImageList) {
        this.mContext = context;
        this.mWalletTextList = walletTextList;
        this.mWalletImageList = walletImageList;
    }
    public void setList(List<String> walletTextList) {
        this.mWalletTextList = walletTextList;
    }
    @Override
    public int getCount() {
        return this.mWalletTextList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mWalletTextList.get(position);
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
            convertView = inflater.inflate(R.layout.item_wallet,null);
            holder = new ViewHolder();
            holder.walletTextView = (TextView) convertView.findViewById(R.id.walletListText1);
            holder.walletImageView = (ImageView) convertView.findViewById(R.id.walletImageView);
            holder.walletTextView2 = (TextView) convertView.findViewById(R.id.walletListText2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.walletTextView.setText(this.mWalletTextList.get(position));
        holder.walletImageView.setImageResource(this.mWalletImageList.get(position));
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String isVerify = userInfoBean.getIsVerity();
        if(position == 2) {
            holder.walletTextView2.setVisibility(View.VISIBLE);
            if(isVerify.equals("2")) {
                holder.walletTextView2.setText("审核通过");
            } else if(isVerify.equals("1")){
                holder.walletTextView2.setText("未认证");
            }
        } else {
            holder.walletTextView2.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new WalletItemOnClickListener(position));
        return convertView;
    }
    class ViewHolder{
        public TextView walletTextView;
        public ImageView walletImageView;
        public TextView walletTextView2;
    }
    class WalletItemOnClickListener implements  View.OnClickListener {
        private int mType;
        public WalletItemOnClickListener(int type){
            this.mType = type;
        }
        @Override
        public void onClick(View view) {
            Intent intent = null;
            UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
            String isVerify = userInfoBean.getIsVerity();
            switch (this.mType) {
                case TYPE_WITHDRAW:
                    if(isVerify.equals("0")) {
                        CommonUtil.showToast("请先实名认证",WalletListAdapter.this.mContext);
                        return;
                    }
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH");
                    int time = Integer.valueOf(sdf.format(date));
                    if(time >= 22 || time <6) {
                        CommonUtil.showToast("晚十点到早六点不能体现哦",WalletListAdapter.this.mContext);
                        return;
                    }
                    intent = new Intent(WalletListAdapter.this.mContext, WithdrawActivity.class);
                    break;
                case TYPE_MODIFY_PASSWORD:
                    intent = new Intent(WalletListAdapter.this.mContext, PasswordManageActivity.class);
                    break;
                case TYPE_CERTIFICATION:
                    if(isVerify.equals("2")) {
                        CommonUtil.showToast("已实名认证",WalletListAdapter.this.mContext);
                        return;
                    }
                    intent = new Intent(WalletListAdapter.this.mContext, CertificationActivity.class);
                    break;
                case TYPE_BANK_LIST:
                    intent = new Intent(WalletListAdapter.this.mContext, BankCardSelectActivity.class);
                    intent.putExtra("type",1);
                    break;
                case TYPE_WITHDRAW_RECORD:
                    intent = new Intent(WalletListAdapter.this.mContext, WithdrawRecordActivity.class);
                    break;
                case TYPE_MATTERS_RECORD:
                    intent = new Intent(WalletListAdapter.this.mContext, MatterRecordActivity.class);
                    break;
                case TYPE_RECHARGE_RECORD:
                    intent = new Intent(WalletListAdapter.this.mContext, RechargeRecordActivity.class);
                    break;
            }
            WalletListAdapter.this.mContext.startActivity(intent);
        }
    }
}
