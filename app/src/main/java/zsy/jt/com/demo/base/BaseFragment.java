package zsy.jt.com.demo.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    private Toast mToast = null;
    protected Context mContext;
    private Dialog progressDialog;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(setLayout(), container, false);
        mContext = getContext();
        initView(view);
        initData();
        initListener();
        initHttp();
        return view;
    }

    // 加载成功的布局, 必须由子类来实现
    protected abstract int setLayout();

    protected abstract void initView(View rootView);

    protected abstract void initData();

    protected abstract void initListener();

    protected abstract void initHttp();

    /**
     * Toast显示 解决Toast等待时间过长
     */
    protected void showToast(String text) {

        //判断队列中是否包含已经显示的Toast
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
//            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
