package com.pepsi.battleofthebands.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.entity.Episode;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;

import java.util.ArrayList;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder> {

    private ArrayList<Episode> episodes;
    private Context context;
    private int selectedPosition;

    public EpisodesAdapter(Context context, ArrayList<Episode> episodes, int selectedPosition) {
        this.context = context;
        this.episodes = episodes;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public EpisodesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false);
        return new EpisodesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EpisodesAdapter.ViewHolder viewHolder, final int position) {
        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getDpiFromPixel(context, 72));
        viewHolder.layoutMain.setLayoutParams(para);
        viewHolder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                itemClickListener.onItemClicked(position);
            }
        });
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            viewHolder.layoutDivider.setBackgroundColor(context.getResources().getColor(R.color.white));
            if (position == selectedPosition) {
                viewHolder.songNameTextView.setTextColor(context.getResources().getColor(R.color.menu_border_color));
                viewHolder.episodeTextView.setTextColor(context.getResources().getColor(R.color.menu_border_color));
                viewHolder.textViewCount.setTextColor(context.getResources().getColor(R.color.menu_border_color));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.menu_border_color));
            } else {
                viewHolder.songNameTextView.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.episodeTextView.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.textViewCount.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.white));
            }
        } else {
            viewHolder.layoutDivider.setBackgroundColor(context.getResources().getColor(R.color.theme_text_color));
            if (position == selectedPosition) {
                viewHolder.songNameTextView.setTextColor(context.getResources().getColor(R.color.theme_text_color));
                viewHolder.episodeTextView.setTextColor(context.getResources().getColor(R.color.theme_text_color));
                viewHolder.textViewCount.setTextColor(context.getResources().getColor(R.color.theme_text_color));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.theme_text_color));
            } else {
                viewHolder.songNameTextView.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.episodeTextView.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.textViewCount.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.black));
            }
        }


        viewHolder.layoutMain.setTag(episodes.get(position));
        para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        if (position == 0) {
            para.setMargins(0, 0, 0, 0);
        } else {
            int marginTop = (Utils.getDpiFromPixel(context, 1) / 2);
            para.setMargins(0, marginTop, 0, 0);
        }
        viewHolder.layoutInner.setLayoutParams(para);

        viewHolder.textViewCount.setText((position + 1) + "");
        viewHolder.textViewCount.setLayoutParams(new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 42), RelativeLayout.LayoutParams.MATCH_PARENT));

        viewHolder.textViewDuration.setText((episodes.get(position).getDuration()));
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 48), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, 0, Utils.getDpiFromPixel(context, 16), 0);
        viewHolder.layoutDuration.setLayoutParams(layoutParams);

        viewHolder.episodeTextView.setTag(episodes.get(position));

        viewHolder.songNameTextView.setText((episodes.get(position).getTitle()));
        //viewHolder.episodeTextView.setText(episodes.get(position).getNumber());
        viewHolder.episodeTextView.setText((episodes.get(position).getGroup()));
        viewHolder.songNameTextView.setPadding(0, 0, Utils.getDpiFromPixel(context, 16), 0);
        viewHolder.episodeTextView.setPadding(0, 0, Utils.getDpiFromPixel(context, 16), 0);

        para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (Utils.getDpiFromPixel(context, 1) / 2));
        para.setMargins(Utils.getDpiFromPixel(context, 42), 0, Utils.getDpiFromPixel(context, 16), 0);
        viewHolder.layoutDivider.setLayoutParams(para);
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public void setNewIndex(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView songNameTextView;
        TextView episodeTextView;
        RelativeLayout layoutMain, layoutDuration, layoutInner, layoutDivider;
        TextView textViewCount, textViewDuration;

        ViewHolder(View row) {
            super(row);
            songNameTextView = row.findViewById(R.id.songNameTextView);
            episodeTextView = row.findViewById(R.id.artistNameTextView);
            layoutMain = row.findViewById(R.id.layoutMain);
            layoutDuration = row.findViewById(R.id.layoutDuration);
            layoutInner = row.findViewById(R.id.layoutInner);
            layoutDivider = row.findViewById(R.id.layoutDivider);

            textViewCount = row.findViewById(R.id.textViewCount);
            textViewDuration = row.findViewById(R.id.textViewDuration);

            songNameTextView.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
            episodeTextView.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
            textViewCount.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
            textViewDuration.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
        }
    }

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
