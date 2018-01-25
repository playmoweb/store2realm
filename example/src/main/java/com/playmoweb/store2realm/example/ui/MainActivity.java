package com.playmoweb.store2realm.example.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
    private Button allButton;
    private Button filteredButton;
    private TextView errorMessage;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActivityComponent().inject(this);

        mainPresenter.attachView(this);

        listOfPosts = findViewById(R.id.listOfPosts);
        allButton = findViewById(R.id.allButton);
        filteredButton = findViewById(R.id.filteredButton);
        errorMessage = findViewById(R.id.errorMessage);

        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPresenter.loadPosts(false);
                disableButtons(true);
            }
        });

        filteredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPresenter.loadPosts(true);
                disableButtons(true);
            }
        });

        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.title, new String[]{});
        listOfPosts.setAdapter(adapter);

        // mainPresenter.loadPosts(false); // load posts
        mainPresenter.loadPosts(false);
    }

    private void disableButtons(boolean disable){
        allButton.setEnabled(!disable);
        filteredButton.setEnabled(!disable);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        mainPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void updatePosts(List<Post> posts){
        disableButtons(false);
        updateAdapter(posts);
        Toast.makeText(this, "UI updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String message) {
        errorMessage.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideError() {
        errorMessage.setVisibility(View.GONE);
    }

    private void updateAdapter(List<Post> posts){
        if(posts == null){
            return;
        }

        String[] items = new String[posts.size()];
        for(int i = 0; i < posts.size(); i++){
            final Post p = posts.get(i);
            items[i] = p.title + " (userId = "+p.userId+")";
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
