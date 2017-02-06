package com.bitbusters.android.speproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class DataViewActivity extends FragmentActivity implements OnTaskCompleted, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private FloatingActionButton mCamButton;

    //variables used for displaying current location
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private boolean connected;
    private Marker currentLocationMarker;
    private FragmentManager fm;
    private SPDataFragment mSPDataFragment;
    private List<Marker> photoMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (marker.getTag().equals("Sample_Point")) {
            Fragment fragment = fm.findFragmentById(R.id.fragment_container);

            if (fragment == null) {
                LatLng markerPos = new LatLng(marker.getPosition().latitude + 0.05f, marker.getPosition().longitude);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPos, 11.0f));

                fragment = new SPDataFragment();
                mSPDataFragment = (SPDataFragment) fragment;

                fm.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                        .add(R.id.fragment_container, fragment)
                        .addToBackStack(null).commit();

                // Make buttons invisible.
                FloatingActionButton gpsButton = (FloatingActionButton) findViewById(R.id.gps_button);
                gpsButton.hide();
                mCamButton.hide();

                // TODO: Hide all other sample point markers.

                // Show all photo markers currently on screen.
                showPhotoMarkersInView();
            }
        }
        else if (marker.getTag().equals("Photo")) {
            PhotoViewFragment fragment = new PhotoViewFragment();
            fragment.setGalleryItem(mSPDataFragment.getItems().get(Integer.valueOf(marker.getTitle())));
            fm.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, 0, 0, R.anim.slide_out_left)
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null).commit();
        }
        else if (marker.getTag().equals("Current_Location")) {
            Log.e("333","test current location click");
        }

        return true;
    }

    // Shows all photo markers currently on screen.
    private void showPhotoMarkersInView() {
        //if (LOGIC TO TEST IF ON SCREEN) {

            LatLng photoLL = new LatLng(51.451902, -2.626990);
            Marker photo0 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("0")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo0.setTag("Photo");

            photoLL = new LatLng(51.480805, -2.679945);
            Marker photo1 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("1")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo1.setTag("Photo");

            photoLL = new LatLng(51.446635, -2.606646);
            Marker photo2 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("2")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo2.setTag("Photo");

            photoLL = new LatLng(51.493915, -2.699290);
            Marker photo3 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("3")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo3.setTag("Photo");

            photoLL = new LatLng(51.485578, -2.660623);
            Marker photo4 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("4")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo4.setTag("Photo");

            photoLL = new LatLng(51.461413, -2.631612);
            Marker photo5 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("5")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo5.setTag("Photo");

            photoLL = new LatLng(51.454605, -2.589866);
            Marker photo6 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("6")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo6.setTag("Photo");

            photoLL = new LatLng(51.448363, -2.594877);
            Marker photo7 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("7")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo7.setTag("Photo");

            photoLL = new LatLng(51.445726, -2.620722);
            Marker photo8 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("8")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo8.setTag("Photo");

            photoLL = new LatLng(51.472145, -2.647501);
            Marker photo9 = mMap.addMarker(new MarkerOptions()
                    .position(photoLL).title("9")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            photo9.setTag("Photo");

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

        //}
    }

    // Clear all photo markers.
    private void clearAllPhotoMarkers() {
        for (Marker marker : photoMarkers) {
            marker.remove();
        }
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

        // Add a marker in Sydney and move the camera
        LatLng bristolS = new LatLng(51.449695, -2.625872);
        Marker test1 = mMap.addMarker(new MarkerOptions()
                .position(bristolS)
                .title("Walk Start")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        test1.setTag("Sample_Point");

        /*
        LatLng bristolF = new LatLng(51.479907, -2.652651);
        Marker test2 = mMap.addMarker(new MarkerOptions()
                .position(bristolF)
                .title("Walk Finish")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        test2.setTag("Sample_Point");
        */

        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public void onTaskCompleted(List<SamplingPoint> result) {
        //do something after fetching sampling points
        for (SamplingPoint r:result){
            System.out.println(r.getId() + " " + r.getLatitude() + " " + r.getLongitude() + " " + r.getSamplingPointType() + " ");
        }
    }

    //Method called when connection established with Google Play Service Location API
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Defining lat and long and calling the APIs
        String[] location = new String[2];
        location[0] = "51.450010";
        location[1] = "-2.625455";
        //new SamplingPointsAPI(this).execute(location);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            connected = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        String[] points = new String[4];
        points[0] = "52";
        points[1] = "-3";
        points[2] = "50";
        points[3] = "2";
        //new ImagesDownloader().execute(points);
        new ImageUploader().execute();
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
            clearAllPhotoMarkers();

            // Re-show the buttons.
            FloatingActionButton gpsButton = (FloatingActionButton) this.findViewById(R.id.gps_button);
            gpsButton.show();
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
