package com.epimorphics.android.myrivers.activities;

import android.Manifest;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.util.DisplayMetrics;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epimorphics.android.myrivers.R;
import com.epimorphics.android.myrivers.apis.CDEPointAPI;
import com.epimorphics.android.myrivers.apis.CDEPointRatingsAPI;
import com.epimorphics.android.myrivers.apis.CDERiverLineAPI;
import com.epimorphics.android.myrivers.apis.DischargePermitPointAPI;
import com.epimorphics.android.myrivers.apis.MyAreaCDEAPI;
import com.epimorphics.android.myrivers.apis.MyAreaCatchmentsAPI;
import com.epimorphics.android.myrivers.apis.MyAreaNearestPermitAPI;
import com.epimorphics.android.myrivers.apis.MyAreaNearestWIMSAPI;
import com.epimorphics.android.myrivers.apis.WIMSPointAPI;
import com.epimorphics.android.myrivers.apis.WIMSPointMetalsAPI;
import com.epimorphics.android.myrivers.apis.WIMSPointMeasurementsAPI;
import com.epimorphics.android.myrivers.data.CDEPoint;
import com.epimorphics.android.myrivers.data.DischargePermitPoint;
import com.epimorphics.android.myrivers.data.MyArea;
import com.epimorphics.android.myrivers.data.Point;
import com.epimorphics.android.myrivers.data.WIMSPoint;
import com.epimorphics.android.myrivers.fragments.CDEDataFragment;
import com.epimorphics.android.myrivers.fragments.CDEDetailsFragment;
import com.epimorphics.android.myrivers.fragments.DischargePermitDataFragment;
import com.epimorphics.android.myrivers.fragments.InfoFragment;
import com.epimorphics.android.myrivers.fragments.MyAreaFragment;
import com.epimorphics.android.myrivers.fragments.WIMSDataFragment;
import com.epimorphics.android.myrivers.fragments.WIMSDetailsFragment;
import com.epimorphics.android.myrivers.helpers.GeoJsonStyles;
import com.epimorphics.android.myrivers.helpers.MultiListener;
import com.epimorphics.android.myrivers.interfaces.OnPopulated;
import com.epimorphics.android.myrivers.interfaces.OnTaskCompleted;
import com.epimorphics.android.myrivers.renderers.DischargePermitPointRenderer;
import com.epimorphics.android.myrivers.renderers.WIMSPointRenderer;

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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
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

import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_GEOCODE;


public class DataViewActivity extends FragmentActivity implements OnTaskCompleted, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "DATA_VIEW_ACTIVITY";

    public static final String SHARED_PREFERENCES_NAME = "search_dialog_preferences";

    private static final Integer BASE_ZOOM_LEVEL = 12;

    private static final int REQUEST_LOCATION = 1;

    private static final int CDE = 0;
    private static final int WIMS = 1;
    private static final int PERMIT = 2;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private FragmentManager mFragmentManager;
    private WIMSDataFragment mWIMSDataFragment;
    private CDEDataFragment mCDEDataFragment;
    private MyAreaFragment mMyAreaFragment;

    private ClusterManager<WIMSPoint> mWIMSClusterManager;
    private ClusterManager<DischargePermitPoint> mPermitClusterManager;
    private GeoJsonLayer mGeoJsonLayer;
    private List<CDEPoint> mCDEPoints = new ArrayList<>();
    private MultiListener mMultiListener = new MultiListener();

    private Drawer mDrawer;

    private ProgressBar mProgressSpinner;
    private ImageButton mMenuButton;
    private ImageButton mSearchButton;
    private FloatingActionButton mGpsButton;
    private TextView mLayerName;
    private Marker currentLocationMarker;
    private CheckBox doNotShowAgain;

    private MyArea myArea;
    private WIMSPoint selectedWIMSPoint;
    private CDEPoint selectedCDEPoint;
    private DischargePermitPoint selectedPermitPoint;

    private Snackbar connectionSnack;
    private Snackbar zoomSnack;
    private boolean wasConnectionSnackDisplayed;
    private boolean wasZoomSnackDisplayed;

    private int currentView;
    private boolean connectedToGooglePlayService;
    private int mMapCameraPadding;

    /**
     * A main activity showing a map and different data layers. Initiated by SplashActivity
     *
     * @see SplashActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mProgressSpinner = findViewById(R.id.progress_spinner);

        setupDrawer();
        setupSnackbars();

        // The action performed when the menu button is pressed.
        mMenuButton = findViewById(R.id.data_view_hamburger_button);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the Drawer
                mDrawer.openDrawer();
            }
        });

        mGpsButton = findViewById(R.id.gps_button);

        mLayerName = findViewById(R.id.layer_name);

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

        // Set mMapCameraPadding used when inside data views to move center of the screen down by
        // the third of the screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mMapCameraPadding = displayMetrics.heightPixels / 3;

        // Set a default view
        currentView = CDE;

        mSearchButton = findViewById(R.id.data_view_search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchDialog();
            }
        });
    }

    /**
     * Opens the given data view
     *
     * @param view View to be opened
     */
    public void openView(int view) {
        currentView = view;
        setMapOnCameraIdleListener(view);
        updateLayerName(view);
        // If zoomed in enough then load markers
        dismissSnackbars();
        addMarkersOrSnack(view);
    }

    /**
     * Closes the given data view
     *
     * @param view View to be closed
     */
    public void closeView(int view) {
        mMap.setOnCameraIdleListener(null);
        clearMarkers(view);
    }

    /**
     * Displays a dialog box informing user of the search conditions
     *
     */
    public void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View searchDialog = inflater.inflate(R.layout.search_dialog, null);

        doNotShowAgain = searchDialog.findViewById(R.id.dialog_do_not_show_again);

        builder.setView(searchDialog)
                .setTitle(R.string.dialog_title)
                .setIcon(R.drawable.ic_search);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Uses SharedPreferences to remember if user ticked doNotShowAgain box
                SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("skipMessage", doNotShowAgain.isChecked());
                editor.apply();

                openSearch();
            }
        });

        SharedPreferences settings = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
        Boolean skipMessage = settings.getBoolean("skipMessage", false);
        if (skipMessage.equals(false)) {
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            openSearch();
        }

    }

    /**
     * Opens an Autocomplete Search Fragment provided by the Google Places API
     */
    public void openSearch() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("GB")
                    .setTypeFilter(TYPE_FILTER_GEOCODE)
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

    /**
     * Makes a relevant API call depending on the provided view and loads markers returned by the API
     *
     * @param view View for which data is to be populated
     *
     * @see CDEPointAPI
     * @see WIMSPointAPI
     * @see DischargePermitPointAPI
     */
    public void loadMarkers(int view) {
        // If client has connection proceed with an API call, otherwise show a connectionSnack
        if (hasNetworkConnection()) {
            mProgressSpinner.setVisibility(View.VISIBLE);
            VisibleRegion screen = mMap.getProjection().getVisibleRegion();
            // Dismiss any opened snackbars
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
            if (!wasConnectionSnackDisplayed && !connectionSnack.isShown()) {
                connectionSnack.show();
            }
        }
    }

    /**
     * Clears all markers of provided view that are currently shown on the map
     *
     * @param view View whose markers are to be removed
     */
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

    /**
     * Sets an onCameraIdleListener to the map ensuring that the data is updated as user browses the map
     *
     * @param view View whose markers are to be updated onCameraIdle
     */
    public void setMapOnCameraIdleListener(int view) {
        final int view_params = view;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                addMarkersOrSnack(view_params);
            }
        });
    }

    /**
     * Adds markers to the map if zoom level condition is met. Otherwise it shows an error Snackbar
     *
     * @param view int layer name
     */
    public void addMarkersOrSnack(int view) {
        clearMarkers(view);
        if (mMap.getCameraPosition().zoom > BASE_ZOOM_LEVEL - 1) {
            loadMarkers(view);
        } else {
            if (!wasZoomSnackDisplayed && !zoomSnack.isShown() && hasNetworkConnection()) {
                zoomSnack.show();
            }
        }
    }

    /**
     * Updates the layer name located on the toolbar with the given view
     *
     * @param view View whose name is to be shown on the toolbar
     */
    public void updateLayerName(int view) {
        switch (view) {
            case CDE:
                mLayerName.setText(R.string.drawer_cde);
                break;
            case WIMS:
                mLayerName.setText(R.string.drawer_wims);
                break;
            case PERMIT:
                mLayerName.setText(R.string.drawer_permit);
                break;
        }
    }

    /**
     * Opens an InfoFragment
     *
     * @see InfoFragment
     */
    public void openInfoView() {
        // Hide the floating action buttons.
        displayHomeLayer(false);
        // Initiate the info fragment.
        InfoFragment fragment = new InfoFragment();
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null).commit();
    }

    /**
     * Runs through a series of checks and if all met it Opens a MyAreaFragment. Otherwise a
     * relevant snackbar is displayed on the screen
     *
     * @see MyAreaFragment
     */
    public void openMyAreaView() {
        if (!hasNetworkConnection()) {
            displaySimpleSnackbar("Internet Connection Required", Snackbar.LENGTH_SHORT);
            return;
        }
        if (!hasGPSOn(getApplicationContext())) {
            displaySimpleSnackbar("GPS Required", Snackbar.LENGTH_SHORT);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        if (connectedToGooglePlayService) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);
        }

        if(mLocation == null) {
            displaySimpleSnackbar("Failed to determine your exact location", Snackbar.LENGTH_SHORT);
            return;
        }

        displayHomeLayer(false);

        mProgressSpinner.setVisibility(View.VISIBLE);
        final Snackbar snack = Snackbar.make(findViewById(R.id.fragment_container),
                "Determining your exact location",
                Snackbar.LENGTH_INDEFINITE);
        snack.show();

        myArea = new MyArea();

        Log.d(TAG, "I should be opening myArea now");
        myArea.setOnPopulatedListener(new OnPopulated() {
            @Override
            public void onMyAreaPopulated() {
                Log.d(TAG, "My Area Populated!");
                Fragment fragment = new MyAreaFragment();
                mMyAreaFragment = (MyAreaFragment) fragment;

                mFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                        .add(R.id.fragment_container, mMyAreaFragment)
                        .addToBackStack(null).commit();
                Log.d(TAG, "GOGOGOGOG");
                mProgressSpinner.setVisibility(View.INVISIBLE);
                snack.dismiss();
            }
        });


        new MyAreaCDEAPI(this).execute(getCurrentLocation(), myArea);

    }

    /**
     * Called when the activity is no longer visible to the user.
     * Disconnects from the google api client.
     */
    @Override
    protected void onStop() {
        super.onStop();
//        if (connectedToGooglePlayService) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }
    }


    /**
     * Initializes GeoJsonLayer and sets onFeatureClickListener to open a CDEDataFragment
     *
     * @see CDEDataFragment
     */
    public void setUpCDEViewSingle() {
        mGeoJsonLayer = new GeoJsonLayer(mMap, new JSONObject());
        mGeoJsonLayer.setOnFeatureClickListener(new Layer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                setSelectedCDEPoint(feature);
                Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
                if (fragment == null) {
                    displayHomeLayer(false);

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

    /**
     * Initializes mWIMSClusterManager and sets onClusterItemClickListener to open a
     * WIMSDataFragment and onClusterClickListener to zoom in until markers are
     * unclustered
     *
     * @see WIMSDataFragment
     */
    public void setUpWIMSViewSingle() {
        mWIMSClusterManager = new ClusterManager<>(this, mMap);
        mWIMSClusterManager.setRenderer(new WIMSPointRenderer(this, mMap, mWIMSClusterManager));
        mWIMSClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<WIMSPoint>() {
            @Override
            public boolean onClusterItemClick(WIMSPoint point) {
                if (point.getTitle().equals("WIMS_Point")) {
                    selectedWIMSPoint = point;
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
                    if (fragment == null) {
                        displayHomeLayer(false);

                        LatLng markerPos = new LatLng(point.getLatitude(), point.getLongitude());
                        mMap.setPadding(0, mMapCameraPadding, 0, 0);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPos, mMap.getCameraPosition().zoom));
                        mMap.setOnCameraIdleListener(null);

                        mWIMSClusterManager.clearItems();
                        mWIMSClusterManager.addItem(point);
                        mWIMSClusterManager.cluster();

                        fragment = new WIMSDataFragment();
                        mWIMSDataFragment = (WIMSDataFragment) fragment;

                        new WIMSPointMeasurementsAPI(mWIMSDataFragment).execute(selectedWIMSPoint);
                        new WIMSPointMetalsAPI(mWIMSDataFragment).execute(selectedWIMSPoint);

                        openFragment(fragment, R.anim.slide_in_top, R.anim.slide_out_top);
                    }
                }
                return true;
            }
        });

        mWIMSClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<WIMSPoint>() {
            @Override
            public boolean onClusterClick(Cluster<WIMSPoint> cluster) {
                onClusterClickDefault(cluster);
                return true;
            }
        });
    }

    /**
     * Initializes mPermitClusterManager and sets onClusterItemClickListener to open a
     * DischargePermitDataFragment and onClusterClickListener to zoom in until markers are
     * unclustered
     *
     * @see DischargePermitDataFragment
     */
    public void setUpPermitViewSingle() {
        mPermitClusterManager = new ClusterManager<>(this, mMap);
        mPermitClusterManager.setRenderer(new DischargePermitPointRenderer(this, mMap, mPermitClusterManager));
        mPermitClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<DischargePermitPoint>() {
            @Override
            public boolean onClusterItemClick(DischargePermitPoint point) {
                if (point.getTitle().equals("Waste_Point")) {
                    selectedPermitPoint = point;
                    Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
                    if (fragment == null) {
                        displayHomeLayer(false);

                        LatLng markerPos = new LatLng(point.getLatitude(), point.getLongitude());
                        mMap.setPadding(0, mMapCameraPadding, 0, 0);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPos, mMap.getCameraPosition().zoom));
                        mMap.setOnCameraIdleListener(null);

                        mPermitClusterManager.clearItems();
                        mPermitClusterManager.addItem(point);
                        mPermitClusterManager.cluster();

                        fragment = new DischargePermitDataFragment();
                        openFragment(fragment, R.anim.slide_in_top, R.anim.slide_out_top);
                    }
                }
                return true;
            }
        });
        mPermitClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<DischargePermitPoint>() {
            @Override
            public boolean onClusterClick(Cluster<DischargePermitPoint> cluster) {
                onClusterClickDefault(cluster);
                return true;
            }
        });
    }

    /**
     * Makes a maximum zoom onto the cluster keeping all ClusterItems inside the view port
     *
     * @param cluster Cluster
     */
    public void onClusterClickDefault(Cluster cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (Object item : cluster.getItems()) {
            builder.include(((ClusterItem) item).getPosition());
        }
        final LatLngBounds bounds = builder.build();
        mMap.setPadding(0, 0, 0, 0);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    /**
     * Opens a CDEDetailsFragment
     *
     * @see CDEDetailsFragment
     */
    public void openCDEDetailsFragment() {
        Fragment fragment = new CDEDetailsFragment();
        openFragment(fragment, R.anim.slide_in_right, R.anim.slide_out_right);
    }

    /**
     * Opens a WIMSDetailsFragment
     *
     * @see WIMSDetailsFragment
     */
    public void openWIMSDetailsFragment() {
        Fragment fragment = new WIMSDetailsFragment();
        openFragment(fragment, R.anim.slide_in_right, R.anim.slide_out_right);
    }

    /**
     * Opens a given fragment
     *
     * @param fragment Fragment to be opened
     * @param animationIn Open animation
     * @param animationOut Close animation
     */
    public void openFragment(Fragment fragment, int animationIn, int animationOut) {
        mFragmentManager.beginTransaction()
                .setCustomAnimations(animationIn, 0, 0, animationOut)
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Called when search result is selected in Google's Autocomplete Search Fragment.
     * Updates map camera to focus on the selected location
     *
     * @param requestCode int request code
     * @param resultCode int result code
     * @param data Intent data
     */
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

    /**
     * Called when the map is created. Manipulates the map style, sets the default view on the map
     * of UK and initialises data managers
     *
     * @param googleMap GoogleMap
     */
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

    /**
     * Initialises mMultiListener
     *
     * @see MultiListener
     */
    public void setUpMultiManager() {
        setUpWIMSViewSingle();
        setUpPermitViewSingle();
        setUpCDEViewSingle();

        mMultiListener.addOC(mWIMSClusterManager);
        mMultiListener.addOC(mPermitClusterManager);

        mMultiListener.addOM(mWIMSClusterManager);
        mMultiListener.addOM(mPermitClusterManager);

        mMap.setOnMarkerClickListener(mMultiListener);
        mMap.setOnCameraIdleListener(mMultiListener);
    }

    /**
     * Populates the map with WIMSPoints returned by WIMSPointAPI
     *
     * @param result List<WIMSPoint> created after an API call in WIMSPointAPI
     *
     * @see WIMSPointAPI
     */
    @Override
    public void onTaskCompletedWIMSPoint(List<WIMSPoint> result) {
        mWIMSClusterManager.addItems(result);
        mWIMSClusterManager.cluster();
    }

    /**
     * Populates the map with CDEPoints returned by CDEPointAPI
     *
     * @param result List<CDEPoint> created after an API call in CDEPointAPI
     *
     * @see CDEPointAPI
     */
    @Override
    public void onTaskCompletedCDEPoint(List<CDEPoint> result) {
        mCDEPoints = result;
        for (CDEPoint r : result) {
            boolean isOnMap = false;
            for (GeoJsonFeature feature : mGeoJsonLayer.getFeatures()) {
                if (feature.equals(r.getRiverPolygon())) isOnMap = true;
            }
            if (!isOnMap) {
                r.getRiverPolygon().setPolygonStyle(GeoJsonStyles.geoJsonPolygonStyle());
                mGeoJsonLayer.addFeature(r.getRiverPolygon());
            }
        }
        mGeoJsonLayer.addLayerToMap();
    }

    /**
     * Displays a CDEPoint river line returned by CDERiverLineAPI on top of the catchment GeoJsonFeature
     *
     * @param result CDEPoint containing a river line obtained from an API call in CDERiverLineAPI
     * @see CDERiverLineAPI
     */
    @Override
    public void onTaskCompletedCDERiverLine(CDEPoint result) {
        result.getRiverLine().setLineStringStyle(GeoJsonStyles.geoJsonLineStringStyle());
        mGeoJsonLayer.addFeature(result.getRiverLine());
        mGeoJsonLayer.addLayerToMap();
    }

    /**
     * Populates the map with DischargePermiPoints returned by DischargePermitPointAPI
     *
     * @param result List<DischargePermitPoint> created after an API call in DischargePermitPointAPI
     * @see DischargePermitPointAPI
     */
    @Override
    public void onTaskCompletedDischargePermitPoint(List<DischargePermitPoint> result) {
        mPermitClusterManager.addItems(result);
        mPermitClusterManager.cluster();
    }

    /**
     * Updates the myArea.wimsPoint with the result returned from MyAreaNearestWIMSAPI
     *
     * @param result WIMSPoint created after an API call in MyAreaNearestWIMSAPI
     * @see MyAreaNearestWIMSAPI
     */
    @Override
    public void onTaskCompletedMyAreaWIMS(WIMSPoint result) {
        myArea.setWimsPoint(result);
    }

    /**
     * Updates the myArea.permitPoint with the result returned from MyAreaNearestPermitAPI
     *
     * @param result DischargePermitPoint created after an API call in MyAreaNearestPermitAPI
     * @see MyAreaNearestPermitAPI
     */
    @Override
    public void onTaskCompletedMyAreaPermit(DischargePermitPoint result) {
        myArea.setPermitPoint(result);
    }

    /**
     * Manages API calls required to populate myArea
     *
     * @see MyAreaCDEAPI
     */
    @Override
    public void onTaskCompletedMyAreaCDE() {
        if(myArea.getWaterbody() != null) {
            myArea.setHasWaterbody(true);
            new MyAreaCatchmentsAPI().execute(myArea);
        } else {
            myArea.setHasWaterbody(false);
        }
        new MyAreaNearestWIMSAPI(this).execute(getCurrentLocation());
        new MyAreaNearestPermitAPI(this).execute(getCurrentLocation());
    }

    /**
     * Requests location permissions from the user and if available zooms in to the current location.
     * Called when connection established with Google Play Service Location API
     *
     * @param bundle Bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        connectedToGooglePlayService = true;
        if (!hasGPSOn(getApplicationContext())) {
            openView(currentView);
        } else {
            displayLocation();
            mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    if (mMap.getCameraPosition().zoom < BASE_ZOOM_LEVEL - 1) {
                        displayLocation();
                        openView(currentView);
                    } else {
                        openView(currentView);
                        // Doesn't work without return, sorry Mike
                        return;
                    }
                }
            });
        }
    }

    /**
     * Displays users current location on the map
     */
    public void displayLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(false);
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation == null) {
                Log.e(TAG, "mLocation was null");
            } else {
                setLocationMarker(mLocation.getLatitude(), mLocation.getLongitude());
                updateMapCameraPositionToCurrentLocation();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
    }

    /**
     * Updates map camera position to the clients current location
     */
    public void updateMapCameraPositionToCurrentLocation() {
        if (mMap != null && mLocation != null) {
            final CameraPosition newCameraPosition = new CameraPosition.Builder().zoom(BASE_ZOOM_LEVEL)
                    .target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
        }
    }

    /**
     * Creates location request
     * @return LocationRequest locationRequest
     */
    public LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    /**
     * Updates the position of the currentLocationMarker
     *
     * @param latitude double latitude of new location
     * @param longitude double longitude of new location
     */
    public void setLocationMarker(double latitude, double longitude) {
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_target_black)));
        currentLocationMarker.setTag("Current Location");
    }

    /**
     * Called when clients location has changed. Updates the position of the currentLocationMarker
     * @param location Location new clients location
     */
    @Override
    public void onLocationChanged(Location location) {
        setLocationMarker(location.getLatitude(), location.getLongitude());
    }

    /**
     * Displays exact users location on the map.
     * Called when mGPSButton is pressed
     *
     * @param v View
     */
    public void currentLocation(View v) {
        if (hasGPSOn(v.getContext())) {
            if (!connectedToGooglePlayService) {
                mGoogleApiClient.connect();
            } else {
                displayLocation();
            }
        } else {
            displaySimpleSnackbar("GPS Required", Snackbar.LENGTH_SHORT);
        }
    }

    /**
     * Requests permission for location information at runtime. Need for devices running Android 6 upwards
     *
     * @param requestCode request code
     * @param permissions permission
     * @param grantResults grant results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_LOCATION) {
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // We can now safely use the API we requested access to
            Log.i(TAG, "Location request allowed");
            connectedToGooglePlayService = true;
            displayLocation();
        } else {
            connectedToGooglePlayService = false;
        }
    }

    /**
     * Called when user is temporarily in a disconnected state.
     *
     * @param i cause
     */
    @Override
    public void onConnectionSuspended(int i) {
        connectedToGooglePlayService = false;
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Called when there is an error connecting the client to the service
     *
     * @param connectionResult connection result
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection Failed");
        displaySimpleSnackbar("Location Connection Failed", Snackbar.LENGTH_SHORT);
        connectedToGooglePlayService = false;
        mGoogleApiClient.connect();
    }

    /**
     * Closes CDEDataFragment by removing the fragment and calling closeDataFragment
     *
     * @param fragment Fragment to be removed
     */
    public void closeCDEDataFragment(Fragment fragment) {
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_top, 0, 0, R.anim.slide_out_top)
                .remove(fragment)
                .commit();
        closeDataFragment(CDE, false);
    }

    /**
     * Closes given view's data fragment by clearing the markers, opening that view as a layer in
     * DataViewActivity and resetting map padding and home buttons.
     *
     * @param view view which data fragment is to be closed
     * @param popBackStack if true fragment manager's back stack is popped
     */
    public void closeDataFragment(int view, boolean popBackStack) {
        if(popBackStack) mFragmentManager.popBackStack();
        clearMarkers(view);
        openView(view);
        mProgressSpinner.setVisibility(View.INVISIBLE);
        mMap.setPadding(0, 0, 0, 0);
        displayHomeLayer(true);
    }

    /**
     * Handles Back Button Presses
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = mFragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment instanceof WIMSDataFragment) {
            closeDataFragment(WIMS, true);
        } else if (fragment instanceof CDEDataFragment) {
            closeCDEDataFragment(fragment);
        } else if (fragment instanceof CDEDetailsFragment) {
            mFragmentManager.popBackStack();
        } else if (fragment instanceof WIMSDetailsFragment) {
            mFragmentManager.popBackStack();
        } else if (fragment instanceof DischargePermitDataFragment) {
            closeDataFragment(PERMIT, true);
        } else if (fragment instanceof InfoFragment) {
            mFragmentManager.popBackStack();
            displayHomeLayer(true);
        } else if (fragment instanceof MyAreaFragment) {
            mFragmentManager.popBackStack();
            displayHomeLayer(true);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Based on the condition hides or shows home buttons and locks or unlocks the drawer
     *
     * @param condition condition
     */
    public void displayHomeLayer(boolean condition) {
        if (condition) {
            mGpsButton.show();
            mSearchButton.setVisibility(View.VISIBLE);
            mMenuButton.setVisibility(View.VISIBLE);
            mLayerName.setVisibility(View.VISIBLE);
            mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mGpsButton.hide();
            mMenuButton.setVisibility(View.INVISIBLE);
            mSearchButton.setVisibility(View.INVISIBLE);
            mLayerName.setVisibility(View.INVISIBLE);
            mDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    /**
     * Returns a selected WIMSPoint
     * @return WIMSPoint selectedWIMSPoint
     */
    public WIMSPoint getSelectedWIMSPoint() {
        return selectedWIMSPoint;
    }

    /**
     * Returns a selected DischargePermitPoint
     * @return DischargePermitPoint selectedPermitPoint
     */
    public DischargePermitPoint getSelectedPermitPoint() {
        return selectedPermitPoint;
    }

    /**
     * Returns a selected CDEPoint
     * @return CDEPoint selectedCDEPoint
     */
    public CDEPoint getSelectedCDEPoint() {
        return selectedCDEPoint;
    }

    /**
     * Sets a selected CDEPoint
     * @param feature Feature of cdePoint
     */
    public void setSelectedCDEPoint(Feature feature) {
        for (CDEPoint cdePoint : mCDEPoints) {
            if (cdePoint.getRiverPolygon() == feature) {
                selectedCDEPoint = cdePoint;
            }
        }
    }

    /**
     * Returns a progress spinner
     * @return ProgressBar mProgressSpinner
     */
    public ProgressBar getProgressSpinner() {
        return mProgressSpinner;
    }

    /**
     * Returns true if client has network connection and false otherwise
     *
     * @return boolean hasNetworkConnection
     */
    public boolean hasNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connectedToGooglePlayService to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connectedToGooglePlayService to wifi
                haveConnectedWifi = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connectedToGooglePlayService to the mobile provider's data plan
                haveConnectedMobile = true;
            }
        }

        return haveConnectedWifi || haveConnectedMobile;

    }

    /**
     * Returns true if client has GPS turned ON and false otherwise
     *
     * @return boolean hasGPSOn
     */
    public boolean hasGPSOn(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Displays a Snackbar displaying given message for the given amount of time
     *
     * @param message String to be shown by the snackbar
     * @param length Integer length of time for snackbar to be shown
     */
    private void displaySimpleSnackbar(String message, Integer length) {
        Snackbar snack = Snackbar.make(findViewById(R.id.fragment_container), message, length);
        snack.show();
    }

    /**
     * Returns a dismissible Snackbar displaying given message for the given amount of time
     *
     * @param message String to be shown by the snackbar
     * @param length Integer length of time for snackbar to be shown
     * @return Snackbar
     */
    public Snackbar dismissibleSnackbar(String message, Integer length) {
        Snackbar snack = Snackbar.make(findViewById(R.id.fragment_container), message, length);
        snack.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return snack;
    }

    /**
     * Initialises connectionSnack and zoomSnack
     */
    private void setupSnackbars() {
        wasConnectionSnackDisplayed = false;
        wasZoomSnackDisplayed = false;

        connectionSnack = dismissibleSnackbar("Data retrieval needs internet connection", Snackbar.LENGTH_INDEFINITE);
        connectionSnack.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                wasConnectionSnackDisplayed = true;
            }
        });

        zoomSnack = dismissibleSnackbar("Zoom in to show data points", Snackbar.LENGTH_INDEFINITE);
        zoomSnack.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                wasZoomSnackDisplayed = true;
            }
        });
    }

    /**
     * Dismisses both connectionSnack and zoomSnack if shown
     */
    private void dismissSnackbars() {
        wasConnectionSnackDisplayed = false;
        wasZoomSnackDisplayed = false;
        if (connectionSnack.isShown()) connectionSnack.dismiss();
        if (zoomSnack.isShown()) zoomSnack.dismiss();
    }

    /**
     * Returns current client location
     *
     * @return Location mLocation
     */
    public Location getCurrentLocation() {
        return mLocation;
    }

    /**
     * Returns myArea
     *
     * @return MyArea myArea
     */
    public MyArea getMyArea() {
        return myArea;
    }

    /**
     * Closes current data view, opens data view to which given marker belongs and zooms in to it
     * @param point Object marker
     */
    public void setCameraFocusOnMarker(Point point) {
        LatLng position = null;
        onBackPressed();
        closeView(currentView);
        if (point instanceof WIMSPoint) {
            openView(WIMS);
            position = new LatLng(((WIMSPoint) point).getLatitude(), ((WIMSPoint) point).getLongitude());
        } else if (point instanceof DischargePermitPoint) {
            openView(PERMIT);
            position = new LatLng(((DischargePermitPoint) point).getLatitude(), ((DischargePermitPoint) point).getLongitude());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

    /**
     * Initialises the drawer
     */
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

        final PrimaryDrawerItem drawerWIMS =
                newPrimaryDrawerItem(1, R.string.drawer_wims, R.drawable.ic_wims_marker);

        final PrimaryDrawerItem drawerCDE =
                newPrimaryDrawerItem(2, R.string.drawer_cde, R.drawable.ic_cde_marker);

        final PrimaryDrawerItem drawerPermit =
                newPrimaryDrawerItem(3, R.string.drawer_permit, R.drawable.ic_permit_marker);

        final SecondaryDrawerItem drawerMyArea =
                newSecondaryDrawerItem(4, R.string.drawer_my_area, R.drawable.ic_where_am_i);

        final SecondaryDrawerItem drawerInfo =
                newSecondaryDrawerItem(5, R.string.drawer_info, R.drawable.info_white_no_padding);

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
                        switch ((int) drawerItem.getIdentifier()) {
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
                                openMyAreaView();
                                break;
                            case 5:
                                openInfoView();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                })
                .withStickyFooterShadow(false)
                .withStickyFooter(R.layout.drawer_footer)
                .build();
    }

    /**
     * Creates a PrimaryDrawerItem from given parameters
     *
     * @param identifier int identifier
     * @param name int name resource
     * @param icon int icon resource
     *
     * @return PrimaryDrawerItem
     */
    private PrimaryDrawerItem newPrimaryDrawerItem(int identifier, int name, int icon) {
        return new PrimaryDrawerItem()
                .withIdentifier(identifier)
                .withName(name)
                .withSelectedColorRes(R.color.colorPrimaryDark)
                .withSelectedTextColorRes(R.color.colorTextPrimary)
                .withTextColorRes(R.color.colorTextPrimary)
                .withIcon(icon);
    }

    /**
     * Creates a SecondaryDrawerItem from given parameters
     *
     * @param identifier int identifier
     * @param name int name resource
     * @param icon int icon resource
     *
     * @return SecondaryDrawerItem
     */
    private SecondaryDrawerItem newSecondaryDrawerItem(int identifier, int name, int icon) {
        return new SecondaryDrawerItem()
                .withIdentifier(identifier)
                .withName(name)
                .withTextColorRes(R.color.colorTextPrimary)
                .withIcon(icon)
                .withSelectable(false);
    }

}