package cn.yinxun.boshixuan.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.yinxun.boshixuan.activity.NewsDetailActivity;
import cn.yinxun.boshixuan.R;
import cn.yinxun.boshixuan.network.model.NewsDetailModel;
import cn.yinxun.boshixuan.util.CommonUtil;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsView> {
    public static final int TYPE_SYSTEM = 1;
    public static final int TYPE_PERSON = 2;
    private int mType;
    private List<NewsDetailModel> mNewsDetailModelList;
    private Context mContext;
    public NewsListAdapter(Context context, List<NewsDetailModel> modelList,int type) {
        this.mNewsDetailModelList = modelList;
        this.mContext = context;
        this.mType = type;
    }
    public void setList(List<NewsDetailModel> modelList) {
        if(modelList != null) {
            this.mNewsDetailModelList = modelList;
        }
    }
    @Override
    public NewsView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsView(view);
    }

    @Override
    public void onBindViewHolder(NewsView holder, int position) {
        holder.mNewsContent.setText(this.mNewsDetailModelList.get(position).msg_content);
        holder.mNewsTitle.setText(this.mNewsDetailModelList.get(position).msg_title);
        String time = this.mNewsDetailModelList.get(position).stamp_created;
        holder.mNewsTime.setText(CommonUtil.formatTime(time));
    }

    @Override
    public int getItemCount() {
        return this.mNewsDetailModelList.size();
    }

    public class NewsView extends RecyclerView.ViewHolder {
        @Bind(R.id.newsContent)
        TextView mNewsContent;
        @Bind(R.id.newsTitle)
        TextView mNewsTitle;
        @Bind(R.id.newsTime)
        TextView mNewsTime;
        @OnClick(R.id.newsLayout)
        public void OnClick(View view) {
            int position = getLayoutPosition() - 1;
            NewsDetailModel newsDetailModel = NewsListAdapter.this.mNewsDetailModelList.get(position);
            Intent intent = new Intent(NewsListAdapter.this.mContext, NewsDetailActivity.class);
            intent.putExtra("title",newsDetailModel.msg_title);
            intent.putExtra("content",newsDetailModel.msg_content);
            intent.putExtra("time",CommonUtil.formatTime(newsDetailModel.stamp_created));
            intent.putExtra("type",NewsListAdapter.this.mType);
            NewsListAdapter.this.mContext.startActivity(intent);
        }
        public NewsView(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
