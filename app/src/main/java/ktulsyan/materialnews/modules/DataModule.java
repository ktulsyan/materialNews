package ktulsyan.materialnews.modules;

import android.net.ConnectivityManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ktulsyan.materialnews.data.NewsApiService;
import ktulsyan.materialnews.data.NetworkSource;
import ktulsyan.materialnews.data.NewsDataSource;
import ktulsyan.materialnews.data.NewsDatabase;
import ktulsyan.materialnews.data.NewsDatabaseDao;
import ktulsyan.materialnews.data.NewsService;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
public class DataModule {
    private static final String NEWS_ORG_API_KEY = "cfc20b04a472402895e9ed0ade7e5fdb";

    @Provides
    static OkHttpClient provideOkHttpClient() {
        Interceptor API_KEY_INSERTER = chain -> {
            Request originalRequest = chain.request();
            if (originalRequest.header("X-Api-Key") == null) {
                Request authenticatedRequest = originalRequest.newBuilder()
                        .addHeader("X-Api-Key", NEWS_ORG_API_KEY)
                        .build();
                return chain.proceed(authenticatedRequest);
            }
            return chain.proceed(originalRequest);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(API_KEY_INSERTER)
                .build();
        return client;
    }

    @Provides
    static NewsApiService provideNewsApiService(OkHttpClient httpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NewsApiService.API_BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(NewsApiService.class);
    }

    @Provides
    @Singleton
    @Named("remote")
    static NewsDataSource provideRemoteSource(NetworkSource networkSource) {
        return networkSource;
    }

    @Provides
    static NewsDatabaseDao provideNewsDatabaseDao(NewsDatabase db) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return new NewsDatabaseDao(db, md);
        } catch (NoSuchAlgorithmException e) {
            Timber.e(e, "Hash algorithm not available!!! ");
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Singleton
    static NewsDataSource provideNewsService(ConnectivityManager connectivityManager,
                                             NewsDatabaseDao db,
                                             NetworkSource remoteSource) {
        return new NewsService(connectivityManager, db, remoteSource);
    }
}
