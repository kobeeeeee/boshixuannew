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
import cn.yinxun.boshixuan.network.model.RechargeRecordDetailModel;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class RechargeRecordAdapter extends RecyclerView.Adapter<RechargeRecordAdapter.RechargeView>{
    private List<RechargeRecordDetailModel> mRechargeRecordDetailModelList;
    private Context mContext;
    public RechargeRecordAdapter(Context context, List<RechargeRecordDetailModel> modelList) {
        this.mRechargeRecordDetailModelList = modelList;
        this.mContext = context;
    }

    public void setList(List<RechargeRecordDetailModel> modelList) {
        this.mRechargeRecordDetailModelList = modelList;
    }
    @Override
    public RechargeView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recharge_record, parent, false);
        return new RechargeView(view);
    }

    @Override
    public void onBindViewHolder(RechargeView holder, int position) {
        holder.mRechargeMode.setText(this.mRechargeRecordDetailModelList.get(position).putin_desc);
        holder.mRechargeMoney.setText(this.mRechargeRecordDetailModelList.get(position).putin_money + "元");
        String time = this.mRechargeRecordDetailModelList.get(position).stamp_created;
        holder.mRechargeTime.setText(CommonUtil.formatTime(time));
        holder.mOrderNo.setText(this.mRechargeRecordDetailModelList.get(position).putin_num);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.mRechargeRecordDetailModelList.size();
    }


    public class RechargeView extends RecyclerView.ViewHolder {
        @Bind(R.id.rechargeMode)
        TextView mRechargeMode;
        @Bind(R.id.rechargeMoney)
        TextView mRechargeMoney;
        @Bind(R.id.rechargeTime)
        TextView mRechargeTime;
        @Bind(R.id.orderNo)
        TextView mOrderNo;
        public RechargeView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
