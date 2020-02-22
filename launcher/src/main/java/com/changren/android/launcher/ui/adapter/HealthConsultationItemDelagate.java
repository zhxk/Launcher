package com.changren.android.launcher.ui.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.changren.android.component.adapter.recyclerview.base.ItemViewDelegate;
import com.changren.android.component.adapter.recyclerview.base.ViewHolder;
import com.changren.android.launcher.R;
import com.changren.android.launcher.database.entity.DataSource;
import com.changren.android.launcher.database.entity.InformationBean;
import com.changren.android.launcher.util.LogUtils;
import com.changren.android.launcher.util.Utils;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by zhy on 16/6/22.
 */

/**
 * 健康资讯页面
 */
public class HealthConsultationItemDelagate implements ItemViewDelegate<DataSource> {
    private RecyclerView planNewsRecycler;
    private TextView defaultNoNews;
    private List<InformationBean.DataBean> listNews;

    @Override
    public int getItemViewLayoutId() {
        return R.layout.home_scrollbar_fragment_health_consultation;
    }

    @Override
    public boolean isForViewType(DataSource item, int position) {
        return item.getItem_type().equals("C");
    }

    @Override
    public void convert(ViewHolder holder, DataSource s, int position) {
        planNewsRecycler = holder.getView(R.id.plan_news);
        defaultNoNews = holder.getView(R.id.default_news_text);
        listNews = ((InformationBean)s).getData();
//        LogUtils.i("listNews == > " + new Gson().toJson(listNews));
        if (listNews == null) {
            planNewsRecycler.setVisibility(View.GONE);
            defaultNoNews.setVisibility(View.VISIBLE);
            return;
        }
        planNewsRecycler.setVisibility(View.VISIBLE);
        defaultNoNews.setVisibility(View.GONE);
        initRecyclerView(Utils.getApp());
    }

    private void initRecyclerView(Context context){
        PlanNewsAdapter planNews = new PlanNewsAdapter(context, R.layout.plan_news, listNews);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        planNewsRecycler.setLayoutManager(manager);
        planNewsRecycler.setAdapter(planNews);
    }

//    @Override
//    public boolean isForViewType(ChatMessage item, int position)
//    {
//        return !item.isComMeg();
//    }
//
//    @Override
//    public void convert(ViewHolder holder, ChatMessage chatMessage, int position)
//    {
//        holder.setText(R.id.chat_send_content, chatMessage.getContent());
//        holder.setText(R.id.chat_send_name, chatMessage.getName());
//        holder.setImageResource(R.id.chat_send_icon, chatMessage.getIcon());
//    }
}
