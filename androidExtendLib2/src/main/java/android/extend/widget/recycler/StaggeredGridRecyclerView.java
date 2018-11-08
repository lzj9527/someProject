package android.extend.widget.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

public class StaggeredGridRecyclerView extends LoadMoreRecyclerView
{
	private StaggeredGridLayoutManager mLayoutManager;
	private MarginItemDecoration mItemDecoration;

	public StaggeredGridRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public StaggeredGridRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public StaggeredGridRecyclerView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
		setLayoutManager(mLayoutManager);
		setItemAnimator(new DefaultItemAnimator());
		mItemDecoration = new MarginItemDecoration();
		addItemDecoration(mItemDecoration);
	}

	@Override
	public StaggeredGridLayoutManager getLayoutManager()
	{
		return mLayoutManager;
	}

	public void setSpanCount(int spanCount)
	{
		mLayoutManager.setSpanCount(spanCount);
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
