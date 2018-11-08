package android.extend.widget.recycler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class ListRecyclerView extends LoadMoreRecyclerView
{
	private LinearLayoutManager mLayoutManager;
	private ListDividerItemDecoration mItemDecoration;

	public ListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public ListRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public ListRecyclerView(Context context)
	{
		super(context);
		init(context);
	}

	private void init(Context context)
	{
		mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
		setLayoutManager(mLayoutManager);
		setItemAnimator(new DefaultItemAnimator());
		mItemDecoration = new ListDividerItemDecoration((Drawable)null, LinearLayoutManager.VERTICAL);
		addItemDecoration(mItemDecoration);
	}

	@Override
	public LinearLayoutManager getLayoutManager()
	{
		return mLayoutManager;
	}

	public void setOrientation(int orientation)
	{
		mLayoutManager.setOrientation(orientation);
		mItemDecoration.setOrientation(orientation);
	}

	public void setDivider(Drawable divider)
	{
		mItemDecoration.setDivider(divider);
	}

	public void setDividerWidth(int width)
	{
		mItemDecoration.setDividerWidth(width);
	}

	public void setDividerHeight(int height)
	{
		mItemDecoration.setDividerHeight(height);
	}
}
