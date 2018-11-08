package android.extend.widget.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class HorizontalGridRecyclerView extends GridRecyclerView
{
	public HorizontalGridRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public HorizontalGridRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public HorizontalGridRecyclerView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		setOrientation(LinearLayoutManager.HORIZONTAL);
	}
}
