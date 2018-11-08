package android.extend.widget.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.extend.util.LogUtil;
import android.extend.util.ViewTools;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class GridView extends AbsListView<BaseGridAdapter<?>>
{
	private int mNumColumns = 2;
	private List<LinearLayout> mRowLayouts = new ArrayList<LinearLayout>();
	private int mStartPosition;

	public GridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public GridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public GridView(Context context)
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
	protected void initLayout()
	{
		setOrientationInner(VERTICAL);

		notifyInitContent();
	}

	@Override
	protected void initContent()
	{
		for (ViewGroup group : mRowLayouts)
		{
			recycleChildViewsResource(group);
		}
		mRowLayouts.clear();
		ViewTools.removeAllViewsInChildren(this);

		mStartPosition = 0;
		if (mAdapter == null)
			return;
		int count = mAdapter.getCount();
		LogUtil.v(TAG, "initContent: count=" + count);
		if (count == 0)
			return;
		notifyAddContent();
	}

	@Override
	protected void addContent(Message msg)
	{
		addContentImpl();
	}

	private void addContentImpl()
	{
		if (mAdapter == null)
		{
			return;
		}
		final int count = mAdapter.getCount();
		while (mStartPosition < count)
		{
			final int position = mStartPosition;
			final AbsAdapterItem item = mAdapter.getItem(position);
			int rowNum = mStartPosition / mNumColumns;
			LogUtil.v(TAG, "addContentImpl: " + position + "; " + count + "; " + rowNum);
			LinearLayout rowLayout = null;
			ViewGroup selector = (ViewGroup)findSelector(position);
			if (selector == null)
			{
				rowLayout = createRowLayout();
				mRowLayouts.add(rowNum, rowLayout);
				addView(rowLayout);
				for (int i = 0; i < mNumColumns; i++)
				{
					int pos = mNumColumns * rowNum + i;
					ViewGroup rowSelector = createSelector(pos);
					rowLayout.addView(rowSelector);
					if (i < mNumColumns - 1)
					{
						View divider = createVerticalDivider();
						rowLayout.addView(divider);
					}
					if (pos == position)
						selector = rowSelector;
				}
			}
			if (rowLayout == null)
				rowLayout = mRowLayouts.get(rowNum);
			ensureAdapterItemViewInSelector(selector, position, item, rowLayout);
			mStartPosition++;
		}
		checkHorizontalDividers();
		// notifyComputeVisibleContent(false);
	}

	private void checkHorizontalDividers()
	{
		for (ViewGroup group : mRowLayouts)
		{
			int count = getChildCount();
			for (int i = 0; i < count; i++)
			{
				View view = getChildAt(i);
				if (group == view)
				{
					if (i > 0)
					{
						view = getChildAt(i - 1);
						if (view.getId() != mDividerID)
						{
							View divider = createHorizontalDivider();
							addView(divider, i);
						}
					}
					break;
				}
			}
		}
	}

	@Override
	protected ViewGroup.LayoutParams generateSelectorLayoutParams()
	{
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		params.weight = 1;
		return params;
	}

	private LinearLayout createRowLayout()
	{
		LinearLayout layout = new LinearLayout(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(params);
		layout.setOrientation(HORIZONTAL);
		layout.setBackgroundColor(Color.TRANSPARENT);
		return layout;
	}

	private View findSelector(int position)
	{
		for (ViewGroup group : mRowLayouts)
		{
			int count = group.getChildCount();
			for (int i = 0; i < count; i++)
			{
				View view = group.getChildAt(i);
				if (view.getId() == mDividerID)
					continue;
				int pos = (Integer)view.getTag(mPositionID);
				if (pos == position)
					return view;
			}
		}
		return null;
	}

	@Override
	protected void removeContent(int position)
	{
		notifyInitContent();
	}

	@Override
	protected void updateContent()
	{
		for (ViewGroup group : mRowLayouts)
		{
			updateChildViews(group);
		}
		notifyComputeVisibleContent(false);
	}

	@Override
	protected void computeVisibleContent(boolean forceReload)
	{
		for (ViewGroup group : mRowLayouts)
		{
			computeVisibleInChildViews(group, forceReload);
		}
	}

	@Override
	protected void changeVerticalDivider()
	{
		for (ViewGroup group : mRowLayouts)
		{
			int count = group.getChildCount();
			for (int i = 0; i < count; i++)
			{
				View view = group.getChildAt(i);
				if (view.getId() == mDividerID)
					changeVerticalDividerImpl(view);
			}
		}
	}

	@Override
	protected void changeHorizontalDivider()
	{
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() == mDividerID)
				changeHorizontalDividerImpl(view);
		}
	}

	@Override
	protected void changeSelector()
	{
		for (ViewGroup group : mRowLayouts)
		{
			changeSelectorInChildViews(group);
		}
	}

	@Override
	protected void changeSelection(int position)
	{
		View selectView = null;
		ViewGroup selectLayout = null;
		for (LinearLayout layout : mRowLayouts)
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
		try
		{
			super.onSizeChanged(w, h, oldw, oldh);
			// LogUtil.d(TAG, "onSizeChanged: " + w + "; " + h + "; " + oldw + "; " + oldh);
			if (w != oldw)
			{
				notifyUpdateContent();
			}
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
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
