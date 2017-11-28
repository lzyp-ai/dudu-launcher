package com.wow.carlauncher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.wow.carlauncher.activity.LauncherActivity;
import com.wow.carlauncher.service.MainService;

import org.xutils.x;

/**
 * Created by 10124 on 2017/10/31.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Intent startIntent = new Intent(context, MainService.class);
        context.startService(startIntent);

        Intent intent2 = new Intent(context, LauncherActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);

        if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
            Toast.makeText(context, "!!!!!!!!!!", Toast.LENGTH_SHORT).show();
        }
    }
}
