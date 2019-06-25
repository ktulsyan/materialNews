package ktulsyan.materialnews.ui;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ktulsyan.materialnews.NewsApplication;
import ktulsyan.materialnews.R;
import ktulsyan.materialnews.adapters.HeadlinesAdapter;
import ktulsyan.materialnews.data.NewsDataSource;
import ktulsyan.materialnews.data.NewsDatabaseDao;
import ktulsyan.materialnews.models.Article;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.swipe_refresher)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.news_list)
    ListView newsList;

    @Inject
    HeadlinesAdapter headlinesAdapter;

    @Inject
    ConnectivityManager connectivityManager;

    @Inject
    @Named("remote")
    NewsDataSource remoteSource;

    @Inject
    NewsDatabaseDao newsDatabase;

    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((NewsApplication)getApplication()).getApplicationComponent().inject(this);

        newsList.setItemsCanFocus(false);
        newsList.setAdapter(headlinesAdapter);
        newsList.setOnItemClickListener((parent, view, position, id) -> {
            Article article = (Article) headlinesAdapter.getItem(position);

            Intent intent = new Intent(this, NewsArticleActivity.class);
            intent.putExtra("article_source", article.getSourceUrl());
            startActivity(intent);
        });
    }

    private boolean isInternetAvailable() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshHeadlines();
        swipeRefreshLayout.setOnRefreshListener(this::refreshHeadlines);
    }

    private void refreshHeadlines() {
        disposables.add(
                Observable.fromCallable(this::fetchHeadlines)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(articles -> {
                                    headlinesAdapter.setData(articles);
                                    swipeRefreshLayout.setRefreshing(false);
                                },
                                e -> Timber.e(e, "Unable to fetch headlines"))
        );
    }


    private List<Article> fetchHeadlines() {
        if (isInternetAvailable()) {
            List<Article> articles = remoteSource.getArticles();
            newsDatabase.putArticles(articles);
            return articles;
        } else {
            return newsDatabase.getArticles();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }
}
