package android.extend.widget.adapter;

import android.content.Context;
import android.extend.util.LogUtil;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class HorizontalListView extends ListView
{
	public HorizontalListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public HorizontalListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public HorizontalListView(Context context)
	{
		super(context);
	}

	@Override
	protected void initLayout()
	{
		setOrientationInner(LinearLayout.HORIZONTAL);

		notifyInitContent();
	}

	@Override
	protected android.view.ViewGroup.LayoutParams generateSelectorLayoutParams()
	{
		return new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	}

	@Override
	protected View generateDivider()
	{
		return createVerticalDivider();
	}

	@Override
	protected void changeVerticalDivider()
	{
		int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			View view = getChildAt(i);
			if (view.getId() != mDividerID)
				continue;
			changeVerticalDividerImpl(view);
		}
	}

	@Override
	protected void changeHorizontalDivider()
	{
		LogUtil.w(TAG, "No HorizontalDivider...");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		try
		{
			super.onSizeChanged(w, h, oldw, oldh);
			// LogUtil.d(TAG, "onSizeChanged: " + w + "; " + h + "; " + oldw + "; " + oldh);
			if (h != oldh)
			{
				notifyUpdateContent();
			}
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}
}
