package com.scilm_k.twitter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.scilm_k.twitter.Config;
import com.scilm_k.twitter.PrefKeys;
import com.scilm_k.twitter.R;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtil {
    public static Twitter getTwitterInstance(Context context){
        TwitterFactory factory = new TwitterFactory();
        Twitter twitter = factory.getInstance();

        twitter.setOAuthConsumer(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret));

        if(hasAccessToken(context)){
            twitter.setOAuthAccessToken(loadAccessToken(context));
        }

        return twitter;
    }

    public static void storeAccessToken(Context context, AccessToken accessToken){
        SharedPreferences preferences = context.getSharedPreferences(PrefKeys.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PrefKeys.TOKEN, accessToken.getToken());
        editor.putString(PrefKeys.TOKEN_SECRET, accessToken.getTokenSecret());
        editor.commit();
    }

    private static AccessToken loadAccessToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PrefKeys.PREFERENCE_NAME, Context.MODE_PRIVATE);

        String token = preferences.getString(PrefKeys.TOKEN, null);
        String tokenSecret = preferences.getString(PrefKeys.TOKEN_SECRET, null);

        if(token == null || tokenSecret == null){
            return null;
        }

        return new AccessToken(token, tokenSecret);
    }

    public static boolean hasAccessToken(Context context) {
        return loadAccessToken(context) != null;
    }
}
