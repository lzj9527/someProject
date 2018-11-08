package android.extend.widget.adapter;

import android.content.Context;
import android.extend.util.LogUtil;
import android.extend.util.ViewTools;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ScrollGridView extends ScrollView implements IAdapterView<BaseGridAdapter<?>>
{
	public final String TAG = getClass().getSimpleName();

	private GridView mGridView;

	public ScrollGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public ScrollGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public ScrollGridView(Context context)
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

			mGridView = new GridView(getContext());
			mGridView.setBackgroundColor(Color.TRANSPARENT);
			addView(mGridView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	public GridView getGridView()
	{
		return mGridView;
	}

	public void setNumColumns(int numColumns)
	{
		mGridView.setNumColumns(numColumns);
	}

	@Override
	public void setAdapter(BaseGridAdapter<?> adapter)
	{
		mGridView.setAdapter(adapter);
	}

	@Override
	public BaseGridAdapter<?> getAdapter()
	{
		return mGridView.getAdapter();
	}

	@Override
	public void setVerticalDividerWidth(int width)
	{
		mGridView.setVerticalDividerWidth(width);
	}

	@Override
	public void setVerticalDividerResource(int resId)
	{
		mGridView.setVerticalDividerResource(resId);
	}

	@Override
	public void setVerticalDividerColor(int color)
	{
		mGridView.setVerticalDividerColor(color);
	}

	@Override
	public void setHorizontalDividerHeight(int height)
	{
		mGridView.setHorizontalDividerHeight(height);
	}

	@Override
	public void setHorizontalDividerResource(int resId)
	{
		mGridView.setHorizontalDividerResource(resId);
	}

	@Override
	public void setHorizontalDividerColor(int color)
	{
		mGridView.setHorizontalDividerColor(color);
	}

	@Override
	public void setSelector(int resId)
	{
		mGridView.setSelector(resId);
	}

	@Override
	public void setSelectorPadding(int padding)
	{
		mGridView.setSelectorPadding(padding);
	}

	@Override
	public void setSelectable(boolean selectable)
	{
		mGridView.setSelectable(selectable);
	}

	@Override
	public void setSelection(int position)
	{
		mGridView.setSelection(position);
	}

	@Override
	public View getSelectedView()
	{
		return mGridView.getSelectedView();
	}

	@Override
	public int getSelectedPosition()
	{
		return mGridView.getSelectedPosition();
	}

	@Override
	public View getItemView(int position)
	{
		return mGridView.getItemView(position);
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mGridView.setOnItemClickListener(listener);
	}

	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener)
	{
		mGridView.setOnItemLongClickListener(listener);
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener)
	{
		mGridView.setOnItemSelectedListener(listener);
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
				mGridView.notifyComputeVisibleContent(false);
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
				mGridView.notifyComputeVisibleContent(false);
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
				mGridView.notifyUpdateContent();
			}
			catch (Exception e)
			{
				LogUtil.w(TAG, "", e);
			}
		}
	}
}
