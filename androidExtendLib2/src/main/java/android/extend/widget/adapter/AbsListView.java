package android.extend.widget.adapter;

import java.util.Collection;

import android.content.Context;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.widget.ExtendFrameLayout;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class AbsListView<T extends BaseAdapter<?>> extends LinearLayout implements IAdapterView<T>
{
	public final String TAG = getClass().getSimpleName();

	protected boolean mContinueRunInDetachedFromWindow = false;
	protected boolean mDetachedFromWindow = false;

	protected int mPositionID;
	protected int mDividerID;
	protected int mItemID;
	// protected int mRecycledID;
	protected int mLoadedID;

	protected T mAdapter = null;
	protected int mSelectorResId;
	protected int mSelectorPadding;
	protected boolean mSelectable = false;
	protected int mSelectionPosition = -1;
	protected View mSelection;
	protected int mHorizontalDividerHeight = 0;
	protected int mHorizontalDividerResId = 0;
	protected int mHorizontalDividerColor = Color.TRANSPARENT;
	protected int mVerticalDividerWidth = 0;
	protected int mVerticalDividerResId = 0;
	protected int mVerticalDividerColor = Color.TRANSPARENT;

	protected OnItemClickListener mItemClickListener;
	protected OnItemLongClickListener mItemLongClickListener;
	protected OnItemSelectedListener mItemSelectedListener;

	protected OnDataSetObserver mDataObserver = new OnDataSetObserver()
	{
		@Override
		public void onDataAdded(int position, AbsAdapterItem item)
		{
			LogUtil.d(TAG, "onDataAdded: " + position + "; " + item);
			AbsListView.this.onDataAdded(position, item);
		}

		@Override
		public void onDataAdded(int position, Collection<? extends AbsAdapterItem> itemCollection)
		{
			LogUtil.d(TAG, "onDataAdded: " + position + "; " + itemCollection);
			AbsListView.this.onDataAdded(position, itemCollection);
		}

		@Override
		public void onDataRemoved(int position)
		{
			LogUtil.d(TAG, "onDataRemoved: " + position);
			AbsListView.this.onDataRemoved(position);
		}

		@Override
		public void onDataCleared()
		{
			LogUtil.d(TAG, "onDataCleared...");
			AbsListView.this.onDataCleared();
		}

		@Override
		public void onChanged()
		{
			LogUtil.d(TAG, "onDataChanged...");
			AbsListView.this.onDataChanged();
		}

		@Override
		public void onInvalidated()
		{
			LogUtil.d(TAG, "onDataInvalidated...");
			AbsListView.this.onDataInvalidated();
		}
	};

	protected Handler mHandler = new Handler(Looper.getMainLooper())
	{
		@Override
		public void handleMessage(Message msg)
		{
			AbsListView.this.handleMessage(msg);
		}
	};

	public AbsListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public AbsListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public AbsListView(Context context)
	{
		super(context);
		init();
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	@Override
	public void setOrientation(int orientation)
	{
		LogUtil.w(TAG, "Unsupport setOrientation");
	}

	protected void setOrientationInner(int orientation)
	{
		super.setOrientation(orientation);
	}

	protected void init()
	{
		try
		{
			mPositionID = ResourceUtil.getPositionId(getContext());
			mDividerID = ResourceUtil.getDividerId(getContext());
			mItemID = ResourceUtil.getItemId(getContext());
			// mRecycledID = ResourceUtil.getRecycledId(getContext());
			mLoadedID = ResourceUtil.getLoadedId(getContext());

			// mVerticalDividerWidth = AndroidUtils.dp2px(getContext(), 10);
			// mHorizontalDividerHeight = AndroidUtils.dp2px(getContext(), 10);

			initLayout();

			// getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
			// {
			// @Override
			// public void onGlobalLayout()
			// {
			// LogUtil.v(TAG, "onGlobalLayout...");
			// notifyComputeVisibleContent(false);
			// }
			// });
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	protected void handleMessage(Message msg)
	{
		try
		{
			LogUtil.d(TAG, "handleMessage: " + msg);
			switch (msg.what)
			{
				case MSG_INIT_LAYOUT:
					initLayout();
					break;
				case MSG_INIT_CONTENT:
					initContent();
					break;
				case MSG_ADD_CONTENT:
					addContent(msg);
					break;
				case MSG_REMOVE_CONTENT:
					removeContent(msg.arg1);
					break;
				case MSG_UPDATE_CONTENT:
					updateContent();
					break;
				case MSG_COMPUTE_VISIBLECONTENT:
					if (getVisibility() == View.VISIBLE)
						computeVisibleContent((Boolean)msg.obj);
					break;
				case MSG_CHANGE_VERTICALDIVIDER:
					changeVerticalDivider();
					break;
				case MSG_CHANGE_HORIZONTALDIVIDER:
					changeHorizontalDivider();
					break;
				case MSG_CHANGE_SELECTOR:
					changeSelector();
					break;
				case MSG_CHANGE_SELECTION:
					changeSelection(msg.arg1);
					break;
			}
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	protected void onDataAdded(int position, AbsAdapterItem item)
	{
		notifyAddContent(position, item);
	}

	protected void onDataAdded(int position, Collection<? extends AbsAdapterItem> itemCollection)
	{
		notifyAddContent(position, itemCollection);
	}

	protected void onDataRemoved(int position)
	{
		notifyRemoveContent(position);
	}

	protected void onDataCleared()
	{
		notifyInitContent();
	}

	protected void onDataChanged()
	{
		notifyUpdateContent();
	}

	protected void onDataInvalidated()
	{
		notifyInitContent();
	}

	protected abstract void initLayout();

	protected abstract void initContent();

	protected abstract void addContent(Message msg);

	protected abstract void removeContent(int position);

	protected abstract void updateContent();

	protected abstract void computeVisibleContent(boolean forceReload);

	protected abstract void changeVerticalDivider();

	protected abstract void changeHorizontalDivider();

	protected abstract void changeSelector();

	protected abstract void changeSelection(int position);

	protected void recycleChildViewsResource(ViewGroup group)
	{
		int count = group.getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = group.getChildAt(i);
			if (view.getId() == mDividerID)
			{
				continue;
			}
			int position = (Integer)view.getTag(mPositionID);
			AbsAdapterItem item = (AbsAdapterItem)view.getTag(mItemID);
			if (item == null)
			{
				LogUtil.w(TAG, "recycle warning, the " + view + " position " + position + " item is null!");
				continue;
			}
			View child = ((ViewGroup)view).getChildAt(0);
			if (child == null)
			{
				continue;
			}
			item.onRecycleViewResource(child, position, this);
		}
	}

	protected void updateChildViews(ViewGroup group)
	{
		int count = group.getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = group.getChildAt(i);
			if (view.getId() == mDividerID)
				continue;
			int position = (Integer)view.getTag(mPositionID);
			AbsAdapterItem item = (AbsAdapterItem)view.getTag(mItemID);
			if (item == null)
			{
				LogUtil.w(TAG, "update warning, the " + view + " position " + position + " item is null!");
				continue;
			}
			View child = ((ViewGroup)view).getChildAt(0);
			if (child == null)
			{
				continue;
			}
			item.onUpdateView(child, position, this);
		}
	}

	protected void computeVisibleInChildViews(ViewGroup group, boolean forceReload)
	{
		Rect rect = new Rect();
		getGlobalVisibleRect(rect);
		int visibleLeft = rect.left;
		int visibleTop = rect.top;
		int visibleRight = rect.right;
		int visibleBottom = rect.bottom;
		// LogUtil.v(TAG, "computeVisibleInChildViews: getGlobalVisibleRect: " + rect);
		int count = group.getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = group.getChildAt(i);
			// LogUtil.i(TAG, i + "; " + view);
			if (view.getId() == mDividerID || view.getWidth() == 0 || view.getHeight() == 0)
				continue;
			int position = (Integer)view.getTag(mPositionID);
			AbsAdapterItem item = (AbsAdapterItem)view.getTag(mItemID);
			if (item == null)
			{
				LogUtil.w(TAG, "compute visible warning, the " + view + " position " + position + " item is null!");
				continue;
			}
			View child = ((ViewGroup)view).getChildAt(0);
			if (child == null || child.getVisibility() != View.VISIBLE)
			{
				continue;
			}
			// boolean recycled = (Boolean)view.getTag(mRecycledID);
			boolean loaded = (Boolean)view.getTag(mLoadedID);
			int[] location = new int[2];
			view.getLocationOnScreen(location);
			// LogUtil.v(TAG, position + " getLocationOnScreen: " + location[0] + "," + location[1]);
			int viewLeft = location[0];
			int viewRight = viewLeft + view.getWidth();
			int viewTop = location[1];
			int viewBottom = viewTop + view.getHeight();
			// LogUtil.v(TAG, position + ": viewLeft=" + viewLeft + "; viewRight=" + viewRight + "; viewTop=" + viewTop
			// + "; viewBottom=" + viewBottom);
			try
			{
				if (viewRight < visibleLeft || viewLeft > visibleRight || viewBottom < visibleTop
						|| viewTop > visibleBottom)
				{
					if (loaded)
					{
						item.onRecycleViewResource(child, position, group);
						// view.setTag(mRecycledID, true);
						view.setTag(mLoadedID, false);
					}
				}
				else
				{
					item.onUpdateView(child, position, group);
					if (!loaded || forceReload)
					{
						if (forceReload)
							item.onRecycleViewResource(child, position, group);
						item.onLoadViewResource(child, position, group);
						view.setTag(mLoadedID, true);
						// view.setTag(mRecycledID, false);
					}
				}
			}
			catch (Exception e)
			{
				LogUtil.w(TAG, "", e);
			}
		}
	}

	protected ViewGroup createSelector(int position)
	{
		// LogUtil.v(TAG, "createSelector: " + position);
		final ExtendFrameLayout selector = new ExtendFrameLayout(getContext());
		selector.setLayoutParams(generateSelectorLayoutParams());
		selector.setTag(mPositionID, position);
		// selector.setTag(mRecycledID, false);
		selector.setTag(mLoadedID, false);
		changeSelectorImpl(selector);
		return selector;
	}

	protected ViewGroup.LayoutParams generateSelectorLayoutParams()
	{
		return new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	}

	protected void ensureAdapterItemViewInSelector(final ViewGroup selector, final int position,
			final AbsAdapterItem item, final ViewGroup parent)
	{
		// LogUtil.v(TAG, "ensureAdapterItemViewInSelector: " + selector + "; " + position + "; " + item);
		selector.setTag(mItemID, item);
		selector.removeAllViews();
		final View view = item.onCreateView(position, parent);
		selector.addView(view);
		selector.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				long id = mAdapter.getItemId(position);
				if (mItemClickListener != null)
					mItemClickListener.onItemClick(AbsListView.this, parent, view, position, id);
				else
					item.onItemClick(AbsListView.this, parent, view, position, id);
				if (!mSelectable)
					return;
				changeSelectionImpl(parent, selector, position);
			}
		});
		selector.setOnLongClickListener(new View.OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				long id = mAdapter.getItemId(position);
				if (mItemLongClickListener != null)
					return mItemLongClickListener.onItemLongClick(AbsListView.this, parent, view, position, id);
				else
					return item.onItemLongClick(AbsListView.this, parent, view, position, id);
			}
		});
		item.onUpdateView(view, position, parent);
	}

	protected View createVerticalDivider()
	{
		View divider = new View(getContext());
		divider.setId(mDividerID);
		changeVerticalDividerImpl(divider);
		return divider;
	}

	protected void changeVerticalDividerImpl(View divider)
	{
		LayoutParams params = (LayoutParams)divider.getLayoutParams();
		int dividerWidth = (mVerticalDividerWidth > 0 ? mVerticalDividerWidth : 0);
		if (params == null)
		{
			params = new LayoutParams(dividerWidth, LayoutParams.MATCH_PARENT);
		}
		else
		{
			params.width = dividerWidth;
			params.height = LayoutParams.MATCH_PARENT;
		}
		divider.setLayoutParams(params);
		if (mVerticalDividerResId > 0)
			divider.setBackgroundResource(mVerticalDividerResId);
		else
			divider.setBackgroundColor(mVerticalDividerColor);
	}

	protected View createHorizontalDivider()
	{
		View divider = new View(getContext());
		divider.setId(mDividerID);
		changeHorizontalDividerImpl(divider);
		return divider;
	}

	protected void changeHorizontalDividerImpl(View divider)
	{
		LayoutParams params = (LayoutParams)divider.getLayoutParams();
		int dividerHeight = (mHorizontalDividerHeight > 0 ? mHorizontalDividerHeight : 0);
		if (params == null)
		{
			params = new LayoutParams(LayoutParams.MATCH_PARENT, dividerHeight);
		}
		else
		{
			params.width = LayoutParams.MATCH_PARENT;
			params.height = dividerHeight;
		}
		divider.setLayoutParams(params);
		if (mHorizontalDividerResId > 0)
			divider.setBackgroundResource(mHorizontalDividerResId);
		else
			divider.setBackgroundColor(mHorizontalDividerColor);
	}

	protected void changeSelectorImpl(View selector)
	{
		if (mSelectorResId > 0)
			selector.setBackgroundResource(mSelectorResId);
		else
			selector.setBackgroundColor(Color.TRANSPARENT);
		selector.setPadding(mSelectorPadding, mSelectorPadding, mSelectorPadding, mSelectorPadding);
	}

	protected void changeSelectorInChildViews(ViewGroup group)
	{
		int count = group.getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = group.getChildAt(i);
			if (view.getId() == mDividerID)
				continue;
			changeSelectorImpl(view);
		}
	}

	protected void changeSelectionImpl(ViewGroup parent, View newSelection, int newPosition)
	{
		LogUtil.v(TAG, "changeSelectionImpl: newSelection=" + newSelection + "; newPosition=" + newPosition);
		mSelectionPosition = newPosition;
		if (newSelection == mSelection)
			return;
		if (mSelection != null)
		{
			mSelection.setSelected(false);
			ViewGroup selector = (ViewGroup)mSelection;
			View view = selector.getChildAt(0);
			long id = mAdapter.getItemId(newPosition);
			AbsAdapterItem item = (AbsAdapterItem)mSelection.getTag(mItemID);
			if (mItemSelectedListener != null)
				mItemSelectedListener.onItemUnSelected(this, parent, view, newPosition, id);
			else
				item.onItemUnSelected(this, parent, view, newPosition, id);
		}
		mSelection = newSelection;
		if (mSelection != null)
		{
			mSelection.setSelected(true);
			ViewGroup selector = (ViewGroup)mSelection;
			View view = selector.getChildAt(0);
			long id = mAdapter.getItemId(newPosition);
			AbsAdapterItem item = (AbsAdapterItem)mSelection.getTag(mItemID);
			if (mItemSelectedListener != null)
				mItemSelectedListener.onItemSelected(this, parent, view, newPosition, id);
			else
				item.onItemSelected(this, parent, view, newPosition, id);
		}
		else
		{
			if (mItemSelectedListener != null)
				mItemSelectedListener.onNothingSelected(this);
		}
	}

	@Override
	public void setAdapter(T adapter)
	{
		if (adapter == null)
		{
			throw new NullPointerException();
		}
		if (mAdapter == adapter)
		{
			return;
		}
		if (mAdapter != null)
		{
			mAdapter.unregisterOnDataSetObserver(mDataObserver);
		}
		mAdapter = adapter;
		mAdapter.registerOnDataSetObserver(mDataObserver);

		notifyInitContent();
	}

	@Override
	public T getAdapter()
	{
		return mAdapter;
	}

	@Override
	public void setVerticalDividerWidth(int width)
	{
		if (mVerticalDividerWidth == width)
			return;
		mVerticalDividerWidth = width;
		notifyChangeVerticalDivider();
	}

	@Override
	public void setVerticalDividerResource(int resId)
	{
		mVerticalDividerResId = resId;
		mVerticalDividerColor = Color.TRANSPARENT;
		notifyChangeVerticalDivider();
	}

	@Override
	public void setVerticalDividerColor(int color)
	{
		mVerticalDividerColor = color;
		mVerticalDividerResId = 0;
		notifyChangeVerticalDivider();
	}

	@Override
	public void setHorizontalDividerHeight(int height)
	{
		if (mHorizontalDividerHeight == height)
			return;
		mHorizontalDividerHeight = height;
		notifyChangeHorizontalDivider();
	}

	@Override
	public void setHorizontalDividerResource(int resId)
	{
		mHorizontalDividerResId = resId;
		mHorizontalDividerColor = Color.TRANSPARENT;
		notifyChangeHorizontalDivider();
	}

	@Override
	public void setHorizontalDividerColor(int color)
	{
		mHorizontalDividerColor = color;
		mHorizontalDividerResId = 0;
		notifyChangeHorizontalDivider();
	}

	@Override
	public void setSelector(int resId)
	{
		mSelectorResId = resId;
		notifyChangeSelector();
	}

	@Override
	public void setSelectorPadding(int padding)
	{
		mSelectorPadding = padding;
		notifyChangeSelector();
	}

	@Override
	public void setSelectable(boolean selectable)
	{
		mSelectable = selectable;
		if (!selectable && mSelection != null)
		{
			changeSelectionImpl(this, null, -1);
		}
	}

	@Override
	public void setSelection(int position)
	{
		setSelectable(true);
		notifyChangeSelection(position);
	}

	@Override
	public View getSelectedView()
	{
		return mSelection;
	}

	@Override
	public int getSelectedPosition()
	{
		return mSelectionPosition;
	}

	@Override
	public View getItemView(int position)
	{
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() == mDividerID)
				continue;
			int pos = (Integer)view.getTag(mPositionID);
			if (pos == position)
				return view;
		}
		return null;
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mItemClickListener = listener;
	}

	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener)
	{
		mItemLongClickListener = listener;
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener)
	{
		mItemSelectedListener = listener;
	}

	public void setContinueRunInDetachedFromWindow(boolean flag)
	{
		mContinueRunInDetachedFromWindow = flag;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		try
		{
			super.onLayout(changed, l, t, r, b);
			// LogUtil.d(TAG, "onLayout: " + changed);
			if (changed)
			{
				notifyComputeVisibleContent(false);
			}
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		try
		{
			super.onScrollChanged(l, t, oldl, oldt);
			// LogUtil.d(TAG, "onScrollChanged: " + l + "; " + t + "; " + oldl + "; " + oldt);
			if (l != oldl || t != oldt)
			{
				notifyComputeVisibleContent(false);
			}
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	@Override
	protected void onAttachedToWindow()
	{
		try
		{
			super.onAttachedToWindow();
			LogUtil.d(TAG, "onAttachedToWindow...");
			mDetachedFromWindow = false;
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	@Override
	protected void onDetachedFromWindow()
	{
		try
		{
			super.onDetachedFromWindow();
			LogUtil.d(TAG, "onDetachedFromWindow...");
			mDetachedFromWindow = true;
			if (!mContinueRunInDetachedFromWindow)
				removeAllMessages();
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility)
	{
		super.onVisibilityChanged(changedView, visibility);
		LogUtil.v(TAG, "onVisibilityChanged: " + changedView + "; " + visibility);
		if (changedView == this && visibility == VISIBLE)
			notifyComputeVisibleContent(false);
	}

	// @Override
	// protected void onWindowVisibilityChanged(int visibility)
	// {
	// super.onWindowVisibilityChanged(visibility);
	// LogUtil.v(TAG, "onWindowVisibilityChanged: " + visibility);
	// }

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		super.onWindowFocusChanged(hasWindowFocus);
		LogUtil.v(TAG, "onWindowFocusChanged: " + hasWindowFocus);
		if (hasWindowFocus)
			notifyComputeVisibleContent(false);
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
			LogUtil.w(TAG, this + " dispatchDraw error", e);
			// invalidate();
			notifyComputeVisibleContent(true);
		}
	}

	protected void removeAllMessages()
	{
		mHandler.removeMessages(MSG_CHANGE_HORIZONTALDIVIDER);
		mHandler.removeMessages(MSG_CHANGE_VERTICALDIVIDER);
		mHandler.removeMessages(MSG_CHANGE_SELECTION);
		mHandler.removeMessages(MSG_CHANGE_SELECTOR);
		mHandler.removeMessages(MSG_UPDATE_CONTENT);
		mHandler.removeMessages(MSG_COMPUTE_VISIBLECONTENT);
		mHandler.removeMessages(MSG_REMOVE_CONTENT);
		mHandler.removeMessages(MSG_ADD_CONTENT);
		mHandler.removeMessages(MSG_INIT_CONTENT);
		mHandler.removeMessages(MSG_INIT_LAYOUT);
	}

	/**
	 * 通知Handler执行MSG_INIT_LAYOUT
	 * */
	public void notifyInitLayout()
	{
		removeAllMessages();
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.sendEmptyMessage(MSG_INIT_LAYOUT);
	}

	/**
	 * 通知Handler执行MSG_INIT_CONTENT
	 * */
	public void notifyInitContent()
	{
		mHandler.removeMessages(MSG_CHANGE_HORIZONTALDIVIDER);
		mHandler.removeMessages(MSG_CHANGE_VERTICALDIVIDER);
		mHandler.removeMessages(MSG_CHANGE_SELECTION);
		mHandler.removeMessages(MSG_CHANGE_SELECTOR);
		mHandler.removeMessages(MSG_UPDATE_CONTENT);
		mHandler.removeMessages(MSG_COMPUTE_VISIBLECONTENT);
		mHandler.removeMessages(MSG_REMOVE_CONTENT);
		mHandler.removeMessages(MSG_ADD_CONTENT);
		mHandler.removeMessages(MSG_INIT_CONTENT);
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		setSelection(-1);
		mHandler.sendEmptyMessage(MSG_INIT_CONTENT);
	}

	/**
	 * 通知Handler执行MSG_ADD_CONTENT
	 * */
	public void notifyAddContent(int position, AbsAdapterItem item)
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_COMPUTE_VISIBLECONTENT);
		Message msg = mHandler.obtainMessage(MSG_ADD_CONTENT);
		msg.arg1 = position;
		msg.obj = item;
		msg.sendToTarget();
	}

	/**
	 * 通知Handler执行MSG_ADD_CONTENT
	 * */
	public void notifyAddContent(int position, Collection<? extends AbsAdapterItem> itemCollection)
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_COMPUTE_VISIBLECONTENT);
		Message msg = mHandler.obtainMessage(MSG_ADD_CONTENT);
		msg.arg1 = position;
		msg.obj = itemCollection;
		msg.sendToTarget();
	}

	/**
	 * 通知Handler执行MSG_REMOVE_CONTENT
	 * */
	public void notifyRemoveContent(int position)
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_COMPUTE_VISIBLECONTENT);
		Message msg = mHandler.obtainMessage(MSG_REMOVE_CONTENT);
		msg.arg1 = position;
		msg.sendToTarget();
	}

	/**
	 * 通知Handler执行MSG_UPDATE_CONTENT
	 * */
	public void notifyUpdateContent()
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_UPDATE_CONTENT);
		mHandler.sendEmptyMessage(MSG_UPDATE_CONTENT);
	}

	/**
	 * 通知Handler执行MSG_COMPUTE_VISIBLECONTENT
	 * */
	public void notifyComputeVisibleContent(boolean forceReload)
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_COMPUTE_VISIBLECONTENT);
		Message msg = mHandler.obtainMessage(MSG_COMPUTE_VISIBLECONTENT, forceReload);
		msg.sendToTarget();
	}

	/**
	 * 通知Handler执行MSG_CHANGE_VERTICALDIVIDER
	 * */
	public void notifyChangeVerticalDivider()
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_CHANGE_VERTICALDIVIDER);
		mHandler.sendEmptyMessage(MSG_CHANGE_VERTICALDIVIDER);
	}

	/**
	 * 通知Handler执行MSG_CHANGE_HORIZONTALDIVIDER
	 * */
	public void notifyChangeHorizontalDivider()
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_CHANGE_HORIZONTALDIVIDER);
		mHandler.sendEmptyMessage(MSG_CHANGE_HORIZONTALDIVIDER);
	}

	/**
	 * 通知Handler执行MSG_CHANGE_SELECTOR
	 * */
	public void notifyChangeSelector()
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_CHANGE_SELECTOR);
		mHandler.sendEmptyMessage(MSG_CHANGE_SELECTOR);
	}

	/**
	 * 通知Handler执行MSG_CHANGE_SELECTION
	 * */
	public void notifyChangeSelection(int position)
	{
		if (mDetachedFromWindow && !mContinueRunInDetachedFromWindow)
			return;
		mHandler.removeMessages(MSG_CHANGE_SELECTION);
		Message msg = mHandler.obtainMessage(MSG_CHANGE_SELECTION);
		msg.arg1 = position;
		msg.sendToTarget();
	}
}
