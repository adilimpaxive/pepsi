package com.pepsi.battleofthebands.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class NetworkManager {

    public static String getHTTPPostRequestResponse(String URL, ArrayList<NameValuePair> params) {
        String result = "";
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(URL);
        // TODO: Request parameters and other properties.

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            // TODO: Execute and get the response.
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                try {
                    // TODO: EntityUtils to get the response content
                    result = EntityUtils.toString(httpEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result = Utils.KEY_SERVER_NO_RESPONSE;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = Utils.KEY_SERVER_NO_RESPONSE;
        } catch (IOException e) {
            e.printStackTrace();
            result = Utils.KEY_SERVER_NO_RESPONSE;
        }
        return result;
    }
}
