package android.extend.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * 使用像素点来判断一张图片是否被点击的ImageButton
 * 
 * 可判断不规则图形的点击事件
 * */
public class PixelImageButton extends ImageButton
{
	public PixelImageButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public PixelImageButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PixelImageButton(Context context)
	{
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (!isClickable() || event.getAction() != MotionEvent.ACTION_DOWN)
		{
			return super.onTouchEvent(event);
		}
		else if (isTouchPixelInImage(event.getX(), event.getY()))
		{
			return super.onTouchEvent(event);
		}
		else
		{
			return false;
		}
	}

	protected boolean isTouchPixelInImage(float touchX, float touchY)
	{
		Drawable drawable = super.getDrawable();
		if (drawable instanceof BitmapDrawable)
		{
			Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
			int x = (int)touchX;
			int y = (int)touchY;
			if (x < 0 || x >= getWidth())
				return false;
			if (y < 0 || y >= getHeight())
				return false;
			try
			{
				int pixel = bitmap.getPixel(x, y);
				if ((pixel & 0xff000000) != 0)
				{ // 点在非透明区
					return true;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}
}
