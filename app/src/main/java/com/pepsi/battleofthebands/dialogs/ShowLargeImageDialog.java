package com.pepsi.battleofthebands.dialogs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pepsi.battleofthebands.R;
import com.pepsi.battleofthebands.utils.RoundCornerImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ShowLargeImageDialog extends DialogFragment {
    public static final String KEY_IMAGELINK = "imageLink";
    private String imageLink;
    Bitmap currentBitmap = null;
    ImageView imageViewDownload;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
        if (getArguments() != null && getArguments().getString(KEY_IMAGELINK) != null)
            imageLink = getArguments().getString(KEY_IMAGELINK, "");
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_show_large_image, container, false);
        currentBitmap = null;
        final RoundCornerImageView desiredImageView = v.findViewById(R.id.desiredImageView);
        ImageView crossImageView = v.findViewById(R.id.crossImageView);
        LinearLayout dialogMainContent = v.findViewById(R.id.dialogMainContent);
        RelativeLayout mainDialogContentLinearLayout = v.findViewById(R.id.mainDialogContentRelativeLayout);

        dialogMainContent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        crossImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mainDialogContentLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });
        imageViewDownload = v.findViewById(R.id.imageViewDownload);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                imageViewDownload.setVisibility(View.VISIBLE);
                desiredImageView.setImageBitmap(bitmap);
                currentBitmap = bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.with(getActivity()).load(imageLink).into(target);
        desiredImageView.setTag(target);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Please wait, we are downloading your image file...");

        // Initialize a new click listener for positive button widget
        imageViewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressDialog != null) {
                    progressDialog.show();
                }
                final File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "Pepsi" + File.separator + System.currentTimeMillis() + ".jpg");
//                if (file.exists()) {
//                    Toast.makeText(getApplicationContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
//                } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            file.getParentFile().mkdirs();
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.close();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    Toast.makeText(getApplicationContext(), "Image saved successfully", Toast.LENGTH_SHORT).show();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        final Uri contentUri = Uri.fromFile(file);
                                        scanIntent.setData(contentUri);
                                        getActivity().sendBroadcast(scanIntent);
                                    } else {
                                        final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                                        getActivity().sendBroadcast(intent);
                                    }
                                }
                            });
                        } catch (Exception e) {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                            e.printStackTrace();
                        }
                    }
                }).start();
//                }
            }
        });
        return v;
    }
}
