package com.wow.carlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wow.carlauncher.service.MainService;
import com.wow.carlauncher.view.activity.launcher.LauncherActivity;

import org.xutils.x;

/**
 * Created by 10124 on 2017/10/31.
 */

public class BootReceiver extends BroadcastReceiver {
    private static boolean bootSuccess = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (!bootSuccess) {
            bootSuccess = true;
            Intent startIntent = new Intent(context, MainService.class);
            context.startService(startIntent);

            x.task().postDelayed(() -> {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(home);
            }, 2000);
        }
    }
}
