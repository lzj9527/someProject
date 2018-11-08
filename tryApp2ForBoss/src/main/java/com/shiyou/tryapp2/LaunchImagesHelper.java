package com.shiyou.tryapp2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.util.ViewTools;
import android.extend.widget.ExtendImageView;
import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.BasePagerAdapter;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.shiyou.tryapp2.boss.zsa.R;

public class LaunchImagesHelper
{
	public static final String TAG = LaunchImagesHelper.class.getSimpleName();

	public static interface Callback
	{
		public void onShowAnimationEnd();

		public void onHideAnimationEnd();
	}

	private static final int ImagesCount = 4;
	private static Activity mActivity;
	private static Callback mCallback;
	private static PopupWindow mPopupWindow;
	private static View mContentView;
	private static ViewGroup mDotContainer;
	private static ViewPager mViewPager;
	private static BasePagerAdapter<AbsAdapterItem> mPagerAdapter;

	private static final String Pref_Name = "user";
	private static final String Key_LaunchImage = "launch_image";

	private static boolean checkLaunchImageShown()
	{
		SharedPreferences pref = mActivity.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE);
		boolean result = pref.getBoolean(Key_LaunchImage, false);
		return result;
	}

	private static void setLaunchImageShown(boolean shown)
	{
		SharedPreferences pref = mActivity.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putBoolean(Key_LaunchImage, shown);
		edit.commit();
	}

	public static void clearCache()
	{
		setLaunchImageShown(false);
	}

	public static void checkAndShow(Activity activity, Callback callback)
	{
		mActivity = activity;
		mCallback = callback;
		if (!checkLaunchImageShown())
		{
			show(mCallback);
		}
		else if (mCallback != null)
		{
			mCallback.onShowAnimationEnd();
			mCallback.onHideAnimationEnd();
		}
	}

	private static void ensureContentView()
	{
		// if (mScreenSaversView == null)
		// {
		int layout = ResourceUtil.getLayoutId(mActivity, "viewpager_layout");
		mContentView = View.inflate(mActivity, layout, null);

		int id = ResourceUtil.getId(mActivity, "dot_container");
		mDotContainer = (ViewGroup)mContentView.findViewById(id);
		ensureDots(ImagesCount - 1);

		id = ResourceUtil.getId(mActivity, "viewpager");
		mViewPager = (ViewPager)mContentView.findViewById(id);
		mPagerAdapter = new BasePagerAdapter<AbsAdapterItem>();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.addOnPageChangeListener(new OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				if (position < ImagesCount - 1)
					setSelectdDot(position);
				else
				{
					hide(mCallback);
					setLaunchImageShown(true);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
		});
		mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				int width = mViewPager.getWidth();
				int height = mViewPager.getHeight();
				if (width == 0 || height == 0)
					return;
				LogUtil.v(TAG, "mViewPager size: " + width + "x" + height);
				mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				LayoutParams params = mViewPager.getLayoutParams();
				params.width = width;
				params.height = height;
				mViewPager.setLayoutParams(params);
			}
		});

		for (int i = 0; i < ImagesCount; i++)
		{
			mPagerAdapter.addItem(new PagerAdapterItem());
		}
		mViewPager.setCurrentItem(0);
		setSelectdDot(0);
		// }
	}

	private static void ensureDots(int length)
	{
		mDotContainer.removeAllViews();
		for (int i = 0; i < length; i++)
		{
			ImageView view = new ImageView(mActivity);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.topMargin = AndroidUtils.dp2px(mActivity, 2);
			params.bottomMargin = AndroidUtils.dp2px(mActivity, 2);
			params.leftMargin = AndroidUtils.dp2px(mActivity, 10);
			params.rightMargin = AndroidUtils.dp2px(mActivity, 10);
			view.setLayoutParams(params);
			int dotUnfocusId = ResourceUtil.getDrawableId(mActivity, "launch_pointer");
			view.setImageResource(dotUnfocusId);
			view.setScaleType(ScaleType.CENTER);
			mDotContainer.addView(view);
		}
		setSelectdDot(0);
	}

	private static void setSelectdDot(final int index)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				LogUtil.d(TAG, "setSelectdDot: " + index);
				int dotFocusId = ResourceUtil.getDrawableId(mActivity, "launch_pointer_s");
				int dotUnfocusId = ResourceUtil.getDrawableId(mActivity, "launch_pointer");
				int count = mDotContainer.getChildCount();
				for (int i = 0; i < count; i++)
				{
					ImageView child = (ImageView)mDotContainer.getChildAt(i);
					if (i == index)
					{
						child.setImageResource(dotFocusId);
					}
					else
					{
						child.setImageResource(dotUnfocusId);
					}
				}
			}
		});
	}

	public static void show(final Callback callback)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				ensureContentView();
				mPopupWindow = new PopupWindow(mContentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				mPopupWindow.setBackgroundDrawable(new ColorDrawable(mActivity.getResources().getColor(
						android.R.color.transparent)));
				mPopupWindow.setFocusable(false);
				mPopupWindow.setTouchable(true);
				mPopupWindow.setOutsideTouchable(true);
				mPopupWindow.setClippingEnabled(true);
				mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
				mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				mPopupWindow.showAtLocation(ViewTools.getActivityDecorView(mActivity), Gravity.CENTER, 0, 0);
				Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_in_right);
				anim.setAnimationListener(new AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation)
					{
					}

					@Override
					public void onAnimationRepeat(Animation animation)
					{
					}

					@Override
					public void onAnimationEnd(Animation animation)
					{
						if (callback != null)
							callback.onShowAnimationEnd();
					}
				});
				mContentView.startAnimation(anim);
			}
		});
	}

	public static void hide(final Callback callback)
	{
		if (mPopupWindow != null)
		{
			Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.fade_out);
			anim.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
					if (callback != null)
						callback.onHideAnimationEnd();
				}
			});
			mContentView.startAnimation(anim);
		}
	}

	private static class PagerAdapterItem extends AbsAdapterItem
	{
		public PagerAdapterItem()
		{
		}

		@Override
		public View onCreateView(int position, ViewGroup parent)
		{
			Context context = parent.getContext();
			ExtendImageView view = new ExtendImageView(context);
			view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
			view.setLayoutParams(new ViewPager.LayoutParams());
			view.setScaleType(ScaleType.CENTER_CROP);
			return view;
		}

		@Override
		public void onUpdateView(View view, int position, ViewGroup parent)
		{
		}

		@Override
		public void onLoadViewResource(View view, int position, ViewGroup parent)
		{
			Context context = parent.getContext();
			ExtendImageView imageView = (ExtendImageView)view;
			if (position < ImagesCount - 1)
			{
				int drawable = ResourceUtil.getDrawableId(context, "launch_image_" + (position + 1));
				imageView.setImageResource(drawable);
			}
		}

		@Override
		public void onRecycleViewResource(View view, int position, ViewGroup parent)
		{
		}
	}
}
