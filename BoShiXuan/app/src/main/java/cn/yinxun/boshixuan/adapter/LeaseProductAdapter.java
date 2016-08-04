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
import cn.yinxun.boshixuan.activity.ProductDetailActivity;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.model.ProductDetailModel;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class LeaseProductAdapter extends RecyclerView.Adapter<LeaseProductAdapter.ProductView> {
    private Context mContext;
    private List<ProductDetailModel> mProductList;
    public LeaseProductAdapter(Context context,List<ProductDetailModel> productList){
        this.mContext = context;
        this.mProductList = productList;
    }
    public void setList(List<ProductDetailModel> list) {
        this.mProductList = list;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.mProductList.size();
    }

    @Override
    public ProductView onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lease_product, viewGroup, false);
        return new ProductView(view);
    }

    @Override
    public void onBindViewHolder(ProductView holder, int position) {
        holder.itemView.setTag(this.mProductList.get(position));
        holder.productName.setText(this.mProductList.get(position).goods_name);
        Picasso.with(this.mContext).load(Config.ROOT_URL + this.mProductList.get(position).larger_img).error(this.mContext.getResources().getDrawable((R.drawable.lease_product_default))).into(holder.productImage);
    }
    public class ProductView extends RecyclerView.ViewHolder {
        @Bind(R.id.productName)
        TextView productName;
        @Bind(R.id.productImage)
        ImageView productImage;
        @OnClick(R.id.productLayout)
        public void OnClick(View view) {
            int position = getLayoutPosition()-1;
            ProductDetailModel model = LeaseProductAdapter.this.mProductList.get(position);
            Intent intent = new Intent(LeaseProductAdapter.this.mContext, ProductDetailActivity.class);
            intent.putExtra("productId",model.goods_id);
            intent.putExtra("productNo",model.goods_code);
            intent.putExtra("productName",model.goods_name);
            intent.putExtra("productImage",model.larger_img);
            LeaseProductAdapter.this.mContext.startActivity(intent);
        }
        public ProductView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
