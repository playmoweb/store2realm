package com.playmoweb.store2realm.example.ui;

import com.playmoweb.store2realm.example.data.models.Post;
import com.playmoweb.store2realm.example.data.services.PostService;
import com.playmoweb.store2store.store.Optional;

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

    public void loadPosts() {
        Disposable d = postService.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<Optional<List<Post>>>(){
                    @Override
                    public void onNext(Optional<List<Post>> items) {
                        mainView.updatePosts(items.get());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        disposable.add(d);
    }
}
