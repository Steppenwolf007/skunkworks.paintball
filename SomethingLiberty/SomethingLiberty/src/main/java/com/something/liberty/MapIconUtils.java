package com.something.liberty;

import java.util.HashMap;

public abstract class MapIconUtils
{
    private static final String OUTGUNNED_ICON_SRC = "outgunned.png";

    public static final String NEWS_TYPE_OUTGUNNED = "PLAYER_OUTGUNNED";
    public static final String NEWS_TYPE_MISS = "PLAYER_MISS";
    public static final String NEWS_TYPE_HIT = "PLAYER_HIT";

    private static HashMap<String,String> colourIconSrcMap = new HashMap<String,String>();
    static
    {
        colourIconSrcMap.put(NEWS_TYPE_HIT + "#FFFFFF","hit-white.png");
        colourIconSrcMap.put(NEWS_TYPE_HIT + "#000000","hit-black.png");
        colourIconSrcMap.put(NEWS_TYPE_HIT + "#FF0000","hit-red.png");
        colourIconSrcMap.put(NEWS_TYPE_HIT + "#FFA500","hit-orange.png");
        colourIconSrcMap.put(NEWS_TYPE_HIT + "#FFFF00","hit-yellow.png");
        colourIconSrcMap.put(NEWS_TYPE_HIT + "#008000","hit-green.png");
        colourIconSrcMap.put(NEWS_TYPE_HIT + "#0000FF","hit-blue.png");
        colourIconSrcMap.put(NEWS_TYPE_HIT + "#4B0082","hit-indigo.png");

        colourIconSrcMap.put(NEWS_TYPE_MISS + "#FFFFFF","miss-white.png");
        colourIconSrcMap.put(NEWS_TYPE_MISS + "#000000","miss-black.png");
        colourIconSrcMap.put(NEWS_TYPE_MISS + "#FF0000","miss-red.png");
        colourIconSrcMap.put(NEWS_TYPE_MISS + "#FFA500","miss-orange.png");
        colourIconSrcMap.put(NEWS_TYPE_MISS + "#FFFF00","miss-yellow.png");
        colourIconSrcMap.put(NEWS_TYPE_MISS + "#008000","miss-green.png");
        colourIconSrcMap.put(NEWS_TYPE_MISS + "#0000FF","miss-blue.png");
        colourIconSrcMap.put(NEWS_TYPE_MISS + "#4B0082","miss-indigo.png");
    }

    public static String getIconSrc(String newsType,String colorHex)
    {
        if(NEWS_TYPE_OUTGUNNED.equals(newsType) || colorHex == null)
        {
            return OUTGUNNED_ICON_SRC;
        }

        String output = colourIconSrcMap.get(newsType + colorHex);
        if(output == null)
        {
            return OUTGUNNED_ICON_SRC;
        }
        return output;
    }
}
