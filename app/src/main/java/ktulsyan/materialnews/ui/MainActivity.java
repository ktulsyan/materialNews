package ktulsyan.materialnews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import javax.inject.Inject;

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
import ktulsyan.materialnews.models.Article;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.swipe_refresher)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.news_list)
    ListView newsList;

    HeadlinesAdapter headlinesAdapter;

    @Inject
    NewsDataSource newsService;

    CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ((NewsApplication)getApplication()).getApplicationComponent().inject(this);

        headlinesAdapter = new HeadlinesAdapter();

        newsList.setItemsCanFocus(false);
        newsList.setAdapter(headlinesAdapter);
        newsList.setOnItemClickListener((parent, view, position, id) -> {
            Article article = (Article) headlinesAdapter.getItem(position);

            Intent intent = new Intent(this, NewsArticleActivity.class);
            intent.putExtra("article_source", article.getSourceUrl());
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshHeadlines();
        swipeRefreshLayout.setOnRefreshListener(this::refreshHeadlines);
    }

    private void refreshHeadlines() {
        disposables.add(
                Observable.fromCallable(newsService::getArticles)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(articles -> {
                                    headlinesAdapter.setData(articles);
                                    swipeRefreshLayout.setRefreshing(false);
                                },
                                e -> Timber.e(e, "Unable to fetch headlines"))
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposables.clear();
    }
}
