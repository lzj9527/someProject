package android.extend.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class RotateImageButton extends PixelImageButton
{
	private float mFromDegrees;
	private float mToDegrees;
	private long mDuration = 100;
	private int mRepeatCount = 0;

	private final long mInterval = 50;
	private float mRotateDegreesSpeed;
	private float mDegrees = 0;
	private int mRepeated = 0;

	private boolean mRotateAnimationStarted = false;

	public RotateImageButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public RotateImageButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public RotateImageButton(Context context)
	{
		super(context);
	}

	public void startRotate(float fromDegrees, float toDegrees, long duration, int repeatCount)
	{
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mDuration = Math.max(duration, mDuration);
		mRepeatCount = repeatCount;
		if (mDuration > 0)
		{
			mRotateDegreesSpeed = (mToDegrees - mFromDegrees) * mInterval / mDuration;
		}
		else
		{
			mRotateDegreesSpeed = (mToDegrees - mFromDegrees) / 10;
		}
		mDegrees = mFromDegrees;
		mRepeated = 0;
		mRotateAnimationStarted = true;
		postRotate();
	}

	public void clearRotate()
	{
		mRotateAnimationStarted = false;
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (!mRotateAnimationStarted)
		{
			super.onDraw(canvas);
			return;
		}
		if (mDegrees != 0)
		{
			canvas.save();
			canvas.rotate(mDegrees, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
			super.onDraw(canvas);
			canvas.restore();
		}
		else
		{
			super.onDraw(canvas);
		}
		if (mRepeatCount > 0 && mRepeated >= mRepeatCount)
		{
			clearRotate();
			return;
		}
	}

	private void postRotate()
	{
		postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				if (!mRotateAnimationStarted)
				{
					return;
				}
				mDegrees += mRotateDegreesSpeed;
				if (mToDegrees >= mFromDegrees)
				{
					if (mDegrees >= mToDegrees)
					{
						mRepeated++;
						mDegrees = mFromDegrees;
					}
				}
				else
				{
					if (mDegrees <= mToDegrees)
					{
						mRepeated++;
						mDegrees = mFromDegrees;
					}
				}
				invalidate();
				if (mRepeatCount > 0 && mRepeated >= mRepeatCount)
				{
					return;
				}
				else
				{
					postRotate();
				}
			}
		}, mInterval);
	}
}
