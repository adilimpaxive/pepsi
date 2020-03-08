package com.pepsi.battleofthebands.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.dialogs.AppDialog;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.PLog;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ResponseApi {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private LoginButton loginButton;
    SignInButton signInButton;
    private Dialog mProgressDialog;
    private CallbackManager callbackManager;
    Context context;
    public RelativeLayout layoutToolbarHeader, layoutEmailTitle, layoutEmail, layoutPasswordTitle, layoutPassword, layoutSignIn, layoutOr, layoutRegister, layoutFacebook, layoutGoogle;
    ImageView imageViewBack;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Facebook SDK should be initialized before displaying the Ui with the FB login Button
        context = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                //            clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(FLAG_TRANSLUCENT_STATUS);
                //             add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //             finally change the color
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_login);
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    AccessToken.setCurrentAccessToken(null);
//                    Profile.setCurrentProfile(null);
//                    LoginManager.getInstance().logOut();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    googleSignInClient.signOut();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        init();
        setUpFacebookLoginButton();
    }

    private void init() {
        mProgressDialog = AppDialog.getProgressDialog(this);
        editTextEmail = findViewById(R.id.emailEditText);
        editTextPassword = findViewById(R.id.passwordEditText);
        editTextPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (isValid()) {
                                onLoginClick();
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        int padding16 = Utils.getDpiFromPixel(context, 16);

        layoutToolbarHeader = findViewById(R.id.headerLinearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 56));
        layoutToolbarHeader.setLayoutParams(params);

        layoutEmailTitle = findViewById(R.id.layoutEmailTitle);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 30));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutEmailTitle.setLayoutParams(params);

        layoutEmail = findViewById(R.id.layoutEmail);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 45));
        params.setMargins(padding16, 0, padding16, 0);
        layoutEmail.setLayoutParams(params);

        layoutPasswordTitle = findViewById(R.id.layoutPasswordTitle);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 30));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutPasswordTitle.setLayoutParams(params);

        layoutPassword = findViewById(R.id.layoutPassword);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 45));
        params.setMargins(padding16, 0, padding16, 0);
        layoutPassword.setLayoutParams(params);

        layoutSignIn = findViewById(R.id.layoutSignIn);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 45));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutSignIn.setLayoutParams(params);

        layoutOr = findViewById(R.id.layoutOr);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 45));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutOr.setLayoutParams(params);

        layoutRegister = findViewById(R.id.layoutRegister);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 55));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutRegister.setLayoutParams(params);

        layoutFacebook = findViewById(R.id.layoutFacebook);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 55));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutFacebook.setLayoutParams(params);

        layoutGoogle = findViewById(R.id.layoutGoogle);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 55));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutGoogle.setLayoutParams(params);

        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        imageViewBack.setPadding(padding16, 0, padding16, 0);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(context);
                if (acct != null) {
                    String personName = acct.getDisplayName();
                    String personGivenName = acct.getGivenName();
                    String personFamilyName = acct.getFamilyName();
                    String personEmail = acct.getEmail();
                    String personId = acct.getId();
                    Uri personPhoto = acct.getPhotoUrl();
                    Prefs.saveString(context, Prefs.KEY_FIRST_NAME, personName);
                    Prefs.saveString(context, Prefs.KEY_EMAIL, personEmail);
                    invokeLoginWithGoogleService(acct.getId());
                } else {
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            }
        });

        Button buttonSignIn = findViewById(R.id.buttonSignIn);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonFacebook = findViewById(R.id.buttonFacebook);
        loginButton = findViewById(R.id.btn_fbSignUp);
        TextView orTextView = findViewById(R.id.orTextView);
        TextView forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        editTextEmail.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        editTextPassword.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        buttonSignIn.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        buttonRegister.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        buttonFacebook.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        orTextView.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        forgotPasswordTextView.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        buttonSignIn.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        buttonFacebook.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(URLManager.GET_FORGOT_PASSWORD_URL));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(browserIntent);
//                layoutLogin.setVisibility(View.GONE);
//                webView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setUpFacebookLoginButton() {
        callbackManager = CallbackManager.Factory.create();
//        btn_fbLogin.setReadPermissions("public_profile");
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                loginResult.getAccessToken();
                invokeLoginWithFacebookService(loginResult.getAccessToken().getUserId(), loginResult.getAccessToken().getToken());
                setFacebookData(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                PLog.showLog(exception.getMessage());
            }
        });
    }

    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response", response.toString());

                            String firstName = response.getJSONObject().getString("first_name");
                            Prefs.saveString(context, Prefs.KEY_FIRST_NAME, firstName);
                            String lastName = response.getJSONObject().getString("last_name");
                            Prefs.saveString(context, Prefs.KEY_LAST_NAME, lastName);
                            String email = response.getJSONObject().getString("email");
                            Prefs.saveString(context, Prefs.KEY_EMAIL, email);
                            String gender = response.getJSONObject().getString("gender");


                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();
                            Log.i("Link", link);
                            if (Profile.getCurrentProfile() != null) {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }

                            Log.i("Login" + "Email", email);
                            Log.i("Login" + "FirstName", firstName);
                            Log.i("Login" + "LastName", lastName);
                            Log.i("Login" + "Gender", gender);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onClick(View v) {
        Utils.hideKeyBoard(editTextEmail, this);
        switch (v.getId()) {
            case R.id.buttonSignIn:
                if (isValid()) {
                    onLoginClick();
                }
                break;
            case R.id.buttonRegister:
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonFacebook:
                loginButton.performClick();
                break;
            case R.id.imageViewBack:
                onBackPressed();
                break;
        }
    }


    private void invokeLoginWithFacebookService(String fbID, String fbToken) {
        RequestBody formBody = new FormBody.Builder()
                .add("facebook_id", fbID)
                .build();
        OKHttpApi okHttpApi = new OKHttpApi(this, this);
        okHttpApi.callPostRequest(URLManager.LOGIN_WITH_FACEBOOK_URL, formBody);
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void invokeLoginWithGoogleService(String google_id) {
        RequestBody formBody = new FormBody.Builder()
                .add("google_id", google_id)
                .build();
        OKHttpApi okHttpApi = new OKHttpApi(this, this);
        okHttpApi.callPostRequest(URLManager.LOGIN_WITH_GOOGLE_URL, formBody);
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    int RC_SIGN_IN = 999;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            return;
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            Prefs.saveString(context, Prefs.KEY_FIRST_NAME, name);
            Prefs.saveString(context, Prefs.KEY_EMAIL, email);
            invokeLoginWithGoogleService(id);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    public void onLoginClick() {
        OKHttpApi okHttpApi = new OKHttpApi(this, this);
        RequestBody formBody = new FormBody.Builder()
                .add("email", editTextEmail.getText().toString())
                .add("password", editTextPassword.getText().toString())
                .build();
        okHttpApi.callPostRequest(URLManager.LOGIN_URL, formBody);
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    boolean isValid() {
        if (editTextEmail.length() < 1) {
            clearError();
            editTextEmail.setError(getString(R.string.email_required));
            editTextEmail.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextEmail.startAnimation(shake);
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches() && !TextUtils.isEmpty(editTextEmail.getText().toString())) {
            clearError();
            editTextEmail.setError(getString(R.string.valid_email_required));
            editTextEmail.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextEmail.startAnimation(shake);
            return false;
        }
        if (editTextPassword.getText().toString().startsWith(" ")) {
            clearError();
            editTextPassword.setError(getString(R.string.password_not_start_with_empty));
            editTextPassword.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextPassword.startAnimation(shake);
            return false;
        } else if (editTextPassword.length() < 1) {
            clearError();
            editTextPassword.setError(getString(R.string.field_required));
            editTextPassword.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextPassword.startAnimation(shake);
            return false;
        } else if (editTextPassword.length() < 4 && editTextPassword.length() >= 1) {
            clearError();
            editTextPassword.setError(getString(R.string.password_character));
            editTextPassword.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextPassword.startAnimation(shake);
            return false;
        }
        return true;
    }

    private void clearError() {
        editTextEmail.setError(null);
        editTextPassword.setError(null);
    }

    @Override
    public void onNetworkNotAvailable() {
        DialogHelper.showDialogError(this, getString(R.string.internet_required_alert));
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (webServiceName.equalsIgnoreCase(URLManager.LOGIN_URL)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Prefs.saveString(LoginActivity.this, Prefs.KEY_TOKEN, jsonObject.getString("token"));
                jsonObject = jsonObject.getJSONObject("user");
                String userID = jsonObject.getString("id");
                String first_name = jsonObject.getString("first_name");
                String last_name = jsonObject.getString("last_name");
                String email = jsonObject.getString("email");
                Utils.saveUserInfoInPreference(LoginActivity.this, userID, first_name, last_name, email, editTextPassword.getText().toString());
                Prefs.saveBoolean(LoginActivity.this, Prefs.KEY_USER_LOGEDIN, true);
                Prefs.saveBoolean(context, Prefs.KEY_USER_LOGEDIN, true);
            } catch (JSONException e) {
                e.printStackTrace();
                DialogHelper.showDialogError(context, getString(R.string.internet_required_alert));
            }
        } else if (webServiceName.startsWith(URLManager.LOGIN_WITH_FACEBOOK_URL)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Prefs.saveString(LoginActivity.this, Prefs.KEY_TOKEN, jsonObject.getString("token"));
                jsonObject = jsonObject.getJSONObject("user");
                String userID = jsonObject.getString("id");
                String first_name = jsonObject.getString("first_name");
                String last_name = jsonObject.getString("last_name");
                String email = jsonObject.getString("email");
                Prefs.saveString(context, Prefs.KEY_USER_ID, userID);
//                Utils.saveUserInfoInPreference(LoginActivity.this, userID, first_name, last_name, email, editTextPassword.getText().toString());
                Prefs.saveBoolean(LoginActivity.this, Prefs.KEY_USER_LOGEDIN, true);
                Prefs.saveBoolean(context, Prefs.KEY_USER_LOGEDIN, true);
            } catch (JSONException e) {
                e.printStackTrace();
                DialogHelper.showDialogError(context, getString(R.string.internet_required_alert));
            }
        } else if (webServiceName.startsWith(URLManager.LOGIN_WITH_GOOGLE_URL)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Prefs.saveString(LoginActivity.this, Prefs.KEY_TOKEN, jsonObject.getString("token"));
                jsonObject = jsonObject.getJSONObject("user");
                String userID = jsonObject.getString("id");
                String first_name = jsonObject.getString("first_name");
                String last_name = jsonObject.getString("last_name");
                String email = jsonObject.getString("email");
                Prefs.saveString(context, Prefs.KEY_USER_ID, userID);
//                Utils.saveUserInfoInPreference(LoginActivity.this, userID, first_name, last_name, email, editTextPassword.getText().toString());
                Prefs.saveBoolean(LoginActivity.this, Prefs.KEY_USER_LOGEDIN, true);
                Prefs.saveBoolean(context, Prefs.KEY_USER_LOGEDIN, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(context, "Login Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String type, String webServiceName) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            JSONObject object = new JSONObject(type);
            JSONObject jsonObject = object.getJSONObject("errors");
            JSONArray jsonArray = jsonObject.getJSONArray("email");
            String error = jsonArray.getString(0);
            DialogHelper.showDialogError(context, error);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCrash(String crashMsg) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

