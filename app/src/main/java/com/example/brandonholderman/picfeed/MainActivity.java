package com.example.brandonholderman.picfeed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final int REQUEST_PICK_PICTURE = 2;

    private Button mTakePictureButton;
    private Button mPickPictureButton;
    private ImageView mImageResult;
    private String mCurrentPhotoPath;
    private TextView mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageResult = (ImageView) findViewById(R.id.image_result);
        mInfo = (TextView) findViewById(R.id.error_text);
        mTakePictureButton = (Button) findViewById(R.id.take_picture);

        attachClickHandlers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        setPictureFromFile();

//        Bundle extras = data.getExtras();
//        Bitmap imageBitmap = (Bitmap) extras.get("data");
//        mImageResult.setImageBitmap(imageBitmap);
    }

    private void attachClickHandlers() {
        mTakePictureButton = (Button) findViewById(R.id.take_picture);
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 0);

            try {
                File imageFile = createImageFile();
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.brandonholderman.picfeed", imageFile);
                getIntent().putExtra(MediaStore.EXTRA_OUTPUT, photoUri);


            } catch(IOException e) {
                String error = "Error: " + e.getMessage();
                Log.d(TAG, "onCreate: " + error);
                mInfo.setText(error);
            }
        }
    }

    private void pickPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_PICTURE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG " + timeStamp + " ";

        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(fileName, ".jpg", directory);

        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void setPictureFromFile() {
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mImageResult.setImageBitmap(bitmap);
    }
}
