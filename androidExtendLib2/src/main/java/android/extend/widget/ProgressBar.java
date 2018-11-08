package android.extend.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ProgressBar extends FrameLayout
{
	public static final String TAG = ProgressBar.class.getSimpleName();

	public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
	public static final int VERTICAL = LinearLayout.VERTICAL;

	public enum ChangeProgressMode
	{
		NONE, TOUCH, SLIDE,
	}

	private static ChangeProgressMode[] mChangeProgressModeArray = new ChangeProgressMode[] { ChangeProgressMode.NONE,
			ChangeProgressMode.TOUCH, ChangeProgressMode.SLIDE };

	private boolean mHasInitialized = false;
	private int mOrientation = HORIZONTAL;
	private View mProgressBackgroundView;
	private View mProgressView;
	private boolean mProgressVisible = true;
	private View mSliderView;
	private boolean mSliderVisible = true;
	// private int mProgressMaxWidth;
	private float mPercent = -1;
	private OnProgressChangedListener mListener;
	// private float mNotifyPercent = -1;
	private ChangeProgressMode mChangeProgressMode = ChangeProgressMode.NONE;
	private boolean mStartTrackingTouch = false;
	private PointF mPointF = new PointF();
	private OnTouchEventActionListener mTouchEventActionListener;

	public ProgressBar(Context context)
	{
		super(context);
		// initialize();
	}

	public ProgressBar(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		// initialize();
	}

	public ProgressBar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		try
		{
			int[] styleableArray = ResourceUtil.getStyleableArray(getContext(), "ProgressBar");
			TypedArray a = context.obtainStyledAttributes(attrs, styleableArray, defStyle, 0);

			int styleable = ResourceUtil.getStyleableId(getContext(), "ProgressBar_orientation");
			mOrientation = a.getInt(styleable, 0);

			styleable = ResourceUtil.getStyleableId(getContext(), "ProgressBar_changeProgressMode");
			int index = a.getInt(styleable, 1);
			mChangeProgressMode = mChangeProgressModeArray[index];

			a.recycle();
			LogUtil.v(TAG, "mOrientation=" + mOrientation + "; mChangeProgressMode=" + mChangeProgressMode);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
		// initialize();
	}

	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		initialize();
	}

	private void initialize()
	{
		if (mHasInitialized)
			return;
		mHasInitialized = true;
		LogUtil.d(TAG, "initialize: mPercent=" + mPercent);

		setProgressImpl(MotionEvent.ACTION_UP, mPercent);
	}

	public void setProgressOrientation(int orientation)
	{
		mOrientation = orientation;
	}

	public int getProgressOrientation()
	{
		return mOrientation;
	}

	private void ensureProgressBackgroudView()
	{
		if (mProgressBackgroundView == null)
		{
			int id = ResourceUtil.getId(getContext(), "background");
			mProgressBackgroundView = findViewById(id);
			if (mProgressBackgroundView == null)
			{
				mProgressBackgroundView = new ImageView(getContext());
				int resId = ResourceUtil.getDrawableId(getContext(), "default_progress_bg");
				mProgressBackgroundView.setBackgroundResource(resId);
				FrameLayout.LayoutParams params;
				if (mOrientation == VERTICAL)
				{
					params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,
							Gravity.CENTER_HORIZONTAL);
				}
				else
				{
					params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
							Gravity.CENTER_VERTICAL);
				}
				mProgressBackgroundView.setLayoutParams(params);
				addView(mProgressBackgroundView);
			}
			// mProgressBackgroundView.setOnTouchListener(new View.OnTouchListener()
			// {
			// @Override
			// public boolean onTouch(View v, MotionEvent event)
			// {
			// if (mChangeProgressMode == ChangeProgressMode.TOUCH)
			// {
			// switch (event.getAction())
			// {
			// case MotionEvent.ACTION_DOWN:
			// mStartTrackingTouch = true;
			// setPressed(true);
			// trackTouchEvent(event);
			// break;
			// case MotionEvent.ACTION_MOVE:
			// trackTouchEvent(event);
			// break;
			// case MotionEvent.ACTION_UP:
			// mStartTrackingTouch = false;
			// trackTouchEvent(event);
			// setPressed(false);
			// invalidate();
			// break;
			// case MotionEvent.ACTION_CANCEL:
			// mStartTrackingTouch = false;
			// setPressed(false);
			// invalidate();
			// break;
			// }
			// return true;
			// }
			// return false;
			// }
			// });
		}
	}

	private void ensureProgressView()
	{
		if (mProgressView == null)
		{
			int id = ResourceUtil.getId(getContext(), "progress");
			mProgressView = findViewById(id);
			if (mProgressView == null)
			{
				mProgressView = new ImageView(getContext());
				int resId = ResourceUtil.getDrawableId(getContext(), "default_progress");
				mProgressView.setBackgroundResource(resId);
				FrameLayout.LayoutParams params;
				if (mOrientation == VERTICAL)
				{
					params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT,
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
				}
				else
				{
					params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
							Gravity.LEFT | Gravity.CENTER_VERTICAL);
				}
				mProgressView.setLayoutParams(params);
				mProgressView.setVisibility(View.INVISIBLE);
				addView(mProgressView);
			}
		}
	}

	private void ensureSliderView()
	{
		if (mSliderView == null)
		{
			int id = ResourceUtil.getId(getContext(), "slider");
			mSliderView = findViewById(id);
			if (mSliderView == null)
			{
				mSliderView = new ImageView(getContext());
				// int resId = ResourceUtil.getDrawableId(getContext(), "default_progress_thumb");
				// mSliderView.setBackgroundResource(resId);
				FrameLayout.LayoutParams params;
				if (mOrientation == VERTICAL)
				{
					params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
				}
				else
				{
					params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
							Gravity.LEFT | Gravity.CENTER_VERTICAL);
				}
				mSliderView.setLayoutParams(params);
				mSliderView.setVisibility(View.INVISIBLE);
				addView(mSliderView);
			}
			mSliderView.setOnTouchListener(new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if (mChangeProgressMode == ChangeProgressMode.SLIDE)
					{
						// LogUtil.d(TAG, "mSliderView onTouch: " + event);
						switch (event.getAction())
						{
							case MotionEvent.ACTION_DOWN:
								onTouchEventActionStart();
								break;
						// case MotionEvent.ACTION_MOVE:
						// trackSliderEvent(event);
						// break;
						// case MotionEvent.ACTION_UP:
						// mStartTrackingTouch = false;
						// ProgressBar.this.setPressed(false);
						// invalidate();
						// break;
						// case MotionEvent.ACTION_CANCEL:
						// mStartTrackingTouch = false;
						// ProgressBar.this.setPressed(false);
						// invalidate();
						// break;
						}
						// mPointF.set(event.getX(), event.getY());
						// return true;
					}
					return false;
				}
			});
		}
	}

	public void setProgressBackgroundResource(int resId)
	{
		ensureProgressBackgroudView();
		mProgressBackgroundView.setBackgroundResource(resId);
	}

	public void setProgressBackgroundColor(int color)
	{
		ensureProgressBackgroudView();
		mProgressBackgroundView.setBackgroundColor(color);
	}

	public void setProgressBackgroundDrawable(Drawable drawable)
	{
		ensureProgressBackgroudView();
		mProgressBackgroundView.setBackground(drawable);
	}

	public void setProgressResource(int resId)
	{
		ensureProgressView();
		mProgressView.setBackgroundResource(resId);
	}

	public void setProgressColor(int color)
	{
		ensureProgressView();
		mProgressView.setBackgroundColor(color);
	}

	public void setProgressDrawable(Drawable drawable)
	{
		ensureProgressView();
		mProgressView.setBackground(drawable);
	}

	public void setSliderResource(int resId)
	{
		ensureSliderView();
		mSliderView.setBackgroundResource(resId);
	}

	public void setSliderColor(int color)
	{
		ensureSliderView();
		mSliderView.setBackgroundColor(color);
	}

	public void setSliderDrawable(Drawable drawable)
	{
		ensureSliderView();
		mSliderView.setBackground(drawable);
	}

	// public View getBackgroundView()
	// {
	// return mBackgroundView;
	// }
	//
	// public View getProgressView()
	// {
	// return mProgressView;
	// }
	//
	// public View getThumbView()
	// {
	// return mThumbView;
	// }

	public void setOnProgressChangedListener(OnProgressChangedListener listener)
	{
		mListener = listener;
	}

	public void setOnTouchEventActionListener(OnTouchEventActionListener listener)
	{
		mTouchEventActionListener = listener;
	}

	public void setProgressVisible(boolean visible)
	{
		mProgressVisible = visible;
	}

	public void setSliderVisible(boolean visible)
	{
		mSliderVisible = visible;
	}

	public void setChangeProgressMode(ChangeProgressMode mode)
	{
		mChangeProgressMode = mode;
	}

	private void notifyProgressChanged(float percent)
	{
		if (mListener != null)
		{
			// mNotifyPercent = percent;
			mListener.onProgressChanged(this, percent);
		}
	}

	private void notifyTouchEventActionStart()
	{
		if (mTouchEventActionListener != null)
			mTouchEventActionListener.onTouchEventActionStart(this);
	}

	private void notifyTouchEventActionFinish()
	{
		if (mTouchEventActionListener != null)
			mTouchEventActionListener.onTouchEventActionFinish(this);
	}

	public float getProgress()
	{
		return mPercent;
	}

	public void setProgress(float percent)
	{
		// LogUtil.d(TAG, "setProgress: " + percent + "; " + mPercent + "; " + mStartTrackingTouch);
		if (mStartTrackingTouch || mPercent == percent)
		{
			return;
		}
		setProgressImpl(MotionEvent.ACTION_UP, percent);
	}

	private void setProgressImpl(final int touchAction, float percent)
	{
		if (percent < 0)
		{
			percent = 0;
		}
		else if (percent > 100)
		{
			percent = 100;
		}
		// if (mPercent != percent) {
		mPercent = percent;
		// postInvalidate();
		post(new Runnable()
		{
			@Override
			public void run()
			{
				ensureProgressBackgroudView();
				ensureProgressView();
				ensureSliderView();
				if (mOrientation == VERTICAL)
				{
					int maxHeight = mProgressBackgroundView.getMeasuredHeight()
							- mProgressBackgroundView.getPaddingTop() - mProgressBackgroundView.getPaddingBottom();
					if (maxHeight <= 0)
					{
						return;
					}

					int ih = mSliderView.getMeasuredHeight();
					int bottom = (int)((maxHeight - ih) * mPercent / 100);
					MarginLayoutParams marginParams = (MarginLayoutParams)mSliderView.getLayoutParams();
					marginParams.bottomMargin = bottom;
					mSliderView.setLayoutParams(marginParams);

					if (mSliderVisible)
					{
						ViewGroup.LayoutParams params = mProgressView.getLayoutParams();
						params.height = bottom;
						mProgressView.setLayoutParams(params);
					}
					else
					{
						int progressHeight = (int)(mPercent * maxHeight / 100);
						ViewGroup.LayoutParams params = mProgressView.getLayoutParams();
						params.height = progressHeight;
						mProgressView.setLayoutParams(params);
					}
				}
				else
				{
					int maxWidth = mProgressBackgroundView.getMeasuredWidth()
							- mProgressBackgroundView.getPaddingLeft() - mProgressBackgroundView.getPaddingRight();
					if (maxWidth <= 0)
					{
						return;
					}

					int iw = mSliderView.getMeasuredWidth();
					int left = (int)((maxWidth - iw) * mPercent / 100);
					MarginLayoutParams marginParams = (MarginLayoutParams)mSliderView.getLayoutParams();
					marginParams.leftMargin = left;
					mSliderView.setLayoutParams(marginParams);

					if (mSliderVisible)
					{
						ViewGroup.LayoutParams params = mProgressView.getLayoutParams();
						params.width = left;
						mProgressView.setLayoutParams(params);
					}
					else
					{
						int progressWidth = (int)(mPercent * maxWidth / 100);
						ViewGroup.LayoutParams params = mProgressView.getLayoutParams();
						params.width = progressWidth;
						mProgressView.setLayoutParams(params);
					}
				}
				if (mProgressVisible)
				{
					mProgressView.setVisibility(View.VISIBLE);
				}
				else
				{
					mProgressView.setVisibility(View.INVISIBLE);
				}
				if (mSliderVisible)
				{
					mSliderView.setVisibility(View.VISIBLE);
				}
				else
				{
					mSliderView.setVisibility(View.INVISIBLE);
				}
				if (touchAction == MotionEvent.ACTION_UP)
					return;
				notifyProgressChanged(mPercent);
			}
		});
		// }
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	// {
	// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// }

	// @Override
	// protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	// {
	// super.onLayout(changed, left, top, right, bottom);
	// }

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		if (mHasInitialized)
			setProgressImpl(MotionEvent.ACTION_UP, mPercent);
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev)
	// {
	// return super.dispatchTouchEvent(ev);
	// }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// LogUtil.v(TAG, "onTouchEvent: " + event);
		switch (mChangeProgressMode)
		{
			case SLIDE:
				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						if (mStartTrackingTouch)
						{
							mPointF.set(event.getX(), event.getY());
							return true;
						}
						break;
					case MotionEvent.ACTION_MOVE:
						if (mStartTrackingTouch)
						{
							trackSliderEvent(event);
							mPointF.set(event.getX(), event.getY());
						}
						return true;
					case MotionEvent.ACTION_UP:
						onTouchEventActionFinish();
						invalidate();
						break;
					case MotionEvent.ACTION_CANCEL:
						onTouchEventActionFinish();
						invalidate();
						return true;
				}
				mPointF.set(event.getX(), event.getY());
				return false;
			case TOUCH:
				switch (event.getAction())
				{
					case MotionEvent.ACTION_DOWN:
						onTouchEventActionStart();
						trackTouchEvent(event);
						return true;
					case MotionEvent.ACTION_MOVE:
						trackTouchEvent(event);
						return true;
					case MotionEvent.ACTION_UP:
						trackTouchEvent(event);
						onTouchEventActionFinish();
						invalidate();
						return true;
					case MotionEvent.ACTION_CANCEL:
						onTouchEventActionFinish();
						invalidate();
						return true;
				}
				return false;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}

	public void setTouchPointF(float x, float y)
	{
		mPointF.set(x, y);
	}

	public void onTouchEventActionStart()
	{
		mStartTrackingTouch = true;
		setPressed(true);
		notifyTouchEventActionStart();
	}

	public void onTouchEventActionMove(MotionEvent event)
	{
		switch (mChangeProgressMode)
		{
			case SLIDE:
				switch (event.getAction())
				{
				// case MotionEvent.ACTION_DOWN:
				// mStartTrackingTouch = true;
				// ProgressBar.this.setPressed(true);
				// break;
					case MotionEvent.ACTION_MOVE:
						if (mStartTrackingTouch)
							trackSliderEvent(event);
						break;
				}
				mPointF.set(event.getX(), event.getY());
				break;
			default:
				break;
		}
	}

	public void onTouchEventActionFinish()
	{
		mStartTrackingTouch = false;
		setPressed(false);
		notifyTouchEventActionFinish();
	}

	private void trackSliderEvent(MotionEvent event)
	{
		if (mOrientation == VERTICAL)
		{
			float moveY = mPointF.y - event.getY();
			if (moveY == 0)
				return;
			int maxHeight = mProgressBackgroundView.getMeasuredHeight() - mProgressBackgroundView.getPaddingTop()
					- mProgressBackgroundView.getPaddingBottom();
			if (maxHeight <= 0)
			{
				return;
			}
			float movePercent = moveY * 100 / (float)maxHeight;
			setProgressImpl(event.getAction(), mPercent + movePercent);
		}
		else
		{
			float moveX = event.getX() - mPointF.x;
			if (moveX == 0)
				return;
			int maxWidth = mProgressBackgroundView.getMeasuredWidth() - mProgressBackgroundView.getPaddingLeft()
					- mProgressBackgroundView.getPaddingRight();
			if (maxWidth <= 0)
			{
				return;
			}
			float movePercent = moveX * 100 / (float)maxWidth;
			setProgressImpl(event.getAction(), mPercent + movePercent);
		}
	}

	private void trackTouchEvent(MotionEvent event)
	{
		if (mOrientation == VERTICAL)
		{
			int maxHeight = mProgressBackgroundView.getMeasuredHeight() - mProgressBackgroundView.getPaddingTop()
					- mProgressBackgroundView.getPaddingBottom();
			if (maxHeight <= 0)
			{
				return;
			}
			float y = event.getY() - mProgressBackgroundView.getBottom() - mProgressBackgroundView.getPaddingBottom();
			y = (maxHeight - event.getY());
			float percent = y * 100f / (float)maxHeight;

			// if (mPercent != percent)
			// {
			setProgressImpl(event.getAction(), percent);
			// }
		}
		else
		{
			int maxWidth = mProgressBackgroundView.getMeasuredWidth() - mProgressBackgroundView.getPaddingLeft()
					- mProgressBackgroundView.getPaddingRight();
			if (maxWidth <= 0)
			{
				return;
			}
			float x = event.getX() - mProgressBackgroundView.getLeft() - mProgressBackgroundView.getPaddingLeft();
			float percent = x * 100f / (float)maxWidth;

			// if (mPercent != percent)
			// {
			setProgressImpl(event.getAction(), percent);
			// }
		}
	}

	// @Override
	// protected void dispatchDraw(Canvas canvas) {
	// // super.dispatchDraw(canvas);
	// int count = canvas.save();
	// int left, top, right, bottom;
	// int width = getWidth();
	// int height = getHeight();
	// left = 0;
	// right = left + width;
	// top = (height - mProgressBackgroundDrawable.getIntrinsicHeight()) / 2;
	// bottom = top + mProgressBackgroundDrawable.getIntrinsicHeight();
	//
	// mProgressBackgroundDrawable.setBounds(left, top, right, bottom);
	// mProgressBackgroundDrawable.draw(canvas);
	//
	// width -= mProgressDrawable.getIntrinsicWidth();
	// left = (int) (width * mPercent / 100.0);
	// if (left > width)
	// left = width - mProgressDrawable.getIntrinsicWidth();
	// else if (left < 0)
	// left = 0;
	// right = left + mProgressDrawable.getIntrinsicWidth();
	// top = 0;
	// bottom = top + mProgressDrawable.getIntrinsicHeight();
	// mProgressDrawable.setBounds(left, top, right, bottom);
	// mProgressDrawable.draw(canvas);
	// canvas.restoreToCount(count);
	// }

	public interface OnProgressChangedListener
	{
		public void onProgressChanged(ProgressBar progressBar, float percent);
	}

	public interface OnTouchEventActionListener
	{
		public void onTouchEventActionStart(ProgressBar progressBar);

		public void onTouchEventActionMove(ProgressBar progressBar);

		public void onTouchEventActionFinish(ProgressBar progressBar);
	}
}
