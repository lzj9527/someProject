package android.extend.widget.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

public class LoadMoreRecyclerView extends RecyclerView
{
	public interface OnLoadMoreListener
	{
		public void onLoadMore();
	}

	public final String TAG;
	OnLoadMoreListener mLoadMoreListener;
	boolean mHasScrolledToFoot = false;
	int mScrolledX, mScrolledY;

	public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		TAG = getClass().getSimpleName();
	}

	public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs)
	{
		super(context, attrs);
		TAG = getClass().getSimpleName();
	}

	public LoadMoreRecyclerView(Context context)
	{
		super(context);
		TAG = getClass().getSimpleName();
	}

	public boolean hasScrolledToFoot()
	{
		return mHasScrolledToFoot;
	}

	@Override
	public void onScrollStateChanged(int state)
	{
		super.onScrollStateChanged(state);
		if (getLayoutManager() == null || state != SCROLL_STATE_IDLE)
			return;
		if (getLayoutManager() instanceof LinearLayoutManager)
		{
			LinearLayoutManager manager = (LinearLayoutManager)getLayoutManager();
			switch (manager.getOrientation())
			{
				case LinearLayoutManager.HORIZONTAL:
					if (!canScrollHorizontally(1))
					{
						if (mLoadMoreListener != null)
							mLoadMoreListener.onLoadMore();
						mHasScrolledToFoot = true;
					}
					else
						mHasScrolledToFoot = false;
					break;
				default:
					if (!canScrollVertically(1))
					{
						if (mLoadMoreListener != null)
							mLoadMoreListener.onLoadMore();
						mHasScrolledToFoot = true;
					}
					else
						mHasScrolledToFoot = false;
					break;
			}
		}
		else if (getLayoutManager() instanceof StaggeredGridLayoutManager)
		{
			StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager)getLayoutManager();
			switch (manager.getOrientation())
			{
				case LinearLayoutManager.HORIZONTAL:
					if (!canScrollHorizontally(1))
					{
						if (mLoadMoreListener != null)
							mLoadMoreListener.onLoadMore();
						mHasScrolledToFoot = true;
					}
					else
						mHasScrolledToFoot = false;
					break;
				default:
					if (!canScrollVertically(1))
					{
						if (mLoadMoreListener != null)
							mLoadMoreListener.onLoadMore();
						mHasScrolledToFoot = true;
					}
					else
						mHasScrolledToFoot = false;
					break;
			}
		}
		else
		{
			if (!canScrollVertically(1))
			{
				if (mLoadMoreListener != null)
					mLoadMoreListener.onLoadMore();
				mHasScrolledToFoot = true;
			}
			else
				mHasScrolledToFoot = false;
		}
	}

	public void setOnLoadMoreListener(OnLoadMoreListener listener)
	{
		mLoadMoreListener = listener;
	}

	public int getScrolledX()
	{
		return mScrolledX;
	}

	public int getScrolledY()
	{
		return mScrolledY;
	}

	@Override
	public void onScrolled(int dx, int dy)
	{
		super.onScrolled(dx, dy);
		mScrolledX += dx;
		mScrolledY += dy;
		// LogUtil.v(TAG, "onScrolled: " + dx + ", " + dy + "; " + mScrolledX + ", " + mScrolledY);
	}
}
