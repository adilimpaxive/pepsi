package com.pepsi.battleofthebands.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.entity.Gallery;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private ArrayList<Gallery> galleries;
    private Context context;

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public GalleryAdapter(Context context, ArrayList<Gallery> galleries) {
        this.context = context;
        this.galleries = galleries;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new GalleryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, final int position) {

        int imageHeight = (Utils.getWindowWidth(context) - Utils.getDpiFromPixel(context, 40));
        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (imageHeight / 3));
        holder.imageViewAlbum.setLayoutParams(paramsImage);

        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(position);
            }
        });
        Picasso.with(context).load(galleries.get(position).getLarge_image()).into(holder.imageViewAlbum);
    }

    @Override
    public int getItemCount() {
        return galleries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewAlbum;
        RelativeLayout layoutMain;

        public ViewHolder(View row) {
            super(row);
            imageViewAlbum = row.findViewById(R.id.imageViewGallery);
            layoutMain = row.findViewById(R.id.layoutMain);
        }
    }
}
