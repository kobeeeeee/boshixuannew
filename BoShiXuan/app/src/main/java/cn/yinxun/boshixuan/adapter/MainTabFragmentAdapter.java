package cn.yinxun.boshixuan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import cn.yinxun.boshixuan.R;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class MainTabFragmentAdapter implements RadioGroup.OnCheckedChangeListener{
    public static final int TYPE_MAIN = 1;
    public static final int TYPE_ORDER = 2;
    public static final int TYPE_NEWS = 3;
    public static final int TYPE_DEPOSIT = 4;
    public static final int TYPE_USE_HELP = 5;
    private int mType;
    private List<Fragment> mFragmentList;
    private RadioGroup mRadioGroup;
    private FragmentActivity mFragmentActivity;
    private int mFragmentContentId;
    private Fragment mFragment;

    private int mCurrentTab;

    public MainTabFragmentAdapter(FragmentActivity fragmentActivity, List<Fragment> fragments, int fragmentContentId, RadioGroup rgs,int type) {
        this.mFragmentList = fragments;
        this.mRadioGroup = rgs;
        this.mFragmentActivity = fragmentActivity;
        this.mFragmentContentId = fragmentContentId;
        this.mType = type;
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(0));
        ft.commit();
        rgs.setOnCheckedChangeListener(this);
    }

    public MainTabFragmentAdapter(Fragment fragment, List<Fragment> fragments, int fragmentContentId, RadioGroup rgs,int type) {
        this.mFragmentList = fragments;
        this.mRadioGroup = rgs;
        this.mFragment = fragment;
        this.mFragmentActivity = fragment.getActivity();
        this.mFragmentContentId = fragmentContentId;
        this.mType = type;
        FragmentTransaction ft = mFragment.getChildFragmentManager().beginTransaction();
        ft.add(fragmentContentId, fragments.get(0));
        ft.commit();
        rgs.setOnCheckedChangeListener(this);
    }

   @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
       for (int i = 0; i < this.mRadioGroup.getChildCount(); i++) {
           RadioButton button = (RadioButton) this.mRadioGroup.getChildAt(i);
           if (this.mRadioGroup.getChildAt(i).getId() == checkedId) {
               if(this.mType == TYPE_MAIN) {
                   button.setTextColor(this.mFragmentActivity.getResources().getColor(R.color.coffee));
               } else {
                   button.setTextColor(this.mFragmentActivity.getResources().getColor(R.color.white));
               }
               Fragment fragment = this.mFragmentList.get(i);
               FragmentTransaction ft = obtainFragmentTransaction(i);


               getCurrentFragment().onPause();
               if (fragment.isAdded()) {
                   fragment.onResume();
               } else {
                   ft.add(this.mFragmentContentId, fragment);
               }
               showTab(i);
               ft.commit();


           } else {
               if(this.mType == TYPE_MAIN) {
                   button.setTextColor(this.mFragmentActivity.getResources().getColor(R.color.white));
               } else {
                   button.setTextColor(this.mFragmentActivity.getResources().getColor(R.color.coffee));
               }
           }
       }
    }
    private void showTab(int idx) {
        for (int i = 0; i < this.mFragmentList.size(); i++) {
            Fragment fragment = this.mFragmentList.get(i);
            FragmentTransaction ft = obtainFragmentTransaction(idx);

            if (idx == i) {
                ft.show(fragment);
                fragment.setUserVisibleHint(true);
            } else {
                ft.hide(fragment);
                fragment.setUserVisibleHint(false);
            }
            ft.commit();
        }
        this.mCurrentTab = idx;
    }

    public int getCurrentTab() {
        return this.mCurrentTab;
    }

    public Fragment getCurrentFragment() {
        return this.mFragmentList.get(this.mCurrentTab);
    }
    private FragmentTransaction obtainFragmentTransaction(int index) {
        FragmentTransaction ft = this.mFragmentActivity.getSupportFragmentManager().beginTransaction();
        if(this.mType == TYPE_NEWS) {
            ft = mFragment.getChildFragmentManager().beginTransaction();
        }
        return ft;
    }
}
