package org.hobart.facetrans;

import android.app.Application;
import android.content.Context;

/**
 * Created by huzeyin on 2017/10/30.
 */

public class FaceTransApplication extends Application {

    private static FaceTransApplication instance = null;


    public static Context getFaceTransApplicationContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
