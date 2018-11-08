package android.extend.widget;

import android.content.Context;
import android.extend.util.LogUtil;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ExtendViewPager extends android.support.v4.view.ViewPager
{
	public final String TAG = getClass().getSimpleName();

	public ExtendViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ExtendViewPager(Context context)
	{
		super(context);
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		try
		{
			return super.onInterceptTouchEvent(ev);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		try
		{
			return super.onTouchEvent(ev);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		try
		{
			super.dispatchDraw(canvas);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}
}
