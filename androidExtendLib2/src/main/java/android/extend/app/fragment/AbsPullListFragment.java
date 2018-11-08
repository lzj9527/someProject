package android.extend.app.fragment;

import java.util.List;

import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.ListAdapter;
import android.extend.widget.pull.BasePullView;
import android.widget.AbsListView;

public abstract class AbsPullListFragment<T extends AbsListView, V extends BasePullView<T>> extends
		AbsPullViewFragment<V>
{
	protected ListAdapter<AbsAdapterItem> mListAdapter;

	public AbsPullListFragment()
	{
		super();
	}

	public AbsPullListFragment(int layoutResID)
	{
		super(layoutResID);
	}

	public ListAdapter<AbsAdapterItem> getListAdapter()
	{
		return mListAdapter;
	}

	@Override
	protected void onEnsureAdapter(V pullView)
	{
		T listView = pullView.getPullContentView();
		mListAdapter = new ListAdapter<AbsAdapterItem>(listView);
		listView.setAdapter(mListAdapter);
	}

	@Override
	protected void onClearAdapterItems()
	{
		mListAdapter.clear();
	}

	@Override
	protected void onAddAdapterItems(List<AbsAdapterItem> itemList)
	{
		mListAdapter.addItems(itemList);
	}
}
