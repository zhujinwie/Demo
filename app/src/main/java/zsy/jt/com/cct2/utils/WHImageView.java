package zsy.jt.com.cct2.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import zsy.jt.com.cct2.R;

public class WHImageView extends android.support.v7.widget.AppCompatImageView {

    private float mRatio;

    public WHImageView(Context context) {
        super(context);
    }
    public WHImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public WHImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WHImageView);
        try{
            mRatio = ta.getFloat(R.styleable.WHImageView_ratio_w_h,1.33f);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            ta.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);

        int heightSize=MeasureSpec.getSize(heightMeasureSpec);

        int widthMode=MeasureSpec.getMode(widthMeasureSpec);

        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        //宽度确定，高度未定
        if(widthMode==MeasureSpec.EXACTLY && heightMode!=MeasureSpec.EXACTLY && mRatio!=0){

            heightSize=(int)(widthSize/mRatio+0.5f);

            heightMeasureSpec=MeasureSpec.makeMeasureSpec(heightSize,MeasureSpec.EXACTLY);

        }//宽度未定， 高度已定
        else if(widthMode!=MeasureSpec.EXACTLY && heightMode==MeasureSpec.EXACTLY && mRatio!=0){

            widthSize=(int)(heightSize*mRatio+0.5f);

            widthMeasureSpec=MeasureSpec.makeMeasureSpec(widthSize,MeasureSpec.EXACTLY);

        }
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }
}
