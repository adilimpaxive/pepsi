package com.pepsi.battleofthebands.adapter;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.entity.MenuItem;
import com.pepsi.battleofthebands.fragments.FeedbackFragment;
import com.pepsi.battleofthebands.fragments.HomeFragment;
import com.pepsi.battleofthebands.fragments.ProfileFragment;
import com.pepsi.battleofthebands.fragments.TermsAndConditionsFragment;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

public class MenuAdapter extends ArrayAdapter<MenuItem> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<MenuItem> values;

    public MenuAdapter(Context context, int textViewResourceId, ArrayList<MenuItem> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public MenuItem getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = inflator.inflate(R.layout.spinner_view_selected, null);
        RelativeLayout layout = row.findViewById(R.id.layoutMain);
        layout.setBackgroundColor(Color.TRANSPARENT);
        View view = row.findViewById(R.id.viewLine);
        view.setBackgroundColor(Color.TRANSPARENT);
        ImageView imageViewMenu = row.findViewById(R.id.imageViewMenu);
        imageViewMenu.setVisibility(View.GONE);
        RelativeLayout viewBottom = row.findViewById(R.id.viewBottom);
        viewBottom.setVisibility(View.GONE);
//        TextView title = row.findViewById(R.id.textViewTitle);
//        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
//        title.setTextColor(context.getResources().getColor(R.color.white));
//        title.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
//        // Then you can get the current item using the values array (Users array) and the current position
//        // You can NOW reference each method you has created in your bean object (User class)
//        title.setText("");
//        title.setPadding(0, Utils.getDpiFromPixel(context, 10), 0, Utils.getDpiFromPixel(context, 10));
//        // And finally return your dynamic (or custom) view for each spinner item
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return row;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(final int position, View convertView, final ViewGroup parent) {
        LayoutInflater inflator = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View row = inflator.inflate(R.layout.spinner_view_selected, null);
        RelativeLayout layout = row.findViewById(R.id.layoutMain);
        layout.setBackgroundColor(Color.BLACK);
        ImageView imageViewMenu = row.findViewById(R.id.imageViewMenu);
        imageViewMenu.setImageResource(values.get(position).getImage());
        TextView title = row.findViewById(R.id.textViewTitle);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.getDpiFromPixel(context, 49));
        layout.setLayoutParams(params);
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        title.setTextColor(context.getResources().getColor(R.color.white));
        title.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_BOLD));
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        title.setText(values.get(position).getName());
        title.setPadding(Utils.getDpiFromPixel(context, 16), 0, 0, 0);

        RelativeLayout viewBottom = row.findViewById(R.id.viewBottom);

        View view = row.findViewById(R.id.viewLine);
        view.setBackgroundColor(Color.WHITE);
        int screenHeight = Utils.getWindowHeight(context);
        screenHeight = screenHeight + (int) (screenHeight * .04);
        int bottomViewSize = screenHeight - Utils.getDpiFromPixel(context, values.size() * 50);

        if (position == values.size() - 1) {
            view.setVisibility(View.GONE);
            viewBottom.setVisibility(View.VISIBLE);
            imageViewMenu.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, bottomViewSize);
            layout.setLayoutParams(params);
            layout.setAlpha(0.80f);
        } else {
            view.setVisibility(View.VISIBLE);
            viewBottom.setVisibility(View.GONE);
            imageViewMenu.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.getDpiFromPixel(context, 50));
            layout.setLayoutParams(params);
            layout.setAlpha(0.95f);
        }
        // And finally return your dynamic (or custom) view for each spinner item
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Prefs.getBoolean(context, Prefs.KEY_USER_LOGEDIN, false)) {
                    switch (position) {
                        case 0:
                            if (((MainActivity) context).getCurrentFragment() instanceof HomeFragment) {
                                ((MainActivity) context).pauseYoutubeVideo();
                            } else {
                                ((MainActivity) context).showHome();
                            }
                            break;
                        case 1:
                            if (((MainActivity) context).getCurrentFragment() instanceof ProfileFragment) {

                            } else {
                                ((MainActivity) context).profileUser();
                            }
                            break;
                        case 2:
                            ((MainActivity) context).logoutUser();
                            break;
                        case 3:
                            if (((MainActivity) context).getCurrentFragment() instanceof FeedbackFragment) {

                            } else {
                                ((MainActivity) context).showFeedbackFragment();
                            }
                            break;
                        case 4:
                            if (((MainActivity) context).getCurrentFragment() instanceof TermsAndConditionsFragment) {

                            } else {
                                ((MainActivity) context).showTermsAndConditionsFragment();
                            }
                            break;
                    }
                } else {
                    switch (position) {
                        case 0:
                            if (((MainActivity) context).getCurrentFragment() instanceof HomeFragment) {
                                ((MainActivity) context).pauseYoutubeVideo();
                            } else {
                                ((MainActivity) context).showHome();
                            }
                            break;
                        case 1:
                            ((MainActivity) context).loginUser();
                            break;
                        case 2:
                            ((MainActivity) context).registerUser();
                            break;
                        case 3:
                            if (((MainActivity) context).getCurrentFragment() instanceof FeedbackFragment) {

                            } else {
                                ((MainActivity) context).showFeedbackFragment();
                            }
                            break;
                        case 4:
                            if (((MainActivity) context).getCurrentFragment() instanceof TermsAndConditionsFragment) {

                            } else {
                                ((MainActivity) context).showTermsAndConditionsFragment();
                            }
                            break;
                    }
                }

                ((MainActivity) context).dismissSpinner(position);
            }
        });
        return row;
    }
}