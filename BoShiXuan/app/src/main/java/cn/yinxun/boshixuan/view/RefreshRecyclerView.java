package cn.yinxun.boshixuan.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import cn.yinxun.boshixuan.R;

/**
 * Created by Administrator on 2016/7/10 0010.
 */
public class RefreshRecyclerView extends PtrFrameLayout{
    private Context mContext;
    private RecyclerView mRecyclerView;
    private PtrFrameLayout.LayoutParams params;
    private LoadMoreRecyclerListener mOnScrollListener;
    private RecyclerMode mode;
    private RefreshHeader mHeaderView;
    private float mDownY;


    public RefreshRecyclerView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public RefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mRecyclerView = new RecyclerView(mContext);
        params = new PtrFrameLayout.LayoutParams(
                PtrFrameLayout.LayoutParams.MATCH_PARENT, PtrFrameLayout.LayoutParams.MATCH_PARENT);
        mRecyclerView.setLayoutParams(params);
        addView(mRecyclerView);

        setResistance(1.7f);
        setRatioOfHeaderHeightToRefresh(1.2f);
        setDurationToClose(200);
        setDurationToCloseHeader(1000);
        setKeepHeaderWhenRefresh(true);
        setPullToRefresh(false);
        //ViewPager滑动冲突
        disableWhenHorizontalMove(true);

        mHeaderView = new RefreshHeader(mContext);
        setHeaderView(mHeaderView);
        addPtrUIHandler(mHeaderView);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (null == adapter) {
            throw new NullPointerException("adapter cannot be null");
        }
        mRecyclerView.setAdapter(adapter);
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
        if (null == itemAnimator) {
            return;
        }
        mRecyclerView.setItemAnimator(itemAnimator);
    }

    public void setMode(RecyclerMode mode) {
        this.mode = mode;
        if (RecyclerMode.NONE == mode || RecyclerMode.BOTTOM == mode) {

            setEnabled(false);
        } else {
            setEnabled(true);
        }

        if (null != mOnScrollListener) {
            mOnScrollListener.setMode(mode);
        }
    }


    public void addOnScrollListener(RecyclerView.OnScrollListener listener) {
        if (null == listener) {
            return;
        }
        if (listener instanceof LoadMoreRecyclerListener) {
            mOnScrollListener = (LoadMoreRecyclerListener) listener;
            mRecyclerView.addOnScrollListener(mOnScrollListener);
        } else {
            mRecyclerView.addOnScrollListener(listener);
        }
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mRecyclerView.getLayoutManager();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decor) {
        if (null == decor) {
            return;
        }
        mRecyclerView.addItemDecoration(decor);
    }

    public void setOnBothRefreshListener(final OnBothRefreshListener listener) {
        if (RecyclerMode.NONE == mode || null == listener) {
            return;
        }

        if (RecyclerMode.BOTH == mode || RecyclerMode.TOP == mode) {
            //当前允许下拉刷新

            setPtrHandler(new PtrHandler() {
                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    listener.onPullDown();

                }
            });
        }

        if (RecyclerMode.BOTH == mode || RecyclerMode.BOTTOM == mode) {
            if (null != mOnScrollListener) {
                mOnScrollListener.setOnBothRefreshListener(listener);
            }
        }
    }

    public void setOnPullDownListener(final OnPullDownListener listener) {
        if (RecyclerMode.NONE == mode || null == listener) {
            return;
        }

        if (RecyclerMode.BOTH == mode || RecyclerMode.TOP == mode) {
            //当前允许下拉刷新
            setPtrHandler(new PtrHandler() {

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                    return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    listener.onPullDown();
                }
            });
        }
    }

    public void setOnLoadMoreListener(final OnLoadMoreListener listener) {
        if (RecyclerMode.NONE == mode || null == listener) {
            return;
        }

        if (RecyclerMode.BOTH == mode || RecyclerMode.BOTTOM == mode) {
            if (null != mOnScrollListener) {
                mOnScrollListener.setOnLoadMoreListener(listener);
            }
        }
    }

    public RecyclerView real() {
        return mRecyclerView;
    }

    public void onRefreshCompleted() {
        if (RecyclerMode.BOTH == mode || RecyclerMode.TOP == mode) {
            refreshComplete();
        }
        if (RecyclerMode.BOTH == mode || RecyclerMode.BOTTOM == mode) {
            if (null != mOnScrollListener) {
                mOnScrollListener.onRefreshComplete();
            }
        }

    }


//    @Override
//    public boolean dispatchTouchEvent(MotionEvent e) {
//        Log.d("Refresh", "" + mOnScrollListener.isLoading);
//        if (mOnScrollListener.isLoading || RefreshHeader.isLoading) {
//            return true;
//        }
//        return super.dispatchTouchEvent(e);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float currentY = event.getY();
                    if ((currentY - mDownY) > 0) {
                        //手指向下
                        mOnScrollListener.isLoadingMoreEnabled = false;
                    } else {
                        //手指向上
                        mOnScrollListener.isLoadingMoreEnabled = true;
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }


    public static class LoadMoreRecyclerListener extends RecyclerView.OnScrollListener {

        private Context mContext;
        private RecyclerMode mode;

        private RefreshRecyclerViewAdapter mAdapter;

        public int firstVisibleItemPosition;
        private int lastVisibleItemPosition;

        private int[] mPositions;
        private int mScrollState;
        private OnLoadMoreListener mOnLoadMoreListener;
        private OnBothRefreshListener mOnBothRefreshListener;
        private RefreshLoadingLayout mFooterLoadingLayout;

        /**
         * 是否是正则加载状态
         */
        private boolean isLoading = false;
        /**
         * 通过滚动方向判断是否允许上拉加载
         */
        public boolean isLoadingMoreEnabled = true;
        /**
         * 加载更多之前RecyclerView的item数量
         */
        private int mOldItemCount;

        private boolean hasCompleted = false;

        public LoadMoreRecyclerListener(Context context, RecyclerMode mode) {
            this.mContext = context;
            this.mode = mode;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            hasCompleted = false;

            isLoadingMoreEnabled = dy > 0;

            RecyclerView.LayoutManager mLayoutManager = recyclerView.getLayoutManager();

            //初始化firstVisibleItemPosition和lastVisibleItemPosition
            if (null != mLayoutManager) {
                if (mLayoutManager instanceof LinearLayoutManager) {
                    firstVisibleItemPosition = ((LinearLayoutManager) mLayoutManager)
                            .findFirstVisibleItemPosition();
                    lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager)
                            .findLastVisibleItemPosition();
                } else if (mLayoutManager instanceof GridLayoutManager) {
                    firstVisibleItemPosition = ((GridLayoutManager) mLayoutManager)
                            .findFirstVisibleItemPosition();
                    lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager)
                            .findLastVisibleItemPosition();
                } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager mStaggeredGridLayoutManager =
                            (StaggeredGridLayoutManager) mLayoutManager;
                    if (null == mPositions) {
                        mPositions = new int[mStaggeredGridLayoutManager.getSpanCount()];
                    }
                    mStaggeredGridLayoutManager.findFirstVisibleItemPositions(mPositions);
                    mStaggeredGridLayoutManager.findLastVisibleItemPositions(mPositions);
                    firstVisibleItemPosition = getFirst(mPositions);
                    lastVisibleItemPosition = getLast(mPositions);
                } else {
                    throw new IllegalArgumentException(
                            "The layoutManager must be one of LinearLayoutManager, " +
                                    "GridLayoutManager or StaggeredGridLayoutManager");
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (RecyclerMode.BOTH != mode && RecyclerMode.BOTTOM != mode) {
                return;
            }

            if (null == recyclerView.getAdapter()
                    || !(recyclerView.getAdapter() instanceof RefreshRecyclerViewAdapter)) {
                return;
            }

            mAdapter = (RefreshRecyclerViewAdapter) recyclerView.getAdapter();

            mScrollState = newState;
            RecyclerView.LayoutManager mLayoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();

            if ((visibleItemCount > 0
                    && mScrollState == RecyclerView.SCROLL_STATE_IDLE
                    && lastVisibleItemPosition >= totalItemCount - 1)
                    && isLoadingMoreEnabled) {

                if (isLoading) {
                    return;
                }

                if (hasCompleted) {
                    hasCompleted = !hasCompleted;
                    return;
                }

                if (RecyclerMode.BOTH == mode) {
                    if (null != mOnBothRefreshListener) {
                        addFooterLoadinLayout(recyclerView);
                        mOnBothRefreshListener.onLoadMore();
                        return;
                    }
                } else if (RecyclerMode.BOTTOM == mode) {
                    if (null != mOnLoadMoreListener) {
                        addFooterLoadinLayout(recyclerView);
                        mOnLoadMoreListener.onLoadMore();
                        return;
                    }
                }
            }
        }

        /**
         * 添加LoadMore布局
         */
        private void addFooterLoadinLayout(RecyclerView recyclerView) {
            isLoading = true;
            if (null == mFooterLoadingLayout) {
                mFooterLoadingLayout = new RotateLoadingLayout(mContext, RecyclerMode.BOTTOM);
            }
            mAdapter.addFooterView(mFooterLoadingLayout);
            mOldItemCount = mAdapter.getItemCount();
            recyclerView.smoothScrollToPosition(mOldItemCount - 1);
            mFooterLoadingLayout.onRefresh();
            mFooterLoadingLayout.setVisibility(View.VISIBLE);
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.mOnLoadMoreListener = onLoadMoreListener;
        }

        public void setOnBothRefreshListener(OnBothRefreshListener onBothRefreshListener) {
            this.mOnBothRefreshListener = onBothRefreshListener;
        }

        /**
         * StaggeredGridLayoutManager firstVisibleItemPosition
         *
         * @param mPositions
         * @return
         */
        private int getFirst(int[] mPositions) {
            int first = mPositions[0];
            for (int value : mPositions) {
                if (value < first) {
                    first = value;
                }
            }
            return first;
        }

        /**
         * StaggeredGridLayoutManager lastVisibleItemPosition
         *
         * @param mPositions
         * @return
         */
        private int getLast(int[] mPositions) {
            int last = mPositions[0];
            for (int value : mPositions) {
                if (value > last) {
                    last = value;
                }
            }
            return last;
        }

        public void setMode(RecyclerMode mode) {
            this.mode = mode;
        }

        public void onRefreshComplete() {
            if (null != mAdapter && mAdapter.getFootersCount() > 0) {
                if (mAdapter.getLastFooter() instanceof RefreshLoadingLayout) {
                    isLoading = false;
                    hasCompleted = true;
                    mAdapter.removeFooter(mFooterLoadingLayout);
                }
            }
        }

    }

    public abstract static class RefreshLoadingLayout extends FrameLayout {

        static final int ROTATION_ANIMATION_DURATION = 1200;

        static final Interpolator ANIMATION_INTERPOLATOR = new LinearInterpolator();

        protected Context mContext;
        protected RecyclerMode mode;

        public RefreshLoadingLayout(Context context, RecyclerMode mode) {
            super(context);
            this.mContext = context;
            this.mode = mode;

            init();

//            reset();
        }

        protected void init() {
        }

        public final void onRefresh() {
            onRefreshImpl();
        }

//        public void reset() {
//            onResetImpl();
//        }

        protected abstract void onRefreshImpl();

//        protected abstract void onResetImpl();
    }

    public interface OnLoadMoreListener {

        void onLoadMore();
    }

    public interface OnBothRefreshListener {

        void onPullDown();

        void onLoadMore();
    }

    public interface OnPullDownListener {

        void onPullDown();

    }

    public enum RecyclerMode {

        NONE, BOTH, TOP, BOTTOM

    }

    public static class RefreshHeader extends FrameLayout implements PtrUIHandler {

        private final static String KEY_SharedPreferences = "cube_ptr_classic_last_update";
        private static SimpleDateFormat sDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        private int mRotateAniTime = 150;
        private RotateAnimation mFlipAnimation;
        private RotateAnimation mReverseFlipAnimation;
        private TextView mTitleTextView;
        private View mRotateView;
        private View mProgressBar;
        private long mLastUpdateTime = -1;
        private TextView mLastUpdateTextView;
        private String mLastUpdateTimeKey;
        private boolean mShouldShowLastUpdate;
        public static boolean isLoading;

        private LastUpdateTimeUpdater mLastUpdateTimeUpdater = new LastUpdateTimeUpdater();

        public RefreshHeader(Context context) {
            super(context);
            initViews(null);
        }

        public RefreshHeader(Context context, AttributeSet attrs) {
            super(context, attrs);
            initViews(attrs);
        }

        public RefreshHeader(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            initViews(attrs);
        }

        protected void initViews(AttributeSet attrs) {
            TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.PtrClassicHeader, 0, 0);
            if (arr != null) {
                mRotateAniTime = arr.getInt(R.styleable.PtrClassicHeader_ptr_rotate_ani_time, mRotateAniTime);
                arr.recycle();
            }

            buildAnimation();
            View header = LayoutInflater.from(getContext()).inflate(R.layout.header_refresh, this);

            mRotateView = header.findViewById(R.id.ptr_classic_header_rotate_view);

            mTitleTextView = (TextView) header.findViewById(R.id.ptr_classic_header_rotate_view_header_title);
            mLastUpdateTextView = (TextView) header.findViewById(R.id.ptr_classic_header_rotate_view_header_last_update);
            mProgressBar = header.findViewById(R.id.ptr_classic_header_rotate_view_progressbar);

            resetView();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (mLastUpdateTimeUpdater != null) {
                mLastUpdateTimeUpdater.stop();
            }
        }

        public void setRotateAniTime(int time) {
            if (time == mRotateAniTime || time == 0) {
                return;
            }
            mRotateAniTime = time;
            buildAnimation();
        }

        /**
         * Specify the last update time by this key string
         *
         * @param key
         */
        public void setLastUpdateTimeKey(String key) {
            if (TextUtils.isEmpty(key)) {
                return;
            }
            mLastUpdateTimeKey = key;
        }

        /**
         * Using an object to specify the last update time.
         *
         * @param object
         */
        public void setLastUpdateTimeRelateObject(Object object) {
            setLastUpdateTimeKey(object.getClass().getName());
        }

        private void buildAnimation() {
            mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            mFlipAnimation.setInterpolator(new LinearInterpolator());
            mFlipAnimation.setDuration(mRotateAniTime);
            mFlipAnimation.setFillAfter(true);

            mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
            mReverseFlipAnimation.setDuration(mRotateAniTime);
            mReverseFlipAnimation.setFillAfter(true);
        }

        private void resetView() {
            hideRotateView();
            mProgressBar.setVisibility(INVISIBLE);
        }

        private void hideRotateView() {
            mRotateView.clearAnimation();
            mRotateView.setVisibility(INVISIBLE);
        }

        @Override
        public void onUIReset(PtrFrameLayout frame) {
            resetView();
            mShouldShowLastUpdate = true;
            tryUpdateLastUpdateTime();
        }

        @Override
        public void onUIRefreshPrepare(PtrFrameLayout frame) {

            mShouldShowLastUpdate = true;
            tryUpdateLastUpdateTime();
            mLastUpdateTimeUpdater.start();

            mProgressBar.setVisibility(INVISIBLE);

            mRotateView.setVisibility(VISIBLE);
            mTitleTextView.setVisibility(VISIBLE);
            if (frame.isPullToRefresh()) {
                mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
            } else {
                mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
            }
        }

        @Override
        public void onUIRefreshBegin(PtrFrameLayout frame) {

            mShouldShowLastUpdate = false;
            hideRotateView();
            mProgressBar.setVisibility(VISIBLE);
            mTitleTextView.setVisibility(GONE);
            mTitleTextView.setText(R.string.cube_ptr_refreshing);
            tryUpdateLastUpdateTime();
            mLastUpdateTimeUpdater.stop();
            isLoading = true;
        }

        @Override
        public void onUIRefreshComplete(PtrFrameLayout frame) {
            isLoading = false;
            hideRotateView();
            mProgressBar.setVisibility(INVISIBLE);
//            resetView();
//            mShouldShowLastUpdate = true;
//            tryUpdateLastUpdateTime();

//            mTitleTextView.setVisibility(VISIBLE);
//            mTitleTextView.setText(getResources().getString(R.string.cube_ptr_refresh_complete));

            // update last update time
//            SharedPreferences sharedPreferences = getContext().getSharedPreferences(KEY_SharedPreferences, 0);
//            if (!TextUtils.isEmpty(mLastUpdateTimeKey)) {
//                mLastUpdateTime = new Date().getTime();
//                sharedPreferences.edit().putLong(mLastUpdateTimeKey, mLastUpdateTime).apply();
//            }
        }

        private void tryUpdateLastUpdateTime() {
            if (TextUtils.isEmpty(mLastUpdateTimeKey) || !mShouldShowLastUpdate) {
                mLastUpdateTextView.setVisibility(GONE);
            } else {
                String time = getLastUpdateTime();
                if (TextUtils.isEmpty(time)) {
                    mLastUpdateTextView.setVisibility(GONE);
                } else {
                    mLastUpdateTextView.setVisibility(VISIBLE);
                    mLastUpdateTextView.setText(time);
                }
            }
        }

        private String getLastUpdateTime() {

            if (mLastUpdateTime == -1 && !TextUtils.isEmpty(mLastUpdateTimeKey)) {
                mLastUpdateTime = getContext().getSharedPreferences(KEY_SharedPreferences, 0).getLong(mLastUpdateTimeKey, -1);
            }
            if (mLastUpdateTime == -1) {
                return null;
            }
            long diffTime = new Date().getTime() - mLastUpdateTime;
            int seconds = (int) (diffTime / 1000);
            if (diffTime < 0) {
                return null;
            }
            if (seconds <= 0) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(getContext().getString(R.string.cube_ptr_last_update));

            if (seconds < 60) {
                sb.append(seconds + getContext().getString(R.string.cube_ptr_seconds_ago));
            } else {
                int minutes = (seconds / 60);
                if (minutes > 60) {
                    int hours = minutes / 60;
                    if (hours > 24) {
                        Date date = new Date(mLastUpdateTime);
                        sb.append(sDataFormat.format(date));
                    } else {
                        sb.append(hours + getContext().getString(R.string.cube_ptr_hours_ago));
                    }

                } else {
                    sb.append(minutes + getContext().getString(R.string.cube_ptr_minutes_ago));
                }
            }
            return sb.toString();
        }

        @Override
        public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            final int mOffsetToRefresh = frame.getOffsetToRefresh();
            final int currentPos = ptrIndicator.getCurrentPosY();
            final int lastPos = ptrIndicator.getLastPosY();

            if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
                if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                    crossRotateLineFromBottomUnderTouch(frame);
                    if (mRotateView != null) {
                        mRotateView.clearAnimation();
                        mRotateView.startAnimation(mReverseFlipAnimation);
                    }
                }
            } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
                if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                    crossRotateLineFromTopUnderTouch(frame);
                    if (mRotateView != null) {
                        mRotateView.clearAnimation();
                        mRotateView.startAnimation(mFlipAnimation);
                    }
                }
            }
        }

        private void crossRotateLineFromTopUnderTouch(PtrFrameLayout frame) {
            if (!frame.isPullToRefresh()) {
                mTitleTextView.setVisibility(VISIBLE);
                mTitleTextView.setText(R.string.cube_ptr_release_to_refresh);
            }
        }

        private void crossRotateLineFromBottomUnderTouch(PtrFrameLayout frame) {
            mTitleTextView.setVisibility(VISIBLE);
            if (frame.isPullToRefresh()) {
                mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
            } else {
                mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_down_to_refresh));
            }
        }

        private class LastUpdateTimeUpdater implements Runnable {

            private boolean mRunning = false;

            private void start() {
                if (TextUtils.isEmpty(mLastUpdateTimeKey)) {
                    return;
                }
                mRunning = true;
                run();
            }

            private void stop() {
                mRunning = false;
                removeCallbacks(this);
            }

            @Override
            public void run() {
                tryUpdateLastUpdateTime();
                if (mRunning) {
                    postDelayed(this, 1000);
                }
            }
        }
    }

    public static class RotateLoadingLayout extends RefreshLoadingLayout {

        private RelativeLayout mRootView;
        private TextView mRefreshText;
        private TextView mRefreshTime;
        private ImageView mImage;

        private String mRefreshing;
        private String mLoading;
        //        private String mComplete;
        private String mLastUpdateTime;
        private Matrix mImageMatrix;
        private RotateAnimation mRotateAnimation;

        private Drawable imageDrawable;
        private LayoutParams layoutParams;

        private boolean mUseIntrinsicAnimation;


        public RotateLoadingLayout(Context context, RecyclerMode mode) {
            super(context, mode);
        }

        @Override
        protected void init() {
            View view = LayoutInflater.from(mContext).inflate(R.layout.loadinglayout, this, false);
            mRootView = (RelativeLayout) view.findViewById(R.id.fl_root);
            mRefreshText = (TextView) view.findViewById(R.id.tv_refresh);
            mRefreshTime = (TextView) view.findViewById(R.id.tv_refresh_time);
            mImage = (ImageView) view.findViewById(R.id.iv_image);

            layoutParams = (LayoutParams) mRootView.getLayoutParams();

            mRefreshing = mContext.getResources().getString(R.string.refreshing);
            mLoading = mContext.getResources().getString(R.string.loading);
//            mComplete = mContext.getResources().getString(R.string.complete);
            mLastUpdateTime = getLastTime();
            if (!TextUtils.isEmpty(mLastUpdateTime)) {
                mRefreshTime.setText(mLastUpdateTime);
            }

            imageDrawable = mContext.getResources().getDrawable(R.mipmap.progress_img);
            mImage.setScaleType(ImageView.ScaleType.MATRIX);
            mImageMatrix = new Matrix();
            mImage.setImageMatrix(mImageMatrix);

            mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            mRotateAnimation.setInterpolator(ANIMATION_INTERPOLATOR);
            mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
            mRotateAnimation.setRepeatCount(Animation.INFINITE);
            mRotateAnimation.setRepeatMode(Animation.RESTART);


            addView(view);
        }

        @Override
        protected void onRefreshImpl() {
            mImage.setImageDrawable(imageDrawable);
            if (null != mImage.getAnimation()) {
                mImage.clearAnimation();
            }
            mImage.startAnimation(mRotateAnimation);

            if (RecyclerMode.BOTH == mode || RecyclerMode.TOP == mode) {
                if (null != mRefreshText) {
                    mRefreshText.setText(mRefreshing);
                }
                if (null != mRefreshTime) {
                    if (TextUtils.isEmpty(mLastUpdateTime)) {
                        mRefreshTime.setVisibility(View.GONE);
                    } else {
                        mRefreshTime.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (null != mRefreshText) {
                    mRefreshText.setText(mLoading);
                }
            }
        }

//        @Override
//        protected void onResetImpl() {
////            if (null != mRefreshText) {
////                mRefreshText.setText(mComplete);
////            }
//            mImage.setImageDrawable(getResources().getDrawable(R.mipmap.refresh_complete));
//            mImage.setVisibility(View.VISIBLE);
//
//            mImage.clearAnimation();
//            resetImageRotation();
//
//            mRefreshTime.setVisibility(GONE);
//        }

        public final void setHeight(int height) {
            layoutParams.height = height;
            requestLayout();
        }

        public final void setWidth(int width) {
            layoutParams.width = width;
            requestLayout();
        }

        public final int getContentSize() {
            return mRootView.getHeight();
        }

        public void setLayoutPadding(int left, int top, int right, int bottom) {
            layoutParams.setMargins(left, top, right, bottom);
            setLayoutParams(layoutParams);
        }

        private void resetImageRotation() {
            if (null != mImageMatrix) {
                mImageMatrix.reset();
                mImage.setImageMatrix(mImageMatrix);
            }
        }

        private String getLastTime() {
            SharedPreferences sp = mContext.getSharedPreferences("RefreshRecycleView", Activity.MODE_PRIVATE);
            String lastUpdateTime = sp.getString("LastUpdateTime", "");
            return lastUpdateTime;
        }
    }

    public static class RefreshRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER_VIEW = Integer.MIN_VALUE;
        private static final int TYPE_FOOTER_VIEW = TYPE_HEADER_VIEW + 1;

        /**
         * The real adapter for RecyclerView
         */
        private RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;

        private ArrayList<View> mHeaderViews = new ArrayList<>();
        private ArrayList<View> mFooterViews = new ArrayList<>();

        private ArrayList<Integer> mHeaderViewTypes = new ArrayList<>();
        private ArrayList<Integer> mFooterViewTypes = new ArrayList<>();
        private int mHeaderViewType;

        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;
        private RecyclerView.LayoutManager mLayoutManager;

        public RefreshRecyclerViewAdapter() {

        }

        public RefreshRecyclerViewAdapter(RecyclerView.Adapter adapter) {
            setAdapter(adapter);
        }

        /**
         * Set the adapter for RecyclerView
         *
         * @param adapter
         */
        public void setAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
            if (null != adapter) {
                if (!(adapter instanceof RecyclerView.Adapter)) {
                    throw new RuntimeException("A RecyclerView.Adapter is Need");
                }

                if (null != mAdapter) {
                    notifyItemRangeRemoved(getHeadersCount(), mAdapter.getItemCount());
                    mAdapter.unregisterAdapterDataObserver(mDataObserver);
                }

                mAdapter = adapter;
                mAdapter.registerAdapterDataObserver(mDataObserver);
                notifyItemRangeInserted(getHeadersCount(), mAdapter.getItemCount());
            }
        }

        /**
         * @return RecyclerView.Adapter
         */
        public RecyclerView.Adapter getAdapter() {
            return mAdapter;
        }

        /**
         * remove view From headerViews
         *
         * @param v
         * @return
         */
        public boolean removeHeader(View v) {
            if (mHeaderViews.contains(v)) {
                mHeaderViews.remove(v);
                notifyDataSetChanged();
                return true;
            }
            return false;
        }

        /**
         * 获取最后一个footer，用于判断当前是否处于LoadMore状态
         *
         * @return
         */
        public View getLastFooter() {
            return mFooterViews.get(mFooterViews.size() - 1);
        }

        /**
         * 获取第一个header，用于判断当前是否处于PullDown状态
         *
         * @return
         */
        public View getFirstHeader() {
            return mHeaderViews.get(0);
        }


        /**
         * remove view From footerViews
         *
         * @param v
         * @return
         */
        public boolean removeFooter(View v) {
            if (mFooterViews.contains(v)) {
                mFooterViews.remove(v);
                notifyDataSetChanged();
                return true;
            }
            return false;
        }

        @Override
        public int getItemCount() {
            if (null != mAdapter) {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            }
            return getHeadersCount() + getFootersCount();
        }

        @Override
        public long getItemId(int position) {
            int headersCount = getHeadersCount();
            if (null != mFooterViews && position >= headersCount) {
                int adjustPosition = position - headersCount;
                int adapterCount = mAdapter.getItemCount();
                if (adjustPosition < adapterCount) {
                    return mAdapter.getItemId(adjustPosition);
                }
            }
            return -1;
        }

        @Override
        public int getItemViewType(int position) {
            int mHeadersCount = getHeadersCount();
            if (null != mAdapter) {
                int itemCount = mAdapter.getItemCount();
                if (position < mHeadersCount) {
                    //current itemType is Header
                    mHeaderViewType = TYPE_HEADER_VIEW + position;
                    mHeaderViewTypes.add(mHeaderViewType);
                    return mHeaderViewType;
                } else if (position >= mHeadersCount && position < mHeadersCount + itemCount) {
                    //current itemType is item defined by user
                    int itemViewType = mAdapter.getItemViewType(position - mHeadersCount);
                    if (itemViewType <= TYPE_HEADER_VIEW + mHeadersCount) {
                        throw new IllegalArgumentException("your adapter's return value of " +
                                "getItemViewType() must > (Integer.MinValue + your headersCount)");
                    }
                    return itemViewType;
                } else {
                    //current itemType is Footer
                    int mFooterViewType = TYPE_FOOTER_VIEW + position - itemCount;
                    mFooterViewTypes.add(mFooterViewType);
                    return mFooterViewType;
                }
            } else {
                return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (null != mAdapter) {
                if (mHeaderViewTypes.contains(viewType)) {
                    //currentPosition in mHeaderViews is (viewType - TYPE_HEADER_VIEW)
                    return new RecyclerHeaderViewHolder(mHeaderViews.get(viewType - TYPE_HEADER_VIEW));
                } else if (mFooterViewTypes.contains(viewType)) {
                    //currentPosition in mFooterViews is (viewType - headersCount - TYPE_FOOTER_VIEW)
                    return new RecyclerHeaderViewHolder(mFooterViews.get(viewType - getHeadersCount()
                            - TYPE_FOOTER_VIEW));
                } else {
                    return mAdapter.onCreateViewHolder(parent, viewType);
                }
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (null != mAdapter) {
                if (position >= getHeadersCount() && position < getHeadersCount() + mAdapter.getItemCount()) {
                    mAdapter.onBindViewHolder(holder, position - getHeadersCount());
                    if (null != mOnItemClickListener) {
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mOnItemClickListener.onItemClick(holder, position - getHeadersCount());
                            }
                        });
                    }
                    if (null != mOnItemLongClickListener) {
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return mOnItemLongClickListener.onItemLongCLick(holder, position - getHeadersCount());
                            }
                        });
                    }
                } else {
                    if (null != mLayoutManager && mLayoutManager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                                StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                                StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT);
                        params.setFullSpan(true);
                        holder.itemView.setLayoutParams(params);
                    }
//                ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
//                if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
//                    ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
//                }
                }
            }
        }

        /**
         * @return headerView's counts
         */
        public Integer getHeadersCount() {
            if (null != mHeaderViews) {
                return mHeaderViews.size();
            }
            return 0;
        }

        public ArrayList<View> getHeaderViews() {
            return mHeaderViews;
        }

        /**
         * @return footerView's counts
         */
        public Integer getFootersCount() {
            if (null != mFooterViews) {
                return mFooterViews.size();
            }
            return 0;
        }

        public ArrayList<View> getFooterViews() {
            return mFooterViews;
        }

        public void addHeaderView(View v, int position) {
            if (null != v) {
                if (mHeaderViews.contains(v)) {
                    mHeaderViews.remove(v);
                }
                if (position > mHeaderViews.size()) {
                    position = mHeaderViews.size();
                }
                mHeaderViews.add(position, v);
                notifyDataSetChanged();
            }
        }

        /**
         * Add a header for RefreshRecyclerView
         *
         * @param v
         */
        public void addHeaderView(View v) {
            if (null != v) {
                if (mHeaderViews.contains(v)) {
                    removeHeader(v);
                }
                mHeaderViews.add(v);
                notifyDataSetChanged();
            }
        }

        /**
         * Add a footer for RefreshRecyclerView
         *
         * @param v
         */
        public void addFooterView(View v) {
            if (null != v) {
                if (mFooterViews.contains(v)) {
                    removeFooter(v);
                }
                mFooterViews.add(v);
                notifyDataSetChanged();
            }
        }

        /**
         * 判断当前是否是header
         *
         * @param position
         * @return
         */
        public boolean isHeader(int position) {
            return getHeadersCount() > 0 && position <= getHeadersCount() - 1;
        }

        /**
         * 判断当前是否是footer
         *
         * @param position
         * @return
         */
        public boolean isFooter(int position) {
            int lastPosition = getItemCount() - getFootersCount();
            return getFootersCount() > 0 && position >= lastPosition;
        }

        public void putLayoutManager(RecyclerView.LayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

            @Override
            public void onChanged() {
                super.onChanged();
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                notifyItemRangeChanged(positionStart + getHeadersCount(), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                notifyItemRangeInserted(positionStart + getHeadersCount(), itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                notifyItemRangeRemoved(positionStart + getHeadersCount(), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                notifyItemRangeChanged(fromPosition + getHeadersCount(), toPosition + getHeadersCount() + itemCount);
            }
        };

        public interface OnItemClickListener {
            void onItemClick(RecyclerView.ViewHolder holder, int position);
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public interface OnItemLongClickListener {
            boolean onItemLongCLick(RecyclerView.ViewHolder holder, int position);
        }

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            this.mOnItemLongClickListener = onItemLongClickListener;
        }

    }

    public static class RecyclerHeaderViewHolder extends RecyclerView.ViewHolder {

        public RecyclerHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class HeaderSapnSizeLookUp extends GridLayoutManager.SpanSizeLookup {

        private RefreshRecyclerViewAdapter mAdapter;
        private int mSpanSize;

        public HeaderSapnSizeLookUp(RefreshRecyclerViewAdapter adapter, int spanSize) {
            this.mAdapter = adapter;
            this.mSpanSize = spanSize;
        }

        @Override
        public int getSpanSize(int position) {
            boolean isHeaderOrFooter = mAdapter.isHeader(position) || mAdapter.isFooter(position);
            return isHeaderOrFooter ? mSpanSize : 1;
        }
    }

    public static class RefreshRecyclerAdapterManager {

        private RefreshRecyclerView recyclerView;
        private RefreshRecyclerViewAdapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private RecyclerMode mode;

        private OnBothRefreshListener mOnBothRefreshListener;
        private OnPullDownListener mOnPullDownListener;
        private OnLoadMoreListener mOnLoadMoreListener;
        private RefreshRecyclerViewAdapter.OnItemClickListener mOnItemClickListener;
        private RefreshRecyclerViewAdapter.OnItemLongClickListener mOnItemLongClickListener;
        private RecyclerView.ItemDecoration mDecor;
        private LoadMoreRecyclerListener loadMoreRecyclerListener;
        private RecyclerView.ItemAnimator mItemAnimator;

        public RefreshRecyclerAdapterManager(
                RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
            this.mAdapter = new RefreshRecyclerViewAdapter(adapter);

            if (null == layoutManager) {
                throw new NullPointerException("Couldn't resolve a null object reference of LayoutManager");
            }
            this.mLayoutManager = layoutManager;
            if (layoutManager instanceof GridLayoutManager) {
                //如果是header或footer，设置其充满整列
                ((GridLayoutManager) layoutManager).setSpanSizeLookup(
                        new HeaderSapnSizeLookUp(mAdapter, ((GridLayoutManager) layoutManager).getSpanCount()));
            }
            this.mLayoutManager = layoutManager;
        }

        private RefreshRecyclerAdapterManager getInstance() {
            return RefreshRecyclerAdapterManager.this;
        }

        public RefreshRecyclerAdapterManager addHeaderView(View v) {
            mAdapter.addHeaderView(v);
            return getInstance();
        }

        public RefreshRecyclerAdapterManager addHeaderView(View v, int position) {
            mAdapter.addHeaderView(v, position);
            return getInstance();
        }

        public RefreshRecyclerAdapterManager addFooterView(View v) {
            mAdapter.addFooterView(v);
            return getInstance();
        }

        public RefreshRecyclerAdapterManager removeHeaderView(View v) {
            mAdapter.removeHeader(v);
            return getInstance();
        }

        public RefreshRecyclerViewAdapter getAdapter() {
            return mAdapter;
        }

        public RefreshRecyclerAdapterManager setOnBothRefreshListener(OnBothRefreshListener onBothRefreshListener) {
            this.mOnBothRefreshListener = onBothRefreshListener;
            return getInstance();
        }

        public RefreshRecyclerAdapterManager setOnPullDownListener(OnPullDownListener onPullDownListener) {
            this.mOnPullDownListener = onPullDownListener;
            return getInstance();
        }

        public RefreshRecyclerAdapterManager setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.mOnLoadMoreListener = onLoadMoreListener;
            return getInstance();
        }

        public RefreshRecyclerAdapterManager removeFooterView(View v) {
            mAdapter.removeFooter(v);
            return getInstance();
        }

        public RefreshRecyclerAdapterManager setMode(RecyclerMode mode) {
            this.mode = mode;
            return getInstance();
        }

//    public RefreshRecyclerAdapterManager setLayoutManager(RecyclerView.LayoutManager layoutManager){
//        if (null == layoutManager){
//            throw new NullPointerException("Couldn't resolve a null object reference of LayoutManager");
//        }
//        this.mLayoutManager = layoutManager;
//        if (layoutManager instanceof GridLayoutManager){
//            //如果是header或footer，设置其充满整列
//            ((GridLayoutManager)layoutManager).setSpanSizeLookup(
//                    new HeaderSapnSizeLookUp(mAdapter, ((GridLayoutManager) layoutManager).getSpanCount()));
//        }
//        return getInstance();
//    }

        public RefreshRecyclerAdapterManager setOnItemClickListener
                (RefreshRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
            return getInstance();
        }

        public RefreshRecyclerAdapterManager setOnItemLongClickListener(
                RefreshRecyclerViewAdapter.OnItemLongClickListener onItemLongClickListener) {
            this.mOnItemLongClickListener = onItemLongClickListener;
            return getInstance();
        }

        public void onRefreshCompleted() {
            if (null == recyclerView) {
                throw new NullPointerException("recyclerView is null");
            }
            if (null == mAdapter) {
                throw new NullPointerException("adapter is null");
            }
            if (RecyclerMode.BOTH == mode || RecyclerMode.TOP == mode) {
                recyclerView.refreshComplete();
            }
            if (RecyclerMode.BOTH == mode || RecyclerMode.BOTTOM == mode) {
                if (null != loadMoreRecyclerListener) {
                    loadMoreRecyclerListener.onRefreshComplete();
                }
            }

        }

        public RefreshRecyclerView getRecyclerView() {
            if (null == recyclerView) {
                throw new NullPointerException("Couldn't resolve a null object reference of RefreshRecyclerView");
            }
            return recyclerView;
        }

        public RefreshRecyclerAdapterManager setItemAnimator(RecyclerView.ItemAnimator itemAnimator) {
            this.mItemAnimator = itemAnimator;
            return getInstance();
        }

        public RefreshRecyclerAdapterManager addItemDecoration(RecyclerView.ItemDecoration decor) {
            this.mDecor = decor;
            return getInstance();
        }

        public void into(RefreshRecyclerView recyclerView, Context context) {
            if (null == recyclerView) {
                throw new NullPointerException("Couldn't resolve a null object reference of RefreshRecyclerView");
            }

            mAdapter.putLayoutManager(mLayoutManager);
            recyclerView.setAdapter(mAdapter);

            recyclerView.setMode(mode);
            //为RefreshRecyclerView添加滚动监听
            loadMoreRecyclerListener = new LoadMoreRecyclerListener(context, mode);
            recyclerView.addOnScrollListener(loadMoreRecyclerListener);
            recyclerView.addItemDecoration(mDecor);
            if (RecyclerMode.BOTH == mode) {
                if (null != mOnBothRefreshListener) {
                    recyclerView.setOnBothRefreshListener(mOnBothRefreshListener);
                }
            } else if (RecyclerMode.TOP == mode) {
                if (null != mOnPullDownListener) {
                    recyclerView.setOnPullDownListener(mOnPullDownListener);
                }
            } else if (RecyclerMode.BOTTOM == mode) {
                if (null != mOnLoadMoreListener) {
                    recyclerView.setOnLoadMoreListener(mOnLoadMoreListener);
                }
            }

            recyclerView.addItemDecoration(mDecor);
            recyclerView.setItemAnimator(mItemAnimator);

            mAdapter.setOnItemClickListener(mOnItemClickListener);
            mAdapter.setOnItemLongClickListener(mOnItemLongClickListener);
            recyclerView.setLayoutManager(mLayoutManager);
            this.recyclerView = recyclerView;
        }

    }

    public static class RecyclerViewManager {

        private static final RecyclerViewManager mInstance = new RecyclerViewManager();

        private static RefreshRecyclerAdapterManager refreshRecyclerAdapterManager;

        private RecyclerViewManager() {

        }

        public static RecyclerViewManager getInstance() {
            return mInstance;
        }

        public static RefreshRecyclerAdapterManager with(
                RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
            if (null == adapter) {
                throw new NullPointerException("Couldn't resolve a null object reference of RecyclerView.Adapter");
            }
            if (null == layoutManager) {
                throw new NullPointerException("Couldn't resolve a null object reference of RecyclerView.LayoutManager");
            }
            return getRefreshRecyclerAdapterManager(adapter, layoutManager);
        }

        private static RefreshRecyclerAdapterManager getRefreshRecyclerAdapterManager(
                RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager) {
            refreshRecyclerAdapterManager = new RefreshRecyclerAdapterManager(adapter, layoutManager);
            return refreshRecyclerAdapterManager;
        }

        public static void setMode(RecyclerMode mode) {
            if (null == refreshRecyclerAdapterManager) {
                throw new RuntimeException("adapter has not been inited");
            }
            refreshRecyclerAdapterManager.setMode(mode);
        }

        public static void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
            if (null == refreshRecyclerAdapterManager) {
                throw new RuntimeException("adapter has not been inited");
            }
            if (layoutManager instanceof GridLayoutManager) {
                //如果是header或footer，设置其充满整列
                ((GridLayoutManager) layoutManager).setSpanSizeLookup(
                        new HeaderSapnSizeLookUp(refreshRecyclerAdapterManager.getAdapter(),
                                ((GridLayoutManager) layoutManager).getSpanCount()));
            }
            refreshRecyclerAdapterManager.getAdapter().putLayoutManager(layoutManager);
            refreshRecyclerAdapterManager.getRecyclerView().setLayoutManager(layoutManager);
        }

        /**
         * Replace RecyclerView.ViewHolder.getLayoutPosition() with this
         *
         * @param recyclerView
         * @param holder
         * @return
         */
        public static int getLayoutPosition(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
            if (null != recyclerView && null != recyclerView.getAdapter() && null != holder) {
                RecyclerView.Adapter mAdapter = recyclerView.getAdapter();
                if (mAdapter instanceof RefreshRecyclerViewAdapter) {
                    int headersCount = ((RefreshRecyclerViewAdapter) mAdapter).getHeadersCount();
                    if (headersCount > 0) {
                        return holder.getLayoutPosition() - headersCount;
                    }
                }
                return holder.getLayoutPosition();
            } else if (null == recyclerView) {
                throw new NullPointerException("RefreshRecyclerView cannot be null");
            } else if (null == recyclerView.getAdapter()) {
                throw new NullPointerException("RecyclerViewAdapter cannot be null");
            } else {
                throw new NullPointerException("RecyclerView.ViewHolde cannot be null");
            }
        }

        /**
         * Replace RecyclerView.ViewHolder.getLayoutPosition() with this
         *
         * @param recyclerView
         * @param holder
         * @return
         */
        public static int getAdapterPosition(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
            if (null != recyclerView && null != recyclerView.getAdapter() && null != holder) {
                RecyclerView.Adapter mAdapter = recyclerView.getAdapter();
                if (mAdapter instanceof RefreshRecyclerViewAdapter) {
                    int headersCount = ((RefreshRecyclerViewAdapter) mAdapter).getHeadersCount();
                    if (headersCount > 0) {
                        return holder.getAdapterPosition() - headersCount;
                    }
                }
                return holder.getAdapterPosition();
            } else if (null == recyclerView) {
                throw new NullPointerException("RefreshRecyclerView cannot be null");
            } else if (null == recyclerView.getAdapter()) {
                throw new NullPointerException("RecyclerViewAdapter cannot be null");
            } else {
                throw new NullPointerException("RecyclerView.ViewHolder cannot be null");
            }
        }

    }

    public void onCreateSet(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager, OnBothRefreshListener listener) {
        RecyclerViewManager.with(adapter, layoutManager)
                .setMode(RecyclerMode.BOTH)
                .setOnBothRefreshListener(listener)
                .into(this, mContext);
    }

    public void onCreateTopSet(RecyclerView.Adapter adapter, RecyclerView.LayoutManager layoutManager, OnPullDownListener listener) {
        RecyclerViewManager.with(adapter, layoutManager)
                .setMode(RecyclerMode.TOP)
                .setOnPullDownListener(listener)
                .into(this, mContext);
    }

}
