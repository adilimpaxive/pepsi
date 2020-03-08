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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.pepsi.battleofthebands.activities.MainActivity.TAG_MINI_PLAYER;

public class JudgesDetailFragment extends Fragment {
    TextView textViewJudgeName, textViewJudgeDesignation, textViewDescription, textViewPerformance;
    ImageView imageViewBanner;
    Context context;
    View rootView;
    String id, name, designation, description, banner;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static JudgesDetailFragment newInstance(String id, String name, String designation, String description, String banner) {
        JudgesDetailFragment fragment = new JudgesDetailFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("name", name);
        args.putString("designation", designation);
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
        designation = bundle.getString("designation");
        description = bundle.getString("description");
        banner = bundle.getString("banner");
        if (context != null) {
            ((MainActivity) context).layoutYoutubePlayer.setVisibility(View.GONE);
            ((MainActivity) context).frameLayout.setVisibility(View.VISIBLE);
            ((MainActivity) context).textViewTitle.setText(name);
        }
        rootView = inflater.inflate(R.layout.fragment_judges_details, container, false);
        setViewPadding();
        try {
            PepsiApplication.tracker.setScreenName("JudgesDetailScreen");
            PepsiApplication.tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeViews();
        context.registerReceiver(br_updateMiniPlayer, new IntentFilter(TAG_MINI_PLAYER));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) context).imageViewBack.setVisibility(View.VISIBLE);
    }

    private void initializeViews() {
//        RelativeLayout layoutAllPerformance = rootView.findViewById(R.id.layoutAllPerformance);
        textViewJudgeName = rootView.findViewById(R.id.textViewJudgeName);
        textViewJudgeDesignation = rootView.findViewById(R.id.textViewJudgeDesignation);
        textViewDescription = rootView.findViewById(R.id.textViewDescription);
        textViewPerformance = rootView.findViewById(R.id.textViewPerformance);

        imageViewBanner = rootView.findViewById(R.id.imageViewBanner);
        RelativeLayout layoutBanner = rootView.findViewById(R.id.layoutBanner);
        layoutBanner.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 200)));

        int padding = Utils.getDpiFromPixel(context, 16);
        textViewJudgeName.setPadding(padding, (padding * 2), 0, 0);
        textViewJudgeDesignation.setPadding(padding, padding / 2, 0, 0);
        textViewDescription.setPadding(padding, (padding / 2), padding, (padding / 2));
        textViewPerformance.setPadding(padding, padding, padding, padding);

        textViewJudgeName.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewJudgeDesignation.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        textViewDescription.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_REGULAR));
        textViewPerformance.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));

//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        layoutAllPerformance.setLayoutParams(params);

        textViewDescription.setText(description);
        textViewJudgeName.setText(name.toUpperCase());
        textViewJudgeDesignation.setText(designation.toUpperCase());

        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            ((MainActivity) context).imageViewBackground.setImageResource(R.mipmap.background_gallery);
            textViewJudgeName.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewJudgeDesignation.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewDescription.setTextColor(ContextCompat.getColor(context, R.color.white));
            textViewPerformance.setBackgroundDrawable(ContextCompat.getDrawable(context, R.mipmap.button));
        }

        Picasso.with(context).load(banner).into(imageViewBanner);
        if (Utils.selectedPerformance.size() > 0) {
            textViewPerformance.setVisibility(View.VISIBLE);
            textViewPerformance.setAlpha(1f);
        } else {
            textViewPerformance.setVisibility(View.GONE);
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
}