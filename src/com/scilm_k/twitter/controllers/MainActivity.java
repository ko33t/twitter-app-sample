package com.scilm_k.twitter.controllers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.scilm_k.twitter.Config;
import com.scilm_k.twitter.R;
import com.scilm_k.twitter.utils.TwitterUtil;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.List;

public class MainActivity extends FragmentActivity {
    private Twitter twitter;
    private ListView timeline;
    private TimelineAdapter adapter;

    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);


        queue = Volley.newRequestQueue(getApplicationContext());

        ImageLoader imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            @Override
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
        });

        timeline = (ListView) findViewById(R.id.main_timeline);
        adapter = new TimelineAdapter(this, queue, imageLoader);
        timeline.setAdapter(adapter);

        twitter = TwitterUtil.getTwitterInstance(this);
        reloadTimeline();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(queue == null){
            queue = Volley.newRequestQueue(getApplicationContext());
        }else {
            queue.start();
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        queue.cancelAll(Config.TAG);
        queue.stop();
    }

    private void reloadTimeline() {
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try {
                    return twitter.getHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<twitter4j.Status> statuses) {
                if(statuses == null){
                    // 失敗
                    return;
                }

                adapter.clear();

                adapter.addAll(statuses);
                timeline.setSelection(0);
            }
        };

        task.execute();
    }
}
