package android.extend.widget.adapter;

import android.content.Context;
import android.extend.util.LogUtil;
import android.extend.util.ViewTools;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ScrollListView extends ScrollView implements IAdapterView<BaseAdapter<?>>
{
	public final String TAG = getClass().getSimpleName();

	private ListView mListView;

	public ScrollListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public ScrollListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public ScrollListView(Context context)
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

			mListView = new ListView(getContext());
			mListView.setBackgroundColor(Color.TRANSPARENT);
			addView(mListView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	public ListView getListView()
	{
		return mListView;
	}

	@Override
	public void setAdapter(BaseAdapter<?> adapter)
	{
		mListView.setAdapter(adapter);
	}

	@Override
	public BaseAdapter<?> getAdapter()
	{
		return mListView.getAdapter();
	}

	@Override
	public void setVerticalDividerWidth(int width)
	{
		LogUtil.w(TAG, "Unsupport VerticalDivider...");
	}

	@Override
	public void setVerticalDividerResource(int resId)
	{
		LogUtil.w(TAG, "Unsupport VerticalDivider...");
	}

	@Override
	public void setVerticalDividerColor(int color)
	{
		LogUtil.w(TAG, "Unsupport VerticalDivider...");
	}

	@Override
	public void setHorizontalDividerHeight(int height)
	{
		mListView.setHorizontalDividerHeight(height);
	}

	@Override
	public void setHorizontalDividerResource(int resId)
	{
		mListView.setHorizontalDividerResource(resId);
	}

	@Override
	public void setHorizontalDividerColor(int color)
	{
		mListView.setHorizontalDividerColor(color);
	}

	@Override
	public void setSelector(int resId)
	{
		mListView.setSelector(resId);
	}

	@Override
	public void setSelectorPadding(int padding)
	{
		mListView.setSelectorPadding(padding);
	}

	@Override
	public void setSelectable(boolean selectable)
	{
		mListView.setSelectable(selectable);
	}

	@Override
	public void setSelection(int position)
	{
		mListView.setSelection(position);
	}

	@Override
	public View getSelectedView()
	{
		return mListView.getSelectedView();
	}

	@Override
	public int getSelectedPosition()
	{
		return mListView.getSelectedPosition();
	}

	@Override
	public View getItemView(int position)
	{
		return mListView.getItemView(position);
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener)
	{
		mListView.setOnItemClickListener(listener);
	}

	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener)
	{
		mListView.setOnItemLongClickListener(listener);
	}

	@Override
	public void setOnItemSelectedListener(OnItemSelectedListener listener)
	{
		mListView.setOnItemSelectedListener(listener);
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
				mListView.notifyComputeVisibleContent(false);
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
				mListView.notifyComputeVisibleContent(false);
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
				mListView.notifyUpdateContent();
			}
			catch (Exception e)
			{
				LogUtil.w(TAG, "", e);
			}
		}
	}
}
