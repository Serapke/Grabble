package com.grabble.android.mantas;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Mantas on 19/10/2016.
 */

public class FetchPointsTask extends AsyncTask<Void, Void, List<Placemark>> {

    private final String TAG = FetchPointsTask.class.getSimpleName();
    private final int CONNECT_TIMEOUT_SEC = 15000;

    private MapsActivity mapsActivity;

    public FetchPointsTask(MapsActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    @Override
    protected List<Placemark> doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        LetterMapParser letterMapParser = new LetterMapParser();
        List<Placemark> placemarks = new ArrayList<>();

        String dayOfWeek = getDayOfWeek();

        Log.v(TAG, "Day of the week: " + dayOfWeek);

        try {
            final String POINTS_BASE_URL = "http://www.inf.ed.ac.uk/teaching/courses/selp/coursework/";

            Uri builtUri = Uri.parse(POINTS_BASE_URL).buildUpon()
                    .appendPath(dayOfWeek + ".kml")
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT_SEC);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            placemarks = letterMapParser.parse(inputStream);

            /*for (Placemark placemark : placemarks) {
                Log.v(TAG, placemark.getName() + " " + placemark.getLetter() + " " + placemark.getCoord());
            }*/
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error while closing stream");
                }
            }
        }

        if (!placemarks.isEmpty()) {
            return placemarks;
        }
        return null;
    }

    private String getDayOfWeek() {
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));

        return calendar
                .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
                .toLowerCase();
    }

    @Override
    protected void onPostExecute(List<Placemark> result) {
        if (result != null) {
            mapsActivity.addLettersToMap(result);
        } else {
            Log.e(TAG, "Error. Did not get placemarks!");
        }
    }
}
