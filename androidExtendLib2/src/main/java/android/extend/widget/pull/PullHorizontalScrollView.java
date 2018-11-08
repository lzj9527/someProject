package android.extend.widget.pull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class PullHorizontalScrollView extends BasePullView<HorizontalScrollView>
{
	private LinearLayout mViewContainer;

	/**
	 * 构造方法
	 * 
	 * @param context context
	 */
	public PullHorizontalScrollView(Context context)
	{
		super(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context context
	 * @param attrs attrs
	 */
	public PullHorizontalScrollView(Context context, AttributeSet attrs)
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
	public PullHorizontalScrollView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public LinearLayout getViewContainer()
	{
		return mViewContainer;
	}

	@Override
	protected boolean isHorizontalLayout()
	{
		return true;
	}

	@Override
	protected HorizontalScrollView createPullConentView(Context context, AttributeSet attrs)
	{
		HorizontalScrollView scrollView = new HorizontalScrollView(context);
		mViewContainer = new LinearLayout(context);
		mViewContainer.setOrientation(LinearLayout.HORIZONTAL);
		scrollView.addView(mViewContainer, LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		return scrollView;
	}

	@Override
	protected boolean isReadyForPullRefresh()
	{
		return mPullContentView.getScrollX() == 0;
	}

	@Override
	protected boolean isReadyForPullLoad()
	{
		View scrollViewChild = mPullContentView.getChildAt(0);
		if (null != scrollViewChild)
		{
			return mPullContentView.getScrollX() >= (scrollViewChild.getWidth() - getWidth());
		}

		return false;
	}
}
