package fr.m4103c.movietheater.controller;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.ArrayList;

import fr.m4103c.movietheater.R;
import fr.m4103c.movietheater.activities.ListMoviesActivity;
import fr.m4103c.movietheater.activities.MoreInfoActivity;
import fr.m4103c.movietheater.objectData.Cinema;

/**
 * @author Antoine LE BORGNE
 *
 * add a listener to the movie list view
 */
public class MovieDisplayListener implements AdapterView.OnItemClickListener {

    private boolean open;
    MoreInfoActivity moreInfoActivity;
    private ArrayList<Cinema> moviesList;
    private Activity activity;

    /**
     * default constructor of the listener
     *
     * @param moviesList the list of movies on the application;
     */
    public MovieDisplayListener(ArrayList<Cinema> moviesList, Activity activity) {
        this.moviesList = moviesList;
        this.activity = activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        moreInfoActivity = new MoreInfoActivity(moviesList.get(position), activity, view);
        moreInfoActivity.showPopup();

        //Toast toast = Toast.makeText(activity,moviesList.get(position).getTitle(),Toast.LENGTH_SHORT);
        //toast.show();

    }
}
