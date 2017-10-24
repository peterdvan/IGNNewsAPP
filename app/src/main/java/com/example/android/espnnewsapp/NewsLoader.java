package com.example.android.espnnewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.io.IOException;
import java.util.List;
/**
 * Created by Peter on 6/18/2017.
 */

public class NewsLoader extends AsyncTaskLoader<List<ESPNNewsArticle>>{
    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<ESPNNewsArticle> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        String JSONData = null;
        try {
            JSONData = MainActivity.makeHttpRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<ESPNNewsArticle> newArticles = MainActivity.extractFeatureFromJson(JSONData);
        return newArticles;
    }
}
