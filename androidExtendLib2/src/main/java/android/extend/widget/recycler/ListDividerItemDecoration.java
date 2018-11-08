package android.extend.widget.recycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.State;
import android.view.View;

public class ListDividerItemDecoration extends RecyclerView.ItemDecoration
{
	private static final int[] ATTRS = new int[] { android.R.attr.listDivider };

	public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

	public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

	public final String TAG = getClass().getSimpleName();

	private Drawable mDivider;
	private int mDividerWidth;
	private int mDividerHeight;

	private int mOrientation;

	public ListDividerItemDecoration(Context context, int orientation)
	{
		final TypedArray a = context.obtainStyledAttributes(ATTRS);
		mDivider = a.getDrawable(0);
		a.recycle();
		setOrientation(orientation);
	}

	public ListDividerItemDecoration(Drawable divider, int orientation)
	{
		setDivider(divider);
		setOrientation(orientation);
	}

	public void setOrientation(int orientation)
	{
		if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST)
		{
			throw new IllegalArgumentException("invalid orientation");
		}
		mOrientation = orientation;
	}

	public void setDivider(Drawable divider)
	{
		mDivider = divider;
	}

	public void setDividerWidth(int width)
	{
		mDividerWidth = width;
	}

	public void setDividerHeight(int height)
	{
		mDividerHeight = height;
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, State state)
	{
		if (mDivider == null)
			return;
		if (mOrientation == VERTICAL_LIST)
		{
			drawVertical(c, parent);
		}
		else
		{
			drawHorizontal(c, parent);
		}
	}

	// @Override
	// public void onDraw(Canvas c, RecyclerView parent)
	// {
	// if (mDivider == null)
	// return;
	// if (mOrientation == VERTICAL_LIST)
	// {
	// drawVertical(c, parent);
	// }
	// else
	// {
	// drawHorizontal(c, parent);
	// }
	// }

	private void drawVertical(Canvas c, RecyclerView parent)
	{
		final int left = parent.getPaddingLeft();
		final int right = parent.getWidth() - parent.getPaddingRight();

		final int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
			final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
			int height = mDividerHeight;
			if (mDivider.getIntrinsicHeight() > 0)
				height = mDivider.getIntrinsicHeight();
			final int bottom = top + height;
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	private void drawHorizontal(Canvas c, RecyclerView parent)
	{
		final int top = parent.getPaddingTop();
		final int bottom = parent.getHeight() - parent.getPaddingBottom();

		final int childCount = parent.getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			final View child = parent.getChildAt(i);
			final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
			final int left = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
			int width = mDividerWidth;
			if (mDivider.getIntrinsicWidth() > 0)
				width = mDivider.getIntrinsicWidth();
			final int right = left + width;
			mDivider.setBounds(left, top, right, bottom);
			mDivider.draw(c);
		}
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, final RecyclerView parent, State state)
	{
		if (mDivider == null)
		{
			if (mOrientation == VERTICAL_LIST)
			{
				outRect.set(0, 0, 0, mDividerHeight);
			}
			else
			{
				outRect.set(0, 0, mDividerWidth, 0);
			}
		}
		else
		{
			if (mOrientation == VERTICAL_LIST)
			{
				int height = mDividerHeight;
				if (mDivider.getIntrinsicHeight() > 0)
					height = mDivider.getIntrinsicHeight();
				outRect.set(0, 0, 0, height);
			}
			else
			{
				int width = mDividerWidth;
				if (mDivider.getIntrinsicWidth() > 0)
					width = mDivider.getIntrinsicWidth();
				outRect.set(0, 0, width, 0);
			}
		}
	}

	// @Override
	// public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent)
	// {
	// if (mDivider == null)
	// {
	// if (mOrientation == VERTICAL_LIST)
	// {
	// outRect.set(0, 0, 0, mDividerHeight);
	// }
	// else
	// {
	// outRect.set(0, 0, mDividerWidth, 0);
	// }
	// }
	// else
	// {
	// if (mOrientation == VERTICAL_LIST)
	// {
	// int height = mDividerHeight;
	// if (mDivider.getIntrinsicHeight() > 0)
	// height = mDivider.getIntrinsicHeight();
	// outRect.set(0, 0, 0, height);
	// }
	// else
	// {
	// int width = mDividerWidth;
	// if (mDivider.getIntrinsicWidth() > 0)
	// width = mDivider.getIntrinsicWidth();
	// outRect.set(0, 0, width, 0);
	// }
	// }
	// }
}
