package zsy.jt.com.demo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import zsy.jt.com.demo.ui.activity.MainActivity;

/**
 * Created by ZhaoShengYang
 * email 99720140@qq.com
 */

public class BootReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                        /* 应用开机自启动 */
            Intent intent_n = new Intent(context,
                    MainActivity.class);

            intent_n.setAction("android.intent.action.MAIN");
            intent_n.addCategory("android.intent.category.LAUNCHER");
            intent_n.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent_n);
        }
    }
}