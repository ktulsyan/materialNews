package ktulsyan.materialnews.models;

import java.util.List;

import lombok.Getter;

@Getter
public class TopHeadLinesResponse {
    String status;
    int totalResults;
    List<Article> articles;
}
