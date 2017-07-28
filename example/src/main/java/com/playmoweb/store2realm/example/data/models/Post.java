package com.playmoweb.store2realm.example.data.models;

import org.parceler.Parcel;

import io.realm.PostRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by thibaud on 28/07/2017.
 */
@Parcel(implementations = { PostRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Post.class })
public class Post extends RealmObject {
    @PrimaryKey
    public int id;
    public int userId;
    public String title;
    public String body;
}
