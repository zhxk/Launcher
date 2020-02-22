package com.changren.android.launcher.user.ui;

import android.content.Context;
import android.content.res.Resources;

import com.changren.android.component.adapter.recyclerview.CommonAdapter;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.Family;
import com.changren.android.launcher.util.LogUtils;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-12-19 17:18
 * Description: TODO(描述文件做什么)
 */
public class FamilyListAdapter extends CommonAdapter<Family> {

    //选中的item
    private int defaultSelection = -1;
    // 背景选中的颜色
    private int bg_selected_color;

    public FamilyListAdapter(Context context, int layoutId, List<Family> datas) {
        super(context, layoutId, datas);

        Resources resources = context.getResources();
        bg_selected_color = resources.getColor(R.color.user_list_bg_checked);
    }

    @Override
    protected void convert(ViewHolder holder, Family family, int position) {
        holder.setText(R.id.tv_family_name, family.getFamily_name());

//        LogUtils.i("position",position, "defaultSelection",defaultSelection);
        if (position == defaultSelection) {// 选中时设置单纯颜色
            holder.setBackgroundColor(R.id.cl_family, bg_selected_color);
        } else {// 未选中时设置selector
            holder.setBackgroundRes(R.id.cl_family, R.drawable.user_family_item_bg_selector);
        }
    }

    /**
     * @param position 设置高亮状态的item
     */
    public void setSelectPosition(int position) {
        if (!(position < 0 || position > getItemCount())) {
            defaultSelection = position;
//            notifyDataSetChanged();
        }
    }

    /**
     * @return  当前选中的item的下标
     */
    public int getSelection() {
        return defaultSelection;
    }
}
