package ktulsyan.materialnews.data;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ktulsyan.materialnews.models.Article;

public class NewsService implements NewsDataSource {
    final ConnectivityManager connectivityManager;
    final NewsDatabaseDao db;
    final NewsDataSource remoteSource;

    @Inject
    public NewsService(ConnectivityManager connectivityManager,
                       NewsDatabaseDao db,
                       @Named("remote") NewsDataSource remoteSource) {
        this.connectivityManager = connectivityManager;
        this.db = db;
        this.remoteSource = remoteSource;
    }

    private boolean isInternetAvailable() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public List<Article> getArticles() {
        if (isInternetAvailable()) {
            List<Article> articles = remoteSource.getArticles();
            db.putArticles(articles);
            return articles;
        } else {
            return db.getArticles();
        }
    }
}
