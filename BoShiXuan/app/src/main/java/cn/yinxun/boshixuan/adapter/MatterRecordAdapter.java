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
import cn.yinxun.boshixuan.network.model.MatterRecordDetailModel;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class MatterRecordAdapter extends RecyclerView.Adapter<MatterRecordAdapter.MatterView> {
    private List<MatterRecordDetailModel> mMatterRecordDetailModelList;
    private Context mContext;
    public MatterRecordAdapter(Context context, List<MatterRecordDetailModel> modelList) {
        this.mMatterRecordDetailModelList = modelList;
        this.mContext = context;
    }

    public void setList(List<MatterRecordDetailModel> modelList) {
        this.mMatterRecordDetailModelList = modelList;
    }
    @Override
    public MatterView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matter_record, parent, false);
        return new MatterView(view);
    }

    @Override
    public void onBindViewHolder(MatterView holder, int position) {
        holder.mMatterName.setText(this.mMatterRecordDetailModelList.get(position).finance_name);
        holder.mBuyMoney.setText(this.mMatterRecordDetailModelList.get(position).finance_money + "元");
        String time = this.mMatterRecordDetailModelList.get(position).stamp_created;
        holder.mBuyTime.setText(CommonUtil.formatTime(time));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.mMatterRecordDetailModelList.size();
    }

    public class MatterView extends RecyclerView.ViewHolder {
        @Bind(R.id.matterName)
        TextView mMatterName;
        @Bind(R.id.buyTime)
        TextView mBuyTime;
        @Bind(R.id.buyMoney)
        TextView mBuyMoney;
        public MatterView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
