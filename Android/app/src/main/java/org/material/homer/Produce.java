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
public class Produce extends View {

    ///
    private float mControl = 0.5f;
    private float mControlAnim = 0;

    public Drawable mProduceBottom;
    public Drawable mProduceContentRed;
    public Drawable mProduceContentGreen;
    public Drawable mProduceTop;

    public Handler mHandler;

    /**
     * Constructor
     *
     * @param context
     */
    public Produce(Context context) {
        super(context);
        setup();
    }

    /**
     * Constructor
     *
     * @param context
     */
    public Produce(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    /**
     * Constructor
     *
     * @param context
     */
    public Produce(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    /**
     * Constructor
     */
    private void setup() {
        mProduceBottom = ContextCompat.getDrawable(getContext(), R.drawable.bar_energy_green_background);
        mProduceContentRed = ContextCompat.getDrawable(getContext(), R.drawable.bar_energy_red_content);
        mProduceContentGreen = ContextCompat.getDrawable(getContext(), R.drawable.bar_energy_green_content);
        mProduceTop = ContextCompat.getDrawable(getContext(), R.drawable.bar_energy_green_details);
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
                if(mControlAnim < mControl) {
                    mControlAnim = mControlAnim + 0.01f;
                    if(mControlAnim > mControl)
                        mControlAnim = mControl;
                    else
                        mHandler.post(this);
                } else if(mControlAnim > mControl) {
                    mControlAnim = mControlAnim - 0.01f;
                    if(mControlAnim < mControl)
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
        mProduceBottom.setBounds(canvas.getClipBounds());
        mProduceContentRed.setBounds(canvas.getClipBounds());
        mProduceContentGreen.setBounds(canvas.getClipBounds());
        mProduceTop.setBounds(canvas.getClipBounds());
        mProduceBottom.draw(canvas);
        canvas.save();
        canvas.clipRect(getContentRect(canvas.getClipBounds()));
        mProduceContentRed.draw(canvas);
        mProduceContentGreen.setAlpha((int) (mControlAnim * 255.0f));
        mProduceContentGreen.draw(canvas);
        canvas.restore();
        mProduceTop.draw(canvas);
    }
}