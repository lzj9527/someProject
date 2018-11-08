package android.extend.widget.pull;

import android.content.Context;
import android.extend.widget.ExtendViewPager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class PullViewPager extends BasePullView<ViewPager>
{
	private ViewPager mViewPager;

	public PullViewPager(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public PullViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullViewPager(Context context)
	{
		super(context);
	}

	@Override
	protected boolean isHorizontalLayout()
	{
		return true;
	}

	@Override
	protected ViewPager createPullConentView(Context context, AttributeSet attrs)
	{
		mViewPager = new ExtendViewPager(context);
		mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return mViewPager;
	}

	@Override
	protected boolean isReadyForPullRefresh()
	{
		return isFirstItemVisible();
	}

	@Override
	protected boolean isReadyForPullLoad()
	{
		return isLastItemVisible();
	}

	/**
	 * 判断第一个child是否完全显示出来
	 * 
	 * @return true完全显示出来，否则false
	 */
	private boolean isFirstItemVisible()
	{
		return mViewPager.getCurrentItem() == 0;
	}

	/**
	 * 判断最后一个child是否完全显示出来
	 * 
	 * @return true完全显示出来，否则false
	 */
	private boolean isLastItemVisible()
	{
		final PagerAdapter adapter = mViewPager.getAdapter();
		return mViewPager.getCurrentItem() == adapter.getCount() - 1;
	}
}
