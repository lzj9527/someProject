package android.extend.widget;

import android.content.Context;
import android.extend.util.ResourceUtil;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

public class ExtendViewFlipper extends ViewFlipper
{
	public interface OnFlipperListener
	{
		void onFlipperStarted(ViewFlipper viewFlipper, boolean next, int inChild, int outChild);

		void onFlipperCompleted(ViewFlipper viewFlipper, boolean next, int inChild, int outChild);

		void onBackToPrevViewCompleted(ViewFlipper viewFlipper, int inChild, int outChild);
	}

	public static final String TAG = ExtendViewFlipper.class.getSimpleName();

	private Animation mNextInAnim;
	private Animation mNextOutAnim;
	private Animation mPrevInAnim;
	private Animation mPrevOutAnim;
	private OnFlipperListener mFlipperListener;
	private int mInChild;
	private int mOutChild;
	// private int mDisplayedChild;
	private boolean mNext = false;
	private boolean mDoFlipperStart = false;
	private boolean mDoBackToPrevStart = false;
	private boolean mListenOutAnim = true;

	private AnimationListener mInnerInAnimationListener = new AnimationListener()
	{
		@Override
		public void onAnimationStart(Animation animation)
		{
			// LogUtil.v(TAG, "inAnimationStart: " + animation);
			if (mInAnimationListener != null)
				mInAnimationListener.onAnimationStart(animation);
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{
			// LogUtil.v(TAG, "inAnimationRepeat: " + animation);
			if (mInAnimationListener != null)
				mInAnimationListener.onAnimationRepeat(animation);
		}

		@Override
		public void onAnimationEnd(Animation animation)
		{
			// LogUtil.v(TAG, "inAnimationEnd: " + animation);
			if (mInAnimationListener != null)
				mInAnimationListener.onAnimationEnd(animation);
			if (mDoFlipperStart && !mListenOutAnim)
			{
				mDoFlipperStart = false;
				if (mFlipperListener != null)
					mFlipperListener.onFlipperCompleted(ExtendViewFlipper.this, mNext, mInChild, mOutChild);
			}
			if (mDoBackToPrevStart && !mListenOutAnim)
			{
				mDoBackToPrevStart = false;
				removeViewAt(mOutChild);
				if (mFlipperListener != null)
					mFlipperListener.onBackToPrevViewCompleted(ExtendViewFlipper.this, mInChild, mOutChild);
			}
		}
	};

	private AnimationListener mInnerOutAnimationListener = new AnimationListener()
	{
		@Override
		public void onAnimationStart(Animation animation)
		{
			// LogUtil.v(TAG, "outAnimationStart: " + animation);
			if (mOutAnimationListener != null)
				mOutAnimationListener.onAnimationStart(animation);
		}

		@Override
		public void onAnimationRepeat(Animation animation)
		{
			// LogUtil.v(TAG, "outAnimationRepeat: " + animation);
			if (mOutAnimationListener != null)
				mOutAnimationListener.onAnimationRepeat(animation);
		}

		@Override
		public void onAnimationEnd(Animation animation)
		{
			// LogUtil.v(TAG, "outAnimationEnd: " + animation);
			if (mOutAnimationListener != null)
				mOutAnimationListener.onAnimationEnd(animation);
			if (mDoFlipperStart && mListenOutAnim)
			{
				mDoFlipperStart = false;
				if (mFlipperListener != null)
					mFlipperListener.onFlipperCompleted(ExtendViewFlipper.this, mNext, mInChild, mOutChild);
			}
			if (mDoBackToPrevStart && mListenOutAnim)
			{
				mDoBackToPrevStart = false;
				removeViewAt(mOutChild);
				if (mFlipperListener != null)
					mFlipperListener.onBackToPrevViewCompleted(ExtendViewFlipper.this, mInChild, mOutChild);
			}
		}
	};

	private AnimationListener mInAnimationListener;
	private AnimationListener mOutAnimationListener;

	public ExtendViewFlipper(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public ExtendViewFlipper(Context context)
	{
		super(context);
		init(context);
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	private void init(Context context)
	{
		int inAnim = ResourceUtil.getAnimId(context, "slide_in_right");
		int outAnim = ResourceUtil.getAnimId(context, "slide_out_left");
		setNextAnimation(context, inAnim, outAnim);
		inAnim = ResourceUtil.getAnimId(context, "slide_in_left");
		outAnim = ResourceUtil.getAnimId(context, "slide_out_right");
		setPrevAnimation(context, inAnim, outAnim);
	}

	private void ensureInAnimationListener()
	{
		if (getInAnimation() != null)
			getInAnimation().setAnimationListener(mInnerInAnimationListener);
	}

	private void ensureOutAnimationListener()
	{
		if (getOutAnimation() != null)
			getOutAnimation().setAnimationListener(mInnerOutAnimationListener);
	}

	private void ensureNextAnimation()
	{
		if (mNextInAnim != null)
			setInAnimation(mNextInAnim);
		if (mNextOutAnim != null)
			setOutAnimation(mNextOutAnim);
	}

	private void ensurePrevAnimation()
	{
		if (mPrevInAnim != null)
			setInAnimation(mPrevInAnim);
		if (mPrevOutAnim != null)
			setOutAnimation(mPrevOutAnim);
	}

	private int ensureInChild(int whichChild)
	{
		int inChild = whichChild;
		if (inChild >= getChildCount())
		{
			inChild = 0;
		}
		else if (inChild < 0)
		{
			inChild = getChildCount() - 1;
		}
		return inChild;
	}

	@Override
	public void setDisplayedChild(int whichChild)
	{
		int displayedChild = super.getDisplayedChild();
		mOutChild = displayedChild;
		mInChild = ensureInChild(whichChild);
		if (whichChild > displayedChild)
		{
			ensureNextAnimation();
			mNext = true;
			if (mFlipperListener != null)
				mFlipperListener.onFlipperStarted(this, mNext, mInChild, mOutChild);
			if (mNextInAnim != null)
			{
				mDoFlipperStart = true;
				mListenOutAnim = false;
			}
			else if (mNextOutAnim != null)
			{
				mDoFlipperStart = true;
				mListenOutAnim = true;
			}
			else
			{
				if (mFlipperListener != null)
					mFlipperListener.onFlipperCompleted(this, mNext, mInChild, mOutChild);
			}
		}
		else if (whichChild < displayedChild)
		{
			ensurePrevAnimation();
			mNext = false;
			if (mFlipperListener != null)
				mFlipperListener.onFlipperStarted(this, mNext, mInChild, mOutChild);
			if (mPrevOutAnim != null)
			{
				mDoFlipperStart = true;
				mListenOutAnim = true;
			}
			else if (mPrevInAnim != null)
			{
				mDoFlipperStart = true;
				mListenOutAnim = false;
			}
			else
			{
				if (mFlipperListener != null)
					mFlipperListener.onFlipperCompleted(this, mNext, mInChild, mOutChild);
			}
		}
		else
		{
			return;
		}
		super.setDisplayedChild(whichChild);
	}

	public View getDisplayedView()
	{
		return getCurrentView();
	}

	@Override
	public void showNext()
	{
		ensureNextAnimation();
		super.showNext();
	}

	@Override
	public void showPrevious()
	{
		ensurePrevAnimation();
		super.showPrevious();
	}

	public boolean backToPrevView()
	{
		if (getChildCount() > 1)
		{
			if (getDisplayedChild() == 0)
				return false;
			showPrevious();
			if (mPrevOutAnim != null)
			{
				mDoBackToPrevStart = true;
				mListenOutAnim = true;
			}
			else if (mPrevInAnim != null)
			{
				mDoBackToPrevStart = true;
				mListenOutAnim = false;
			}
			else
			{
				removeViewAt(mOutChild);
				if (mFlipperListener != null)
					mFlipperListener.onBackToPrevViewCompleted(this, mInChild, mOutChild);
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	public void addAndShowView(View child)
	{
		addView(child);
		ensureNextAnimation();
		setDisplayedChild(getChildCount() - 1);
	}

	public void addAndShowView(View child, int width, int height)
	{
		addView(child, width, height);
		ensureNextAnimation();
		setDisplayedChild(getChildCount() - 1);
	}

	public void addAndShowView(View child, ViewGroup.LayoutParams params)
	{
		addView(child, params);
		ensureNextAnimation();
		setDisplayedChild(getChildCount() - 1);
	}

	public void setAnimations(int nextIn, int nextOut, int prevIn, int prevOut)
	{
		mNextInAnim = AnimationUtils.loadAnimation(getContext(), nextIn);
		mNextOutAnim = AnimationUtils.loadAnimation(getContext(), nextOut);
		mPrevInAnim = AnimationUtils.loadAnimation(getContext(), prevIn);
		mPrevOutAnim = AnimationUtils.loadAnimation(getContext(), prevOut);
	}

	public void setNextAnimation(Animation inAnimation, Animation outAnimation)
	{
		mNextInAnim = inAnimation;
		mNextOutAnim = outAnimation;
	}

	public void setNextAnimation(Context context, int inAnim, int outAnim)
	{
		setNextAnimation(AnimationUtils.loadAnimation(context, inAnim), AnimationUtils.loadAnimation(context, outAnim));
	}

	public void setPrevAnimation(Animation inAnimation, Animation outAnimation)
	{
		mPrevInAnim = inAnimation;
		mPrevOutAnim = outAnimation;
	}

	public void setPrevAnimation(Context context, int inAnim, int outAnim)
	{
		setPrevAnimation(AnimationUtils.loadAnimation(context, inAnim), AnimationUtils.loadAnimation(context, outAnim));
	}

	@Override
	public void setInAnimation(Animation inAnimation)
	{
		super.setInAnimation(inAnimation);
		ensureInAnimationListener();
	}

	@Override
	public void setInAnimation(Context context, int resourceID)
	{
		setInAnimation(AnimationUtils.loadAnimation(context, resourceID));
	}

	@Override
	public void setOutAnimation(Animation outAnimation)
	{
		super.setOutAnimation(outAnimation);
		ensureOutAnimationListener();
	}

	@Override
	public void setOutAnimation(Context context, int resourceID)
	{
		setOutAnimation(AnimationUtils.loadAnimation(context, resourceID));
	}

	public void setInAnimationListener(AnimationListener listener)
	{
		mInAnimationListener = listener;
	}

	public void setOutAnimationListener(AnimationListener listener)
	{
		mOutAnimationListener = listener;
	}

	public void setOnFlipperListener(OnFlipperListener listener)
	{
		mFlipperListener = listener;
	}
}
