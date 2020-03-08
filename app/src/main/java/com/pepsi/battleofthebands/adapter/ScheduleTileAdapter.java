package com.pepsi.battleofthebands.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.entity.Schedule;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ScheduleTileAdapter extends RecyclerView.Adapter<ScheduleTileAdapter.ViewHolder> {

    private ArrayList<Schedule> schedule;
    private Context context;

    ScheduleTileAdapter(Context context, ArrayList<Schedule> schedule) {
        this.context = context;
        this.schedule = schedule;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule_tile, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        int screenWidth = (Utils.getWindowWidth(context) - Utils.getDpiFromPixel(context, 60));
        int imageHeight = (screenWidth / 5);
        int textHeight = imageHeight / 3;

        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(imageHeight, (imageHeight + textHeight));
        if (position == (schedule.size() - 1)) {
            para.setMargins(0, 0, 0, 0); //left,top,right, bottom
        } else {
            para.setMargins(0, 0, Utils.getDpiFromPixel(context, 10), 0); //left,top,right, bottom
        }
        holder.layoutMain.setLayoutParams(para);

        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(imageHeight, imageHeight);
        holder.imageViewSchedule.setLayoutParams(paramsImage);
        Picasso.with(context).load(schedule.get(position).getIcon()).into(holder.imageViewSchedule);
        holder.textViewScheduleName.setText(schedule.get(position).getName());
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            holder.textViewScheduleName.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return schedule.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewSchedule;
        RelativeLayout layoutMain;
        TextView textViewScheduleName;

        ViewHolder(View row) {
            super(row);
            imageViewSchedule = row.findViewById(R.id.imageViewSchedule);
            textViewScheduleName = row.findViewById(R.id.textViewScheduleName);
            layoutMain = row.findViewById(R.id.layoutMain);
        }
    }
}