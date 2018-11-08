package android.extend.widget.waterfall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.extend.util.LogUtil;
import android.extend.util.ViewTools;
import android.extend.widget.ExtendLinearLayout;
import android.extend.widget.ViewObservable.OnViewObserver;
import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.AbsListView;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;

public class WaterfallView extends AbsListView<BaseWaterfallAdapter<?>>
{
	private int mNumColumns = 2;
	private List<LinearLayout> mColumnLayouts = new ArrayList<LinearLayout>();
	private int mStartPosition;
	private OnViewObserver mViewObserver = new OnViewObserver()
	{
		@Override
		public void onViewSizeChanged(View view, int w, int h, int oldw, int oldh)
		{
			// LogUtil.v(TAG, "onSizeChanged: " + view + "; " + w + "; " + h + "; " + oldw + "; " + oldh);
			for (int i = 0; i < mColumnLayouts.size(); i++)
			{
				if (mColumnLayouts.get(i).equals(view))
				{
					if (w != oldw)
					{
						notifyUpdateContent();
					}
				}
			}
		}

		@Override
		public void onViewMeasure(View view, int widthMeasureSpec, int heightMeasureSpec)
		{
		}

		@Override
		public void onViewLayout(View view, boolean changed, int left, int top, int right, int bottom)
		{
		}
	};

	public WaterfallView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public WaterfallView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public WaterfallView(Context context)
	{
		super(context);
	}

	public void setNumColumns(int numColumns)
	{
		if (mNumColumns == numColumns || numColumns < 1)
			return;
		mNumColumns = numColumns;
		notifyInitLayout();
	}

	@Override
	protected void onDataAdded(int position, AbsAdapterItem item)
	{
		notifyAddContent();
	}

	@Override
	protected void onDataAdded(int position, Collection<? extends AbsAdapterItem> itemCollection)
	{
		notifyAddContent();
	}

	@Override
	protected void onDataRemoved(int position)
	{
		LogUtil.w(TAG, "Unsupport DataRemoved...");
	}

	@Override
	protected void initLayout()
	{
		setOrientationInner(LinearLayout.HORIZONTAL);

		notifyInitContent();
	}

	@Override
	protected void initContent()
	{
		for (ViewGroup group : mColumnLayouts)
		{
			recycleChildViewsResource(group);
		}
		mColumnLayouts.clear();
		ViewTools.removeAllViewsInChildren(this);
		for (int i = 0; i < mNumColumns; i++)
		{
			LinearLayout layout = createColumnLayout();
			mColumnLayouts.add(layout);
			addView(layout);
			if (i < mNumColumns - 1)
			{
				View divider = createVerticalDivider();
				addView(divider);
			}
		}

		mStartPosition = 0;
		if (mAdapter == null)
			return;
		int count = mAdapter.getCount();
		LogUtil.v(TAG, "initContent: count=" + count);
		if (count == 0)
			return;
		notifyAddContent();
	}

	private LinearLayout createColumnLayout()
	{
		ExtendLinearLayout layout = new ExtendLinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(Color.TRANSPARENT);
		layout.registerObserver(mViewObserver);
		return layout;
	}

	@Override
	protected void addContent(Message msg)
	{
		int width = 0;
		for (LinearLayout layout : mColumnLayouts)
		{
			width += layout.getMeasuredWidth();
		}
		LogUtil.v(TAG, "addContent: width = " + width);
		if (width == 0)
		{
			mHandler.removeMessages(MSG_ADD_CONTENT);
			mHandler.sendEmptyMessageDelayed(MSG_ADD_CONTENT, 500);
			return;
		}
		addContentImpl();
	}

	private void addContentImpl()
	{
		if (mAdapter == null)
		{
			return;
		}
		final int count = mAdapter.getCount();
		LogUtil.v(TAG, "addContentImpl: " + mStartPosition + " " + count);
		// for (int i = startPosition; i < count; i++)
		if (mStartPosition < count)
		{
			final int position = mStartPosition;
			final AbsAdapterItem item = mAdapter.getItem(position);
			final int columnLocation = computeNextViewAddColumnLocation();
			final LinearLayout layout = mColumnLayouts.get(columnLocation);

			final ViewGroup selector = createSelector(position);
			ensureAdapterItemViewInSelector(selector, position, item, this);
			layout.addView(selector);

			int index = layout.getChildCount() - 1;
			if (index > 0)
			{
				// 检查前一个View是否为Divider
				View view = layout.getChildAt(index - 1);
				if (view.getId() != mDividerID)
				{
					View divider = createHorizontalDivider();
					layout.addView(divider, index);
				}
			}

			if (mSelectable && position == mSelectionPosition)
				changeSelectionImpl(layout, selector, position);

			mStartPosition++;
			layout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
			{
				@Override
				public void onGlobalLayout()
				{
					layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					notifyComputeVisibleContent(false);
					if (mStartPosition < count)
					{
						notifyAddContent();
					}
				}
			});
			return;
		}
	}

	private int computeNextViewAddColumnLocation()
	{
		int location = 0;
		int height = mColumnLayouts.get(location).getHeight();
		for (int i = 1; i < mColumnLayouts.size(); i++)
		{
			if (height > mColumnLayouts.get(i).getHeight())
			{
				location = i;
				height = mColumnLayouts.get(i).getHeight();
			}
		}
		LogUtil.v(TAG, "computeNextViewAddColumnLocation: " + location + "; " + height);
		return location;
	}

	@Override
	protected void removeContent(int position)
	{
	}

	@Override
	protected void updateContent()
	{
		for (ViewGroup group : mColumnLayouts)
		{
			updateChildViews(group);
		}
		notifyComputeVisibleContent(false);
	}

	@Override
	protected void computeVisibleContent(boolean forceReload)
	{
		for (ViewGroup group : mColumnLayouts)
		{
			computeVisibleInChildViews(group, forceReload);
		}
	}

	@Override
	protected void changeVerticalDivider()
	{
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() == mDividerID)
				changeVerticalDividerImpl(view);
		}
	}

	@Override
	protected void changeHorizontalDivider()
	{
		for (LinearLayout layout : mColumnLayouts)
		{
			int count = layout.getChildCount();
			for (int i = 0; i < count; i++)
			{
				View view = layout.getChildAt(i);
				if (view.getId() == mDividerID)
					changeHorizontalDividerImpl(view);
			}
		}
	}

	@Override
	protected void changeSelector()
	{
		for (ViewGroup group : mColumnLayouts)
		{
			changeSelectorInChildViews(group);
		}
	}

	@Override
	protected void changeSelection(int position)
	{
		View selectView = null;
		ViewGroup selectLayout = null;
		for (LinearLayout layout : mColumnLayouts)
		{
			int count = layout.getChildCount();
			for (int i = 0; i < count; i++)
			{
				View view = layout.getChildAt(i);
				if (view.getId() == mDividerID)
					continue;
				int pos = (Integer)view.getTag(mPositionID);
				if (pos == position)
				{
					selectLayout = layout;
					selectView = view;
					break;
				}
			}
			if (selectView != null)
				break;
		}
		changeSelectionImpl(selectLayout, selectView, position);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		// LogUtil.d(TAG, "onSizeChanged: " + w + "; " + h + "; " + oldw + "; " + oldh);
		if (w != oldw)
		{
			notifyUpdateContent();
		}
	}

	/**
	 * 通知Handler执行MSG_ADD_CONTENT
	 * */
	public void notifyAddContent()
	{
		mHandler.removeMessages(MSG_COMPUTE_VISIBLECONTENT);
		mHandler.removeMessages(MSG_ADD_CONTENT);
		mHandler.sendEmptyMessage(MSG_ADD_CONTENT);
	}
}
