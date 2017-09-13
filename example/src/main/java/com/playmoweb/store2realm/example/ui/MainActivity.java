package com.playmoweb.store2realm.example.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.playmoweb.store2realm.example.ExampleApplication;
import com.playmoweb.store2realm.example.R;
import com.playmoweb.store2realm.example.data.models.Post;
import com.playmoweb.store2realm.example.injection.component.ActivityComponent;
import com.playmoweb.store2realm.example.injection.component.DaggerActivityComponent;
import com.playmoweb.store2realm.example.injection.module.ActivityModule;

import java.util.List;

import javax.inject.Inject;

/**
 * @author  Thibaud Giovannetti
 * @by      Playmoweb
 * @date    10/09/2017
 */
public class MainActivity extends AppCompatActivity implements MainView {

    @Inject
    MainPresenter mainPresenter;

    private ListView listOfPosts;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);

        mainPresenter.attachView(this);

        listOfPosts = findViewById(R.id.listOfPosts);

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.title, new String[]{});
        listOfPosts.setAdapter(adapter);

        mainPresenter.loadPosts(); // load posts
    }

    @Override
    protected void onDestroy() {
        mainPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void updatePosts(List<Post> posts){
        updateAdapter(posts);
        Toast.makeText(this, "UI refreshed with datas", Toast.LENGTH_LONG).show();
    }

    private void updateAdapter(List<Post> posts){
        String[] items = new String[posts.size()];
        for(int i = 0; i < posts.size(); i++){
            items[i] = posts.get(i).title;
        }

        // monkey update ;)
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.title, items);
        listOfPosts.setAdapter(adapter);
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
