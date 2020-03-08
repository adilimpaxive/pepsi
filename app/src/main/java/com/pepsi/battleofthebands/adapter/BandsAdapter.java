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
import com.pepsi.battleofthebands.entity.Bands;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BandsAdapter extends RecyclerView.Adapter<BandsAdapter.ViewHolder> {
    private ArrayList<Bands> bands;
    private Context context;

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public BandsAdapter(Context context, ArrayList<Bands> bands) {
        this.context = context;
        this.bands = bands;
    }

    @Override
    public BandsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_band, parent, false);
        return new BandsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BandsAdapter.ViewHolder holder, final int position) {

        int imageHeight = (Utils.getWindowWidth(context) - Utils.getDpiFromPixel(context, 30));
        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (imageHeight / 2));
        holder.imageViewAlbum.setLayoutParams(paramsImage);

        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(position);
            }
        });
        Picasso.with(context).load(bands.get(position).getLarge_logo()).into(holder.imageViewAlbum);
    }

    @Override
    public int getItemCount() {
        return bands.size();
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