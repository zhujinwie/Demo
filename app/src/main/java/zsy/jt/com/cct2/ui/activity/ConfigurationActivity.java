package zsy.jt.com.cct2.ui.activity;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;

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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.util.Enumeration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

import zsy.jt.com.cct2.R;
import zsy.jt.com.cct2.app.App;

import zsy.jt.com.cct2.utils.SharedPreferencesUtils;
import zsy.jt.com.cct2.utils.StateBarTranslucentUtils;

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
            App.rtspUrl,
            "rtsp://admin:password@host:port/cam/realmonitor?channel=1&subtype=0",
            "rtsp://10.0.5.200:554/user=admin&password=&channel=1&stream=0.sdp?real_stream",
    };

    final String[] fblList={"4:3","16:9","2:1","5:4","16:10","21:9","自定义"};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StateBarTranslucentUtils.setStateBarTranslucent(this);
        setContentView(R.layout.activity_configuration);
        ButterKnife.bind(this);


        //ipEdit.setText(SharedPreferencesUtils.get(this, "MQ_HOST", "") + "");
        new Thread(){
            @Override
            public void run() {
               final String ip = getClientIP();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ipEdit.setText(ip);
                    }
                });
            }
        }.start();
        pathEdit.setText(SharedPreferencesUtils.get(this, "PLAY_PATH", "rtsp://10.0.5.200:554/user=admin&password=&channel=1&stream=0.sdp?real_stream") + "");
        imageEdit.setText(SharedPreferencesUtils.get(this, "image_path", "") + "");
        fblEt.setText(SharedPreferencesUtils.get(this,"fbl","1.78")+"");

        if (SharedPreferencesUtils.get(this, "state", false) != null) {
            switchBtn.setChecked((Boolean) SharedPreferencesUtils.get(ConfigurationActivity.this, "state", false));
        }
        if (SharedPreferencesUtils.get(this, "state2", false) != null) {
            switchBtn2.setChecked((Boolean) SharedPreferencesUtils.get(ConfigurationActivity.this, "state2", false));
        }
        if (SharedPreferencesUtils.get(this, "state3", true) != null) {
            switchBtn3.setChecked((Boolean) SharedPreferencesUtils.get(ConfigurationActivity.this, "state3", true));
        }

        initOrientation();

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

                //Toast.makeText(ConfigurationActivity.this, "部分设置需要重启软件才能生效！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    /**
     * 获取安卓设备当前的IP地址（有线或无线）
     *
     * @return
     */
    private String getClientIP() {

        try {
            // 获取本地设备的所有网络接口
            Enumeration<NetworkInterface> enumerationNi = NetworkInterface
                    .getNetworkInterfaces();
            while (enumerationNi.hasMoreElements()) {
                NetworkInterface networkInterface = enumerationNi.nextElement();
                String interfaceName = networkInterface.getDisplayName();
                Log.i(TAG, "网络名字" + interfaceName);

                // 如果是有限网卡
                if (interfaceName.contains("eth")) {
                    Enumeration<InetAddress> enumIpAddr = networkInterface
                            .getInetAddresses();

                    while (enumIpAddr.hasMoreElements()) {
                        // 返回枚举集合中的下一个IP地址信息
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        // 不是回环地址，并且是ipv4的地址
                        if (!inetAddress.isLoopbackAddress()
                                && inetAddress instanceof Inet4Address) {
                            Log.i(TAG, inetAddress.getHostAddress() + "   ");

                            return inetAddress.getHostAddress();
                        }
                    }
                    //  如果是无限网卡
                } else if (interfaceName.equals("wlan0")) {
                    Enumeration<InetAddress> enumIpAddr = networkInterface
                            .getInetAddresses();

                    while (enumIpAddr.hasMoreElements()) {
                        // 返回枚举集合中的下一个IP地址信息
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        // 不是回环地址，并且是ipv4的地址
                        if (!inetAddress.isLoopbackAddress()
                                && inetAddress instanceof Inet4Address) {
                            Log.i(TAG, inetAddress.getHostAddress() + "   ");

                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";

    }

    private void initOrientation(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
