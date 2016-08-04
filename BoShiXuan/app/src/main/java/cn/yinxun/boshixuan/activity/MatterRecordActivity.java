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
import cn.yinxun.boshixuan.adapter.MatterRecordAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.MatterRecordDetailModel;
import cn.yinxun.boshixuan.network.model.MatterRecordModel;
import cn.yinxun.boshixuan.network.model.MatterRecordResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.xrecyclerview.ProgressStyle;
import cn.yinxun.boshixuan.xrecyclerview.XRecyclerView;

/**
 * Created by Administrator on 2016/7/10 0010.
 */
public class MatterRecordActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.matterRecyclerView)
    XRecyclerView mMatterRecyclerView;
    private MatterRecordAdapter mMatterRecordAdapter;
    private List<MatterRecordDetailModel> mMatterRecordModelList = new ArrayList<>();
    private NetWorkCallBack mNetWorkCallBack = new NetWorkCallBack();
    private int mPageSize = 10;
    private int mPageNum = 1;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private int mCurrentListSize = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matter_record);
        ButterKnife.bind(this);
        initHeader();
        initRecyclerView();
        getData();
    }
    private void initHeader(){
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("理财记录");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initRecyclerView() {
        this.mMatterRecordAdapter = new MatterRecordAdapter(this,this.mMatterRecordModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        this.mMatterRecyclerView.setLayoutManager(linearLayoutManager);
        this.mMatterRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        this.mMatterRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        this.mMatterRecyclerView.setArrowImageView(R.drawable.icon_font_down_grey);


        this.mMatterRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getData();
            }

            @Override
            public void onLoadMore() {
                isLoadMore = true;
                MatterRecordActivity.this.mPageSize = MatterRecordActivity.this.mPageSize + 10;
                getData();

            }
        });
        this.mMatterRecyclerView.setAdapter(this.mMatterRecordAdapter);
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
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_FINANCE_LIST,map,MatterRecordActivity.this.mNetWorkCallBack, MatterRecordResponse.class);

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
            if(object instanceof MatterRecordResponse) {
                MatterRecordResponse matterRecordResponse = (MatterRecordResponse)object;
                LogUtil.i(this,"matterRecordResponse = " + matterRecordResponse);
                MatterRecordModel model = matterRecordResponse.finance_list;
                MatterRecordActivity.this.mMatterRecordModelList = model.data_list;
                if(MatterRecordActivity.this.mMatterRecordModelList.size() == 0) {
                    CommonUtil.showToast("亲，还没有购买过理财产品呢",MatterRecordActivity.this);
                }
                MatterRecordActivity.this.mMatterRecordAdapter.setList(MatterRecordActivity.this.mMatterRecordModelList);
                MatterRecordActivity.this.mMatterRecordAdapter.notifyDataSetChanged();
                if(isRefresh) {
                    MatterRecordActivity.this.mMatterRecyclerView.refreshComplete();
                    isRefresh = false;
                }
                if(isLoadMore) {
                    if(MatterRecordActivity.this.mCurrentListSize == model.data_list.size()) {
                        CommonUtil.showToast("亲，就只有这么多了",MatterRecordActivity.this);
                    }
                    MatterRecordActivity.this.mMatterRecyclerView.loadMoreComplete();
                    isLoadMore = false;
                }
                MatterRecordActivity.this.mCurrentListSize = model.data_list.size();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,MatterRecordActivity.this);
            if(isRefresh) {
                MatterRecordActivity.this.mMatterRecyclerView.refreshComplete();
                isRefresh = false;
            }
            if(isLoadMore) {
                MatterRecordActivity.this.mMatterRecyclerView.loadMoreComplete();
                isLoadMore = false;
            }
        }
    }
}
