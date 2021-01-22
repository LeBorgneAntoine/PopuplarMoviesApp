package fr.m4103c.movietheater.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
//from dependency
import com.bumptech.glide.Glide;

import fr.m4103c.movietheater.R;
import fr.m4103c.movietheater.objectData.Cinema;

/**
 * @author Antoine LE BORGNE
 *
 * the activity is used to generate a popup
 * window to show more details on the movie
 */
public class MoreInfoActivity {

    //private variable
    private Activity activity;
    private PopupWindow popupWindow;
    private Cinema movie;
    private View view;
    private View pop;

    //element on the activity
    private TextView title;
    private TextView director;
    private TextView time;
    private TextView synopsis;
    private TextView release;
    private ImageView poster;
    private RatingBar ratingBar;


    /**
     * default constructor of the popup window
     *
     * @param movie    the movie to display
     * @param activity the activity to get context
     * @param view     the view to display it
     */
    public MoreInfoActivity(Cinema movie, Activity activity, View view) {

        this.movie = movie;
        this.view = view;
        this.activity = activity;

        setPopup();
        setData();
    }

    /**
     * set the property of the popup
     */
    public void setPopup() {

        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        pop = layoutInflater.inflate(R.layout.more_info, null);

        //get the screen size in integer
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();

        display.getSize(size);
        int width = size.x;
        int height = size.y;


        //create a popup window with the size of width at 100% and height at 2/3 of the screen height
        popupWindow = new PopupWindow(pop, width, (int) (height / 1.5), true);
        //make full visibility of the background
        popupWindow.setOnDismissListener(() -> dimBackground(1f));
        //make the background darker
        dimBackground(0.3f);
        //put an soft animation ("popup_close" and "popup_open")
        popupWindow.setAnimationStyle(R.style.Animation);

    }

    /**
     * modify the opacity of the background
     *
     * @param bg the level of visibility (0 = completely black / 1 = fully visible)
     */
    public void dimBackground(Float bg) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bg;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

    /**
     * show the popup on the bottom of the screen
     */
    public void showPopup() {
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * set the data to display on the popup layout
     * by the movie passed in the constructor parameter
     */
    public void setData() {

        title = (TextView) pop.findViewById(R.id.more_info_title);
        director = (TextView) pop.findViewById(R.id.more_info_director);
        time = (TextView) pop.findViewById(R.id.more_info_time);
        synopsis = (TextView) pop.findViewById(R.id.more_info_synopsis);
        poster = (ImageView) pop.findViewById(R.id.more_poster_image);
        poster.setClipToOutline(true);
        ratingBar = (RatingBar) pop.findViewById(R.id.ratingBar);
        release = (TextView) pop.findViewById(R.id.more_info_release);


        title.setText(movie.getTitle());
        release.setText(movie.getReleaseDate());
        director.setText(movie.getDirector());
        time.setText(movie.getTime());
        float rating = Float.parseFloat(movie.getRating());
        ratingBar.setRating(rating/2);
        synopsis.setText(movie.getSynopsis());

        //render the poster URL by the Glide dependency
        Glide.with(pop.getContext()).load(movie.getPosterURL()).into(poster);

    }

}
