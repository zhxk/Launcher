package com.changren.android.launcher.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.changren.android.launcher.R;

/**
 * Author: wangsy
 * Create: 2018-09-17 11:03
 * Description: 健康检测App在Launcher的入口
 */
public class HealthMonitorFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_scrollbar_fragment_health_monitor, container, false);
        view.setBackgroundResource(R.drawable.home_scrollbar_bg_shape_white_corners_20);

        ViewGroup.LayoutParams params = view.getLayoutParams();

        params.width = 740;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        return view;
    }

}
