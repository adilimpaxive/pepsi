package com.pepsi.battleofthebands.utils;

import android.content.Context;
import android.graphics.Typeface;

public class PFonts {
    public static final int FONT_LIGHT = 1;
    public static final int FONT_REGULAR = 2;
    public static final int FONT_BOLD = 3;
    public static final int FONT_MEDIUM = 4;


    private Typeface typefaceLight = null;
    private Typeface typefaceRegular = null;
    private Typeface typefaceBold = null;
    private Typeface typefaceMedium = null;

    private static PFonts mSFont = null;

    public static PFonts getInstance(Context context) {
        if (mSFont == null) {
            mSFont = new PFonts();
            mSFont.init(context);
        }
        return mSFont;
    }

    private void init(Context context) {
        typefaceLight = Typeface.createFromAsset(context.getAssets(), "fonts/DUBAI-LIGHT.OTF");
        typefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/DUBAI-BOLD.OTF");
        typefaceRegular = Typeface.createFromAsset(context.getAssets(), "fonts/DUBAI-REGULAR.OTF");
        typefaceMedium = Typeface.createFromAsset(context.getAssets(), "fonts/DUBAI-MEDIUM.OTF");
    }

    public Typeface getFont(int fontID) {
        Typeface font = null;
        switch (fontID) {
            case FONT_LIGHT:
                font = typefaceLight;
                break;
            case FONT_REGULAR:
                font = typefaceRegular;
                break;
            case FONT_BOLD:
                font = typefaceBold;
                break;
            case FONT_MEDIUM:
                font = typefaceMedium;
                break;
        }
        return font;
    }
}
