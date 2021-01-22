package fr.m4103c.movietheater.objectData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fr.m4103c.movietheater.R;

/**
 * @author Antoine LE BORGNE
 *
 * the item which will be display on the listview
 * of the application
 */
public class CinemaAdapter extends ArrayAdapter<Cinema> {

    private ArrayList<Cinema> movies;
    private Context context;
    private int resource;


    /**
     * default constructor of the cinema adapter
     *
     * @param context the context of the adapter
     * @param resource the resource to put the data
     * @param movies the list of movies objects
     */
    public CinemaAdapter(Context context, int resource, ArrayList<Cinema> movies) {
        super(context, resource, movies);

        this.context = context;
        this.movies = movies;
        this.resource = resource;

    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        //get the different values from the object
        String imageURL = getItem(position).getPosterURL();
        String title = getItem(position).getTitle();
        String director = getItem(position).getDirector();
        String time = getItem(position).getTime();
        String syn = getItem(position).getSynopsis();
        String rating = getItem(position).getRating();
        String date = getItem(position).getReleaseDate();


        Cinema cinema = new Cinema(imageURL,title,director,time,rating,date,syn);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);


        //get the object component of the view
        ImageView posterImg = (ImageView) convertView.findViewById(R.id.movie_poster);
        posterImg.setClipToOutline(true);
        TextView movieTitle = (TextView) convertView.findViewById(R.id.movie_title);
        TextView movieDirector = (TextView) convertView.findViewById(R.id.movie_director);
        TextView movieTime = (TextView) convertView.findViewById(R.id.movie_time);
        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.movie_row_star);


        float ratingValue = Float.parseFloat(cinema.getRating());

        movieTime.setText(cinema.getTime());
        ratingBar.setRating(ratingValue/2);



        //use Glide dependencies to get image from URL faster
        Glide.with(convertView).load(imageURL).into(posterImg);

        //set the
        movieTitle.setText(cinema.getTitle());
        movieDirector.setText(cinema.getDirector().replace("-", "/"));


        return convertView;

    }
}
