package com.bitbusters.android.speproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Layer;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class DataViewActivity extends FragmentActivity implements OnTaskCompleted, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String BITMAP_TAG = "BITMAP";
    private static final String TAG = "DATA_VIEW_ACTIVITY";
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_CAMERA = 2;
    private static final int CDE = 0;
    private static final int WIMS = 1;
    private static final int PERMIT = 2;
    private int currentView;
    private GoogleMap mMap;
    private ProgressBar mProgressSpinner;
    private FloatingActionButton mCameraButton;
    private ImageButton mMenuButton;
    private FloatingActionButton mGpsButton;
    private TextView mLayerName;

    //variables used for displaying current location
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private boolean connected;
    private boolean inPhotoDataView;
    private int mMapCameraPadding;
    private Marker currentLocationMarker;
    private FragmentManager mFragmentManager;
    private WIMSDataFragment mWIMSDataFragment;
    private CDEDataFragment mCDEDataFragment;
    private DischargePermitDataFragment mDischargePermitDataFragment;
    private PhotoDataFragment mPhotoDataFragment;
    private List<WIMSPoint> mWIMSPoints = new ArrayList<>();
    private List<CDEPoint> mCDEPoints = new ArrayList<>();
    private List<GalleryItem> photoMarkers = new ArrayList<>();
    private List<DischargePermitPoint> mDischargePermitPoints = new ArrayList<>();
    private Boolean imageLocationsDownloaded;
    private ClusterManager<WIMSPoint> mWIMSClusterManager;
    private ClusterManager<DischargePermitPoint> mPermitClusterManager;
    private GeoJsonLayer mGeoJsonLayer;
    private ClusterManager<GalleryItem> mPictureClusterManager;
    private MultiListener mMultiListener = new MultiListener();
    private Drawer mDrawer;
    private WIMSDbHelper mDbHelper;
    private CoordinateSystemConverter coordinateSystemConverter;

    private WIMSPoint selectedWIMSPoint;
    private CDEPoint selectedCDEPoint;
    private DischargePermitPoint selectedPermitPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mProgressSpinner = (ProgressBar) findViewById(R.id.progress_spinner);

        // Initialises the drawer menu
        setupDrawer();

        // The action performed when the menu button is pressed.
        mMenuButton = (ImageButton) findViewById(R.id.hamburger_button);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the Drawer
                mDrawer.openDrawer();
            }
        });

        // The action performed when the camera button is pressed.
        mCameraButton = (FloatingActionButton) findViewById(R.id.cam_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DataViewActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startCameraIntent(v);
                } else {
                    ActivityCompat.requestPermissions(DataViewActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
            }
        });

        mGpsButton = (FloatingActionButton) this.findViewById(R.id.gps_button);

        mLayerName = (TextView) this.findViewById(R.id.layer_name);

        // Create an instance of GoogleAPIClient -> Required for the GPS Location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mFragmentManager = getSupportFragmentManager();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mMapCameraPadding = displayMetrics.heightPixels / 3;

        currentView = CDE;

        coordinateSystemConverter = new CoordinateSystemConverter();

//        mDbHelper = new WIMSDbHelper(getApplicationContext());

//        new WIMSPopulateDatabase(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setupDrawer() {

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_back)
                .addProfiles(
                        new ProfileDrawerItem().withName("myRivers").withIcon(R.drawable.icon_green_blue)
                )
                .withTextColor(Color.BLACK)
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        final PrimaryDrawerItem drawerCDE = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName(R.string.drawer_cde)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.ic_cde_marker);

        final PrimaryDrawerItem drawerWIMS = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName(R.string.drawer_wims)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.ic_wims_marker);

        final PrimaryDrawerItem drawerImages = new PrimaryDrawerItem()
                .withIdentifier(3)
                .withName(R.string.drawer_images)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.ic_photo_marker);

        final SecondaryDrawerItem drawerInfo = new SecondaryDrawerItem()
                .withIdentifier(4)
                .withName(R.string.drawer_info)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.info_white);

        //create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .withSliderBackgroundColor(Color.DKGRAY)
                .addDrawerItems(
                        drawerCDE,
                        drawerWIMS,
                        drawerImages,
                        new DividerDrawerItem(),
                        drawerInfo
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        mDrawer.closeDrawer();
//                        Log.i(TAG, "identifier: " + Long.toString(drawerItem.getIdentifier()));
                        switch ((int) drawerItem.getIdentifier()){
                            case 1:
                                closeView(currentView);
                                openView(CDE);
                                break;
                            case 2:
                                closeView(currentView);
                                openView(WIMS);
                                break;
                            case 3:
                                if(currentView != PERMIT) {
                                    closeView(currentView);
                                    openView(PERMIT);
                                }
                                break;
                            case 4:
                                showInfo(view);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                })
                .build();
    }

    public void openView(int view) {
        currentView = view;
        loadMarkers(view);
        setMapOnCameraIdleListener(view);
        updateLayerName(view);
    }

    public void closeView(int view) {
        mMap.setOnCameraIdleListener(null);
        clearMarkers(view);
    }

    public void loadMarkers(int view) {
        if (haveNetworkConnection()) {
            mProgressSpinner.setVisibility(View.VISIBLE);
            LatLng camCentre = mMap.getCameraPosition().target;
            VisibleRegion screen = mMap.getProjection().getVisibleRegion();
            switch (view) {
                case CDE:
                    LatLng[] polygon = new LatLng[4];
                    polygon[0] = screen.farLeft;
                    polygon[1] = screen.farRight;
                    polygon[2] = screen.nearRight;
                    polygon[3] = screen.nearLeft;
                    new CDEPointAPI(DataViewActivity.this).execute(polygon);
                    break;
                case WIMS:
                    double distanceM = SphericalUtil.computeDistanceBetween(screen.farLeft,screen.nearRight);
                    int distanceKM = (int) (distanceM / 1.5) / 1000;
                    String[] params = {String.valueOf(camCentre.latitude),
                            String.valueOf(camCentre.longitude),
                            String.valueOf(distanceKM)};
                    new WIMSPointAPI(DataViewActivity.this).execute(params);
//                    String[] pt = new String[5];
//                    pt[0] = String.valueOf(screen.farLeft.latitude);
//                    pt[1] = String.valueOf(screen.farLeft.longitude);
//                    pt[2] = String.valueOf(screen.nearRight.latitude);
//                    pt[3] = String.valueOf(screen.nearRight.longitude);
//                    pt[4] = "2016";
//                    Log.i(TAG, "Total Rows: " + mDbHelper.numberOfRows());
//                    Log.i(TAG, "Total Nulls: " + mDbHelper.numberOfNulls());
//                    new WIMSPointAPIDatabase(this, mDbHelper).execute(pt);
                    break;
                case PERMIT:
                    mDischargePermitPoints = new ArrayList<>();
                    double distM = SphericalUtil.computeDistanceBetween(screen.farLeft,screen.nearRight);
                    int distKM = (int) (distM / 1.5) / 1000;
                    Pair<Double,Double> eastNorth =
                            coordinateSystemConverter.convertLatLngToEastNorth(camCentre.latitude, camCentre.longitude);
                    getDischargePermitData(eastNorth.first, eastNorth.second, distKM);
                    break;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Data retrieval needs internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void getDischargePermitData(Double easting, Double northing, int distance) {
        List<String> effluentTypes = new ArrayList<>();
        effluentTypes.add("waste-site");
        effluentTypes.add("agriculture");
//        effluentTypes.add("sewage-not-water-company");
        for (String effluentType : effluentTypes) {
            String[] parameters = {String.valueOf(easting.intValue()),
                                   String.valueOf(northing.intValue()),
                                   String.valueOf(distance),
                                   effluentType};
            new DischargePermitPointAPI(this).execute(parameters);
        }
    }

    public void clearMarkers(int view) {
        switch (view) {
            case CDE:
//                mCDEClusterManager.clearItems();
//                mCDEClusterManager.cluster();
//                mGeoJsonLayer.removeLayerFromMap();
//                Log.i(TAG, "GeoJsonLayerFeatures : " + mGeoJsonLayer.getFeatures().toString());
                List<GeoJsonFeature> featuresToRemove = new ArrayList<>();
                for(GeoJsonFeature feature : mGeoJsonLayer.getFeatures()) {
                    featuresToRemove.add(feature);
                }
                for(GeoJsonFeature feature : featuresToRemove) {
                    mGeoJsonLayer.removeFeature(feature);
                }
                break;
            case WIMS:
                mWIMSClusterManager.clearItems();
                mWIMSClusterManager.cluster();
                break;
            case PERMIT:
                mPermitClusterManager.clearItems();
                mPermitClusterManager.cluster();
                break;
        }
    }

    public void setMapOnCameraIdleListener(int view) {
        final int view_params = view;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                clearMarkers(view_params);
                if( mMap.getCameraPosition().zoom > 10 ) {
                    loadMarkers(view_params);
                }
            }
        });
    }

    public void updateLayerName(int view) {
        switch (view) {
            case CDE:
                mLayerName.setText(R.string.layer_cde);
                mLayerName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.cde));
                break;
            case WIMS:
                mLayerName.setText(R.string.layer_wims);
                mLayerName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.wims));
                break;
            case PERMIT:
                mLayerName.setText(R.string.layer_image);
                mLayerName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.photo));
                break;
        }
    }

    public void showInfo(View v) {
        // Hide the floating action buttons.
        displayHomeButtons(false);
        // Initiate the info fragment.
        InfoFragment fragment = new InfoFragment();
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    public void startCameraIntent(View v) {
        if (haveNetworkConnection() && haveGPSOn(v.getContext())) {
            Intent photoCommentActivityIntent = new Intent(v.getContext(), PhotoCommentActivity.class);
            startActivity(photoCommentActivityIntent);
        } else {
            Toast.makeText(v.getContext(), "Uploading image needs internet connection and gps", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
            connected = false;
        }
    }


    // On CDE sampling point click.
    public void setUpCDEManager() {
        mGeoJsonLayer = new GeoJsonLayer(mMap, new JSONObject());
        mGeoJsonLayer.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                setSelectedCDEPoint(feature);
                Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
                if (fragment == null) {
                    displayHomeButtons(false);

                    mMap.setOnCameraIdleListener(null);

                    clearMarkers(CDE);
                    mGeoJsonLayer.addFeature((GeoJsonFeature) feature);

                    fragment = new CDEDataFragment();
                    mCDEDataFragment = (CDEDataFragment) fragment;

                    mFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                            .add(R.id.fragment_container, fragment)
                            .addToBackStack(null).commit();
                }
            }
        });
    }

    // On WIMS sampling point click
    public void setUpWIMSManager() {
        mWIMSClusterManager.setRenderer(new WIMSPointRenderer(this, mMap, mWIMSClusterManager));
        mWIMSClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<WIMSPoint>() {
            @Override
            public boolean onClusterItemClick(WIMSPoint point) {
                if (point.getTitle().equals("WIMS_Point")) {
                    selectedWIMSPoint = point;
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
                    if (fragment == null) {
                        displayHomeButtons(false);

                        LatLng markerPos = new LatLng(point.getLatitude(), point.getLongitude());
                        mMap.setPadding(0, mMapCameraPadding, 0, 0);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPos, mMap.getCameraPosition().zoom));
                        mMap.setOnCameraIdleListener(null);

                        mWIMSClusterManager.clearItems();
                        mWIMSClusterManager.addItem(point);
                        mWIMSClusterManager.cluster();

                        fragment = new WIMSDataFragment();
                        mWIMSDataFragment = (WIMSDataFragment) fragment;

                        mFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                                .add(R.id.fragment_container, fragment)
                                .addToBackStack(null).commit();
                    }
                }
                return true;
            }
        });

        mWIMSClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<WIMSPoint>() {
            @Override
            public boolean onClusterClick(Cluster<WIMSPoint> cluster) {
                mMap.setPadding(0, mMapCameraPadding, 0, 0);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                        (float) Math.floor(mMap.getCameraPosition().zoom + 1)), 300, null);
                return true;
            }
        });
    }

    // On Image point click.
    public void setUpPermitManager() {
        mPermitClusterManager.setRenderer(new DischargePermitPointRenderer(this, mMap, mPermitClusterManager));
        mPermitClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<DischargePermitPoint>() {
            @Override
            public boolean onClusterItemClick(DischargePermitPoint point) {
                if (point.getTitle().equals("Waste_Point")) {
                    selectedPermitPoint = point;
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
                    if (fragment == null) {
                        displayHomeButtons(false);

                        LatLng markerPos = new LatLng(point.getLatitude(), point.getLongitude());
                        mMap.setPadding(0, mMapCameraPadding, 0, 0);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPos, mMap.getCameraPosition().zoom));
                        mMap.setOnCameraIdleListener(null);

                        mPermitClusterManager.clearItems();
                        mPermitClusterManager.addItem(point);
                        mPermitClusterManager.cluster();

                        fragment = new DischargePermitDataFragment();
                        mDischargePermitDataFragment = (DischargePermitDataFragment) fragment;

                        mFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                                .add(R.id.fragment_container, fragment)
                                .addToBackStack(null).commit();
                    }
                }
                return true;
            }
        });
        mPermitClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<DischargePermitPoint>() {
            @Override
            public boolean onClusterClick(Cluster<DischargePermitPoint> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(),
                        (float) Math.floor(mMap.getCameraPosition().zoom + 1)), 300, null);
                return true;
            }
        });
    }

    public void repopulateWIMSPoints() {
        for (WIMSPoint sp : mWIMSPoints) {
            mWIMSClusterManager.addItem(sp);
        }
        mWIMSClusterManager.cluster();
    }
//
//    public void repopulateCDEPoints() {
//        for (CDEPoint cp : mCDEPoints) {
//            mCDEClusterManager.addItem(cp);
//        }
//        mCDEClusterManager.cluster();
//    }


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
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.036837, -3.625488), 5.0f));

        setUpMultiManager();
        // Zooms in on current location
        currentLocation(findViewById(R.id.map));

        if(!haveGPSOn(getApplicationContext())){
            Toast.makeText(getApplicationContext(), "Please enable your GPS or zoom in to a " +
                    "desired location", Toast.LENGTH_LONG).show();
        }

        // When zoom finished it populates the map with sampling points
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                openView(PERMIT);
            }
        });

    }

    public void setUpMultiManager() {
        mPictureClusterManager = new ClusterManager<>(this, mMap);
        mWIMSClusterManager = new ClusterManager<>(this, mMap);
        mPermitClusterManager = new ClusterManager<>(this, mMap);

        setUpPermitManager();
        setUpWIMSManager();
        setUpCDEManager();

        mMultiListener.addOC(mWIMSClusterManager);
        mMultiListener.addOC(mPermitClusterManager);
        mMultiListener.addOC(mPictureClusterManager);

        mMultiListener.addOM(mWIMSClusterManager);
        mMultiListener.addOM(mPermitClusterManager);
        mMultiListener.addOM(mPictureClusterManager);

        mMap.setOnMarkerClickListener(mMultiListener);
        mMap.setOnCameraIdleListener(mMultiListener);
    }

    @Override
    public void onTaskCompletedWIMSPoint(List<WIMSPoint> result) {
        mWIMSPoints = result;
        for (WIMSPoint r : result) {
            mWIMSClusterManager.addItem(r);
        }
        mWIMSClusterManager.cluster();
    }

    @Override
    public void onTaskCompletedCDEPoint(List<CDEPoint> result) {
        mCDEPoints = result;
        for (CDEPoint r : result) {
            new CDEPointRatingsAPI(this).execute(r);
        }
    }

    @Override
    public void onTaskCompletedDischargePermitPoint(List<DischargePermitPoint> result) {
        mDischargePermitPoints.addAll(result);
        mPermitClusterManager.addItems(result);
        mPermitClusterManager.cluster();
    }

    public void showGeoJsonData(CDEPoint cdePoint) {
        cdePoint.getGeoJSONFeature().setPolygonStyle(GeoJsonStyles.geoJsonPolygonStyle(cdePoint));
        cdePoint.getGeoJSONFeature().setLineStringStyle(GeoJsonStyles.geoJsonLineStringStyle(cdePoint));
        mGeoJsonLayer.addFeature(cdePoint.getGeoJSONFeature());
        mGeoJsonLayer.addLayerToMap();
    }

    //Method called when connection established with Google Play Service Location API
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            connected = true;
            displayLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    public void displayLocation() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation == null) {
                Log.e(TAG, "mLocation was null");
            }
            if (mLocation != null) {
                setLocationMarker(mLocation.getLatitude(), mLocation.getLongitude());
                updateMapCameraPosition();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    public void updateMapCameraPosition() {
        if (mMap != null && mLocation != null) {
            CameraPosition newCameraPosition = new CameraPosition.Builder().zoom(11)
                    .target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())).build();
            if(currentView == PERMIT) {
                mMap.setPadding(0, 0, 0, mMapCameraPadding);
            } else {
                mMap.setPadding(0, 0, 0, 0);
            }

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
        }
    }

    public LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;

    }

    public void setLocationMarker(double latitude, double longitude){
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.target_icon)));
        currentLocationMarker.setTag("Current Location");
    }

    @Override
    public void onLocationChanged(Location location) {
        setLocationMarker(location.getLatitude(), location.getLongitude());
    }

    //Method called when location button is pressed
    public void currentLocation(View v){
        if(haveGPSOn(v.getContext())){
            if(!connected){
                mGoogleApiClient.connect();

            }else if(currentLocationMarker != null){
                displayLocation();
            }

        }else{
            Toast.makeText(v.getContext(), "GPS Required", Toast.LENGTH_LONG).show();
        }
    }

    //Requesting permission for location information at runtime. Need for devices running Android 6 upwards
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                Log.i(TAG,"Location request allowed");
                connected = true;
                displayLocation();
            } else {
                connected = false;
            }
        }else if(requestCode == REQUEST_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG,"Photo granted");
                mCameraButton.callOnClick();
            }
        }
    }

    //Called when user is temporarily in a disconnected state.
    @Override
    public void onConnectionSuspended(int i) {
        connected = false;
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    //Called when there is an error connecting the client to the service
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed");
        Toast.makeText(this,"Location Connection Failed", Toast.LENGTH_SHORT).show();
        connected = false;
        mGoogleApiClient.connect();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment instanceof WIMSDataFragment) {
            mFragmentManager.popBackStack();
            clearMarkers(WIMS);
            openView(WIMS);
            mProgressSpinner.setVisibility(View.INVISIBLE);
            mMap.setPadding(0, 0, 0, 0);
            displayHomeButtons(true);
        } else if (fragment instanceof CDEDataFragment) {
            mFragmentManager.popBackStack();
            clearMarkers(CDE);
            openView(CDE);
            mProgressSpinner.setVisibility(View.INVISIBLE);
            mMap.setPadding(0, 0, 0, 0);
            displayHomeButtons(true);
        } else if (fragment instanceof DischargePermitDataFragment) {
            mFragmentManager.popBackStack();
            clearMarkers(PERMIT);
            openView(PERMIT);
            mProgressSpinner.setVisibility(View.INVISIBLE);
            mMap.setPadding(0, 0, 0, 0);
            displayHomeButtons(true);
        } else if (fragment instanceof PhotoDataFragment) {
            closeView(PERMIT);
            openView(CDE);
        }
        else if (fragment instanceof PhotoViewFragment) {
            mFragmentManager.popBackStack();
            displayHomeButtons(true);
        }
        else if (fragment instanceof InfoFragment) {
            mFragmentManager.popBackStack();
            displayHomeButtons(true);
        }
        else {
            super.onBackPressed();
        }
    }

    public void displayHomeButtons(boolean condition) {
        if(condition) {
            mGpsButton.show();
            mCameraButton.show();
            mMenuButton.setVisibility(View.VISIBLE);
            mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mGpsButton.hide();
            mCameraButton.hide();
            mMenuButton.setVisibility(View.INVISIBLE);
            mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public void setHomeButtonsPhotoView(boolean condition) {
        // Initialise camera button layout
        mCameraButton = (FloatingActionButton) this.findViewById(R.id.cam_button);
        FrameLayout.LayoutParams mCameraButtonLayoutParams =
                (FrameLayout.LayoutParams) mCameraButton.getLayoutParams();

        // Initialise gps button layout
        mGpsButton = (FloatingActionButton) this.findViewById(R.id.gps_button);
        FrameLayout.LayoutParams mGpsButtonLayoutParams =
                (FrameLayout.LayoutParams) mGpsButton.getLayoutParams();

        // Set the new positions
        if(condition) {
            mCameraButtonLayoutParams.gravity = Gravity.TOP | Gravity.END;
            mGpsButtonLayoutParams.gravity = Gravity.TOP | Gravity.END;
        } else {
            mCameraButtonLayoutParams.gravity = Gravity.BOTTOM | Gravity.END;
            mGpsButtonLayoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        }
        //Apply the changes
        mCameraButton.setLayoutParams(mCameraButtonLayoutParams);
        mGpsButton.setLayoutParams(mGpsButtonLayoutParams);
    }

    public List<GalleryItem> getPhotoMarkers() {
        return photoMarkers;
    }

    public void setPhotoMarkers(List<GalleryItem> photoMarkers) {
        this.photoMarkers = photoMarkers;
    }

    public ClusterManager<GalleryItem> getPictureClusterManager() {
        return mPictureClusterManager;
    }

    public void setPictureClusterManager(ClusterManager<GalleryItem> pictureClusterManager) {
        mPictureClusterManager = pictureClusterManager;
    }

    public Boolean getImageLocationsDownloaded() {
        return imageLocationsDownloaded;
    }

    public void setImageLocationsDownloaded(Boolean imageLocationsDownloaded) {
        this.imageLocationsDownloaded = imageLocationsDownloaded;
    }

    public WIMSPoint getSelectedWIMSPoint(){
        return selectedWIMSPoint;
    }

    public DischargePermitPoint getSelectedPermitPoint(){
        return selectedPermitPoint;
    }

    public CDEPoint getSelectedCDEPoint(){
        return selectedCDEPoint;
    }

    public void setSelectedCDEPoint(Feature feature) {
        for (CDEPoint cdePoint : mCDEPoints) {
            if (cdePoint.getGeoJSONFeature() == feature) {
                selectedCDEPoint = cdePoint;
            }
        }
    }

    public GeoJsonFeature getGeoJSONFeature(Feature feature) {
        GeoJsonFeature result = null;
        for (CDEPoint cdePoint : mCDEPoints) {
            if (cdePoint.getGeoJSONFeature() == feature) {
                result = cdePoint.getGeoJSONFeature();
            }
        }
        return result;
    }

    public ProgressBar getProgressSpinner() {
        return mProgressSpinner;
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                haveConnectedWifi = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                haveConnectedMobile = true;
            }
        }

        return haveConnectedWifi || haveConnectedMobile;

    }

    public boolean haveGPSOn(Context context){
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}