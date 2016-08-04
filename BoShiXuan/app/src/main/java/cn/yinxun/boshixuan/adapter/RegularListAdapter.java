package cn.yinxun.boshixuan.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.activity.RegularBuyActivity;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.model.RegularDetailModel;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class RegularListAdapter extends RecyclerView.Adapter<RegularListAdapter.RegularView>{
    private List<RegularDetailModel> mRegularDetailModelList;
    private Activity mContext;
    public RegularListAdapter(Activity context, List<RegularDetailModel> modelList) {
        this.mRegularDetailModelList = modelList;
        this.mContext = context;
    }
    public void setList(List<RegularDetailModel> modelList) {
        this.mRegularDetailModelList = modelList;
    }
    @Override
    public RegularView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_regular, parent, false);
        return new RegularView(view);
    }
    @Override
    public void onBindViewHolder(RegularView holder, int position) {
        holder.mRegularProductName.setText(this.mRegularDetailModelList.get(position).finance_name);
        holder.mRegularProductIncome.setText(this.mRegularDetailModelList.get(position).interest_rate + "%");
        holder.mRegularProductDay.setText(this.mRegularDetailModelList.get(position).invest_days + "天");
        holder.mRegularProductMin.setText(this.mRegularDetailModelList.get(position).invest_money + "元起投");
        holder.mRegularDesc.setText(this.mRegularDetailModelList.get(position).product_desc);
        Picasso.with(this.mContext).load(Config.ROOT_URL + this.mRegularDetailModelList.get(position).finance_img).error(this.mContext.getResources().getDrawable(R.drawable.regular_default_image)).into(holder.mRegularImage);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.mRegularDetailModelList.size();
    }

    public class RegularView extends RecyclerView.ViewHolder {
        @Bind(R.id.regularProductName)
        TextView mRegularProductName;
        @Bind(R.id.regularProductIncome)
        TextView mRegularProductIncome;
        @Bind(R.id.regularProductDay)
        TextView mRegularProductDay;
        @Bind(R.id.regularProductMin)
        TextView mRegularProductMin;
        @Bind(R.id.regularDesc)
        TextView mRegularDesc;
        @Bind(R.id.regularImage)
        ImageView mRegularImage;
        @OnClick(R.id.regularLayout)
        public void OnClick(View view) {
            UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
            String isVerify = userInfoBean.getIsVerity();
            if(isVerify.equals("0")) {
                CommonUtil.showCertificationDialog(RegularListAdapter.this.mContext,"购买定期");
                return;
            }
            int position = getLayoutPosition() - 1;
            RegularDetailModel regularDetailModel = RegularListAdapter.this.mRegularDetailModelList.get(position);
            Intent intent = new Intent(RegularListAdapter.this.mContext, RegularBuyActivity.class);
            intent.putExtra("productId",regularDetailModel.product_id);
            intent.putExtra("productName",regularDetailModel.finance_name);
            intent.putExtra("interestRate",regularDetailModel.interest_rate + "%");
            intent.putExtra("investDay",regularDetailModel.invest_days + "天");
            intent.putExtra("investMoney",regularDetailModel.invest_money);
            RegularListAdapter.this.mContext.startActivity(intent);
        }
        public RegularView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
