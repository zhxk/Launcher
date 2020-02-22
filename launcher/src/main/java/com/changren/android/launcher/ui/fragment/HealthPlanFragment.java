package com.changren.android.launcher.ui.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.changren.android.launcher.R;
import com.changren.android.launcher.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: wangsy
 * Create: 2018-09-17 19:07
 * Description: TODO(描述文件做什么)
 */
public class HealthPlanFragment extends Fragment {
    private static final String packageName = "com.changren.android.healthplan2";
    private static final String className = "com.changren.android.healthplan2.activity.MainActivity";
    private TextView textMore;
    private TextView textViewTest;

    @BindView(R.id.tv_plan_title)
    TextView tvPlanTitle;
    @BindView(R.id.plan_tv_more)
    TextView tvMore;
    @BindView(R.id.view_h_line)
    View viewHLine;
    /*@BindView(R.id.plan_line_info)
    RecyclerView planLineInfo;*/
    @BindView(R.id.plan_now)
    TextView planNow;
    @BindView(R.id.plan_next)
    TextView planNext;
//    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_scrollbar_fragment_health_plan, container, false);
        Log.d("zhengshibo", "onCreateView: ");
        textMore = view.findViewById(R.id.plan_tv_more);
//        textViewTest = view.findViewById(R.id.textViewTest);
//                unbinder = ButterKnife.bind(this, view);

        return view;
    }
}
