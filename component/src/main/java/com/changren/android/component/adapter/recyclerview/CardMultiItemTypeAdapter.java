package com.changren.android.component.adapter.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.changren.android.component.R;
import com.changren.android.component.adapter.recyclerview.base.ItemViewDelegate;
import com.changren.android.component.adapter.recyclerview.base.ItemViewDelegateManager;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.component.slidingwindow.BannerAdapterHelper;

import java.util.List;

/**
 * Author: wangsy
 * Create: 2018-09-21 9:47
 * Description: TODO(描述文件做什么)
 */
public class CardMultiItemTypeAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> mDatas;

    protected ItemViewDelegateManager mItemViewDelegateManager;
    protected MultiItemTypeAdapter.OnItemClickListener mOnItemClickListener;
    private BannerAdapterHelper mBannerAdapterHelper = new BannerAdapterHelper();

    public CardMultiItemTypeAdapter(Context context, List<T> datas) {
        mContext = context;
        mDatas = datas;
        mItemViewDelegateManager = new ItemViewDelegateManager();
    }

    @Override
    public int getItemViewType(int position) {
        if (!useItemViewDelegateManager()) {
            return super.getItemViewType(position);
        }
        return mItemViewDelegateManager.getItemViewType(mDatas.get(innerPosition(position)), position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewDelegate itemViewDelegate = mItemViewDelegateManager.getItemViewDelegate(viewType);
        int layoutId = itemViewDelegate.getItemViewLayoutId();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        mBannerAdapterHelper.onCreateViewHolder(parent, itemView);
//        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, layoutId);
        ViewHolder holder = ViewHolder.createViewHolder(mContext, itemView);
        onViewHolderCreated(holder,holder.getConvertView());
        setListener(parent, holder, viewType);
        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder,View itemView){

    }

    public void convert(ViewHolder holder, T t) {
        mItemViewDelegateManager.convert(holder, t, holder.getAdapterPosition());
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder , position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mBannerAdapterHelper.onBindViewHolder(holder.getConvertView(), position, getItemCount());
        convert(holder, mDatas.get(innerPosition(position)));
    }

    @Override
    public int getItemCount() {
//        int itemCount = mDatas.size();
//        return itemCount;
        return Integer.MAX_VALUE;
    }

    private int innerPosition(int position) {
        return position % mDatas.size();
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public CardMultiItemTypeAdapter addItemViewDelegate(ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(itemViewDelegate);
        return this;
    }

    public CardMultiItemTypeAdapter addItemViewDelegate(int viewType, ItemViewDelegate<T> itemViewDelegate) {
        mItemViewDelegateManager.addDelegate(viewType, itemViewDelegate);
        return this;
    }

    protected boolean useItemViewDelegateManager() {
        return mItemViewDelegateManager.getItemViewDelegateCount() > 0;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, RecyclerView.ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(MultiItemTypeAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
