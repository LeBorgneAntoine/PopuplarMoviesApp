package fr.m4103c.movietheater.objectData;

/**
 * @author Antoine LE BORGNE
 *
 * class object of a movie which will be render
 * in the application as a row in a list view
 */
public class Cinema {

    //private variables
    private String posterURL;
    private String title;
    private String director;
    private String time;


    //do to more ;)
    private String synopsis;
    private String rating;
    private String releaseDate;

    /**
     * default constructor of the cinema
     *
     * @param posterURL the internet URL to the poster
     * @param title the title of the movie
     * @param director the director of the movie
     * @param time the duration of the movie
     * @param rating the rating value of the movie(between 0 and 10)
     * @param releaseDate the realise date
     * @param synopsis a short synopsis of the movie
     */
    public Cinema(String posterURL, String title, String director, String time, String rating, String releaseDate, String synopsis) {
        this.posterURL = posterURL;
        this.title = title;
        this.director = director;
        this.time = time;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Cinema{" +
                "posterURL='" + posterURL + '\'' +
                ", title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", time='" + time + '\'' +
                ", synopsis='" + synopsis + '\'' +
                ", rating='" + rating + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
