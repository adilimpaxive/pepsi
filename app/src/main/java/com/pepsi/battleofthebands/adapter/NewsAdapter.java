package com.pepsi.battleofthebands.adapter;

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
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.entity.News;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

    private ArrayList<News> newsList;
    private Context context;

    public NewsAdapter(Context context, ArrayList<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder viewHolder, final int position) {
        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Utils.getDpiFromPixel(context, 72));
        viewHolder.layoutMain.setLayoutParams(para);
        viewHolder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(position);
            }
        });
        int padding16 = Utils.getDpiFromPixel(context, 16);
        viewHolder.layoutMain.setTag(newsList.get(position));

        Picasso.with(context).load(newsList.get(position).getThumbnail()).into(viewHolder.imageViewNews);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 48), Utils.getDpiFromPixel(context, 48));
        layoutParams.setMargins(padding16, 0, padding16, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        viewHolder.layoutImage.setLayoutParams(layoutParams);

        viewHolder.textViewNewsTitle.setText(newsList.get(position).getTitle());
        viewHolder.textViewNewsDescription.setText(newsList.get(position).getTitle());
        viewHolder.textViewNewsDate.setText(newsList.get(position).getDate());

        viewHolder.textViewNewsTitle.setPadding(0, 0, padding16, 0);
        viewHolder.textViewNewsDescription.setPadding(0, 0, padding16, 0);
        viewHolder.textViewNewsDate.setPadding(0, 0, padding16, 0);

        para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (Utils.getDpiFromPixel(context, 1) / 2));
        para.setMargins(padding16, 0, padding16, 0);
        viewHolder.layoutDivider.setLayoutParams(para);
        if (position == 0) {
            viewHolder.layoutDivider.setVisibility(View.GONE);
        } else {
            viewHolder.layoutDivider.setVisibility(View.VISIBLE);
        }
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            viewHolder.textViewNewsTitle.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.textViewNewsDescription.setTextColor(ContextCompat.getColor(context, R.color.white));
            viewHolder.textViewNewsDate.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNewsTitle, textViewNewsDescription, textViewNewsDate;
        RelativeLayout layoutMain, layoutDivider, layoutImage;
        ImageView imageViewNews;

        ViewHolder(View row) {
            super(row);
            textViewNewsTitle = row.findViewById(R.id.textViewNewsTitle);
            textViewNewsDescription = row.findViewById(R.id.textViewNewsDescription);
            textViewNewsDate = row.findViewById(R.id.textViewNewsDate);
            layoutMain = row.findViewById(R.id.layoutMain);
            layoutDivider = row.findViewById(R.id.layoutDivider);
            layoutImage = row.findViewById(R.id.layoutImage);

            imageViewNews = row.findViewById(R.id.imageViewNews);

            textViewNewsTitle.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_BOLD));
            textViewNewsDescription.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
        }
    }

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
