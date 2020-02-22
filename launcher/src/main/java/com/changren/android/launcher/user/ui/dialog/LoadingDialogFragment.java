package com.changren.android.launcher.user.ui.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.changren.android.launcher.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadingDialogFragment extends DialogFragment {

    private String parentTag;

    @BindView(R.id.uploading)
    ProgressBar mProgressBar;

    @BindView(R.id.tv_confirm)
    TextView tv_confirm;

    @BindView(R.id.tv_content)
    TextView tv_content;

    @BindView(R.id.tv_retry)
    TextView tv_retry;

    private boolean isSuccess = false;

    private OnLoadingListener mListener;
    private Context mActivity;

    public interface OnLoadingListener {
        void startLoader();
        void reload();
        void loadCompleted();
        void cancel();
    }

    public static LoadingDialogFragment newInstance() {
        LoadingDialogFragment fragment = new LoadingDialogFragment();
//        if (bundle != null) {
//            fragment.setArguments(bundle);
//        }
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = context;
        if (context instanceof OnLoadingListener) {
            mListener = (OnLoadingListener) context;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        if (activity instanceof OnLoadingListener) {
            mListener = (OnLoadingListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//        }

        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment_dialog_loading, null);

        ButterKnife.bind(this, view);
        //TODO 此次设置Dialog尺寸不生效,暂时未查明原因
//        Window window = getDialog().getWindow();
//        if (window != null) {
//            DisplayMetrics dm = new DisplayMetrics();
//            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.gravity = Gravity.CENTER;
//            lp.width = (int)(dm.widthPixels * 0.5);
////            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            window.setAttributes(lp);
//            window.setBackgroundDrawableResource(android.R.color.transparent);
//        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        if (window != null) {
//            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = (int)(dm.widthPixels * 0.6);
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mListener != null) {
            mListener.startLoader();
        }
    }

    public void setErrorMessage(String msg) {
        isSuccess = false;
        //提交失败
        mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_failed));
        mProgressBar.setProgressDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_failed));
        tv_retry.setVisibility(View.GONE);
        if (TextUtils.isEmpty(msg)) {
            tv_content.setText(R.string.user_upload_data_failed);
        } else {
            tv_content.setText(msg);
        }
        tv_confirm.setText(R.string.user_confirm);
    }

    public void setFailedMessage(String msg) {
        isSuccess = false;
        //提交失败
        mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_failed));
        mProgressBar.setProgressDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_failed));
        tv_retry.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(msg)) {
            tv_content.setText(R.string.user_upload_data_failed);
        } else {
            tv_content.setText(msg);
        }
        tv_confirm.setText(R.string.user_upload_data_cancel);
    }

    public void setSuccessMessage(String msg) {
        isSuccess = true;
        //提交成功
        mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_completed));
        mProgressBar.setProgressDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_completed));
        if (TextUtils.isEmpty(msg)) {
            tv_content.setText(R.string.user_upload_data_completed);
        } else {
            tv_content.setText(msg);
        }
        tv_confirm.setText(R.string.user_confirm);

        if (mListener != null) {
            mListener.loadCompleted();
            dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        //TODO ft.remove(this)会导致Manager为null
        if (getFragmentManager() != null) {
            Fragment parentFragment = getFragmentManager().findFragmentByTag(parentTag);
            if (parentFragment instanceof DialogInterface.OnDismissListener) {
                ((DialogInterface.OnDismissListener) parentFragment).onDismiss(dialog);
            }
        }

        if (getActivity() instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) getActivity()).onDismiss(dialog);
        }
    }

    @OnClick({R.id.tv_confirm, R.id.tv_retry})
    public void onClick(View v) {
        if (v.getId() == R.id.tv_confirm) {
            if (!isSuccess) {//取消上传
                if (tv_retry.getVisibility() == View.VISIBLE) {
                    //不再次上传
                    dismiss();
                    return;
                }
                if (mListener != null) {
                    mListener.cancel();
                    dismiss();
                }
            } else {
                dismiss();
            }
        } else if (v.getId() == R.id.tv_retry) {
            mProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.user_progress_circular_loading));
            mProgressBar.setProgressDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.user_progress_circular_loading));
            tv_retry.setVisibility(View.GONE);
            tv_content.setText(R.string.user_upload_data_during);

            if (mListener != null) {
                mListener.reload();
            }
        }
    }
}
