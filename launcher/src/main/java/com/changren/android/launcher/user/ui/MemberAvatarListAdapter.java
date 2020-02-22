package com.changren.android.launcher.user.ui;

import android.content.Context;
import android.widget.ImageView;

import com.changren.android.component.adapter.recyclerview.CommonAdapter;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.User;
import com.changren.android.launcher.util.GlideApp;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-12-25 11:53
 * Description:
 */
public class MemberAvatarListAdapter extends CommonAdapter<User> {

    public MemberAvatarListAdapter(Context context, int layoutId, List<User> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, User user, int position) {

        GlideApp.with(mContext)
            .load(user.getAvatar())
            .placeholder(user.getSex() == 1 ? R.drawable.default_male_middle : R.drawable.default_female_middle)
            .into((ImageView) holder.getView(R.id.iv_avatar));
    }
}
