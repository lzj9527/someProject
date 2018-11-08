package android.extend.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.extend.util.LogUtil;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;

public class ExtendDialog extends Dialog
{
	public final String TAG = getClass().getSimpleName();

	private int visibility = View.SYSTEM_UI_FLAG_VISIBLE;

	public ExtendDialog(Context context, int theme)
	{
		super(context, theme);
	}

	public ExtendDialog(Context context)
	{
		super(context);
	}

	public void setSystemUiVisibility(int visibility)
	{
		LogUtil.v(TAG, "setSystemUiVisibility: " + visibility);
		this.visibility = visibility;
	}

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onCreate: " + savedInstanceState);
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener()
		{
			@Override
			public void onSystemUiVisibilityChange(int visibility)
			{
				getWindow().getDecorView().setSystemUiVisibility(visibility);
			}
		});
		getWindow().getDecorView().setSystemUiVisibility(visibility);
	}

	@Override
	protected void onStart()
	{
		LogUtil.d(TAG, "onStart...");
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		LogUtil.d(TAG, "onStop...");
		super.onStop();
	}
}
