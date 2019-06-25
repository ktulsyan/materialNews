package ktulsyan.materialnews;

import ktulsyan.materialnews.models.TopHeadLinesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NewsService {
    String API_BASE_URL = "https://newsapi.org/";

    @GET("/v2/top-headlines")
    Call<TopHeadLinesResponse>  listHeadlines(@Query("country") String countryCode,
                                              @Query("pageSize") int pageSize,
                                              @Query("page") int page);
}
