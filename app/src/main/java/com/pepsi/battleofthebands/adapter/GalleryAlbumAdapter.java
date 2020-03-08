package com.pepsi.battleofthebands.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.pepsi.battleofthebands.entity.GalleryAlbum;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAlbumAdapter extends RecyclerView.Adapter<GalleryAlbumAdapter.ViewHolder> {
    private ArrayList<GalleryAlbum> galleries;
    private Context context;

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public GalleryAlbumAdapter(Context context, ArrayList<GalleryAlbum> galleries) {
        this.context = context;
        this.galleries = galleries;
    }

    @Override
    public GalleryAlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_album, parent, false);
        return new GalleryAlbumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAlbumAdapter.ViewHolder holder, final int position) {

        int imageHeight = (Utils.getWindowWidth(context) - Utils.getDpiFromPixel(context, 30));
        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (imageHeight / 2));
        holder.layoutMain.setLayoutParams(paramsImage);
//        holder.imageViewAlbum.setLayoutParams(paramsImage);
        paramsImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (imageHeight / 10));
        paramsImage.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        holder.layoutGalleryAlbumShare.setLayoutParams(paramsImage);
        holder.textViewAlbumName.setText(galleries.get(position).getName());
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            holder.textViewAlbumName.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(position);
            }
        });
        Picasso.with(context).load(galleries.get(position).getImage()).into(holder.imageViewAlbum);
        holder.imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = galleries.get(position).getName() + ", a Bob Moments" + " on Pepsi";
                String shareLink = galleries.get(position).getShare_url();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, description + "\n" + shareLink);
                context.startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAlbum, imageViewShare;
        RelativeLayout layoutMain, layoutGalleryAlbumShare;
        TextView textViewAlbumName;

        public ViewHolder(View row) {
            super(row);
            imageViewAlbum = row.findViewById(R.id.imageViewGallery);
            imageViewShare = row.findViewById(R.id.imageViewShare);
            textViewAlbumName = row.findViewById(R.id.textViewAlbumName);
            layoutMain = row.findViewById(R.id.layoutMain);
            layoutGalleryAlbumShare = row.findViewById(R.id.layoutGalleryAlbumShare);
        }
    }
}
