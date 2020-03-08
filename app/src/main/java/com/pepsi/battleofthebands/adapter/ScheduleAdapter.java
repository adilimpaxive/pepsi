package com.pepsi.battleofthebands.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.entity.ScheduleTime;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private ArrayList<ScheduleTime> schedules;
    private Context context;

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ScheduleAdapter(Context context, ArrayList<ScheduleTime> schedules) {
        this.context = context;
        this.schedules = schedules;
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleAdapter.ViewHolder holder, final int position) {

        int screenWidth = (Utils.getWindowWidth(context) - Utils.getDpiFromPixel(context, 60));
        int imageHeight = (screenWidth / 5);
        int textHeight = imageHeight / 3;
        final ScheduleTime scheduleTime = schedules.get(position);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setOnFlingListener(null);
        holder.textViewTitle.setPadding(Utils.getDpiFromPixel(context, 16), Utils.getDpiFromPixel(context, 16), 0, Utils.getDpiFromPixel(context, 10));
        try {
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            holder.recyclerView.setLayoutParams(rowParams);
            ScheduleTileAdapter homeRecyclerAdapterNew = new ScheduleTileAdapter(context, scheduleTime.getChannels());
            holder.recyclerView.setAdapter(homeRecyclerAdapterNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (position == (schedules.size() - 1))
            holder.recyclerView.setPadding(Utils.getDpiFromPixel(context, 16), 0, Utils.getDpiFromPixel(context, 16), Utils.getDpiFromPixel(context, 16));
        else
            holder.recyclerView.setPadding(Utils.getDpiFromPixel(context, 16), 0, Utils.getDpiFromPixel(context, 16), 0);
        try {
            holder.textViewTitle.setText(scheduleTime.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            holder.textViewTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle;
        RecyclerView recyclerView;

        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            textViewTitle.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_BOLD));
        }
    }
}
