package com.example.avkor.test;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by avkor on 02.12.2017.
 */

public class Permission {
    public static final int REQUEST_PERMISSION = 100;
    public static void checkPermission(Activity act, String[] perm) {
        PackageManager pm = act.getPackageManager();
        for (String p : perm) {
            int hasPerm = pm.checkPermission(p, act.getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions( act, perm , REQUEST_PERMISSION);
                break;
            }
        }

    }
}
