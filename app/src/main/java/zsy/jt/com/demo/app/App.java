package zsy.jt.com.demo.app;

import android.app.Application;
import android.content.Context;

import zsy.jt.com.demo.utils.SharedPreferencesUtils;

/**
 * Created by ZhaoShengYang
 * email 99720140@qq.com
 */

public class App extends Application {

    public static Context context;
    //大雄兔
    public static final String rtspUrl="rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov";
    //客户测试rtsp://admin:JT123456@192.168.0.64/h264/ch1/sub/av_stream
    public static final String RTSP_URL="rtsp://admin:admin@192.168.181.157:554/h264/ch1/sub/av_stream";
    //三脚架摄像头
    public static final String RTSP_SANJIAOJIA_URL="rtsp://admin:@192.168.0.10:554/h264/ch1/sub/av_stream";
    //客户测试2
    public static final String RTSP_02_URL="rtsp://admin:admin@192.168.181.157/h264/ch2/sub/av_stream";
    //海康威视 测试
    public static final String RTSP_HKV_URL="rtsp://admin:JT123456@192.168.181.157:554/h264/ch1/main/av_stream";
    //客户测试 3
    public static final String RTSP_03_URL="rtsp://admin:admin@192.168.181.157:554/cam/realmonitor?channel=1&subtype=0";
    //小摄像头
    public static final String RTSP_LITTLE_URL="rtsp://192.168.0.11:554/user=admin&password=&channel=1&stream=0.sdp?real_stream";


    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    /*    SharedPreferencesUtils.put(this, "MQ_QUEUE", "faceScreenQueue");
        SharedPreferencesUtils.put(this, "MQ_EXCHANGE", "faceScreen");
        SharedPreferencesUtils.put(this, "MQ_ROUTING_KEY_Screen", "faceRecogSystem.faceRecog.003");
        SharedPreferencesUtils.put(this, "MQ_PORT", 5672);
        SharedPreferencesUtils.put(this, "MQ_USER", "admin");
        SharedPreferencesUtils.put(this, "MQ_PASSWORD", "admin");*/
       if ("".equals(SharedPreferencesUtils.get(this, "MQ_HOST", ""))) {
            SharedPreferencesUtils.put(this, "MQ_HOST", "192.168.0.129");
        }//192.168.181.156 139.199.94.154
        if ("".equals(SharedPreferencesUtils.get(this, "PLAY_PATH", ""))) {
            SharedPreferencesUtils.put(this, "PLAY_PATH", RTSP_HKV_URL);
        }//海康rtsp://admin:jt123456@192.168.0.64:554/h264/ch1/main/av_stream
        //大华 rtsp://admin:admin@192.168.181.157:554/cam/realmonitor?channel=1&subtype=0
        //http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4
        if ("".equals(SharedPreferencesUtils.get(this, "image_path", ""))) {
            SharedPreferencesUtils.put(this, "image_path", "");
        }
        if (!(Boolean) SharedPreferencesUtils.get(this, "state", false)) {
            SharedPreferencesUtils.put(this, "state", true);
        }
        if (!(Boolean) SharedPreferencesUtils.get(this, "state2", false)) {
            SharedPreferencesUtils.put(this, "state2", true);
        }
        if (!(Boolean) SharedPreferencesUtils.get(this, "state3", false)) {
            SharedPreferencesUtils.put(this, "state3", true);
        }

    }
}
