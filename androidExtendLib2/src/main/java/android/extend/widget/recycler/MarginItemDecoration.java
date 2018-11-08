package android.extend.widget.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class MarginItemDecoration extends RecyclerView.ItemDecoration
{
	private int mMarginLeft;
	private int mMarginTop;
	private int mMarginRight;
	private int mMarginBottom;

	public void setMargin(int margin)
	{
		mMarginLeft = margin;
		mMarginTop = margin;
		mMarginRight = margin;
		mMarginBottom = margin;
	}

	public void setMargin(int left, int top, int right, int bottom)
	{
		mMarginLeft = left;
		mMarginTop = top;
		mMarginRight = right;
		mMarginBottom = bottom;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state)
	{
		outRect.set(mMarginLeft, mMarginTop, mMarginRight, mMarginBottom);
	}
}
