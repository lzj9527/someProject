package android.extend.app.fragment;

import android.extend.widget.pull.PullListView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

public abstract class AbsPullListViewFragment extends AbsPullListFragment<ListView, PullListView>
{
	public AbsPullListViewFragment()
	{
		super();
	}

	public AbsPullListViewFragment(int layoutResID)
	{
		super(layoutResID);
	}

	@Override
	protected PullListView onCreatePullView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return new PullListView(getAttachedActivity());
	}
}
