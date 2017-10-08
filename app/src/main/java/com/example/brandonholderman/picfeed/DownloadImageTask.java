package com.example.brandonholderman.picfeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brandonholderman on 10/7/17.
 */

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
    private static Map<String, Bitmap> cache = new HashMap<>();
    private Context mContext;
    private String mUrl;
    private ImageView mView;

    public DownloadImageTask(Context context, String url, ImageView view) {
        mContext = context;
        mUrl = url;
        mView = view;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            if (cache.containsKey(mUrl)) {
                return cache.get(mUrl);
            }

            InputStream stream = new URL(mUrl).openConnection().getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(stream);

            cache.put(mUrl, bitmap);

            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mView.setImageBitmap(bitmap);
    }
}

