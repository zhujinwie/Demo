package zsy.jt.com.demo.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;


import org.greenrobot.eventbus.EventBus;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import io.reactivex.functions.Consumer;
import zsy.jt.com.demo.R;
import zsy.jt.com.demo.utils.IpUtil;
import zsy.jt.com.demo.utils.SharedPreferencesUtils;
import zsy.jt.com.demo.utils.StateBarTranslucentUtils;

@SuppressWarnings("ConstantConditions")
public class ConfigurationActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.ip_edit)
    EditText ipEdit;
    @BindView(R.id.path_edit)
    EditText pathEdit;
    @BindView(R.id.image_edit)
    EditText imageEdit;
    @BindView(R.id.switch_btn)
    SwitchCompat switchBtn;
    @BindView(R.id.switch_btn2)
    SwitchCompat switchBtn2;
    @BindView(R.id.switch_btn3)
    SwitchCompat switchBtn3;
    @BindView(R.id.put)
    Button put;
    @BindView(R.id.fenbianlv_edit)
    EditText fblEt;

    ListPopupWindow listPopulWindow;

    final String[] titleList={
            "rtsp://admin:password@host:port/h264/ch1/main/av_stream",
            "rtsp://host/h264/ch2/sub/av_stream",
            "rtsp://host/vod/mp4:***.mov",
            "rtsp://admin:password@host:port/cam/realmonitor?channel=1&subtype=0"};

    final String[] fblList={"4:3","16:9","2:1","5:4","16:10","21:9","自定义"};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateBarTranslucentUtils.setStateBarTranslucent(this);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);
        initOrientation();
        RxPermissions rxPermissions=new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if(aBoolean){
                            /*//获取权限成功
                            ConnectivityManager conManager= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo mobileNetWorkInfo=conManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                            NetworkInfo wifiInfo=conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                            if(mobileNetWorkInfo!=null && mobileNetWorkInfo.isConnected()){
                                //移动网络链接，获取相应ip
                                String mobileIp=getLocalIpAddress();
                                ipEdit.setText(mobileIp);
                            }else if(wifiInfo != null && wifiInfo.isConnected()){
                                //wifi 网络，获取相应ip
                                String wifiIp=getWifiIpAddress();
                                ipEdit.setText(wifiIp);
                            }else{
                                Toast.makeText(ConfigurationActivity.this, "", Toast.LENGTH_SHORT).show();
                            }*/
                            if(TextUtils.isEmpty(IpUtil.getLocalIp())){

                                ipEdit.setText("未获取到本机Ip，请手动获取！");

                            }else{

                                ipEdit.setText(IpUtil.getLocalIp());
                            }

                        }else{
                            //未获得权限
                            Toast.makeText(ConfigurationActivity.this, "未获得网络权限，无法获取本机IP地址！", Toast.LENGTH_SHORT).show();

                            new AlertDialog.Builder(ConfigurationActivity.this)
                                    .setMessage("我们需要您提供完全的网络访问权限，以获取本机连接状态与Ip")
                                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent localIntent = new Intent();
                                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            if (Build.VERSION.SDK_INT >= 9) {
                                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                                            } else if (Build.VERSION.SDK_INT <= 8) {
                                                localIntent.setAction(Intent.ACTION_VIEW);
                                                localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
                                                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                                            }
                                            startActivity(localIntent);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(ConfigurationActivity.this, "您拒绝了权限申请，请到设置界面打开，否侧无法使用本软件！", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                    }
                });


        //ipEdit.setText(SharedPreferencesUtils.get(this, "MQ_HOST", "") + "");
        pathEdit.setText(SharedPreferencesUtils.get(this, "PLAY_PATH", "") + "");
        imageEdit.setText(SharedPreferencesUtils.get(this, "image_path", "") + "");
        fblEt.setText(SharedPreferencesUtils.get(this,"fbl","1.33")+"");

        if (SharedPreferencesUtils.get(this, "state", false) != null) {
            switchBtn.setChecked((Boolean) SharedPreferencesUtils.get(ConfigurationActivity.this, "state", false));
        }
        if (SharedPreferencesUtils.get(this, "state2", false) != null) {
            switchBtn2.setChecked((Boolean) SharedPreferencesUtils.get(ConfigurationActivity.this, "state2", false));
        }
        if (SharedPreferencesUtils.get(this, "state3", false) != null) {
            switchBtn3.setChecked((Boolean) SharedPreferencesUtils.get(ConfigurationActivity.this, "state3", false));
        }


        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(ConfigurationActivity.this, "切换为横屏", Toast.LENGTH_SHORT).show();
                    //SharedPreferencesUtils.put(ConfigurationActivity.this, "state", true);
                } else {
                    Toast.makeText(ConfigurationActivity.this, "切换为竖屏", Toast.LENGTH_SHORT).show();
                    //SharedPreferencesUtils.put(ConfigurationActivity.this, "state", false);
                }
            }
        });
        switchBtn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(ConfigurationActivity.this, "切换为多人", Toast.LENGTH_SHORT).show();
                    //SharedPreferencesUtils.put(ConfigurationActivity.this, "state2", true);
                } else {
                    Toast.makeText(ConfigurationActivity.this, "切换为单人", Toast.LENGTH_SHORT).show();
                    // SharedPreferencesUtils.put(ConfigurationActivity.this, "state2", false);
                }
            }
        });
        switchBtn3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(ConfigurationActivity.this, "切换为背景视频模式", Toast.LENGTH_SHORT).show();
                    //SharedPreferencesUtils.put(ConfigurationActivity.this, "state3", true);
                } else {
                    Toast.makeText(ConfigurationActivity.this, "切换为背景图片模式", Toast.LENGTH_SHORT).show();
                    //SharedPreferencesUtils.put(ConfigurationActivity.this, "state3", false);
                }
            }
        });

        put.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SharedPreferencesUtils.put(ConfigurationActivity.this, "MQ_HOST", ipEdit.getText().toString().trim());
                SharedPreferencesUtils.put(ConfigurationActivity.this, "PLAY_PATH", pathEdit.getText().toString().trim());
                SharedPreferencesUtils.put(ConfigurationActivity.this, "image_path", imageEdit.getText().toString().trim());
                SharedPreferencesUtils.put(ConfigurationActivity.this,"fbl",fblEt.getText().toString().trim());

                SharedPreferencesUtils.put(ConfigurationActivity.this,"state",switchBtn.isChecked());
                SharedPreferencesUtils.put(ConfigurationActivity.this,"state2",switchBtn2.isChecked());
                SharedPreferencesUtils.put(ConfigurationActivity.this,"state3",switchBtn3.isChecked());



                Intent intent=new Intent(ConfigurationActivity.this,MainActivity.class);
                intent.putExtra("play_path",pathEdit.getText().toString().trim());
                intent.putExtra("image_path",imageEdit.getText().toString().trim());
                intent.putExtra("state",switchBtn.isChecked());
                intent.putExtra("state2",switchBtn2.isChecked());
                intent.putExtra("state3",switchBtn3.isChecked());
                setResult(RESULT_OK,intent);

                Toast.makeText(ConfigurationActivity.this, "部分设置需要重启软件才能生效！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public String getLocalIpAddress(){

        try {
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni: nilist)
            {
                ArrayList<InetAddress>  ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address: ialist){
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address)
                    {
                        return address.getHostAddress();
                    }
                }
            }

        } catch (SocketException ex) {
            Log.e("localip", ex.toString());
        }
        return null;

    }
    public String getWifiIpAddress() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        StringBuilder sb = new StringBuilder();
        sb.append(ipAddress & 0xFF).append(".");
        sb.append((ipAddress >> 8) & 0xFF).append(".");
        sb.append((ipAddress >> 16) & 0xFF).append(".");
        sb.append((ipAddress >> 24) & 0xFF);
        return sb.toString();
    }

    private void initOrientation(){
        if ((Boolean)SharedPreferencesUtils.get(this,"state",true)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @OnTouch({R.id.path_edit,R.id.fenbianlv_edit})
    boolean onTouchPathEdit(View v, MotionEvent event){

        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if(v.getId() == R.id.path_edit){
                if (event.getX() >= (pathEdit.getWidth() - pathEdit.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // your action here
                    pathEdit.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.jiantou_up), null);
                    showListPopulWindow();
                    return true;
                }
            }else if(v.getId() == R.id.fenbianlv_edit){
                if (event.getX() >= (fblEt.getWidth() - fblEt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // your action here
                    fblEt.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.jiantou_up), null);
                    showFenBianLvListWindow();
                    return true;
                }
            }

        }
        return false;
    }


    public void showListPopulWindow(){

        pathEdit.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.jiantou_up),null);
        listPopulWindow = new ListPopupWindow(this);
        listPopulWindow.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titleList));
        listPopulWindow.setAnchorView(pathEdit);
        listPopulWindow.setModal(true);
        //自动填入
        listPopulWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pathEdit.setText(titleList[position]);
            }
        });

        listPopulWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                pathEdit.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.jiantou_down),null);
            }
        });
        listPopulWindow.show();

    }


    public void showFenBianLvListWindow(){


        pathEdit.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.jiantou_up),null);
        listPopulWindow = new ListPopupWindow(this);
        listPopulWindow.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fblList));
        listPopulWindow.setAnchorView(fblEt);
        listPopulWindow.setModal(true);

        //自动填入
        listPopulWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //{"4:3","16:9","2:1","5:4","16:10","21:9","自定义"}
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){

                    case 0:
                        Log.d(TAG,"--> dianji position == 0");
                        fblEt.setText("1.33");
                        break;
                    case 1:
                        fblEt.setText("1.78");
                        break;
                    case 2:
                        fblEt.setText("2");
                        break;
                    case 3:
                        fblEt.setText("1.25");
                        break;
                    case 4:
                        fblEt.setText("1.6");
                        break;
                    case 5:
                        fblEt.setText("2.33");
                        break;
                   default:
                        fblEt.setText("");
                        break;
                }
            }
        });

        listPopulWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                fblEt.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.jiantou_down),null);
            }
        });

        listPopulWindow.show();
    }




}
