package oluwaseun.akintola.movies;
/**
 * Created by AKINTOLA OLUWASEUN on 4/24/2017.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
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
import oluwaseun.akintola.movies.custom.TrailerAdapter;
import oluwaseun.akintola.movies.model.Trailer;
import oluwaseun.akintola.movies.utilities.NetworkUtils;

public class TrailerActivity extends AppCompatActivity
        implements TrailerAdapter.TrailerItemListener{

    private TrailerAdapter adapter;
    private RecyclerView recyclerView;
    private List<Trailer> list;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);


        recyclerView = (RecyclerView)findViewById(R.id.rv_movies_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        adapter = new TrailerAdapter(this, list, this);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String id = "";
        if(extras != null && extras.containsKey("_id"))
            id =  intent.getStringExtra("_id");

        url = NetworkUtils.buildUrl(id + getString(R.string.video_path), this);
        loadTrailers(url.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            if (list != null && list.size() > 0){
                String firstTrailerKey = list.get(0).getKey();
                String firstTrailerURL = getString(R.string.youtube_url) + "?v=" + firstTrailerKey;
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject));
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.message, firstTrailerURL));
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.chooser)));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadTrailers(String url) {

        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //mProgressBar.setVisibility(View.INVISIBLE);
                if (jsonObject != null) {
                    try {
                        parseJSONObject(jsonObject);
                        adapter.refreshList(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TrailerActivity.this, "Couldn't get videos", Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void parseJSONObject(JSONObject jsonObject) throws JSONException {

        JSONArray array = jsonObject.getJSONArray("results");
        for(int i = 0; i < array.length(); i++){
            JSONObject detail = array.getJSONObject(i);
            String name = detail.getString("name");
            int size = detail.getInt("size");
            String key = detail.getString("key");
            list.add(new Trailer(name, key, size));
        }
    }



    @Override
    public void onClick(int index) {
        Uri uri = Uri.parse(getString(R.string.youtube_url)).buildUpon()
                .appendQueryParameter("v", list.get(index).getKey()).build();

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(Intent.createChooser(intent, getString(R.string.chooser)));
    }
}
