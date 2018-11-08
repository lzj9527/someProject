package android.extend.widget;

import android.content.Context;
import android.extend.widget.ViewObservable.OnViewObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;
import android.widget.RelativeLayout;

public class ExtendRelativeLayout extends RelativeLayout implements ViewObservable.IViewObservable, Checkable
{
	ViewObservable mViewObservable = new ViewObservable(this);
	boolean mChecked = false;
	boolean mInterceptTouchEventToDownward = false;
	DispatchTouchListener mDispatchTouchListener;

	public ExtendRelativeLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public ExtendRelativeLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ExtendRelativeLayout(Context context)
	{
		super(context);
	}

	@Override
	public void registerObserver(OnViewObserver observer)
	{
		mViewObservable.registerObserver(observer);
	}

	@Override
	public void unregisterObserver(OnViewObserver observer)
	{
		mViewObservable.unregisterObserver(observer);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewObservable.notifyOnMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		mViewObservable.notifyOnLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mViewObservable.notifyOnSizeChanged(w, h, oldw, oldh);
	}

	public void setDispatchTouchListener(DispatchTouchListener listener)
	{
		mDispatchTouchListener = listener;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		boolean result = false;
		if (mDispatchTouchListener != null)
			result = mDispatchTouchListener.dispatchTouch(this, event);
		if (!result)
			result = super.dispatchTouchEvent(event);
		if (!result)
			result = super.onTouchEvent(event);
		if (mInterceptTouchEventToDownward)
			return true;
		else
			return result;
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		mViewObservable.clear();
	}

	@Override
	public void setChecked(boolean checked)
	{
		mChecked = checked;
		super.setActivated(checked);
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View child = getChildAt(i);
			if (child instanceof Checkable)
				((Checkable)child).setChecked(checked);
			else
				child.setActivated(checked);
		}
	}

	@Override
	public boolean isChecked()
	{
		return mChecked;
	}

	@Override
	public void toggle()
	{
		setChecked(!mChecked);
	}

	/**
	 * 拦截TouchEvent事件向下传递
	 * */
	public void setInterceptTouchEventToDownward(boolean intercept)
	{
		mInterceptTouchEventToDownward = intercept;
	}

	public boolean isInterceptTouchEventToDownward()
	{
		return mInterceptTouchEventToDownward;
	}
}
