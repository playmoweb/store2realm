package com.playmoweb.store2realm.example.data.models;

import com.playmoweb.store2realm.model.HasId;

import org.parceler.Parcel;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    10/09/2017
 */
@Parcel
public class Post extends RealmObject implements HasId<Integer> {
    @PrimaryKey
    public int id;
    public int userId;
    public String title;
    public String body;

    @Override
    public Integer getUniqueIdentifier() {
        return id;
    }

    @Override
    public String getUniqueIdentifierName() {
        return "id";
    }
}
