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
import oluwaseun.akintola.movies.model.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder>{
    private Context context;
    private List<Trailer> list;
    private TrailerItemListener reviewItemListener;

    public interface TrailerItemListener{
        void onClick(int index);
    }
    public TrailerAdapter(Context cx, List trailers, TrailerItemListener listener){
        context = cx;
        list = trailers;
        reviewItemListener = listener;
    }
    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_trailer, parent, false);
        TrailerHolder holder = new TrailerHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        String name = list.get(position).getName();
        int size = list.get(position).getSize();

        Typeface authorFont = Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-BlackItalic.ttf");
        holder.name.setTypeface(authorFont);

        Typeface commentFont = Typeface.createFromAsset(context.getAssets(), "fonts/SourceSansPro-Regular.ttf");
        holder.size.setTypeface(commentFont);

        holder.name.setText(name);
        holder.size.setText(String.valueOf(size) + "KB");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshList(List<Trailer> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    class TrailerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        TextView size;

        public TrailerHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name);
            size = (TextView)itemView.findViewById(R.id.size);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            reviewItemListener.onClick(index);
        }
    }
}
