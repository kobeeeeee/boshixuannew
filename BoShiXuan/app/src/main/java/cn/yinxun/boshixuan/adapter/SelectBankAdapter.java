package cn.yinxun.boshixuan.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.event.BankSelectEvent;
import cn.yinxun.boshixuan.network.model.BankDetailModel;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class SelectBankAdapter extends BaseAdapter{
    private List<BankDetailModel> mBankModelList;
    private Activity mContext;
    private int mType;
    public SelectBankAdapter(Activity context, List<BankDetailModel> modelList,int type) {
        this.mContext = context;
        this.mBankModelList = modelList;
        this.mType = type;
    }
    public void setList(List<BankDetailModel> modelList) {
        if(modelList != null) {
            this.mBankModelList = modelList;
        }
    }
    @Override
    public int getCount() {
        return this.mBankModelList==null?0:this.mBankModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mBankModelList.get(position);
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
            convertView = inflater.inflate(R.layout.item_select_bank,null);
            holder = new ViewHolder();
            holder.bankName = (TextView) convertView.findViewById(R.id.bankName);
            holder.bankNo = (TextView) convertView.findViewById(R.id.bankNo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BankDetailModel model = this.mBankModelList.get(position);
        if(model == null) {
            return convertView;
        }
        String cardNumber="";
        if(!TextUtils.isEmpty(model.card_number)) {
            int length = model.card_number.length();
            cardNumber = model.card_number.substring(0,4);
            for(int i=0;i<length-8;i++) {
                cardNumber = cardNumber + "*";
            }
            cardNumber = cardNumber + model.card_number.substring(length-5,length-1);
        }
        holder.bankName.setText(model.bank_name);
        holder.bankNo.setText(cardNumber);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (SelectBankAdapter.this.mType){
                    case 0:
                        selectBankCard(model);
                        break;
                    case 1:
                        BankSelectEvent bankSelectEvent = new BankSelectEvent(model);
                        EventBus.getDefault().post(bankSelectEvent);
                        break;
                }
            }
        });
        return convertView;
    }

    /**
     * 选择银行卡
     * @param model
     */
    public void selectBankCard(BankDetailModel model) {
        Intent intent = new Intent();
        intent.putExtra("bankName",model.bank_name);
        intent.putExtra("bankNo",model.card_number);
        intent.putExtra("bankId",model.bank_id);
        SelectBankAdapter.this.mContext.setResult(SelectBankAdapter.this.mContext.RESULT_OK,intent);
        SelectBankAdapter.this.mContext.finish();
    }


    class ViewHolder{
        public TextView bankName;
        public TextView bankNo;
    }
}
