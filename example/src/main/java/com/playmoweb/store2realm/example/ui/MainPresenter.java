package com.playmoweb.store2realm.example.ui;

import com.playmoweb.store2realm.example.data.models.Post;
import com.playmoweb.store2realm.example.data.services.PostService;
import com.playmoweb.store2store.store.Optional;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortType;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    10/09/2017
 */
public class MainPresenter {
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final PostService postService;

    private MainView mainView;


    @Inject
    public MainPresenter(PostService postService) {
        this.postService = postService;
    }

    public void detachView() {
        disposable.clear();
        this.mainView = null;
    }

    public void attachView(MainView view) {
        mainView = view;
    }

    public void loadPosts(boolean filterAndSortPosts) {
        mainView.hideError();

        SortingMode sortingMode = null;
        Filter filter = null;
        if(filterAndSortPosts){
            sortingMode = new SortingMode("userId", SortType.ASCENDING);
            filter = new Filter("userId", 5);
        }

        Disposable d = postService.getAll(filter, sortingMode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), true) // add true here if you want to delay the error
                .subscribeWith(new DisposableSubscriber<Optional<List<Post>>>(){
                    @Override
                    public void onNext(Optional<List<Post>> items) {
                        if(!items.isNull()){
                            mainView.updatePosts(items.get());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mainView.updatePosts(null);
                        mainView.showError("Network error");
                    }

                    @Override
                    public void onComplete() {
                        mainView.hideError();
                    }
                });

        disposable.add(d);
    }
}
