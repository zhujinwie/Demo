package zsy.jt.com.demo.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import zsy.jt.com.demo.Config;
import zsy.jt.com.demo.R;
import zsy.jt.com.demo.adapter.RightDialogAdapter;
import zsy.jt.com.demo.bean.MqBean;
import zsy.jt.com.demo.bean.RightDialog;
import zsy.jt.com.demo.service.MyService;
import zsy.jt.com.demo.ui.fragment.ShowDialogFragment;
import zsy.jt.com.demo.ui.fragment.ShowRecyclerFragment;
import zsy.jt.com.demo.utils.HouseLoadingView;
import zsy.jt.com.demo.utils.SharedPreferencesUtils;
import zsy.jt.com.demo.utils.StateBarTranslucentUtils;
import zsy.jt.com.demo.utils.ThreadPoolManager;
import zsy.jt.com.demo.utils.TimeThread;
import zsy.jt.com.demo.utils.VideoPlayerIJK;
import zsy.jt.com.demo.utils.VideoPlayerListener;

/**
 * 日期时间星期几✔️
 * 设置ip✔️ 视频地址✔
 * 多人展示✔ 设置横屏竖屏✔ 重连mq✔ 背景图片✔️ 设置声音X
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private Context mContext;

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

    private boolean isMultiMode=true;
    private boolean isVideoMode=true;
    private String playPath;
    private String imagePath;
    private Handler handler;
    private int progress;

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

        //网络状态监听
       // initNetWorkListener();
        initSettings();
        initOrientation();
        initRecycler();
        initSp();
        //initMQ();
        initBackGround(playPath,imagePath);
        initDialogRecycler();
        initUdpClient();
        initLoading();
    }




    private void initSettings(){


        Log.d(TAG,"设置更新！");
        if (SharedPreferencesUtils.get(this, "state3", false) != null) {
            isVideoMode=(Boolean) SharedPreferencesUtils.get(this, "state3", false);
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
        //ThreadPoolManager.getInstance().addTask(client);

    }

    private void initOrientation(){
        if (isLand) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
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

            if(isVideoMode1){
                //视频模式
                if(isVideoMode){
                    //本来也是视频模式
                    if(playPath.equals(playPath1)){
                        //啥也没改...
                        Log.d(TAG,"模式和地址不变");
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
                    bg.setVisibility(View.INVISIBLE);
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
                                bg.setImageResource(R.mipmap.timg2);
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
                        Picasso.with(mContext).load(imagePath1).placeholder(R.mipmap.timg2).into(bg);
                    } else {
                        if(isLand){
                            bg.setImageResource(R.mipmap.timg22);
                        }else{
                            bg.setImageResource(R.mipmap.timg4);
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
            bg.setVisibility(View.INVISIBLE);
            initPlayer(videaPath);
        } else {
            ijkPlayer.setVisibility(View.INVISIBLE);
            bg.setVisibility(View.VISIBLE);
            if (!"".equals(imagePath)) {
                Picasso.with(mContext).load(imagePath).placeholder(R.mipmap.timg2).into(bg);
            } else {
                if(isLand){
                    bg.setImageResource(R.mipmap.timg22);
                }else{

                    //华侨城 bg
                    // bg.setImageResource(R.mipmap.timg4);

                    //学院路 bg
                    bg.setImageResource(R.mipmap.timg2);

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
                //-10000 ： 流地址错误
            /*    new AlertDialog.Builder(MainActivity.this)
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
                        }).create().show();*/
            if(what == -10000) {
                Snackbar.make(rootView,"视频流地址错误",Snackbar.LENGTH_LONG).setAction("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(MainActivity.this,ConfigurationActivity.class),CHANGE_SETTING_CODE);
                    }
                }).show();
            }

                handler.sendEmptyMessageDelayed(3,5000);
                return true;
            }

            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

                Log.d(TAG,"mediaplayer --> 播放信息: what="+what+"; extra="+extra);
                if(what == 3){
                    Snackbar.make(rootView,"马上开始播放",Snackbar.LENGTH_SHORT).show();
                    handler.sendEmptyMessage(2);
                }
               // startLoading();
                return false;
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
     * 固定10秒准备动画
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
                    }
                }
                if(msg.what == 2){

                    handler.removeMessages(3);
                    if(progress<100) {
                        progress = 99;
                        house.setProgress(progress);
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }

                }
                if(msg.what == 3){
                    if(progress<100) {
                        initBackGround(playPath, imagePath);
                    }
                    else{
                        new AlertDialog.Builder(MainActivity.this)
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
                                }).create().show();
                    }
                }
            }
        };

        handler.sendEmptyMessage(1);
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        ShowDialogFragment fragment = ShowDialogFragment.initFragment(mqBean);
        fragment.show(fragmentManager, "ShowDialogFragment");
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
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInitPlayer) {
            ijkPlayer.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

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
                    Log.d(TAG,"--> udp还活着！");
                    socket1.receive(packetRcv);
                    String rcvMsg=new String(packetRcv.getData(),packetRcv.getOffset(),packetRcv.getLength());
                    Log.d( TAG,"client --> msg:"+rcvMsg);
                    final MqBean mqBean = new Gson().fromJson(rcvMsg, MqBean.class);
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
                                            } else if("0".equals(mqBean.getMq_type())){
                                                mSoundPool.play(soundID.get(2), 1, 1, 0, 0, 1);
                                            } else {
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
                        initRightDialog(mqBean);
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


}
