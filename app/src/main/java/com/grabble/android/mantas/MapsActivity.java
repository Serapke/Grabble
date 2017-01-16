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
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.grabble.android.mantas.data.GrabbleDbHelper;

import com.grabble.android.mantas.data.GrabbleContract.BagEntry;

import java.util.ArrayList;
import java.util.List;

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

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastUserLocation;
    private Marker userLocationMarker;

    private GoogleMap mMap;
    private IconGenerator iconFactory;
    private UiSettings mUiSettings;

    private List<Placemark> placemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "ON CREATE");
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
        Log.v(TAG, "ON START");
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "ON STOP");
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        Log.v(TAG, "ON MAP READY");
        mMap = googleMap;

        setOnMarkerClickCollectLetter();
        setMapUiSettings();
        loadLetters();
    }

    private void setMapUiSettings() {
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setZoomGesturesEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(false);
    }

    private void handleNewLocation(Location location) {
        if (location == null) return;

        LatLng lastLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatLng, DEFAULT_ZOOM_LEVEL));
        iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
        // remove the old user location marker, if exists
        if (userLocationMarker != null) {
            userLocationMarker.remove();
        }
        userLocationMarker = mMap.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(makeTextWithColor("YOU", Color.WHITE))))
                        .position(lastLocationLatLng)
                        .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "Connected to GoogleApiClient");

        if (mLastUserLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        android.Manifest.permission.ACCESS_FINE_LOCATION, true);
                return;
            }
            mLastUserLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            handleNewLocation(mLastUserLocation);
        }
        startLocationUpdates();
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
            mGoogleApiClient.reconnect();
        }
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

    private void setOnMarkerClickCollectLetter() {
        Log.v(TAG, "DISABLE ON MARKER CLICK CENTERING");
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Check if the marker clicked is not the user location marker
                if (marker.getId().equals(userLocationMarker.getId()))
                    return false;

                double distance = SphericalUtil.computeDistanceBetween(
                        marker.getPosition(),
                        userLocationMarker.getPosition());
                Log.d(TAG, "User tried to collect a letter from " + distance + " meter distance");
                /*
                 * If the distance between user location and marker location is
                 * less than or equal to 15 meters let user pick up the letter
                 */
                if (distance <= 15) {
                    collectLetter(marker);
                }
                return true;
            }
        });
    }

    private void collectLetter(Marker marker) {
        GrabbleDbHelper dbHelper = new GrabbleDbHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Log.v(TAG,
                "Picked: " + marker.getTitle() +
                ", (" + marker.getPosition().latitude + ", " +
                marker.getPosition().longitude + ")");

        ContentValues values = new ContentValues();
        values.put(BagEntry.COLUMN_NAME_LETTER, marker.getTitle());
        values.put(BagEntry.COLUMN_NAME_LATITUDE, marker.getPosition().latitude);
        values.put(BagEntry.COLUMN_NAME_LONGITUDE, marker.getPosition().longitude);
        long _id = db.insert(BagEntry.TABLE_NAME, null, values);

        db.close();

        if ( _id > 0) {
            marker.remove();
            Toast.makeText(this, "You picked letter " + marker.getTitle(), Toast.LENGTH_SHORT).show();
        }
        // if for some reason couldn't save the letter to database, inform the user about the error
        else
            Toast.makeText(this, "Error! Cannot pick the letter", Toast.LENGTH_LONG).show();
    }

    // Create an instance of GoogleAPIClient
    protected synchronized void buildGoogleApiClient() {
        Log.v(TAG, "BUILD GOOGLE API CLIENT");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
    }

    protected void addLettersToMap(List<Placemark> placemarks) {
        this.placemarks = placemarks;
        Log.v(TAG, "ADD LETTERS TO MAP");

        List<LatLng> collectedLettersLocations = getLettersCollectedToday();

        // Paint letter markers blue
        iconFactory.setStyle(IconGenerator.STYLE_BLUE);

        for (Placemark placemark : this.placemarks) {
            if (isAlreadyCollected(placemark, collectedLettersLocations)) continue;

            String letterString = Character.toString(placemark.getLetter());
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(makeTextWithColor(letterString, Color.WHITE))))
                    .title(letterString)
                    .position(placemark.getCoord())
                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
        }
    }

    /* Checks if the letter at given placemark location has not been already collected today */
    private boolean isAlreadyCollected(Placemark p, List<LatLng> collectedLettersLocations) {
        for (LatLng collectedLetterLocation : collectedLettersLocations) {
            double distance = SphericalUtil.computeDistanceBetween(
                    collectedLetterLocation,
                    p.getCoord());
            if (distance == 0) {
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
        Log.v(TAG, "Load letters");
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
        Log.v(TAG, "ON LOCATION CHANGED");
        mLastUserLocation = location;
        handleNewLocation(mLastUserLocation);
    }

    public void onBagButtonClick(View view) {
        Log.v(TAG, "OPEN BAG");
        Intent intent = new Intent(this, BagActivity.class);
        startActivity(intent);
    }

    public void onLeaderboardButtonClick(View view) {
        Log.v(TAG, "OPEN LEADERBOARD");
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    public void onUserinfoButtonClick(View view) {
        Log.v(TAG, "OPEN USERINFO");
        Intent intent = new Intent(this, UserInfoActivity.class);
        startActivity(intent);
    }
}
