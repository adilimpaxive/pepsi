package com.pepsi.battleofthebands.networkcalls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Window;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Muhammad Kashan on 8/15/2016.
 */
public class OKHttpApi implements Callback {
    private OkHttpClient okHttpClient;
    private ResponseApi mResponseApi;
    private Call call;
    private ProgressDialog progressDialog;
    private Activity mContext;
    private PepsiApplication globalApp;

//    private String baseUrl;

    private Request.Builder builder;

    private boolean isShowProgress;
    private String authToken;

    private String webServiceName;

    private boolean saveHeader;

    public OKHttpApi(Activity mContext, ResponseApi responseApi) {
        this.mContext = mContext;
        mResponseApi = responseApi;
        globalApp = (PepsiApplication) mContext.getApplicationContext();
//        baseUrl = mContext.getString(R.string.base_url);

        setShowProgress(true);

//        setHeader();
    }

    public boolean isSaveHeader() {
        return saveHeader;
    }

    public void setSaveHeader(boolean value) {
        this.saveHeader = value;
    }

    public boolean isShowProgress() {
        return isShowProgress;
    }

    public void setShowProgress(boolean showProgress) {
        isShowProgress = showProgress;
    }

//    public OKHttpApi(Activity mContext, ResponseApi responseApi, String baseUrl) {
//        this.mContext = mContext;
//        mResponseApi = responseApi;
//        globalApp = (PepsiApplication) mContext.getApplicationContext();
//        this.baseUrl = baseUrl;

//        setShowProgress(true);

    // setHeader();
//    }

//    public void setHeader() {
//        builder = new Request.Builder();
//        builder.addHeader("Content-Type", "application/json");
//        builder.addHeader("device-type", "android");
//
//        Location loc = LocationServiceManager.getInstance(mContext).getCurrentLocation();
//
//        if (loc != null)
//            builder.addHeader("coordinates", loc.getLatitude() + "," + loc.getLongitude());
//
//        String token = SettingsHelper.getInstance().getString(mContext, RipilConstants.GCM_TOKEN, null);
//        if (token != null) {
//            builder.addHeader("push-token", token);
//        }
//
//        String authToken = SettingsHelper.getInstance().getString(mContext, RipilConstants.RIPIL_AUTH_TOKEN, null);
//
//        if (authToken != null)
//            builder.addHeader("auth-token", authToken);
//
//    }

    public void callGetRequest(String url) {
        this.webServiceName = url;

        if (isWifiConnected()) {
//            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append(baseUrl);
//            stringBuffer.append(url);

//            if (isShowProgress()) {
//                showProgressDialogWithTitle(progressMsg, mContext);
//            }

            okHttpClient = globalApp.getOkHttpClient();
            builder = new Request.Builder();
            builder.url(url);
            builder.addHeader("Content-Type", "application/json");
            builder.addHeader("cache-control", "no-cache");
            Request request = builder.build();
            call = okHttpClient.newCall(request);
            call.enqueue(this);
        } else {
            if (mResponseApi != null) {
                mResponseApi.onNetworkNotAvailable();
            }
        }
    }

    public void callGetRequest() {
        this.webServiceName = URLManager.GET_VOTING_URL;

        if (isWifiConnected()) {

            okHttpClient = globalApp.getOkHttpClient();
            builder = new Request.Builder();
            builder.url(webServiceName);
            builder.addHeader("Content-Type", "application/json");
            builder.header("Authorization", String.format("bearer %s", Prefs.getString(mContext, Prefs.KEY_TOKEN, "")));
            Request request = builder.build();
            call = okHttpClient.newCall(request);
            call.enqueue(this);
        } else {
            if (mResponseApi != null) {
                mResponseApi.onNetworkNotAvailable();
            }
        }
    }

    public void callPostRequest(String url, RequestBody postBody) {
        this.webServiceName = url;

        final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");

        if (isWifiConnected()) {

//            showProgressDialogWithTitle(progressMsg, mContext);

            okHttpClient = globalApp.getOkHttpClient();
            builder = new Request.Builder();

            builder.url(url);
            builder.addHeader("Accept", "application/json");
            builder.post(postBody);
            Request request = builder.build();

            call = okHttpClient.newCall(request);
            call.enqueue(this);
        } else {
            if (mResponseApi != null) {
                mResponseApi.onNetworkNotAvailable();
            }
        }
    }

    public void callPostRequestWithToken(String url, RequestBody postBody) {
        this.webServiceName = url;

        final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");

        if (isWifiConnected()) {

//            showProgressDialogWithTitle(progressMsg, mContext);

            okHttpClient = globalApp.getOkHttpClient();
            builder = new Request.Builder();

            builder.url(url);
            builder.addHeader("Accept", "application/json");
            builder.header("Authorization", String.format("bearer %s", Prefs.getString(mContext, Prefs.KEY_TOKEN, "")));
            builder.post(postBody);
            Request request = builder.build();

            call = okHttpClient.newCall(request);
            call.enqueue(this);
        } else {
            if (mResponseApi != null) {
                mResponseApi.onNetworkNotAvailable();
            }
        }
    }

    public void callPostRequest(String url, JSONObject postBody) {
        this.webServiceName = url;

        final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");

        if (isWifiConnected()) {

            okHttpClient = globalApp.getOkHttpClient();
            builder = new Request.Builder();

            builder.url(url);
            RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody.toString());
            builder.post(body);
            Request request = builder.build();

            call = okHttpClient.newCall(request);
            call.enqueue(this);
        } else {
            if (mResponseApi != null) {
                mResponseApi.onNetworkNotAvailable();
            }
        }
    }
//    public void callPostRequestBackground() {
//        this.webServiceName = URLManager.LOGIN_URL;
//
//        if (isWifiConnected()) {
//            RequestBody formBody = new FormBody.Builder()
//                    .add("email_address", Prefs.getString(mContext, Prefs.KEY_EMAIL, ""))
//                    .add("password", Prefs.getString(mContext, Prefs.KEY_PASSWORD, ""))
//                    .build();
//            okHttpClient = globalApp.getOkHttpClient();
//            builder = new Request.Builder();
//            builder.url(URLManager.LOGIN_URL);
//            builder.post(formBody);
//            Request request = builder.build();
//            call = okHttpClient.newCall(request);
//            call.enqueue(this);
//        } else {
//            if (mResponseApi != null) {
//                mResponseApi.onNetworkNotAvailable();
//            }
//        }
//    }

    public void callDeleteRequest(String url, String progressMsg, String deleteBody) {
        this.webServiceName = url;

        final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/json; charset=utf-8");

        if (isWifiConnected()) {
//            StringBuffer stringBuffer = new StringBuffer();
//            stringBuffer.append(baseUrl);
//            stringBuffer.append(url);

            if (isShowProgress()) {
                showProgressDialogWithTitle(progressMsg, mContext);
            }

            okHttpClient = globalApp.getOkHttpClient();

            builder.url(url);
            builder.delete(RequestBody.create(MEDIA_TYPE_MARKDOWN, deleteBody));
            Request request = builder.build();

            call = okHttpClient.newCall(request);
            call.enqueue(this);
        } else {
            if (mResponseApi != null) {
                mResponseApi.onNetworkNotAvailable();
            }
        }
    }

    public void callMultipartRequest(String url, String progressMsg, File file) {
        final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("image/png");

        if (isWifiConnected()) {
            showProgressDialogWithTitle(progressMsg, mContext);

            okHttpClient = globalApp.getOkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", file.getAbsolutePath(), RequestBody.create(MEDIA_TYPE_MARKDOWN, file)).build();

            Request.Builder builder = new Request.Builder();
            builder.url(url);
            builder.post(requestBody);
            Request request = builder.build();

            call = okHttpClient.newCall(request);
            call.enqueue(this);
        } else {
            if (mResponseApi != null) {
                mResponseApi.onNetworkNotAvailable();
            }
        }
    }

    @Override
    public void onFailure(Call call, IOException exc) {
        if (isShowProgress()) {
            cancelProgressDialog();
        }
        clearCall();

        Log.e("Exception type", exc.toString());

//        if (exc instanceof SocketTimeoutException) {
//            //sendFailure("Time Out", webServiceName);
//            sendFailure(mContext.getString(R.string.internet_unavailable_msg), webServiceName);
//        }

        /*
         * if want to identify specific exception
         * else if (exc instanceof SocketException)
         * {
         * sendFailure("Socket Exception");
         * }
         * else if (exc instanceof UnknownHostException)
         * {
         * sendFailure("Unknown Host Exception");
         * }
         */
//        else {
        if (isShowProgress()) {
            sendFailure(mContext.getString(R.string.internet_required_alert), webServiceName);
        }
        // }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            if (isShowProgress()) {
                cancelProgressDialog();
            }

//            if (isSaveHeader()) {
//                Headers headers = response.headers();
//                for (int i = 0; i < headers.size(); i++) {
//                    Log.e(headers.name(i), headers.value(i));
//                    if (headers.name(i).equals(RipilConstants.RIPIL_AUTH_TOKEN)) {
//                        Common.getInstance().showErrorLog(RipilConstants.RIPIL_AUTH_TOKEN, headers.value(i));
//                        SettingsHelper.getInstance().setString(mContext, RipilConstants.RIPIL_AUTH_TOKEN, headers.value(i));
//                    }
//                }
//            }
            clearCall();

            final String json = response.body().string();


            if (response.code() == 200 && response.isSuccessful()) {

                Log.e(OKHttpApi.class.getSimpleName(), response.code() + "");
                sendSuccess(json, webServiceName);
            } else {
                sendFailure(json, webServiceName);

                /*
                 * response to each code separately
                 * switch (response.code())
                 * {
                 * case 400:
                 * case 401:
                 * case 404:
                 * case 406:
                 * case 500:
                 * break;
                 * default:
                 * break;
                 * }
                 */

            }
            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();

            sendCrash(e.toString());
        }
    }

    private void sendSuccess(String json, final String webServiceName) {
        final String mJson = json;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mResponseApi.onResponse(mJson, webServiceName);
            }
        };

        mContext.runOnUiThread(runnable);
    }

    private void sendFailure(final String msg, final String webServiceName) {
        Runnable runnable = new Runnable() {
            public void run() {
                if (mResponseApi != null) {
                    mResponseApi.onFailed(msg, webServiceName);
                }
            }
        };
        mContext.runOnUiThread(runnable);
    }

    private void sendCrash(final String msg) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mResponseApi != null) {
                    mResponseApi.onCrash(msg);
                }
            }
        };
        mContext.runOnUiThread(runnable);
    }

    private void clearCall() {
        if (call != null) {
            call = null;
        }
    }

    public void cancelCall() {
        if (call != null) {
            if (!call.isCanceled()) {
                call.cancel();
            }
            call = null;
        }
        if (isShowProgress()) {
            cancelProgressDialog();
        }
    }

    public void showProgressDialogWithTitle(String title, Context context) {
        if (!((Activity) context).isFinishing()) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(false);
                progressDialog.setMessage(title);
                progressDialog.show();
            }
        }
    }

    private void cancelProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }
    }

    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
    }
}
