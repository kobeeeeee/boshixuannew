package cn.yinxun.boshixuan.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class BannerViewPagerAdapter extends PagerAdapter {
    private List<ImageView> mViewList = null;
    private int mChildCount = 0;
    private Context mContext;
    public BannerViewPagerAdapter(List<ImageView> views, Context context) {
        this.mViewList = views;
        this.mContext = context;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(mViewList.get(arg1));
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public Object instantiateItem(View arg0, final int arg1) {
        if (mViewList.get(arg1 % mViewList.size()).getParent() != null) {
            ((ViewPager) mViewList.get(arg1 % mViewList.size())
                    .getParent()).removeView(mViewList.get(arg1
                    % mViewList.size()));
        }
        try {
            ((ViewPager) arg0).addView(
                    mViewList.get(arg1 % mViewList.size()), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mViewList.get(arg1 % mViewList.size());
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {

    }
    @Override

    public void notifyDataSetChanged() {

        mChildCount = getCount();

        super.notifyDataSetChanged();

    }



    @Override

    public int getItemPosition(Object object)   {

        if ( mChildCount > 0) {

            mChildCount --;

            return POSITION_NONE;

        }

        return super.getItemPosition(object);

    }
}
