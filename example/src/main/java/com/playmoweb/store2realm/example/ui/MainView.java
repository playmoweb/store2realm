package com.playmoweb.store2realm.example.ui;

import com.playmoweb.store2realm.example.data.models.Post;

import java.util.List;

/**
 * Created by thibaud on 28/07/2017.
 */
public interface MainView {
    void updatePosts(List<Post> posts);
}
