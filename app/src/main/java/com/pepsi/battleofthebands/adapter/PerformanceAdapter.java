package com.pepsi.battleofthebands.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.entity.Episode;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PerformanceAdapter extends RecyclerView.Adapter<PerformanceAdapter.ViewHolder> {

    private ArrayList<Song> songs;
    EpisodesAdapter episodesAdapte;



    private Context context;
    private int selectedPosition;




    public PerformanceAdapter(Context context, ArrayList<Song> songs, int selectedPosition) {
        this.context = context;
        this.songs = songs;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public PerformanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_performance, parent, false);
        return new PerformanceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PerformanceAdapter.ViewHolder viewHolder, final int position) {
        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getDpiFromPixel(context, 72));
        viewHolder.layoutMain.setLayoutParams(para);
        viewHolder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(position);
            }
        });

        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            viewHolder.layoutDivider.setBackgroundColor(context.getResources().getColor(R.color.white));
            if (position == selectedPosition) {
                viewHolder.textViewName.setTextColor(context.getResources().getColor(R.color.menu_border_color));
                viewHolder.textViewDescription.setTextColor(context.getResources().getColor(R.color.menu_border_color));
            } else {
                viewHolder.textViewName.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.textViewDescription.setTextColor(context.getResources().getColor(R.color.white));
            }
        } else {
            viewHolder.layoutDivider.setBackgroundColor(context.getResources().getColor(R.color.theme_text_color));
            if (position == selectedPosition) {
                viewHolder.textViewName.setTextColor(context.getResources().getColor(R.color.theme_text_color));
                viewHolder.textViewDescription.setTextColor(context.getResources().getColor(R.color.theme_text_color));
            } else {
                viewHolder.textViewName.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.textViewDescription.setTextColor(context.getResources().getColor(R.color.black));
            }
        }

        int padding16 = Utils.getDpiFromPixel(context, 16);
        viewHolder.layoutMain.setTag(songs.get(position));

        Picasso.with(context).load(songs.get(position).getThumbnail()).into(viewHolder.imageViewThumbnail);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 48), Utils.getDpiFromPixel(context, 48));
        layoutParams.setMargins(padding16, 0, padding16, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        viewHolder.layoutImage.setLayoutParams(layoutParams);
         viewHolder.textViewDescription.setTag("");

        viewHolder.textViewName.setText(songs.get(position).getName());
        //viewHolder.textViewDescription.setText("Episode " +episodes.get(0).getTitle() );
        viewHolder.textViewDescription.setText(songs.get(position).getEpisode().getTitle());
        viewHolder.textViewName.setPadding(0, 0, padding16, 0);
        viewHolder.textViewDescription.setPadding(0, 0, padding16, 0);

        para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (Utils.getDpiFromPixel(context, 1) / 2));
        para.setMargins(padding16, 0, padding16, 0);
        viewHolder.layoutDivider.setLayoutParams(para);
        viewHolder.layoutDivider.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return songs.size();

    }

    public void setNewIndex(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewDescription;
        RelativeLayout layoutMain, layoutDivider, layoutImage;
        ImageView imageViewThumbnail;

        ViewHolder(View row) {
            super(row);
            textViewName = row.findViewById(R.id.textViewName);
            textViewDescription = row.findViewById(R.id.textViewDescription);
            layoutMain = row.findViewById(R.id.layoutMain);
            layoutDivider = row.findViewById(R.id.layoutDivider);
            layoutImage = row.findViewById(R.id.layoutImage);

            imageViewThumbnail = row.findViewById(R.id.imageViewThumbnail);

            textViewName.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_BOLD));
            textViewDescription.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
        }
    }

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
