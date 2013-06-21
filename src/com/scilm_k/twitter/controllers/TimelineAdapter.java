package com.scilm_k.twitter.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.scilm_k.twitter.Config;
import com.scilm_k.twitter.R;
import org.json.JSONObject;
import twitter4j.Status;

import java.util.List;

public class TimelineAdapter extends ArrayAdapter<Status>{
    private LayoutInflater inflater;
    private RequestQueue queue;
    private ImageLoader imageLoader;

    public TimelineAdapter(Context context, RequestQueue queue, ImageLoader imageLoader) {
        super(context, 0);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.queue = queue;
        this.imageLoader = imageLoader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.timeline_row, null);
        }

        TextView userName = (TextView) convertView.findViewById(R.id.timeline_user_name);
        TextView screenName = (TextView) convertView.findViewById(R.id.timeline_screen_name);
        NetworkImageView userImage = (NetworkImageView) convertView.findViewById(R.id.timeline_user_image);
        TextView tweet = (TextView) convertView.findViewById(R.id.timeline_tweet);

        Status status = getItem(position);

        userName.setText(status.getUser().getName());
        screenName.setText(status.getUser().getScreenName());
        tweet.setText(status.getText());

        userImage.setTag(Config.TAG);
        userImage.setImageUrl(status.getUser().getProfileImageURL(), imageLoader);


        return convertView;
    }
}
