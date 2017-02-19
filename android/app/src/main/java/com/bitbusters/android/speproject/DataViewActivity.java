package com.bitbusters.android.speproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;

public class DataViewActivity extends FragmentActivity implements OnTaskCompleted, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String BITMAP_TAG = "BITMAP";
    private GoogleMap mMap;
    private FloatingActionButton mCamButton;
    private FloatingActionButton mSPVButton;

    //variables used for displaying current location
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private boolean connected;
    private Marker currentLocationMarker;
    private FragmentManager fm;
    private SPDataFragment mSPDataFragment;
    private List<SamplingPoint> mSamplePoints = new ArrayList<>();
    private Circle mRadiusCircle;
    private List<PicturePoint> photoMarkers = new ArrayList<>();
    private ClusterManager<SamplingPoint> mSampleClusterManager;
    private ClusterManager<PicturePoint> mPictureClusterManager;
    private MultiListener ml = new MultiListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // The action performed when the sample point view button is pressed.
        mSPVButton = (FloatingActionButton) findViewById(R.id.sp_view_button);
        mSPVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get sample point data from API.
                mSampleClusterManager.clearItems();
                LatLng camCentre = mMap.getCameraPosition().target;
                String[] location = {String.valueOf(camCentre.latitude), String.valueOf(camCentre.longitude)};
                new SamplingPointsAPI(DataViewActivity.this).execute(location);

                // Add a radius circle around sample point query area.
                if (mRadiusCircle != null) {
                    mRadiusCircle.remove();
                }
                mRadiusCircle = mMap.addCircle(new CircleOptions()
                        .center(camCentre)
                        .radius(14142) // i.e. hypotenuse of 10km x 10km triangle.
                        .strokeColor(0x661854E1)
                        .fillColor(0x331854E1));
            }
        });

        // The action performed when the camera button is pressed.
        mCamButton = (FloatingActionButton) findViewById(R.id.cam_button);
        mCamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pcaintent = new Intent(v.getContext(), PhotoCommentActivity.class);
                startActivity(pcaintent);
            }
        });

        // Create an instance of GoogleAPIClient -> Required for the GPS Location
         if(mGoogleApiClient == null) {
             mGoogleApiClient = new GoogleApiClient.Builder(this)
                     .addConnectionCallbacks(this)
                     .addOnConnectionFailedListener(this)
                     .addApi(LocationServices.API)
                     .build();
         }

        fm = getSupportFragmentManager();

    }

    @Override
    protected void onResume() {
        mGoogleApiClient.connect();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    public void setUpSampleManager() {
        mSampleClusterManager.setRenderer(new SamplingPointRenderer(this, mMap, mSampleClusterManager));

        mSampleClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<SamplingPoint>() {
            @Override
            public boolean onClusterItemClick(SamplingPoint point) {
                if (point.getTitle().equals("Sample_Point")) {

                    Fragment fragment = fm.findFragmentById(R.id.fragment_container);
                    if (fragment == null) {
                        FloatingActionButton gpsButton = (FloatingActionButton) findViewById(R.id.gps_button);
                        gpsButton.hide();
                        mSPVButton.hide();
                        mCamButton.hide();

                        // Hide the radius circle.
                        mRadiusCircle.setVisible(false);
                        LatLng markerPos = new LatLng(point.getLatitude() + 0.05f, point.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 11.0f));

                        fragment = new SPDataFragment();
                        mSPDataFragment = (SPDataFragment) fragment;
                        // Make buttons invisible.

                        fm.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                                .add(R.id.fragment_container, fragment)
                                .addToBackStack(null).commit();

                        mSampleClusterManager.clearItems();
                        mSampleClusterManager.addItem(point);
                        mSampleClusterManager.cluster();
                        // Show all photo markers currently on screen.
                        showPhotoMarkersInView();

                    }
                }

                return true;
            }
        });


    }

    public void setUpPictureManager(){
        mPictureClusterManager.setRenderer(new PicturePointRenderer(this, mMap, mPictureClusterManager));
        mPictureClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<PicturePoint>() {
            @Override
            public boolean onClusterItemClick(PicturePoint point) {

                if (point.getTitle().equals("Picture_Point")) {
                    PicturePoint pp = point;
                    PhotoViewFragment fragment = new PhotoViewFragment();
                    fragment.setGalleryItem(mSPDataFragment.getItems().get(Integer.valueOf(pp.getId())));
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left)
                            .add(R.id.fragment_container, fragment)
                            .addToBackStack(null).commit();
                }
                return true;
            }
        });
    }



    // Shows all photo markers currently on screen.
    private void showPhotoMarkersInView() {
        //if (LOGIC TO TEST IF ON SCREEN) {
            String[] points = new String[4];
            points[0] = "53";
            points[1] = "-3";
            points[2] = "50";
            points[3] = "3";
            new ImagesLocationDownloader().execute(points);

            photoMarkers.clear();
            PicturePoint photo0 = new PicturePoint(51.451902,-2.626990,0);
            PicturePoint photo1 = new PicturePoint(51.480805, -2.679945,1);
            PicturePoint photo2 = new PicturePoint(51.446635, -2.606646,2);
            PicturePoint photo3 = new PicturePoint(51.493915, -2.699290,3);
            PicturePoint photo4 = new PicturePoint(51.485578, -2.660623,4);
            PicturePoint photo5 = new PicturePoint(51.461413, -2.631612,5);
            PicturePoint photo6 = new PicturePoint(51.454605, -2.589866,6);
            PicturePoint photo7 = new PicturePoint(51.448363, -2.594877,7);
            PicturePoint photo8 = new PicturePoint(51.445726, -2.620722,8);
            PicturePoint photo9 = new PicturePoint(51.472145, -2.647501,9);

            photoMarkers.add(photo0);
            photoMarkers.add(photo1);
            photoMarkers.add(photo2);
            photoMarkers.add(photo3);
            photoMarkers.add(photo4);
            photoMarkers.add(photo5);
            photoMarkers.add(photo6);
            photoMarkers.add(photo7);
            photoMarkers.add(photo8);
            photoMarkers.add(photo9);

            for(PicturePoint p : photoMarkers){
                mPictureClusterManager.addItem(p);
            }
            mPictureClusterManager.cluster();
        //}
    }

    public void repopulateSamplePoints(ClusterManager<SamplingPoint> mSampleClusterManager){
        for(SamplingPoint sp : mSamplePoints){
            mSampleClusterManager.addItem(sp);
        }
        mSampleClusterManager.cluster();
    }


    // Manipulates the map once available when created.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        try {
            //This customises the google maps using the json file
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));
            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(55.036837,-3.625488), 5.0f) );
        setUpMultiManager();

    }

    public void setUpMultiManager(){
        mPictureClusterManager = new ClusterManager<PicturePoint>(this, mMap);
        mSampleClusterManager = new ClusterManager<SamplingPoint>(this, mMap);
        setUpSampleManager();
        setUpPictureManager();
        ml.addOC(mSampleClusterManager);
        ml.addOC(mPictureClusterManager);
        ml.addOM(mSampleClusterManager);
        ml.addOM(mPictureClusterManager);
        mMap.setOnMarkerClickListener(ml);
        mMap.setOnCameraIdleListener(ml);
    }

    @Override
    public void onTaskCompleted(List<SamplingPoint> result) {
        //do something after fetching sampling points
        mSamplePoints = result;
        for (SamplingPoint r : result) {
            mSampleClusterManager.addItem(r);
        }
        mSampleClusterManager.cluster();

        /*
        for (SamplingPoint r:result){
            System.out.println(r.getId() + " " + r.getLatitude() + " " + r.getLongitude() + " " + r.getSamplingPointType() + " ");
        }
        */
    }

    //Method called when connection established with Google Play Service Location API
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            connected = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        String id = "58a6dced3305b93398348546";
        new ImageDownloader().execute(id);
        /*
        String[] points = new String[4];
        points[0] = "52";
        points[1] = "-3";
        points[2] = "50";
        points[3] = "2";
        new ImagesLocationDownloader().execute(points);
        */
        /*
        int imageId = getResources().getIdentifier("sample1", "drawable", "com.bitbusters.android.speproject");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),imageId);
        Log.d(BITMAP_TAG, "Image width is : " + bitmap.getWidth());
        Log.d(BITMAP_TAG, "Image height is: " + bitmap.getHeight());
        Image image = new Image(bitmap,52.231,2.01,"Pollution over here!!!");
        new ImageUploader().execute(image);
*/
    }

    //Attempts to display user current location, zooming in to LatLng if connection exists
    public void zoomToCurrentLocation() {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            double longitude = mLocation.getLongitude();
            double latitude = mLocation.getLatitude();
            if(currentLocationMarker != null){
                currentLocationMarker.remove();
            }
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.target_icon)));
            currentLocationMarker.setTag("Current_Location");
            CameraPosition newcameraPosition = new CameraPosition.Builder().zoom(10).target(new LatLng(latitude, longitude)).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newcameraPosition));

        }
    }

    //Method called when location button is pressed
    public void currentLocation(View v){
        if(connected) {
            zoomToCurrentLocation();
        }else{
            mGoogleApiClient.connect();
        }
    }

    //Requesting permission for location information at runtime. Need for devices running Android 6 upwards
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                connected = true;
                zoomToCurrentLocation();
            } else {
                connected = false;
            }
        }
    }

    //Called when user is temporarily in a disconnected state.
    @Override
    public void onConnectionSuspended(int i) {
        connected = false;
        Log.i("LocationAPI", "Connection Suspended");
        mGoogleApiClient.connect();
    }

    //Called when there is an error connecting the client to the service
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connected = false;
        mGoogleApiClient.connect();
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment instanceof SPDataFragment) {
            fm.popBackStack();
            mRadiusCircle.setVisible(true);
            mPictureClusterManager.clearItems();
            mPictureClusterManager.cluster();
            mSampleClusterManager.clearItems();
            repopulateSamplePoints(mSampleClusterManager);


            // Re-show the buttons.
            FloatingActionButton gpsButton = (FloatingActionButton) this.findViewById(R.id.gps_button);
            gpsButton.show();
            mSPVButton.show();
            mCamButton.show();
        }
        else if (fragment instanceof PhotoViewFragment) {
            fm.popBackStack();
        }
        // Else do normal back button stuff.
        else {
            super.onBackPressed();
        }
    }



}
