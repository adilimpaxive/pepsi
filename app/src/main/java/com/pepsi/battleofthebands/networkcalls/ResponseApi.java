package com.pepsi.battleofthebands.networkcalls;

/**
 * Created by Muhammad Kashan on 8/15/2016.
 */
public interface ResponseApi {
    void onNetworkNotAvailable();

    void onResponse(String res, String webServiceName);

    void onFailed(String type, String webServiceName);

    void onCrash(String crashMsg);
}
