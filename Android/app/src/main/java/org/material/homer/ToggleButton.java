package org.material.homer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Pedro on 19/05/2016.
 */
public class ToggleButton extends View {

    ///
    private RectF mContentRect;
    private Rect mButtonRect;
    private Rect mIconEnabledRect;
    private Rect mIconDisabledRect;
    public Drawable mBackgroundDrawable;
    public Drawable mEnabledButtonDrawable;
    public Drawable mDisableButtonDrawable;
    public Drawable mEnabledIconDrawable;
    public Drawable mDisabledIconDrawable;
    private boolean mPressed = false;
    private boolean mDrag = false;
    private boolean mToggle = false;
    private float mPressX = 0.0f;
    private float mPressY = 0.0f;
    private long mPressT = 0;
    private float mDragY = 0;
    private float mControl = 0;
    private Handler mAnimationHandler;
    private float mAnimationSpeed = 0.05f;
    private ToggleButtonListener mListener;

    private Runnable mTopReleaseAnimation = new Runnable() {
        @Override
        public void run() {
            mControl -= mAnimationSpeed;
            if(mControl < 0) {
                mControl = 0;
            } else {
                mAnimationHandler.post(mTopReleaseAnimation);
            }
            invalidate();
        }
    };

    private Runnable mBottomReleaseAnimation = new Runnable() {

        @Override
        public void run() {
            mControl += mAnimationSpeed;
            if(mControl >= 1) {
                mControl = 1;
            } else {
                mAnimationHandler.post(mBottomReleaseAnimation);
            }
            invalidate();
        }
    };

    /**
     * Constructor
     *
     * @param context
     */
    public ToggleButton(Context context) {
        super(context);
        setup();
    }

    /**
     * Constructor
     *
     * @param context
     */
    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    /**
     * Constructor
     *
     * @param context
     */
    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    /**
     * Constructor
     */
    private void setup() {
        mAnimationHandler = new Handler();
        mBackgroundDrawable = ContextCompat.getDrawable(getContext(), R.drawable.toggle_background);
        mEnabledButtonDrawable = ContextCompat.getDrawable(getContext(), R.drawable.toggle_button_enabled);
        mDisableButtonDrawable = ContextCompat.getDrawable(getContext(), R.drawable.toggle_button_disabled);
        mEnabledIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.toggle_icon_enabled);
        mDisabledIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.toggle_icon_disabled);
    }

    /**
     * On Measure
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mContentRect = getContentRect();
        mIconEnabledRect = getIconRect(0);
        mIconDisabledRect = getIconRect(1);
        mButtonRect = getButtonRect();
    }

    /**
     * Get Content Rect
     *
     * @return
     */
    private RectF getContentRect() {
        float left = getMeasuredWidth() * 0.11650f;
        float top = getMeasuredHeight() * 0.05333f;
        float right = getMeasuredWidth() * (1.0f - 0.11650f);
        float bottom = getMeasuredHeight() * (1.0f - 0.05333f);
        return new RectF(left, top, right, bottom);
    }

    /**
     * Get Button Rect
     *
     * @return
     */
    private Rect getButtonRect() {
        float buttonHeight = mContentRect.height() / 2.0f;
        float left = mContentRect.left;
        float top = mContentRect.top + buttonHeight * calcControl();
        float right = mContentRect.right;
        float bottom = top + buttonHeight;
        return new Rect((int) left, (int) top, (int) right, (int) bottom);
    }

    /**
     * Get Icon Rect
     *
     * @return
     */
    private Rect getIconRect(final int id) {
        if(id == 0) {
            float iconWidth = mContentRect.width() * 0.8f;
            float iconHeight = iconWidth;
            float left = mContentRect.left + (mContentRect.width() - iconWidth) / 2;
            float top = mContentRect.top + 3 * mContentRect.height() / 4 - iconHeight / 2;
            float right = left + iconWidth;
            float bottom = top + iconHeight;
            return new Rect((int) left, (int) top, (int) right, (int) bottom);
        } else {
            float iconWidth = mContentRect.width() * 0.8f;
            float iconHeight = iconWidth;
            float left = mContentRect.left + (mContentRect.width() - iconWidth) / 2;
            float top = mContentRect.top + mContentRect.height() / 4 - iconHeight / 2;
            float right = left + iconWidth;
            float bottom = top + iconHeight;
            return new Rect((int) left, (int) top, (int) right, (int) bottom);
        }
    }


    /**
     * Calc Control
     *
     * @return
     */
    private float calcControl() {
        return Math.max(0, Math.min(mControl + mDragY, 1.0f));
    }

    /**
     * Set Toggle
     *
     * @param toggle
     */
    public void setToggle(final boolean toggle, final boolean animate) {
        if(toggle == mToggle)
            return;
        releaseAnimation(!toggle, toggle, false, animate);
    }

    /**
     * Get Toggle
     *
     * @return
     */
    public boolean getToggle() {
        return mToggle;
    }

    /**
     * On Touch Event
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mPressed = true;
                mDrag = mButtonRect.contains((int)event.getX(), (int)event.getY());
                mPressX = event.getX();
                mPressY = event.getY();
                mPressT = event.getEventTime();
                mDragY = 0;
                return true;
            case MotionEvent.ACTION_MOVE:
                if(mPressed && mDrag) {
                    mDragY = (event.getY() - mPressY) / ((mContentRect.height() - mButtonRect.height()) * 1.0f);
                    invalidate();
                }
                return true;
            case MotionEvent.ACTION_UP:
                if(mPressed) {
                    if(mDrag) {
                        mControl = calcControl();
                        mDragY = 0;
                    }
                    final float dx = event.getX() - mPressX;
                    final float dy = event.getY() - mPressY;
                    final float d = (float)Math.hypot(dy, dx);
                    if(d <= getResources().getDisplayMetrics().density * 10 && (event.getEventTime() - mPressT) <= 1000) {
                        releaseAnimation(mToggle, !mToggle, true, true);
                    } else {
                        releaseAnimation(false, false, true, true);
                    }

                    mDrag = false;
                    mPressed = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mPressed = false;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Release Animation
     */
    protected void releaseAnimation(final boolean forceTop, final boolean forceDown, final boolean info, boolean animate) {
        mAnimationHandler.removeCallbacksAndMessages(null);
        if((mControl <= 0.5f || forceTop) && !forceDown) {
            mToggle = false;
            if(animate)
                 mAnimationHandler.post(mTopReleaseAnimation);
            else {
                mControl = 0;
                invalidate();
            }
            if(mListener != null && info)
                mListener.onToggle(false);
        } else if((mControl > 0.5f || forceDown) && !forceTop) {
            mToggle = true;
            if(animate)
                mAnimationHandler.post(mBottomReleaseAnimation);
            else {
                mControl = 1;
                invalidate();
            }
            if(mListener != null && info)
                mListener.onToggle(true);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mButtonRect = getButtonRect();

        final float control = calcControl();

        mBackgroundDrawable.setBounds(canvas.getClipBounds());
        mEnabledButtonDrawable.setBounds(mButtonRect);
        mDisableButtonDrawable.setBounds(mButtonRect);
        mEnabledIconDrawable.setBounds(mIconEnabledRect);
        mDisabledIconDrawable.setBounds(mIconDisabledRect);

        mBackgroundDrawable.draw(canvas);

       // mEnabledButtonDrawable.setAlpha((int) Math.round(255 * control));
        mEnabledButtonDrawable.setAlpha(255 - (int) Math.round(255 * control));

        mEnabledIconDrawable.draw(canvas);
        mDisabledIconDrawable.draw(canvas);
        mDisableButtonDrawable.draw(canvas);
        mEnabledButtonDrawable.draw(canvas);
    }

    /**
     * Set Listener
     *
     * @param listener
     */
    public void setListener(final ToggleButtonListener listener) {
        mListener = listener;
    }
}
