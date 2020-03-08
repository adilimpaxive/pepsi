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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, ResponseApi {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextFirstName, editTextLastName;
    private LoginButton loginButton;
    SignInButton signInButton;
    private Dialog mProgressDialog;
    private CallbackManager callbackManager;
    Context context;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;

    public RelativeLayout layoutToolbarHeader, layoutFirstName, layoutLastName, layoutEmail, layoutPassword, layoutPasswordConfirm, layoutSignUp, layoutOr, layoutFacebook, layoutGoogle;
    ImageView imageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //Facebook SDK should be initialized before displaying the Ui with the FB login Button
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
        setContentView(R.layout.activity_sign_up);
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
        editTextConfirmPassword = findViewById(R.id.passwordEditTextConfirm);
        editTextFirstName = findViewById(R.id.firstNameEditText);
        editTextLastName = findViewById(R.id.lastNameEditText);
        TextView orTextView = findViewById(R.id.orTextView);
        Button buttonSignUp = findViewById(R.id.buttonSignUp);
        Button buttonFacebook = findViewById(R.id.buttonFacebook);
        loginButton = findViewById(R.id.btn_fbSignUp);
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
        editTextEmail.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        editTextPassword.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        editTextConfirmPassword.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        buttonSignUp.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_BOLD));
        orTextView.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));
        buttonFacebook.setOnClickListener(this);
        buttonSignUp.setOnClickListener(this);

        buttonFacebook.setTypeface(PFonts.getInstance(this).getFont(PFonts.FONT_MEDIUM));

        int padding16 = Utils.getDpiFromPixel(context, 16);

        layoutToolbarHeader = findViewById(R.id.headerLinearLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 56));
        layoutToolbarHeader.setLayoutParams(params);

        layoutFirstName = findViewById(R.id.layoutFirstName);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16 / 2, padding16, 0);
        layoutFirstName.setLayoutParams(params);

        layoutLastName = findViewById(R.id.layoutLastName);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutLastName.setLayoutParams(params);

        layoutEmail = findViewById(R.id.layoutEmail);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutEmail.setLayoutParams(params);

        layoutPassword = findViewById(R.id.layoutPassword);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutPassword.setLayoutParams(params);

        layoutPasswordConfirm = findViewById(R.id.layoutPasswordConfirm);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutPasswordConfirm.setLayoutParams(params);

        layoutSignUp = findViewById(R.id.layoutSignUp);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 55));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutSignUp.setLayoutParams(params);

        layoutOr = findViewById(R.id.layoutOr);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 55));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutOr.setLayoutParams(params);

        layoutFacebook = findViewById(R.id.layoutFacebook);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 55));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutFacebook.setLayoutParams(params);

        layoutGoogle = findViewById(R.id.layoutGoogle);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 60));
        params.setMargins(Utils.getDpiFromPixel(context, 13), padding16, Utils.getDpiFromPixel(context, 13), 0);
        layoutGoogle.setLayoutParams(params);

        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(this);
        imageViewBack.setPadding(padding16, 0, padding16, 0);
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
            case R.id.buttonSignUp:
                if (isValid()) {
                    signUp();
                }
                break;
            case R.id.buttonFacebook:
                loginButton.performClick();
                break;
            case R.id.imageViewBack:
                onBackPressed();
                break;
        }
    }

    private void signUp() {
        RequestBody formBody = new FormBody.Builder()
                .add("first_name", editTextFirstName.getText().toString())
                .add("last_name", editTextLastName.getText().toString())
                .add("email", editTextEmail.getText().toString())
                .add("password", editTextPassword.getText().toString())
                .add("password_confirmation", editTextConfirmPassword.getText().toString())
                .build();
        OKHttpApi okHttpApi = new OKHttpApi(this, this);
        okHttpApi.callPostRequest(URLManager.SIGNUP_URL, formBody);
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
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

    boolean isValid() {
        if (editTextFirstName.getText().toString().startsWith(" ")) {
            clearError();
            editTextFirstName.setError(getString(R.string.user_Name_not_start_with_empty));
            editTextFirstName.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextFirstName.startAnimation(shake);
            return false;
        } else if (editTextFirstName.length() < 1) {
            clearError();
            editTextFirstName.setError(getString(R.string.field_required));
            editTextFirstName.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextFirstName.startAnimation(shake);
            return false;
        }
        if (editTextLastName.getText().toString().startsWith(" ")) {
            clearError();
            editTextLastName.setError(getString(R.string.user_Name_not_start_with_empty));
            editTextLastName.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextLastName.startAnimation(shake);
            return false;
        } else if (editTextLastName.length() < 1) {
            clearError();
            editTextLastName.setError(getString(R.string.field_required));
            editTextLastName.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextLastName.startAnimation(shake);
            return false;
        }

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
        if (!editTextPassword.getText().toString().equalsIgnoreCase(editTextConfirmPassword.getText().toString())) {
            clearError();
            editTextConfirmPassword.setError(getString(R.string.password_error));
            editTextConfirmPassword.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextConfirmPassword.startAnimation(shake);
            return false;
        }
        return true;
    }

    private void clearError() {
        editTextEmail.setError(null);
        editTextPassword.setError(null);
        editTextConfirmPassword.setError(null);
        editTextFirstName.setError(null);
        editTextLastName.setError(null);
    }

    @Override
    public void onNetworkNotAvailable() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        DialogHelper.showDialogError(this, getString(R.string.internet_required_alert));
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (webServiceName.startsWith(URLManager.SIGNUP_URL)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Prefs.saveString(SignUpActivity.this, Prefs.KEY_TOKEN, jsonObject.getString("token"));
                jsonObject = jsonObject.getJSONObject("user");
                String userID = jsonObject.getString("id");
                String first_name = jsonObject.getString("first_name");
                String last_name = jsonObject.getString("last_name");
                String email = jsonObject.getString("email");
                Utils.saveUserInfoInPreference(SignUpActivity.this, userID, first_name, last_name, email, editTextPassword.getText().toString());
                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
//                Prefs.saveBoolean(context, Prefs.KEY_USER_LOGEDIN, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (webServiceName.startsWith(URLManager.LOGIN_WITH_FACEBOOK_URL)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Prefs.saveString(SignUpActivity.this, Prefs.KEY_TOKEN, jsonObject.getString("token"));
                jsonObject = jsonObject.getJSONObject("user");
                String userID = jsonObject.getString("id");
                String first_name = jsonObject.getString("first_name");
                String last_name = jsonObject.getString("last_name");
                String email = jsonObject.getString("email");
                Prefs.saveString(context, Prefs.KEY_USER_ID, userID);
                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show();
                Prefs.saveBoolean(SignUpActivity.this, Prefs.KEY_USER_LOGEDIN, true);
                finish();
//                Utils.saveUserInfoInPreference(SignUpActivity.this, userID, first_name, last_name, email, editTextPassword.getText().toString());
//                Prefs.saveBoolean(context, Prefs.KEY_USER_LOGEDIN, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (webServiceName.startsWith(URLManager.LOGIN_WITH_GOOGLE_URL)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                Prefs.saveString(SignUpActivity.this, Prefs.KEY_TOKEN, jsonObject.getString("token"));
                jsonObject = jsonObject.getJSONObject("user");
                String userID = jsonObject.getString("id");
                String first_name = jsonObject.getString("first_name");
                String last_name = jsonObject.getString("last_name");
                String email = jsonObject.getString("email");
                Prefs.saveString(context, Prefs.KEY_USER_ID, userID);
                Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show();
                Prefs.saveBoolean(SignUpActivity.this, Prefs.KEY_USER_LOGEDIN, true);
                finish();
//                Utils.saveUserInfoInPreference(SignUpActivity.this, userID, first_name, last_name, email, editTextPassword.getText().toString());
//                Prefs.saveBoolean(context, Prefs.KEY_USER_LOGEDIN, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    }
}