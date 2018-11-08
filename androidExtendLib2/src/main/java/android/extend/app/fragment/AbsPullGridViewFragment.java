package android.extend.app.fragment;

import android.extend.widget.pull.PullGridView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.GridView;

public abstract class AbsPullGridViewFragment extends AbsPullListFragment<GridView, PullGridView>
{
	public AbsPullGridViewFragment()
	{
		super();
	}

	public AbsPullGridViewFragment(int layoutResID)
	{
		super(layoutResID);
	}

	@Override
	protected PullGridView onCreatePullView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return new PullGridView(getAttachedActivity());
	}
}
