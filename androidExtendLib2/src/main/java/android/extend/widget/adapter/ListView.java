package android.extend.widget.adapter;

import java.util.Collection;

import android.content.Context;
import android.extend.util.LogUtil;
import android.extend.util.ViewTools;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ListView extends AbsListView<BaseAdapter<?>>
{
	public ListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public ListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ListView(Context context)
	{
		super(context);
	}

	@Override
	protected void initLayout()
	{
		setOrientationInner(LinearLayout.VERTICAL);

		notifyInitContent();
	}

	@Override
	protected void initContent()
	{
		recycleChildViewsResource(this);
		ViewTools.removeAllViewsInChildren(this);
		if (mAdapter == null)
			return;
		int count = mAdapter.getCount();
		LogUtil.v(TAG, "initContent: count=" + count);
		for (int i = 0; i < count; i++)
		{
			addContent(i, mAdapter.getItem(i));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addContent(Message msg)
	{
		if (msg.obj instanceof AbsAdapterItem)
		{
			addContent(msg.arg1, (AbsAdapterItem)msg.obj);
		}
		else if (msg.obj instanceof Collection)
		{
			addContent(msg.arg1, (Collection<? extends AbsAdapterItem>)msg.obj);
		}
	}

	private void addContent(int position, AbsAdapterItem item)
	{
		int count = getChildCount();
		int index = count;
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() == mDividerID)
				continue;
			int pos = (Integer)view.getTag(mPositionID);
			if (pos == position)
			{
				index = i;
				AbsAdapterItem viewItem = (AbsAdapterItem)view.getTag(mItemID);
				if (item == viewItem)
				{
					LogUtil.i(TAG, "this position " + position + " view exists.");
					return;
				}
				break;
			}
		}
		addContentImpl(index, position, item);
		// count = getChildCount();
		// boolean updatePosition = false;
		// int pos = 0;
		// for (int i = 0; i < count; i++)
		// {
		// View view = getChildAt(i);
		// if (view.getId() == mDividerID)
		// continue;
		// int pos = (Integer)view.getTag(mPositionID);
		// if (updatePosition)
		// {
		// view.setTag(mPositionID, pos + 1);
		// }
		// else if (pos == position)
		// {
		// updatePosition = true;
		// }
		// view.setTag(mPositionID, pos);
		// pos++;
		// }
		checkContentPositons();
		checkDividers();

		notifyComputeVisibleContent(false);
	}

	private void addContentImpl(int index, final int position, final AbsAdapterItem item)
	{
		LogUtil.i(TAG, "addContentImpl: " + index + "; " + position + "; " + item);
		// if (item != mAdapter.getItem(position))
		// throw new IllegalStateException("the position " + position + " AdapterItem is not equals!!!");
		final ViewGroup selector = createSelector(position);
		ensureAdapterItemViewInSelector(selector, position, item, this);
		addView(selector, index);
		// if (index > 0)
		// {
		// // 检查前一个View是否为Divider
		// View view = getChildAt(index - 1);
		// if (view.getId() != mDividerID)
		// {
		// View divider = generateDivider();
		// addView(divider, index);
		// index++;
		// }
		// }
		// if (index < getChildCount() - 1)
		// {
		// // 检查后一个View是否为Divider
		// View view = getChildAt(index + 1);
		// if (view.getId() != mDividerID)
		// {
		// View divider = generateDivider();
		// addView(divider, index + 1);
		// }
		// }
		if (mSelectable && position == mSelectionPosition)
			changeSelectionImpl(this, selector, position);
	}

	private void addContent(int position, Collection<? extends AbsAdapterItem> itemCollection)
	{
		for (AbsAdapterItem item : itemCollection)
		{
			addContent(position, item);
			position++;
		}
	}

	protected View generateDivider()
	{
		return createHorizontalDivider();
	}

	private void checkDividers()
	{
		for (int i = 0; i < getChildCount(); i++)
		{
			View view = getChildAt(i);
			if (view.getId() != mDividerID)
			{
				if (i > 0)
				{
					view = getChildAt(i - 1);
					if (view.getId() != mDividerID)
					{
						View divider = generateDivider();
						addView(divider, i);
					}
				}
			}
		}
	}

	@Override
	protected void removeContent(int position)
	{
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() == mDividerID)
				continue;
			int pos = (Integer)view.getTag(mPositionID);
			if (pos == position)
			{
				removeViewAt(i);
				AbsAdapterItem item = (AbsAdapterItem)view.getTag(mItemID);
				item.onRecycleViewResource(view, position, this);
				count = getChildCount();
				if (i < count)
				{
					view = getChildAt(i);
					if (view.getId() == mDividerID)
					{
						removeViewAt(i);
						break;
					}
				}
				else if (i > 0)
				{
					view = getChildAt(i - 1);
					if (view.getId() == mDividerID)
					{
						removeViewAt(i - 1);
						break;
					}
				}
				break;
			}
		}
		checkContentPositons();
		notifyUpdateContent();
	}

	protected void checkContentPositons()
	{
		int position = 0;
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() == mDividerID)
				continue;
			int pos = (Integer)view.getTag(mPositionID);
			if (pos != position)
			{
				view.setTag(mPositionID, position);
			}
			position++;
		}
	}

	@Override
	protected void updateContent()
	{
		updateChildViews(this);
		computeVisibleContent(false);
	}

	@Override
	protected void computeVisibleContent(boolean forceReload)
	{
		computeVisibleInChildViews(this, forceReload);
	}

	@Override
	protected void changeVerticalDivider()
	{
		LogUtil.w(TAG, "No VerticalDivider...");
	}

	@Override
	protected void changeHorizontalDivider()
	{
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() != mDividerID)
				continue;
			changeHorizontalDividerImpl(view);
		}
	}

	@Override
	protected void changeSelector()
	{
		changeSelectorInChildViews(this);
	}

	@Override
	protected void changeSelection(int position)
	{
		View selectView = null;
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() == mDividerID)
				continue;
			int pos = (Integer)view.getTag(mPositionID);
			if (pos == position)
			{
				selectView = view;
				break;
			}
		}
		changeSelectionImpl(this, selectView, position);
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
}
