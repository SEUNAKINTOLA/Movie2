package oluwaseun.akintola.movies.custom;
/**
 * Created by AKINTOLA OLUWASEUN on 4/26/2017.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import oluwaseun.akintola.movies.R;
import oluwaseun.akintola.movies.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder>{
    private Context context;
    private List<Review> list;
    private ReviewItemListener reviewItemListener;

    public interface ReviewItemListener{
        void onClick(String id, String comment);
    }
    public ReviewAdapter(Context cx, List<Review> reviews, ReviewItemListener listener){
        context = cx;
        list = reviews;
        reviewItemListener = listener;
    }
    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.review_item, parent, false);
        ReviewHolder holder = new ReviewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        String user = list.get(position).getAuthor();
        String review = list.get(position).getReview();

        Typeface authorFont = Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-BlackItalic.ttf");
        holder.author.setTypeface(authorFont);

        Typeface commentFont = Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-Regular.ttf");
        holder.comment.setTypeface(commentFont);

        holder.author.setText(user);
        holder.comment.setText(review);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshList(List<Review> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    class ReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView author;
        TextView comment;

        public ReviewHolder(View itemView) {
            super(itemView);
            author = (TextView)itemView.findViewById(R.id.author);
            comment = (TextView)itemView.findViewById(R.id.comment);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            String id = author.getText().toString();
            String review = comment.getText().toString();
            reviewItemListener.onClick(id, review);
        }
    }
}
