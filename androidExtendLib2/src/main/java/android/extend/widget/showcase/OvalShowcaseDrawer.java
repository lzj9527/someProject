package android.extend.widget.showcase;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class OvalShowcaseDrawer implements IShowcaseDrawer
{
	private RectF mOval;

	public OvalShowcaseDrawer(int cx, int cy, int xRadius, int yRadius)
	{
		mOval = new RectF(cx - xRadius, cy - yRadius, cx + xRadius, cy + yRadius);
	}

	public OvalShowcaseDrawer(Rect oval)
	{
		mOval = new RectF(oval);
	}

	public OvalShowcaseDrawer(float cx, float cy, float xRadius, float yRadius)
	{
		mOval = new RectF(cx - xRadius, cy - yRadius, cx + xRadius, cy + yRadius);
	}

	public OvalShowcaseDrawer(RectF oval)
	{
		mOval = oval;
	}

	@Override
	public void draw(Canvas canvas, Paint paint)
	{
		canvas.drawOval(mOval, paint);
	}

	@Override
	public RectF getBounds()
	{
		return mOval;
	}
}
