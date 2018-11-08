package com.shiyou.tryapp2.app;

import android.app.Activity;
import android.content.Intent;
import android.extend.app.BaseFragment;
import android.extend.util.ResourceUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class FragmentActivity extends BaseAppActivity
{
	private static Fragment launchFragment;

	public static void launchMe(Activity activity, BaseFragment launchFragment)
	{
		FragmentActivity.launchFragment = launchFragment;
		Intent intent = new Intent(activity, FragmentActivity.class);
		activity.startActivity(intent);
	}

	public static void launchMeForResult(Activity activity, BaseFragment launchFragment, int requestCode)
	{
		FragmentActivity.launchFragment = launchFragment;
		Intent intent = new Intent(activity, FragmentActivity.class);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void launchMeForResult(Fragment fragment, BaseFragment launchFragment, int requestCode)
	{
		FragmentActivity.launchFragment = launchFragment;
		Intent intent = new Intent(fragment.getActivity(), FragmentActivity.class);
		fragment.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		int layout = ResourceUtil.getLayoutId(getApplicationContext(), "default_fragment_layout");
		setContentView(layout);

		if (launchFragment != null)
			BaseFragment.add(this, launchFragment, false);
	}
}
