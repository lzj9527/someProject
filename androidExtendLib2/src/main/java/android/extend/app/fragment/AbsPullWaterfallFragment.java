package android.extend.app.fragment;

import java.util.List;

import android.extend.app.IPageLoading;
import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.BaseAdapter;
import android.extend.widget.pull.PullScrollWaterfallView;
import android.extend.widget.waterfall.BaseWaterfallAdapter;
import android.extend.widget.waterfall.ScrollWaterfallView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class AbsPullWaterfallFragment extends AbsPullViewFragment<PullScrollWaterfallView> implements
		IPageLoading<AbsAdapterItem>
{
	protected BaseWaterfallAdapter<AbsAdapterItem> mWaterfallAdapter;

	public AbsPullWaterfallFragment()
	{
		super();
	}

	public AbsPullWaterfallFragment(int layoutResID)
	{
		super(layoutResID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	public BaseAdapter<AbsAdapterItem> getWaterfallAdapter()
	{
		return mWaterfallAdapter;
	}

	public int getCurrentPage()
	{
		return mPageNumber;
	}

	@Override
	protected PullScrollWaterfallView onCreatePullView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return new PullScrollWaterfallView(getAttachedActivity());
	}

	@Override
	protected void onEnsureAdapter(PullScrollWaterfallView pullView)
	{
		ScrollWaterfallView waterfallView = pullView.getPullContentView();
		mWaterfallAdapter = new BaseWaterfallAdapter<AbsAdapterItem>();
		waterfallView.setAdapter(mWaterfallAdapter);
	}

	@Override
	protected void onClearAdapterItems()
	{
		mWaterfallAdapter.clear();
	}

	@Override
	protected void onAddAdapterItems(List<AbsAdapterItem> itemList)
	{
		mWaterfallAdapter.addItems(itemList);
	}
}
