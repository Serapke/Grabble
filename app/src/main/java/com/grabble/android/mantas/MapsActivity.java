package com.grabble.android.mantas;

import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.grabble.android.mantas.data.GrabbleDbHelper;

import com.grabble.android.mantas.data.GrabbleContract.BagEntry;
import com.grabble.android.mantas.utils.AchievementsUtil;
import com.grabble.android.mantas.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *  Shows the map with today's letters and provides functionality for
 *  collecting letters.
 */
public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int LOCATION_REQUEST_INTERVAL = 5 * 1000;     // 5 seconds
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL = 5 * 1000; // 5 second
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM_LEVEL = 20.0f;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location mLastUserLocation;
    private Marker userLocationMarker;

    private GoogleMap map;
    private IconGenerator iconFactory;

    private List<Placemark> placemarks;

    GrabbleDbHelper dbHelper;
    SQLiteDatabase db;

    private static AchievementsUtil achv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
        iconFactory = new IconGenerator(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        dbHelper = new GrabbleDbHelper(this);
        db = dbHelper.getWritableDatabase();
        achv = new AchievementsUtil(this, db);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
        db.close();
    }

    // Create an instance of GoogleAPIClient
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setOnMarkerClickCollectLetter();
        setMapUiSettings();
        loadLetters();
    }

    /**
     *  Disables scroll and zoom gestures and 'My Location' button
     */
    private void setMapUiSettings() {
        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setScrollGesturesEnabled(false);
        uiSettings.setZoomGesturesEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
    }

    /**
     *  Reconnects GoogleApiClient until user gives permission to ACCESS_FINE_LOCATION
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && requestCode == LOCATION_PERMISSION_REQUEST_CODE
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED
                || grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            googleApiClient.reconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mLastUserLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION, true);
                return;
            }
            mLastUserLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            handleNewLocation(mLastUserLocation);
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     *  Updates user location marker on the map
     */
    private void handleNewLocation(Location location) {
        if (location == null) return;

        LatLng lastLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatLng, DEFAULT_ZOOM_LEVEL));
        iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
        // remove the old user location marker, if exists
        if (userLocationMarker != null) {
            userLocationMarker.remove();
        }
        userLocationMarker = map.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(makeTextWithColor("YOU", Color.WHITE))))
                        .position(lastLocationLatLng)
                        .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
    }

    private void setOnMarkerClickCollectLetter() {
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Check if the marker clicked is not the user location marker
                if (marker.getId().equals(userLocationMarker.getId()))
                    return false;

            /*
             * If the marker is visible on the screen, user can collect the corresponding
             * letter.
             */
                LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                if (bounds.contains(marker.getPosition())) {
                    collectLetter(marker);
                } else {
                    Toast.makeText(MapsActivity.this, "You are too far from the letter!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    /**
     *  Save the collected letter and the metadata (position, timestamp) to the bag table
     */
    private void collectLetter(Marker marker) {
        Log.v(TAG,
                "Picked: " + marker.getTitle() +
                ", (" + marker.getPosition().latitude + ", " +
                marker.getPosition().longitude + ")");

        ContentValues values = new ContentValues();
        values.put(BagEntry.COLUMN_NAME_LETTER, marker.getTitle());
        values.put(BagEntry.COLUMN_NAME_LATITUDE, marker.getPosition().latitude);
        values.put(BagEntry.COLUMN_NAME_LONGITUDE, marker.getPosition().longitude);
        long _id = db.insert(BagEntry.TABLE_NAME, null, values);

        achv.checkLetterAchievements(marker.getPosition());

        if ( _id > 0) {
            marker.remove();
            Toast.makeText(this, "You picked letter " + marker.getTitle(), Toast.LENGTH_SHORT).show();
        }
        // if for some reason couldn't save the letter to database, inform the user about the error
        else
            Toast.makeText(this, "Error! Cannot pick the letter ", Toast.LENGTH_LONG).show();
    }

    /**
     *  A callback function from FetchPointsTask, which populates map with
     *  retrieved letter markers.
     */
    protected void addLettersToMap(List<Placemark> placemarks) {
        Log.v(TAG, "Adding letters to map...");
        this.placemarks = placemarks;

        List<LatLng> collectedLettersLocations = getLettersCollectedToday();

        // Paint letter markers blue
        iconFactory.setStyle(IconGenerator.STYLE_BLUE);

        for (Placemark placemark : this.placemarks) {
            if (isAlreadyCollected(placemark, collectedLettersLocations)) continue;

            String letterString = Character.toString(placemark.getLetter());
            map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(makeTextWithColor(letterString, Color.WHITE))))
                    .title(letterString)
                    .position(placemark.getCoord())
                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
        }
    }

    /* Checks if the letter at given placemark location has not been already collected today */
    protected boolean isAlreadyCollected(Placemark p, List<LatLng> collectedLettersLocations) {
        if (collectedLettersLocations == null) return false;
        for (LatLng collectedLetterLocation : collectedLettersLocations) {
            double distance = SphericalUtil.computeDistanceBetween(
                    collectedLetterLocation,
                    p.getCoord());
            if (distance == 0.00) {
                collectedLettersLocations.remove(collectedLetterLocation);
                return true;
            }
        }
        return false;
    }

    private List<LatLng> getLettersCollectedToday() {
        List<LatLng> locations = new ArrayList<>();

        GrabbleDbHelper dbHelper = new GrabbleDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.getLettersCollectedToday(db);
        Log.d(TAG, "Collected " + cursor.getCount() + " letters today.");

        while (cursor.moveToNext()) {
            double latitude = cursor.getDouble(cursor.getColumnIndex(BagEntry.COLUMN_NAME_LATITUDE));
            double longitude = cursor.getDouble(cursor.getColumnIndex(BagEntry.COLUMN_NAME_LONGITUDE));
            String timestamp = cursor.getString(cursor.getColumnIndex(BagEntry.COLUMN_NAME_COLLECTION_DATE));
            locations.add(new LatLng(latitude, longitude));
            Log.v(TAG, "Collected letter today at: (" + latitude + ", " + longitude + ") on " + timestamp);
        }
        cursor.close();
        db.close();

        return locations;
    }

    private void loadLetters() {
        Log.v(TAG, "Started loading letters...");
        // if we already have letters, then just skip
        if (this.placemarks != null) return;
        FetchPointsTask pointsTask = new FetchPointsTask(this);
        pointsTask.execute();
    }

    private String makeTextWithColor(String text, int color) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);
        ssb.setSpan(new ForegroundColorSpan(color), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return ssb.toString();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastUserLocation = location;
        handleNewLocation(mLastUserLocation);
    }

    public void onBagButtonClick(View view) {
        Intent intent = new Intent(this, BagActivity.class);
        startActivity(intent);
    }

    public void onLeaderboardButtonClick(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    public void onUserInfoButtonClick(View view) {
        Intent intent = new Intent(this, UserInfoActivity.class);
        startActivity(intent);
    }
}
