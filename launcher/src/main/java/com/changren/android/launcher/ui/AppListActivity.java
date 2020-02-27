package com.changren.android.launcher.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.changren.android.launcher.R;
import com.changren.android.launcher.ui.adapter.AppListAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: wangsy
 * Create: 2018-09-28 18:48
 * Description: TODO(描述文件做什么)
 */
public class AppListActivity extends AppCompatActivity {

    @BindView(R.id.rv_list)
    RecyclerView mRecycler;

    private List<ResolveInfo> mApps;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.home_activity_applist);
//        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.height = (int) (display.getHeight() * 0.6);
//        params.width = (int) (display.getWidth() * 0.5);
//        params.alpha = 1.0f;
//        getWindow().setAttributes(params);
//        getWindow().setGravity(Gravity.CENTER);

        Window window = getWindow();
        if (window != null) {
//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            window.setAttributes(lp);
//            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.dimAmount = 0.7f;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        ButterKnife.bind(this);
        
        initView();
    }

    private void initView() {
        PackageManager packageManager = this.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = packageManager.queryIntentActivities(mainIntent, 0);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(gridLayoutManager);
        mRecycler.addItemDecoration(new MarginDecoration(56, 36, 4));
        AppListAdapter mAdapter = new AppListAdapter(this, R.layout.home_recycler_item_icon, mApps);
        mRecycler.setAdapter(mAdapter);
    }

    class MarginDecoration extends RecyclerView.ItemDecoration {

        private int hSpace;
        private int vSpace;
        private int itemNum;

        /**
         *
         * @param hSpace 列间隔
         * @param vSpace 行间隔
         * @param itemNum 每行item的个数
         */
        public MarginDecoration(int hSpace, int vSpace, int itemNum) {
            this.hSpace = hSpace;
            this.vSpace = vSpace;
            this.itemNum = itemNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildLayoutPosition(view) / itemNum == 0) {
                outRect.top = 0;
            } else {
                outRect.top = vSpace;
            }

            if (parent.getChildLayoutPosition(view) % itemNum == 0){  //parent.getChildLayoutPosition(view) 获取view的下标
                outRect.left = 0;
            } else {
                outRect.left = hSpace;
            }
        }
    }
}
