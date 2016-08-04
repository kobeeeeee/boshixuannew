package cn.yinxun.boshixuan.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.adapter.SelectBankAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.config.Config;
import cn.yinxun.boshixuan.event.BankSelectEvent;
import cn.yinxun.boshixuan.event.BaseEvent;
import cn.yinxun.boshixuan.network.RequestListener;
import cn.yinxun.boshixuan.network.RequestManager;
import cn.yinxun.boshixuan.network.model.BankDetailModel;
import cn.yinxun.boshixuan.network.model.BankListResponse;
import cn.yinxun.boshixuan.network.model.UnBindBankResponse;
import cn.yinxun.boshixuan.util.CommonUtil;
import cn.yinxun.boshixuan.util.LogUtil;
import cn.yinxun.boshixuan.view.CustomDialog;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class BankCardSelectActivity extends BaseActivity{
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.addLayout)
    RelativeLayout mAddBtn;
    @Bind(R.id.selectBankCardListView)
    ListView mSelectBankListView;
    private SelectBankAdapter mAdapter;
    private NetWorkCallBack mNetWorkCallBack;
    private List<BankDetailModel> mBankDetailModelList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        ButterKnife.bind(this);
        initHeader();
    }

    @Override
    public void onResume() {
        super.onResume();
        initListView();
        getData();
    }

    public void initHeader() {
        this.mHeader.setText("选择银行卡");
        this.mHeader.setVisibility(View.VISIBLE);
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mAddBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BankCardSelectActivity.this,BankCardAddActivity.class);
                startActivity(intent);
            }
        });
    }
    public void initListView() {
        Intent intent = getIntent();
        int type = intent.getIntExtra("type",0);
        if(type == 1) {
            if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        }
        this.mAdapter = new SelectBankAdapter(this,this.mBankDetailModelList,type);
        this.mSelectBankListView.setAdapter(this.mAdapter);
    }
    private void getData() {
        this.mNetWorkCallBack = new NetWorkCallBack();
        //传入参数
        Map<String,Object> map = new HashMap<>();
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_BANK_LIST,map,BankCardSelectActivity.this.mNetWorkCallBack, BankListResponse.class);

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
            if(object instanceof BankListResponse) {
                BankListResponse bankListResponse = (BankListResponse)object;
                LogUtil.i(this,"bankListResponse = " + bankListResponse);
                BankCardSelectActivity.this.mBankDetailModelList = bankListResponse.bank_list;
                BankCardSelectActivity.this.mAdapter.setList(BankCardSelectActivity.this.mBankDetailModelList);
                BankCardSelectActivity.this.mAdapter.notifyDataSetChanged();
                CommonUtil.showToast("刷新成功",BankCardSelectActivity.this);
            }

            if(object instanceof UnBindBankResponse) {
                UnBindBankResponse unBindBankResponse = (UnBindBankResponse)object;
                LogUtil.i(this,"unBindBankResponse = " + unBindBankResponse);
                CommonUtil.showToast("解绑成功",BankCardSelectActivity.this);
                getData();
            }
        }

        @Override
        public void onFailure(Object message) {
            String msg = (String) message;
            CommonUtil.showToast(msg,BankCardSelectActivity.this);
        }
    }
    @Subscribe
    public void onEvent(final BaseEvent event) {
        if(event instanceof BankSelectEvent) {
            StringBuffer message = new StringBuffer();
            message.append("确定解除绑定么");
            CustomDialog.Builder builder=new CustomDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage(message.toString());
            builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BankDetailModel model = ((BankSelectEvent) event).mBankDetailModel;
                    unBindBankCard(model);
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
    /**
     * 解绑银行卡
     * @param model
     */
    public void unBindBankCard(BankDetailModel model) {
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String userId = userInfoBean.getCustId();
        String userPhone = userInfoBean.getCustMobile();
        //传入参数
        Map<String,Object> map = new HashMap<>();
        map.put("bank_id",model.bank_id);
        map.put("user_id",userId);
        map.put("user_phone",userPhone);
        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_UNBANDING_BANK,map,this.mNetWorkCallBack, UnBindBankResponse.class);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
