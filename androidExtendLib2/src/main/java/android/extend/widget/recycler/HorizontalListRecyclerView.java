package android.extend.widget.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class HorizontalListRecyclerView extends ListRecyclerView
{
	public HorizontalListRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public HorizontalListRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public HorizontalListRecyclerView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		setOrientation(LinearLayoutManager.HORIZONTAL);
	}
}
