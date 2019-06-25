package ktulsyan.materialnews.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Inject;

public class NewsDatabase extends SQLiteOpenHelper {
    static class ArticleColumns {
        static final String COLUMN_TITLE_HASH = "title_hash";
        static final String COLUMN_TITLE = "title";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_SOURCE_URL = "sourceUrl";
        static final String COLUMN_IMAGE_URL = "imageUrl";
        static final String COLUMN_CONTENT = "content";
        static final String COLUMN_SOURCE = "source";
        static final String COLUMN_PUBLISHED_AT = "publishedAt";
    }

    static final String TABLE_ARTICLES = "articles";

    private static final String DATABASE_NAME = "NewsArticles";
    private static final int DATABASE_VERSION = 1;

    @Inject
    public NewsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_ARTICLES + "("
                + ArticleColumns.COLUMN_TITLE_HASH + " TEXT PRIMARY KEY, "
                + ArticleColumns.COLUMN_TITLE + " TEXT,"
                + ArticleColumns.COLUMN_DESCRIPTION + " TEXT,"
                + ArticleColumns.COLUMN_CONTENT + " TEXT,"
                + ArticleColumns.COLUMN_SOURCE + " TEXT,"
                + ArticleColumns.COLUMN_SOURCE_URL + " TEXT,"
                + ArticleColumns.COLUMN_IMAGE_URL + " TEXT,"
                + ArticleColumns.COLUMN_PUBLISHED_AT + " INTEGER" + ")";
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Upgrade not expected now
    }
}
