package oluwaseun.akintola.movies;
/**
 * Created by AKINTOLA OLUWASEUN on 4/24/2017.
 */

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ReviewDetailActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_detail);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String id = "";
        String review = "";
        if(extras != null && extras.containsKey("_id") &&
                extras.containsKey("review")) {
            id = intent.getStringExtra("_id");
            review = intent.getStringExtra("review");
        }

        TextView author = (TextView)findViewById(R.id.author);
        TextView comment = (TextView)findViewById(R.id.comment);

        Typeface authorFont = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-BlackItalic.ttf");
        author.setTypeface(authorFont);
        author.setText(id);

        Typeface commentFont = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Regular.ttf");
        comment.setTypeface(commentFont);
        comment.setText(review);
    }
}
