package oluwaseun.akintola.movies.utilities;
/**
 * Created by AKINTOLA OLUWASEUN on 4/25/2017.
 */
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

import oluwaseun.akintola.movies.R;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {
    public static String MOVIE_BASE_URL;
    public static String PARAM_API;
    public static String API_KEY;

    public static URL buildUrl(String path, Context context) {
        MOVIE_BASE_URL = context.getString(R.string.base_url);
        PARAM_API = context.getString(R.string.api_param);
        API_KEY = context.getString(R.string.api_key);

        Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendEncodedPath(path)
                    .appendQueryParameter(PARAM_API, API_KEY)
                    .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static boolean isNetworkAvailable(Activity activity){
        ConnectivityManager con = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = con.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }
}