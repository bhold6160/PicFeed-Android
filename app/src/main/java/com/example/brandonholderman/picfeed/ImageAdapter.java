package com.example.brandonholderman.picfeed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by brandonholderman on 10/7/17.
 */

public class ImageAdapter extends ArrayAdapter<String> {
    Context mContext;
    List<String> mUrls;
    int mLayoutId;

    public ImageAdapter(Context context, int resource, List<String> urls) {
        super(context, resource, urls);
        mContext = context;
        mLayoutId = resource;
        mUrls = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        }

        ImageView image = convertView.findViewById(R.id.download_image);
        String url = mUrls.get(position);

        (new DownloadImageTask(mContext, url, image)).execute();

        return convertView;
    }
}
