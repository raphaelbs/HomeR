package org.material.homer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Pedro on 19/05/2016.
 */
public class Batery extends View {

    ///
    private float mControl = 0f;
    private float mControlAnim = 0;

    public Drawable mBateryBottom;
    public Drawable mBateryContentRed;
    public Drawable mBateryContentGreen;
    public Drawable mBateryTop;

    public Handler mHandler;

    /**
     * Constructor
     *
     * @param context
     */
    public Batery(Context context) {
        super(context);
        setup();
    }

    /**
     * Constructor
     *
     * @param context
     */
    public Batery(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    /**
     * Constructor
     *
     * @param context
     */
    public Batery(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    /**
     * Constructor
     */
    private void setup() {
        mBateryBottom = ContextCompat.getDrawable(getContext(), R.drawable.bar_batery_red_background);
        mBateryContentRed = ContextCompat.getDrawable(getContext(), R.drawable.bar_batery_red_content);
        mBateryContentGreen = ContextCompat.getDrawable(getContext(), R.drawable.bar_batery_green_content);
        mBateryTop = ContextCompat.getDrawable(getContext(), R.drawable.bar_batery_green_detail);
        mHandler = new Handler();
    }

    /**
     * Get Content Rect
     * @return
     */
    protected Rect getContentRect(final Rect c) {
        final Rect content = new Rect(c);
        c.top += (int)((1.0f - mControlAnim) * c.height());
        return c;
    }

    /**
     * Set Control
     *
     * @param control
     */
    public void setControl(final float control) {
        mControl = Math.max(0, Math.min(control, 1));
        mHandler.removeCallbacksAndMessages(null);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mControlAnim < mControl) {
                    mControlAnim = mControlAnim + 0.01f;
                    if (mControlAnim > mControl)
                        mControlAnim = mControl;
                    else
                        mHandler.post(this);
                } else if (mControlAnim > mControl) {
                    mControlAnim = mControlAnim - 0.01f;
                    if (mControlAnim < mControl)
                        mControlAnim = mControl;
                    else
                        mHandler.post(this);
                }
                invalidate();
            }
        });
    }

    /**
     * On Draw
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBateryBottom.setBounds(canvas.getClipBounds());
        mBateryContentRed.setBounds(canvas.getClipBounds());
        mBateryContentGreen.setBounds(canvas.getClipBounds());
        mBateryTop.setBounds(canvas.getClipBounds());
        mBateryBottom.draw(canvas);
        canvas.save();
        canvas.clipRect(getContentRect(canvas.getClipBounds()));
        mBateryContentRed.draw(canvas);
        mBateryContentGreen.setAlpha((int) (mControlAnim * 255.0f));
        mBateryContentGreen.draw(canvas);
        canvas.restore();
        mBateryTop.draw(canvas);
    }
}
