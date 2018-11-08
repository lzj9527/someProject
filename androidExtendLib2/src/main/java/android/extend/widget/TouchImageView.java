package android.extend.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.extend.util.LogUtil;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class TouchImageView extends ExtendImageView
{
	public interface OnDoubleClickListener
	{
		public void onDoubleClick(View v);
	}

	public enum GestureMode
	{
		/**
		 * 单张图片模式
		 * */
		SINGLE,
		/**
		 * @deprecated Use {@link #VIEWPAGER} instead.
		 */
		VIEWPAGER_TAOBAO,
		/**
		 * ViewPager模式，必须与ViewPager一起使用
		 * */
		VIEWPAGER,
	}

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int MULTI_POINTER = 2;

	private boolean mMoveEnabled = true;
	private boolean mScaleEnabled = true;
	private boolean mRotateEnabled = false;
	private boolean mDoubleTapResetEnabled = true;
	private boolean mOnlyMoveOutOfViewBounds = false;
	private GestureDetector mGestureDetector;
	private OnClickListener mClickListener;
	private OnDoubleClickListener mDoubleClickListener;
	private OnLongClickListener mLongClickListener;

	private GestureMode mGestureMode = GestureMode.SINGLE;

	// private boolean mScaleLimited = true;
	// private float mScaleMinimumRadio = 0.5f;
	// private float mScaleMaximumRadio = 2f;

	private int pointerMode = NONE;

	private Matrix sourceMatrix = new Matrix();
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	// private final float[] matrixValues = new float[9];
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float oldRotation = 0;

	public TouchImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public TouchImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public TouchImageView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()
		{
			@Override
			public boolean onDown(MotionEvent e)
			{
				// LogUtil.v(TAG, "onDown: " + e);
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e)
			{
				// LogUtil.v(TAG, "onShowPress: " + e);
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e)
			{
				LogUtil.v(TAG, "onSingleTapUp...");
				if (mClickListener != null)
					mClickListener.onClick(TouchImageView.this);
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
			{
				// LogUtil.v(TAG, "onScroll: ");
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e)
			{
				LogUtil.v(TAG, "onLongPress...");
				if (mLongClickListener != null)
					mLongClickListener.onLongClick(TouchImageView.this);
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
			{
				// LogUtil.v(TAG, "onFling: ");
				return false;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e)
			{
				// LogUtil.v(TAG, "onSingleTapConfirmed: " + e);
				return false;
			}

			@Override
			public boolean onDoubleTap(MotionEvent e)
			{
				LogUtil.v(TAG, "onDoubleTap...");
				if (mDoubleTapResetEnabled)
					resetMatrixToSource();
				if (mDoubleClickListener != null)
					mDoubleClickListener.onDoubleClick(TouchImageView.this);
				return true;
			}

			@Override
			public boolean onDoubleTapEvent(MotionEvent e)
			{
				// LogUtil.v(TAG, "onDoubleTapEvent: ");
				return false;
			}
		});
		mGestureDetector.setIsLongpressEnabled(true);
	}

	// @Override
	// public void setImageBitmap(Bitmap bitmap)
	// {
	// super.setImageBitmap(bitmap);
	// }

	@Override
	public void setImageDrawable(Drawable drawable)
	{
		// if (drawable != null)
		// LogUtil.v(TAG, "setImageDrawable: " + drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight());
		super.setImageDrawable(drawable);
		sourceMatrix.set(getImageMatrix());
		LogUtil.v(TAG, "getImageMatrix: " + sourceMatrix);
		LogUtil.v(TAG, "getDrawable: " + getDrawable().getIntrinsicWidth() + "x" + getDrawable().getIntrinsicHeight());
	}

	// @Override
	// public void setImageResource(int resId)
	// {
	// super.setImageResource(resId);
	// LogUtil.v(TAG, "getImageMatrix: " + getImageMatrix().toString());
	// }
	//
	// @Override
	// public void setImageURI(Uri uri)
	// {
	// super.setImageURI(uri);
	// LogUtil.v(TAG, "getImageMatrix: " + getImageMatrix().toString());
	// }

	public void resetMatrixToSource()
	{
		if (sourceMatrix != null)
			setImageMatrix(sourceMatrix);
	}

	@SuppressWarnings("incomplete-switch")
	public void setGestureMode(GestureMode mode)
	{
		if (mode == null)
			return;
		mGestureMode = mode;
		switch (mGestureMode)
		{
			case VIEWPAGER_TAOBAO:
				mGestureMode = GestureMode.VIEWPAGER;
				break;
		}
		switch (mGestureMode)
		{
			case SINGLE:
				setMoveEnabled(true);
				setScaleEnabled(true);
				setRotateEnabled(false);
				setDoubleTapResetEnabled(true);
				break;
			case VIEWPAGER:
				setMoveEnabled(true);
				setScaleEnabled(true);
				setRotateEnabled(false);
				setDoubleTapResetEnabled(false);
				break;
			default:
				break;
		}
	}

	public void setMoveEnabled(boolean enabled)
	{
		mMoveEnabled = enabled;
	}

	public void setScaleEnabled(boolean enabled)
	{
		mScaleEnabled = enabled;
	}

	// public void setScaleLimited(boolean limited)
	// {
	// mScaleLimited = limited;
	// }
	//
	// public void setScaleLimitRadio(float minRadio, float maxRadio)
	// {
	// mScaleMinimumRadio = minRadio;
	// mScaleMaximumRadio = maxRadio;
	// }

	public void setRotateEnabled(boolean enabled)
	{
		mRotateEnabled = enabled;
	}

	public void setDoubleTapResetEnabled(boolean enabled)
	{
		mDoubleTapResetEnabled = enabled;
	}

	public void setOnlyMoveOutOfViewBounds(boolean onlyMoveOutOfViewBounds)
	{
		mOnlyMoveOutOfViewBounds = onlyMoveOutOfViewBounds;
	}

	@Override
	public void setOnClickListener(OnClickListener listener)
	{
		mClickListener = listener;
	}

	// public void setDoubleClickable(boolean clickable)
	// {
	// mDoubleClickable = clickable;
	// }

	public void setOnDoubleClickListener(OnDoubleClickListener listener)
	{
		mDoubleClickListener = listener;
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener listener)
	{
		mLongClickListener = listener;
	}

	// public void setLongPressEnabled(boolean enabled)
	// {
	// mLongPressEnabled = enabled;
	// mGestureDetector.setIsLongpressEnabled(enabled);
	// }

	// 触碰两点间距离
	private float spacing(MotionEvent event)
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	// 取手势中心点
	private void midPoint(PointF point, MotionEvent event)
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 取旋转角度
	private float rotation(MotionEvent event)
	{
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float)Math.toDegrees(radians);
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent event)
	// {
	// LogUtil.d(TAG, "dispatchTouchEvent: " + event);
	// return super.dispatchTouchEvent(event);
	// }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// LogUtil.d(TAG, "onTouchEvent: " + event);
		boolean result = mGestureDetector.onTouchEvent(event);
		if (getDrawable() == null)
			return super.onTouchEvent(event);
		// boolean result = false;
		boolean changed = false;
		switch (event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
				pointerMode = DRAG;
				start.set(event.getX(), event.getY());
				savedMatrix.set(getImageMatrix());
				return true;
			case MotionEvent.ACTION_POINTER_DOWN:
				pointerMode = MULTI_POINTER;
				oldDist = spacing(event);
				oldRotation = rotation(event);
				savedMatrix.set(getImageMatrix());
				midPoint(mid, event);
				return true;
			case MotionEvent.ACTION_MOVE:
				if (pointerMode == MULTI_POINTER && event.getPointerCount() == 2)
				{
					float rotation = rotation(event) - oldRotation;
					float newDist = spacing(event);
					float scale = newDist / oldDist;
					// LogUtil.v(TAG, "rotation=" + rotation + "; scale=" + scale);
					matrix.set(savedMatrix);
					if (mRotateEnabled && Math.abs(rotation) > 1f)
					{
						matrix.postRotate(rotation, mid.x, mid.y);// 旋轉
						changed = true;
						result = true;
					}
					if (mScaleEnabled && scale != 1f)
					{
						if (mOnlyMoveOutOfViewBounds)
						{
							// float px = mid.x;
							// float py = mid.y;
							// if (width <= getWidth())
							float px = getWidth() / 2.0f;
							// if (height <= getHeight())
							float py = getHeight() / 2.0f;
							matrix.postScale(scale, scale, px, py);// 縮放
							if (scale < 1f)
							{
								float[] values = new float[9];
								matrix.getValues(values);
								float scaleX = values[Matrix.MSCALE_X];
								float scaleY = values[Matrix.MSCALE_Y];
								float transX = values[Matrix.MTRANS_X];
								float transY = values[Matrix.MTRANS_Y];
								PointF centerTrans = computeCenterTrans(scaleX, scaleY);
								// LogUtil.d(TAG, "scale=" + scale);
								// LogUtil.v(TAG, "get matrix value: scaleX=" + scaleX + ", scaleY=" + scaleY
								// + ", transX=" + transX + ", transY=" + transY + ", centerTrans=" + centerTrans);
								sourceMatrix.getValues(values);
								float srcScaleX = values[Matrix.MSCALE_X];
								float srcScaleY = values[Matrix.MSCALE_Y];
								// float srcTransX = values[Matrix.MTRANS_X];
								// float srcTransY = values[Matrix.MTRANS_Y];
								// LogUtil.v(TAG, "get srcmatrix value: srcScaleX=" + srcScaleX + ", srcScaleY="
								// + srcScaleY);
								float dx = 0f;
								float dy = 0f;
								if (scaleX > srcScaleX)
								{
									float diff = scaleX - srcScaleX;
									if (diff < 1f)
									{
										dx = (centerTrans.x - transX) * (1 - diff);
									}
								}
								else
								{
									dx = (centerTrans.x - transX);
								}
								if (scaleY > srcScaleY)
								{
									float diff = scaleY - srcScaleY;
									if (diff < 1f)
									{
										dy = (centerTrans.y - transY) * (1 - diff);
									}
								}
								else
								{
									dy = (centerTrans.y - transY);
								}
								// LogUtil.d(TAG, "compute dx=" + dx + ", dy=" + dy);
								if (dx != 0f || dy != 0f)
									matrix.postTranslate(dx, dy);
							}
							checkMatrixBorder(matrix);
						}
						else
						{
							matrix.postScale(scale, scale, mid.x, mid.y);// 縮放
							checkMatrixBorder(matrix);
						}
						changed = true;
						result = true;
					}
				}
				else if (mMoveEnabled && pointerMode == DRAG)
				{
					float deltaX = event.getX() - start.x;
					float deltaY = event.getY() - start.y;
					if (deltaX != 0f || deltaY != 0f)
					{
						matrix.set(savedMatrix);
						float[] values = new float[9];
						matrix.getValues(values);
						float scaleX = values[Matrix.MSCALE_X];
						float scaleY = values[Matrix.MSCALE_Y];
						// LogUtil.d(TAG, "get matrix scale: " + scaleX + ", " + scaleY);
						float width = getDrawable().getIntrinsicWidth() * scaleX;
						float height = getDrawable().getIntrinsicHeight() * scaleY;
						float left = values[Matrix.MTRANS_X];
						float top = values[Matrix.MTRANS_Y];
						float right = left + width;
						float bottom = top + height;
						// LogUtil.d(TAG, "compute matrix border: " + left + ", " + top + ", " + right + ", " + bottom);
						float moveX = deltaX;
						float moveY = deltaY;
						if (width <= getWidth())
						{
							if (!mOnlyMoveOutOfViewBounds)
							{
								if (left + deltaX < 0)
									moveX = -left;
								else if (right + deltaX > getWidth())
									moveX = getWidth() - right;
							}
							else
							{
								moveX = 0;
							}
						}
						else
						{
							if (left + deltaX > 0)
								moveX = -left;
							else if (right + deltaX < getWidth())
								moveX = getWidth() - right;
						}
						if (height <= getHeight())
						{
							if (!mOnlyMoveOutOfViewBounds)
							{
								if (top + deltaY < 0)
									moveY = -top;
								else if (bottom + deltaY > getHeight())
									moveY = getHeight() - bottom;
							}
							else
							{
								moveY = 0;
							}
						}
						else
						{
							if (top + deltaY > 0)
								moveY = -top;
							else if (bottom + deltaY < getHeight())
								moveY = getHeight() - bottom;
						}
						if (moveX != 0 || moveY != 0)
						{
							// LogUtil.v(TAG, "compute matrix postTranslate: " + moveX + ", " + moveY);
							matrix.postTranslate(moveX, moveY);// 平移
							changed = true;
							if (mGestureMode == GestureMode.VIEWPAGER)
							{
								if (moveX != 0)
									result = true;
							}
							else
								result = true;
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				pointerMode = NONE;
				return true;
		}

		if (changed)
		{
			if (getScaleType() != ScaleType.MATRIX)
				setScaleType(ScaleType.MATRIX);
			setImageMatrix(matrix);
			if (result)
				getParent().requestDisallowInterceptTouchEvent(true);
			// LogUtil.v(TAG, "getImageMatrix: " + getImageMatrix().toString());
			// LogUtil.v(TAG, "onTouchEvent result: " + result);
			// return result;
		}
		LogUtil.v(TAG, "onTouchEvent result: " + result);
		return result;
	}

	private PointF getMatrixScale(Matrix matrix)
	{
		if (matrix != null)
		{
			float[] values = new float[9];
			matrix.getValues(values);
			float scaleX = values[Matrix.MSCALE_X];
			float scaleY = values[Matrix.MSCALE_Y];
			return new PointF(scaleX, scaleY);
		}
		return null;
	}

	private PointF getMatrixTrans(Matrix matrix)
	{
		if (matrix != null)
		{
			float[] values = new float[9];
			matrix.getValues(values);
			float transX = values[Matrix.MTRANS_X];
			float transY = values[Matrix.MTRANS_Y];
			return new PointF(transX, transY);
		}
		return null;
	}

	private PointF computeCenterTrans(float scaleX, float scaleY)
	{
		float width = getDrawable().getIntrinsicWidth() * scaleX;
		float height = getDrawable().getIntrinsicHeight() * scaleY;
		float transX = (getWidth() - width) / 2f;
		float transY = (getHeight() - height) / 2f;
		return new PointF(transX, transY);
	}

	private void checkMatrixBorder(Matrix matrix)
	{
		float[] values = new float[9];
		matrix.getValues(values);
		float scaleX = values[Matrix.MSCALE_X];
		float scaleY = values[Matrix.MSCALE_Y];
		float width = getDrawable().getIntrinsicWidth() * scaleX;
		float height = getDrawable().getIntrinsicHeight() * scaleY;
		float left = values[Matrix.MTRANS_X];
		float top = values[Matrix.MTRANS_Y];
		float right = left + width;
		float bottom = top + height;
		// LogUtil.d(TAG, "checkMatrixBorder: border= " + left + ", " + top + ", " + right + ", " + bottom);
		float deltaX = 0;
		float deltaY = 0;
		// boolean xMoveOutOfViewBounds = false;
		// boolean yMoveOutOfViewBounds = false;
		if (width <= getWidth())
		{
			// if (mOnlyMoveOutOfViewBounds)
			// {
			// deltaX = ((float)getWidth() - (float)width) / 2.0f;
			// xMoveOutOfViewBounds = true;
			// }
			// else
			// {
			if (left < 0)
				deltaX = -left;
			else if (right > getWidth())
				deltaX = getWidth() - right;
			// }
		}
		else
		{
			if (left > 0)
				deltaX = -left;
			else if (right < getWidth())
				deltaX = getWidth() - right;
		}
		if (height <= getHeight())
		{
			// if (mOnlyMoveOutOfViewBounds)
			// {
			// deltaY = ((float)getHeight() - (float)height) / 2.0f;
			// yMoveOutOfViewBounds = true;
			// }
			// else
			// {
			if (top < 0)
				deltaY = -top;
			else if (bottom > getHeight())
				deltaY = getHeight() - bottom;
			// }
		}
		else
		{
			if (top > 0)
				deltaY = -top;
			else if (bottom < getHeight())
				deltaY = getHeight() - bottom;
		}
		// LogUtil.v(TAG, "checkMatrixBorder: delta= " + deltaX + ", " + deltaY + ", " + xMoveOutOfViewBounds + ", "
		// + yMoveOutOfViewBounds);
		if (deltaX != 0f || deltaY != 0f)
		{
			matrix.postTranslate(deltaX, deltaY);
			// if (xMoveOutOfViewBounds && yMoveOutOfViewBounds)
			// {
			// matrix.getValues(values);
			// if (xMoveOutOfViewBounds)
			// values[Matrix.MTRANS_X] = deltaX;
			// if (yMoveOutOfViewBounds)
			// values[Matrix.MTRANS_Y] = deltaY;
			// matrix.setValues(values);
			// }
		}
	}

	// private PointF getMinimumScale()
	// {
	// if (sourceMatrix != null)
	// {
	// float[] values = new float[9];
	// sourceMatrix.getValues(values);
	// float scaleX = values[Matrix.MSCALE_X];
	// float scaleY = values[Matrix.MSCALE_Y];
	// return new PointF(scaleX * mScaleMinimumRadio, scaleY * mScaleMinimumRadio);
	// }
	// return null;
	// }
	//
	// private PointF getMaximumScale()
	// {
	// if (sourceMatrix != null)
	// {
	// float[] values = new float[9];
	// sourceMatrix.getValues(values);
	// float scaleX = values[Matrix.MSCALE_X];
	// float scaleY = values[Matrix.MSCALE_Y];
	// return new PointF(scaleX * mScaleMaximumRadio, scaleY * mScaleMaximumRadio);
	// }
	// return null;
	// }

	// private void computeMatrixBorder(Matrix matrix, float[] borders)
	// {
	// float[] values = new float[9];
	// matrix.getValues(values);
	// float scaleX = values[Matrix.MSCALE_X];
	// float scaleY = values[Matrix.MSCALE_Y];
	// float width = getDrawable().getIntrinsicWidth() * scaleX;
	// float height = getDrawable().getIntrinsicHeight() * scaleY;
	// float left = values[Matrix.MTRANS_X];
	// float top = values[Matrix.MTRANS_Y];
	// float right = left + width;
	// float bottom = top + height;
	// LogUtil.v(TAG, "border: " + left + ", " + top + ", " + right + ", " + bottom);
	// }

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas)
	{
		// 去除锯齿毛边
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		super.onDraw(canvas);
	}
}
