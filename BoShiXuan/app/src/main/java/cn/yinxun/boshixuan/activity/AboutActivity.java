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
 * Created by yangln10784 on 2016/7/9.
 */
public class AboutActivity extends BaseActivity {
    public static final int TYPE_AGREE = 1;
    @Bind(R.id.tv_head)
    TextView mHeader;
    @Bind(R.id.bar_left_btn)
    RelativeLayout mBackBtn;
    @Bind(R.id.agree)
    TextView agreeText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initHeader();
        agreeText.setOnClickListener(new MyOnClickListener(TYPE_AGREE));
//        agreeText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
//        agreeText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initHeader() {
        this.mHeader.setVisibility(View.VISIBLE);
        this.mHeader.setText("关于我们");
        this.mBackBtn.setVisibility(View.VISIBLE);
        this.mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    class MyOnClickListener implements  View.OnClickListener{
        public int mType;
        public MyOnClickListener(int type) {
            this.mType = type;
        }
        @Override
        public void onClick(View v){
            Intent intent;
            switch (this.mType){
                case TYPE_AGREE:
                    intent = new Intent(AboutActivity.this,AgreementActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

}
