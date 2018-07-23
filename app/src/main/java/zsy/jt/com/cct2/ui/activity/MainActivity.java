package zsy.jt.com.cct2.ui.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.content.res.AssetManager;

import android.graphics.Typeface;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.squareup.picasso.Picasso;
import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.RollingTextView;
import com.yy.mobile.rollingtextview.strategy.Strategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import zsy.jt.com.cct2.Config;
import zsy.jt.com.cct2.R;
import zsy.jt.com.cct2.adapter.RightDialogAdapter;
import zsy.jt.com.cct2.bean.MqBean;
import zsy.jt.com.cct2.bean.RightDialog;
import zsy.jt.com.cct2.service.MyService;
import zsy.jt.com.cct2.ui.fragment.ShowDialogFragment;
import zsy.jt.com.cct2.ui.fragment.ShowDialogFragmentB;
import zsy.jt.com.cct2.ui.fragment.ShowRecyclerFragment;
import zsy.jt.com.cct2.utils.AnimUtil;
import zsy.jt.com.cct2.utils.HouseLoadingView;
import zsy.jt.com.cct2.utils.LogThread;
import zsy.jt.com.cct2.utils.PxUtils;
import zsy.jt.com.cct2.utils.SharedPreferencesUtils;
import zsy.jt.com.cct2.utils.StateBarTranslucentUtils;
import zsy.jt.com.cct2.utils.ThreadPoolManager;
import zsy.jt.com.cct2.utils.TimeThread;
import zsy.jt.com.cct2.utils.TrafficInfo;
import zsy.jt.com.cct2.utils.VideoPlayerIJK;
import zsy.jt.com.cct2.utils.VideoPlayerListener;

/**
 * 日期时间星期几✔️
 * 设置ip✔️ 视频地址✔
 * 多人展示✔ 设置横屏竖屏✔ 重连mq✔ 背景图片✔️ 设置声音X
 */
@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
    public static final String INTENT_ALARM_LOG = "intent_alarm_log";
    private static final int CHANGE_SETTING_CODE=100;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.right_parent)
    RelativeLayout rightParent;
    @BindView(R.id.ijk_player)
    VideoPlayerIJK ijkPlayer;
    @BindView(R.id.view)
    TextView view;
    @BindView(R.id.fragment)
    FrameLayout frameLayout;
    @BindView(R.id.bg)
    ImageView bg;
    @BindView(R.id.houser)
    HouseLoadingView house;
    @BindView(R.id.root)
    View rootView;
    @BindView(R.id.hsv)
    HorizontalScrollView hsv;
    @BindView(R.id.title_project)
    TextView titleProj;
    @BindView(R.id.number_msg)
    RollingTextView numberMsg;
    @BindView(R.id.msg_rl)
    RelativeLayout msgRl;
    //  @BindView(R.id.log)
//    TextView log;
    private RightDialogAdapter adapter;
    private List<RightDialog> list;
    private SoundPool mSoundPool = null;
    private HashMap<Integer, Integer> soundID;
    private Unbinder bind;
    private boolean isInitPlayer = false;
    private boolean isLand=true;
    private MyUdpClient client;

    private AlertDialog dialog;
    private boolean isMultiMode=true;
    private boolean isVideoMode=false;
    private String playPath;
    private String imagePath;
    private Handler handler;
    private int progress;
    private int width,heigth;
    private long exitTime;
    private float density;
    private AlarmManager am;
    LogThread logThread;
    TrafficInfo trafficInfo;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏透明.
        StateBarTranslucentUtils.setStateBarTranslucent(this);
        mContext = this;
        //noinspection ConstantConditions

        setContentView(R.layout.activity_main);
        bind = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        TimeThread timeThread = new TimeThread(view);//显示时间的控件TextView
        timeThread.start();//启动线程

/*        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try{

                    while(true) {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                numberMsg.setText(String.valueOf(Integer.valueOf(numberMsg.getText().toString()) + 1));
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });*/


        initAlertDialog();
        initSettings();
        initOrientation();
        initRecycler();
        initSp();
        initLoading();
        //initMQ();
        initBackGround(playPath,imagePath);
        initDialogRecycler();
        initUdpClient();
        setPlayerRestart();
        test();
        initView();
        //网络状态监听
        MainActivityPermissionsDispatcher.initTrafficInfoWithCheck(this);
     //   initLog();
    }

    @OnShowRationale({
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.READ_LOGS
    })
    void show(final PermissionRequest request){

        new AlertDialog.Builder(this)
                .setMessage("权限请求")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .show();

    }

    @OnPermissionDenied({
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.READ_LOGS
    })
    void denied(){}

    @OnNeverAskAgain({
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.READ_LOGS
    })
    void nerverAsk(){
        Toast.makeText(this, "权限未打开！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(MainActivity.this,requestCode,grantResults);
    }



    /**
     * 用于帮助 解决播放器在短暂断网后无法重连的问题
     *
     * 1秒获取1次流量增量，连续5次小于50k则发出重连提示
     *
     * **/
    @NeedsPermission({  Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,})
    void initTrafficInfo(){
        trafficInfo = new TrafficInfo(this);
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{

                    long last = 0;
                    long increment = 0;
                    while(true) {
                        if(isVideoMode){
                            sleep(1000);
                            increment = trafficInfo.getRcvTraffic()-last;
                            Log.d(TAG, "下载流量增加了:" + increment);
                            last = trafficInfo.getRcvTraffic();
                            if(increment < 50*1024){
                                count++;
                            }else{
                                count = 0;
                            }

                            if(count >4){
                                //发出重连提示
                                EventBus.getDefault().post("restart");
                                count = 0;
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /***
     * 将log信息记录到本地
     * /sdcard/cct_ylpd_log.txt
     * */
    @NeedsPermission({  Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,})
    void initLog(){
        logThread = new LogThread();
        logThread.start();
    }

    private void initView() {
        //获取设备屏幕信息
        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        heigth = metrics.heightPixels;
        density=metrics.densityDpi;
        Log.d(TAG,"--> screen width ="+width+"px ; heigth ="+heigth+"px ;density="+density);
        Log.d(TAG,"--> screen width ="+ PxUtils.px2dip(MainActivity.this,width)+"dp ; heigth="+PxUtils.px2dip(MainActivity.this,heigth)+"dp");
        //背景禁止拖动
        hsv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //设置第三方字体
        AssetManager mgr= getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/hignblack.ttf");
        titleProj.setTypeface(tf);

        numberMsg.setAnimationDuration(1200l);
        numberMsg.setCharStrategy(Strategy.NormalAnimation());
        numberMsg.addCharOrder(CharOrder.Number);
        numberMsg.setAnimationInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator obj = AnimUtil.nope(bg,(int)(1.78*heigth-width),30000);
        obj.setRepeatMode(ValueAnimator.RESTART);
        obj.setRepeatCount(ValueAnimator.INFINITE);
        obj.start();
        // initReStartTimeThread();

    }

    private void test() {
        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MqBean bean = new MqBean();
                bean.setMq_name("周杰伦JAy");
                bean.setMq_message("项目开始时间为：\n10:22:22");
                bean.setMq_section("激流勇进");
                bean.setMq_number("排队号:9");
                bean.setMq_type("02");
                initDialog(bean);

            }
        });

        findViewById(R.id.test1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MqBean bean = new MqBean();
                bean.setMq_name("周杰伦Jay");
                bean.setMq_type("0");
                bean.setMq_number("2131311");
                bean.setMq_section("云霄飞车");
                initDialog(bean);
            }
        });
    }

    private void initAlertDialog(){

        dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("播放出现错误!")
                .setMessage("请重新设置！")
                .setCancelable(false)
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(MainActivity.this, ConfigurationActivity.class),CHANGE_SETTING_CODE);
                        // finish();
                    }
                })
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
    }

    /**
     * 重新启动时调用
     * **/
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * 播放器定时重启
     * **/
    private void setPlayerRestart(){

        //初始化定时器
        am = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,1);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Intent intent = new Intent(this,AlarmReceiver.class).setAction(INTENT_ALARM_LOG);
        PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this,0,intent,0);
        am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),INTERVAL,pi);
    }

    public synchronized void replay(){
        ijkPlayer.setVideoPath(playPath);
    }

    private void initSettings(){
        Log.d(TAG,"设置更新！");
        if (SharedPreferencesUtils.get(this, "state3", false) != null) {
            isVideoMode=(Boolean) SharedPreferencesUtils.get(this, "state3", true);
        }
        if (SharedPreferencesUtils.get(this, "state2", false) != null) {
            isMultiMode=(Boolean) SharedPreferencesUtils.get(this, "state2", false);
        }
        if (SharedPreferencesUtils.get(this, "state", false) != null) {
            isLand=(Boolean) SharedPreferencesUtils.get(this, "state", false);
        }

        if (SharedPreferencesUtils.get(this, "image_path", "") != null){
            imagePath=(String) SharedPreferencesUtils.get(this,"image_path","");
        }

        if (SharedPreferencesUtils.get(this, "PLAY_PATH", "") != null){
            playPath=(String) SharedPreferencesUtils.get(this,"PLAY_PATH","");
        }

    }

    private void initUdpClient(){
        client=new MyUdpClient();
        new Thread(client).start();
    }

    private void initOrientation(){
        if (isLand) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){

            if((System.currentTimeMillis() - exitTime) >2000){
                Toast.makeText(this,"在按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }else{
                ThreadPoolManager.getInstance().shutdownNow();
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    protected void  onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.d(TAG,"接收到设置界面的结果--> requestCode="+requestCode+"; resultCode="+resultCode);
        if(requestCode == CHANGE_SETTING_CODE && resultCode == RESULT_OK){

            //视频流地址
            String playPath1=intent.getStringExtra("play_path");
            //图片地址
            String imagePath1=intent.getStringExtra("image_path");
            //横竖屏切换
            isLand=intent.getBooleanExtra("state",true);
            //多人/单人切换
            isMultiMode=intent.getBooleanExtra("state2",true);
            //视频/图片模式
            boolean isVideoMode1=intent.getBooleanExtra("state3",true);

            Log.d(TAG,"设置界面返回 --> playPath="+playPath1+" ;isLand ="+isLand+" ; isMul="+isMultiMode+" ; isVidea ="+isVideoMode);
            initOrientation();

            //progress = 0;
            if(isVideoMode1){
                //视频模式
                if(isVideoMode){
                    //本来也是视频模式
                    if(playPath.equals(playPath1)){
                        //啥也没改...
                        Log.d(TAG,"模式和地址不变");
                        ijkPlayer.stop();
                        ijkPlayer.release();
                        initPlayer(playPath1);
                    }else{
                        //更改流地址
                        Log.d(TAG,"更改流地址：playPath1 =" +playPath1);
                        ijkPlayer.stop();
                        ijkPlayer.release();
                        initPlayer(playPath1);
                    }
                }else{
                    //本来是图片模式
                    Log.d(TAG,"图片变视频模式");
                    //bg.setVisibility(View.INVISIBLE);
                    initPlayer(playPath1);
                    ijkPlayer.setVisibility(View.VISIBLE);
                }
            }else{
                //图片模式
                if(!isVideoMode){
                    //本来也是图片模式
                    if(imagePath.equals(imagePath1)){
                        //啥也没改...
                        Log.d(TAG,"模式和图片不变！");
                    }else{
                        Log.d(TAG,"更改图片地址！");
                        //更改图片地址
                        //ijkPlayer.release();
                        ijkPlayer.setVisibility(View.INVISIBLE);
                        bg.setVisibility(View.VISIBLE);
                        if (!"".equals(imagePath1)) {
                            Picasso.with(mContext).load(imagePath1).placeholder(R.mipmap.timg2).into(bg);
                        } else {
                            if(isLand){
                                bg.setImageResource(R.mipmap.timg22);
                            }else{
                                // bg.setImageResource(R.mipmap.timg2);
                                // Picasso.with(mContext).load(R.mipmap.img).placeholder(R.mipmap.timg2).into(bg);
                                bg.setImageResource(R.mipmap.img);
                            }
                        }
                    }
                }else{
                    //本来是视频模式
                    Log.d(TAG,"视频变图片！");
                    ijkPlayer.setVisibility(View.INVISIBLE);
                    ijkPlayer.stop();
                    ijkPlayer.setListener(null);
                    bg.setVisibility(View.VISIBLE);
                    if (!"".equals(imagePath1)) {
                        Picasso.with(mContext).load(imagePath1).placeholder(R.mipmap.img).into(bg);
                    } else {
                        if(isLand){
                            bg.setImageResource(R.mipmap.timg22);
                        }else{
                            // bg.setImageResource(R.mipmap.timg4);
                            bg.setImageResource(R.mipmap.img);
                        }
                    }
                }
            }

            playPath=playPath1;
            imagePath=imagePath1;
            isVideoMode=isVideoMode1;
        }
    }


    private void initBackGround(String videaPath,String imagePath){

        if (isVideoMode) {
            ijkPlayer.setVisibility(View.VISIBLE);
            //bg.setVisibility(View.INVISIBLE);
            initPlayer(videaPath);
        } else {

            ijkPlayer.setVisibility(View.INVISIBLE);
            bg.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(4);
            if (!"".equals(imagePath)) {
                Picasso.with(mContext).load(imagePath).placeholder(R.mipmap.timg2).into(bg);
            } else {
                if(isLand){
                    bg.setImageResource(R.mipmap.timg22);
                }else{

                    //华侨城 bg
                    //bg.setImageResource(R.mipmap.timg4);

                    //学院路 bg
                    //bg.setImageResource(R.mipmap.timg2);

                    //预约排队
                    bg.setImageResource(R.mipmap.img);
                    //bg.setBackgroundResource(R.mipmap.img);
                    //Picasso.with(mContext).load(R.mipmap.img).fit().into(bg);
                }
            }
        }
    }


    private void initPlayer(String path) {

        isInitPlayer = true;
        //加载so文件
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        ijkPlayer.setListener(new VideoPlayerListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                Log.d(TAG,"mediaplaer --> 缓冲更新完成！ percent="+percent);
            }

            @Override
            public void onCompletion(IMediaPlayer mp) {

                Log.d(TAG,"mediaplayer --> 播放完成！");
                //stopLoading();
                mp.seekTo(0);
                mp.start();
            }

            @Override
            public boolean onError(IMediaPlayer mp, int what, int extra) {

                Log.d(TAG,"mediaplayer --> 出现错误！ what="+what+"; extra="+extra);
                Snackbar.make(rootView,"视频流地址错误,自动重连中...",Snackbar.LENGTH_SHORT).setAction("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(MainActivity.this,ConfigurationActivity.class),CHANGE_SETTING_CODE);
                    }
                }).show();
                //Toast.makeText(MainActivity.this, "播放失败，正在重连中...", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(3,5000);
                return true;
            }

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

                Log.d(TAG,"mediaplayer --> 播放信息: what="+what+"; extra="+extra);
                if(what == 3){
                    Snackbar.make(rootView,"马上开始播放",Snackbar.LENGTH_SHORT).show();
                    //  Toast.makeText(MainActivity.this, "马上开始播放", Toast.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(2);
                }
                // startLoading();
                return true;
            }

            @Override
            public void onPrepared(IMediaPlayer mp) {

                Log.d(TAG,"mediaplayer --> 准备完毕！");
                //stopLoading();
                mp.start();

            }

            @Override
            public void onSeekComplete(IMediaPlayer mp) {

                Log.d(TAG,"mediaplayer --> 拖动完毕！");
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
                //获取到视频的宽和高
                Log.d(TAG,"mediaplayer --> 获取到 width="+width+";height="+height);
                //ijkPlayer.setRatio((width+0f)/height);
            }


        });

        Log.d(TAG,"播放地址为 --> "+path);
        ijkPlayer.setVideoPath(path);
    }

    /**
     * 固定25秒准备动画
     *
     * **/
    public void initLoading(){

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1){
                    if(progress<100){
                        house.setVisibility(View.VISIBLE);
                        progress+=1;
                        handler.sendEmptyMessageDelayed(1,250);
                        house.setProgress(progress);
                    }else{
                        house.setVisibility(View.GONE);
                        sendEmptyMessage(3);
                    }
                }
                if(msg.what == 2){
                    //加载成功，开始播放
                    handler.removeMessages(1);
                    handler.removeMessages(3);
                    dialog.dismiss();
                    progress = 100;
                    house.setProgress(progress);
                    house.setVisibility(View.GONE);

                }
                if(msg.what == 3){
                    /*if(progress<100) {
                        //动画中自动重连
                        initBackGround(playPath, imagePath);
                    }
                    else{
                        //动画结束 提示报错
                       if(dialog == null){
                           initAlertDialog();
                       }
                       dialog.show();
                    }*/

                   /* if(dialog == null){
                        initAlertDialog();
                    }
                    dialog.show();*/
                    //initBackGround(playPath,imagePath);
                    replay();
                }
                else if(msg.what == 4){
                    handler.removeMessages(1);
                    handler.removeMessages(2);
                    handler.removeMessages(3);
                    if(progress<100){
                        house.setVisibility(View.VISIBLE);
                        progress+=1;
                        handler.sendEmptyMessageDelayed(4,100);
                        house.setProgress(progress);
                    }else{
                        house.setVisibility(View.GONE);
                    }
                }
            }
        };

    }


    private void initDialogRecycler() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, new ShowRecyclerFragment());
        transaction.commit();
    }

    /**
     * recycler配置
     */
    private void initRecycler() {
        list = new ArrayList<>();
        adapter = new RightDialogAdapter(list);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    /**
     * 中间弹框
     */
    private void initDialog(final MqBean mqBean) {

        if(mqBean == null)return;
        if("00".equals(mqBean.getMq_type()) || "01".equals(mqBean.getMq_type()) || "02".equals(mqBean.getMq_type())){

            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ShowDialogFragment fragment = ShowDialogFragment.initFragment(mqBean);
                fragment.show(fragmentManager, "ShowDialogFragment");
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
        else{
            try {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ShowDialogFragmentB fragment = ShowDialogFragmentB.initFragment(mqBean);
                fragment.show(fragmentManager, "ShowDialogFragmentB");
            }catch (IllegalStateException e){
                e.printStackTrace();
            } }

    }

    /**
     * 服务配置
     */
    private void initService() {
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
    }

    /**
     * MQ配置
     */
    private void initMQ() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "MQ初始化");
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setHost(Config.getValue(mContext, "MQ_HOST"));
                    factory.setPort(Config.getInt(mContext));
                    factory.setUsername(Config.getValue(mContext, "MQ_USER"));
                    factory.setPassword(Config.getValue(mContext, "MQ_PASSWORD"));

                    Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel();
                    channel.queueDeclare(Config.getValue(mContext, "MQ_QUEUE"), false, false, false, null);
                    channel.exchangeDeclare(Config.getValue(mContext, "MQ_EXCHANGE"), "direct");
                    channel.queueBind(Config.getValue(mContext, "MQ_QUEUE"), Config.getValue(mContext, "MQ_EXCHANGE"), Config.getValue(mContext, "MQ_ROUTING_KEY_Screen"));
                    Consumer consumer = new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            final String message = new String(body, "UTF-8");
                            Log.e("receive message", message);
                            final MqBean mqBean = new Gson().fromJson(message, MqBean.class);
                            if (mqBean != null && mqBean.getMq_type() != null) {
                                ThreadPoolManager.getInstance().addTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(200);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if ("1".equals(mqBean.getMq_type())) {
                                                        mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
                                                    } else {
                                                        mSoundPool.play(soundID.get(2), 1, 1, 0, 0, 1);
                                                    }

                                                }
                                            });
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                            if ((Boolean) SharedPreferencesUtils.get(mContext, "state2", false) != null) {
                                if ((Boolean) SharedPreferencesUtils.get(mContext, "state2", false)) {
                                    EventBus.getDefault().post(mqBean);
                                } else {
                                    initDialog(mqBean);
                                    initRightDialog(mqBean);
                                }
                            } else {
                                initDialog(mqBean);
                                initRightDialog(mqBean);
                            }
                        }
                    };
                    channel.basicConsume(Config.getValue(mContext, "MQ_QUEUE"), true, consumer);
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 右部信息提醒弹窗
     */
    private void initRightDialog(MqBean mqBean) {
        if (mqBean.getMq_message() != null && mqBean.getMq_message().length() > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    rightParent.setVisibility(View.VISIBLE);
                    for (int i = 0; i < 2; i++) {
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(500);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            list.add(new RightDialog());
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                }
            });
        }
    }

    /**
     * 初始化SoundPool
     */
    @SuppressLint("UseSparseArrays")
    private void initSp() {
        soundID = new HashMap<>();
        //当前系统的SDK版本大于等于21(Android 5.0)时
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频数量
            builder.setMaxStreams(3);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
        }
        //当系统的SDK版本小于21时
        else {//设置最多可容纳3个音频流，音频的品质为5
            mSoundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        }

        soundID.put(1, mSoundPool.load(this, R.raw.finishfile, 1));
        soundID.put(2, mSoundPool.load(this, R.raw.windows, 1));
        soundID.put(3,mSoundPool.load(this,R.raw.overtime,1));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThread(String content) {
        if (content.equals("dismiss")) {
            rightParent.setVisibility(View.INVISIBLE);
            list.clear();
            adapter.notifyDataSetChanged();
        }else if(content.equals("restart")){
            Log.d(TAG,"重新连接replay");
            if(isVideoMode){
                //resetPlayer();
                Toast.makeText(mContext, "正在重新连接相机...", Toast.LENGTH_SHORT).show();
                replay();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isVideoMode){
            if (isInitPlayer) {
                ijkPlayer.start();
                progress = 0;
                handler.sendEmptyMessage(1);
            }
        }

 /*       receiver=new AlarmReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(INTENT_ALARM_LOG);
        registerReceiver(receiver,filter);*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isInitPlayer) {
            ijkPlayer.pause();
            IjkMediaPlayer.native_profileEnd();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPool.release();   //回收SoundPool资源
        ijkPlayer.release();
        EventBus.getDefault().unregister(this);
        bind.unbind();
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.view:
                startActivityForResult(new Intent(this, ConfigurationActivity.class),CHANGE_SETTING_CODE);
                //finish();
                break;
        }
    }

    public class MyUdpClient implements Runnable{

        private boolean isInterrupt=false;
        private DatagramSocket socket1;
        private DatagramPacket packetRcv;
        private byte[] message=new byte[1024];
        private final Gson gson = new Gson();
        public MyUdpClient(){
            super();
        }

        @Override
        public void run() {

            socket1=null;
            try{
                socket1=new DatagramSocket(Config.UDP_PORT);
                packetRcv=new DatagramPacket(message,message.length);
                Log.d(TAG,"--> udp 连接成功");
                Snackbar.make(rootView,"Socket连接成功",Snackbar.LENGTH_LONG).show();
                while(!isInterrupt){
                    socket1.receive(packetRcv);
                    String rcvMsg=new String(packetRcv.getData(),packetRcv.getOffset(),packetRcv.getLength());
                    Log.d( TAG,"client --> msg:"+rcvMsg);
                    final MqBean mqBean = gson.fromJson(rcvMsg, MqBean.class);
                    if (mqBean != null && mqBean.getMq_type() != null) {
                        ThreadPoolManager.getInstance().addTask(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(100);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if ("1".equals(mqBean.getMq_type()) || "01".equals(mqBean.getMq_type())) {
                                                mSoundPool.play(soundID.get(1), 1, 1, 0, 0, 1);
                                            } else if("0".equals(mqBean.getMq_type()) || "00".equals(mqBean.getMq_type())){
                                                mSoundPool.play(soundID.get(2), 1, 1, 0, 0, 1);
                                            }else if("2".equals(mqBean.getMq_type()) || "02".equals(mqBean.getMq_type())){
                                                mSoundPool.play(soundID.get(3), 1, 1, 0, 0, 1);
                                            }
                                            else {
                                                mSoundPool.play(soundID.get(3),1,1,0,0,1);
                                            }

                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    if (isMultiMode) {
                        EventBus.getDefault().post(mqBean);
                    } else {
                        initDialog(mqBean);
                        //  initRightDialog(mqBean);
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
                Log.d(TAG,"抓获异常 -->"+ e.toString());
            }finally {
                if(socket1 != null){
                    socket1.close();
                    socket1 = null;
                }
            }
        }


        //暂时不加发送
        public void sendMessage(String msg){}

    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("ALARM REVEIVER","--> alarm receive!");
            if (!TextUtils.isEmpty(action) && action .equals(MainActivity.INTENT_ALARM_LOG)) {
                EventBus.getDefault().post("restart");
            }

        }
    }

}
