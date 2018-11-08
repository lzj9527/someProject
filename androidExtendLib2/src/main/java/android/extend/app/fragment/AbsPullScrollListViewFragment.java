package android.extend.app.fragment;

import java.util.List;

import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.BaseAdapter;
import android.extend.widget.adapter.ScrollListView;
import android.extend.widget.pull.PullScrollListView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class AbsPullScrollListViewFragment extends AbsPullViewFragment<PullScrollListView>
{
	protected BaseAdapter<AbsAdapterItem> mAdapter;

	public AbsPullScrollListViewFragment()
	{
		super();
	}

	public AbsPullScrollListViewFragment(int layoutResID)
	{
		super(layoutResID);
	}

	@Override
	protected PullScrollListView onCreatePullView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return new PullScrollListView(getAttachedActivity());
	}

	@Override
	protected void onEnsureAdapter(PullScrollListView pullView)
	{
		ScrollListView scrollView = pullView.getPullContentView();
		mAdapter = new BaseAdapter<AbsAdapterItem>();
		scrollView.setAdapter(mAdapter);
	}

	@Override
	protected void onClearAdapterItems()
	{
		mAdapter.clear();
	}

	@Override
	protected void onAddAdapterItems(List<AbsAdapterItem> itemList)
	{
		mAdapter.addItems(itemList);
	}
}
