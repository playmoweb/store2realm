package com.playmoweb.store2realm.example.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.playmoweb.store2realm.example.ExampleApplication;
import com.playmoweb.store2realm.example.R;
import com.playmoweb.store2realm.example.data.models.Post;
import com.playmoweb.store2realm.example.injection.component.ActivityComponent;
import com.playmoweb.store2realm.example.injection.component.DaggerActivityComponent;
import com.playmoweb.store2realm.example.injection.module.ActivityModule;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainView {

    @Inject
    MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);

        mainPresenter.attachView(this);

        mainPresenter.loadPosts(); // load posts
    }

    @Override
    protected void onDestroy() {
        mainPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void updatePosts(List<Post> posts, boolean updated){
        if(updated){
            Toast.makeText(this, "Datas updated ! Kill and relaunch the app to see the realm cache operate !", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Datas come from realm cache", Toast.LENGTH_LONG).show();
        }
    }


    // UTILS
    private ActivityComponent mActivityComponent;
    public ActivityComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(ExampleApplication.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }
}
