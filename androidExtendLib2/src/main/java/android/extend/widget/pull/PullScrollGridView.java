package android.extend.widget.pull;

import android.content.Context;
import android.extend.widget.adapter.ScrollGridView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 封装了ScrollView的下拉刷新
 * 
 * @author Li Hong
 * @since 2013-8-22
 */
public class PullScrollGridView extends BasePullView<ScrollGridView>
{
	/**
	 * 构造方法
	 * 
	 * @param context context
	 */
	public PullScrollGridView(Context context)
	{
		super(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context context
	 * @param attrs attrs
	 */
	public PullScrollGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	/**
	 * 构造方法
	 * 
	 * @param context context
	 * @param attrs attrs
	 * @param defStyle defStyle
	 */
	public PullScrollGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean isHorizontalLayout()
	{
		return false;
	}

	@Override
	protected ScrollGridView createPullConentView(Context context, AttributeSet attrs)
	{
		ScrollGridView scrollView = new ScrollGridView(context);
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullRefresh()
	{
		return mPullContentView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullLoad()
	{
		View scrollViewChild = mPullContentView.getChildAt(0);
		if (null != scrollViewChild)
		{
			return mPullContentView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
		}

		return false;
	}
}
