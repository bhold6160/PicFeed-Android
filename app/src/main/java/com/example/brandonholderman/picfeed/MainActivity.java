package com.example.brandonholderman.picfeed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final int REQUEST_IMAGE_CAPTURE =1;
    private final int REQUEST_PICK_PICTURE = 2;

    private Button mTakePictureButton;
    private Button mPickPictureButton;
    private Button mDownloadPictureButton;
    private ImageView mImageResult;
    private String mCurrentPhotoPath;
    private TextView mInfo;

    private ListView mImageList;
    private ImageAdapter mAdapter;
    private List<String> mImageUrls;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageResult = (ImageView) findViewById(R.id.image_result);
        mInfo = (TextView) findViewById(R.id.info);
        mTakePictureButton = (Button) findViewById(R.id.take_picture);
        mPickPictureButton = (Button) findViewById(R.id.pick_picture);
        mDownloadPictureButton = (Button) findViewById(R.id.download_button);

        mImageUrls = Database.allImages;
        mImageList = (ListView) findViewById(R.id.image_list);
        mAdapter = new ImageAdapter(this, R.layout.image_item, mImageUrls);
        mImageList.setAdapter(mAdapter);

        attachClickHandlers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {

            setPictureFromFile();
            addPicToGallery();
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_PICTURE) {
            setPictureFromGallery(data);
        }


//        Bundle extras = data.getExtras();
//        Bitmap imageBitmap = (Bitmap) extras.get("data");
//        mImageResult.setImageBitmap(imageBitmap);
    }

    private void attachClickHandlers() {
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        mPickPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPicture();
            }
        });

        mDownloadPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadPicture();
            }
        });
    }

    private void takePicture() {

            try {
                File imageFile = createImageFile();
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.brandonholderman.picfeed", imageFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getIntent().putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startActivityForResult(intent, 1);
            } catch(IOException e) {
                String error = "Error: " + e.getMessage();
                Log.d(TAG, "onCreate: " + error);
                mInfo.setText(error);
            }
        }


    private void pickPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_PICTURE);
    }

    private void downloadPicture() {
        String url = Database.image1;
        (new DownloadImageTask(this, url, mImageResult)).execute();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";

        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(fileName, ".jpg", directory);

        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    private void addPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPictureFromFile() {
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mImageResult.setImageBitmap(bitmap);
    }

    private void setPictureFromGallery(Intent data) {
        try {
            InputStream stream = this.getContentResolver().openInputStream(data.getData());
            Bitmap galleryPicture = BitmapFactory.decodeStream(stream);
            mImageResult.setImageBitmap(galleryPicture);
        } catch(FileNotFoundException e) {
            mInfo.setText("Error: " + e.getMessage());
        }
    }
}
