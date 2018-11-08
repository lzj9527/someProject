package android.extend.widget.pull;

import android.content.Context;
import android.extend.widget.waterfall.ScrollWaterfallView;
import android.util.AttributeSet;
import android.view.View;

/**
 * 封装了WaterfallView的下拉刷新
 */
public class PullScrollWaterfallView extends BasePullView<ScrollWaterfallView>
{
	/**
	 * 构造方法
	 * 
	 * @param context context
	 */
	public PullScrollWaterfallView(Context context)
	{
		super(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context context
	 * @param attrs attrs
	 */
	public PullScrollWaterfallView(Context context, AttributeSet attrs)
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
	public PullScrollWaterfallView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	protected boolean isHorizontalLayout()
	{
		return false;
	}

	@Override
	protected ScrollWaterfallView createPullConentView(Context context, AttributeSet attrs)
	{
		ScrollWaterfallView waterfallView = new ScrollWaterfallView(context);
		return waterfallView;
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
