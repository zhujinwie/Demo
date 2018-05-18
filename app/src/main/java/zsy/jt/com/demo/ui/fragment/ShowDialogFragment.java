package zsy.jt.com.demo.ui.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import zsy.jt.com.demo.R;
import zsy.jt.com.demo.base.BaseDialogFragment;
import zsy.jt.com.demo.bean.MqBean;
import zsy.jt.com.demo.utils.ThreadPoolManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowDialogFragment extends BaseDialogFragment {

    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.top)
    RelativeLayout top;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.type)
    TextView type;
    @BindView(R.id.right)
    ImageView right;

    Unbinder unbinder;
    private MqBean bean;

    public ShowDialogFragment() {
        // Required empty public constructor
    }

    public static ShowDialogFragment initFragment(MqBean mqBean) {
        ShowDialogFragment showFragment = new ShowDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bean", mqBean);
        showFragment.setArguments(bundle);
        return showFragment;
    }

    @Override
    protected int setLayout() {
        return R.layout.fragment_show;
    }

    @Override
    protected void initView(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        Bundle bundle = getArguments();
        bean = (MqBean) bundle.getSerializable("bean");
        setViewData();
        initAnimator(right);
        dataThread();
    }

    /**
     * 弹框停留 millis:3000 后 dismiss
     */
    private void dataThread() {
        ThreadPoolManager.getInstance()
                .addTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    EventBus.getDefault().post("dismiss");
                                    dismiss();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 初始化
     */
    private void setViewData() {
        if (bean != null && bean.toString().length() > 0) {
            if ("1".equals(bean.getMq_type())) {
                // 未通过
                right.setImageResource(R.mipmap.unright);
                type.setTextColor(Color.parseColor("#fb3829"));
                name.setText("未知");
                type.setText("陌生人");
                image.setImageResource(R.mipmap.person);
            } else if("0".equals(bean.getMq_type())){
                // 已通过
                if (bean.getMq_image() != null && bean.getMq_image().length() > 0) {
                    Picasso.with(mContent).load(bean.getMq_image()).placeholder(R.mipmap.person).error(R.mipmap.person).into(image);
                }
                right.setImageResource(R.mipmap.right);
                type.setTextColor(Color.parseColor("#a6d1eb"));
                name.setText(bean.getMq_name() != null && bean.getMq_name().length() > 0 ? bean.getMq_name() : "未知");
                type.setText(bean.getMq_section() != null && bean.getMq_section().length() > 0 ? bean.getMq_section() : "陌生人");
            } else if("2".equals(bean.getMq_type())){
                // 账号已过期
                if (bean.getMq_image() != null && bean.getMq_image().length() > 0) {
                    Picasso.with(mContent).load(bean.getMq_image()).placeholder(R.mipmap.person).error(R.mipmap.person).into(image);
                }
                right.setImageResource(R.mipmap.overtime);
                type.setTextColor(Color.parseColor("#e6a23c"));
                name.setText(bean.getMq_name() != null && bean.getMq_name().length() > 0 ? bean.getMq_name() : "未知");
                //type.setText(bean.getMq_section() != null && bean.getMq_section().length() > 0 ? bean.getMq_section() : "陌生人");
                type.setText("该账号已过期");

            }
        }
    }

    /**
     * 对号设置属性动画
     *
     * @param right 对号image
     */
    private void initAnimator(final ImageView right) {
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(right, "scaleX", 0f, 1f, 1.1f, 1f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(right, "scaleY", 0f, 1f, 1.1f, 1f);
        final AnimatorSet set = new AnimatorSet();
        set.play(scaleXAnimator).with(scaleYAnimator);
        set.setDuration(800);
        ThreadPoolManager.getInstance()
                .addTask(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    set.start();
                                    right.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });


    }


    @Override
    protected void initData() {


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
