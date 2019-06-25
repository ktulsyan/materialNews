package ktulsyan.materialnews.data;

import java.util.List;

import ktulsyan.materialnews.models.Article;

public interface NewsDataSource {
    List<Article> getArticles();
}
