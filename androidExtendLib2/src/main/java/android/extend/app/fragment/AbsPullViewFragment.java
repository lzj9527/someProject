package android.extend.app.fragment;

import java.util.List;

import android.extend.app.BaseFragment;
import android.extend.app.IPageLoading;
import android.extend.util.AndroidUtils;
import android.extend.util.ResourceUtil;
import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.pull.BasePullView;
import android.extend.widget.pull.BasePullView.OnPullActionListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public abstract class AbsPullViewFragment<T extends BasePullView<?>> extends BaseFragment implements
		OnPullActionListener, IPageLoading<AbsAdapterItem>
{
	protected T mPullView;
	protected int mPageNumber = 0;

	public AbsPullViewFragment()
	{
		super();
	}

	public AbsPullViewFragment(int layoutResID)
	{
		super(layoutResID);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		if (getCreatedView() != null)
			return getCreatedView();
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (view != null)
		{
			int id = ResourceUtil.getId(getAttachedActivity(), "pull");
			mPullView = (T)view.findViewById(id);
		}
		if (mPullView == null)
		{
			mPullView = onCreatePullView(inflater, container, savedInstanceState);
			mPullView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			view = mPullView;
		}
		mPullView.setOnPullActionListener(this);
		mPullView.setPullRefreshEnabled(true);
		mPullView.setPullLoadEnabled(true);
		onEnsureAdapter(mPullView);
		return view;
	}

	protected abstract T onCreatePullView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState);

	protected abstract void onEnsureAdapter(T pullView);

	@Override
	public void onFirstStart()
	{
		super.onFirstStart();
		View view = mPullView.getPullContentView();
		if (view instanceof ViewGroup)
			((ViewGroup)view).focusableViewAvailable(view);
		mPullView.doPullRefreshing();
	}

	public T getPullView()
	{
		return mPullView;
	}

	public int getCurrentPage()
	{
		return mPageNumber;
	}

	@Override
	public void onPullToRefresh(BasePullView<?> pullView)
	{
		doPullRefresh();
	}

	@Override
	public void onPullToLoad(BasePullView<?> pullView)
	{
		doPullLoad();
	}

	@Override
	public boolean hasMoreData()
	{
		return mPageNumber <= getMaxPageNumber();
	}

	public void doRefresh()
	{
		mPullView.doPullRefreshing();
	}

	protected void doPullRefresh()
	{
		mPullView.setHasMoreData(true);
		mPageNumber = 0;
		onClearAdapterItems();
		doPullLoad();
	}

	protected abstract void onClearAdapterItems();

	protected void doPullLoad()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				mPageNumber++;
				if (mPageNumber > getMaxPageNumber())
				{
					AndroidUtils.MainHandler.post(new Runnable()
					{
						@Override
						public void run()
						{
							mPullView.onPullRefreshComplete();
							mPullView.onPullLoadComplete();
							mPullView.setHasMoreData(false);
						}
					});
					return;
				}
				onPageLoadStart(mPageNumber);
			}
		}).start();
	}

	@Override
	public void onPageLoadFinish(List<AbsAdapterItem> itemList, boolean success)
	{
		if (itemList != null && !itemList.isEmpty())
		{
			onAddAdapterItems(itemList);
		}
		if (!success)
			mPageNumber--;
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				mPullView.onPullRefreshComplete();
				mPullView.onPullLoadComplete();
				if (mPageNumber + 1 > getMaxPageNumber())
				{
					mPullView.setHasMoreData(false);
				}
				else
				{
					mPullView.setHasMoreData(true);
				}
			}
		});
	}

	protected abstract void onAddAdapterItems(List<AbsAdapterItem> itemList);
}
