package com.pepsi.battleofthebands.adapter;

import android.content.Context;
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
import com.pepsi.battleofthebands.entity.Judges;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Muhammad Kashan on 3/28/2018.
 */

public class JudgesAdapter extends RecyclerView.Adapter<JudgesAdapter.ViewHolder> {
    private ArrayList<Judges> judges;
    private Context context;

    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public JudgesAdapter(Context context, ArrayList<Judges> judges) {
        this.context = context;
        this.judges = judges;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_judge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        int imageHeight = (Utils.getWindowWidth(context) - Utils.getDpiFromPixel(context, 48));
        RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (imageHeight / 1.1));
        holder.imageViewJudge.setLayoutParams(paramsImage);
//
//        RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        int dpValue10 = Utils.getDpiFromPixel(context, 16);
//        int dpValue5 = Utils.getDpiFromPixel(context, 8);
//        if (position % 2 == 0)
//            para.setMargins(dpValue10, 0, dpValue5, dpValue10);
//        else
//            para.setMargins(dpValue5, 0, dpValue10, dpValue10);
//        holder.layoutMain.setLayoutParams(para);
        holder.layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClicked(position);
            }
        });

        holder.textViewJudgeName.setText(judges.get(position).getName().toUpperCase());
        int padding = Utils.getDpiFromPixel(context, 10);
        holder.textViewJudgeName.setPadding(0, padding, 0, padding);
        if (Prefs.getInt(context, Prefs.KEY_THEME, 0) == 0) {
            holder.textViewJudgeName.setBackgroundColor(ContextCompat.getColor(context, R.color.judge_text_background));
        } else {
            holder.textViewJudgeName.setBackgroundColor(ContextCompat.getColor(context, R.color.theme_background_color));
        }
        Picasso.with(context).load(judges.get(position).getImage()).into(holder.imageViewJudge);
    }

    @Override
    public int getItemCount() {
        return judges.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewJudgeName;
        ImageView imageViewJudge;
        RelativeLayout layoutMain;

        public ViewHolder(View row) {
            super(row);
            textViewJudgeName = row.findViewById(R.id.textViewJudgeName);
            imageViewJudge = row.findViewById(R.id.imageViewJudge);
            layoutMain = row.findViewById(R.id.layoutMain);

            // set fonts
            textViewJudgeName.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_BOLD));
        }
    }
}
