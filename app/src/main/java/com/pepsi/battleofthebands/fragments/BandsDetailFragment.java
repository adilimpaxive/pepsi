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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.adapter.BandMemberAdapter;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.Member;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class BandsDetailFragment extends Fragment implements ResponseApi {
    ArrayList<Member> members;
    BandMemberAdapter bandMemberAdapter;
    RecyclerView recyclerViewMembers;
    private ProgressBar progressBar;
    TextView textViewDescription, textViewMemberTitle, textViewPerformance;
    ImageView imageViewBanner;
    Context context;
    View rootView;
    String id, name, description, banner;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static BandsDetailFragment newInstance(String id, String name, String description, String banner) {
        BandsDetailFragment fragment = new BandsDetailFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("name", name);
        args.putString("description", description);
        args.putString("banner", banner);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        name = bundle.getString("name");
        description = bundle.getString("description");
        banner = bundle.getString("banner");
        if (context != null) {
            ((MainActivity) context).layoutYoutubePlayer.setVisibility(View.GONE);
            ((MainActivity) context).frameLayout.setVisibility(View.VISIBLE);
            ((MainActivity) context).textViewTitle.setText(name);
        }
        rootView = inflater.inflate(R.layout.fragment_bands_detail, container, false);
        setViewPadding();
        initializeViews();
        loadFromInternet();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }
    private void loadFromInternet() {
        if (members == null) {
            try {
                PepsiApplication.tracker.setScreenName("BandsDetailScreen");
                PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
            } catch (Exception e) {
                e.printStackTrace();
            }
            OKHttpApi okHttpApi = new OKHttpApi(((MainActivity) context), this);
            okHttpApi.callGetRequest(URLManager.BANDS_URL + "/" + id + "/members");
        } else {
            setUpViewsWithData();
        }
    }

    private void setUpViewsWithData() {
        progressBar.setVisibility(View.INVISIBLE);
        bandMemberAdapter = new BandMemberAdapter(context, members);
        recyclerViewMembers.setAdapter(bandMemberAdapter);
    }

    private void initializeViews() {
//        RelativeLayout layoutAllPerformance = rootView.findViewById(R.id.layoutAllPerformance);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerViewMembers = rootView.findViewById(R.id.recyclerViewMember);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewMembers.setHasFixedSize(true);
        textViewDescription = rootView.findViewById(R.id.textViewDescription);
        textViewMemberTitle = rootView.findViewById(R.id.textViewMemberTitle);
        textViewPerformance = rootView.findViewById(R.id.textViewPerformance);
        imageViewBanner = rootView.findViewById(R.id.imageViewBanner);

        RelativeLayout layoutBanner = rootView.findViewById(R.id.layoutBanner);
        layoutBanner.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 200)));

        textViewDescription.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_REGULAR));
        textViewMemberTitle.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewPerformance.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));

        int padding = Utils.getDpiFromPixel(context, 16);
        textViewDescription.setPadding(padding, padding, padding, padding);
        textViewMemberTitle.setPadding(padding, padding, padding, padding);
        textViewPerformance.setPadding(padding, padding, padding, padding);

//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        layoutAllPerformance.setLayoutParams(params);

        textViewDescription.setText(description);


        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
            textViewMemberTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewDescription.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewPerformance.setBackgroundDrawable(ContextCompat.getDrawable(context, R.mipmap.button));
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
        } else {
            ((MainActivity) context).imageViewBackground.setImageResource(0);
            ((MainActivity) context).imageViewBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        Picasso.with(context).load(banner).into(imageViewBanner);
        if (Utils.selectedPerformance.size() > 0) {
            textViewPerformance.setAlpha(1f);
        } else {
            textViewPerformance.setAlpha(.3f);
        }
        textViewPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.selectedPerformance.size() > 0) {
                    ((MainActivity) context).showPerformanceFragment(name);
                } else {
                    Toast.makeText(context, "No Performance Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                members = gson.fromJson(response, new TypeToken<ArrayList<Member>>() {
                }.getType());
                setUpViewsWithData();
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