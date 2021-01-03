package com.phpexperts.kioskapp.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

/**
 * Created by mohit on 30/11/15.
 */

public class DroidPrefs {

    public static boolean contains(Context context, String key) {
        return getDefaultInstance(context).contains(key);
    }

    public static <T> T get(Context context, String key, Class<T> cls) {
        return getDefaultInstance(context).get(key, cls);
    }

    public static void apply(Context context, String key, Object value) {
        getDefaultInstance(context).apply(key, value);
    }

    public static void commit(Context context, String key, Object value) {
        getDefaultInstance(context).commit(key, value);
    }

    public static DroidPrefs getDefaultInstance(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return new DroidPrefs(sp);
    }

    private Gson mGson;

    private SharedPreferences mSharedPrefs;

    public DroidPrefs(SharedPreferences sharedPrefs) {
        mGson = new Gson();
        mSharedPrefs = sharedPrefs;
    }

    public boolean contains(String key) {
        return mSharedPrefs.contains(key);
    }

    public <T> T get(String key, Class<T> cls) {
        if (contains(key)) {
            return mGson.fromJson(mSharedPrefs.getString(key, null), cls);
        }

        try {
            return cls.newInstance();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("class must have an empty constructor");
        }
    }

    public void apply(String key, Object value) {
        put(key, value).apply();
    }

    public void commit(String key, Object value) {
        put(key, value).commit();
    }

    @SuppressLint("CommitPrefEdits")
    private SharedPreferences.Editor put(String key, Object value) {
        SharedPreferences.Editor e = mSharedPrefs.edit();
        e.putString(key, mGson.toJson(value));
        return e;
    }
   public void clear(){
       SharedPreferences.Editor e = mSharedPrefs.edit();
       e.clear().apply();
   }
   public void clearkey(String key){
       SharedPreferences.Editor e = mSharedPrefs.edit();
       e.remove(key).apply();
   }
}
