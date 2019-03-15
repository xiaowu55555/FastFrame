package com.bingo.fastframe.library.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bingo.fastframe.R;
import com.bingo.fastframe.library.app.BaseApplication;
import com.bingo.fastframe.library.utils.NetworkUtils;
import com.bingo.fastframe.library.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;


public abstract class BaseListActivity<V, T extends BaseViewModel> extends BaseActivity<T> implements
        BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener {
    protected RecyclerView recyclerView;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected BaseQuickAdapter<V, BaseViewHolder> adapter;
    protected int pageIndex = 0;
    protected int pageSize = 20;

    @Override
    protected void initView(Bundle savedInstanceState) {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView.setLayoutManager(getLayoutManager());
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setEnableLoadMore(enableLoadMore());
        if (enableLoadMore()) {
            adapter.setOnLoadMoreListener(this, recyclerView);
            adapter.disableLoadMoreIfNotFullPage();
        }
        if (enableRefresh()) {
            swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary);
            swipeRefreshLayout.post(() -> swipeRefreshLayout.setRefreshing(true));
            swipeRefreshLayout.setOnRefreshListener(this);
        }
        requestData();
    }

    protected void setListData(List<V> list) {
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        adapter.setEmptyView(getEmptyView());
        if (list != null && list.size() > 0) {
            if (pageIndex == 0) {
                adapter.setNewData(list);
            } else {
                adapter.addData(list);
                if (list.size() < pageSize) {
                    adapter.loadMoreEnd();
                } else {
                    adapter.loadMoreComplete();
                }
            }
        }
        adapter.setEnableLoadMore(enableLoadMore());
    }

    @Override
    protected void showError(String message) {
        ToastUtil.showToast(message);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        adapter.setEnableLoadMore(enableLoadMore());
        if (pageIndex == 0 && statusView != null) {
            if (!NetworkUtils.isNetworkAvailable(BaseApplication.getInstance())) {
                statusView.showNoNetwork();
            } else {
                statusView.showError();
            }
        } else {
            adapter.loadMoreFail();
        }
    }

    protected View getEmptyView() {
        return View.inflate(context, R.layout.empty_view, null);
    }

    protected boolean enableRefresh() {
        return true;//默认开启下拉刷新
    }

    protected abstract void requestData();

    protected boolean enableLoadMore() {
        return true;//默认开启加载更多
    }

    protected abstract BaseQuickAdapter<V, BaseViewHolder> getAdapter();

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(context);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.base_list_layout;
    }

    @Override
    public void onLoadMoreRequested() {
        pageIndex++;
        requestData();
    }

    @Override
    public void onRefresh() {
        adapter.setEnableLoadMore(false);
        pageIndex = 0;
        requestData();
    }
}
