package oluwaseun.akintola.movies.model;
/**
 * Created by AKINTOLA OLUWASEUN on 4/25/2017.
 */
public class Trailer {
    private String name;
    private String key;
    private int size;
    public Trailer(String name, String key, int size) {
        this.name = name;
        this.key = key;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public int getSize() {
        return size;
    }
}
