package com.pepsi.battleofthebands.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.entity.Member;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class BandMemberAdapter extends RecyclerView.Adapter<BandMemberAdapter.ViewHolder> {

    private ArrayList<Member> members;
    private Context context;

    public BandMemberAdapter(Context context, ArrayList<Member> items) {
        this.context = context;
        this.members = items;
    }

    @NonNull
    @Override
    public BandMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_band_member, parent, false);
        return new BandMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BandMemberAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.layoutMain.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, Utils.getDpiFromPixel(context, 48)));

        viewHolder.textViewName.setText(members.get(position).getName());
        viewHolder.textViewDesignation.setText(members.get(position).getDesignation());
        viewHolder.textViewDesignation.setPadding(Utils.getDpiFromPixel(context, 16), 0, 0, 0);
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            viewHolder.textViewName.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.textViewDesignation.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            viewHolder.textViewName.setTextColor(ContextCompat.getColor(context, R.color.theme_background_color));
            viewHolder.textViewDesignation.setTextColor(ContextCompat.getColor(context, R.color.theme_background_color));
        }
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewDesignation;
        LinearLayout layoutMain;

        ViewHolder(View row) {
            super(row);

            layoutMain = row.findViewById(R.id.layoutMain);

            textViewName = row.findViewById(R.id.textViewName);
            textViewDesignation = row.findViewById(R.id.textViewDesignation);

            textViewName.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
            textViewDesignation.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
        }
    }
}
