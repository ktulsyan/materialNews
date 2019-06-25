package ktulsyan.materialnews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ktulsyan.materialnews.models.Article;
import ktulsyan.materialnews.models.TopHeadLinesResponse;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main {
    private static final String api_key = "cfc20b04a472402895e9ed0ade7e5fdb";
    private static final String USER_LOCATION = "in";
    private static final int PAGE_SIZE = 20;

    public static void main(String[] args) throws IOException {
        Interceptor API_KEY_INSERTER = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                if (originalRequest.header("X-Api-Key") == null) {
                    Request authenticatedRequest = originalRequest.newBuilder()
                            .addHeader("X-Api-Key", api_key)
                            .build();
                    return chain.proceed(authenticatedRequest);
                }
                return chain.proceed(originalRequest);
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(API_KEY_INSERTER)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NewsService.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsService service = retrofit.create(NewsService.class);

        System.out.println(String.format("%x, ", 10));

        List<Article> articles = new ArrayList<>();
        int page = 1;
        Call<TopHeadLinesResponse> call = service.listHeadlines(USER_LOCATION, PAGE_SIZE, page);
        Response<TopHeadLinesResponse> httpResponse = call.execute();

        if (httpResponse.code() == 200) {
            TopHeadLinesResponse response = httpResponse.body();

            articles.addAll(response.getArticles());

            int totalResults = response.getTotalResults();

            int remaining = totalResults - PAGE_SIZE;
            while (true) {
                page++;
                Call<TopHeadLinesResponse> newCall = service.listHeadlines(USER_LOCATION, PAGE_SIZE, page);
                httpResponse = newCall.execute();
                response = httpResponse.body();
                List<Article> partialResult = response.getArticles();
                articles.addAll(partialResult);
                remaining = remaining - partialResult.size();
            }
        }
    }
}
