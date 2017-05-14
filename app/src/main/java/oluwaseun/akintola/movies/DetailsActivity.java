package oluwaseun.akintola.movies;
/**
 * Created by AKINTOLA OLUWASEUN on 4/24/2017.
 */

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import oluwaseun.akintola.movies.custom.MovieAdapter;
import oluwaseun.akintola.movies.data.MovieContract;
import oluwaseun.akintola.movies.model.Movie;

public class DetailsActivity extends Activity{
    private TextView originalTitle;
    private TextView overview;
    private TextView releasedDate;

    private TextView voteAverage;
    private ImageView poster;
    private ImageView backdrop;
    private Movie movieDetails;

    private int movieIsFavourite = android.R.drawable.btn_star_big_on;
    private int movieIsNotFavourite = android.R.drawable.btn_star_big_off;
    private boolean isFavourite;
    private SharedPreferences preferences;
    public static final String FOLDER = "Posters";
    public static final String FAVORITES = "favourites";

    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.details_activity);
        preferences = getSharedPreferences(FAVORITES, MODE_PRIVATE);

        originalTitle = (TextView) findViewById(R.id.title);
        overview  = (TextView) findViewById(R.id.overview);
        releasedDate  = (TextView) findViewById(R.id.date);

        voteAverage  = (TextView) findViewById(R.id.ratings);
        poster  = (ImageView) findViewById(R.id.poster);
        backdrop = (ImageView)findViewById(R.id.backdrop);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        movieDetails = null;
        if(extras != null && extras.containsKey("movieDetails"))
            movieDetails =  intent.getParcelableExtra("movieDetails");

        isFavourite = preferences.getBoolean(movieDetails.getId(), false);
        setFavouriteIcon();
        setDetails();
    }

    private void setDetails() {
        Typeface TitleFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        originalTitle.setText(movieDetails.getMovieTitle());
        originalTitle.setTypeface(TitleFont);

        Typeface overViewFont = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Light.ttf");
        overview.setText(movieDetails.getOverview());
        overview.setTypeface(overViewFont);

        String year = movieDetails.getReleasedDate().substring(0, 4);
        releasedDate.setText("Year: " + year);
        voteAverage.setText(String.valueOf("Rating: " + movieDetails.getViewerRatings()));

        if (MovieAdapter.loadFromSDCard){
            Bitmap bitmap = BitmapFactory.decodeFile(movieDetails.getPosterImage());
            poster.setImageBitmap(bitmap);
            poster.setImageBitmap(bitmap);
        }
        else {
            Picasso.with(getBaseContext()).load(movieDetails.getPosterImage())
                    .networkPolicy(NetworkPolicy.OFFLINE).resize(300, 400).into(poster);

            Picasso.with(getBaseContext()).load(movieDetails.getPosterImage())
                    .into(backdrop);
        }
    }

    public void toggleFavouriteIcon(View view){
        if (!isFavourite)
        {
            ((ImageButton)findViewById(R.id.favoriteLogo))
                    .setImageResource(movieIsFavourite);
            isFavourite = true;
            saveFavouriteSettings();
            saveFavouriteToDb();
        }
        else
        {
            ((ImageButton)findViewById(R.id.favoriteLogo))
                    .setImageResource(movieIsNotFavourite);
            isFavourite = false;
            saveFavouriteSettings();
            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            getContentResolver().delete(uri, movieDetails.getId(), null);
        }
    }

    private void saveFavouriteToDb() {
        String posterSDCardLocation = saveImage();
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movieDetails.getMovieTitle());
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieDetails.getOverview());
        values.put(MovieContract.MovieEntry.COLUMN_DATE, movieDetails.getReleasedDate());
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieDetails.getId());
        values.put(MovieContract.MovieEntry.COLUMN_POSTER, posterSDCardLocation);
        values.put(MovieContract.MovieEntry.COLUMN_RATING, movieDetails.getViewerRatings());

        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

        if(uri != null) {
            Toast.makeText(getBaseContext(), getString(R.string.favourite), Toast.LENGTH_LONG).show();
        }
    }

    private String saveImage() {
        String root = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
        File imageDir = new File(root, FOLDER);
        imageDir.mkdirs();
        String poster = "poster-" + movieDetails.getId() + ".png";
        File file = new File(imageDir, poster);
        if (file.exists())
            file.delete();
        try {
            Bitmap posterBitmap = ((BitmapDrawable)backdrop.getDrawable()).getBitmap();
            FileOutputStream out = new FileOutputStream(file);
            posterBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private void saveFavouriteSettings() {
        SharedPreferences.Editor editor = preferences.edit();
        if (isFavourite){
            editor.putBoolean(movieDetails.getId(), isFavourite);
            editor.commit();
        }
        else {
            editor.remove(movieDetails.getId());
            editor.commit();
        }

    }

    public void setFavouriteIcon(){
        if (isFavourite) {
            ((ImageButton) findViewById(R.id.favoriteLogo))
                    .setImageResource(movieIsFavourite);
        }
        else
        {
            ((ImageButton)findViewById(R.id.favoriteLogo))
                    .setImageResource(movieIsNotFavourite);
        }
    }

    public void goToReviews(View view){
        String id = movieDetails.getId();
        Intent reviewIntent = new Intent(this, ReviewActivity.class);
        reviewIntent.putExtra("_id", id);
        startActivity(reviewIntent);
    }

    public void goToTrailers(View view){
        String id = movieDetails.getId();
        Intent trailerIntent = new Intent(this, TrailerActivity.class);
        trailerIntent.putExtra("_id", id);
        startActivity(trailerIntent);
    }
}
