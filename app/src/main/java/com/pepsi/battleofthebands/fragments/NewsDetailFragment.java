package com.pepsi.battleofthebands.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class NewsDetailFragment extends Fragment implements ResponseApi {
    private ProgressBar progressBar;
    TextView textViewNewsTitle, textViewDescription;
    ImageView imageViewBanner, imageViewLogo;
    Context context;
    View rootView;
    String id, title, description, thumb, banner;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static NewsDetailFragment newInstance(String id, String title, String description, String thumb, String banner) {
        NewsDetailFragment fragment = new NewsDetailFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("title", title);
        args.putString("description", description);
        args.putString("thumb", thumb);
        args.putString("banner", banner);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        title = bundle.getString("title");
        description = bundle.getString("description");
        banner = bundle.getString("banner");
        thumb = bundle.getString("thumb");

        if (context != null) {
            ((MainActivity) context).textViewTitle.setText("NEWS");
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        rootView = inflater.inflate(R.layout.fragment_news_detail, container, false);
        setViewPadding();
        try {
            PepsiApplication.tracker.setScreenName("NewsDetailScreen");
            PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeViews();

        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;
    }

    private void initializeViews() {
        progressBar = rootView.findViewById(R.id.progressBar);
        textViewDescription = rootView.findViewById(R.id.textViewDescription);
        textViewNewsTitle = rootView.findViewById(R.id.textViewNewsTitle);

        imageViewLogo = rootView.findViewById(R.id.imageViewLogoNews);
        imageViewLogo.setLayoutParams(new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 48), Utils.getDpiFromPixel(context, 48)));

        imageViewBanner = rootView.findViewById(R.id.imageViewBanner);
        RelativeLayout layoutBanner = rootView.findViewById(R.id.layoutBanner);
        layoutBanner.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 200)));

        int padding = Utils.getDpiFromPixel(context, 16);

        RelativeLayout layoutLogo = rootView.findViewById(R.id.layoutLogo);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 48), Utils.getDpiFromPixel(context, 48));
        layoutParams.setMargins(padding, padding, padding, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutLogo.setLayoutParams(layoutParams);

        textViewNewsTitle.setPadding(padding, 0, padding, padding);

        textViewNewsTitle.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));

        textViewNewsTitle.setText(title);
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            textViewNewsTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewDescription.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        textViewDescription.setPadding(padding, (padding / 2), padding, padding);

        textViewDescription.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_REGULAR));
        textViewDescription.setText(Html.fromHtml(description));

        Picasso.with(context).load(thumb).into(imageViewLogo);
        Picasso.with(context).load(banner).into(imageViewBanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) context).showMiniPlayer(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (br_updateMiniPlayer != null)
            context.unregisterReceiver(br_updateMiniPlayer);
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
        progressBar.setVisibility(View.INVISIBLE);
        DialogHelper.showDialogError(context, getString(R.string.internet_required_alert));
    }

    @Override
    public void onResponse(String response, String webServiceName) {
        try {
            progressBar.setVisibility(View.INVISIBLE);
            try {
                Gson gson = new Gson();
//                members = gson.fromJson(response, new TypeToken<ArrayList<Member>>() {
//                }.getType());
//                setUpViewsWithData();
            } catch (Exception e) {
                DialogHelper.showDialogError(context, "Something went wrong.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailed(String type, String webServiceName) {

    }

    @Override
    public void onCrash(String crashMsg) {

    }
}