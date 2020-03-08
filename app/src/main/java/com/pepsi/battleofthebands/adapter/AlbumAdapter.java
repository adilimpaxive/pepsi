package com.pepsi.battleofthebands.adapter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.MainActivity;
import com.pepsi.battleofthebands.app.PepsiApplication;
import com.pepsi.battleofthebands.callback.OnAlbumItemClickListener;
import com.pepsi.battleofthebands.db.DownloadedSongsTableDataManager;
import com.pepsi.battleofthebands.dialogs.DialogHelper;
import com.pepsi.battleofthebands.entity.DownloadedSong;
import com.pepsi.battleofthebands.entity.Seasons;
import com.pepsi.battleofthebands.entity.Song;
import com.pepsi.battleofthebands.networkcalls.OKHttpApi;
import com.pepsi.battleofthebands.networkcalls.ResponseApi;
import com.pepsi.battleofthebands.offlinemedia.SongsDownloader;
import com.pepsi.battleofthebands.services.MusicService;
import com.pepsi.battleofthebands.utils.PFonts;
import com.pepsi.battleofthebands.utils.Prefs;
import com.pepsi.battleofthebands.utils.URLManager;
import com.pepsi.battleofthebands.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private ArrayList<Song> songs;
    private Context context;
    private PopupWindow popupWindow;

    public AlbumAdapter(Context context, ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_songs, parent, false);
        return new AlbumAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumAdapter.ViewHolder viewHolder, final int position) {
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
            if (MusicService.getInstance() == null && position == 0) {
                viewHolder.textViewSongName.setTextColor(context.getResources().getColor(R.color.menu_border_color));
                viewHolder.textViewArtistName.setTextColor(context.getResources().getColor(R.color.menu_border_color));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.menu_border_color));
            } else if (MusicService.getInstance() != null && MusicService.getInstance().currentPlayingSong.getSongID().equalsIgnoreCase(songs.get(position).getSongID())) {
                viewHolder.textViewSongName.setTextColor(context.getResources().getColor(R.color.menu_border_color));
                viewHolder.textViewArtistName.setTextColor(context.getResources().getColor(R.color.menu_border_color));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.menu_border_color));
            } else {
                viewHolder.textViewSongName.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.textViewArtistName.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.white));
            }
        } else {
            viewHolder.layoutDivider.setBackgroundColor(context.getResources().getColor(R.color.theme_text_color));
            if (MusicService.getInstance() == null && position == 0) {
                viewHolder.textViewSongName.setTextColor(context.getResources().getColor(R.color.theme_text_color));
                viewHolder.textViewArtistName.setTextColor(context.getResources().getColor(R.color.theme_text_color));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.theme_text_color));
            } else if (MusicService.getInstance() != null && MusicService.getInstance().currentPlayingSong.getSongID().equalsIgnoreCase(songs.get(position).getSongID())) {
                viewHolder.textViewSongName.setTextColor(context.getResources().getColor(R.color.theme_text_color));
                viewHolder.textViewArtistName.setTextColor(context.getResources().getColor(R.color.theme_text_color));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.theme_text_color));
            } else {
                viewHolder.textViewSongName.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.textViewArtistName.setTextColor(context.getResources().getColor(R.color.black));
                viewHolder.textViewDuration.setTextColor(context.getResources().getColor(R.color.black));
            }
        }

        Picasso.with(context).load(songs.get(position).getThumbnail()).into(viewHolder.imageViewSong);
        int padding16 = Utils.getDpiFromPixel(context, 16);
        int padding10 = Utils.getDpiFromPixel(context, 10);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utils.getDpiFromPixel(context, 48), Utils.getDpiFromPixel(context, 48));
        layoutParams.setMargins(padding16, 0, padding16, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        viewHolder.layoutImage.setLayoutParams(layoutParams);

        viewHolder.textViewDuration.setText((songs.get(position).getDuration()));
        viewHolder.textViewDuration.setPadding(padding16, 0, padding16, 0);

        viewHolder.textViewSongName.setText(songs.get(position).getName());
        viewHolder.textViewArtistName.setText(songs.get(position).getSinger().getName());
        viewHolder.imageViewMoreOption.setTag(songs.get(position));

        viewHolder.imageViewMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDropDownPopUpWindow(v);
            }
        });

//        viewHolder.textViewDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Prefs.getBoolean(context, Prefs.KEY_USER_LOGEDIN, false)) {
//                    Song song = songs.get(position);
//                    File songFile = new File(context.getExternalFilesDir("kashan") + File.separator + song.getSongID());
//                    if (!songFile.exists()) {
//                        Intent intent = new Intent(context, DownloadService.class);
//                        intent.putExtra(Song.KEY, song);
//                        context.startService(intent);
//                    } else {
//                        DialogHelper.showDialogCancelSong(context, context.getResources().getString(R.string.track_remove), song);
//                    }
//                } else {
//                    DialogHelper.showSignUpAlert(context, context.getResources().getString(R.string.login_error));
//                }
//            }
//        });

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
        return songs.size();
    }

    public void filterData(ArrayList<Song> songArrayList) {
        songs = songArrayList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSongName;
        TextView textViewArtistName;
        RelativeLayout layoutMain, layoutDivider, layoutImage;
        ImageView imageViewSong, imageViewMoreOption;
        TextView textViewDuration;

        ViewHolder(View row) {
            super(row);
            textViewSongName = row.findViewById(R.id.textViewSongName);
            textViewArtistName = row.findViewById(R.id.textViewArtistName);
            layoutMain = row.findViewById(R.id.layoutMain);
            layoutDivider = row.findViewById(R.id.layoutDivider);
            layoutImage = row.findViewById(R.id.layoutImage);

            imageViewSong = row.findViewById(R.id.imageViewSong);
            imageViewMoreOption = row.findViewById(R.id.imageViewMoreOption);

            textViewDuration = row.findViewById(R.id.textViewDuration);

            textViewSongName.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
            textViewArtistName.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
            textViewDuration.setTypeface(PFonts.getInstance(itemView.getContext()).getFont(PFonts.FONT_MEDIUM));
        }
    }

    private void showDropDownPopUpWindow(final View view) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View popUpView = inflater.inflate(R.layout.popup_window_option_song, null);

        TextView downloadTextView = popUpView.findViewById(R.id.textViewDownload);
        TextView shareTextView = popUpView.findViewById(R.id.textViewShare);

        downloadTextView.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
        shareTextView.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));

        final Song selectedSong = (Song) view.getTag();

        final DownloadedSongsTableDataManager dataManager = DownloadedSongsTableDataManager.getInstance();
        final DownloadedSong downloadedSong = dataManager.findSong(selectedSong.getSongID());

        if (downloadedSong == null) {
            downloadTextView.setText(context.getResources().getString(R.string.download));
        } else {
            if (downloadedSong.getDownloadingStatus().equals(SongsDownloader.STATUS_DOWNLOADED)) {
                downloadTextView.setText(context.getResources().getString(R.string.remove));
            } else {
                downloadTextView.setText(context.getResources().getString(R.string.cancel));
            }
        }
        downloadTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (Prefs.getBoolean(context, Prefs.KEY_USER_LOGEDIN, false)) {
                    if (downloadedSong == null) {
                        if (dataManager.getAllSongs(SongsDownloader.STATUS_DOWNLOADING).size() < 3) {
                            SongsDownloader.downloadSong(context, selectedSong, SongsDownloader.STATUS_DOWNLOADING, true);
                        } else {
                            SongsDownloader.queuedSong(context, selectedSong, SongsDownloader.STATUS_QUEUE);
                        }
                    } else {
                        switch (downloadedSong.getDownloadingStatus()) {
                            case SongsDownloader.STATUS_DOWNLOADED:
                                DialogHelper.showDialogCancelSong(context, context.getString(R.string.track_remove), downloadedSong);
                                break;
                            case SongsDownloader.STATUS_QUEUE:
                                DialogHelper.showDialogCancelSong(context, context.getString(R.string.track_queue), downloadedSong);
                                break;
                            default:
                                DialogHelper.showDialogCancelSong(context, context.getString(R.string.track_cancel), downloadedSong);
                                break;
                        }
                    }
                } else {
                    DialogHelper.showSignUpAlert(context, context.getResources().getString(R.string.login_error));
                }
            }
        });

        shareTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                String description = selectedSong.getName() + ", a song by " + selectedSong.getSinger().getName() + " on Pepsi";
                String shareLink = selectedSong.getAudio();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, description + "\n" + shareLink);
                context.startActivity(Intent.createChooser(shareIntent, "Share"));
            }
        });

        popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popup_window_bg));
            popupWindow.setElevation(Utils.getDpiFromPixel(context, 4));
        } else {
            popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.popup_window_bg_line));
        }
        popUpView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        Rect locationView = new Rect();
        locationView.left = location[0];
        locationView.top = location[1];
        locationView.right = (int) (locationView.left - (view.getWidth() * 2.2));
        locationView.bottom = locationView.top + (view.getHeight() / 2);
        int X = (int) (Utils.getWindowWidth(context) * .02);
        popupWindow.showAtLocation(view, Gravity.TOP | Gravity.END, X, locationView.bottom);
    }



    private OnAlbumItemClickListener itemClickListener;

    public void setItemClickListener(OnAlbumItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
