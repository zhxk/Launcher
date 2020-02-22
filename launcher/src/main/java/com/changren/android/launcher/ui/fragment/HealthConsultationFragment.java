package com.changren.android.launcher.ui.fragment;

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
 * Create: 2018-09-17 19:08
 * Description: TODO(描述文件做什么)
 */
public class HealthConsultationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_scrollbar_fragment_health_consultation, container, false);

        return view;
    }
}
