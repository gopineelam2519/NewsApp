package com.hk.newsapp.vm;

import android.util.Log;

import com.hk.newsapp.repositories.NewsRepo;
import com.hk.newsapp.enums.RequestResult;
import com.hk.newsapp.model.NewsItem;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewsListVM extends ViewModel {

    public NewsListVM(NewsRepo newsRepo) {
        this.newsRepo = newsRepo;
        requestResult = new MutableLiveData<>();
        this.newsItems = new MutableLiveData<>();
        loadNews();
    }

    public static final long DEFAULT_ITEM_ID = -1L;

    private MutableLiveData<RequestResult> requestResult;
    private MutableLiveData<List<NewsItem>> newsItems;
    private long lastItemId = DEFAULT_ITEM_ID;
    private Disposable newsDisposable;
    private NewsRepo newsRepo;
public static final String TAG = "LOG_TAG";

    public void loadNews() {
        requestResult.setValue(RequestResult.IN_PROGRESS);
        newsDisposable = newsRepo.getNews()
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<NewsItem>>() {
                    @Override
                    public void onNext(List<NewsItem> newsItems) {
                        Log.d(TAG, "VM onNext: " +newsItems.size());
                        NewsListVM.this.newsItems.postValue(newsItems);
                        requestResult.postValue(RequestResult.SUCCESS);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "VM onError: ");
                        requestResult.postValue(RequestResult.FAILURE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (newsDisposable != null && !newsDisposable.isDisposed())
            newsDisposable.dispose();
    }

    public MutableLiveData<List<NewsItem>> getNewsItems() {
        return newsItems;
    }

    public MutableLiveData<RequestResult> getRequestResult() {
        return requestResult;
    }

    public void onResultReceived() {
        requestResult.setValue(RequestResult.NONE);
    }

    public long getLastItemId() {
        return lastItemId;
    }

    public void setLastItemId(long lastItemId) {
        this.lastItemId = lastItemId;
    }
}
