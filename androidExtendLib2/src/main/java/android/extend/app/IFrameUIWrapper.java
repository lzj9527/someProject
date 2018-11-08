package android.extend.app;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public interface IFrameUIWrapper
{
	public void setBackgroundColor(int color);

	public void setBackgroundResource(int resid);

	public void setBackgroundDrawable(Drawable background);

	public void setContentView(int layoutResID);

	public void setContentView(View view);

	public void setContentView(View view, LayoutParams params);

	public View getContentView();

	public void setLoadingIndicator(int layoutResID);

	public void setLoadingIndicator(View view);

	public void showLoadingIndicator();

	public void showLoadingIndicator(String message);

	public void hideLoadingIndicator();

	public void setTitleBar(int layoutResID);

	public void setTitleBar(View view);

	public View getTitleBar();

	public void showTitleBar();

	public void hideTitleBar();

	public void setSubTitleBar(int layoutResID);

	public void setSubTitleBar(View view);

	public View getSubTitleBar();

	public void showSubTitleBar();

	public void hideSubTitleBar();

	public void setBottomBar(int layoutResID);

	public void setBottomBar(View view);

	public View getBottomBar();

	public void showBottomBar();

	public void hideBottomBar();

	public void onTitleBarChanged();

	public void onSubTitleBarChanged();

	public void onContentViewChanged();

	public void onBottomBarChanged();
}
