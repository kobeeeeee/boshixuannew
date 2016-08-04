package cn.yinxun.boshixuan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.adapter.OrderListAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.OrderDetailModel;
import cn.yinxun.boshixuan.network.model.OrderModel;
import cn.yinxun.boshixuan.network.model.OrderResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.xrecyclerview.ProgressStyle;
import cn.yinxun.boshixuan.xrecyclerview.XRecyclerView;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class UnPayOrderFragment extends BaseFragment{
    private View mView;
    private OrderListAdapter mOrderListAdapter;
    @Bind(R.id.dispayOrderRecyclerView)
    XRecyclerView mDisPayOrderRecyclerView;
    private NetWorkCallBack mNetWorkCallBack = new NetWorkCallBack();
    private List<OrderDetailModel> mOrderDetailModelList = new ArrayList<>();
    private int mPageNum = 1;
    private int mPageSize = 10;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private int mCurrentListSize = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_dispay_order, container, false);
        ButterKnife.bind(this, this.mView);
        return this.mView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecycleView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void initRecycleView() {
        this.mOrderListAdapter = new OrderListAdapter(getActivity(),this.mOrderDetailModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        this.mDisPayOrderRecyclerView.setLayoutManager(linearLayoutManager);
        this.mDisPayOrderRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        this.mDisPayOrderRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        this.mDisPayOrderRecyclerView.setArrowImageView(R.drawable.icon_font_down_grey);


        this.mDisPayOrderRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getData();
            }

            @Override
            public void onLoadMore() {
                isLoadMore = true;
                UnPayOrderFragment.this.mPageSize = UnPayOrderFragment.this.mPageSize + 10;
                getData();
            }
        });
        this.mDisPayOrderRecyclerView.setAdapter(this.mOrderListAdapter);
    }
    private void getData() {
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();
        //传入参数
        Map<String,Object> map = new HashMap<>();
        map.put("is_Payment","0");
        map.put("page_size",String.valueOf(this.mPageSize));
        map.put("page_num",String.valueOf(this.mPageNum));
        map.put("user_id",userId);
        map.put("user_phone",userPhone);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_QUERY_ORDER,map,UnPayOrderFragment.this.mNetWorkCallBack, OrderResponse.class);

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
            if(object instanceof OrderResponse) {
                OrderResponse orderResponse = (OrderResponse)object;
                LogUtil.i(this,"orderResponse = " + orderResponse);
                OrderModel orderModel = orderResponse.order_list;
                List<OrderDetailModel> orderDetailModelList = orderModel.data_list;
                if(orderDetailModelList.size() == 0) {
                    CommonUtil.showToast("暂无待支付订单",getActivity());
                }
                UnPayOrderFragment.this.mOrderListAdapter.setList(orderDetailModelList);
                UnPayOrderFragment.this.mOrderListAdapter.notifyDataSetChanged();
                if(isRefresh) {
                    UnPayOrderFragment.this.mDisPayOrderRecyclerView.refreshComplete();
                    isRefresh = false;
                }
                if(isLoadMore) {
                    if(UnPayOrderFragment.this.mCurrentListSize == orderDetailModelList.size()) {
                        CommonUtil.showToast("亲，就只有这么多了",getActivity());
                    }
                    UnPayOrderFragment.this.mDisPayOrderRecyclerView.loadMoreComplete();
                    isLoadMore = false;
                }
                UnPayOrderFragment.this.mCurrentListSize = orderDetailModelList.size();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,getActivity());
            if(isRefresh) {
                UnPayOrderFragment.this.mDisPayOrderRecyclerView.refreshComplete();
                isRefresh = false;
            }
            if(isLoadMore) {
                UnPayOrderFragment.this.mDisPayOrderRecyclerView.loadMoreComplete();
                isLoadMore = false;
            }
        }
    }
}
