package com.shiyou.tryapp2.app;

import java.util.Date;

import android.app.Activity;
import android.extend.ErrorInfo;
import android.extend.app.BaseFragment;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.widget.MenuBar;
import android.extend.widget.MenuBar.OnMenuListener;
import android.extend.widget.MenuView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shiyou.tryapp2.Config;
import com.shiyou.tryapp2.RequestManager;
import com.shiyou.tryapp2.RequestManager.RequestCallback;
import com.shiyou.tryapp2.app.login.LoginHelper;
import com.shiyou.tryapp2.app.product.MainWebViewFragment;
import com.shiyou.tryapp2.app.product.Product3DFragment;
import com.shiyou.tryapp2.data.response.BaseResponse;
import com.shiyou.tryapp2.data.response.ShoppingcartListResponse;

public class MainFragment extends BaseFragment {

	private MenuBar mMenuBar;
	public static MainFragment instance = null;

	// public View fragmentC1, fragmentC2, fragmentC3, fragmentC4;
	// public int fragmentC1ID, fragmentC2ID, fragmentC3ID, fragmentC4ID;
	// public ContainerFragment mContainerFragment1, mContainerFragment2,
	// mContainerFragment3, mContainerFragment4;

	private MainWebViewFragment mFragment1 = new MainWebViewFragment(
			Config.WebProductCategory, "", 0);
	private MainWebViewFragment mFragment2 = new MainWebViewFragment(
			Config.WebBossShoppingCart, "购物车", 4);
	private MainWebViewFragment mFragment3 = new MainWebViewFragment(
			Config.WebBossOrder, "订单列表", 7);
	private MainWebViewFragment mFragment4 = new MainWebViewFragment(
			Config.WebBossMy, "我的设置", 6);

	private TextView shoppingcart_num;
	private Long lastTime = 0L;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		instance = this;
		mLayoutResID = ResourceUtil.getLayoutId(getContext(), "main_layout");
		View view = super.onCreateView(inflater, container, savedInstanceState);

		// fragmentC1ID = ResourceUtil.getId(getContext(),
		// "fragment_container1");
		// fragmentC1 = view.findViewById(fragmentC1ID);
		// mContainerFragment1 = new ContainerFragment();
		// add(instance, fragmentC1ID, mContainerFragment1, false);

		// fragmentC2ID = ResourceUtil.getId(getContext(),
		// "fragment_container2");
		// fragmentC2 = view.findViewById(fragmentC2ID);
		// mContainerFragment2 = new ContainerFragment();
		// add(instance, fragmentC2ID, mContainerFragment2, false);

		// fragmentC3ID = ResourceUtil.getId(getContext(),
		// "fragment_container3");
		// fragmentC3 = view.findViewById(fragmentC3ID);
		// mContainerFragment3 = new ContainerFragment();
		// add(instance, fragmentC3ID, mContainerFragment3, false);

		// fragmentC4ID = ResourceUtil.getId(getContext(),
		// "fragment_container4");
		// fragmentC4 = view.findViewById(fragmentC4ID);
		// mContainerFragment4 = new ContainerFragment();
		// add(instance, fragmentC1ID, mContainerFragment4, false);

		int id = ResourceUtil.getId(getContext(), "menubar");
		mMenuBar = (MenuBar) view.findViewById(id);
		mMenuBar.setOnMenuListener(new OnMenuListener() {
			@Override
			public void onMenuUnSelected(MenuBar menuBar, MenuView menuView,
					int menuIndex) {

			}

			@Override
			public void onMenuSelected(MenuBar menuBar, MenuView menuView,
					int menuIndex) {
				if (new Date().getTime() - lastTime > 500) {
					LogUtil.d(TAG, "onMenuSelected: " + menuIndex);
					setCurrentFragmentImpl(menuIndex);
				}
			}
		});
		mMenuBar.setCurrentMenu(0);

		id = ResourceUtil.getId(getContext(), "shoppingcart_num");
		shoppingcart_num = (TextView) view.findViewById(id);
		shoppingcart_num.setVisibility(View.GONE);
		updateShoppingcartBadgeNum();

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		instance = null;
	}

	// 切换碎片
	private void setCurrentFragmentImpl(final int index) {
		// fragmentC1.setVisibility(View.GONE);
		// fragmentC2.setVisibility(View.GONE);
		// fragmentC3.setVisibility(View.GONE);
		// fragmentC4.setVisibility(View.GONE);
		if (Product3DFragment.instance != null)
			Product3DFragment.instance.onBackPressed();
		AndroidUtils.MainHandler.post(new Runnable() {
			@Override
			public void run() {

				lastTime = new Date().getTime();

				if (popBackStackInclusive()) {
					AndroidUtils.MainHandler.postDelayed(this, 300L);
					return;
				}
				Log.i("test", "press:" + index);
				switch (index) {
				case 0:
					// fragmentC1.setVisibility(View.VISIBLE);
					replace(MainFragment.this, mFragment1, false);
					break;
				case 1:
					// fragmentC2.setVisibility(View.VISIBLE);
					replace(MainFragment.this, mFragment2, false);
					updateShoppingcartBadgeNum();
					break;
				case 2:
					// fragmentC3.setVisibility(View.VISIBLE);
					replace(MainFragment.this, mFragment3, false);
					break;
				case 3:
					// fragmentC4.setVisibility(View.VISIBLE);
					replace(MainFragment.this, mFragment4, false);
					break;
				}
				invalidateMenuBar();
				Log.i("test", "end:  " + index);
			}
		});
	}

	@Override
	public boolean onBackPressed() {
		invalidateMenuBar();
		// if (fragmentC1.getVisibility() == View.VISIBLE)
		// {
		// return super.onBackPressed();
		// }
		// else if (fragmentC2.getVisibility() == View.VISIBLE)
		// {
		// return false;
		// }
		// else if (fragmentC3.getVisibility() == View.VISIBLE)
		// {
		// return false;
		// }
		// else if (fragmentC4.getVisibility() == View.VISIBLE)
		// {
		// return false;
		// }
		return super.onBackPressed();
	}

	public void updateShoppingcartBadgeNum() {
		updateShoppingcartBadgeNum(getActivity(), shoppingcart_num);
	}

	public void updateShoppingcartBadgeNum(TextView otherBadgeView) {
		updateShoppingcartBadgeNum(getActivity(), new TextView[] {
				shoppingcart_num, otherBadgeView });
	}

	public static void updateShoppingcartBadgeNum(final Activity activity,
			final TextView[] badgeViews) {
		RequestManager.loadShoppingcartList(activity, LoginHelper.getUserKey(),
				new RequestCallback() {
					@Override
					public void onRequestResult(int requestCode, long taskId,
							BaseResponse response, DataFrom from) {
						if (response.resultCode == BaseResponse.RESULT_OK) {
							ShoppingcartListResponse slResponse = (ShoppingcartListResponse) response;
							if (slResponse.datas != null
									&& slResponse.datas.list != null)
								updateShoppingcartBadgeNumImpl(badgeViews,
										slResponse.datas.list.length);
							else
								updateShoppingcartBadgeNumImpl(badgeViews, 0);
						} else {
							// AndroidUtils.showToast(activity, response.error);
							updateShoppingcartBadgeNumImpl(badgeViews, 0);
						}
					}

					@Override
					public void onRequestError(int requestCode, long taskId,
							ErrorInfo error) {
						// AndroidUtils.showToast(activity, "网络异常: " +
						// error.errorCode);
						updateShoppingcartBadgeNumImpl(badgeViews, 0);
					}
				});
	}

	public static void updateShoppingcartBadgeNum(final Activity activity,
			final TextView badgeView) {
		updateShoppingcartBadgeNum(activity, new TextView[] { badgeView });
	}

	private static void updateShoppingcartBadgeNumImpl(
			final TextView[] badgeViews, final int num) {
		AndroidUtils.MainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (badgeViews != null) {
					if (num > 0) {
						for (TextView badgeView : badgeViews) {
							if (badgeView != null) {
								badgeView.setVisibility(View.VISIBLE);
								badgeView.setText("" + num);
							}
						}
					} else
						for (TextView badgeView : badgeViews) {
							if (badgeView != null)
								badgeView.setVisibility(View.GONE);
						}
				}
			}
		});
	}

	public void setCurrentFragment(final int index) {
		AndroidUtils.MainHandler.post(new Runnable() {
			@Override
			public void run() {
				LogUtil.v(TAG, "setCurrentFragment: " + index + "; "
						+ isResumed());
				if (isResumed()) {
					switch (index) {
					case 1:
						setCurrentFragmentToShoppingCart();
						break;
					case 2:
						setCurrentFragmentToOrder(null);
						break;
					default:
						mMenuBar.setCurrentMenu(index);
						break;
					}
				} else
					AndroidUtils.MainHandler.postDelayed(this, 100L);
			}
		});
	}

	public void setCurrentFragmentToOrder(final String url) {
		AndroidUtils.MainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (isResumed()) {
					if (!TextUtils.isEmpty(url))
						mFragment3 = new MainWebViewFragment(url, "订单列表", 7);
					else
						mFragment3 = new MainWebViewFragment(
								Config.WebBossOrder, "订单列表", 7);
					if (mMenuBar.getCurrentMenuIdx() == 2) {
						// mFragment3.refresh();
						replace(MainFragment.this, mFragment3, false);
					} else {
						mMenuBar.setCurrentMenu(2);
					}
				} else {
					AndroidUtils.MainHandler.postDelayed(this, 100L);
				}
			}
		});
	}

	public void setCurrentFragmentToShoppingCart() {
		AndroidUtils.MainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (isResumed()) {
					// mFragment2 = new MainWebViewFragment(Carurl, "购物车", 4);
					if (mMenuBar.getCurrentMenuIdx() == 1) {
						mFragment2.refresh();
						updateShoppingcartBadgeNum();
					} else {
						mMenuBar.setCurrentMenu(1);
					}
				} else {
					AndroidUtils.MainHandler.postDelayed(this, 100L);
				}
			}
		});
	}

	// public void addFragmentToCurrent(final Fragment fragment, final boolean
	// backToRoot)
	// {
	// AndroidUtils.MainHandler.post(new Runnable()
	// {
	// @Override
	// public void run()
	// {
	// if (isResumed())
	// {
	// if (backToRoot && popBackStackInclusive())
	// {
	// // if (ProductDetailsFragment.instance != null)
	// // ProductDetailsFragment.instance.onBackPressed();
	// AndroidUtils.MainHandler.postDelayed(this, 300L);
	// return;
	// }
	// add(instance, fragment, true);
	// invalidateMenuBar();
	// return;
	// }
	// AndroidUtils.MainHandler.postDelayed(this, 50L);
	// }
	// });
	// }

	public void invalidateMenuBar() {
		invalidateMenuBar(15L, 10);
	}

	int invalidateCount;

	private void invalidateMenuBar(final long delayMillis,
			final int numOfInvalidate) {
		invalidateCount = 0;
		AndroidUtils.MainHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mMenuBar.invalidate();
				invalidateCount++;
				if (invalidateCount < numOfInvalidate) {
					AndroidUtils.MainHandler.postDelayed(this, delayMillis);
				}
			}
		}, delayMillis);
	}
}
