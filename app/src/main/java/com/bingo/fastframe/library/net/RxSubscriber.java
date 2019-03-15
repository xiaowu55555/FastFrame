package com.bingo.fastframe.library.net;



import com.bingo.fastframe.library.base.BaseViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public abstract class RxSubscriber<T> implements Observer<T> {
    private BaseViewModel viewModel;
    private String message = "加载中...";
    private boolean showLoading;


    public RxSubscriber(BaseViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public RxSubscriber(BaseViewModel viewModel, boolean showLoading) {
        this.viewModel = viewModel;
        this.showLoading = showLoading;
    }

    public RxSubscriber(BaseViewModel viewModel, boolean showLoading, String message) {
        this.viewModel = viewModel;
        this.message = message;
        this.showLoading = showLoading;
    }

    @Override
    public void onSubscribe(Disposable d) {
        viewModel.addDisposable(d);
        if (showLoading) {
            viewModel.showLoading(message);
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        if (showLoading) {
            viewModel.hideLoading();
        }
        if (e instanceof ApiException) {
            ApiException ex = (ApiException) e;
            if (ex.isNeedLogin()) {
                viewModel.needLogin();
            }
        }
        onFailed(e);
    }

    @Override
    public void onNext(T t) {
        if (showLoading) {
            viewModel.hideLoading();
        }
        onSuccess(t);
    }

    public abstract void onSuccess(T t);

    public abstract void onFailed(Throwable e);
}
