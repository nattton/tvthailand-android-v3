package com.codemobi.android.tvthailand.utils;

import android.content.Context;
import android.content.Intent;

import com.codemobi.android.tvthailand.MainActivity;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by nattapong on 10/31/14 AD.
 */
public class Receiver extends ParsePushBroadcastReceiver {
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
