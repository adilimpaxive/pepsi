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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.FeedbackAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.dialogs.AppDialog;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Feedback;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class FeedbackFragment extends Fragment implements ResponseApi, AdapterView.OnItemSelectedListener {
    Context context;
    View rootView;
    RelativeLayout layoutNameTitle, layoutName, layoutEmailTitle, layoutEmail, layoutFeedbackTitle, layoutFeedback, layoutSubmit;
    TextView textViewNameTitle, textViewEmailTitle, textViewFeedbackTitle;
    EditText editTextName, editTextEmail, editTextFeedback;
    Button buttonSubmit;
    private Dialog mProgressDialog;
    Spinner spinner;
    int selectedPosition = 0;

    public static Fragment newInstance() {
        return new FeedbackFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        ((MainActivity) context).layoutYoutubePlayer.setVisibility(View.GONE);
        ((MainActivity) context).frameLayout.setVisibility(View.VISIBLE);
        ((MainActivity) context).textViewTitle.setText("FEEDBACK");

        rootView = inflater.inflate(R.layout.fragment_feedback, container, false);
        setViewPadding();
        try {
            PepsiApplication.tracker.setScreenName("FeedbackScreen");
            PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initUi();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        context.sendBroadcast(new Intent(MainActivity.TAG_YOUTUBE_PLAYER).putExtra("showPlayer", false));
        return rootView;
    }

    private void initUi() {
        mProgressDialog = AppDialog.getProgressDialog(context);

        layoutNameTitle = rootView.findViewById(R.id.layoutNameTitle);
        layoutName = rootView.findViewById(R.id.layoutName);
        layoutEmailTitle = rootView.findViewById(R.id.layoutEmailTitle);
        layoutEmail = rootView.findViewById(R.id.layoutEmail);
        layoutFeedbackTitle = rootView.findViewById(R.id.layoutFeedbackTitle);
        layoutFeedback = rootView.findViewById(R.id.layoutFeedback);
        layoutSubmit = rootView.findViewById(R.id.layoutSubmit);

        int padding16 = Utils.getDpiFromPixel(context, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins(padding16, padding16 / 4, padding16, 0);
        layoutNameTitle.setLayoutParams(params);

        params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins(padding16, padding16, padding16, 0);
        layoutEmailTitle.setLayoutParams(params);

        params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins(padding16, padding16, padding16, 0);
        layoutFeedbackTitle.setLayoutParams(params);

        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16 / 4, padding16, 0);
        layoutName.setLayoutParams(params);

        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16 / 4, padding16, 0);
        layoutEmail.setLayoutParams(params);

        params = new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 168));
        params.setMargins(padding16, padding16 / 4, padding16, 0);
        layoutFeedback.setLayoutParams(params);

        textViewNameTitle = rootView.findViewById(R.id.textViewNameTitle);
        editTextName = rootView.findViewById(R.id.editTextName);
        textViewEmailTitle = rootView.findViewById(R.id.textViewEmailTitle);
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        textViewFeedbackTitle = rootView.findViewById(R.id.textViewFeedbackTitle);
        editTextFeedback = rootView.findViewById(R.id.editTextFeedback);

        textViewNameTitle.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        editTextName.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        textViewEmailTitle.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        editTextEmail.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        textViewFeedbackTitle.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        editTextFeedback.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));

        if (Prefs.getBoolean(context, Prefs.KEY_USER_LOGEDIN, false)) {
            editTextName.setText(Prefs.getString(context, Prefs.KEY_FIRST_NAME, "") + " " + Prefs.getString(context, Prefs.KEY_LAST_NAME, ""));
            editTextEmail.setText(Prefs.getString(context, Prefs.KEY_EMAIL, ""));
        }

        buttonSubmit = rootView.findViewById(R.id.buttonSubmit);
        buttonSubmit.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid())
                    submitFeedback();
            }
        });

        RelativeLayout layoutSpinner = rootView.findViewById(R.id.layoutSpinner);

        spinner = rootView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Utils.getDpiFromPixel(context, 42));
        params.setMargins(padding16, padding16, padding16, 0);
        layoutSpinner.setLayoutParams(params);

        FeedbackAdapter adapter = new FeedbackAdapter(context, R.layout.spinner_view_selected, getFeedback());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            buttonSubmit.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.background_sign_up));
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
    private void submitFeedback() {
        RequestBody formBody = new FormBody.Builder()
                .add("name", editTextName.getText().toString())
                .add("email", editTextEmail.getText().toString())
                .add("feedback", editTextFeedback.getText().toString())
                .add("type", feedback.get(selectedPosition).getType())
                .build();
        OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
        okHttpApi.callPostRequest(URLManager.GET_FEEDBACK_URL, formBody);
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    ArrayList<Feedback> feedback = new ArrayList<>();

    private ArrayList<Feedback> getFeedback() {
        Feedback item = new Feedback();
        item.setType("General feedback");
        feedback.add(item);

        item = new Feedback();
        item.setType("Report bug");
        feedback.add(item);

        item = new Feedback();
        item.setType("Something isn,t working");
        feedback.add(item);

        return feedback;
    }

    boolean isValid() {
        if (editTextName.getText().toString().startsWith(" ")) {
            clearError();
            editTextName.setError(getString(R.string.password_not_start_with_empty));
            editTextName.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextName.startAnimation(shake);
            return false;
        } else if (editTextName.length() < 1) {
            clearError();
            editTextName.setError(getString(R.string.field_required));
            editTextName.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextName.startAnimation(shake);
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
        if (editTextFeedback.getText().toString().startsWith(" ")) {
            clearError();
            editTextFeedback.setError(getString(R.string.password_not_start_with_empty));
            editTextFeedback.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextFeedback.startAnimation(shake);
            return false;
        } else if (editTextFeedback.length() < 1) {
            clearError();
            editTextFeedback.setError(getString(R.string.field_required));
            editTextFeedback.requestFocus();
            Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
            editTextFeedback.startAnimation(shake);
            return false;
        }
        return true;
    }

    private void clearError() {
        editTextEmail.setError(null);
        editTextName.setError(null);
        editTextFeedback.setError(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Utils.hideKeyBoard(editTextEmail, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onNetworkNotAvailable() {
        DialogHelper.showDialogError(context, getString(R.string.internet_required_alert));
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            String userName = jsonObject.getString("name");
            if (userName.equalsIgnoreCase(editTextName.getText().toString())) {
                Toast.makeText(context, "Thanks for your feedback", Toast.LENGTH_SHORT).show();
                ((MainActivity) context).onBackPressed();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String type, String webServiceName) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onCrash(String crashMsg) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
