package com.pepsi.battleofthebands.networkcalls;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by Muhammad Kashan on 8/15/2017.
 */
public class SimplePostRequest extends StringRequest {

    private Map<String, String> requestParams;

    public SimplePostRequest(int method, String url, Map<String, String> params, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        requestParams = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (requestParams == null)
            requestParams = super.getParams();
        return requestParams;
    }
}