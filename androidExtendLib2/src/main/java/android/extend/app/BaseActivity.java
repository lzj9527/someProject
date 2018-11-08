package android.extend.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.extend.app.ActivityProxy.IActivityExtend;
import android.extend.app.ActivityProxy.OnActivityObserver;
import android.extend.app.ActivityProxy.State;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.widget.MenuBar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class BaseActivity extends Activity implements IActivityExtend
{
	public final String TAG = getClass().getSimpleName();

	private final ActivityProxy mActivityProxy = new ActivityProxy(this);
	protected final OnCancelListener mLoadingIndicatorDialogCancelListener = new OnCancelListener()
	{
		@Override
		public void onCancel(DialogInterface dialog)
		{
			onLoadingIndicatorDialogCancel();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mActivityProxy.onCreate(savedInstanceState);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		mActivityProxy.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		mActivityProxy.onStart();
	}

	@Override
	public void onFirstStart()
	{
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mActivityProxy.onResume();
	}

	@Override
	protected void onPostResume()
	{
		super.onPostResume();
		mActivityProxy.onPostResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mActivityProxy.onPause();
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
		mActivityProxy.onRestart();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		mActivityProxy.onStop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mActivityProxy.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		mActivityProxy.onNewIntent(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		mActivityProxy.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		mActivityProxy.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		mActivityProxy.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mActivityProxy.onConfigurationChanged(newConfig);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mActivityProxy.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onLowMemory()
	{
		LogUtil.d(TAG, "onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level)
	{
		LogUtil.d(TAG, "onTrimMemory: " + level);
		super.onTrimMemory(level);
	}

	@Override
	public void finish()
	{
		super.finish();
		mActivityProxy.overrideFinishPendingTransition();
	}

	@Override
	public void finishFromChild(Activity child)
	{
		super.finishFromChild(child);
		mActivityProxy.overrideFinishPendingTransition();
	}

	@Override
	public void finishActivity(int requestCode)
	{
		super.finishActivity(requestCode);
		mActivityProxy.overrideFinishPendingTransition();
	}

	@Override
	public void finishActivityFromChild(Activity child, int requestCode)
	{
		super.finishActivityFromChild(child, requestCode);
		mActivityProxy.overrideFinishPendingTransition();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		super.startActivityForResult(intent, requestCode);
		mActivityProxy.overrideStartPendingTransition();
	}

	@Override
	public void startActivity(Intent intent)
	{
		super.startActivity(intent);
		mActivityProxy.overrideStartPendingTransition();
	}

	@Override
	public void startActivities(Intent[] intents)
	{
		super.startActivities(intents);
		mActivityProxy.overrideStartPendingTransition();
	}

	@Override
	public void startActivityFromChild(Activity child, Intent intent, int requestCode)
	{
		super.startActivityFromChild(child, intent, requestCode);
		mActivityProxy.overrideStartPendingTransition();
	}

	@Override
	public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode)
	{
		super.startActivityFromFragment(fragment, intent, requestCode);
		mActivityProxy.overrideStartPendingTransition();
	}

	@Override
	public ActivityProxy getActivityProxy()
	{
		return mActivityProxy;
	}

	@Override
	public Activity getRootActivity()
	{
		return mActivityProxy.getRootActivity();
	}

	@Override
	public State getActivityState()
	{
		return mActivityProxy.getActivityState();
	}

	@Override
	public View getDecorView()
	{
		return mActivityProxy.getDecorView();
	}

	@Override
	public ViewGroup getContentRootView()
	{
		return mActivityProxy.getContentRootView();
	}

	@Override
	public MenuBar getMenuBar()
	{
		return mActivityProxy.getMenuBar();
	}

	@Override
	public String getString(String resName)
	{
		return mActivityProxy.getString(resName);
	}

	@Override
	public void registerObserver(OnActivityObserver observer)
	{
		mActivityProxy.registerObserver(observer);
	}

	@Override
	public void unregisterObserver(OnActivityObserver observer)
	{
		mActivityProxy.unregisterObserver(observer);
	}

	@Override
	public void setStartActivityAnimation(int enterAnim, int exitAnim)
	{
		mActivityProxy.setStartActivityAnimation(enterAnim, exitAnim);
	}

	@Override
	public void setFinishActivityAnimation(int enterAnim, int exitAnim)
	{
		mActivityProxy.setFinishActivityAnimation(enterAnim, exitAnim);
	}

	@Override
	public void showToast(String text)
	{
		mActivityProxy.showToast(text);
	}

	/**
	 * @deprecated Use {@link #showLoadingIndicator} instead.
	 */
	@Override
	public void showLoadingIndicatorDialog()
	{
		showLoadingIndicator();
	}

	// @Override
	// public void showLoadingindicatorDialog(int progress)
	// {
	// mActivityProxy.showLoadingindicatorDialog(progress);
	// }

	/**
	 * @deprecated Use {@link #showLoadingIndicator(String message)} instead.
	 */
	@Override
	public void showLoadingIndicatorDialog(String message)
	{
		showLoadingIndicator(message);
	}

	// @Override
	// public void showLoadingIndicatorDialog(String message, int theme)
	// {
	// mActivityProxy.showLoadingIndicatorDialog(message, theme);
	// }

	@Override
	public void showLoadingIndicator()
	{
		int resId = ResourceUtil.getStringId(getApplicationContext(), "default_loading_text");
		showLoadingIndicator(getString(resId));
	}

	@Override
	public void showLoadingIndicator(String message)
	{
		showLoadingIndicator(message, 0, false, mLoadingIndicatorDialogCancelListener, -1);
	}

	@Override
	public void showLoadingIndicator(int theme)
	{
		int resId = ResourceUtil.getStringId(getApplicationContext(), "default_loading_text");
		showLoadingIndicator(getString(resId), theme, false, mLoadingIndicatorDialogCancelListener, -1);
	}

	@Override
	public void showLoadingIndicator(boolean cancelable)
	{
		int resId = ResourceUtil.getStringId(getApplicationContext(), "default_loading_text");
		showLoadingIndicator(getString(resId), 0, cancelable, mLoadingIndicatorDialogCancelListener, -1);
	}

	@Override
	public void showLoadingIndicator(OnCancelListener listener)
	{
		int resId = ResourceUtil.getStringId(getApplicationContext(), "default_loading_text");
		showLoadingIndicator(getString(resId), 0, true, listener, -1);
	}

	@Override
	public void showLoadingIndicator(long showingTime)
	{
		int resId = ResourceUtil.getStringId(getApplicationContext(), "default_loading_text");
		showLoadingIndicator(getString(resId), 0, false, mLoadingIndicatorDialogCancelListener, showingTime);
	}

	@Override
	public void showLoadingIndicator(String message, int theme, boolean cancelable, OnCancelListener listener,
			long showingTime)
	{
		mActivityProxy.showLoadingIndicatorDialog(message, theme, cancelable, listener, showingTime);
	}

	/**
	 * @deprecated Use {@link #hideLoadingIndicator} instead.
	 */
	@Override
	public void hideLoadingIndicatorDialog()
	{
		hideLoadingIndicator();
	}

	@Override
	public void hideLoadingIndicator()
	{
		mActivityProxy.hideLoadingIndicatorDialog();
	}

	@Override
	public void onLoadingIndicatorDialogCancel()
	{
		LogUtil.v(TAG, "onLoadingIndicatorDialogCancel...");
	}
}
