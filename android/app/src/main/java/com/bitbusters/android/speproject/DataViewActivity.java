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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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

    private static final String TAG = "DATA_VIEW_ACTIVITY";
    private static final Integer BASE_ZOOM_LEVEL = 12;
    private static final int REQUEST_LOCATION = 1;
    private static final int CDE = 0;
    private static final int WIMS = 1;
    private static final int PERMIT = 2;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
//    private static final int TYPE_FILTER_CITIES = 5;

    private int currentView;
    private GoogleMap mMap;
    private ProgressBar mProgressSpinner;
    private ImageButton mMenuButton;
    private ImageButton mSearchButton;
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
    private MyAreaFragment mMyAreaFragment;
    private CDEDetailsFragment mCDEDetailsFragment;
    private WIMSDetailsFragment mWIMSDetailsFragment;
    private DischargePermitDataFragment mDischargePermitDataFragment;
    private List<WIMSPoint> mWIMSPoints = new ArrayList<>();
    private List<CDEPoint> mCDEPoints = new ArrayList<>();
    private List<DischargePermitPoint> mDischargePermitPoints = new ArrayList<>();
    private ClusterManager<WIMSPoint> mWIMSClusterManager;
    private ClusterManager<DischargePermitPoint> mPermitClusterManager;
    private GeoJsonLayer mGeoJsonLayer;
    private MultiListener mMultiListener = new MultiListener();
    private Drawer mDrawer;
    private CoordinateSystemConverter coordinateSystemConverter;

    private MyArea myArea;

    private WIMSPoint selectedWIMSPoint;
    private CDEPoint selectedCDEPoint;
    private DischargePermitPoint selectedPermitPoint;

    private Snackbar connectionSnack;
    private Snackbar zoomSnack;
    private boolean wasConnectionSnackDisplayed;
    private boolean wasZoomSnackDisplayed;


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
        mMenuButton = (ImageButton) findViewById(R.id.data_view_hamburger_button);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the Drawer
                mDrawer.openDrawer();
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
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }

        mFragmentManager = getSupportFragmentManager();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mMapCameraPadding = displayMetrics.heightPixels / 3;

        currentView = CDE;

        coordinateSystemConverter = new CoordinateSystemConverter();

        setupSnackbars();

        mSearchButton = (ImageButton) findViewById(R.id.data_view_search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch();
            }
        });
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

        final PrimaryDrawerItem drawerWIMS = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName(R.string.drawer_wims)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.ic_wims_marker);

        final PrimaryDrawerItem drawerCDE = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName(R.string.drawer_cde)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.ic_cde_marker);

        final PrimaryDrawerItem drawerPermit = new PrimaryDrawerItem()
                .withIdentifier(3)
                .withName(R.string.drawer_permit)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.ic_permit_marker);

        final SecondaryDrawerItem drawerMyArea = new SecondaryDrawerItem()
                .withIdentifier(4)
                .withName(R.string.drawer_my_area)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.ic_where_am_i);

        final SecondaryDrawerItem drawerInfo = new SecondaryDrawerItem()
                .withIdentifier(5)
                .withName(R.string.drawer_info)
                .withSelectedColor(0x0d4caf)
                .withSelectedTextColor(Color.WHITE)
                .withTextColor(Color.WHITE)
                .withIcon(R.drawable.info_white_no_padding);

        //create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(header)
                .withSliderBackgroundColor(Color.DKGRAY)
                .addDrawerItems(
                        drawerCDE,
                        drawerWIMS,
                        drawerPermit,
                        new DividerDrawerItem(),
                        drawerMyArea,
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
                                openView(WIMS);
                                break;
                            case 2:
                                closeView(currentView);
                                openView(CDE);
                                break;
                            case 3:
                                closeView(currentView);
                                openView(PERMIT);
                                break;
                            case 4:
                                showMyArea(view);
                                break;
                            case 5:
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
        setMapOnCameraIdleListener(view);
        updateLayerName(view);
        if(mMap.getCameraPosition().zoom > BASE_ZOOM_LEVEL - 1) {
            loadMarkers(view);
        }
    }

    public void closeView(int view) {
        mMap.setOnCameraIdleListener(null);
        clearMarkers(view);
    }

    public void openSearch() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("GB")
                    .build();

            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadMarkers(int view) {
        if (hasNetworkConnection()) {
            mProgressSpinner.setVisibility(View.VISIBLE);
            LatLng camCentre = mMap.getCameraPosition().target;
            VisibleRegion screen = mMap.getProjection().getVisibleRegion();
            dismissSnackbars();
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
                    String[] params = {String.valueOf(screen.farLeft.latitude),
                            String.valueOf(screen.farLeft.longitude),
                            String.valueOf(screen.nearRight.latitude),
                            String.valueOf(screen.nearRight.longitude),
                            String.valueOf(2017)};
                    new WIMSPointAPI(DataViewActivity.this).execute(params);
                    break;
                case PERMIT:
                    String[] input = {String.valueOf(screen.farLeft.latitude),
                            String.valueOf(screen.farLeft.longitude),
                            String.valueOf(screen.nearRight.latitude),
                            String.valueOf(screen.nearRight.longitude)};
                    new DischargePermitPointAPI(this).execute(input);
                    break;
            }
        } else {
//            Log.i(TAG, "waszoom: " + String.valueOf(wasConnectionSnackDisplayed) + " iszoom: " + String.valueOf(connectionSnack.isShown()));
            if(!wasConnectionSnackDisplayed && !connectionSnack.isShown()) {
                connectionSnack.show();
            }
        }
    }

    public void clearMarkers(int view) {
        switch (view) {
            case CDE:
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
                if( mMap.getCameraPosition().zoom > BASE_ZOOM_LEVEL - 1 ) {
                    loadMarkers(view_params);
                } else {
                    if(!wasZoomSnackDisplayed && !zoomSnack.isShown() && hasNetworkConnection()) {
                        zoomSnack.show();
                    }
                }
            }
        });
    }

    public void updateLayerName(int view) {
        switch (view) {
            case CDE:
                mLayerName.setText(R.string.drawer_cde);
                mLayerName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.layer_cde));
                break;
            case WIMS:
                mLayerName.setText(R.string.drawer_wims);
                mLayerName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.layer_wims));
                break;
            case PERMIT:
                mLayerName.setText(R.string.drawer_permit);
                mLayerName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.layer_permit));
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

    public void showMyArea(View v) {
        mDrawer.closeDrawer();
        if(hasNetworkConnection()) {
            if(haveGPSOn(getApplicationContext())) {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(connected) {
                        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);
                    }
                    if(mLocation != null) {
                        displayHomeButtons(false);

                        mProgressSpinner.setVisibility(View.VISIBLE);
                        final Snackbar snack = Snackbar.make(findViewById(R.id.fragment_container),
                                "Determining your exact location",
                                Snackbar.LENGTH_INDEFINITE);
                        snack.show();

                        myArea = new MyArea();

                        new MyAreaCDEAPI(this).execute(getCurrentLocation(), myArea);

                        myArea.setOnPopulatedListener(new OnPopulated() {
                            @Override
                            public void onPopulated() {
                                Fragment fragment = new MyAreaFragment();
                                mMyAreaFragment = (MyAreaFragment) fragment;

                                mFragmentManager.beginTransaction()
                                        .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                                        .add(R.id.fragment_container, mMyAreaFragment)
                                        .addToBackStack(null).commit();

                                mProgressSpinner.setVisibility(View.INVISIBLE);
                                snack.dismiss();
                            }
                        });
                    } else {
                        displaySimpleSnackbar("Failed to determine your exact location", Snackbar.LENGTH_SHORT);
                    }
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                }
            } else {
                displaySimpleSnackbar("GPS Required", Snackbar.LENGTH_SHORT);
            }
        } else {
            displaySimpleSnackbar("Internet Connection Required", Snackbar.LENGTH_SHORT);
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


    // On CDE feature click.
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

                    new CDEPointRatingsAPI(mCDEDataFragment).execute(selectedCDEPoint, CDEPoint.REAL);
                    new CDEPointRatingsAPI(mCDEDataFragment).execute(selectedCDEPoint, CDEPoint.PREDICTED);
                    new CDEPointRatingsAPI(mCDEDataFragment).execute(selectedCDEPoint, CDEPoint.OBJECTIVE);

                    mFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                            .add(R.id.fragment_container, fragment)
                            .commit();
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

                        new WIMSPointRatingsAPI(mWIMSDataFragment).execute(selectedWIMSPoint);
                        new WIMSPointMetalsAPI(mWIMSDataFragment).execute(selectedWIMSPoint);

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

    public void openCDEDetailsFragment() {
        Fragment fragment = new CDEDetailsFragment();
        mCDEDetailsFragment = (CDEDetailsFragment) fragment;

        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void openWIMSDetailsFragment() {
        Fragment fragment = new WIMSDetailsFragment();
        mWIMSDetailsFragment = (WIMSDetailsFragment) fragment;

        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_left)
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(place.getViewport(), 0));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            }
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
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.036837, -3.625488), 5.0f));

        setUpMultiManager();

        mGoogleApiClient.connect();
    }

    public void setUpMultiManager() {
        mWIMSClusterManager = new ClusterManager<>(this, mMap);
        mPermitClusterManager = new ClusterManager<>(this, mMap);

        setUpWIMSManager();
        setUpPermitManager();
        setUpCDEManager();

        mMultiListener.addOC(mWIMSClusterManager);
        mMultiListener.addOC(mPermitClusterManager);

        mMultiListener.addOM(mWIMSClusterManager);
        mMultiListener.addOM(mPermitClusterManager);

        mMap.setOnMarkerClickListener(mMultiListener);
        mMap.setOnCameraIdleListener(mMultiListener);
    }

    @Override
    public void onTaskCompletedWIMSPoint(List<WIMSPoint> result) {
        mWIMSPoints = result;
        mWIMSClusterManager.addItems(result);
        mWIMSClusterManager.cluster();
    }

    @Override
    public void onTaskCompletedCDEPoint(List<CDEPoint> result) {
        mCDEPoints = result;
        for (CDEPoint r : result) {
            boolean isOnMap = false;
            for(GeoJsonFeature feature : mGeoJsonLayer.getFeatures()) {
                if (feature.equals(r.getRiverPolygon())) isOnMap = true;
            }
            if(!isOnMap) {
                r.getRiverPolygon().setPolygonStyle(GeoJsonStyles.geoJsonPolygonStyle());
                mGeoJsonLayer.addFeature(r.getRiverPolygon());
            }
        }
        mGeoJsonLayer.addLayerToMap();
    }

    @Override
    public void onTaskCompletedCDERiverLine(CDEPoint result) {
        result.getRiverLine().setLineStringStyle(GeoJsonStyles.geoJsonLineStringStyle());
        mGeoJsonLayer.addFeature(result.getRiverLine());
        mGeoJsonLayer.addLayerToMap();
    }

    @Override
    public void onTaskCompletedDischargePermitPoint(List<DischargePermitPoint> result) {
        mDischargePermitPoints.addAll(result);
        mPermitClusterManager.addItems(result);
        mPermitClusterManager.cluster();
    }

    @Override
    public void onTaskCompletedMyAreaWIMS(WIMSPoint wimsPoint) {
        myArea.setWimsPoint(wimsPoint);
    }

    @Override
    public void onTaskCompletedMyAreaPermit(DischargePermitPoint permitPoint) {
        myArea.setPermitPoint(permitPoint);
    }

    @Override
    public void onTaskCompletedMyAreaCDE() {
        new MyAreaCatchmentsAPI().execute(myArea);
        new MyAreaNearestWIMSAPI(this).execute(getCurrentLocation());
        new MyAreaNearestPermitAPI(this).execute(getCurrentLocation());
    }


    public void showGeoJsonData(CDEPoint cdePoint) {
        cdePoint.getRiverPolygon().setPolygonStyle(GeoJsonStyles.geoJsonPolygonStyle());
        mGeoJsonLayer.addFeature(cdePoint.getRiverPolygon());
        mGeoJsonLayer.addLayerToMap();
    }

    //Method called when connection established with Google Play Service Location API
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            connected = true;
            if(!haveGPSOn(getApplicationContext())){
                openView(currentView);
            } else {
                displayLocation();
                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if(mMap.getCameraPosition().zoom < BASE_ZOOM_LEVEL - 1) {
                            displayLocation();
                        } else {
                            openView(currentView);
                            return;
                        }
                    }
                });
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    public void displayLocation() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation == null) {
                Log.e(TAG, "mLocation was null");
            } else {
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
            final CameraPosition newCameraPosition = new CameraPosition.Builder().zoom(BASE_ZOOM_LEVEL)
                    .target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())).build();
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
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_target_black)));
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
            } else {
                displayLocation();
            }
        } else {
            displaySimpleSnackbar("GPS Required", Snackbar.LENGTH_SHORT);
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
        displaySimpleSnackbar("Location Connection Failed", Snackbar.LENGTH_SHORT);
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
            mFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                    .remove(fragment)
                    .commit();
            clearMarkers(CDE);
            openView(CDE);
            mProgressSpinner.setVisibility(View.INVISIBLE);
            mMap.setPadding(0, 0, 0, 0);
            displayHomeButtons(true);
        } else if (fragment instanceof CDEDetailsFragment) {
            mFragmentManager.popBackStack();
        } else if (fragment instanceof WIMSDetailsFragment) {
            mFragmentManager.popBackStack();
        } else if (fragment instanceof DischargePermitDataFragment) {
            mFragmentManager.popBackStack();
            clearMarkers(PERMIT);
            openView(PERMIT);
            mProgressSpinner.setVisibility(View.INVISIBLE);
            mMap.setPadding(0, 0, 0, 0);
            displayHomeButtons(true);
        } else if (fragment instanceof InfoFragment) {
            mFragmentManager.popBackStack();
            displayHomeButtons(true);
        } else if (fragment instanceof MyAreaFragment) {
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
            mSearchButton.setVisibility(View.VISIBLE);
            mMenuButton.setVisibility(View.VISIBLE);
            mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mGpsButton.hide();
            mMenuButton.setVisibility(View.INVISIBLE);
            mSearchButton.setVisibility(View.INVISIBLE);
            mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
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
            if (cdePoint.getRiverPolygon() == feature) {
                selectedCDEPoint = cdePoint;
            }
        }
    }

    public GeoJsonFeature getGeoJSONFeature(Feature feature) {
        GeoJsonFeature result = null;
        for (CDEPoint cdePoint : mCDEPoints) {
            if (cdePoint.getRiverPolygon() == feature) {
                result = cdePoint.getRiverPolygon();
            }
        }
        return result;
    }

    public ProgressBar getProgressSpinner() {
        return mProgressSpinner;
    }

    public boolean hasNetworkConnection() {
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

    private void displaySimpleSnackbar(String message, Integer length) {
        Snackbar snack = Snackbar.make(findViewById(R.id.fragment_container), message, length);
        snack.show();
    }

    private Snackbar dismissableSnackbar(String message, Integer length) {
        Snackbar snack = Snackbar.make(findViewById(R.id.fragment_container),message, length);
        snack.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return snack;
    }

    private void setupSnackbars(){
        wasConnectionSnackDisplayed = false;
        wasZoomSnackDisplayed = false;

        connectionSnack  = dismissableSnackbar( "Data retrieval needs internet connection", Snackbar.LENGTH_INDEFINITE);
        connectionSnack.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                wasConnectionSnackDisplayed = true;
            }
        });

        zoomSnack = dismissableSnackbar("Zoom in to show data points", Snackbar.LENGTH_INDEFINITE);
        zoomSnack.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                wasZoomSnackDisplayed = true;
            }
        });
    }

    private void dismissSnackbars() {
        wasConnectionSnackDisplayed = false;
        wasZoomSnackDisplayed = false;
        if(connectionSnack.isShown()) connectionSnack.dismiss();
        if(zoomSnack.isShown()) zoomSnack.dismiss();
    }

    public Location getCurrentLocation() {
        return mLocation;
    }

    public MyArea getMyArea() {
        return myArea;
    }

    public void setCameraFocusOnMarker(Object point) {
        LatLng position = null;
        onBackPressed();
        closeView(currentView);
        if(point instanceof WIMSPoint) {
            openView(WIMS);
            position = new LatLng(((WIMSPoint) point).getLatitude(), ((WIMSPoint) point).getLongitude());
        } else if(point instanceof DischargePermitPoint) {
            openView(PERMIT);
            position = new LatLng(((DischargePermitPoint) point).getLatitude(), ((DischargePermitPoint) point).getLongitude());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

}