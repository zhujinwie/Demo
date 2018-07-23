package zsy.jt.com.cct2;

import android.content.Context;

import zsy.jt.com.cct2.utils.SharedPreferencesUtils;

/**
 * Created by ZhaoShengYang
 * email 99720140@qq.com
 */

public class Config {
//    public static final String MQ_QUEUE = "faceScreenQueue";
//    public static final String MQ_EXCHANGE = "faceScreen";
//    public static final String MQ_ROUTING_KEY_Screen = "faceRecogSystem.faceRecog.003";
//    //    public static final String MQ_HOST = "192.168.0.100";
//    public static final String MQ_HOST = "139.199.94.154";
//
//    public static final Integer MQ_PORT = 5672;
//    public static final String MQ_USER = "admin";
//    public static final String MQ_PASSWORD = "admin";
//    public static final String PLAY_PATH = "rtsp://admin:admin@192.168.0.25:554/cam/realmonitor?channel=1&subtype=0";

    public static final int UDP_PORT=12345;

    public static String getValue(Context context, String value) {
        return (String) SharedPreferencesUtils.get(context, value, "");
    }

    public static Integer getInt(Context context) {
        return (Integer) SharedPreferencesUtils.get(context, "MQ_PORT", 5672);
    }

    public static Integer getUdpPort(Context context){
        return (Integer) SharedPreferencesUtils.get(context,"UDP_PORT",8848);
    }

    public static String getUdpHost(Context context){
        return (String) SharedPreferencesUtils.get(context,"UDP_HOST","192.168.0.11");
    }






}
