package cn.yinxun.boshixuan.adapter;

import android.content.Context;
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
import cn.yinxun.boshixuan.activity.OrderDetailActivity;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.model.OrderDetailModel;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderView>{
    private List<OrderDetailModel> mOrderDetailModelList;
    private Context mContext;
    public OrderListAdapter(Context context, List<OrderDetailModel> modelList) {
        this.mOrderDetailModelList = modelList;
        this.mContext = context;
    }
    public void setList(List<OrderDetailModel> modelList) {
        this.mOrderDetailModelList = modelList;
    }
    @Override
    public OrderView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderView(view);
    }

    @Override
    public void onBindViewHolder(OrderView holder, int position) {
        holder.mProductBrand.setText(this.mOrderDetailModelList.get(position).brand_name);
        holder.mProductDayRent.setText(this.mOrderDetailModelList.get(position).rent_price);
        holder.mProductDeposit.setText(this.mOrderDetailModelList.get(position).deposit_price);
        holder.mOrderNo.setText(this.mOrderDetailModelList.get(position).order_number);
        if(this.mOrderDetailModelList.get(position).state.equals("B")) {
            holder.mOrderPayTime.setText("未付款");
        }
        String time = this.mOrderDetailModelList.get(position).stamp_created;
        holder.morderCreateTime.setText(CommonUtil.formatTime(time));
        Picasso.with(this.mContext).load(Config.ROOT_URL + this.mOrderDetailModelList.get(position).small_img).error(this.mContext.getResources().getDrawable(R.drawable.order_default_image)).into(holder.mProductImage);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.mOrderDetailModelList.size();
    }


    public class OrderView extends RecyclerView.ViewHolder {
        @Bind(R.id.productBrand)
        TextView mProductBrand;
        @Bind(R.id.productDeposit)
        TextView mProductDeposit;
        @Bind(R.id.productDayRent)
        TextView mProductDayRent;
        @Bind(R.id.orderNo)
        TextView mOrderNo;
        @Bind(R.id.orderCreateTime)
        TextView morderCreateTime;
        @Bind(R.id.orderPayTime)
        TextView mOrderPayTime;
        @Bind(R.id.productImage)
        ImageView mProductImage;
        @OnClick(R.id.orderLayout)
        public void OnClick(View view) {
            int position = getLayoutPosition()-1;
            OrderDetailModel orderDetailModel = OrderListAdapter.this.mOrderDetailModelList.get(position);
            String state = orderDetailModel.state;
            if(state.equals("B")) {
                CommonUtil.showToast("请联系店铺后支付",OrderListAdapter.this.mContext);
            } else {
                Intent intent = new Intent(OrderListAdapter.this.mContext, OrderDetailActivity.class);
                intent.putExtra("orderId",orderDetailModel.goods_order_id);
                intent.putExtra("rentPrice",orderDetailModel.rent_price);
                intent.putExtra("depositPrice",orderDetailModel.deposit_price);
                intent.putExtra("orderNumber",orderDetailModel.order_number);
                OrderListAdapter.this.mContext.startActivity(intent);
            }
        }
        public OrderView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
