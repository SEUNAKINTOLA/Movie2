package oluwaseun.akintola.movies;
/**
 * Created by AKINTOLA OLUWASEUN on 4/24/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
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
import oluwaseun.akintola.movies.custom.ReviewAdapter;
import oluwaseun.akintola.movies.model.Review;
import oluwaseun.akintola.movies.utilities.NetworkUtils;

public class ReviewActivity extends AppCompatActivity
        implements ReviewAdapter.ReviewItemListener{
    private RecyclerView recyclerView;
    private List<Review> list;
    private ReviewAdapter adapter;
    private String reviewTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        recyclerView = (RecyclerView)findViewById(R.id.rv_review);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        list = new ArrayList<>();
        adapter = new ReviewAdapter(this, list, this);
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String id = "";
        if(extras != null && extras.containsKey("_id"))
            id =  intent.getStringExtra("_id");

        URL url = NetworkUtils.buildUrl(id + getString(R.string.review_path), this);
        loadReviews(url.toString());
    }

    private void loadReviews(String url) {

        // making fresh volley request and getting json
        JsonObjectRequest jsonReq = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //mProgressBar.setVisibility(View.INVISIBLE);
                if (jsonObject != null) {
                    try {
                        parseJSONObject(jsonObject);
                        ((TextView)findViewById(R.id.reviewTitle))
                                .setText("Comments(" + reviewTotal + ")");
                        adapter.refreshList(list);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(ReviewActivity.this, "Couldn't get Reviews", Toast.LENGTH_SHORT).show();
            }
        });

        // Adding request to volley request queue
        AppController.getInstance().addToRequestQueue(jsonReq);
    }

    private void parseJSONObject(JSONObject jsonObject) throws JSONException {
        reviewTotal = jsonObject.getString("total_results");
        JSONArray array = jsonObject.getJSONArray("results");
        for(int i = 0; i < array.length(); i++){
            JSONObject detail = array.getJSONObject(i);
            String author = detail.getString("author");
            String comment = detail.getString("content");
            list.add(new Review(author, comment));
        }
    }

    @Override
    public void onClick(String id, String comment) {
        Intent detailIntent = new Intent(this, ReviewDetailActivity.class);
        detailIntent.putExtra("_id", id);
        detailIntent.putExtra("review", comment);
        startActivity(detailIntent);
    }
}
