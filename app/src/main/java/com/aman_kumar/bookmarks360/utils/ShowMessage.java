package com.aman_kumar.bookmarks360.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.aman_kumar.bookmarks360.App;

/**
 * Created by aman on 2/10/15.
 */
public class ShowMessage {
    static Context context = App.getAppContext();

    public static void toast(String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        Log.d("ShowMessage",string);
    }

    public static void log(String string) {

        Log.d("ShowMessage",string);
    }
}
