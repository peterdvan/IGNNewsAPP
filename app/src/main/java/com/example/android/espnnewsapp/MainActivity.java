package com.example.android.espnnewsapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{

    /** Adapter for the list of earthquakes */
    private NewsArrayAdapter mAdapter;

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;
    private static final String NEWS_URL = "https://newsapi.org/v1/articles?source=ign&sortBy=latest&apiKey=d96be4d4a4064aadba3523cb16241658";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(1, null, this);
        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);


        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new NewsArrayAdapter(this, new ArrayList<ESPNNewsArticle>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);
    }

    @Override
    public Loader<List<ESPNNewsArticle>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsLoader(this, NEWS_URL);
    }


    @Override
    public void onLoadFinished(Loader loader, Object data) {
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        ArrayList<ESPNNewsArticle> newsArticles = (ArrayList<ESPNNewsArticle>) data;
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsArticles != null && !newsArticles.isEmpty()) {
            mAdapter.addAll(newsArticles);
        }
    }


    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.clear();
    }

    public static String makeHttpRequest() throws IOException {
        String jsonResponse = "";
        URL url = null;
        try {
            url = new URL(NEWS_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("MAIN", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("MAIN", "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                    inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    public static List<ESPNNewsArticle> extractFeatureFromJson(String JSONString) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(JSONString)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<ESPNNewsArticle> newsArticles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(JSONString);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            JSONArray newsArray = baseJsonResponse.getJSONArray("articles");

            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentArticle = newsArray.getJSONObject(i);


                String description = currentArticle.getString("description");

                String title = currentArticle.getString("title");

                String imageURL = currentArticle.getString("urlToImage");
                Drawable image;
                try {
                    InputStream is = (InputStream) new URL(imageURL).getContent();
                    image = Drawable.createFromStream(is, "src name");
                } catch (Exception e) {
                    return null;
                }
                // Create a new {@link Earthquake} object with the magnitude, location, time,
                // and url from the JSON response.
                ESPNNewsArticle earthquake = new ESPNNewsArticle(title, description, image);

                // Add the new {@link Earthquake} to the list of earthquakes.
                newsArticles.add(earthquake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return newsArticles;
    }
}
