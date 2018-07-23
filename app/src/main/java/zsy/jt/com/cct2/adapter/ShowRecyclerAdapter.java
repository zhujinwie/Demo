package zsy.jt.com.cct2.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.internal.operators.observable.ObservableJoin;
import zsy.jt.com.cct2.R;
import zsy.jt.com.cct2.bean.MqBean;

/**
 * Created by ZhaoShengYang
 * email 99720140@qq.com
 */

public class ShowRecyclerAdapter extends BaseQuickAdapter<MqBean, BaseViewHolder> {

    public ShowRecyclerAdapter(@Nullable List<MqBean> data) {
        super(R.layout.fragment_show, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MqBean item) {
        ImageView right = helper.getView(R.id.right);
        right.setVisibility(View.VISIBLE);
        ImageView image = helper.getView(R.id.image);
        TextView name = helper.getView(R.id.name);
        TextView type = helper.getView(R.id.type);
        if (item != null && item.toString().length() > 0) {
            if ("1".equals(item.getMq_type())) {
                right.setImageResource(R.mipmap.unright);
                type.setTextColor(Color.parseColor("#fb3829"));
                name.setText("未知");
                type.setText("陌生人");
                image.setImageResource(R.mipmap.person);
            } else if("0".equals(item.getMq_type())){
                if (item.getMq_image() != null && item.getMq_image().length() > 0) {
                    Picasso.with(mContext).load(item.getMq_image()).placeholder(R.mipmap.person).error(R.mipmap.person).into(image);
                } else {
                    image.setImageResource(R.mipmap.person);
                }
                right.setImageResource(R.mipmap.right);
                type.setTextColor(Color.parseColor("#a6d1eb"));
                name.setText(item.getMq_name() != null && item.getMq_name().length() > 0 ? item.getMq_name() : "未知");
                type.setText(item.getMq_section() != null && item.getMq_section().length() > 0 ? item.getMq_section() : "陌生人");
            } else if("2".equals(item.getMq_type())){
                // 账号已过期
                if (item.getMq_image() != null && item.getMq_image().length() > 0) {
                    Picasso.with(mContext).load(item.getMq_image()).placeholder(R.mipmap.person).error(R.mipmap.person).into(image);
                }
                right.setImageResource(R.mipmap.overtime);
                type.setTextColor(Color.parseColor("#e6a23c"));
                name.setText(item.getMq_name() != null && item.getMq_name().length() > 0 ? item.getMq_name() : "未知");
                //type.setText(bean.getMq_section() != null && bean.getMq_section().length() > 0 ? bean.getMq_section() : "陌生人");
                type.setText("该账号已过期");
            }
            right.setAnimation(AnimationUtils.loadAnimation(mContext,R.anim.right_scale));
        }


    }


}
