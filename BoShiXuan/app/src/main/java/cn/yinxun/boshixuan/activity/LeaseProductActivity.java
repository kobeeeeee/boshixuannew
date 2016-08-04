package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.adapter.LeaseProductAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.ProductDetailModel;
import cn.yinxun.boshixuan.network.model.ProductGoodsModel;
import cn.yinxun.boshixuan.network.model.ProductResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.xrecyclerview.ProgressStyle;
import cn.yinxun.boshixuan.xrecyclerview.XRecyclerView;

/**
 * Created by Administrator on 2016/7/9 0009.
 */
public class LeaseProductActivity extends BaseActivity{
    public static final int TYPE_BAG = 1;
    public static final int TYPE_JEWELLERY = 3;
    public static final int TYPE_WATCH = 2;
    public static final int TYPE_OTHERS = 4;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.leaseProductRecyclerView)
    XRecyclerView mRecyclerView;
    private LeaseProductAdapter mLeaseProductAdapter;
    public NetWorkCallBack mNetWorkCallBack;
    public int mPageNum = 1;
    public int mPageSize = 10;
    private List<ProductDetailModel> mProductDetailModelList;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private int mCurrentListSize = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lease_product);
        ButterKnife.bind(this);
        initHeader();
        initRecyclerView();
        getData();
    }
    private void initHeader() {
        Intent intent = getIntent();
        int type = intent.getIntExtra("type",0);
        String headerString = "";
        switch (type) {
            case TYPE_BAG:
                headerString = "包袋";
                break;
            case TYPE_JEWELLERY:
                headerString = "首饰";
                break;
            case TYPE_WATCH:
                headerString = "手表";
                break;
            case TYPE_OTHERS:
                headerString = "其他";
                break;
        }
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText(headerString);
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void getData() {
        this.mNetWorkCallBack = new NetWorkCallBack();

        Intent intent = getIntent();
        int type = intent.getIntExtra("type",0);
        //传入参数
        Map<String,Object> map = new HashMap<>();
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();
        map.put("goods_type",String.valueOf(type));
        map.put("page_size",String.valueOf(10));
        map.put("page_num",String.valueOf(mPageNum));
        map.put("user_id",userId);
        map.put("user_phone",userPhone);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_RENT_GOODS,map,LeaseProductActivity.this.mNetWorkCallBack, ProductResponse.class);

    }
    private void initRecyclerView() {
        this.mProductDetailModelList = new ArrayList<>();
        this.mLeaseProductAdapter = new LeaseProductAdapter(this,this.mProductDetailModelList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        this.mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        this.mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        this.mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.drawable.icon_font_down_grey);


        this.mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                Intent intent = getIntent();
                int type = intent.getIntExtra("type",0);
                //传入参数
                Map<String,Object> map = new HashMap<>();
                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                String userId = userInfoBean.getCustId();
                String userPhone = userInfoBean.getCustMobile();
                map.put("goods_type",String.valueOf(type));
                map.put("page_size",String.valueOf(mPageSize));
                map.put("page_num",String.valueOf(mPageNum));
                map.put("user_id",userId);
                map.put("user_phone",userPhone);
                RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_RENT_GOODS,map,LeaseProductActivity.this.mNetWorkCallBack, ProductResponse.class);
            }

            @Override
            public void onLoadMore() {
                isLoadMore = true;
                Intent intent = getIntent();
                int type = intent.getIntExtra("type",0);
                mPageSize = mPageSize + 10;
                //传入参数
                Map<String,Object> map = new HashMap<>();
                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
                String userId = userInfoBean.getCustId();
                String userPhone = userInfoBean.getCustMobile();
                map.put("goods_type",String.valueOf(type));
                map.put("page_size",String.valueOf(mPageSize));
                map.put("page_num",String.valueOf(mPageNum));
                map.put("user_id",userId);
                map.put("user_phone",userPhone);
                RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_RENT_GOODS,map,LeaseProductActivity.this.mNetWorkCallBack, ProductResponse.class);

            }
        });
        this.mRecyclerView.setAdapter(this.mLeaseProductAdapter);
    }
    private class NetWorkCallBack implements RequestListener {

        @Override
        public void onBegin() {

        }

        @Override
        public void onResponse(Object object) {
            if(object == null) {
                return;
            }
            if(object instanceof ProductResponse) {
                ProductResponse productResponse = (ProductResponse)object;
                LogUtil.i(this,"productResponse = " + productResponse);
                ProductGoodsModel productGoodsModel = productResponse.goods_list;
                LeaseProductActivity.this.mProductDetailModelList = productGoodsModel.data_list;
                if(LeaseProductActivity.this.mProductDetailModelList.size() == 0) {
                    CommonUtil.showToast("暂无产品",LeaseProductActivity.this);
                }
                LeaseProductActivity.this.mLeaseProductAdapter.setList(LeaseProductActivity.this.mProductDetailModelList);
                LeaseProductActivity.this.mLeaseProductAdapter.notifyDataSetChanged();
                if(isLoadMore) {
                    if(LeaseProductActivity.this.mCurrentListSize == LeaseProductActivity.this.mProductDetailModelList.size()) {
                        CommonUtil.showToast("亲，就只有这么多了",LeaseProductActivity.this);
                    }
                    LeaseProductActivity.this.mRecyclerView.loadMoreComplete();
                    isLoadMore = false;
                }
                if(isRefresh) {
                    LeaseProductActivity.this.mRecyclerView.refreshComplete();
                    isRefresh = false;
                }
                LeaseProductActivity.this.mCurrentListSize = LeaseProductActivity.this.mProductDetailModelList.size();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,LeaseProductActivity.this);
            if(isLoadMore) {
                LeaseProductActivity.this.mRecyclerView.loadMoreComplete();
                isLoadMore = false;
            }
            if(isRefresh) {
                LeaseProductActivity.this.mRecyclerView.refreshComplete();
                isRefresh = false;
            }
        }
    }
}
