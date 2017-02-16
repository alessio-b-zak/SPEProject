package com.bitbusters.android.speproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.provider.MediaStore;
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

public class PhotoCommentActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String BITMAP_TAG = "BITMAP";
    private Bitmap imageTaken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        setContentView(R.layout.activity_photo_comment);
        EditText sometext = (EditText)findViewById(R.id.editText);
        setUpEditText(sometext);
        setUpSpinner();

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
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setPrompt("Set tag");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tags, R.layout.spinner_format);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void backClick(View v){
        onBackPressed();
    }

    public void submitClick(View v){
        onBackPressed();
        com.bitbusters.android.speproject.Image image = new com.bitbusters.android.speproject.Image(imageTaken,52.231,2.01,"Pollution over here!!!");
        new ImageUploader().execute(image);
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
