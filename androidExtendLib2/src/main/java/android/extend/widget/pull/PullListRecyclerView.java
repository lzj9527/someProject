package android.extend.widget.pull;

import android.content.Context;
import android.extend.widget.recycler.ListRecyclerView;
import android.util.AttributeSet;

public class PullListRecyclerView extends BasePullView<ListRecyclerView>
{
	public PullListRecyclerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public PullListRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullListRecyclerView(Context context)
	{
		super(context);
	}

	@Override
	protected boolean isHorizontalLayout()
	{
		return false;
	}

	@Override
	protected ListRecyclerView createPullConentView(Context context, AttributeSet attrs)
	{
		ListRecyclerView view = new ListRecyclerView(context);
		return view;
	}

	@Override
	protected boolean isReadyForPullRefresh()
	{
		return mPullContentView.getScrolledY() == 0;
	}

	@Override
	protected boolean isReadyForPullLoad()
	{
		return mPullContentView.hasScrolledToFoot();
	}
}
