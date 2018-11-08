package android.extend.widget.pull;

import android.content.Context;
import android.extend.widget.recycler.GridRecyclerView;
import android.util.AttributeSet;

public class PullGridRecyclerView extends BasePullView<GridRecyclerView>
{
	public PullGridRecyclerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public PullGridRecyclerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullGridRecyclerView(Context context)
	{
		super(context);
	}

	@Override
	protected boolean isHorizontalLayout()
	{
		return false;
	}

	@Override
	protected GridRecyclerView createPullConentView(Context context, AttributeSet attrs)
	{
		GridRecyclerView view = new GridRecyclerView(context);
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
