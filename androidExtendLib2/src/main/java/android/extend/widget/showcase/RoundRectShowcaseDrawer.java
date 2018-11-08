package android.extend.widget.showcase;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class RoundRectShowcaseDrawer implements IShowcaseDrawer
{
	private RectF mRect;
	private float mXRadius;
	private float mYRadius;

	public RoundRectShowcaseDrawer(int x, int y, int width, int height, int xRadius, int yRadius)
	{
		mRect = new RectF(x, y, x + width, y + height);
		mXRadius = xRadius;
		mYRadius = yRadius;
	}

	public RoundRectShowcaseDrawer(Rect rect, int xRadius, int yRadius)
	{
		mRect = new RectF(rect);
		mXRadius = xRadius;
		mYRadius = yRadius;
	}

	public RoundRectShowcaseDrawer(float x, float y, float width, float height, float xRadius, float yRadius)
	{
		mRect = new RectF(x, y, x + width, y + height);
		mXRadius = xRadius;
		mYRadius = yRadius;
	}

	public RoundRectShowcaseDrawer(RectF rect, float xRadius, float yRadius)
	{
		mRect = rect;
		mXRadius = xRadius;
		mYRadius = yRadius;
	}

	@Override
	public void draw(Canvas canvas, Paint paint)
	{
		canvas.drawRoundRect(mRect, mXRadius, mYRadius, paint);
	}

	@Override
	public RectF getBounds()
	{
		return mRect;
	}
}
