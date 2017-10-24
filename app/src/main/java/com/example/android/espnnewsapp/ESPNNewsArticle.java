package com.example.android.espnnewsapp;

import android.graphics.drawable.Drawable;

/**
 * Created by Peter on 6/16/2017.
 */

public class ESPNNewsArticle {
    private String title;
    private String description;
    private Drawable mImageResourceId;

    public ESPNNewsArticle(String title, String description, Drawable imageResourceId) {
        this.title = title;
        this.description = description;
        this.mImageResourceId = imageResourceId;
    }

    public String getTitle () {
        return title;
    }

    public String getDescription () {
        return description;
    }

    public Drawable getmImageResourceId() {
        return mImageResourceId;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public void setImageID(Drawable idURL) {

        this.mImageResourceId = idURL;
    }
}
