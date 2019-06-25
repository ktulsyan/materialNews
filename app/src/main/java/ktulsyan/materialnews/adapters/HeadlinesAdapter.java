package ktulsyan.materialnews.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ktulsyan.materialnews.R;
import ktulsyan.materialnews.models.Article;
import lombok.Getter;
import lombok.Setter;

public class HeadlinesAdapter extends BaseAdapter {
    private final List<Article> articles;

    @Inject
    public HeadlinesAdapter() {
        articles = new ArrayList<>();
    }

    public void setData(List<Article> data) {
        articles.clear();
        articles.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            convertView = layoutInflater.inflate(R.layout.layout_news_item, parent, false);
        }

        Article article = (Article) getItem(position);
        ViewHolder vh = (ViewHolder) convertView.getTag();
        if (vh == null) {
            vh = new ViewHolder(convertView);
        }
        vh.bindView(article);

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.headline_image)
        ImageView headline_image;

        @BindView(R.id.headline_text)
        TextView headline_text;

        @BindView(R.id.content_preview)
        TextView article_content;

        @BindView(R.id.news_source)
        TextView article_source;

        @Getter
        @Setter
        String articleUrl;

        ViewHolder(@NonNull View view) {
            ButterKnife.bind(ViewHolder.this, view);
            articleUrl = null;
            view.setTag(ViewHolder.this);
        }

        void bindView(Article article) {
            Picasso.get()
                    .load(article.getUrlToImage())
                    .resize(112, 112)
                    .centerCrop()
                    .into(headline_image);
            article_content.setText(article.getDescription());
            headline_text.setText(article.getTitle());
            article_source.setText(article.getSource().getName());
            articleUrl = article.getSourceUrl();
        }
    }

}


