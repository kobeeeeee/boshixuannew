package cn.yinxun.boshixuan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
import cn.yinxun.boshixuan.adapter.RechargeRecordAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.RechargeRecordDetailModel;
import cn.yinxun.boshixuan.network.model.RechargeRecordModel;
import cn.yinxun.boshixuan.network.model.RechargeRecordResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.xrecyclerview.ProgressStyle;
import cn.yinxun.boshixuan.xrecyclerview.XRecyclerView;

/**
 * Created by Administrator on 2016/7/10 0010.
 */
public class RechargeRecordActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.rechargeRecyclerView)
    XRecyclerView mRechargeRecyclerView;
    private RechargeRecordAdapter mRechargeRecordAdapter;
    private List<RechargeRecordDetailModel> mRechargeRecordDetailModelList = new ArrayList<>();
    private NetWorkCallBack mNetWorkCallBack = new NetWorkCallBack();
    private int mPageSize = 10;
    private int mPageNum = 1;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private int mCurrentListSize = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);
        ButterKnife.bind(this);
        initHeader();
        initRecyclerView();
        getData();
    }
    private void initHeader(){
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("充值记录");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initRecyclerView() {
        this.mRechargeRecordAdapter = new RechargeRecordAdapter(this,this.mRechargeRecordDetailModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.mRechargeRecyclerView.setLayoutManager(linearLayoutManager);
        this.mRechargeRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        this.mRechargeRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        this.mRechargeRecyclerView.setArrowImageView(R.drawable.icon_font_down_grey);


        this.mRechargeRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getData();
            }

            @Override
            public void onLoadMore() {
                isLoadMore = true;
                RechargeRecordActivity.this.mPageSize = RechargeRecordActivity.this.mPageSize + 10;
                getData();

            }
        });
        this.mRechargeRecyclerView.setAdapter(this.mRechargeRecordAdapter);
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
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_PUT_IN_LIST,map,RechargeRecordActivity.this.mNetWorkCallBack, RechargeRecordResponse.class);

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
            if(object instanceof RechargeRecordResponse) {
                RechargeRecordResponse rechargeRecordResponse = (RechargeRecordResponse)object;
                LogUtil.i(this,"rechargeRecordResponse = " + rechargeRecordResponse);
                RechargeRecordModel model = rechargeRecordResponse.putin_list;
                RechargeRecordActivity.this.mRechargeRecordDetailModelList = model.data_list;
                if(RechargeRecordActivity.this.mRechargeRecordDetailModelList.size() == 0) {
                    CommonUtil.showToast("亲，暂无充值记录",RechargeRecordActivity.this);
                }
                RechargeRecordActivity.this.mRechargeRecordAdapter.setList(RechargeRecordActivity.this.mRechargeRecordDetailModelList);
                RechargeRecordActivity.this.mRechargeRecordAdapter.notifyDataSetChanged();
                if(isRefresh) {
                    RechargeRecordActivity.this.mRechargeRecyclerView.refreshComplete();
                    isRefresh = false;
                }
                if(isLoadMore) {
                    if(RechargeRecordActivity.this.mCurrentListSize == model.data_list.size()) {
                        CommonUtil.showToast("亲，就只有这么多了",RechargeRecordActivity.this);
                    }
                    RechargeRecordActivity.this.mRechargeRecyclerView.loadMoreComplete();
                    isLoadMore = false;
                }
                RechargeRecordActivity.this.mCurrentListSize = model.data_list.size();
            }
        }

        @Override
        public void onFailure(Object message) {

            String msg = (String) message;
            CommonUtil.showToast(msg,RechargeRecordActivity.this);
            if(isRefresh) {
                RechargeRecordActivity.this.mRechargeRecyclerView.refreshComplete();
                isRefresh = false;
            }
            if(isLoadMore) {
                RechargeRecordActivity.this.mRechargeRecyclerView.loadMoreComplete();
                isLoadMore = false;
            }
        }
    }
}
