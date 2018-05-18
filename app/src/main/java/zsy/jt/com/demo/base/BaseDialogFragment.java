package zsy.jt.com.demo.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import zsy.jt.com.demo.R;

public abstract class BaseDialogFragment extends AppCompatDialogFragment {
    private Toast mToast;
    protected Context mContent;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mContent=getContext();
        View view = LayoutInflater.from(mContent).inflate(setLayout(), null);
        initView(view);
        initData();
        AppCompatDialog appCompatDialog = new AppCompatDialog(mContent, R.style.DialogNoTitle);
        appCompatDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        appCompatDialog.setCanceledOnTouchOutside(false);
        appCompatDialog.getWindow().getAttributes().windowAnimations=R.style.dialogAnim;

        return appCompatDialog;

    }

    // 加载成功的布局, 必须由子类来实现
    protected abstract int setLayout();

    protected abstract void initView(View rootView);

    protected abstract void initData();
    protected void showToast(Context context, String text) {

        //判断队列中是否包含已经显示的Toast
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
}
