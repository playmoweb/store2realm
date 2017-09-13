# Rxjava2 Store2Realm

This is the new version of Store2Realm. This version is incompatible with previous versions.

*If you are looking for a version with rxjava 1.x, please use the rxjava1 branch. The API is different !*

## About

Store2Realm simplify the synchronization between a store (eg: a distant API) with a local Realm datastore on android.

## Installation

### Gradle

```
# main build.gradle file
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' } # add this line
    }
}
```

```
# app/build.gradle file
compile 'com.github.playmoweb:store2realm:<TAG>'
```


## Example

You can try/browse the example in the repository. This example connect to a remote API and load all posts from it.

It demonstrate the cache of Realm (try it offline after first launch) which is synchronized with the initial call to the API.

## Usage

This is an example of a sync between an API and a local Realm storage.


1. Extend RealmService<T>


```java
public class PostService extends StoreService<Post> {
    
}
```

2. Implement required methods


We want to get all objects from our API. 
This example use Dagger for the injection and the attribut ApiService is a Retrofit class to connect to your API. The class below is just an entry point to it.

We implement the getAll method from the RealmService to point to our API. This method can call any kind of service (file, sql, ...), it doesn't matter.

```java
@Parcel(implementations = { PostRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { Post.class })
public class Post extends RealmObject implements HasId {
    // ...
}

// The service and its DAO
public class PostService extends StoreService<Post> {

    @Inject
    public PostService(ApiService apiService) {
        super(Post.class, new PostDao(apiService));
        
        // things happen here !
        // We will sync this Store (external APIStore) to a RealmStore.
        // Every changes applied to the API will be reflected in the Realm store (cache)
        this.syncWith(new BaseRealmService<>(Post.class));
    }

    // DAO impl
    public static class PostDao extends StoreDao<Post> {
        private final ApiService apiService;

        public PostDao(ApiService apiService) {
            this.apiService = apiService;
        }

        @Override
        public Flowable<Optional<List<Post>>> getAll(Filter filter, SortingMode sortingMode) {
            // If you use retrofit you can wrap the response directly in a
            // Optional object by default
            return wrapOptional(apiService.getPosts());
        }
    }
}
```

3. Call from your presenter (MVP) your service

```java
    private final PostService postService; // injected by dagger in the constructor

    public void loadPosts() {
        Disposable d = postService.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<Optional<List<Post>>>(){
                    @Override
                    public void onNext(Optional<List<Post>> items) {
                        mainView.updatePosts(items.get()); // refresh the UI
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace(); // do something here
                    }

                    @Override
                    public void onComplete() { }
                });

        disposable.add(d);
    }
````


## Contributors
Please see [CONTRIBUTORS.md](https://github.com/playmoweb/store2realm/blob/master/CONTRIBUTORS.md)
