package ktulsyan.materialnews.ui;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import ktulsyan.materialnews.R;
import timber.log.Timber;

public class NewsArticleActivity extends AppCompatActivity {
    @BindView(R.id.webview)
    WebView newsArticleWebview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        ButterKnife.bind(this);

        newsArticleWebview.getSettings().setJavaScriptEnabled(true);

        newsArticleWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(NewsArticleActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        //Read url from intent
        String articleUrl = intent.getStringExtra("article_source");
        Timber.d("Opening url %s in webview", articleUrl);
        newsArticleWebview.loadUrl(articleUrl);
    }
}

