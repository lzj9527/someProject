package android.extend.widget.showcase;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RectangleShowcaseDrawer implements IShowcaseDrawer
{
	private RectF mRect;

	public RectangleShowcaseDrawer(int x, int y, int width, int height)
	{
		mRect = new RectF(x, y, x + width, y + height);
	}

	public RectangleShowcaseDrawer(Rect rect)
	{
		mRect = new RectF(rect);
	}

	public RectangleShowcaseDrawer(float x, float y, float width, float height)
	{
		mRect = new RectF(x, y, x + width, y + height);
	}

	public RectangleShowcaseDrawer(RectF rect)
	{
		mRect = rect;
	}

	@Override
	public void draw(Canvas canvas, Paint paint)
	{
		canvas.drawRect(mRect, paint);
	}

	@Override
	public RectF getBounds()
	{
		return mRect;
	}
}
