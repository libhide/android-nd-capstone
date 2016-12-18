package com.ratik.todone;

import android.app.Application;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by Ratik on 18/12/16.
 */

public class TodoneApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}
