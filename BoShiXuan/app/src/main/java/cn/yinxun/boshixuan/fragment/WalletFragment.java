package cn.yinxun.boshixuan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.adapter.WalletListAdapter;
import cn.yinxun.boshixuan.bean.UserInfoBean;
import cn.yinxun.boshixuan.R;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class WalletFragment extends BaseFragment {
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.walletListView)
    ListView mWalletListView;
    @Bind(R.id.userName)
    TextView mUserName;
    @Bind(R.id.phoneNo)
    TextView mPhoneNo;
    private View mView;
    private List<String> mWalletTextList;
    private List<Integer> mWalletImageList;
    private WalletListAdapter mWalletListAdapter;
//    private NetWorkCallBack mNetWorkCallBack;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_wallet, container, false);
        ButterKnife.bind(this, this.mView);
        return this.mView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initHeader();
        initData();
        initListView();
    }
    public void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("钱包");
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mWalletListAdapter.notifyDataSetChanged();
        getUserInfoData();
    }

    public void initData() {
        getUserInfoData();
        this.mWalletTextList = new ArrayList<>();
        this.mWalletTextList.add("提款到银行账户");
        this.mWalletTextList.add("修改密码");
        this.mWalletTextList.add("实名认证");
        this.mWalletTextList.add("银行卡列表");
        this.mWalletTextList.add("提现记录");
        this.mWalletTextList.add("理财记录");
        this.mWalletTextList.add("充值记录");


        this.mWalletImageList = new ArrayList<>();
        this.mWalletImageList.add(R.drawable.wallet_drawing);
        this.mWalletImageList.add(R.drawable.wallet_password);
        this.mWalletImageList.add(R.drawable.wallet_real_name);
        this.mWalletImageList.add(R.drawable.wallet_bank_card);
        this.mWalletImageList.add(R.drawable.wallet_drawing_record);
        this.mWalletImageList.add(R.drawable.wallet_financing_record);
        this.mWalletImageList.add(R.drawable.wallet_recharge_record);
    }

    private void getUserInfoData() {
        UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
        String phone = userInfoBean.getCustMobile();
        String userName = userInfoBean.getUserName();
        String phoneNo = phone.substring(0,3) + "****" + phone.substring(7,11);
        this.mUserName.setText(userName);
        this.mPhoneNo.setText(phoneNo);
    }

    public void initListView() {
        this.mWalletListAdapter = new WalletListAdapter(getActivity(),this.mWalletTextList,this.mWalletImageList);
        mWalletListView.setAdapter(this.mWalletListAdapter);
    }
//    public void getData() {
//
//        this.mNetWorkCallBack = new NetWorkCallBack();
//        Map<String,Object> map = new HashMap<>();
//        RequestManager.getInstance().post(Config.URL + Config.SLASH, Config.BSX_USER_INFO_QUERY,map,WalletFragment.this.mNetWorkCallBack, UserInfoResponse.class);
//    }
//    private class NetWorkCallBack implements RequestListener {
//
//        @Override
//        public void onBegin() {
//
//        }
//
//        @Override
//        public void onResponse(Object object) {
//            if(object == null) {
//                return;
//            }
//            if(object instanceof UserInfoResponse) {
//                UserInfoResponse userInfoResponse = (UserInfoResponse)object;
//                LogUtil.i(this,"userInfoResponse = " + userInfoResponse);
//                String is_verify = userInfoResponse.is_verify;
//                UserInfoBean userInfoBean = UserInfoBean.getUserInfoBeanInstance();
//                userInfoBean.setIsVerrity(is_verify);
//                WalletFragment.this.mWalletListAdapter.setList(WalletFragment.this.mWalletTextList);
//                WalletFragment.this.mWalletListAdapter.notifyDataSetChanged();
//            }
//        }
//
//        @Override
//        public void onFailure(Object message) {
//            String msg = (String) message;
//            CommonUtil.showToast(msg,getActivity());
//        }
//    }
}
