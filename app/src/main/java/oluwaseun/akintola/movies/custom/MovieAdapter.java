package oluwaseun.akintola.movies.custom;
/**
 * Created by AKINTOLA OLUWASEUN on 4/26/2017.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import oluwaseun.akintola.movies.R;
import oluwaseun.akintola.movies.model.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    final private ListItemClickListener mListClickListener;
    private List<Movie> list;
    private Context context;
    public static boolean loadFromSDCard = false;

    public interface ListItemClickListener{
       void onItemClick(int position);
    }
    public MovieAdapter(List list, ListItemClickListener listener){
        this.list = list;
        mListClickListener = listener;
    }
    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutForListItem = R.layout.list_item;
        boolean attachImmediatelyToParent = false;

        View view = inflater.inflate(layoutForListItem, viewGroup, attachImmediatelyToParent);
        MovieViewHolder viewHolder = new MovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        ImageView poster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.rv_image);
            itemView.setOnClickListener(this);
        }

        void bind(int index){
            Movie movie = list.get(index);
            if (loadFromSDCard){
                //if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(movie.getPosterImage());
                    poster.setImageBitmap(bitmap);
                //}else Toast.makeText(context, "image not found", Toast.LENGTH_SHORT).show();
            }
            else {
                Picasso.with(context).load(movie.getPosterImage())
                        .resize(185, 195).into(poster);
            }
        }

        @Override
        public void onClick(View v) {
            int itemClickedIndex = getAdapterPosition();
            mListClickListener.onItemClick(itemClickedIndex);
        }
    }

    public void swapList(List list){
        if(list != null){
            this.list = list;
            this.notifyDataSetChanged();
        }
    }
}
