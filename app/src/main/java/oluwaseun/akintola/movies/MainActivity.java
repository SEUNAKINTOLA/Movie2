package oluwaseun.akintola.movies;
/**
 * Created by AKINTOLA OLUWASEUN on 4/24/2017.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import oluwaseun.akintola.movies.app.AppController;
import oluwaseun.akintola.movies.custom.MovieAdapter;
import oluwaseun.akintola.movies.data.MovieContract;
import oluwaseun.akintola.movies.model.Movie;
import oluwaseun.akintola.movies.utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TASK_LOADER_ID = 0;
    private final static String SORT_ORDER = "pref_sort_order";
    private List<Movie> movies;
    private RecyclerView mMovieList;
    private MovieAdapter adapter;
    private String mSortOrder;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mSortOrder = preferences.getString(SORT_ORDER, "popular");

        movies = new ArrayList<>();
        mMovieList = (RecyclerView) findViewById(R.id.rv_movies_list);
        adapter = new MovieAdapter(movies, MainActivity.this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mMovieList.setLayoutManager(new GridLayoutManager(this, 2));
        else
            mMovieList.setLayoutManager(new GridLayoutManager(this, 3));
        mMovieList.setHasFixedSize(true);

        if (mSortOrder.equals(DetailsActivity.FAVORITES)) {
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        } else {
            if (NetworkUtils.isNetworkAvailable(this)) {
                mProgressBar.setVisibility(View.VISIBLE);
                downloadMovieDetails();
            } else
                setNoConnectionLayout();
        }
    }

    private void setNoConnectionLayout() {
        ((findViewById(R.id.rv_no_conn))).setVisibility(View.VISIBLE);
        ((findViewById(R.id.rv_refresh))).setVisibility(View.VISIBLE);
    }

    public void refresh(View view) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            mProgressBar.setVisibility(View.VISIBLE);
            ((findViewById(R.id.rv_no_conn))).setVisibility(View.INVISIBLE);
            ((findViewById(R.id.rv_refresh))).setVisibility(View.INVISIBLE);
            downloadMovieDetails();
        }
    }

    private void downloadMovieDetails() {
        URL apiURL = NetworkUtils.buildUrl(mSortOrder, this);
        String url = apiURL.toString();
        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                mProgressBar.setVisibility(View.INVISIBLE);
                if (jsonObject != null) {
                    parseJSONObject(jsonObject);
                    MovieAdapter.loadFromSDCard = false;
                    mMovieList.setAdapter(adapter);
                    //adapter.swapList(movies);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MainActivity.this, "Couldn't get movies", Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("movieDetails", movies.get(position));
        startActivity(intent);
    }

    private void parseJSONObject(JSONObject jsonObject) {
        movies.clear();
        try {
            JSONArray list = jsonObject.getJSONArray("results");

            for (int i = 0; i < list.length(); i++) {
                JSONObject details = list.getJSONObject(i);
                String id = details.getString("id");
                String title = details.getString("original_title");
                String poster = details.getString("poster_path");
                String summary = details.getString("overview");
                double ratings = details.getDouble("vote_average");
                String date = details.getString("release_date");
                String backdrop = details.getString("backdrop_path");
                movies.add(new Movie(id, title, ratings, summary, date, poster, backdrop));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(
                        SharedPreferences sharedPreferences, String key) {
                    if (key.equals(SORT_ORDER)) {
                        mSortOrder = sharedPreferences.getString(SORT_ORDER, "popular");
                        if (mSortOrder.equals(DetailsActivity.FAVORITES))
                            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, MainActivity.this);
                        else{
                        if (NetworkUtils.isNetworkAvailable(MainActivity.this))
                            downloadMovieDetails();
                        }
                    }
                }
            };

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        if (mSortOrder.equals(DetailsActivity.FAVORITES))
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor movieData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (movieData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(movieData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                movieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movies.clear();
        if (data != null) {
            int indexTitle = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            int indexOverview = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
            int indexDate = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_DATE);
            int indexID = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            int indexPoster = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER);
            int indexRating = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING);

            for (int i = 0; i < data.getCount(); i++) {
                data.moveToPosition(i);
                String id = data.getString(indexID);
                String title = data.getString(indexTitle);
                String rating = data.getString(indexRating);
                String date = data.getString(indexDate);
                String overview = data.getString(indexOverview);
                String poster = data.getString(indexPoster);
                movies.add(new Movie(id, title, Double.parseDouble(rating), overview, date, poster));
            }
            data.close();
            MovieAdapter.loadFromSDCard = true;
            //adapter = new MovieAdapter(movies, MainActivity.this);
            mMovieList.setAdapter(adapter);
            //adapter.swapList(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
