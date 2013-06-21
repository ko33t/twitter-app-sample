package com.scilm_k.twitter.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.scilm_k.twitter.R;
import com.scilm_k.twitter.utils.TwitterUtil;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOAuthActivity extends Activity {
    private String callbackURL;
    private Twitter twitter;
    private RequestToken requestToken;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);

        callbackURL = "scilmk://twitter";
        twitter = TwitterUtil.getTwitterInstance(this);

        Button button = (Button) findViewById(R.id.launch_authorize);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuthorize();
            }
        });
    }

    private void startAuthorize() {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {


                    requestToken = twitter.getOAuthRequestToken(callbackURL);
                    return requestToken.getAuthenticationURL();
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if(s != null){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    startActivity(intent);
                }
            }
        };

        task.execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent == null || intent.getData() == null ||
                !intent.getData().toString().startsWith(callbackURL)){
            return;
        }

        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try{
                    return twitter.getOAuthAccessToken(requestToken, params[0]);
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken) {
                if(accessToken != null){
                    successOAuth(accessToken);
                }

                // 失敗
            }
        };

        task.execute(verifier);
    }


    private void successOAuth(AccessToken accessToken){
        TwitterUtil.storeAccessToken(this, accessToken);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
