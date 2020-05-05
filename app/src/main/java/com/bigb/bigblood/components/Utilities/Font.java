package com.bigb.bigblood.components.Utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by yadhukrishnan.e@oneteam.us
 */

public class Font {
    public final static int AVENIR_BOOK = 0;
    public final static int AVENIR_HEAVY = 1;
    public final static int AVENIR_LIGHT = 2;
    public final static int AVENIR_MEDIUM = 3;
    public final static int AVENIR_ROMAN = 4;

    public static Typeface getFontType(Context context, int fontSetInXML) {
        String fontName = null;
        switch (fontSetInXML) {
            case AVENIR_BOOK :
                fontName = "Avenir-Book.ttf";
                break;
            case AVENIR_HEAVY :
                fontName = "Avenir-Heavy.ttf";
                break;
            case AVENIR_LIGHT :
                fontName = "Avenir-Light.ttf";
                break;
            case AVENIR_MEDIUM :
                fontName = "Avenir-Medium.ttf";
                break;
            case AVENIR_ROMAN :
                fontName = "Avenir-Roman.ttf";
                break;
        }
        if (fontName == null) {
            return null;
        } else
            return Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);

    }
}
