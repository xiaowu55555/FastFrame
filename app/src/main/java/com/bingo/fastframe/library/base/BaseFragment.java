package com.bingo.fastframe.library.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bingo.fastframe.R;
import com.bingo.fastframe.library.app.BaseApplication;
import com.bingo.fastframe.library.event.ActionEvent;
import com.bingo.fastframe.library.utils.NetworkUtils;
import com.bingo.fastframe.library.utils.ToastUtil;
import com.bingo.fastframe.library.widget.MultipleStatusView;

public abstract class BaseFragment<T extends BaseViewModel> extends Fragment {

    private boolean isPrepared;
    private boolean isLazyLoaded;//是否已经加载过数据
    protected Context context;
    protected T viewModel;
    protected View rootView;
    protected MultipleStatusView statusView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        lazyLoad();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutRes(), container, false);
        }
        statusView = rootView.findViewById(R.id.stateful_layout);
        viewModel = createViewModel();
        observeActionEvent();
        initView(rootView);
        return rootView;
    }

    private void observeActionEvent() {
        if (viewModel != null) {
            viewModel.getAction().observe(this, actionEvent -> {
                switch (actionEvent.getAction()) {
                    case ActionEvent.SHOW_LOADING:
                        showLoading();
                        break;
                    case ActionEvent.HIDE_LOADING:
                        hideLoading();
                        break;
                    case ActionEvent.SHOW_ERROR:
                        showError(actionEvent.getMessage());
                        break;
                    case ActionEvent.SHOW_TOAST:
                        ToastUtil.showToast(actionEvent.getMessage());
                        break;
                }
            });
        }
    }

    protected void hideLoading() {
        if (statusView != null) {
            statusView.showContent();
        }
    }

    protected void showLoading() {
        if (statusView != null) {
            statusView.showLoading();
        }
    }

    protected void showError(String message) {
        if (statusView != null) {
            if (!NetworkUtils.isNetworkAvailable(BaseApplication.getInstance())) {
                statusView.showNoNetwork();
            } else {
                statusView.showError();
            }
        }
    }

    protected abstract T createViewModel();

    protected abstract void initView(View rootView);

    protected abstract int getLayoutRes();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        lazyLoad();
    }

    private void lazyLoad() {
        if (getUserVisibleHint() && isPrepared && !isLazyLoaded) {
            isLazyLoaded = true;
            onLazyLoad();
        }
    }

    protected abstract void onLazyLoad();
}
