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
import cn.yinxun.boshixuan.adapter.WithdrawRecordAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.WithdrawRecordDetailModel;
import cn.yinxun.boshixuan.network.model.WithdrawRecordModel;
import cn.yinxun.boshixuan.network.model.WithdrawRecordResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.xrecyclerview.ProgressStyle;
import cn.yinxun.boshixuan.xrecyclerview.XRecyclerView;

/**
 * Created by Administrator on 2016/7/10 0010.
 */
public class WithdrawRecordActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.withdrawRecyclerView)
    XRecyclerView mWithdrawRecyclerView;
    private WithdrawRecordAdapter mWithdrawRecordAdapter;
    private List<WithdrawRecordDetailModel> mWithdrawRecordDetailModelList = new ArrayList<>();
    private NetWorkCallBack mNetWorkCallBack = new NetWorkCallBack();
    private int mPageSize = 10;
    private int mPageNum = 1;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private int mCurrentListSize = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_record);
        ButterKnife.bind(this);
        initHeader();
        initRecyclerView();
        getData();
    }
    private void initHeader(){
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("提现记录");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initRecyclerView() {
        this.mWithdrawRecordAdapter = new WithdrawRecordAdapter(this,this.mWithdrawRecordDetailModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.mWithdrawRecyclerView.setLayoutManager(linearLayoutManager);
        this.mWithdrawRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        this.mWithdrawRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        this.mWithdrawRecyclerView.setArrowImageView(R.drawable.icon_font_down_grey);


        this.mWithdrawRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getData();
            }

            @Override
            public void onLoadMore() {
                isLoadMore = true;
                WithdrawRecordActivity.this.mPageSize = WithdrawRecordActivity.this.mPageSize + 10;
                getData();
            }
        });
        this.mWithdrawRecyclerView.setAdapter(this.mWithdrawRecordAdapter);
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
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_FETCH_CASH_LIST,map,WithdrawRecordActivity.this.mNetWorkCallBack, WithdrawRecordResponse.class);

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
            if(object instanceof WithdrawRecordResponse) {
                WithdrawRecordResponse withdrawRecordResponse = (WithdrawRecordResponse)object;
                LogUtil.i(this,"withdrawRecordResponse = " + withdrawRecordResponse);
                WithdrawRecordModel model = withdrawRecordResponse.fetchcash_list;
                WithdrawRecordActivity.this.mWithdrawRecordDetailModelList = model.data_list;
                if(WithdrawRecordActivity.this.mWithdrawRecordDetailModelList.size() == 0) {
                    CommonUtil.showToast("亲，暂无提现记录",WithdrawRecordActivity.this);
                }
                WithdrawRecordActivity.this.mWithdrawRecordAdapter.setList(WithdrawRecordActivity.this.mWithdrawRecordDetailModelList);
                WithdrawRecordActivity.this.mWithdrawRecordAdapter.notifyDataSetChanged();

                if(isRefresh) {
                    WithdrawRecordActivity.this.mWithdrawRecyclerView.refreshComplete();
                    isRefresh = false;
                }
                if(isLoadMore) {
                    if(WithdrawRecordActivity.this.mCurrentListSize == model.data_list.size()) {
                        CommonUtil.showToast("亲，就只有这么多了",WithdrawRecordActivity.this);
                    }
                    WithdrawRecordActivity.this.mWithdrawRecyclerView.loadMoreComplete();
                    isLoadMore = false;
                }
                WithdrawRecordActivity.this.mCurrentListSize = model.data_list.size();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,WithdrawRecordActivity.this);
            if(isRefresh) {
                WithdrawRecordActivity.this.mWithdrawRecyclerView.refreshComplete();
                isRefresh = false;
            }
            if(isLoadMore) {
                WithdrawRecordActivity.this.mWithdrawRecyclerView.loadMoreComplete();
                isLoadMore = false;
            }
        }
    }
}
