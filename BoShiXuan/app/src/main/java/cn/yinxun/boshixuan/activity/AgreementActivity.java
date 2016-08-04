package cn.yinxun.boshixuan.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;
import cn.yinxun.boshixuan.R;

/**
 * Created by yangln10784 on 2016/7/23.
 */
public class AgreementActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree);
        ButterKnife.bind(this);

    }
}
