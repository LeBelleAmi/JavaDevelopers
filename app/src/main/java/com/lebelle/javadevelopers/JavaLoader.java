package com.lebelle.javadevelopers;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of javaDevelopers by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class JavaLoader extends AsyncTaskLoader<List<JavaDevelopers>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = JavaLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link JavaLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public JavaLoader(Context context, String url) {
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
    public List<JavaDevelopers> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of javaDevelopers.
        List<JavaDevelopers> javaDevelopers = QueryUtils.fetchJavaDevelopersData(mUrl);
        return javaDevelopers;

    }
}
