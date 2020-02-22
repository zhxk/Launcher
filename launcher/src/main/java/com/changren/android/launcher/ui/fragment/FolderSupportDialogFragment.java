package com.changren.android.launcher.ui.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.changren.android.component.adapter.recyclerview.MultiItemTypeAdapter;
import com.changren.android.component.fastblur.SupportBlurDialogFragment;
import com.changren.android.launcher.R;
import com.changren.android.launcher.ui.adapter.AppListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author: wangsy
 * Create: 2018-09-30 10:07
 * Description: TODO(描述文件做什么)
 */
public class FolderSupportDialogFragment extends SupportBlurDialogFragment {

    /**
     * Bundle key used to start the blur dialog with a given scale factor (float).
     */
    private static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";

    /**
     * Bundle key used to start the blur dialog with a given blur radius (int).
     */
    private static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";

    /**
     * Bundle key used to start the blur dialog with a given dimming effect policy.
     */
    private static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";

    /**
     * Bundle key used to start the blur dialog with a given debug policy.
     */
    private static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";

    /**
     * Bundle key used for blur effect on action bar policy.
     */
    private static final String BUNDLE_KEY_BLURRED_ACTION_BAR = "bundle_key_blurred_action_bar";

    /**
     * Bundle key used for RenderScript
     */
    private static final String BUNDLE_KEY_USE_RENDERSCRIPT = "bundle_key_use_renderscript";
    /**
     * Bundle key used for RenderScript
     */
    private static final String BUNDLE_KEY_TYPE = "bundle_key_type";

    private int mType;
    private int mRadius;
    private float mDownScaleFactor;
    private boolean mDimming;
    private boolean mDebug;
    private boolean mBlurredActionBar;
    private boolean mUseRenderScript;

    /**
     * Retrieve a new instance of the sample fragment.
     *
     * @param radius            blur radius.
     * @param downScaleFactor   down scale factor.
     * @param dimming           dimming effect.
     * @param debug             debug policy.
     * @param mBlurredActionBar blur affect on actionBar policy.
     * @param useRenderScript   use of RenderScript
     * @return well instantiated fragment.
     */
    public static FolderSupportDialogFragment newInstance(int type,
                                                          int radius,
                                                          float downScaleFactor,
                                                          boolean dimming,
                                                          boolean debug,
                                                          boolean mBlurredActionBar,
                                                          boolean useRenderScript) {
        FolderSupportDialogFragment fragment = new FolderSupportDialogFragment();
        Bundle args = new Bundle();
        args.putInt(BUNDLE_KEY_TYPE, type);
        args.putInt(BUNDLE_KEY_BLUR_RADIUS, radius);
        args.putFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR, downScaleFactor);
        args.putBoolean(BUNDLE_KEY_DIMMING, dimming);
        args.putBoolean(BUNDLE_KEY_DEBUG, debug);
        args.putBoolean(BUNDLE_KEY_BLURRED_ACTION_BAR, mBlurredActionBar);
        args.putBoolean(BUNDLE_KEY_USE_RENDERSCRIPT, useRenderScript);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        mType = args.getInt(BUNDLE_KEY_TYPE);
        mRadius = args.getInt(BUNDLE_KEY_BLUR_RADIUS);
        mDownScaleFactor = args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR);
        mDimming = args.getBoolean(BUNDLE_KEY_DIMMING);
        mDebug = args.getBoolean(BUNDLE_KEY_DEBUG);
        mBlurredActionBar = args.getBoolean(BUNDLE_KEY_BLURRED_ACTION_BAR);
        mUseRenderScript = args.getBoolean(BUNDLE_KEY_USE_RENDERSCRIPT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @BindView(R.id.rv_list)
    RecyclerView mRecycler;

    @BindView(R.id.tv_title)
    TextView mTitleTv;

    private List<ResolveInfo> mApps;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_activity_applist, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
//        WindowManager.LayoutParams windowAttributes = window.getAttributes();
//        windowAttributes.dimAmount = 0.0f;
//        window.setAttributes(windowAttributes);
//        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (window != null) {
//            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.3f;
            lp.gravity = Gravity.CENTER;
//            lp.width = (int)(dm.widthPixels * 0.6);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }

        PackageManager packageManager = getActivity().getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = packageManager.queryIntentActivities(mainIntent, 0);
        mApps = new ArrayList<>();
        if (mType == 1) {   // 个人健康
            mTitleTv.setText(R.string.home_tabs_personal);
            for (ResolveInfo info : list) { //modify for sort app by shibo.zheng 20190307
                if (info.activityInfo.packageName.contains("com.changren.android") &&
                        !info.activityInfo.packageName.equals("com.changren.android.robotaiui") &&
                        !info.activityInfo.packageName.equals("com.changren.android.floatingball") &&
                        !info.activityInfo.packageName.equals("com.changren.android.launcher")) {
                    mApps.add(info);
                }
            }
        } else if (mType == 2) {    // 健康服务
            mTitleTv.setText(R.string.home_tabs_service);
            for (ResolveInfo info : list) { //modify for sort app by shibo.zheng 20190307
                if (info.activityInfo.packageName.equals("com.changren.android.robotaiui") || info.activityInfo.packageName.equals("com.changren.android.floatingball") || info.activityInfo.packageName.equals("com.netease.nim.demo")) {
                    mApps.add(info);
                }
            }
            //add for sort app by shibo.zheng 20190307 start
        } else if (mType == 3) {    // 应用
            mTitleTv.setText(R.string.home_tabs_app);
            for (ResolveInfo info : list) {
                if (!info.activityInfo.packageName.contains("com.changren.android.") && !info.activityInfo.packageName.equals("com.netease.nim.demo")) {
                    mApps.add(info);
                }
            }
        }//add for sort app by shibo.zheng 20190307 end

        //todo 暂时写死横向4列，竖向3列，实际应当动态获取RecyclerView的width，并除以item的固定width，得出spanCount.
        int spanCount;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            spanCount = 4;
        } else {
            spanCount = 3;
        }
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), spanCount);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(gridLayoutManager);
        mRecycler.addItemDecoration(new MarginDecoration(56, 36, spanCount));
        AppListAdapter mAdapter = new AppListAdapter(getActivity(), R.layout.home_recycler_item_icon, mApps);
        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                ResolveInfo info = mApps.get(position);
                String pkg = info.activityInfo.packageName;
                String cls = info.activityInfo.name;
                ComponentName componentName = new ComponentName(pkg, cls);
                Intent intent = new Intent();
                //需要加这个flags，否则会出现recent list不显示启动的app
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(componentName);
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        mRecycler.setAdapter(mAdapter);

        //因为xml布局全屏了，所以不能点击空白处，dismiss掉Dialog
        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isTouchPointInView(mRecycler, x, y)) {
                        return false;
                    } else {
                        dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * (x,y)是否在view的区域内
     */
    private boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (y >= top && y <= bottom && x >= left
                && x <= right) {
            return true;
        }
        return false;
    }

    class MarginDecoration extends RecyclerView.ItemDecoration {

        private int hSpace;
        private int vSpace;
        private int itemNum;

        /**
         * @param hSpace  列间隔
         * @param vSpace  行间隔
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

            if (parent.getChildLayoutPosition(view) % itemNum == 0) {  //parent.getChildLayoutPosition(view) 获取view的下标
                outRect.left = 0;
            } else {
                outRect.left = hSpace;
            }
        }
    }

    @Override
    protected boolean isDebugEnable() {
        return mDebug;
    }

    @Override
    protected boolean isDimmingEnable() {
        return mDimming;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return mBlurredActionBar;
    }

    @Override
    protected float getDownScaleFactor() {
        return mDownScaleFactor;
    }

    @Override
    protected int getBlurRadius() {
        return mRadius;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return mUseRenderScript;
    }
}
