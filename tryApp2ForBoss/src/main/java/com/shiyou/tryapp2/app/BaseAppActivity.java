package com.shiyou.tryapp2.app;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.extend.app.BaseFragmentActivity;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.extend.util.ReflectHelper;
import android.extend.util.ResourceUtil;
import android.extend.widget.ExtendDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.shiyou.tryapp2.FileDownloadHelper;

public abstract class BaseAppActivity extends BaseFragmentActivity
{
	private SystemBarTintManager mTintManager;
	int visibility = View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// if (AndroidUtils.checkDeviceHasNavigationBar(this))
		// {
		// visibility = View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		// | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
		// }
		// else
		visibility = View.SYSTEM_UI_FLAG_VISIBLE;
		if (Build.VERSION.SDK_INT >= 19)
		{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			mTintManager = new SystemBarTintManager(this);
			mTintManager.setStatusBarTintEnabled(true);
			// mTintManager.setStatusBarAlpha(0);
			mTintManager.setNavigationBarTintEnabled(true);
			// 设置一个状态栏资源
			mTintManager.setTintColor(getResources().getColor(android.R.color.transparent));
			// mTintManager.setStatusBarTintColor(getResources().getColor(R.color.statusbar_background));
			// int drawable =ResourceUtil.getDrawableId(getApplicationContext(),"statusbar_bg");
			// mTintManager.setStatusBarTintDrawable(getResources().getDrawable(drawable));
		}
	}

	@Override
	public void onFirstStart()
	{
		super.onFirstStart();
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener()
		{
			@Override
			public void onSystemUiVisibilityChange(int visibility)
			{
				LogUtil.v(TAG, "onSystemUiVisibilityChange: " + visibility);
				setSystemUiVisibility();
			}
		});
		setSystemUiVisibility();
	}

	public void setSystemUiVisibility()
	{
		getWindow().getDecorView().setSystemUiVisibility(visibility);
	}

	@Override
	public void showLoadingIndicator(String message, int theme, boolean cancelable, OnCancelListener listener,
			long showingTime)
	{
		showLoadingIndicatorDialogImpl(message, theme, cancelable, listener, showingTime);
	}

	@Override
	public void hideLoadingIndicator()
	{
		hideLoadingIndicatorDialogImpl();
	}

	private ExtendDialog mLoadingIndicatorDialog;

	private void showLoadingIndicatorDialogImpl(final String message, final int theme, final boolean cancelable,
			final OnCancelListener listener, final long showingTime)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (mLoadingIndicatorDialog == null)
				{
					int layout = ResourceUtil.getLayoutId(getApplicationContext(), "loading_dialog");
					View view = View.inflate(getApplicationContext(), layout, null);
					int id = ResourceUtil.getId(getApplicationContext(), "loading_indicator");
					ImageView loading_indicator = (ImageView)view.findViewById(id);
					// AnimationDrawable animation = (AnimationDrawable)loading_indicator.getDrawable();
					// animation.start();
					int theme = ResourceUtil.getStyleId(getApplicationContext(), "TransparentDialog");
					mLoadingIndicatorDialog = AndroidUtils
							.createDialog(BaseAppActivity.this, theme, view, false, false);
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
					
					mLoadingIndicatorDialog.setSystemUiVisibility(visibility);
					mLoadingIndicatorDialog.show();
					ObjectAnimator oa = ObjectAnimator.ofFloat(loading_indicator, "rotation", 0f, 360f);
					ReflectHelper.setDeclaredFieldValue(oa, "android.animation.ValueAnimator", "sDurationScale", 1.0f);
					oa.setDuration(1000L);
					oa.setInterpolator(new LinearInterpolator());
					oa.setRepeatCount(ValueAnimator.INFINITE);
					oa.start();
				}
				else
				{
					mLoadingIndicatorDialog.setCancelable(cancelable);
					// mLoadingIndicatorDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
				}
			}
		});
	}

	private void hideLoadingIndicatorDialogImpl()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (mLoadingIndicatorDialog != null)
				{
					mLoadingIndicatorDialog.dismiss();
					mLoadingIndicatorDialog = null;
				}
			}
		});
	}

	@Override
	public void onLoadingIndicatorDialogCancel()
	{
		super.onLoadingIndicatorDialogCancel();
		FileDownloadHelper.cancelAllDownload(getApplicationContext());
	}
}
