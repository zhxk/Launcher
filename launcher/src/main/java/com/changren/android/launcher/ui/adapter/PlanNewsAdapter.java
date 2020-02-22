package com.changren.android.launcher.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.changren.android.component.adapter.recyclerview.CommonAdapter;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.InformationBean;
import com.changren.android.launcher.ui.constants.Constants;
import com.changren.android.launcher.util.Utils;

import java.util.List;

import butterknife.internal.DebouncingOnClickListener;

public class PlanNewsAdapter extends CommonAdapter<InformationBean.DataBean> {
    private RelativeLayout healthNewsRoot;
    private TextView newText;
    private ImageView newImg;

    public PlanNewsAdapter(Context context, int layoutId, List<InformationBean.DataBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, final InformationBean.DataBean dataBean, int position) {
        healthNewsRoot = holder.getView(R.id.health_news_root);
        newText = holder.getView(R.id.new_text);
        newImg = holder.getView(R.id.new_img);
        if (dataBean != null) {
            newText.setText(dataBean.getTitle());
            Glide.with(mContext).load(dataBean.getImg()).into(newImg);
            healthNewsRoot.setOnClickListener(new DebouncingOnClickListener() {
                @Override
                public void doClick(View v) {
                    int id = dataBean.getId();
                    Log.d("zhengshibo", "doClick: id == > " + id);
                    Utils.toHealthPlan(Utils.getApp(), Constants.PLAN_PACKAGE_NAME, Constants.AITICLE_DETAIL_CLASS_NAME, id);
                }
            });
        }
    }
}
