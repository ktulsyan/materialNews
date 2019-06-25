package ktulsyan.materialnews.models;

import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Article {
    String title;
    String description;
    @SerializedName("url") String sourceUrl;
    String urlToImage;
    String publishedAt;
    String content;
    Source source;

    @Getter
    @Builder
    public static class Source {
        String id;
        String name;
    }
}
