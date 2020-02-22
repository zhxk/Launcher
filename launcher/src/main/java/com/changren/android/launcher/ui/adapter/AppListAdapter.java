package com.changren.android.launcher.ui.adapter;

import android.content.Context;
import android.content.pm.ResolveInfo;

import com.changren.android.component.adapter.recyclerview.CommonAdapter;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.R;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-09-28 20:51
 * Description: TODO(描述文件做什么)
 */
public class AppListAdapter extends CommonAdapter<ResolveInfo> {

    public AppListAdapter(Context context, int layoutId, List<ResolveInfo> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, ResolveInfo resolveInfo, int position) {
        holder.setImageDrawable(R.id.iv_app_icon,
                resolveInfo.activityInfo.loadIcon(mContext.getPackageManager()));

        holder.setText(R.id.tv_app_name,
                resolveInfo.activityInfo.loadLabel(mContext.getPackageManager()).toString());
    }
}
