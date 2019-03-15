package com.bingo.fastframe.library.base;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.bingo.fastframe.library.event.ActionEvent;
import com.bingo.fastframe.library.event.IViewModelAction;
import com.bingo.fastframe.library.net.ApiService;
import com.bingo.fastframe.library.net.HttpClient;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends AndroidViewModel implements IViewModelAction {

    private MutableLiveData<ActionEvent> actionEventMutableLiveData;
    private CompositeDisposable mCompositeDisposable;
    protected ApiService apiService;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        apiService = HttpClient.getInstance().getApiService();
        actionEventMutableLiveData = new MutableLiveData<>();
    }

    @Override
    public MutableLiveData<ActionEvent> getAction() {
        return actionEventMutableLiveData;
    }

    @Override
    public void showLoading(String message) {
        actionEventMutableLiveData.setValue(new ActionEvent(ActionEvent.SHOW_LOADING, message));
    }

    @Override
    public void hideLoading() {
        actionEventMutableLiveData.setValue(new ActionEvent(ActionEvent.HIDE_LOADING, null));
    }

    @Override
    public void showToast(String message) {
        actionEventMutableLiveData.setValue(new ActionEvent(ActionEvent.SHOW_TOAST, message));
    }

    @Override
    public void showError(String message) {
        actionEventMutableLiveData.setValue(new ActionEvent(ActionEvent.SHOW_ERROR, message));
    }

    @Override
    public void needLogin() {
        actionEventMutableLiveData.setValue(new ActionEvent(ActionEvent.NEED_LOGIN, null));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        unDisposable();
    }

    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    private void unDisposable() {
        if (mCompositeDisposable != null && mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
        }
    }
}
