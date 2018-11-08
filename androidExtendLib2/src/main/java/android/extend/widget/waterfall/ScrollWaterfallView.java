package android.extend.widget.waterfall;

import android.content.Context;
import android.extend.util.LogUtil;
import android.extend.util.ViewTools;
import android.extend.widget.adapter.IAdapterView;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ScrollWaterfallView extends ScrollView implements IAdapterView<BaseWaterfallAdapter<?>>
{
	public final String TAG = getClass().getSimpleName();

	private WaterfallView mWaterfallView;

	public ScrollWaterfallView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public ScrollWaterfallView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public ScrollWaterfallView(Context context)
	{
		super(context);
		init();
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	private void init()
	{
		try
		{
			ViewTools.removeAllViewsInChildren(this);

			mWaterfallView = new WaterfallView(getContext());
			mWaterfallView.setBackgroundColor(Color.TRANSPARENT);
			addView(mWaterfallView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	public WaterfallView getWaterfallView()
	{
		return mWaterfallView;
	}

	@Override
	public void setAdapter(BaseWaterfallAdapter<?> adapter)
	{
		mWaterfallView.setAdapter(adapter);
	}

	@Override
	public BaseWaterfallAdapter<?> getAdapter()
	{
		return mWaterfallView.getAdapter();
	}

	@Override
	public void setVerticalDividerWidth(int width)
	{
		mWaterfallView.setVerticalDividerWidth(width);
	}

	@Override
	public void setVerticalDividerResource(int resId)
	{
		mWaterfallView.setVerticalDividerResource(resId);
	}

	@Override
	public void setVerticalDividerColor(int color)
	{
		mWaterfallView.setVerticalDividerColor(color);
	}

	@Override
	public void setHorizontalDividerHeight(int height)
	{
		mWaterfallView.setHorizontalDividerHeight(height);
	}

	@Override
	public void setHorizontalDividerResource(int resId)
	{
		mWaterfallView.setHorizontalDividerResource(resId);
	}

	@Override
	public void setHorizontalDividerColor(int color)
	{
		mWaterfallView.setHorizontalDividerColor(color);
	}

	@Override
	public void setSelector(int resId)
	{
		mWaterfallView.setSelector(resId);
	}

	@Override
	public void setSelectorPadding(int padding)
	{
		mWaterfallView.setSelectorPadding(padding);
	}

	@Override
	public void setSelectable(boolean selectable)
	{
		mWaterfallView.setSelectable(selectable);
	}

	@Override
	public void setSelection(int position)
	{
		mWaterfallView.setSelection(position);
	}

	@Override
	public View getSelectedView()
	{
		return mWaterfallView.getSelectedView();
	}

	@Override
	public int getSelectedPosition()
	{
		return mWaterfallView.getSelectedPosition();
	}

	@Override
	public View getItemView(int position)
	{
		return mWaterfallView.getItemView(position);
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mWaterfallView.setOnItemClickListener(listener);
	}

	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener)
	{
		mWaterfallView.setOnItemLongClickListener(listener);
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener)
	{
		mWaterfallView.setOnItemSelectedListener(listener);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		// LogUtil.d(TAG, "onLayout: " + changed);
		if (changed)
		{
			try
			{
				mWaterfallView.notifyComputeVisibleContent(false);
			}
			catch (Exception e)
			{
				LogUtil.w(TAG, "", e);
			}
		}
	}

	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		// LogUtil.d(TAG, "onScrollChanged: " + l + "; " + t + "; " + oldl + "; " + oldt);
		if (l != oldl || t != oldt)
		{
			try
			{
				mWaterfallView.notifyComputeVisibleContent(false);
			}
			catch (Exception e)
			{
				LogUtil.w(TAG, "", e);
			}
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		// LogUtil.d(TAG, "onSizeChanged: " + w + "; " + h + "; " + oldw + "; " + oldh);
		if (w != oldw)
		{
			try
			{
				mWaterfallView.notifyUpdateContent();
			}
			catch (Exception e)
			{
				LogUtil.w(TAG, "", e);
			}
		}
	}
}
