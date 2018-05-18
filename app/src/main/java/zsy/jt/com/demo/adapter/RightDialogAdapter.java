package zsy.jt.com.demo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import zsy.jt.com.demo.R;
import zsy.jt.com.demo.bean.RightDialog;

/**
 * Created by ZhaoShengYang
 * email 99720140@qq.com
 */

public class RightDialogAdapter extends BaseQuickAdapter<RightDialog, BaseViewHolder> {
    public RightDialogAdapter(@Nullable List<RightDialog> data) {
        super(R.layout.right_dialog, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RightDialog item) {

    }
}
