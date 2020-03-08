package com.pepsi.battleofthebands.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.dialogs.AppDialog;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class ProfileFragment extends Fragment implements ResponseApi, View.OnClickListener {
    private ProgressBar progressBar;
    Context context;
    View rootView;
    private Dialog mProgressDialog;
    public RelativeLayout layoutFirstName, layoutLastName, layoutEmail, layoutPassword, layoutPasswordConfirm, layoutUpdateProfile;
    EditText editTextPassword, editTextConfirmPassword, editTextFirstName, editTextLastName, editTextEmail;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("PROFILE");
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        setViewPadding();
        mProgressDialog = AppDialog.getProgressDialog(context);
        try {
            PepsiApplication.tracker.setScreenName("UpdateProfileScreen");
            PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ini();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;
    }

    private void ini() {
        progressBar = rootView.findViewById(R.id.progressBar);
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        editTextConfirmPassword = rootView.findViewById(R.id.passwordConfirmEditText);

        editTextPassword = rootView.findViewById(R.id.passwordEditText);
        editTextFirstName = rootView.findViewById(R.id.firstNameEditText);
        editTextLastName = rootView.findViewById(R.id.lastNameEditText);

        editTextEmail.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        editTextPassword.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));

        Button buttonUpdateProfile = rootView.findViewById(R.id.buttonUpdateProfile);
        buttonUpdateProfile.setOnClickListener(this);
        buttonUpdateProfile.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));

        int padding16 = Utils.getDpiFromPixel(context, 16);

        layoutFirstName = rootView.findViewById(R.id.layoutFirstName);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16 / 2, padding16, 0);
        layoutFirstName.setLayoutParams(params);

        layoutLastName = rootView.findViewById(R.id.layoutLastName);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutLastName.setLayoutParams(params);

        layoutEmail = rootView.findViewById(R.id.layoutEmail);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutEmail.setLayoutParams(params);

        layoutPassword = rootView.findViewById(R.id.layoutPassword);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutPassword.setLayoutParams(params);

        layoutPasswordConfirm = rootView.findViewById(R.id.layoutPasswordConfirm);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutPasswordConfirm.setLayoutParams(params);

        layoutUpdateProfile = rootView.findViewById(R.id.layoutUpdateProfile);
        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 55));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutUpdateProfile.setLayoutParams(params);

        editTextFirstName.setText(Prefs.getString(context, Prefs.KEY_FIRST_NAME, ""));
        editTextLastName.setText(Prefs.getString(context, Prefs.KEY_LAST_NAME, ""));
        editTextEmail.setText(Prefs.getString(context, Prefs.KEY_EMAIL, ""));
        editTextPassword.setText(Prefs.getString(context, Prefs.KEY_PASSWORD, ""));

        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            buttonUpdateProfile.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.background_sign_up));
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_background_color));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br_updateMiniPlayer != null)
            context.unregisterReceiver(br_updateMiniPlayer);
    }

    @Override
    public void onNetworkNotAvailable() {
        progressBar.setVisibility(View.INVISIBLE);
        DialogHelper.showDialogError(context, getString(R.string.internet_required_alert));
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        try {
            progressBar.setVisibility(View.INVISIBLE);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("id")) {
                    String userID = jsonObject.getString("id");
                    String first_name = jsonObject.getString("first_name");
                    String last_name = jsonObject.getString("last_name");
                    String email = jsonObject.getString("email");
                    Utils.saveUserInfoInPreference(context, userID, first_name, last_name, email, editTextPassword.getText().toString());
                    ((MainActivity) context).onBackPressed();
                    Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String type, String webServiceName) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            String userID = Prefs.getString(context, Prefs.KEY_USER_ID, "");
            String first_name = editTextFirstName.getText().toString();
            String last_name = editTextLastName.getText().toString();
            String email = editTextEmail.getText().toString();
            Utils.saveUserInfoInPreference(context, userID, first_name, last_name, email, editTextPassword.getText().toString());
            ((MainActivity) context).onBackPressed();
            Toast.makeText(context, "Update Successfully", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    public BroadcastReceiver br_updateMiniPlayer = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            setViewPadding();
        }
    };

    private void setViewPadding() {
        if (((MainActivity) context).showMiniPlayer(true)) {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, Utils.getDpiFromPixel(context, 56));
        } else {
            rootView.setPadding(0, Utils.getDpiFromPixel(context, 56), 0, 0);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonUpdateProfile:
                if (isValid())
                    updateProfile();
                break;
        }
    }

    private void updateProfile() {
        RequestBody formBody = new FormBody.Builder()
                .add("first_name", editTextFirstName.getText().toString())
                .add("last_name", editTextLastName.getText().toString())
                .add("email", editTextEmail.getText().toString())
                .add("password", editTextPassword.getText().toString())
                .add("password_confirmation", editTextConfirmPassword.getText().toString())
                .build();
        OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
        okHttpApi.callPostRequestWithToken(URLManager.GET_UPDATE_URL, formBody);
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }
}