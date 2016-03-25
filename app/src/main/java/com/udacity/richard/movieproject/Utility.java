package com.udacity.richard.movieproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by richard on 3/23/16.
 */
public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();

    public static final int IMAGE_SIZE_LARGE = 0;
    public static final int IMAGE_SIZE_MEDIUM = 1;
    public static final int IMAGE_SIZE_SMALL = 2;

    public static void putConfigInSharedPreferences(Context context, String baseUrl, String[] imageSizes){
        Set<String> imagesSizeSet = new HashSet<String>(Arrays.asList(imageSizes));
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_config_key)
                , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(context.getString(R.string.config_base_url), baseUrl);
        editor.putStringSet(context.getString(R.string.config_imagessize_set), imagesSizeSet);
        editor.commit();
    }

    public static String getBaseUrlInSharedPreferences(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_config_key)
        , Context.MODE_PRIVATE);
        return sharedPref.getString(context.getString(R.string.config_base_url), null);
    }

    public static Set<String> getImagesSizeSetInSharedPreferences(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_config_key)
        , Context.MODE_PRIVATE);
        return sharedPref.getStringSet(context.getString(R.string.config_imagessize_set), null);
    }

    public static String buildImageUri(Context context, int imageSize, String imagePath){
        String baseUrl = getBaseUrlInSharedPreferences(context);
        Set<String> imagesSizesSet = getImagesSizeSetInSharedPreferences(context);
        String[] imagesSizes = imagesSizesSet.toArray(new String[imagesSizesSet.size()]);
        String imageSizePath;
        Uri.Builder builder = new Uri.Builder();

        switch(imageSize){
            case IMAGE_SIZE_LARGE:
                imageSizePath = imagesSizes[0];
                break;
            case IMAGE_SIZE_MEDIUM:
                imageSizePath = imagesSizes[1];
                break;
            case IMAGE_SIZE_SMALL:
                imageSizePath = imagesSizes[2];
                break;
            default:
                throw new UnsupportedOperationException("Unknown imageSize selection: " + imageSize);
        }

        String url = Uri.parse(baseUrl)
                .buildUpon()
                .appendPath(imageSizePath)
                .appendPath(imagePath)
                .build().toString();

        return url;
    }
}
