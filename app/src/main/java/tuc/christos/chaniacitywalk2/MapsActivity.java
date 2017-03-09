package tuc.christos.chaniacitywalk2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tuc.christos.chaniacitywalk2.model.Poi;
import tuc.christos.chaniacitywalk2.model.Scene;


public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback {

    private final String KEY_CAM_TILT = "camT";
    private final String KEY_CAM_BEAR = "camB";
    private final String KEY_CAM_LAT = "camLa";
    private final String KEY_CAM_LONG = "camLo";
    private final String KEY_CAM_ZOOM = "camZ";

    private GoogleMap mMap;
    private UiSettings mUiSettings;

    private boolean flag = false;
    private boolean lflag = false;
    private boolean rflag = false;
    private boolean polyFlag = false;


    protected static final String TAG = "Google Client";

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest = new LocationRequest();
    protected Location mCurrentLocation;
    protected String mLastUpdateTime;
    protected boolean mRequestingLocationUpdates = true;

    private ArrayList<Scene> Route = new ArrayList<>();
    private Location lastKnownLocation;
    private Map<Scene, Marker> sceneToMarkerMap = new HashMap();
    private Map<Marker, Scene> markerToSceneMap = new HashMap();
    private Map<Polyline, Scene> lineToSceneMap = new HashMap();
    private Map<Scene, Polyline> sceneToLineMap = new HashMap();

    private String[] tags = {"Byzantine", "Ottoman", "Venetian", "Route"};


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static final float DEFAULT_MIN_ZOOM = 15.0f;

    private static final LatLngBounds CHANIA = new LatLngBounds(
            new LatLng(35.515544, 24.018199), new LatLng(35.517623, 24.022168));

    private CameraPosition currentCameraPosition;
    private double camLat = 35.514388, camLong = 24.020335;
    private float camZoom = 17.0f, camBearing = 0, camTilt = 50;

    private static final CameraPosition CHANIA_CAMERA = new CameraPosition.Builder()
            .target(new LatLng(35.514388, 24.020335)).zoom(17.0f).bearing(0).tilt(50).build();

    private dataManager dM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (savedInstanceState != null) {
            camLat = (double) savedInstanceState.get(KEY_CAM_LAT);
            camLong = (double) savedInstanceState.get(KEY_CAM_LONG);
            camBearing = (float) savedInstanceState.get(KEY_CAM_BEAR);
            camTilt = (float) savedInstanceState.get(KEY_CAM_TILT);
            camZoom = (float) savedInstanceState.get(KEY_CAM_ZOOM);
        }

        setCameraPosition();

        buildGoogleApiClient();
        createLocationRequest();
        dM = dataManager.getInstance(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;
        //mMap.setPadding(0,0,0,80);
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.normal_map_json));

            if (!success) {
                Log.i(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.i(TAG, "Can't find style. Error: ", e);
        }

        //mMap.setOnCameraMoveCanceledListener(this);
        mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // Since we return true, we have to show the info window manually
                marker.showInfoWindow();
                Scene scene = markerToSceneMap.get(marker);
                if (scene.isHasAR()) {
                    Polyline line = sceneToLineMap.get(scene);
                    if (line.getColor() == Color.BLUE) {
                        line.setColor(Color.GREEN);
                    } else if (line.getColor() == Color.GREEN) {
                        line.setColor(Color.BLUE);
                    }
                }
                // We have handled the click, so don't centre again and return true
                return true;
            }

        });

        setupMapOptions();
        drawMap();
        enableMyLocation();

    }

    public void setupMapOptions() {
    }

    /**
     * INITIALISE MAP STATE BASED ON USER PROGRESS
     */
    private void drawMap() {
        mMap.clear();
        sceneToMarkerMap.clear();
        markerToSceneMap.clear();
        lineToSceneMap.clear();
        sceneToLineMap.clear();

        for (Scene temp : dM.getScenes()) {

            if (temp.isVisible() && !temp.isHasAR()) {
                LatLng pos = new LatLng(temp.getLatitude(), temp.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(temp.getName())
                        .snippet(temp.getTAG()));

                if(temp.isVisited()){

                    if (temp.getTAG() != null && temp.getTAG().equals("Ottoman")) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_otto));
                    } else if (temp.getTAG() != null && temp.getTAG().equals("Venetian")) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_venetian));
                    } else if (temp.getTAG() != null && temp.getTAG().equals("Modern")) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_modern));
                    } else if (temp.getTAG() != null && temp.getTAG().equals("NeoGreek")) {
                        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_ruins));
                    } else {
                        marker = mMap.addMarker(new MarkerOptions().position(pos).title(temp.getName()));
                    }

                }else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.unknown_32));
                }

                sceneToMarkerMap.put(temp, marker);
                markerToSceneMap.put(marker, temp);
            }else if (temp.isHasAR()){
                Marker marker;

                LatLng pos = new LatLng(temp.getLatitude(), temp.getLongitude());
                marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(temp.getName())
                        .snippet(temp.getTAG()));

                if (temp.getId() == 1)
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.giali_thumb)));
                else if (temp.getId() == 2)
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.byzantine_thumb)));
                else if (temp.getId() == 3)
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.kasteli_thumb)));
                else if (temp.getId() == 4)
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.rocco_thumb)));
                else
                    marker = null;


                if(marker != null){
                    sceneToMarkerMap.put(temp, marker);
                    markerToSceneMap.put(marker, temp);
                }

                Polyline line;
                PolylineOptions options = new PolylineOptions().width(10).color(Color.BLUE).geodesic(true);

                for(LatLng ll: dM.getPolyPoints(temp)){
                    options.add(ll);
                }
                /*line = mMap.addPolyline(options);
                lineToSceneMap.put(line, temp);
                sceneToLineMap.put(temp, line);*/
            }
        }

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentCameraPosition));
        //mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentCameraPosition));
        //mMap.setMinZoomPreference(DEFAULT_MIN_ZOOM);
        //mMap.setLatLngBoundsForCameraTarget(CHANIA);

    }

    public void onMiddleBtnClick(View view) {
        /*for(Scene temp: dM.getScenes()){
            if(temp.getTAG()!=null && temp.getTAG().equals("Venetian") && !flag) {
                temp.setVisible(false);
                flag = true;
            }
        }*/
        getCameraPosition();
        setCameraPosition();
        if (flag) {
            dM.setAllInvisible();
            flag = false;
        } else {
            dM.setAllvisible();
            flag = true;
        }
        drawMap();
    }

    public void onLeftBtnClick(View view) {

        getCameraPosition();
        setCameraPosition();
        if (lflag) {
            dM.setAllvisited();
            lflag = false;
        } else {
            dM.unsetAllvisited();
            lflag = true;
        }
        drawMap();
    }

    public void onRightBtnClick(View view) {

        getCameraPosition();
        setCameraPosition();
        dM.setRandVisited();
        drawMap();
    }

    public void showRel(View view){
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.button_pad);
        if(rflag) {
            rl.setVisibility(View.VISIBLE);
            rflag = false;
        }else {
            rl.setVisibility(View.INVISIBLE);
            rflag = true;
        }
    }

    public void getCameraPosition() {
        CameraPosition cm = mMap.getCameraPosition();
        LatLng trg = cm.target;

        camBearing = cm.bearing;
        camTilt = cm.tilt;
        camZoom = cm.zoom;
        camLat = trg.latitude;
        camLong = trg.longitude;
    }

    public void setCameraPosition() {

        currentCameraPosition = new CameraPosition.Builder()
                .target(new LatLng(camLat, camLong)).zoom(camZoom).bearing(camBearing).tilt(camTilt).build();

    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {

            mMap.setMyLocationEnabled(true);
            mUiSettings.setMyLocationButtonEnabled(true);
            mUiSettings.setMapToolbarEnabled(true);
        }
    }

    /**
     * LOCATION REQUEST VARIABLES
     **/
    protected void createLocationRequest() {
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * GOOGLE API CLIENT INITIALIZATION
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onPause() {
        super.onPause();
        Bundle b;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        getCameraPosition();

        savedInstanceState.putFloat(KEY_CAM_BEAR, camBearing);
        savedInstanceState.putFloat(KEY_CAM_TILT, camTilt);
        savedInstanceState.putDouble(KEY_CAM_LAT, camLat);
        savedInstanceState.putDouble(KEY_CAM_LONG, camLong);
        savedInstanceState.putFloat(KEY_CAM_ZOOM, camZoom);
    }

    /**
     * GOOGLE API CLIENT CALLBACKS
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        Log.i(TAG, "CONNECTION SUCCESSFUL");
        if (mRequestingLocationUpdates)
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

    public void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);

        } catch (SecurityException e) {
            Log.i(TAG, "PERMISSION EXCEPTION :" + e.getMessage());
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Location From Google Play Services API
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

            View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
            ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
            markerImageView.setImageResource(resId);
            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null)
                drawable.draw(canvas);
            customMarkerView.draw(canvas);
            return returnedBitmap;
        }



    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter{

        private final View mContents;

        CustomInfoWindowAdapter(){
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents,null);
        }

        @Override
        public View getInfoWindow(Marker marker) {

            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            Scene scene = markerToSceneMap.get(marker);
            if (scene.getId() == 1)
                ((ImageView) mContents.findViewById(R.id.badge)).setImageResource(R.drawable.giali_thumb);
            else if (scene.getId() == 2)
                ((ImageView) mContents.findViewById(R.id.badge)).setImageResource(R.drawable.byzantine_thumb);
            else if (scene.getId() == 3)
                ((ImageView) mContents.findViewById(R.id.badge)).setImageResource(R.drawable.kasteli_thumb);
            else if (scene.getId() == 4)
                ((ImageView) mContents.findViewById(R.id.badge)).setImageResource(R.drawable.rocco_thumb);
            else
                ((ImageView) mContents.findViewById(R.id.badge)).setImageResource(0);

            TextView titleUi = ((TextView) mContents.findViewById(R.id.title));
            titleUi.setText(marker.getTitle());

            TextView snippetUi = ((TextView) mContents.findViewById(R.id.snippet));
            snippetUi.setText(marker.getSnippet());

            return mContents;
        }
    }

}
