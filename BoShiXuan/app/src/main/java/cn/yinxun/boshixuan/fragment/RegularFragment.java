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
import cn.yinxun.boshixuan.adapter.RegularListAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.RegularDetailModel;
import cn.yinxun.boshixuan.network.model.RegularModel;
import cn.yinxun.boshixuan.network.model.RegularResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.xrecyclerview.ProgressStyle;
import cn.yinxun.boshixuan.xrecyclerview.XRecyclerView;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class RegularFragment extends BaseFragment{
    @Bind(R.id.regularRecyclerView)
    XRecyclerView mRegularRecyclerView;
    private RegularListAdapter mRegularListAdapter;
    private View mView;
    private List<RegularDetailModel> mRegularDetailModelList = new ArrayList<>();
    private NetWorkCallBack mNetWorkCallBack;
    public int mPageNum = 1;
    public int mPageSize = 10;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private int mCurrentListSize = -1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_regular, container, false);
        ButterKnife.bind(this, this.mView);
        return this.mView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        this.mNetWorkCallBack = new NetWorkCallBack();
        getData();
    }
    private void initRecyclerView() {


        this.mRegularListAdapter = new RegularListAdapter(getActivity(),this.mRegularDetailModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        this.mRegularRecyclerView.setLayoutManager(linearLayoutManager);
        this.mRegularRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        this.mRegularRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        this.mRegularRecyclerView.setArrowImageView(R.drawable.icon_font_down_grey);


        this.mRegularRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getData();
            }

            @Override
            public void onLoadMore() {

                isLoadMore = true;
                RegularFragment.this.mPageSize = RegularFragment.this.mPageSize + 10;
                getData();

            }
        });
        this.mRegularRecyclerView.setAdapter(this.mRegularListAdapter);
    }
    private void getData() {
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();
        //传入参数
        Map<String,Object> map = new HashMap<>();
        map.put("page_size",String.valueOf(this.mPageSize));
        map.put("page_num",String.valueOf(this.mPageNum));
        map.put("user_id",userId);
        map.put("user_phone",userPhone);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_FINANCE_PRODUCT,map,RegularFragment.this.mNetWorkCallBack, RegularResponse.class);

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
            if(object instanceof RegularResponse) {
                RegularResponse regularResponse = (RegularResponse)object;
                LogUtil.i(this,"regularResponse = " + regularResponse);
                RegularModel regularModel = regularResponse.financeproduct_list;
                List<RegularDetailModel> regularDetailModelList = regularModel.data_list;
                if(regularDetailModelList.size() == 0) {
                    CommonUtil.showToast("暂无产品",getActivity());
                }
                RegularFragment.this.mRegularListAdapter.setList(regularDetailModelList);
                RegularFragment.this.mRegularListAdapter.notifyDataSetChanged();
                if(isRefresh) {
                    RegularFragment.this.mRegularRecyclerView.refreshComplete();
                    isRefresh = false;
                }
                if(isLoadMore) {
                    if(RegularFragment.this.mCurrentListSize == regularDetailModelList.size()) {
                        CommonUtil.showToast("亲，就只有这么多了",getActivity());
                    }
                    RegularFragment.this.mRegularRecyclerView.loadMoreComplete();
                    isLoadMore = false;
                }
                RegularFragment.this.mCurrentListSize = regularDetailModelList.size();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,getActivity());
            if(isRefresh) {
                RegularFragment.this.mRegularRecyclerView.refreshComplete();
                isRefresh = false;
            }
            if(isLoadMore) {
                RegularFragment.this.mRegularRecyclerView.loadMoreComplete();
                isLoadMore = false;
            }
        }
    }

}
