package com.changren.android.launcher.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.changren.android.launcher.R;
import com.changren.android.launcher.util.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author: wangsy
 * Create: 2019-02-25 11:54
 * Description: 显示已绑定的手机号码
 */
public class PhoneInfoActivity extends AppCompatActivity {

    public static final String PHONE = "PHONE";

    @BindView(R.id.tv_phone)
    TextView phoneTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_activity_member_bind_phone);
        ButterKnife.bind(this);

        init();
    }

    private void init() {

        String phone_num = getIntent().getStringExtra(PHONE);
        phoneTv.setText(phone_num);
    }

    @OnClick({R.id.iv_back, R.id.btn_bind})
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else {
            Intent intent = new Intent(this, BindingPhoneActivity.class);
            intent.putExtra(BindingPhoneActivity.UPDATE_PHONE, true);
            ActivityUtils.startActivity(intent);
            ActivityUtils.finishActivity(this);
        }
    }
}
