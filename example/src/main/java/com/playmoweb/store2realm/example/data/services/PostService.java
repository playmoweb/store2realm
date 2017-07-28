package com.playmoweb.store2realm.example.data.services;

import com.playmoweb.store2realm.example.data.ApiService;
import com.playmoweb.store2realm.example.data.models.Post;
import com.playmoweb.store2realm.service.BaseRealmService;
import com.playmoweb.store2store.utils.Filter;
import com.playmoweb.store2store.utils.SortingMode;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by thibaud on 28/07/2017.
 */
public class PostService extends BaseRealmService<Post> {

    private final ApiService apiService;

    @Inject
    public PostService(ApiService apiService) {
        super(Post.class);
        this.apiService = apiService;
    }

    @Override
    protected Observable<List<Post>> getAll(Filter filter, SortingMode sortingMode) {
        return apiService.getPosts();
    }
}
