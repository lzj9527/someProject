package android.extend.widget;

import android.content.Intent;
import android.view.View.OnClickListener;

public interface ITitleAction
{
	public void setTitle(CharSequence text);

	public void setTitle(int textResID);

	public void setTitleColor(int textColor);

	public void setTitleSize(float textSize);

	public void setTitleImage(int imageResID);

	public void setLeftButton(int resID, OnClickListener listener);

	public void setLeftButton(int resID, final Intent intent);

	public void setLeftButtonVisible(boolean visible);

	public void setRightButton(int resID, OnClickListener listener);

	public void setRightButton(int resID, final Intent intent);

	public void setRightButtonVisible(boolean visible);
}
