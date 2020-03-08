package com.pepsi.battleofthebands.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.entity.SocialMedia;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.ViewHolder> {

    private ArrayList<SocialMedia> socialMedia;
    private Context context;

    public SocialMediaAdapter(Context context, ArrayList<SocialMedia> socialMedia) {
        this.context = context;
        this.socialMedia = socialMedia;
    }

    @NonNull
    @Override
    public SocialMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow_us, parent, false);
        return new SocialMediaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SocialMediaAdapter.ViewHolder viewHolder, final int position) {
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
        } else {
            viewHolder.layoutDivider.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        Picasso.with(context).load(socialMedia.get(position).getIcon()).into(viewHolder.imageViewSocialMedia);
        int padding16 = Utils.getDpiFromPixel(context, 16);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 48), Utils.getDpiFromPixel(context, 48));
        layoutParams.setMargins(padding16, 0, padding16, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        viewHolder.layoutImage.setLayoutParams(layoutParams);

        viewHolder.textViewFollowUsName.setText(socialMedia.get(position).getName());

        para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (Utils.getDpiFromPixel(context, 1) / 2));
        para.setMargins(padding16, 0, padding16, 0);
        viewHolder.layoutDivider.setLayoutParams(para);
        if (position == 0) {
            viewHolder.layoutDivider.setVisibility(View.GONE);
        } else {
            viewHolder.layoutDivider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return socialMedia.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFollowUsName;
        RelativeLayout layoutMain, layoutDivider, layoutImage;
        ImageView imageViewSocialMedia;

        ViewHolder(View row) {
            super(row);
            textViewFollowUsName = row.findViewById(R.id.textViewFollowUsName);
            layoutMain = row.findViewById(R.id.layoutMain);
            layoutDivider = row.findViewById(R.id.layoutDivider);
            layoutImage = row.findViewById(R.id.layoutImage);

            imageViewSocialMedia = row.findViewById(R.id.imageViewSocialMedia);
            textViewFollowUsName.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
        }
    }

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
