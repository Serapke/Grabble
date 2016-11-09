package com.grabble.android.mantas;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.google.maps.android.ui.IconGenerator;

import java.util.List;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int LOCATION_REQUEST_INTERVAL = 10 * 1000;     // 10 seconds
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL = 5 * 1000; // 1 second
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float DEFAULT_ZOOM_LEVEL = 20.0f;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastUserLocation;

    private GoogleMap mMap;
    private IconGenerator iconFactory;

    private UiSettings mUiSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "ON CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "ON RESUME");
        super.onResume();
        setUpMap();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "ON PAUSE");
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    public void setUpMap() {
        Log.v(TAG, "SET UP MAP");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();
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
        iconFactory = new IconGenerator(this);

        enableMyLocation();
        disableOnMarkerClickCentering();
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setZoomGesturesEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(false);

        mGoogleApiClient.connect();
        updateLetters();
    }

    private void handleNewLocation(Location location) {
        Log.v(TAG, "HANDLE NEW LOCATION");
        Log.d(TAG, location.toString());

        LatLng lastLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatLng, DEFAULT_ZOOM_LEVEL));
        iconFactory.setStyle(IconGenerator.STYLE_ORANGE);
        mMap.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(makeTextWithColor("YOU", Color.WHITE))))
                        .position(lastLocationLatLng)
                        .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
    }



    @Override
    public void onConnected(Bundle connectionHint) {
        Log.v(TAG, "ON CONNECTED");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "ACCESS_FINE_LOCATION permission not granted!");
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
            return;
        }
        mLastUserLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastUserLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else  {
            handleNewLocation(mLastUserLocation);
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

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        Log.v(TAG, "ENABLE MY LOCATION");
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    /* Disables marker clicks */
    private void disableOnMarkerClickCentering() {
        Log.v(TAG, "DISABLE ON MARKER CLICK CENTERING");
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setZIndex(1.0f);
                return true;
            }
        });
    }

    // Create an instance of GoogleAPIClient
    protected synchronized void buildGoogleApiClient() {
        Log.v(TAG, "BUILD GOOGLE API CLIENT");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.v(TAG, "Google API Client has been built");
        }
    }

    protected void addLettersToMap(List<Placemark> placemarks) {
        Log.v(TAG, "ADD LETTERS TO MAP");
        if (placemarks.isEmpty()) {
            Log.e(TAG, "No markers to add");
        }
        iconFactory.setStyle(IconGenerator.STYLE_BLUE);
        for (Placemark placemark : placemarks) {
            String letterString = Character.toString(placemark.getLetter());
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(makeTextWithColor(letterString, Color.WHITE))))
                    .position(placemark.getCoord())
                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()));
        }
    }

    private void updateLetters() {
        Log.v(TAG, "UPDATE LETTERS");
        FetchPointsTask pointsTask = new FetchPointsTask(this, mMap, iconFactory);
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
}
