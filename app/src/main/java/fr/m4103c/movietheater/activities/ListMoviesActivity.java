package fr.m4103c.movietheater.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import fr.m4103c.movietheater.R;
import fr.m4103c.movietheater.controller.MovieDisplayListener;
import fr.m4103c.movietheater.objectData.Cinema;
import fr.m4103c.movietheater.objectData.CinemaAdapter;

/**
 * @author Antoine LE BORGNE
 *
 * the activity consist to fill the movie list
 * and add the rendered rows in the list view.
 * this avtivity is link to "activity_list_movies.xml"
 */
public class ListMoviesActivity extends AppCompatActivity {

    //private variable
    private ArrayList<Cinema> movies;
    private ListView list_movie;
    private CinemaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_movies);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //hide the default actionBar to set the custom one
        getSupportActionBar().hide();


        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
                pullToRefresh.setRefreshing(false);
            }
        });

        updateData();


    }

    private void updateData(){
        if(userIsConnectedToInternet()) {
            this.movies = new ArrayList<Cinema>();
            //fillMovies();//add movies by hands
            new JsonTask().execute("https://api.themoviedb.org/3/movie/popular?api_key=c9b41a98f92fef4c87df33dd25b275b2");//add movies by internet API

            list_movie = (ListView) findViewById(R.id.list_movies);

            //add listener
            list_movie.setOnItemClickListener(new MovieDisplayListener(movies, this));
        }else{
            Toast.makeText(this,"You need to be connected to wifi",Toast.LENGTH_LONG).show();
        }
    }


    private boolean userIsConnectedToInternet(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * @author Antoine LE BORGNE
     *
     * make an async task to load the data of the movies
     * from the API "TMDB"
     * unfortunalty there is no director and duration information
     *
     * resourse help:
     * -https://www.themoviedb.org/;(the api website)
     * -https://www.youtube.com/watch?v=scJy6gA230s&t=260s;
     * -https://stackoverflow.com/questions/33229869/get-json-data-from-url-using-android;
     *
     */
    private class JsonTask extends AsyncTask<String, String, String> {




        protected void onPreExecute() { super.onPreExecute(); }

        /**
         * specified the task to do in the background
         *
         * @param params the url to pass
         * @return the String generated from the request
         */
        protected String doInBackground(String... params) {

            return requestFromURL(params[0]);

        }


        /**
         * make an http request to an API
         *
         * @param requestHttp the http request in string format
         * @return the response string
         */
        private String requestFromURL(String requestHttp){

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                //get the url from a string
                URL url = new URL(requestHttp);

                //open the connection to the api and connect to it
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                //create a buffer to make an string from the result request
                StringBuilder buffer = new StringBuilder();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //and finally disconnect and close every connection
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                //create a Json object from the string
                JSONObject jsonObject = new JSONObject(result);
                //get the array of the "results"
                JSONArray jsonArray = jsonObject.getJSONArray("results");


                for(int i = 0; i<jsonArray.length();i++){

                    //get the json data of one movie
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    //make a request of the details (to get the runtime)
                    String detailsOfTheMovie = requestFromURL("https://api.themoviedb.org/3/movie/"+jsonObject1.getString("id")+"?api_key=c9b41a98f92fef4c87df33dd25b275b2");

                    //request to get the cast and crew (looking for director)
                    String detailsCast = requestFromURL("https://api.themoviedb.org/3/movie/"+jsonObject1.getString("id")+"/credits?api_key=c9b41a98f92fef4c87df33dd25b275b2");

                    //parse to Json
                    JSONObject details = new JSONObject(detailsOfTheMovie);
                    JSONObject cast = new JSONObject(detailsCast);




                    String director = "";

                    //get only the crew
                    JSONArray crews = cast.getJSONArray("crew");


                   //looking for the director
                    for(int j = 0; j<crews.length(); j++){
                        if(crews.getJSONObject(j).getString("job").equals("Director")){
                            director = "Director: "+crews.getJSONObject(j).getString("original_name");
                            break;
                        }
                    }


                    //get the all data together
                    String posterURL = "https://image.tmdb.org/t/p/w500"+ jsonObject1.getString("poster_path");
                    String title =  jsonObject1.getString("original_title");
                    String duration = "Duration: " + this.toHoursFormat(details.getString("runtime"));
                    String rating = jsonObject1.getString("vote_average");
                    String release ="Release: "+ details.getString("release_date").replace("-","/");
                    String synopsis = jsonObject1.getString("overview");



                    /*
                    //debug purpose
                    System.out.println("poster url: "+posterURL );
                    System.out.println("title: "+title );
                    System.out.println("duration: "+duration );
                    System.out.println("rating: "+rating );
                    System.out.println("release: "+release );
                    System.out.println("syn: "+synopsis );
                    */

                    //add the movie to the list to display it
                    movies.add(new Cinema(posterURL, title, director, duration, rating, release, synopsis));

                }

                //because it's async we have to wait until we get the response to render it
                adapter = new CinemaAdapter(ListMoviesActivity.this, R.layout.movie_row, movies);
                list_movie.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        /**
         * set the format minute string to hours string
         * (ex: 120 -> 2h00)
         * @param runtime the string value of the runtime
         * @return the formatted runtime
         */
        private String toHoursFormat(String runtime){

            int integerValue = Integer.parseInt(runtime);


            if(integerValue == 0){
                return "no information";
            }

            if(integerValue < 60){
                return ""+integerValue+" min";
            }

            int hours = integerValue / 60;
            int minutes = integerValue % 60;

            if(minutes < 10){
                return hours+"h0"+minutes;
            }else{
                return hours+"h"+minutes;
            }


        }
    }

    /*
     * fill the array of movies by hand
     * to be on the rail of the specification
     * (director and duration added)*
     */
    /*
    private void fillMovies() {


        movies.add(new Cinema(
                "https://images-na.ssl-images-amazon.com/images/I/71HyTegC0SL._AC_SY879_.jpg",
                "Avengers : endgame",
                "By Russo's brothers",
                "3h30",
                "The Titan Thanos, having succeeded in appropriating the six Infinity Stones and bringing them together on the Golden Gauntlet, was able to achieve his goal of pulverizing half the population of the Universe. Five years later, Scott Lang, aka Ant-Man, manages to escape the subatomic dimension where he was trapped. He offers the Avengers a solution to bring back to life all the missing beings, including their allies and teammates: recover the Infinity Stones in the past."
        ));

        movies.add(new Cinema(
                "https://images-na.ssl-images-amazon.com/images/I/51p4M9-VzPL._AC_.jpg",
                "Green Mile",
                "By Paul Greengrass",
                "3h09",
                "Paul Edgecomb, a hundred-year-old resident of a retirement home, is haunted by his memories. Head warden of Cold Mountain Penitentiary in 1935, Louisiana, he was responsible for ensuring the smooth running of executions in Block E (the green line) by striving to soften the last moments of the condemned. Among them was a colossus named John Coffey, accused of the rape and murder of two young girls."
        ));

        movies.add(new Cinema(
                "https://lumiere-a.akamaihd.net/v1/images/image_ef98a49a.jpeg",
                "Madame Doubtfire",
                "By Chris Columbus",
                "2h05",
                "Daniel, an unemployed cartoon voice actor, has no authority over his three children, Lydia, Chris and Natalie. After fourteen years together, his wife Miranda, an ambitious decorator, files for divorce and obtains custody of the children. In order to continue to see them, Daniel poses as Mrs. Doubtfire, a respectable Irish housekeeper, and enters the service of his ex-wife."
        ));


        movies.add(new Cinema(
                "https://upload.wikimedia.org/wikipedia/en/2/29/Movie_poster_for_%22Scary_Movie%22.jpg",
                "Scary movie",
                "By David Zucker",
                "1h28",
                "One evening, Drew Becker receives an anonymous call from a maniac. Hunted in her house, then in her garden, she ends up being killed. His death plunges his high school friends into a nightmare, especially since they must now face a serial killer, hidden among them. Sniffing the scoop, journalist Gail Hailstorn arrives in town, determined to harass Cindy Campbell and her friends about this story."
        ));

        movies.add(new Cinema(
                "https://images-na.ssl-images-amazon.com/images/I/51O-Xu4MkrL._AC_SY445_.jpg",
                "The shining",
                "By Stanley Kubrick",
                "2h26",
                "Jack Torrance, caretaker of a hotel closed in winter, his wife and son Danny are preparing to live long months of solitude. Danny, who has a gift of medium, the Shining, is scared at the idea of \u200B\u200Bliving in this place, a theater marked by terrible past events ..."
        ));


    }
    */


}