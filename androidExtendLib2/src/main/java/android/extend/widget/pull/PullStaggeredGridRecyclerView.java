package android.extend.widget.pull;

import android.content.Context;
import android.extend.widget.recycler.StaggeredGridRecyclerView;
import android.util.AttributeSet;

public class PullStaggeredGridRecyclerView extends BasePullView<StaggeredGridRecyclerView>
{
	public PullStaggeredGridRecyclerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public PullStaggeredGridRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullStaggeredGridRecyclerView(Context context)
	{
		super(context);
	}

	@Override
	protected boolean isHorizontalLayout()
	{
		return false;
	}

	@Override
	protected StaggeredGridRecyclerView createPullConentView(Context context, AttributeSet attrs)
	{
		StaggeredGridRecyclerView view = new StaggeredGridRecyclerView(context);
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
