package cn.yinxun.boshixuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.network.model.WithdrawRecordDetailModel;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class WithdrawRecordAdapter extends RecyclerView.Adapter<WithdrawRecordAdapter.WithdrawView> {
    private List<WithdrawRecordDetailModel> mWithdrawRecordDetailModel;
    private Context mContext;
    public WithdrawRecordAdapter(Context context, List<WithdrawRecordDetailModel> model) {
        this.mWithdrawRecordDetailModel = model;
        this.mContext = context;
    }
    public void setList(List<WithdrawRecordDetailModel> modelList) {
        this.mWithdrawRecordDetailModel = modelList;
    }
    @Override
    public WithdrawView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdraw_record, parent, false);
        return new WithdrawView(view);
    }

    @Override
    public void onBindViewHolder(WithdrawView holder, int position) {
        holder.mWithdrawMoney.setText(this.mWithdrawRecordDetailModel.get(position).fetch_money + "元");
        String time = this.mWithdrawRecordDetailModel.get(position).stamp_created;
        holder.mWithdrawTime.setText(CommonUtil.formatTime(time));
        String bankNo = this.mWithdrawRecordDetailModel.get(position).card_number;
        bankNo = CommonUtil.formatBankNo(bankNo);
        holder.mWithdrawBankCard.setText(bankNo);
        holder.mWithdrawBankName.setText(this.mWithdrawRecordDetailModel.get(position).bank_name);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.mWithdrawRecordDetailModel.size();
    }

    public class WithdrawView extends RecyclerView.ViewHolder {
        @Bind(R.id.withdrawMoney)
        TextView mWithdrawMoney;
        @Bind(R.id.withdrawTime)
        TextView mWithdrawTime;
        @Bind(R.id.withdrawBankNo)
        TextView mWithdrawBankCard;
        @Bind(R.id.withdrawBankName)
        TextView mWithdrawBankName;
        public WithdrawView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
