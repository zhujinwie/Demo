package zsy.jt.com.cct2.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import zsy.jt.com.cct2.R;
import zsy.jt.com.cct2.bean.RightDialog;

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
