package android.extend.widget.showcase;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.extend.util.AndroidUtils;
import android.extend.util.BitmapUtils;
import android.extend.util.LogUtil;
import android.extend.util.ViewTools;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

public class ShowcaseView extends FrameLayout
{
	public final String TAG = getClass().getSimpleName();

	public interface OnShowcaseListener
	{
		public void onBuild(ShowcaseView showcaseView);

		public void onShow(ShowcaseView showcaseView);

		public void onHide(ShowcaseView showcaseView);

		public void onDismiss(ShowcaseView showcaseView);
	}

	private int mViewX;
	private int mViewY;
	private int mViewWidth;
	private int mViewHeight;
	private int mMaskColor = 0xb0000000;
	// private List<IShowcaseDrawer> mDrawerList = new ArrayList<IShowcaseDrawer>();
	private Bitmap mShowcaseBitmap;
	private Activity mActivity;
	private boolean mChanged;
	private boolean mBuildCalled = false;
	private boolean mShowCalled = false;
	private boolean mLayoutCalled = false;
	private boolean mDismissCalled = false;
	private View mDismissView;
	private List<OnShowcaseListener> mShowcaseListeners = new ArrayList<OnShowcaseListener>();

	private class Target
	{
		RectF targetBounds;
		View targetView;
		Class<? extends IShowcaseDrawer> drawerClass;
		int padding;
		IShowcaseDrawer drawer;

		public Target(RectF bounds, Class<? extends IShowcaseDrawer> drawerClass, int padding)
		{
			this.targetBounds = bounds;
			this.drawerClass = drawerClass;
			this.padding = padding;
		}

		public Target(Rect bounds, Class<? extends IShowcaseDrawer> drawerClass, int padding)
		{
			this.targetBounds = new RectF(bounds);
			this.drawerClass = drawerClass;
			this.padding = padding;
		}

		public Target(View view, Class<? extends IShowcaseDrawer> drawerClass, int padding)
		{
			this.targetView = view;
			this.drawerClass = drawerClass;
			this.padding = padding;
		}

		public IShowcaseDrawer makeDrawer()
		{
			float x = 0, y = 0, width = 0, height = 0;
			if (targetView != null)
			{
				int[] location = new int[2];
				targetView.getLocationOnScreen(location);
				x = location[0] - mViewX;
				y = location[1] - mViewY;
				width = targetView.getWidth();
				height = targetView.getHeight();
			}
			else if (targetBounds != null)
			{
				x = targetBounds.left;
				y = targetBounds.top;
				width = targetBounds.width();
				height = targetBounds.height();
			}
			if (drawerClass == CircleShowcaseDrawer.class)
			{
				float xRadius = width / 2f;
				float yRadius = height / 2f;
				float cx = x + xRadius;
				float cy = y + yRadius;
				float radius = Math.max(xRadius, yRadius) + padding;
				drawer = new CircleShowcaseDrawer(cx, cy, radius);
			}
			else if (drawerClass == RectangleShowcaseDrawer.class)
			{
				x = x - padding;
				y = y - padding;
				width = width + padding * 2;
				height = height + padding * 2;
				drawer = new RectangleShowcaseDrawer(x, y, width, height);
			}
			else if (drawerClass == OvalShowcaseDrawer.class)
			{
				float xRadius = width / 2f;
				float yRadius = height / 2f;
				float cx = x + xRadius;
				float cy = y + yRadius;
				xRadius = xRadius + padding;
				yRadius = yRadius + padding;
				drawer = new OvalShowcaseDrawer(cx, cy, xRadius, yRadius);
			}
			else if (drawerClass == RoundRectShowcaseDrawer.class)
			{
				float xRadius = width / 2f;
				float yRadius = height / 2f;
				x = x - padding;
				y = y - padding;
				width = width + padding * 2;
				height = height + padding * 2;
				drawer = new RoundRectShowcaseDrawer(x, y, width, height, xRadius, yRadius);
			}
			else
			{
				throw new UnsupportedClassVersionError();
			}
			return drawer;
		}
	}

	private List<Target> mTargetList = new ArrayList<Target>();

	private OnGlobalLayoutListener mLayoutListener = new OnGlobalLayoutListener()
	{

		@Override
		public void onGlobalLayout()
		{
			mLayoutCalled = true;
			int[] location = new int[2];
			getLocationOnScreen(location);
			if (mViewX != location[0] || mViewY != location[1] || mViewWidth != getWidth()
					|| mViewHeight != getHeight())
			{
				mChanged = true;
				mViewX = location[0];
				mViewY = location[1];
				mViewWidth = getWidth();
				mViewHeight = getHeight();
				LogUtil.v(TAG, "onGlobalLayout: mViewX=" + mViewX + "; mViewY=" + mViewY + "; mViewWidth=" + mViewWidth
						+ "; mViewHeight=" + mViewHeight);
				buildShowcaseBitmap();
			}
		}
	};

	public ShowcaseView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public ShowcaseView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public ShowcaseView(Context context)
	{
		super(context);
		init();
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	private void init()
	{
		setVisibility(View.INVISIBLE);
		getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
	}

	private void recycleShowcaseBitmap()
	{
		setBackground(null);
		if (mShowcaseBitmap != null && !mShowcaseBitmap.isRecycled())
		{
			mShowcaseBitmap.recycle();
			mShowcaseBitmap = null;
		}
	}

	private void buildShowcaseBitmap()
	{
		if (!mChanged || !mBuildCalled || !mLayoutCalled)
			return;
		LogUtil.v(TAG, "buildShowcaseBitmap...");
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				recycleShowcaseBitmap();
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						List<IShowcaseDrawer> list = new ArrayList<IShowcaseDrawer>();
						for (Target target : mTargetList)
						{
							list.add(target.makeDrawer());
						}
						mShowcaseBitmap = BitmapUtils.createShowcaseBitmap(mViewWidth, mViewHeight, mMaskColor, list);
						mChanged = false;
						notifyOnBuild();
						AndroidUtils.MainHandler.post(new Runnable()
						{
							@Override
							public void run()
							{
								setBackground(new BitmapDrawable(getResources(), mShowcaseBitmap));
								if (mShowCalled)
								{
									notifyOnShow();
									setVisibility(View.VISIBLE);
								}
							}
						});
					}
				}).start();
			}
		});
	}

	public ShowcaseView addOnShowcaseListener(OnShowcaseListener listener)
	{
		mShowcaseListeners.add(listener);
		return this;
	}

	public ShowcaseView removeOnShowcaseListener(OnShowcaseListener listener)
	{
		mShowcaseListeners.remove(listener);
		return this;
	}

	private void notifyOnBuild()
	{
		synchronized (this)
		{
			for (OnShowcaseListener listener : mShowcaseListeners)
			{
				listener.onBuild(this);
			}
		}
	}

	private void notifyOnShow()
	{
		synchronized (this)
		{
			for (OnShowcaseListener listener : mShowcaseListeners)
			{
				listener.onShow(this);
			}
		}
	}

	private void notifyOnHide()
	{
		synchronized (this)
		{
			for (OnShowcaseListener listener : mShowcaseListeners)
			{
				listener.onHide(this);
			}
		}
	}

	private void notifyOnDismiss()
	{
		synchronized (this)
		{
			for (OnShowcaseListener listener : mShowcaseListeners)
			{
				listener.onDismiss(this);
			}
		}
	}

	public ShowcaseView setMaskColor(int color)
	{
		mMaskColor = color;
		mChanged = true;
		return this;
	}

	// public void addShowcaseDrawer(IShowcaseDrawer drawer)
	// {
	// mDrawerList.add(drawer);
	// mChanged = true;
	// }

	public ShowcaseView addShowcaseTarget(View targetView, Class<? extends IShowcaseDrawer> drawerClass, int padding)
	{
		mTargetList.add(new Target(targetView, drawerClass, padding));
		mChanged = true;
		return this;
	}

	public ShowcaseView addShowcaseTarget(RectF targetBounds, Class<? extends IShowcaseDrawer> drawerClass, int padding)
	{
		mTargetList.add(new Target(targetBounds, drawerClass, padding));
		mChanged = true;
		return this;
	}

	public ShowcaseView addShowcaseTarget(Rect targetBounds, Class<? extends IShowcaseDrawer> drawerClass, int padding)
	{
		mTargetList.add(new Target(targetBounds, drawerClass, padding));
		mChanged = true;
		return this;
	}

	public IShowcaseDrawer getShowcaseDrawer(View target)
	{
		if (target == null)
			return null;
		for (Target viewTarget : mTargetList)
		{
			if (viewTarget.targetView == target)
			{
				return viewTarget.drawer;
			}
		}
		return null;
	}

	public IShowcaseDrawer getShowcaseDrawer(int index)
	{
		if (index < 0 || index >= mTargetList.size())
			return null;
		return mTargetList.get(index).drawer;
	}

	public ShowcaseView build()
	{
		LogUtil.d(TAG, "build...");
		mBuildCalled = true;
		buildShowcaseBitmap();
		return this;
	}

	public ShowcaseView show(Activity activity)
	{
		LogUtil.d(TAG, "show...");
		mShowCalled = true;
		mActivity = activity;
		if (!mBuildCalled)
			build();
		ViewTools.getActivityDecorView(activity).addView(this,
				new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		if (mLayoutCalled)
		{
			notifyOnShow();
			setVisibility(View.VISIBLE);
		}
		return this;
	}

	public ShowcaseView hide()
	{
		LogUtil.d(TAG, "hide...");
		mShowCalled = false;
		setVisibility(View.INVISIBLE);
		notifyOnHide();
		return this;
	}

	public ShowcaseView dismiss()
	{
		LogUtil.d(TAG, "dismiss...");
		mDismissCalled = true;
		mBuildCalled = false;
		mLayoutCalled = false;
		mChanged = false;
		getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
		hide();
		ViewTools.getActivityDecorView(mActivity).removeView(this);
		removeAllViews();
		// mDrawerList.clear();
		mTargetList.clear();
		recycleShowcaseBitmap();
		notifyOnDismiss();
		mShowcaseListeners.clear();
		return this;
	}

	public ShowcaseView setDismissView(View view)
	{
		mDismissView = view;
		if (view != null)
			view.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dismiss();
				}
			});
		return this;
	}

	public View getDismissView()
	{
		return mDismissView;
	}

	@Override
	protected void onDetachedFromWindow()
	{
		LogUtil.d(TAG, "onDetachedFromWindow");
		super.onDetachedFromWindow();
		if (!mDismissCalled)
			dismiss();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		super.dispatchTouchEvent(ev);
		return true;
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
			e.printStackTrace();
		}
	}

	@Override
	public void draw(Canvas canvas)
	{
		try
		{
			super.draw(canvas);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		try
		{
			super.onDraw(canvas);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
