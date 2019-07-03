package ktulsyan.materialnews.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ktulsyan.materialnews.models.Article;
import ktulsyan.materialnews.models.TopHeadLinesResponse;
import retrofit2.Call;
import retrofit2.Response;

public class NetworkSource implements NewsDataSource {
    private static final int PAGE_SIZE = 40;
    private static final String USER_LOCATION = "in";

    private final NewsApiService service;

    @Inject
    NetworkSource(NewsApiService service) {
        this.service = service;
    }

    @Override
    public List<Article> getArticles() {
        List<Article> articles = new ArrayList<>();
        try {
            int page = 1;
            Call<TopHeadLinesResponse> call = service.listHeadlines(USER_LOCATION, PAGE_SIZE, page);
            Response<TopHeadLinesResponse> httpResponse = call.execute();

            if (httpResponse.code() == 200) {
                TopHeadLinesResponse response = httpResponse.body();
                articles.addAll(response.getArticles());

                int totalResults = response.getTotalResults();

                int remaining = totalResults - PAGE_SIZE;
                while (remaining > 0) {
                    page++;
                    call = service.listHeadlines(USER_LOCATION, PAGE_SIZE, page);
                    httpResponse = call.execute();
                    response = httpResponse.body();
                    List<Article> partialResult = response.getArticles();
                    articles.addAll(partialResult);
                    remaining = remaining - partialResult.size();
                }
            }
        } catch (IOException e) {
            //Unable to refresh
        }
        return articles;
    }
}
