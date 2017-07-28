package com.playmoweb.store2realm.example.ui;

import android.util.Log;

import com.playmoweb.store2realm.example.data.models.Post;
import com.playmoweb.store2realm.example.data.services.PostService;
import com.playmoweb.store2store.utils.CustomObserver;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by thibaud on 28/07/2017.
 */

public class MainPresenter {
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final PostService postService;

    private MainView mainView;


    @Inject
    public MainPresenter(PostService postService) {
        this.postService = postService;
    }

    public void detachView(){
        disposable.clear();
        this.mainView = null;
    }

    public void attachView(MainView view){
        mainView = view;
    }

    public void loadPosts(){
        postService.getAll(observerForPosts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observerForPosts);
    }

    CustomObserver<List<Post>> observerForPosts = new CustomObserver<List<Post>>(disposable) {
        @Override
        public void onError(Throwable throwable, boolean updated) {
            Log.e("MainPresenter", throwable.getMessage());
        }

        @Override
        public void onNext(List<Post> posts, boolean updated) {
            mainView.updatePosts(posts, updated);
        }
    };
}
