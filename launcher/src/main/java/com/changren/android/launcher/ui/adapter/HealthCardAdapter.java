package com.changren.android.launcher.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.changren.android.component.adapter.recyclerview.MultiItemTypeAdapter;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.database.entity.DataSource;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Author: wangsy
 * Create: 2018-09-21 9:58
 * Description: TODO(描述文件做什么)
 */
public class HealthCardAdapter extends MultiItemTypeAdapter<DataSource> {

    public HealthCardAdapter(Context context, List<DataSource> datas) {
        super(context, datas);


        addItemViewDelegate(new HealthMonitorItemDelagate((Activity) context));
        addItemViewDelegate(new HealthPlanItemDelagate());
        addItemViewDelegate(new HealthConsultationItemDelagate());
    }
    @Override
    public void onViewHolderCreated(ViewHolder holder, View itemView) {
        super.onViewHolderCreated(holder, itemView);

        ButterKnife.bind(itemView);
    }
}
