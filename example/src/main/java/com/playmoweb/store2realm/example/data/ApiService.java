package com.playmoweb.store2realm.example.data;

import com.google.gson.GsonBuilder;
import com.playmoweb.store2realm.example.data.models.Post;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by thibaud on 28/07/2017.
 */
public interface ApiService {

    @GET("/posts")
    Observable<List<Post>> getPosts();

    final class Creator {
        private Creator() {}

        public static ApiService buildApiService() {


            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient clientOkHttp = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://jsonplaceholder.typicode.com")
                    .client(clientOkHttp)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            return retrofit.create(ApiService.class);
        }
    }
}
