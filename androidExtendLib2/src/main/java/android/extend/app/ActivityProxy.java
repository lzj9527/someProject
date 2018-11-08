package android.extend.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.extend.BasicConfig;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.util.ViewTools;
import android.extend.widget.MenuBar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;

public final class ActivityProxy
{
	public enum State
	{
		UNKNOW, CREATED, STARTED, RESUMED, PAUSED, STOPED, DESTROYED,
	}

	public interface IActivityExtend
	{
		void onFirstStart();

		ActivityProxy getActivityProxy();

		Activity getRootActivity();

		State getActivityState();

		View getDecorView();

		ViewGroup getContentRootView();

		MenuBar getMenuBar();

		String getString(String resName);

		void registerObserver(OnActivityObserver observer);

		void unregisterObserver(OnActivityObserver observer);

		void setStartActivityAnimation(int enterAnim, int exitAnim);

		void setFinishActivityAnimation(int enterAnim, int exitAnim);

		void showToast(String text);

		/**
		 * @deprecated Use {@link #showLoadingIndicator} instead.
		 */
		void showLoadingIndicatorDialog();

		/**
		 * @deprecated Use {@link #showLoadingIndicator(String message)} instead.
		 */
		void showLoadingIndicatorDialog(String message);

		void showLoadingIndicator();

		void showLoadingIndicator(String message);

		void showLoadingIndicator(int theme);

		void showLoadingIndicator(boolean cancelable);

		void showLoadingIndicator(OnCancelListener listener);

		void showLoadingIndicator(long showingTime);

		void showLoadingIndicator(String message, int theme, boolean cancelable, OnCancelListener listener,
				long showingTime);

		/**
		 * @deprecated Use {@link #hideLoadingIndicator} instead.
		 */
		void hideLoadingIndicatorDialog();

		void hideLoadingIndicator();

		void onLoadingIndicatorDialogCancel();
	}

	/**
	 * ActivityObserver
	 * 
	 * 用于在Activity外部监听Activity生命周期及状态变化等等事件
	 * */
	public interface OnActivityObserver
	{
		void onActivityCreate(Activity activity, Bundle savedInstanceState);

		void onActivityStart(Activity activity);

		void onActivityResume(Activity activity);

		void onActivityPause(Activity activity);

		void onActivityRestart(Activity activity);

		void onActivityStop(Activity activity);

		void onActivityDestroy(Activity activity);

		void onActivityNewIntent(Activity activity, Intent intent);

		void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

		void onActivityRestoreInstanceState(Activity activity, Bundle savedInstanceState);

		void onActivitySaveInstanceState(Activity activity, Bundle outState);

		void onActivityConfigurationChanged(Configuration newConfig);

		void onActivityWindowFocusChanged(boolean hasFocus);
	}

	static class ActivityObservable
	{
		private Activity mActivity;
		private List<OnActivityObserver> mObservers = new ArrayList<OnActivityObserver>();

		ActivityObservable(Activity activity)
		{
			mActivity = activity;
		}

		void registerObserver(OnActivityObserver observer)
		{
			synchronized (mObservers)
			{
				if (!mObservers.contains(observer))
					mObservers.add(observer);
			}
		}

		void unregisterObserver(OnActivityObserver observer)
		{
			synchronized (mObservers)
			{
				mObservers.remove(observer);
			}
		}

		void clearObservers()
		{
			synchronized (mObservers)
			{
				mObservers.clear();
			}
		}

		void notifyActivityCreate(Bundle savedInstanceState)
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityCreate(mActivity, savedInstanceState);
				}
			}
		}

		void notifyActivityStart()
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityStart(mActivity);
				}
			}
		}

		void notifyActivityResume()
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityResume(mActivity);
				}
			}
		}

		void notifyActivityPause()
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityPause(mActivity);
				}
			}
		}

		void notifyActivityRestart()
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityRestart(mActivity);
				}
			}
		}

		void notifyActivityStop()
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityStop(mActivity);
				}
			}
		}

		void notifyActivityDestroy()
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityDestroy(mActivity);
				}
			}
		}

		void notifyActivityNewIntent(Intent intent)
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityNewIntent(mActivity, intent);
				}
			}
		}

		void notifyActivityResult(int requestCode, int resultCode, Intent data)
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityResult(mActivity, requestCode, resultCode, data);
				}
			}
		}

		void notifyActivityRestoreInstanceState(Bundle savedInstanceState)
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityRestoreInstanceState(mActivity, savedInstanceState);
				}
			}
		}

		void notifyActivitySaveInstanceState(Bundle outState)
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivitySaveInstanceState(mActivity, outState);
				}
			}
		}

		void notifyActivityConfigurationChanged(Configuration newConfig)
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityConfigurationChanged(newConfig);
				}
			}
		}

		void notifyActivityWindowFocusChanged(boolean hasFocus)
		{
			synchronized (mObservers)
			{
				for (OnActivityObserver observer : mObservers)
				{
					observer.onActivityWindowFocusChanged(hasFocus);
				}
			}
		}
	}

	public final String TAG;
	private Activity mActivity;
	private MenuBar mMenuBar;
	private final ActivityObservable mObservable;
	// private boolean mActivityCreated = false;
	// private boolean mInterceptBackPressed = false;
	private ProgressDialog mLoadingIndicatorDialog;
	private String mLoadingIndicatorMessage;
	private int mStartEnterAnim;
	private int mStartExitAnim;
	private int mFinishEnterAnim;
	private int mFinishExitAnim;
	private boolean mFirstStarted = false;
	private State mActivityState = State.UNKNOW;

	public ActivityProxy(Activity activity)
	{
		TAG = activity.getClass().getSimpleName();
		mActivity = activity;
		mObservable = new ActivityObservable(activity);
	}

	public void onCreate(Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onCreate: " + mActivity.getIntent());
		// mActivityCreated = true;
		int enterAnim = ResourceUtil.getAnimId(mActivity, BasicConfig.ActivityStartEnterAnim);
		int exitAnim = ResourceUtil.getAnimId(mActivity, BasicConfig.ActivityStartExitAnim);
		setStartActivityAnimation(enterAnim, exitAnim);
		enterAnim = ResourceUtil.getAnimId(mActivity, BasicConfig.ActivityFinishEnterAnim);
		exitAnim = ResourceUtil.getAnimId(mActivity, BasicConfig.ActivityFinishExitAnim);
		setFinishActivityAnimation(enterAnim, exitAnim);
		mActivityState = State.CREATED;
		mObservable.notifyActivityCreate(savedInstanceState);
	}

	public void onPostCreate(Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onPostCreate");
	}

	public void onStart()
	{
		LogUtil.d(TAG, "onStart");
		mActivityState = State.STARTED;
		mObservable.notifyActivityStart();
		if (!mFirstStarted)
		{
			mFirstStarted = true;
			if (mActivity instanceof IActivityExtend)
				((IActivityExtend)mActivity).onFirstStart();
		}
	}

	public void onResume()
	{
		LogUtil.d(TAG, "onResume");
		mActivityState = State.RESUMED;
		mObservable.notifyActivityResume();
		if (mActivity.getParent() == null && BasicConfig.UseUMengAnalytics)
			MobclickAgent.onResume(mActivity);
	}

	public void onPostResume()
	{
		LogUtil.d(TAG, "onPostResume");
	}

	public void onPause()
	{
		LogUtil.d(TAG, "onPause");
		mActivityState = State.PAUSED;
		mObservable.notifyActivityPause();
		if (mActivity.getParent() == null && BasicConfig.UseUMengAnalytics)
			MobclickAgent.onPause(mActivity);
	}

	public void onRestart()
	{
		LogUtil.d(TAG, "onRestart");
		mActivityState = State.STARTED;
		mObservable.notifyActivityRestart();
	}

	public void onStop()
	{
		LogUtil.d(TAG, "onStop");
		mActivityState = State.STOPED;
		mObservable.notifyActivityStop();
	}

	public void onDestroy()
	{
		LogUtil.d(TAG, "onDestroy");
		// mActivityCreated = false;
		mActivityState = State.DESTROYED;
		mObservable.notifyActivityDestroy();
		mObservable.clearObservers();
		mFirstStarted = false;
	}

	public void onNewIntent(Intent intent)
	{
		LogUtil.d(TAG, "onNewIntent: " + intent);
		mObservable.notifyActivityNewIntent(intent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		LogUtil.d(TAG, "onActivityResult: " + requestCode + "; " + resultCode + "; " + data);
		mObservable.notifyActivityResult(requestCode, resultCode, data);
	}

	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onRestoreInstanceState");
		mObservable.notifyActivityRestoreInstanceState(savedInstanceState);
	}

	public void onSaveInstanceState(Bundle outState)
	{
		LogUtil.d(TAG, "onSaveInstanceState");
		mObservable.notifyActivitySaveInstanceState(outState);
	}

	public void onConfigurationChanged(Configuration newConfig)
	{
		LogUtil.d(TAG, "onConfigurationChanged: " + newConfig);
		mObservable.notifyActivityConfigurationChanged(newConfig);
	}

	public void onWindowFocusChanged(boolean hasFocus)
	{
		LogUtil.d(TAG, "onWindowFocusChanged: " + hasFocus);
		mObservable.notifyActivityWindowFocusChanged(hasFocus);
	}

	public Activity getActivity()
	{
		return mActivity;
	}

	public Activity getRootActivity()
	{
		return AndroidUtils.getRootActivity(mActivity);
	}

	public State getActivityState()
	{
		return mActivityState;
	}

	public View getDecorView()
	{
		return mActivity.getWindow().getDecorView();
	}

	public ViewGroup getContentRootView()
	{
		return ViewTools.getActivityContentRootView(mActivity);
	}

	public String getString(String resName)
	{
		int resId = ResourceUtil.getStringId(mActivity, resName);
		return mActivity.getString(resId);
	}

	public MenuBar getMenuBar()
	{
		if (mMenuBar == null)
		{
			int id = ResourceUtil.getId(mActivity, "menubar");
			mMenuBar = (MenuBar)mActivity.findViewById(id);
		}
		return mMenuBar;
	}

	public void registerObserver(OnActivityObserver observer)
	{
		mObservable.registerObserver(observer);
	}

	public void unregisterObserver(OnActivityObserver observer)
	{
		mObservable.unregisterObserver(observer);
	}

	public void setStartActivityAnimation(int enterAnim, int exitAnim)
	{
		mStartEnterAnim = enterAnim;
		mStartExitAnim = exitAnim;
	}

	public void setFinishActivityAnimation(int enterAnim, int exitAnim)
	{
		mFinishEnterAnim = enterAnim;
		mFinishExitAnim = exitAnim;
	}

	public void overrideStartPendingTransition()
	{
		overridePendingTransition(mStartEnterAnim, mStartExitAnim);
	}

	public void overrideFinishPendingTransition()
	{
		overridePendingTransition(mFinishEnterAnim, mFinishExitAnim);
	}

	public void overridePendingTransition(int enterAnim, int exitAnim)
	{
		mActivity.overridePendingTransition(enterAnim, exitAnim);
	}

	public void showToast(String text)
	{
		AndroidUtils.showToast(mActivity, text);
	}

	public void showLoadingIndicatorDialog(final String message, final int theme, final boolean cancelable,
			final OnCancelListener listener, final long showingTime)
	{
		Activity rootActivity = getRootActivity();
		if (rootActivity != mActivity && rootActivity instanceof IActivityExtend)
		{
			((IActivityExtend)rootActivity).showLoadingIndicator(message, theme, cancelable, listener, showingTime);
		}
		else
		{
			mActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					showLoadingIndicatorDialogImpl(message, theme, cancelable, listener, showingTime);
				}
			});
		}
	}

	private synchronized void showLoadingIndicatorDialogImpl(String message, int theme, boolean cancelable,
			final OnCancelListener listener, long showingTime)
	{
		LogUtil.v(TAG, "showLoadingIndicatorDialog: " + message + "; " + theme + "; " + cancelable + "; " + listener
				+ "; " + showingTime + "; " + mLoadingIndicatorDialog);
		try
		{
			if (mLoadingIndicatorDialog == null)
			{
				mLoadingIndicatorMessage = message;
				mLoadingIndicatorDialog = new ProgressDialog(getRootActivity(), theme);
				mLoadingIndicatorDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mLoadingIndicatorDialog.setMessage(message);
				mLoadingIndicatorDialog.setCancelable(cancelable);
				mLoadingIndicatorDialog.setCanceledOnTouchOutside(false);
				mLoadingIndicatorDialog.setOnCancelListener(listener);
				mLoadingIndicatorDialog.setOnDismissListener(new OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface dialog)
					{
						mLoadingIndicatorDialog = null;
					}
				});
				mLoadingIndicatorDialog.show();
			}
			else if (message != null && !message.equalsIgnoreCase(mLoadingIndicatorMessage))
			{
				mLoadingIndicatorMessage = message;
				mLoadingIndicatorDialog.setMessage(message);
				mLoadingIndicatorDialog.setCancelable(cancelable);
				// mLoadingIndicatorDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
				// mLoadingIndicatorDialog.setOnCancelListener(null);
				// mLoadingIndicatorDialog.setOnCancelListener(listener);
			}
			if (showingTime > 0)
			{
				updateHideLoadingIndicatorDialogRunnable(showingTime);
			}
		}
		catch (Throwable th)
		{
			th.printStackTrace();
		}
	}

	private class hideLoadingIndicatorDialogRunnable implements Runnable
	{
		@Override
		public void run()
		{
			LogUtil.i(TAG, "hideLoadingIndicatorDialogRunnable executed");
			hideLoadingIndicatorDialogImpl();
		}
	}

	private hideLoadingIndicatorDialogRunnable mHideIndicatorDialogRunnable = null;

	private void removeHideLoadingIndicatorDialogRunnable()
	{
		if (mHideIndicatorDialogRunnable != null)
		{
			AndroidUtils.MainHandler.removeCallbacks(mHideIndicatorDialogRunnable);
			mHideIndicatorDialogRunnable = null;
		}
	}

	private void updateHideLoadingIndicatorDialogRunnable(long showingTime)
	{
		removeHideLoadingIndicatorDialogRunnable();
		if (mHideIndicatorDialogRunnable == null)
		{
			mHideIndicatorDialogRunnable = new hideLoadingIndicatorDialogRunnable();
			AndroidUtils.MainHandler.postDelayed(mHideIndicatorDialogRunnable, showingTime);
		}
	}

	public void hideLoadingIndicatorDialog()
	{
		Activity rootActivity = getRootActivity();
		if (rootActivity != mActivity && rootActivity instanceof IActivityExtend)
		{
			((IActivityExtend)rootActivity).hideLoadingIndicator();
		}
		else
		{
			mActivity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					hideLoadingIndicatorDialogImpl();
				}
			});
		}
	}

	private synchronized void hideLoadingIndicatorDialogImpl()
	{
		removeHideLoadingIndicatorDialogRunnable();
		if (mLoadingIndicatorDialog != null)
		{
			LogUtil.v(TAG, "hideLoadingIndicatorDialog");
			mLoadingIndicatorDialog.dismiss();
			mLoadingIndicatorDialog = null;
		}
	}
}
