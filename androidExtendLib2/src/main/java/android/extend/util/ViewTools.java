package android.extend.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.extend.widget.ExtendImageView;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

public final class ViewTools
{
	public static final String TAG = ViewTools.class.getSimpleName();

	public enum FitMode
	{
		FIT_IN_PARENT_WIDTH, FIT_IN_PARENT_HEIGHT, FIT_IN_WIDTH, FIT_IN_HEIGHT
	}

	public static void autoFitViewDimension(final View view, final View parent, final FitMode mode, final float xyRatio)
	{
		LogUtil.d(TAG, "autoFitViewDimension: " + view + "; " + parent + "; " + mode + "; " + xyRatio);
		switch (mode)
		{
			case FIT_IN_PARENT_WIDTH:
			case FIT_IN_PARENT_HEIGHT:
				if (parent != null)
				{
					switch (mode)
					{
						case FIT_IN_PARENT_WIDTH:
							if (parent.getWidth() > 0)
							{
								int width = parent.getWidth();
								int height = (int)(parent.getWidth() / xyRatio);
								LayoutParams params = view.getLayoutParams();
								if (params != null)
								{
									params.width = width;
									params.height = height;
									LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode=" + mode
											+ "; dimension=" + params.width + "x" + params.height);
								}
								else
								{
									LogUtil.w(TAG, view + " LayoutParams is null!");
									params = new LayoutParams(width, height);
								}
								view.setLayoutParams(params);
							}
							else
							{
								parent.addOnLayoutChangeListener(new OnLayoutChangeListener()
								{
									@Override
									public void onLayoutChange(View v, int left, int top, int right, int bottom,
											int oldLeft, int oldTop, int oldRight, int oldBottom)
									{
										if (parent.getWidth() > 0)
										{
											parent.removeOnLayoutChangeListener(this);
											int width = parent.getWidth();
											int height = (int)(parent.getWidth() / xyRatio);
											LayoutParams params = view.getLayoutParams();
											if (params != null)
											{
												params.width = width;
												params.height = height;
												LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode="
														+ mode + "; dimension=" + params.width + "x" + params.height);
											}
											else
											{
												LogUtil.w(TAG, view + " LayoutParams is null!");
												params = new LayoutParams(width, height);
											}
											view.setLayoutParams(params);
										}
									}
								});
							}
							break;
						case FIT_IN_PARENT_HEIGHT:
							if (parent.getHeight() > 0)
							{
								int height = parent.getHeight();
								int width = (int)(parent.getHeight() * xyRatio);
								LayoutParams params = view.getLayoutParams();
								if (params != null)
								{
									params.height = height;
									params.width = width;
									LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode=" + mode
											+ "; dimension=" + params.width + "x" + params.height);
								}
								else
								{
									LogUtil.w(TAG, view + " LayoutParams is null!");
									params = new LayoutParams(width, height);
								}
								view.setLayoutParams(params);
							}
							else
							{
								parent.addOnLayoutChangeListener(new OnLayoutChangeListener()
								{
									@Override
									public void onLayoutChange(View v, int left, int top, int right, int bottom,
											int oldLeft, int oldTop, int oldRight, int oldBottom)
									{
										if (parent.getHeight() > 0)
										{
											parent.removeOnLayoutChangeListener(this);
											int height = parent.getHeight();
											int width = (int)(parent.getHeight() * xyRatio);
											LayoutParams params = view.getLayoutParams();
											if (params != null)
											{
												params.height = height;
												params.width = width;
												LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode="
														+ mode + "; dimension=" + params.width + "x" + params.height);
											}
											else
											{
												LogUtil.w(TAG, view + " LayoutParams is null!");
												params = new LayoutParams(width, height);
											}
											view.setLayoutParams(params);
										}
									}
								});
							}
							break;
						default:
							break;
					}
				}
				break;
			case FIT_IN_WIDTH:
				if (view.getWidth() > 0)
				{
					LayoutParams params = view.getLayoutParams();
					if (params != null)
					{
						params.height = (int)(view.getWidth() / xyRatio);
						view.setLayoutParams(params);
						LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode=" + mode + "; dimension="
								+ params.width + "x" + params.height);
					}
					else
					{
						LogUtil.w(TAG, view + " LayoutParams is null!");
					}
				}
				else
				{
					view.addOnLayoutChangeListener(new OnLayoutChangeListener()
					{
						@Override
						public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
								int oldTop, int oldRight, int oldBottom)
						{
							if (view.getWidth() > 0)
							{
								view.removeOnLayoutChangeListener(this);
								LayoutParams params = view.getLayoutParams();
								if (params != null)
								{
									params.height = (int)(view.getWidth() / xyRatio);
									view.setLayoutParams(params);
									LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode=" + mode
											+ "; dimension=" + params.width + "x" + params.height);
								}
								else
								{
									LogUtil.w(TAG, view + " LayoutParams is null!");
								}
							}
						}
					});
					// view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
					// {
					// @Override
					// public void onGlobalLayout()
					// {
					// if (view.getWidth() > 0)
					// {
					// view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					// LayoutParams params = view.getLayoutParams();
					// if (params != null)
					// {
					// params.height = (int)(view.getWidth() / xyRatio);
					// view.setLayoutParams(params);
					// LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode=" + mode
					// + "; dimension=" + params.width + "x" + params.height);
					// }
					// else
					// {
					// LogUtil.w(TAG, view + " LayoutParams is null!");
					// }
					// }
					// }
					// });
				}
				break;
			case FIT_IN_HEIGHT:
				if (view.getHeight() > 0)
				{
					LayoutParams params = view.getLayoutParams();
					if (params != null)
					{
						params.width = (int)(view.getHeight() * xyRatio);
						view.setLayoutParams(params);
						LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode=" + mode + "; dimension="
								+ params.width + "x" + params.height);
					}
					else
					{
						LogUtil.w(TAG, view + " LayoutParams is null!");
					}
				}
				else
				{
					view.addOnLayoutChangeListener(new OnLayoutChangeListener()
					{
						@Override
						public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
								int oldTop, int oldRight, int oldBottom)
						{
							if (view.getHeight() > 0)
							{
								view.removeOnLayoutChangeListener(this);
								LayoutParams params = view.getLayoutParams();
								if (params != null)
								{
									params.width = (int)(view.getHeight() * xyRatio);
									view.setLayoutParams(params);
									LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode=" + mode
											+ "; dimension=" + params.width + "x" + params.height);
								}
								else
								{
									LogUtil.w(TAG, view + " LayoutParams is null!");
								}
							}
						}
					});
					// view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
					// {
					// @Override
					// public void onGlobalLayout()
					// {
					// if (view.getHeight() > 0)
					// {
					// view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					// LayoutParams params = view.getLayoutParams();
					// if (params != null)
					// {
					// params.width = (int)(view.getHeight() * xyRatio);
					// view.setLayoutParams(params);
					// LogUtil.v(TAG, "fitViewDimension finished: view=" + view + "; mode=" + mode
					// + "; dimension=" + params.width + "x" + params.height);
					// }
					// else
					// {
					// LogUtil.w(TAG, view + " LayoutParams is null!");
					// }
					// }
					// }
					// });
				}
				break;
		}
	}

	public static ViewGroup getActivityDecorView(Activity activity)
	{
		return (ViewGroup)activity.getWindow().getDecorView();
	}

	public static ViewGroup getActivityContentRootView(Activity activity)
	{
		return (ViewGroup)activity.findViewById(android.R.id.content);
	}

	public static void setViewVisibilityInMainThread(final View view, final int visibility)
	{
		if (view == null)
			return;
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				view.setVisibility(visibility);
			}
		});
	}

	public static void setImageViewResourceInMainThread(Context context, final ImageView imageView, String resIdName)
	{
		if (context == null || imageView == null)
			return;
		final int resId = ResourceUtil.getDrawableId(context, resIdName);
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				imageView.setImageResource(resId);
			}
		});
	}

	/**
	 * 回收一个ImageView的Bitmap
	 * */
	public static void recycleImageView(ImageView imageView)
	{
		if (imageView instanceof ExtendImageView)
		{
			((ExtendImageView)imageView).recyleBitmapImage();
			return;
		}
		Drawable dw = imageView.getDrawable();
		imageView.setImageBitmap(null);
		if (dw != null && dw instanceof BitmapDrawable)
		{
			Bitmap bm = ((BitmapDrawable)dw).getBitmap();
			if (bm != null)
			{
				if (!bm.isRecycled())
				{
					LogUtil.v(TAG, "recycleImageView " + bm);
					bm.recycle();
				}
			}
		}
	}

	/**
	 * 回收View及子View的Bitmap
	 * */
	public static void recycleViewImageInChilds(View view)
	{
		if (view instanceof ViewGroup)
		{
			ViewGroup group = (ViewGroup)view;
			int count = group.getChildCount();
			for (int i = 0; i < count; i++)
			{
				View child = group.getChildAt(i);
				if (child != null)
				{
					recycleViewImageInChilds(child);
				}
			}
		}
		else if (view instanceof ExtendImageView)
		{
			((ExtendImageView)view).recyleBitmapImage();
		}
		else if (view instanceof ImageView)
		{
			recycleImageView((ImageView)view);
		}
	}

	public static List<View> findAllViewsById(ViewGroup group, int id)
	{
		List<View> list = new ArrayList<View>();
		int count = group.getChildCount();
		for (int i = 0; i < count; i++)
		{
			View child = group.getChildAt(i);
			if (child.getId() == id)
			{
				list.add(child);
			}
		}
		return list;
	}

	public static void removeAllViewsById(ViewGroup group, int id)
	{
		View view;
		while ((view = group.findViewById(id)) != null)
		{
			group.removeView(view);
		}
	}

	public static void removeAllViewsInChildren(ViewGroup group)
	{
		int count = group.getChildCount();
		for (int i = 0; i < count; i++)
		{
			View child = group.getChildAt(i);
			if (child instanceof ViewGroup)
			{
				removeAllViewsInChildren((ViewGroup)child);
			}
		}
		if (group instanceof AdapterView<?>)
		{
		}
		else
		{
			group.removeAllViews();
		}
	}

	public static void removeViewParent(View view)
	{
		ReflectHelper.setDeclaredFieldValue(view, View.class.getName(), "mParent", null);
	}

	public static void removeViewParentInChildren(View view)
	{
		LogUtil.v(TAG, "removeViewParentInChildren: " + view);
		if (view instanceof ViewGroup)
		{
			ViewGroup group = (ViewGroup)view;
			int count = group.getChildCount();
			for (int i = 0; i < count; i++)
			{
				View child = group.getChildAt(i);
				removeViewParentInChildren(child);
			}
		}
		removeViewParent(view);
	}

	public static void clearViewTags(View view)
	{
		try
		{
			Object tags = ReflectHelper.getDeclaredFieldValue(view, View.class.getName(), "mKeyedTags");
			ReflectHelper.invokePublicMethod(tags, "clear", null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static boolean containsView(ViewGroup container, View view)
	{
		int count = container.getChildCount();
		for (int i = 0; i < count; i++)
		{
			View child = container.getChildAt(i);
			if (child == view)
				return true;
		}
		return false;
	}

	private static boolean needIgnore(View view, int[] ignores)
	{
		if (ignores == null || ignores.length == 0 || view.getId() == View.NO_ID)
			return false;
		for (int id : ignores)
		{
			if (view.getId() == id)
				return true;
		}
		return false;
	}

	public static void adapterViewPadding(View view, float scaled)
	{
		if (scaled == 1.0f)
			return;
		boolean changed = false;
		int paddingLeft = view.getPaddingLeft();
		if (paddingLeft != 0)
		{
			paddingLeft = Math.round(paddingLeft * scaled);
			changed = true;
		}
		int paddingTop = view.getPaddingTop();
		if (paddingTop != 0)
		{
			paddingTop = Math.round(paddingTop * scaled);
			changed = true;
		}
		int paddingRight = view.getPaddingRight();
		if (paddingRight != 0)
		{
			paddingRight = Math.round(paddingRight * scaled);
			changed = true;
		}
		int paddingBottom = view.getPaddingBottom();
		if (paddingBottom != 0)
		{
			paddingBottom = Math.round(paddingBottom * scaled);
			changed = true;
		}
		if (changed)
		{
			LogUtil.v(TAG,
					"adapterViewPadding: " + view + " change [" + view.getPaddingLeft() + ", " + view.getPaddingTop()
							+ ", " + view.getPaddingRight() + ", " + view.getPaddingBottom() + "] to [" + paddingLeft
							+ ", " + paddingTop + ", " + paddingRight + ", " + paddingBottom + "]");
			view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
		}
	}

	public static void adapterAllViewPaddingInChildren(View view, float scaled, int[] ignores, boolean ignoreChildren)
	{
		boolean ignore = needIgnore(view, ignores);
		if (!ignore)
			adapterViewPadding(view, scaled);
		if (!ignore || (ignore && !ignoreChildren))
			if (view instanceof ViewGroup)
			{
				ViewGroup group = (ViewGroup)view;
				int count = group.getChildCount();
				for (int i = 0; i < count; i++)
				{
					View child = group.getChildAt(i);
					adapterAllViewPaddingInChildren(child, scaled, ignores, ignoreChildren);
				}
			}
	}

	public static void adapterAllViewPaddingInChildren(View view, float scaled)
	{
		adapterAllViewPaddingInChildren(view, scaled, null, true);
	}

	public static void adapterViewWidth(final View view, final float scaled)
	{
		if (scaled == 1.0f)
			return;
		if (view.getWidth() == 0)
		{
			view.addOnLayoutChangeListener(new OnLayoutChangeListener()
			{
				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
						int oldRight, int oldBottom)
				{
					if (view.getWidth() == 0)
						return;
					view.removeOnLayoutChangeListener(this);
					adapterViewWidth(view, scaled);
				}
			});
			return;
		}
		LayoutParams params = view.getLayoutParams();
		if (params == null)
			return;
		if (params.width == LayoutParams.MATCH_PARENT || params.width == LayoutParams.WRAP_CONTENT)
			return;
		int width = view.getWidth();
		params.width = Math.round(width * scaled);
		view.setLayoutParams(params);
		LogUtil.v(TAG, "adapterViewWidth: " + view + "; change " + width + " to " + params.width);
	}

	public static void adapterAllViewWidthInChildren(View view, float scaled, int[] ignores, boolean ignoreChildren)
	{
		boolean ignore = needIgnore(view, ignores);
		if (!ignore)
			adapterViewWidth(view, scaled);
		if (!ignore || (ignore && !ignoreChildren))
			if (view instanceof ViewGroup)
			{
				ViewGroup group = (ViewGroup)view;
				int count = group.getChildCount();
				for (int i = 0; i < count; i++)
				{
					View child = group.getChildAt(i);
					adapterAllViewWidthInChildren(child, scaled, ignores, ignoreChildren);
				}
			}
	}

	public static void adapterAllViewWidthInChildren(View view, float scaled)
	{
		adapterAllViewWidthInChildren(view, scaled, null, true);
	}

	public static void adapterViewHeight(final View view, final float scaled)
	{
		if (scaled == 1.0f)
			return;
		if (view.getHeight() == 0)
		{
			view.addOnLayoutChangeListener(new OnLayoutChangeListener()
			{
				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
						int oldRight, int oldBottom)
				{
					if (view.getHeight() == 0)
						return;
					view.removeOnLayoutChangeListener(this);
					adapterViewHeight(view, scaled);
				}
			});
			return;
		}
		LayoutParams params = view.getLayoutParams();
		if (params == null)
			return;
		if (params.height == LayoutParams.MATCH_PARENT || params.height == LayoutParams.WRAP_CONTENT)
			return;
		int height = view.getHeight();
		params.height = Math.round(height * scaled);
		view.setLayoutParams(params);
		LogUtil.v(TAG, "adapterViewHeight: " + view + "; change " + height + " to " + params.height);
	}

	public static void adapterAllViewHeightInChildren(View view, float scaled, int[] ignores, boolean ignoreChildren)
	{
		boolean ignore = needIgnore(view, ignores);
		if (!ignore)
			adapterViewHeight(view, scaled);
		if (!ignore || (ignore && !ignoreChildren))
			if (view instanceof ViewGroup)
			{
				ViewGroup group = (ViewGroup)view;
				int count = group.getChildCount();
				for (int i = 0; i < count; i++)
				{
					View child = group.getChildAt(i);
					adapterAllViewHeightInChildren(child, scaled, ignores, ignoreChildren);
				}
			}
	}

	public static void adapterAllViewHeightInChildren(View view, float scaled)
	{
		adapterAllViewHeightInChildren(view, scaled, null, true);
	}

	public static void adapterViewSize(final View view, final float scaled)
	{
		if (scaled == 1.0f)
			return;
		if (view.getWidth() == 0 || view.getHeight() == 0)
		{
			view.addOnLayoutChangeListener(new OnLayoutChangeListener()
			{
				@Override
				public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
						int oldRight, int oldBottom)
				{
					if (view.getWidth() == 0 || view.getHeight() == 0)
						return;
					view.removeOnLayoutChangeListener(this);
					adapterViewSize(view, scaled);
				}
			});
			return;
		}
		LayoutParams params = view.getLayoutParams();
		if (params == null)
			return;
		boolean changed = false;
		if (params.width != LayoutParams.MATCH_PARENT && params.width != LayoutParams.WRAP_CONTENT)
		{
			int width = view.getWidth();
			params.width = Math.round(width * scaled);
			changed = true;
			LogUtil.v(TAG, "adapterViewSize: " + view + "; change width " + width + " to " + params.width);
		}
		if (params.height != LayoutParams.MATCH_PARENT && params.height != LayoutParams.WRAP_CONTENT)
		{
			int height = view.getHeight();
			params.height = Math.round(height * scaled);
			changed = true;
			LogUtil.v(TAG, "adapterViewSize: " + view + "; change height " + height + " to " + params.height);
		}
		if (changed)
			view.setLayoutParams(params);
	}

	public static void adapterAllViewSizeInChildren(View view, float scaled, int[] ignores, boolean ignoreChildren)
	{
		boolean ignore = needIgnore(view, ignores);
		if (!ignore)
			adapterViewSize(view, scaled);
		if (!ignore || (ignore && !ignoreChildren))
			if (view instanceof ViewGroup)
			{
				ViewGroup group = (ViewGroup)view;
				int count = group.getChildCount();
				for (int i = 0; i < count; i++)
				{
					View child = group.getChildAt(i);
					adapterAllViewSizeInChildren(child, scaled, ignores, ignoreChildren);
				}
			}
	}

	public static void adapterAllViewSizeInChildren(View view, float scaled)
	{
		adapterAllViewSizeInChildren(view, scaled, null, true);
	}

	public static void adapterViewMargin(View view, float scaled)
	{
		if (scaled == 1.0f)
			return;
		LayoutParams params = view.getLayoutParams();
		if (params == null)
			return;
		if (params instanceof MarginLayoutParams)
		{
			MarginLayoutParams marginParams = (MarginLayoutParams)params;
			boolean changed = false;
			int leftMargin = marginParams.leftMargin;
			if (leftMargin != 0 && leftMargin != Integer.MIN_VALUE)
			{
				marginParams.leftMargin = Math.round(leftMargin * scaled);
				changed = true;
			}
			int topMargin = marginParams.topMargin;
			if (topMargin != 0 && topMargin != Integer.MIN_VALUE)
			{
				marginParams.topMargin = Math.round(topMargin * scaled);
				changed = true;
			}
			int rightMargin = marginParams.rightMargin;
			if (rightMargin != 0 && rightMargin != Integer.MIN_VALUE)
			{
				marginParams.rightMargin = Math.round(rightMargin * scaled);
				changed = true;
			}
			int bottomMargin = marginParams.bottomMargin;
			if (bottomMargin != 0 && bottomMargin != Integer.MIN_VALUE)
			{
				marginParams.bottomMargin = Math.round(bottomMargin * scaled);
				changed = true;
			}
			if (changed)
			{
				view.setLayoutParams(marginParams);
				LogUtil.v(TAG, "adapterViewMargin: " + view + " change [" + leftMargin + ", " + topMargin + ", "
						+ rightMargin + "," + bottomMargin + "] to [" + marginParams.leftMargin + ", "
						+ marginParams.topMargin + ", " + marginParams.rightMargin + ", " + marginParams.bottomMargin
						+ "]");
			}
		}
	}

	public static void adapterAllViewMarginInChildren(View view, float scaled, int[] ignores, boolean ignoreChildren)
	{
		boolean ignore = needIgnore(view, ignores);
		if (!ignore)
			adapterViewMargin(view, scaled);
		if (!ignore || (ignore && !ignoreChildren))
			if (view instanceof ViewGroup)
			{
				ViewGroup group = (ViewGroup)view;
				int count = group.getChildCount();
				for (int i = 0; i < count; i++)
				{
					View child = group.getChildAt(i);
					adapterAllViewMarginInChildren(child, scaled, ignores, ignoreChildren);
				}
			}
	}

	public static void adapterAllViewMarginInChildren(View view, float scaled)
	{
		adapterAllViewMarginInChildren(view, scaled, null, true);
	}

	public static void adapterTextViewTextSize(TextView textView, float scaled)
	{
		if (scaled == 1.0f)
			return;
		float srcSize = textView.getTextSize();
		float textSize = Math.round(srcSize * scaled);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		LogUtil.v(TAG, "adapterTextViewTextSize: " + textView + " change " + srcSize + " to " + textSize);
	}

	public static void adapterAllTextViewTextSizeInChildren(View view, float scaled, int[] ignores,
			boolean ignoreChildren)
	{
		boolean ignore = needIgnore(view, ignores);
		if (!ignore && view instanceof TextView)
			adapterTextViewTextSize((TextView)view, scaled);
		if (!ignore || (ignore && !ignoreChildren))
			if (view instanceof ViewGroup)
			{
				ViewGroup group = (ViewGroup)view;
				int count = group.getChildCount();
				for (int i = 0; i < count; i++)
				{
					View child = group.getChildAt(i);
					adapterAllTextViewTextSizeInChildren(child, scaled, ignores, ignoreChildren);
				}
			}
	}

	public static void adapterAllTextViewTextSizeInChildren(View view, float scaled)
	{
		adapterAllTextViewTextSizeInChildren(view, scaled, null, true);
	}
}
