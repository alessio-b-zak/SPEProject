package com.bitbusters.android.speproject;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Image;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class PhotoCommentActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String BITMAP_TAG = "BITMAP";
    private Bitmap imageTaken;
    private EditText sometext;
    private Spinner spinner;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            com.bitbusters.android.speproject.Image image = new com.bitbusters.android.speproject.Image("NoId",
                                imageTaken, mLocation.getLatitude(), mLocation.getLongitude(), sometext.getText().toString(), (PhotoTag)spinner.getSelectedItem());
            Log.e(String.valueOf(mLocation.getLongitude()), String.valueOf(mLocation.getLatitude()));
            new ImageUploader().execute(image);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGoogleApiClient.connect();
            } else {
                //TODO: SHOULD A PICTURE STILL BE SUBMITED WITH DUMMY LOCATION
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"Could not find location", Toast.LENGTH_LONG);

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"Location disconnected", Toast.LENGTH_SHORT);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null && savedInstanceState == null)
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        setContentView(R.layout.activity_photo_comment);
        sometext = (EditText)findViewById(R.id.editText);
        setUpEditText(sometext);
        setUpSpinner();

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected  void setUpEditText(final EditText sometext){
        sometext.setHint("Add a comment");
        sometext.clearFocus();
        sometext.setMaxHeight(sometext.getHeight());
        sometext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    sometext.setHint("");
                }
            }
        });
    }

    protected void setUpSpinner(){
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt("Set tag");
        ArrayAdapter<PhotoTag> adapter = new ArrayAdapter<>(this, R.layout.spinner_format, PhotoTag.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void backClick(View v){
        onBackPressed();
    }

    public void submitClick(View v){
        mGoogleApiClient.connect();
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){

            Bundle extras = data.getExtras();
            imageTaken = (Bitmap) extras.get("data");
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),imageId);

            LinearLayout rlayout = (LinearLayout) findViewById(R.id.activity_photo_comment);
            ImageView image;
            image = (ImageView) findViewById(R.id.imageView2);
            image.setImageBitmap(imageTaken);

        }else{
            finish();
        }
    }

}
