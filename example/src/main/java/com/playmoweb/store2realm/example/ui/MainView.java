package com.playmoweb.store2realm.example.ui;

import com.playmoweb.store2realm.example.data.models.Post;

import java.util.List;

/**
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    10/09/2017
 */
public interface MainView {
    void updatePosts(List<Post> posts);
}
