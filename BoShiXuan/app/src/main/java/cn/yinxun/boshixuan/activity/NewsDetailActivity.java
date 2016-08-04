package cn.yinxun.boshixuan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;

/**
 * Created by Administrator on 2016/7/17 0017.
 */
public class NewsDetailActivity extends BaseActivity{
    public static final int TYPE_SYSTEM = 1;
    public static final int TYPE_PERSON = 2;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.newsTitle)
    TextView mNewsTitle;
    @Bind(R.id.newsContent)
    TextView mNewsContent;
    @Bind(R.id.newsTime)
    TextView mNewsTime;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        ButterKnife.bind(this);
        initHeader();
        initData();
    }
    private void initHeader() {
        Intent intent = getIntent();
        int type = intent.getIntExtra("type",TYPE_SYSTEM);
        String header = "";
        if(TYPE_SYSTEM == type) {
            header = "系统消息";
        } else {
            header = "个人消息";
        }
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText(header);
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void initData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String time = intent.getStringExtra("time");
        this.mNewsContent.setText(content);
        this.mNewsTime.setText(time);
        this.mNewsTitle.setText(title);
    }
}
