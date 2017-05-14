package oluwaseun.akintola.movies.model;
/**
 * Created by AKINTOLA OLUWASEUN on 4/25/2017.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{
    private String id;
    private String movieTitle;
    private double viewerRatings;
    private String overview;
    private String releasedDate;
    private String posterImage;
    private String backPoster;
    private final static String BASE_URL = "http://image.tmdb.org/t/p/w185//";

    public Movie(String _id, String title, double ratings,
                 String summary, String date, String relativePath, String backdropPath){
        id = _id;
        movieTitle = title;
        viewerRatings = ratings;
        overview = summary;
        releasedDate = date;
        posterImage = BASE_URL + relativePath;
        backPoster = BASE_URL + backdropPath;
    }

    public Movie(String _id, String title, double ratings,
                 String summary, String date, String localPath){
        id = _id;
        movieTitle = title;
        viewerRatings = ratings;
        overview = summary;
        releasedDate = date;
        posterImage = localPath;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        movieTitle = in.readString();
        viewerRatings = in.readDouble();
        overview = in.readString();
        releasedDate = in.readString();
        posterImage = in.readString();
        backPoster = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public double getViewerRatings() {
        return viewerRatings;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public String getPosterImage() {
        return posterImage;
    }

    public String getBackPoster() {
        return backPoster;
    }

    public String getMovieTitle() {

        return movieTitle;
    }

    public String getId(){
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(movieTitle);
        dest.writeDouble(viewerRatings);
        dest.writeString(overview);
        dest.writeString(releasedDate);
        dest.writeString(posterImage);
        dest.writeString(backPoster);
    }
}
