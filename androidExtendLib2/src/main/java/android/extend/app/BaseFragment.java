package android.extend.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.extend.BasicConfig;
import android.extend.app.ActivityProxy.IActivityExtend;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.util.ViewTools;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class BaseFragment extends Fragment
{
	public static void add(FragmentManager manager, int containerViewId, Fragment fragment, int transition,
			boolean addToBackStack)
	{
		FragmentTransaction ft = manager.beginTransaction();
		ft.setTransition(transition);
		ft.add(containerViewId, fragment);
		if (addToBackStack)
			ft.addToBackStack(null);
		// if (fragment instanceof BaseFragment)
		// {
		// ((BaseFragment)fragment).mAddToBackStack = addToBackStack;
		// }
		ft.commit();
	}

	public static void add(FragmentManager manager, int containerViewId, Fragment fragment, boolean addToBackStack)
	{
		add(manager, containerViewId, fragment, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, addToBackStack);
	}

	public static void replace(FragmentManager manager, int containerViewId, Fragment fragment, int transition,
			boolean addToBackStack)
	{
		FragmentTransaction ft = manager.beginTransaction();
		ft.setTransition(transition);
		ft.replace(containerViewId, fragment);
		if (addToBackStack)
			ft.addToBackStack(null);
		// if (fragment instanceof BaseFragment)
		// {
		// ((BaseFragment)fragment).mAddToBackStack = addToBackStack;
		// }
		ft.commit();
	}

	public static void replace(FragmentManager manager, int containerViewId, Fragment fragment, boolean addToBackStack)
	{
		replace(manager, containerViewId, fragment, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, addToBackStack);
	}

	public static void show(FragmentManager manager, Fragment fragment)
	{
		if (fragment.isAdded())
		{
			FragmentTransaction ft = manager.beginTransaction();
			ft.show(fragment);
			ft.commit();
		}
	}

	public static void add(FragmentActivity activity, int containerViewId, Fragment fragment, int transition,
			boolean addToBackStack)
	{
		if (fragment instanceof BaseFragment)
		{
			((BaseFragment)fragment).mParent = null;
			((BaseFragment)fragment).mAddToBackStack = addToBackStack;
		}
		add(activity.getSupportFragmentManager(), containerViewId, fragment, transition, addToBackStack);
	}

	public static void add(FragmentActivity activity, int containerViewId, Fragment fragment, boolean addToBackStack)
	{
		add(activity, containerViewId, fragment, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, addToBackStack);
	}

	public static void add(FragmentActivity activity, Fragment fragment, boolean addToBackStack)
	{
		int containerViewId = ResourceUtil.getId(activity, "fragment_container");
		add(activity, containerViewId, fragment, addToBackStack);
	}

	public static void add(Fragment fragment, int containerViewId, Fragment child, int transition,
			boolean addToBackStack)
	{
		if (child instanceof BaseFragment)
		{
			((BaseFragment)child).mParent = fragment;
			((BaseFragment)child).mAddToBackStack = addToBackStack;
		}
		add(fragment.getChildFragmentManager(), containerViewId, child, transition, addToBackStack);
	}

	public static void add(Fragment fragment, int containerViewId, Fragment child, boolean addToBackStack)
	{
		add(fragment, containerViewId, child, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, addToBackStack);
	}

	public static void add(Fragment fragment, Fragment child, boolean addToBackStack)
	{
		int containerViewId = ResourceUtil.getId(fragment.getActivity(), "fragment_container");
		add(fragment, containerViewId, child, addToBackStack);
	}

	public static void replace(FragmentActivity activity, int containerViewId, Fragment fragment, int transition,
			boolean addToBackStack)
	{
		if (fragment instanceof BaseFragment)
		{
			((BaseFragment)fragment).mParent = null;
			((BaseFragment)fragment).mAddToBackStack = addToBackStack;
		}
		replace(activity.getSupportFragmentManager(), containerViewId, fragment, transition, addToBackStack);
	}

	public static void replace(FragmentActivity activity, int containerViewId, Fragment fragment, boolean addToBackStack)
	{
		replace(activity, containerViewId, fragment, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, addToBackStack);
	}

	public static void replace(FragmentActivity activity, Fragment fragment, boolean addToBackStack)
	{
		int containerViewId = ResourceUtil.getId(activity, "fragment_container");
		replace(activity, containerViewId, fragment, addToBackStack);
	}

	public static void replace(Fragment fragment, int containerViewId, Fragment child, int transition,
			boolean addToBackStack)
	{
		if (child instanceof BaseFragment)
		{
			((BaseFragment)child).mParent = fragment;
			((BaseFragment)child).mAddToBackStack = addToBackStack;
		}
		replace(fragment.getChildFragmentManager(), containerViewId, child, transition, addToBackStack);
	}

	public static void replace(Fragment fragment, int containerViewId, Fragment child, boolean addToBackStack)
	{
		replace(fragment, containerViewId, child, FragmentTransaction.TRANSIT_FRAGMENT_OPEN, addToBackStack);
	}

	public static void replace(Fragment fragment, Fragment child, boolean addToBackStack)
	{
		int containerViewId = ResourceUtil.getId(fragment.getActivity(), "fragment_container");
		replace(fragment, containerViewId, child, addToBackStack);
	}

	public static void show(FragmentActivity activity, Fragment fragment)
	{
		show(activity.getSupportFragmentManager(), fragment);
	}

	public static void show(Fragment fragment, Fragment child)
	{
		show(fragment.getChildFragmentManager(), child);
	}

	public final String TAG = getClass().getSimpleName();

	Fragment mParent;
	List<Fragment> mChildFragments = new ArrayList<Fragment>();
	boolean mAddToBackStack = false;

	// public enum State
	// {
	// UNKNOW, CREATED, ACTIVITY_CREATED, ATTACHED, STARTED, RESUMED, PAUSED, STOPED, DESTROYED, DETACHED,
	// }

	// protected State mState = State.UNKNOW;
	protected int mLayoutResID;
	// protected boolean mAttachedFragmentActivity = false;// 是否依赖Android本来的Fragment框架
	// protected Activity mAttachedActivity;
	protected View mCreatedView;
	// private ViewGroup mCreatedViewContainer;
	private boolean mFirstStarted = false;
	private boolean mIsDestroyed = false;

	public BaseFragment()
	{
		this(-1);
	}

	public BaseFragment(int layoutResID)
	{
		mLayoutResID = layoutResID;
	}

	public boolean isAddToBackStack()
	{
		return mAddToBackStack;
	}

	// public State getState()
	// {
	// return mState;
	// }

	@Deprecated
	public Activity getAttachedActivity()
	{
		return getActivity();
	}

	public View getCreatedView()
	{
		return mCreatedView;
	}

	public String getString(String resName)
	{
		int resId = ResourceUtil.getStringId(getContext(), resName);
		return getContext().getString(resId);
	}

	/**
	 * @deprecated Use {@link #showLoadingIndicator} instead.
	 */
	public void showLoadingIndicatorDialog()
	{
		showLoadingIndicator();
	}

	// public void showLoadingindicatorDialog(int progress)
	// {
	// if (getActivity() instanceof IActivityExtend)
	// ((IActivityExtend)getActivity()).showLoadingindicatorDialog(progress);
	// }

	/**
	 * @deprecated Use {@link #showLoadingIndicator(String message)} instead.
	 */
	public void showLoadingIndicatorDialog(String message)
	{
		showLoadingIndicator(message);
	}

	public void showLoadingIndicator()
	{
		if (getActivity() instanceof IActivityExtend)
			((IActivityExtend)getActivity()).showLoadingIndicator();
	}

	public void showLoadingIndicator(String message)
	{
		if (getActivity() instanceof IActivityExtend)
			((IActivityExtend)getActivity()).showLoadingIndicator(message);
	}

	public void showLoadingIndicator(int theme)
	{
		if (getActivity() instanceof IActivityExtend)
			((IActivityExtend)getActivity()).showLoadingIndicator(theme);
	}

	public void showLoadingIndicator(boolean cancelable)
	{
		if (getActivity() instanceof IActivityExtend)
			((IActivityExtend)getActivity()).showLoadingIndicator(cancelable);
	}

	public void showLoadingIndicator(OnCancelListener listener)
	{
		if (getActivity() instanceof IActivityExtend)
			((IActivityExtend)getActivity()).showLoadingIndicator(listener);
	}

	public void showLoadingIndicator(long showingTime)
	{
		if (getActivity() instanceof IActivityExtend)
			((IActivityExtend)getActivity()).showLoadingIndicator(showingTime);
	}

	public void showLoadingIndicator(String message, int theme, boolean cancelable, OnCancelListener listener,
			long showingTime)
	{
		if (getActivity() instanceof IActivityExtend)
			((IActivityExtend)getActivity()).showLoadingIndicator(message, theme, cancelable, listener, showingTime);
	}

	/**
	 * @deprecated Use {@link #hideLoadingIndicator} instead.
	 */
	public void hideLoadingIndicatorDialog()
	{
		hideLoadingIndicator();
	}

	public void hideLoadingIndicator()
	{
		if (getActivity() instanceof IActivityExtend)
			((IActivityExtend)getActivity()).hideLoadingIndicator();
	}

	public void showToast(String text)
	{
		AndroidUtils.showToast(getActivity(), text);
	}

	public boolean canPopBackStack()
	{
		if (!isResumed())
			return false;
		if (getChildFragmentManager() != null && getChildFragmentManager().getBackStackEntryCount() > 0)
		{
			return true;
		}
		return false;
	}

	public boolean canPopBackStackInChildren()
	{
		if (!isResumed())
			return false;
		if (mChildFragments != null && !mChildFragments.isEmpty())
		{
			for (Fragment fragment : mChildFragments)
			{
				if (fragment == null || !fragment.isResumed() || !fragment.isVisible())
					continue;
				if (fragment instanceof BaseFragment)
				{
					if (((BaseFragment)fragment).canPopBackStackInChildren())
					{
						return true;
					}
				}
			}
		}
		if (getChildFragmentManager() != null && getChildFragmentManager().getBackStackEntryCount() > 0)
		{
			return true;
		}
		return false;
	}

	public boolean popBackStackImmediate()
	{
		if (!isResumed())
			return false;
		if (getChildFragmentManager() != null)
			return getChildFragmentManager().popBackStackImmediate();
		return false;
	}

	public boolean popBackStackInclusiveImmediate()
	{
		if (!isResumed())
			return false;
		if (getChildFragmentManager() != null)
			return getChildFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		return false;
	}

	public boolean popBackStack()
	{
		if (!isResumed())
			return false;
		if (getChildFragmentManager() != null && getChildFragmentManager().getBackStackEntryCount() > 0)
		{
			getChildFragmentManager().popBackStack();
			return true;
		}
		return false;
	}

	public boolean popBackStackInclusive()
	{
		if (!isResumed())
			return false;
		if (getChildFragmentManager() != null && getChildFragmentManager().getBackStackEntryCount() > 0)
		{
			getChildFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			return true;
		}
		return false;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		LogUtil.i(TAG, "onKeyDown: " + keyCode + "; " + event.getRepeatCount() + "; " + isResumed() + "; "
				+ isAddToBackStack());
		if (mChildFragments != null && !mChildFragments.isEmpty())
		{
			for (Fragment fragment : mChildFragments)
			{
				if (fragment == null || !fragment.isResumed() || !fragment.isVisible())
					continue;
				if (fragment instanceof BaseFragment)
				{
					if (((BaseFragment)fragment).onKeyDown(keyCode, event))
					{
						return true;
					}
					if (((BaseFragment)fragment).isAddToBackStack() && keyCode == KeyEvent.KEYCODE_BACK
							&& event.getRepeatCount() == 0)
					{
						return onBackPressed();
					}
				}
			}
		}
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			return onBackPressed();
		}
		return false;
	}

	// public boolean onKeyUp(int keyCode, KeyEvent event)
	// {
	// LogUtil.i(TAG, "onKeyUp: " + keyCode + "; " + event.getRepeatCount() + "; " + isResumed() + "; "
	// + isAddToBackStack());
	// if (mChildFragments != null && !mChildFragments.isEmpty())
	// {
	// for (Fragment fragment : mChildFragments)
	// {
	// if (fragment == null || !fragment.isResumed())
	// continue;
	// if (fragment instanceof BaseFragment)
	// {
	// if (((BaseFragment)fragment).onKeyUp(keyCode, event))
	// {
	// return true;
	// }
	// if (((BaseFragment)fragment).isAddToBackStack() && keyCode == KeyEvent.KEYCODE_BACK
	// && event.getRepeatCount() == 0)
	// {
	// break;
	// }
	// }
	// }
	// }
	// return false;
	// }

	public boolean onBackPressed()
	{
		// if (AndroidUtils.isFastClick())
		// if (canPopBackStack())
		// return true;
		// else
		// return false;
		boolean result = popBackStack();
		LogUtil.d(TAG, "onBackPressed: " + result);
		return result;
	}

	@Override
	public void startActivity(Intent intent)
	{
		// if (getActivity() != null)
		super.startActivity(intent);
		// else if (mAttachedActivity != null)
		// mAttachedActivity.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode)
	{
		if (getActivity() != null)
		{
			if (getActivity() instanceof BaseFragmentActivity)
				getActivity().startActivityFromFragment(this, intent, requestCode);
			else
				super.startActivityForResult(intent, requestCode);
		}
		// else if (mAttachedActivity != null)
		// {
		// if (mAttachedActivity instanceof FragmentActivity)
		// ((FragmentActivity)mAttachedActivity).startActivityFromFragment(this, intent, requestCode);
		// else
		// mAttachedActivity.startActivityForResult(intent, requestCode);
		// }
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onActivityCreated: " + savedInstanceState);
		super.onActivityCreated(savedInstanceState);
		// mState = State.ACTIVITY_CREATED;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		LogUtil.d(TAG, "onActivityResult: " + requestCode + " " + resultCode + " " + data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	// @Override
	// public void onAttach(Activity activity)
	// {
	// if (activity instanceof FragmentActivity)
	// {
	// mAttachedFragmentActivity = true;
	// }
	// LogUtil.d(TAG, "onAttach: " + activity + "; AttachedFragmentActivity: " + mAttachedFragmentActivity);
	// super.onAttach(activity);
	// mAttachedActivity = activity;
	// // mState = State.ATTACHED;
	// }

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		LogUtil.d(TAG, "onConfigurationChanged: " + newConfig);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onAttach(Context context)
	{
		LogUtil.d(TAG, "onAttach: " + context);
		super.onAttach(context);
		if (mParent != null && mParent instanceof BaseFragment)
		{
			((BaseFragment)mParent).mChildFragments.add(0, this);
		}
		else if (getActivity() instanceof BaseFragmentActivity)
		{
			((BaseFragmentActivity)getActivity()).mFragments.add(0, this);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		LogUtil.d(TAG, "onContextItemSelected: " + item);
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onCreate: " + savedInstanceState);
		super.onCreate(savedInstanceState);
		// mState = State.CREATED;
	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim)
	{
		if (nextAnim == 0)
		{
			switch (transit)
			{
				case FragmentTransaction.TRANSIT_FRAGMENT_OPEN:
					if (enter)
					{
						nextAnim = ResourceUtil.getAnimId(getContext(), BasicConfig.FragmentOpenEnterAnim);
					}
					else
					{
						nextAnim = ResourceUtil.getAnimId(getContext(), BasicConfig.FragmentOpenExitAnim);
					}
					break;
				case FragmentTransaction.TRANSIT_FRAGMENT_FADE:
					if (enter)
					{
						nextAnim = android.R.anim.fade_in;
					}
					else
					{
						nextAnim = android.R.anim.fade_out;
					}
					break;
				case FragmentTransaction.TRANSIT_FRAGMENT_CLOSE:
					if (enter)
					{
						nextAnim = ResourceUtil.getAnimId(getContext(), BasicConfig.FragmentCloseEnterAnim);
					}
					else
					{
						nextAnim = ResourceUtil.getAnimId(getContext(), BasicConfig.FragmentCloseExitAnim);
					}
					break;
				default:
					// if (enter)
					// {
					// nextAnim = ResourceUtil.getAnimId(mAttachedActivity, AppConfig.FragmentOpenEnterAnim);
					// }
					// else
					// {
					// nextAnim = ResourceUtil.getAnimId(mAttachedActivity, AppConfig.FragmentCloseExitAnim);
					// }
					break;
			}
		}
		LogUtil.d(TAG, "onCreateAnimation: " + transit + " " + enter + " " + nextAnim);
		if (nextAnim != 0)
			return AnimationUtils.loadAnimation(getActivity(), nextAnim);
		else
			return super.onCreateAnimation(transit, enter, nextAnim);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo)
	{
		LogUtil.d(TAG, "onCreateContextMenu: " + menu + " " + view + " " + menuInfo);
		super.onCreateContextMenu(menu, view, menuInfo);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		LogUtil.d(TAG, "onCreateOptionsMenu: " + menu + " " + inflater);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onCreateView: " + inflater + " " + mLayoutResID + " " + container + " " + savedInstanceState);
		if (mLayoutResID > 0)
		{
			return inflater.inflate(mLayoutResID, null);
		}
		else
		{
			return super.onCreateView(inflater, container, savedInstanceState);
		}
	}

	@Override
	public void onDestroy()
	{
		LogUtil.d(TAG, "onDestroy");
		// if (mAttachedFragmentActivity)
		super.onDestroy();
		// if (mCreatedViewContainer != null)
		// {
		// ViewTools.removeAllViewsInChildren(mCreatedViewContainer);
		// mCreatedViewContainer.destroyDrawingCache();
		// mCreatedViewContainer = null;
		// mCreatedView = null;
		// }
		// else
		if (mCreatedView != null)
		{
			if (mCreatedView instanceof ViewGroup)
			{
				ViewTools.removeAllViewsInChildren((ViewGroup)mCreatedView);
				mCreatedView.destroyDrawingCache();
			}
			ViewTools.removeViewParent(mCreatedView);
			mCreatedView = null;
		}
		mChildFragments.clear();
		// mChildFragments = null;
		// mState = State.DESTROYED;
		mFirstStarted = false;
		mIsDestroyed = true;
	}

	public boolean isDestroyed()
	{
		return mIsDestroyed;
	}

	@Override
	public void onDestroyOptionsMenu()
	{
		LogUtil.d(TAG, "onDestroyOptionsMenu");
		super.onDestroyOptionsMenu();
	}

	@Override
	public void onDestroyView()
	{
		LogUtil.d(TAG, "onDestroyView");
		super.onDestroyView();
		// if (mCreatedViewContainer != null)
		// {
		// ViewTools.removeAllViewsInChildren(mCreatedViewContainer);
		// mCreatedViewContainer.destroyDrawingCache();
		// mCreatedViewContainer = null;
		// mCreatedView = null;
		// }
		// else if (mCreatedView != null)
		// {
		// if (mCreatedView instanceof ViewGroup)
		// {
		// ViewTools.removeAllViewsInChildren((ViewGroup)mCreatedView);
		// mCreatedView.destroyDrawingCache();
		// }
		// ViewTools.removeViewParent(mCreatedView);
		// mCreatedView = null;
		// }
	}

	@Override
	public void onDetach()
	{
		LogUtil.d(TAG, "onDetach");
		super.onDetach();
		mFirstStarted = false;
		// mAttachedActivity = null;
		// mState = State.DETACHED;
		if (mParent != null && mParent instanceof BaseFragment)
		{
			((BaseFragment)mParent).mChildFragments.remove(this);
		}
		else if (getActivity() instanceof BaseFragmentActivity)
		{
			((BaseFragmentActivity)getActivity()).mFragments.remove(this);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden)
	{
		LogUtil.d(TAG, "onHiddenChanged: " + hidden);
		super.onHiddenChanged(hidden);
	}

	// @Override
	// public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState)
	// {
	// LogUtil.d(TAG, "onInflate: " + activity + " " + attrs + " " + savedInstanceState);
	// super.onInflate(activity, attrs, savedInstanceState);
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		LogUtil.d(TAG, "onOptionsItemSelected: " + item);
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		LogUtil.d(TAG, "onOptionsMenuClosed: " + menu);
		super.onOptionsMenuClosed(menu);
	}

	@Override
	public void onPause()
	{
		LogUtil.d(TAG, "onPause");
		super.onPause();
		// if (BasicConfig.UseUMengAnalytics)
		// MobclickAgent.onPageEnd(TAG);
		// mState = State.PAUSED;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		LogUtil.d(TAG, "onPrepareOptionsMenu: " + menu);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onResume()
	{
		LogUtil.d(TAG, "onResume");
		super.onResume();
		// if (BasicConfig.UseUMengAnalytics)
		// MobclickAgent.onPageStart(TAG);
		// mState = State.RESUMED;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		LogUtil.d(TAG, "onSaveInstanceState: " + outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart()
	{
		LogUtil.d(TAG, "onStart");
		// if (mAttachedFragmentActivity)
		super.onStart();
		if (!mFirstStarted)
		{
			mFirstStarted = true;
			onFirstStart();
		}
		mIsDestroyed = false;
		// mState = State.STARTED;
	}

	public void onFirstStart()
	{
		LogUtil.d(TAG, "onFirstStart");
	}

	@Override
	public void onStop()
	{
		LogUtil.d(TAG, "onStop");
		super.onStop();
		// mState = State.STOPED;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onViewCreated: " + view + " " + savedInstanceState);
		super.onViewCreated(view, savedInstanceState);
		// if (ReflectHelper.isInstanceOf(view, "android.support.v4.app.NoSaveStateFrameLayout"))
		// {
		// FrameLayout fl = (FrameLayout)view;
		// mCreatedViewContainer = fl;
		// mCreatedView = fl.getChildAt(0);
		// }
		// else
		// {
		mCreatedView = view;
		// }
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState)
	{
		LogUtil.d(TAG, "onViewStateRestored: " + savedInstanceState);
		super.onViewStateRestored(savedInstanceState);
	}
}
