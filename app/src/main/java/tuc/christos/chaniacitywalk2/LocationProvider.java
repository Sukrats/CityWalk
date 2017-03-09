package tuc.christos.chaniacitywalk2;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;

import java.text.DateFormat;
import java.util.Date;

public class LocationProvider implements ConnectionCallbacks, OnConnectionFailedListener, LocationSource, LocationListener  {

    private static final String TAG = "LocationProvider";

    private GoogleApiClient mGoogleApiClient;

    private OnLocationChangedListener mListener;

    private LocationRequest mLocationRequest = new LocationRequest();

    private Location mCurrentLocation;

    private String mLastUpdateTime;

    private boolean mRequestingLocationUpdates = true;

    /**
     * LOCATION REQUEST VARIABLES
     * **/
    private void createLocationRequest() {
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     *
     * GOOGLE API CLIENT INITIALIZATION
     */
    private synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**             GOOGLE API CLIENT CALLBACKS
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        Log.i(TAG, "CONNECTION SUCCESSFUL");
        if(mRequestingLocationUpdates)
            startLocationUpdates();

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }



    /**
     * LOCATION UPDATES LIFECYCLE
     */

    private void startLocationUpdates(){
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        }catch(SecurityException e){
            Log.i(TAG, "PERMISSION EXCEPTION :" + e.getMessage());
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Location From Google Play Services API
     * @param location
     */
    @Override
    public void onLocationChanged(Location location){
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


        //Update Location for the provider
        mListener.onLocationChanged(location);
    }


    @Override
    public void activate(OnLocationChangedListener listener) {

        mListener = listener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    public void initiateLocationProvider(Context context){
        buildGoogleApiClient(context);
        createLocationRequest();
        mGoogleApiClient.connect();
    }
    public void stopLocationProvider(){
        mGoogleApiClient.disconnect();
        mListener = null;
    }

    /**
     * ACTIVITY LIFECYCLE CONTROLS
     */

   /* @Override
    public void onPause(){
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createLocationRequest();
        buildGoogleApiClient();

        //defineLocationSettings();

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }*/
}
