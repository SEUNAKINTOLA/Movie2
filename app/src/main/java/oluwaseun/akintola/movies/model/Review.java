package oluwaseun.akintola.movies.model;
/**
 * Created by AKINTOLA OLUWASEUN on 4/25/2017.
 */
public class Review {
    private String review;
    private String author;

    public Review(String user, String comments){
        review = comments;
        author = user;
    }


    public String getReview() {
        return review;
    }

    public String getAuthor() {
        return author;
    }
}
