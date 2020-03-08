package com.pepsi.battleofthebands.dialogs;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.activities.LoginActivity;
import com.pepsi.battleofthebands.db.DownloadedSongsTableDataManager;
import com.pepsi.battleofthebands.entity.DownloadedSong;
import com.pepsi.battleofthebands.utils.PFonts;

import java.io.File;

public class DialogHelper {
    private static ProgressDialog mProgressDialog;
    private static Dialog mFullScreenDialog;

    private DialogHelper() {
    }

    public static void showFullScreenProgressDialog(Context context) {
        dismissFullScreenProgressDialog();
        dismissProgressDialog();
        if (context != null) {
            mFullScreenDialog = new Dialog(context);
            mFullScreenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mFullScreenDialog.setContentView(R.layout.progress_bar);
            try {
                mFullScreenDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFullScreenDialog.setCancelable(false);
            mFullScreenDialog.show();
        }
    }

    public final static void dismissFullScreenProgressDialog() {
        if (mFullScreenDialog != null && mFullScreenDialog.isShowing())
            mFullScreenDialog.dismiss();
    }

    /**
     * Show progress dialog without title
     *
     * @param context    context
     * @param message    message
     * @param cancelable cancelable
     */
    public final static void showProgressDialog(Context context, String message, boolean cancelable) {
        dismissFullScreenProgressDialog();
        dismissProgressDialog();
        if (context != null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(cancelable);
            mProgressDialog.show();
        }
    }

    /**
     * Dismiss Progress dialog
     */
    public final static void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    public static void showDialogError(final Context context, String message) {
        if (context != null)
            try {
                final Dialog customDialog;
                customDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                customDialog.setContentView(R.layout.layout_dialog_error);
                customDialog.show();

                TextView tvTitle = customDialog.findViewById(R.id.textViewTitle);
                TextView tvMessage = customDialog.findViewById(R.id.textViewMessage);
                TextView tvDescription = customDialog.findViewById(R.id.textViewDiscription);
                tvMessage.setText(message);
                tvTitle.setText("ERROR");
                tvTitle.setTextColor(context.getResources().getColor(R.color.colorAccent));
                tvDescription.setText("Please retry");
                Button yes = customDialog.findViewById(R.id.buttonYes);

                yes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
                tvTitle.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
                tvMessage.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_LIGHT));
                tvDescription.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_LIGHT));

                yes.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
    }

    public static void showSignUpAlert(final Context context, String message) {
        if (context != null)
            try {
                final Dialog customDialog;
                customDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                customDialog.setContentView(R.layout.layout_dialog_option_one);
                customDialog.show();

                View view = customDialog.findViewById(R.id.mainView);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
                TextView title = customDialog.findViewById(R.id.textViewTitle);
                TextView tvMessage = customDialog.findViewById(R.id.textViewMessage);
                TextView description = customDialog.findViewById(R.id.textViewDiscription);

                tvMessage.setText(message);
                title.setText("LOGIN REQUIRED");
                title.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
                tvMessage.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_LIGHT));
                description.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_LIGHT));

                Button yes = customDialog.findViewById(R.id.buttonYes);
                final Button no = customDialog.findViewById(R.id.buttonNo);
                yes.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
                no.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
                yes.setText("Login / Signup");
                no.setText("Cancel");

                yes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        context.startActivity(new Intent(context.getApplicationContext(), LoginActivity.class));
//                        ((MainActivity) context).finish();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void showDialogCancelSong(final Context context, String message, final DownloadedSong downloadedSong) {
        if (context != null)
            try {
                final Dialog customDialog;
                customDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
                customDialog.setContentView(R.layout.layout_dialog_option_one);
                customDialog.show();

                TextView title = customDialog.findViewById(R.id.textViewTitle);
                TextView tvMessage = customDialog.findViewById(R.id.textViewMessage);
                TextView description = customDialog.findViewById(R.id.textViewDiscription);

                tvMessage.setText(message);

                title.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
                tvMessage.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_LIGHT));
                description.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_LIGHT));

                Button yes = customDialog.findViewById(R.id.buttonYes);
                Button no = customDialog.findViewById(R.id.buttonNo);
                yes.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));
                no.setTypeface(PFonts.getInstance(context).getFont(PFonts.FONT_MEDIUM));

                yes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            File songFile = new File(context.getExternalFilesDir("kashan") + File.separator + downloadedSong.getSongID());
                            songFile.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DownloadedSongsTableDataManager downloadSongsManager = DownloadedSongsTableDataManager.getInstance();
                        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        try {
                            if (dm != null) {
                                dm.remove(downloadedSong.getDownloadingId());
                                downloadSongsManager.deleteSongDetails(downloadedSong.getSongID());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        customDialog.dismiss();
                    }
                });

                no.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
