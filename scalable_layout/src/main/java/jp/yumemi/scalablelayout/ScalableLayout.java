package jp.yumemi.scalablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Scalable layout.
 */
public class ScalableLayout extends ViewGroup {

    private static final int ORIENTATION_PORTRAIT = 0;
    private static final int ORIENTATION_LANDSCAPE = 1;

    protected int mBaseOrientation = ORIENTATION_PORTRAIT;
    protected int mBaseWidth = 640, mBaseHeight = 640;
    protected float mScaleFactor = 1;

    public ScalableLayout(Context context) {
        super(context);
    }

    public ScalableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFromAttributes(context, attrs);
    }

    public ScalableLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFromAttributes(context, attrs);
    }

    private void initFromAttributes(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ymlab);
        mBaseOrientation = a.getInteger(R.styleable.ymlab_ym_base_orientation, mBaseOrientation);
        mBaseWidth = a.getInteger(R.styleable.ymlab_ym_base_width, mBaseWidth);
        mBaseHeight = a.getInteger(R.styleable.ymlab_ym_base_height, mBaseHeight);
        mScaleFactor = a.getFloat(R.styleable.ymlab_ym_scale_factor, mScaleFactor);
        a.recycle();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        int WP = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        return new ScalableLayoutParams(WP, WP);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ScalableLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new ScalableLayoutParams(p);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();

        for (int i = 0; i < count; ++i) {
            View v = getChildAt(i);

            if (v.getVisibility() == View.GONE)
                continue;

            if (v.getLayoutParams() instanceof ScalableLayoutParams) {
                ScalableLayoutParams params = (ScalableLayoutParams) v.getLayoutParams();
                int childLeft = (int) (params.relativeLeft * mScaleFactor);
                int childTop = (int) (params.relativeTop * mScaleFactor);
                v.layout(childLeft, childTop, childLeft + v.getMeasuredWidth(), childTop + v.getMeasuredHeight());
            }
            else {
                int childLeft = 0;
                int childTop = 0;
                v.layout(childLeft, childTop, childLeft + v.getMeasuredWidth(), childTop + v.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBaseOrientation == ORIENTATION_PORTRAIT) {
            int w = MeasureSpec.getSize(widthMeasureSpec);
            mScaleFactor = (float) w / (float) mBaseWidth;
            int h = (int) (mBaseHeight * mScaleFactor);

            setMeasuredDimension(w, h);
        }
        else if (mBaseOrientation == ORIENTATION_LANDSCAPE) {
            int h = MeasureSpec.getSize(heightMeasureSpec);
            mScaleFactor = (float) h / (float) mBaseHeight;
            int w = (int) (mBaseWidth * mScaleFactor);

            setMeasuredDimension(w, h);
        }
        else {
            throw new IllegalStateException();
        }

        int count = getChildCount();

        for (int i = 0; i < count; ++i) {
            View v = getChildAt(i);

            assert v != null;

            if (v.getVisibility() == View.GONE)
                continue;

            if (v.getLayoutParams() instanceof ScalableLayoutParams) {
                ScalableLayoutParams params = (ScalableLayoutParams) v.getLayoutParams();
                int childWidth = (int) (params.relativeWidth * mScaleFactor);
                int childHeight = (int) (params.relativeHeight * mScaleFactor);

                int childMeasureWidth = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
                int childMeasureHeight = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);

                if (v instanceof TextView && params.fontSize > 0) {
                    ((TextView) v).setTextSize(TypedValue.COMPLEX_UNIT_PX, params.fontSize * mScaleFactor);
                }

                v.measure(childMeasureWidth, childMeasureHeight);
            }
            else {
                measureChild(v, widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    public static class ScalableLayoutParams extends ViewGroup.LayoutParams {

        public float relativeLeft = 0, relativeTop = 0;
        public float relativeWidth = 0, relativeHeight = 0;
        private float fontSize = 0;

        public ScalableLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ymlab);
            relativeLeft = a.getFloat(R.styleable.ymlab_ym_left, relativeLeft);
            relativeTop = a.getFloat(R.styleable.ymlab_ym_top, relativeTop);
            relativeWidth = a.getFloat(R.styleable.ymlab_ym_width, relativeWidth);
            relativeHeight = a.getFloat(R.styleable.ymlab_ym_height, relativeHeight);
            fontSize = a.getFloat(R.styleable.ymlab_ym_font_size, fontSize);
            a.recycle();
        }

        public ScalableLayoutParams(int width, int height) {
            super(width, height);
        }

        public ScalableLayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}
