package android.extend.widget.waterfall;

import android.content.Context;
import android.extend.loader.BitmapLoader.DecodeMode;
import android.extend.util.BitmapUtils;
import android.extend.util.LogUtil;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class HorizontalWaterfallAdapterItem extends WaterfallAdapterItem
{
	private int mParentHeight;
	private int mImageViewHeight;

	public HorizontalWaterfallAdapterItem(Context context, WaterfallData data, int layoutResID)
	{
		super(context, data, layoutResID);
	}

	public HorizontalWaterfallAdapterItem(Context context, WaterfallData data, int layoutResID,
			OnItemClickListener clickListener)
	{
		super(context, data, layoutResID, clickListener);
	}

	@Override
	public void onUpdateView(final View view, final int position, final ViewGroup parent)
	{
		mParentHeight = parent.getHeight();
	}

	@Override
	protected void ensureViewLayoutParams(View view)
	{
		LayoutParams params = view.getLayoutParams();
		if (params == null)
		{
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		}
		else
		{
			params.width = LayoutParams.WRAP_CONTENT;
			params.height = LayoutParams.MATCH_PARENT;
		}
		view.setLayoutParams(params);
	}

	@Override
	protected void updateImageViewDimension(ImageView imageView)
	{
		int height = imageView.getHeight();
		if (mImageViewHeight == height)
		{
			return;
		}
		mImageViewHeight = height;
		int width = BitmapUtils.computeOutWidth(mData.imageWidth, mData.imageHeight, height);
		LayoutParams params = imageView.getLayoutParams();
		if (params.width != width || params.height != LayoutParams.MATCH_PARENT)
		{
			params.width = width;
			params.height = LayoutParams.MATCH_PARENT;
			LogUtil.i(TAG, "updateImageViewDimension: " + height + "x" + height + "; " + imageView);
			imageView.setLayoutParams(params);
		}
	}

	@Override
	public DecodeMode getDecodeMode()
	{
		return DecodeMode.FIT_HEIGHT;
	}

	@Override
	public int getOutHeight()
	{
		return mParentHeight;
	}
}
