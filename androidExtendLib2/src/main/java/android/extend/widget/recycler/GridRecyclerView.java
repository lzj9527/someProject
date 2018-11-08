package android.extend.widget.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class GridRecyclerView extends LoadMoreRecyclerView
{
	private GridLayoutManager mLayoutManager;
	private MarginItemDecoration mItemDecoration;

	public GridRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public GridRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public GridRecyclerView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		mLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
		setLayoutManager(mLayoutManager);
		setItemAnimator(new DefaultItemAnimator());
		mItemDecoration = new MarginItemDecoration();
		addItemDecoration(mItemDecoration);
	}

	@Override
	public GridLayoutManager getLayoutManager()
	{
		return mLayoutManager;
	}

	public void setSpanCount(int spanCount)
	{
		mLayoutManager.setSpanCount(spanCount);
	}

	public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup)
	{
		mLayoutManager.setSpanSizeLookup(spanSizeLookup);
	}

	public void setOrientation(int orientation)
	{
		mLayoutManager.setOrientation(orientation);
	}

	public void setItemMargin(int margin)
	{
		mItemDecoration.setMargin(margin);
	}

	public void setItemMargin(int left, int top, int right, int bottom)
	{
		mItemDecoration.setMargin(left, top, right, bottom);
	}
}
