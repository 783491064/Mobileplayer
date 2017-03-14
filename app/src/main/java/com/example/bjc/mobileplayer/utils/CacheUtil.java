package com.example.bjc.mobileplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 毕静存 on 2017/1/3.
 */

public class CacheUtil {
    public static  void putString(Context context,String key,String value){
        SharedPreferences sharedPreferences=context.getSharedPreferences(key,Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key,value).commit();

    }
    public static String getString(Context context,String key){
        SharedPreferences sharedPreferences=context.getSharedPreferences(key,Context.MODE_PRIVATE);
        String reuslt = sharedPreferences.getString(key, "");
        return reuslt;
    }

    public static void putPlayModule(Context context,String key,int value){
        SharedPreferences sharedPreferences=context.getSharedPreferences(key,Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key,value).commit();
    }

    public  static int getPlayModule(Context context,String key){
        SharedPreferences sharedPreferences =context.getSharedPreferences(key,Context.MODE_PRIVATE);
        int value = sharedPreferences.getInt(key, 0);
        return value;
    }
}
