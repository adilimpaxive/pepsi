package com.pepsi.battleofthebands.networkcalls;


import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.util.LinkedList;

/**
 * A Singleton class that contains all the webservices and constants that will
 * be used by the app.This is done using volley. Right now Volley is being
 * referenced from libs directory in instabug.
 *
 * @author Muhammad Kashan
 */
public class WebServices {
    private static final String TAG = "WebServices";


    public enum Response {

    }

    private static WebServices instance;
    private static RequestQueue requestQueue;

    private WebServices() {
    }

    public static WebServices getInstance(Context context) {
        if (instance == null) {
            instance = new WebServices();
            requestQueue = Volley.newRequestQueue(context);

        }
        return instance;
    }

    /**
     * This is used for generating complete URL for GET Request by appending
     * UrlEncoded list of params at the end of the URL
     *
     * @param url    to append list to
     * @param params list of params
     * @return url having parameters appended at the end
     */
    private String getParameterEmbeddedUrl(String url, LinkedList<NameValuePair> params) {
        if (!url.endsWith("?"))
            url += "?";
        // For encoding complete Url using a coding scheme
        String paramString = URLEncodedUtils.format(params, "UTF-8");
        // Used For Replacing created_at%2Basc with created_at+asc
        paramString = paramString.replace("%2B", "+");
        url += paramString;
        return url;

    }

    /**
     * Cancel all requests depending upon the parameter provided
     *
     * @param RequestName name of request
     */
    public synchronized void cancelRequests(String RequestName) {
        requestQueue.cancelAll(RequestName);
    }

    public synchronized void cancelCurrentRequest() {
        if (requestQueue != null)
            requestQueue.cancelAll(TAG);
    }
}
