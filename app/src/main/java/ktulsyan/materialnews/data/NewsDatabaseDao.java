package ktulsyan.materialnews.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ktulsyan.materialnews.models.Article;
import timber.log.Timber;

import static ktulsyan.materialnews.data.NewsDatabase.ArticleColumns;
import static ktulsyan.materialnews.data.NewsDatabase.TABLE_ARTICLES;

public class NewsDatabaseDao {

    private NewsDatabase dbHelper;
    private MessageDigest messageDigest;

    @Inject
    public NewsDatabaseDao(NewsDatabase dbHelper, MessageDigest messageDigest) {
        this.dbHelper = dbHelper;
        this.messageDigest = messageDigest;
    }




    public void putArticles(List<Article> articles) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

        articles.stream().forEach(article -> {
            ContentValues values = new ContentValues();

            String fingerprint = fingerprint(article);

            values.put(ArticleColumns.COLUMN_TITLE_HASH, fingerprint);
            values.put(ArticleColumns.COLUMN_TITLE, article.getTitle());
            values.put(ArticleColumns.COLUMN_DESCRIPTION, article.getDescription());
            values.put(ArticleColumns.COLUMN_SOURCE_URL, article.getSourceUrl());
            values.put(ArticleColumns.COLUMN_SOURCE, article.getSource().getName());
            values.put(ArticleColumns.COLUMN_IMAGE_URL, article.getUrlToImage());
            values.put(ArticleColumns.COLUMN_CONTENT, article.getContent());
            String publishTime = article.getPublishedAt();
            try {
                Date date = dateFormat.parse(publishTime);
                values.put(ArticleColumns.COLUMN_PUBLISHED_AT, date.getTime());
            } catch (ParseException e) {
                Timber.e(e, "Illegal date in article: %s", publishTime);
            }

            db.insertWithOnConflict(TABLE_ARTICLES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        });
        db.close();
    }

    @NotNull
    private String fingerprint(Article article) {
        messageDigest.reset();
        String title = article.getTitle().toLowerCase();
        byte[] digest = messageDigest.digest(title.getBytes());
        BigInteger hash = new BigInteger(1, digest);
        return String.format("%32x", hash).replace(' ','0');
    }

    public List<Article> getArticles() {
        String query = "SELECT * FROM " + TABLE_ARTICLES
                + " ORDER BY " + ArticleColumns.COLUMN_PUBLISHED_AT + " DESC "
                + "LIMIT 20";
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Article> articles = new ArrayList<>();

        Cursor result = db.rawQuery(query, null);
        while (result.moveToNext()) {
            articles.add(buildArticle(result));
        }
        return articles;
    }

    private static Article buildArticle(Cursor cursor) {
        Article.Source source = Article.Source.builder()
                .name(cursor.getString(cursor.getColumnIndex(ArticleColumns.COLUMN_SOURCE)))
                .build();

        return Article.builder()
                .content(cursor.getString(cursor.getColumnIndex(ArticleColumns.COLUMN_CONTENT)))
                .title(cursor.getString(cursor.getColumnIndex(ArticleColumns.COLUMN_TITLE)))
                .description(cursor.getString(cursor.getColumnIndex(ArticleColumns.COLUMN_DESCRIPTION)))
                .sourceUrl(cursor.getString(cursor.getColumnIndex(ArticleColumns.COLUMN_SOURCE_URL)))
                .urlToImage(cursor.getString(cursor.getColumnIndex(ArticleColumns.COLUMN_IMAGE_URL)))
                .source(source)
                .build();
    }
}
