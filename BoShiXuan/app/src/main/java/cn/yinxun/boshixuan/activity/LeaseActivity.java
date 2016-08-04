package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.adapter.LeaseListAdapter;

/**
 * Created by Administrator on 2016/7/5 0005.
 */
public class LeaseActivity extends BaseActivity{
    @Bind(R.id.leaseListView)
    ListView mLeaseListView;
    @Bind(R.id.tv_head)
    TextView mLeaseHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.rentLayout)
    RelativeLayout mRentLayout;
    private LeaseListAdapter mLeaseListAdapter;
    private List<String> mLeaseTextCHList;
    private List<String> mLeaseTextENList;
    private List<Integer> mLeaseBgList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lease);
        ButterKnife.bind(this);
        initHeader();
        initData();
        initListView();
    }
    private void initData() {
        this.mLeaseTextCHList = new ArrayList<>();
        this.mLeaseTextCHList.add("包袋");
        this.mLeaseTextCHList.add("首饰");
        this.mLeaseTextCHList.add("腕表");
        this.mLeaseTextCHList.add("其他");


        this.mLeaseTextENList = new ArrayList<>();
        this.mLeaseTextENList.add("Bags");
        this.mLeaseTextENList.add("Jewellery");
        this.mLeaseTextENList.add("Watch");
        this.mLeaseTextENList.add("Others");

        this.mLeaseBgList = new ArrayList<>();
        this.mLeaseBgList.add(R.drawable.lease_bag);
        this.mLeaseBgList.add(R.drawable.lease_jewelry);
        this.mLeaseBgList.add(R.drawable.lease_watch);
        this.mLeaseBgList.add(R.drawable.lease_other);
    }
    private void initHeader() {
        this.mLeaseHeader.setVisibility(View.VISIBLE);
        this.mLeaseHeader.setText("租赁");

        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.mRentLayout.setVisibility(View.VISIBLE);
        this.mRentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeaseActivity.this,LeasingProcessActivity.class);
                startActivity(intent);
            }
        });
    }
    private void initListView() {
        this.mLeaseListAdapter = new LeaseListAdapter(this,this.mLeaseTextCHList,this.mLeaseTextENList,this.mLeaseBgList);
        this.mLeaseListView.setAdapter(this.mLeaseListAdapter);
    }

}
