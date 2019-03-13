package com.surya.onspot.locationprovider;


import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

public class LocationTask extends AsyncTask<Void, Void, Location> {

    private AfterGettingLocation mListener;
    private Location loca;

    public LocationTask() {
        // TODO Auto-generated constructor stub
    }

    public LocationTask(Context context, AfterGettingLocation mListener) {
        this.mListener = mListener;

    }

    @Override
    protected void onPreExecute() {


        super.onPreExecute();
    }


    @Override
    protected Location doInBackground(Void... params) {


        return loca;
    }

    @Override
    protected void onPostExecute(Location result) {

        mListener.afterLocation(result);

        super.onPostExecute(result);


    }


    public interface AfterGettingLocation {
        void afterLocation(Location result);
    }


}
