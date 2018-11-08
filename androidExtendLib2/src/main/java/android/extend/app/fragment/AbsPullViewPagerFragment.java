package android.extend.app.fragment;

import java.util.List;

import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.BasePagerAdapter;
import android.extend.widget.pull.PullViewPager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract class AbsPullViewPagerFragment extends AbsPullViewFragment<PullViewPager>
{
	protected BasePagerAdapter<AbsAdapterItem> mPageAdapter;

	public AbsPullViewPagerFragment()
	{
		super();
	}

	public AbsPullViewPagerFragment(int layoutResID)
	{
		super(layoutResID);
	}

	@Override
	protected PullViewPager onCreatePullView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return new PullViewPager(getAttachedActivity());
	}

	public PagerAdapter getPagerAdapter()
	{
		return mPageAdapter;
	}

	@Override
	protected void onEnsureAdapter(PullViewPager pullView)
	{
		ViewPager viewPager = pullView.getPullContentView();
		mPageAdapter = new BasePagerAdapter<AbsAdapterItem>();
		viewPager.setAdapter(mPageAdapter);
	}

	@Override
	protected void onClearAdapterItems()
	{
		mPageAdapter.clear();
	}

	@Override
	protected void onAddAdapterItems(List<AbsAdapterItem> itemList)
	{
		mPageAdapter.addItems(itemList);
	}
}
