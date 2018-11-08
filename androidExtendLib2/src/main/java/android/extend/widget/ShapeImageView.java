package android.extend.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.extend.util.BitmapUtils;
import android.extend.util.BitmapUtils.ClipType;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class ShapeImageView extends ExtendImageView
{
	public enum Shape
	{
		CIRCLE, OVAL, ROUNDRECT,
	}

	private static Shape[] sShapeArray = { Shape.CIRCLE, Shape.OVAL, Shape.ROUNDRECT };
	private static ClipType[] sClipTypeArray = { ClipType.CENTER, ClipType.START, ClipType.END };

	private Shape mShape;
	private BitmapUtils.ClipType mClipType;
	private int mRadiusX;
	private int mRadiusY;
	private Bitmap mOriginBitmap;

	public ShapeImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		try
		{
			int[] styleableArray = ResourceUtil.getStyleableArray(getContext(), "ShapeImageView");
			TypedArray a = context.obtainStyledAttributes(attrs, styleableArray, defStyle, 0);

			int styleable = ResourceUtil.getStyleableId(getContext(), "ShapeImageView_radiusX");
			mRadiusX = a.getDimensionPixelSize(styleable, 0);
			styleable = ResourceUtil.getStyleableId(getContext(), "ShapeImageView_radiusY");
			mRadiusY = a.getDimensionPixelSize(styleable, 0);

			styleable = ResourceUtil.getStyleableId(getContext(), "ShapeImageView_clipType");
			int index = a.getInt(styleable, 0);
			mClipType = sClipTypeArray[index];

			styleable = ResourceUtil.getStyleableId(getContext(), "ShapeImageView_imageShape");
			index = a.getInt(styleable, 0);
			setShape(sShapeArray[index]);

			a.recycle();
			LogUtil.v(TAG, "mShape=" + mShape + "; mClipType=" + mClipType + "; mRadiusX=" + mRadiusX + "; mRadiusY="
					+ mRadiusY);
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	public ShapeImageView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ShapeImageView(Context context)
	{
		super(context);
	}

	@Override
	public void setImageBitmap(Bitmap bitmap)
	{
		mOriginBitmap = bitmap;
		bitmap = createShapeBitmap(mOriginBitmap);
		if (bitmap != null)
			super.setImageDrawable(new BitmapDrawable(bitmap));
		else
			super.setImageDrawable(null);
	}

	@Override
	public void setImageDrawable(Drawable drawable)
	{
		mOriginBitmap = BitmapUtils.drawableToBitmap(drawable);
		Bitmap bitmap = createShapeBitmap(mOriginBitmap);
		if (bitmap != null)
			super.setImageDrawable(new BitmapDrawable(bitmap));
		else
			super.setImageDrawable(null);
	}

	@Override
	public void setImageResource(int resId)
	{
		Drawable drawable = getResources().getDrawable(resId);
		mOriginBitmap = BitmapUtils.drawableToBitmap(drawable);
		Bitmap bitmap = createShapeBitmap(mOriginBitmap);
		if (bitmap != null)
			super.setImageDrawable(new BitmapDrawable(bitmap));
		else
			super.setImageDrawable(null);
	}

	public void setShape(Shape shape)
	{
		LogUtil.d(TAG, "setShape: " + shape + "; " + mShape);
		if (mShape == shape)
			return;
		mShape = shape;
		if (mOriginBitmap != null && !mOriginBitmap.isRecycled())
		{
			setImageBitmap(mOriginBitmap);
		}
	}

	public void setClipType(ClipType clipType)
	{
		LogUtil.d(TAG, "setShape: " + clipType + "; " + mClipType);
		if (mClipType == clipType)
			return;
		mClipType = clipType;
		if (mShape == Shape.CIRCLE && mOriginBitmap != null && !mOriginBitmap.isRecycled())
		{
			setImageBitmap(mOriginBitmap);
		}
	}

	protected Bitmap createShapeBitmap(Bitmap bitmap)
	{
		if (bitmap == null || mShape == null)
			return null;
		LogUtil.v(TAG, "createShapeBitmap: " + bitmap + "; " + bitmap.getWidth() + "x" + bitmap.getHeight() + "; "
				+ mShape + "; " + mClipType);
		Bitmap result = null;
		try
		{
			switch (mShape)
			{
				case CIRCLE:
					result = BitmapUtils.createCircleBitmap(bitmap, mClipType);
					break;
				case OVAL:
					result = BitmapUtils.createOvalBitmap(bitmap);
					break;
				case ROUNDRECT:
					result = BitmapUtils.createRoundRectBitmap(bitmap, mRadiusX, mRadiusX);
					break;
			}
		}
		catch (Throwable th)
		{
			th.printStackTrace();
		}
		return result;
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		if (mAutoRecyleBitmap || !mUseBitmapCache)
			if (mOriginBitmap != null && !mOriginBitmap.isRecycled())
				mOriginBitmap.recycle();
		mOriginBitmap = null;
	}
}
