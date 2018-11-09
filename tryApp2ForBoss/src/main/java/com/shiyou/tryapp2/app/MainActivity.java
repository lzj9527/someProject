package com.shiyou.tryapp2.app;

import java.io.File;

import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.extend.BasicConfig;
import android.extend.app.BaseFragment;
import android.extend.util.AndroidUtils;
import android.extend.util.FileUtils;
import android.extend.util.LogUtil;
import android.extend.util.ReflectHelper;
import android.extend.util.ResourceUtil;
import android.extend.util.ViewTools;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.shiyou.tryapp2.Config;
import com.shiyou.tryapp2.Define;
import com.shiyou.tryapp2.LaunchImagesHelper;
import com.shiyou.tryapp2.LaunchImagesHelper.Callback;
import com.shiyou.tryapp2.app.login.LoginHelper;
import com.shiyou.tryapp2.app.product.MainWebViewFragment;
import com.shiyou.tryapp2.app.product.Product3DFragment;
import com.shiyou.tryapp2.data.TryonPoseData;
import com.shiyou.tryapp2.data.UnityImageInfo;
import com.umeng.analytics.MobclickAgent;
import com.unity3d.player.UnityPlayer;

public class MainActivity extends BaseAppActivity {
	// public enum StartFrom
	// {
	// NONE, Product, CoupleRing,
	// }
	final static String PERMISSIONS[]=new String[]{
			Manifest.permission.CAMERA,
			Manifest.permission.CALL_PHONE,
			Manifest.permission.GET_ACCOUNTS,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE

	};
	public static final String TAG = MainActivity.class.getSimpleName();
	public static MainActivity instance = null;
	public static DisplayMetrics windowDisplayMetrics = null;
	public static Point windowDisplaySize;

	// private static StartFrom mStartFrom = StartFrom.NONE;
	// private static BaseData mStartFromData;

	private View mTryonUI;
	public UnityPlayer mUnityPlayer;
	private FrameLayout mUnityContainer;

	private View mSplashView;

	private View mMainUI;

	private String mCurrentUI = Define.UI_SPLASH;
	private String mUIState;

	public enum ScreenShotAfter {
		None, Save, Back, Share, Upload,
	}

	// private ScreenShotAfter mScreenShotAfter = ScreenShotAfter.None;
	// private boolean mNeedScreenshot = true;
	// private String mScreenshotPath;

	// private GoodsItem[] mGoodsItemList = null;
	// private String mSelectedId;
	// private Map<String, GoodsItem> mGoodsItemMap =
	// Collections.synchronizedMap(new HashMap<String, GoodsItem>());
	// private List<String> mCheckedGoodsList = new ArrayList<String>();

	private boolean mBackPressed = false;

	// private TryonPoseData mTryonPoseData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
//			for (int i = 0; i < PERMISSIONS.length; i++) {
//				if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, PERMISSIONS[i])) {
//					int permissionCode = 200;
//					Log.d(TAG, "onResume: 权限申请不成功");
//					ActivityCompat.requestPermissions(this, PERMISSIONS, permissionCode);
//				} else {
//
//					Log.d(TAG, "onResume: 权限申请成功");
//				}
//			}
//		}
		instance = this;

		/*
		windowDisplayMetrics = AndroidUtils.getActivityDisplayMetrics(this);
		LogUtil.w(TAG, "windowDisplayMetrics: "
				+ windowDisplayMetrics.widthPixels + " x "
				+ windowDisplayMetrics.heightPixels + "; "
				+ windowDisplayMetrics.density + "; "
				+ windowDisplayMetrics.densityDpi + "; "
				+ windowDisplayMetrics.scaledDensity);
		windowDisplaySize = AndroidUtils.getActivityDisplaySize(this);
		LogUtil.w(TAG, "windowDisplaySize: " + windowDisplaySize.x + " x "
				+ windowDisplaySize.y);
		*/

		// 创建启动图片
		int layout = ResourceUtil.getLayoutId(getApplicationContext(),
				"base_main_layout");
		View view = View.inflate(getApplicationContext(), layout, null);
		setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		// if (AndroidUtils.checkDeviceHasNavigationBar(this))
		// view.setFitsSystemWindows(false);
		// else
		view.setFitsSystemWindows(true);
		view.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				LogUtil.v(TAG, "onSystemUiVisibilityChange: " + visibility);
				setSystemUiVisibility();
			}
		});

		// ensureUnityPlayer();
		// 创建 UI

		ensureTryonUI();
		ensureSplashView();
		ensureMainUI();

		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// if (!AndroidUtils.checkDeviceHasNavigationBar(this))
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new
		// OnSystemUiVisibilityChangeListener()
		// {
		// @Override
		// public void onSystemUiVisibilityChange(int visibility)
		// {
		// LogUtil.v(TAG, "onSystemUiVisibilityChange: " + visibility);
		// setSystemUiVisibility();
		// }
		// });
		// setSystemUiVisibility();

		// doFullscreen(false);
		setCurrentUI(Define.UI_SPLASH, null);

		// PGEditImageLoader.initImageLoader(this);
		//
		// PGEditSDK.instance().initSDK(this);

		// ShareSDK.initSDK(this);
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		LogUtil.d(
				TAG,
				"JPushInterface.getRegistrationID: "
						+ JPushInterface
								.getRegistrationID(getApplicationContext()));
		BasicConfig.init(this);
	}

	@Override
	public void setSystemUiVisibility() {
		super.setSystemUiVisibility();
		if (AndroidUtils.checkDeviceHasNavigationBar(this))
			mUnityPlayer.setSystemUiVisibility(visibility);
	}

	@Override
	protected void onDestroy() {
		mUnityPlayer.quit();
		super.onDestroy();
		instance = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		mUnityPlayer.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mUnityPlayer.resume();
		setSystemUiVisibility();

		// updateShoppingCartNum();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		 LogUtil.d(TAG, "onNewIntent: " + intent.getExtras());
		if (intent.getExtras() != null) {
			Bundle extras = intent.getExtras();
			if (extras.containsKey(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				if (LoginHelper.isLogined()) {
					MainActivity.backToHomepage(this, 2);
				}
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
		setSystemUiVisibility();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
		setSystemUiVisibility();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mCurrentUI == null || mCurrentUI.equals(Define.UI_SPLASH))
			return true;
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mBackPressed)
				return true;
			if (mCurrentUI != null && mCurrentUI.equals(Define.UI_TRYON_UI)) {
				onBackPressed();
				return true;
			}
			if (canPopBackStackInChildren()) {
				return super.onKeyDown(keyCode, event);
			}
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		LogUtil.v(TAG, "onBackPressed: " + mCurrentUI);
		if (mCurrentUI != null && mCurrentUI.equals(Define.UI_SPLASH))
			return;
		if (mCurrentUI != null && mCurrentUI.equals(Define.UI_TRYON_UI)) {
			if (AndroidUtils.isFastClick())
				return;
			if (mUIState != null) {
				if (mUIState.equals(Define.STATE_ERASURE)) {
					UnityPlayer.UnitySendMessage("PlatformMessageHandler",
							"NotifyChangeSelectedModelState",
							Define.STATE_ACTION);
					return;
				}
				// if (mUIState.equals(Define.STATE_ACTION))
				// {
				// UnityPlayer.UnitySendMessage("PlatformMessageHandler",
				// "NotifySetSelectModel", "");
				// return;
				// }
			}
			backToMainUI(null);
			return;
		}
		if (popBackStackImmediate()) {
			return;
		}
		exitApp();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case Define.REQ_CAMERA_FROM_TRYON:
				if (data != null) {
					String path = data.getStringExtra(Define.Name_Path);
					LogUtil.d(TAG, "path: " + path);
					UnityImageInfo image = new UnityImageInfo();
					image.tag = Define.TAG_PERSON_IMAGE;
					image.url = path;
					image.path = path;
					UnityPlayer.UnitySendMessage("PlatformMessageHandler",
							"NotifyRemoveAllImages", "");
					UnityPlayer.UnitySendMessage("PlatformMessageHandler",
							"NotifyLoadImage", UnityImageInfo.toJson(image));
				}
				break;
			}
		}
	}

	private long exitTime = 0;

	/**
	 * 退出程序
	 */
	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT)
					.show();
			exitTime = System.currentTimeMillis();
		} else {
			quit();
		}
	}

	public static void quit() {
		LogUtil.d(TAG, "quit");
		MobclickAgent.onKillProcess(instance);
		instance.finish();
		System.exit(0);
	}

	public void clearCache() {
		// mTryonPoseData = null;
	}

	private void setCurrentUI(final String uiTag,
			final AnimatorListener listener) {
		if (uiTag.equals(mCurrentUI))
			return;
		LogUtil.v(TAG, "setCurrentUI: " + uiTag);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mCurrentUI = uiTag;
				if (uiTag.equals(Define.UI_SPLASH)) {
					mSplashView.setVisibility(View.VISIBLE);
				} else if (uiTag.equals(Define.UI_MAIN_UI)) {
					UnityPlayer.UnitySendMessage("PlatformMessageHandler",
							"NotifyBackToWaiting", "");
					showMainUI(listener);
				} else if (uiTag.equals(Define.UI_DETAIL_UI)) {

				} else if (uiTag.equals(Define.UI_TRYON_UI)) {
					UnityPlayer.UnitySendMessage("PlatformMessageHandler",
							"NotifyBackToWaiting", "");
					mSplashView.setVisibility(View.GONE);
					hideMainUI(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							attachUnityPlayer();
							UnityPlayer.UnitySendMessage(
									"PlatformMessageHandler",
									"NotifyEnterCombineTryonScene", "");
							if (listener != null)
								listener.onAnimationEnd(animation);
						}
					});
				}
			}
		});
	}

	private void ensureTryonUI() {
		if (mTryonUI == null) {
			// int layout = ResourceUtil.getLayoutId(getApplicationContext(),
			// "tryon_layout");
			// mTryonUI = View.inflate(getApplicationContext(), layout, null);
			// addContentView(mTryonUI, new
			// LayoutParams(LayoutParams.MATCH_PARENT,
			// LayoutParams.MATCH_PARENT));

			int id = ResourceUtil
					.getId(getApplicationContext(), "tryon_layout");
			mTryonUI = findViewById(id);
		}
		ensureUnityPlayer();
		// ensureToolbar();
		// ensureCategoryLayout();
		// ensureGoodsListLayout();
		// ensureEraseLayout();
	}

	private void ensureUnityPlayer() {
		if (mUnityPlayer == null) {
			mUnityPlayer = new UnityPlayer(this);
			// if (AndroidUtils.checkDeviceHasNavigationBar(this))
			// mUnityPlayer.setFitsSystemWindows(false);
			// else
			mUnityPlayer.setFitsSystemWindows(true);
			if (AndroidUtils.checkDeviceHasNavigationBar(this))
				mUnityPlayer
						.setOnSystemUiVisibilityChangeListener(new OnSystemUiVisibilityChangeListener() {
							@Override
							public void onSystemUiVisibilityChange(
									int visibility) {
								LogUtil.v(TAG, "onSystemUiVisibilityChange: "
										+ visibility);
								setSystemUiVisibility();
							}
						});
			// int gles_mode = mUnityPlayer.getSettings().getInt("gles_mode",
			// 1);
			// mUnityPlayer.init(gles_mode, false);
			// UnityPlayer.currentActivity = this;
			// if (mUnityPlayer.getSettings ().getBoolean ("hide_status_bar",
			// true))
			// ReflectHelper.invokeDeclaredMethod(mUnityPlayer, "setFullscreen",
			// new Class[] { boolean.class },
			// new Object[] { false });

			int id = ResourceUtil.getId(getApplicationContext(),
					"unity_container");
			mUnityContainer = (FrameLayout) mTryonUI.findViewById(id);
			mUnityContainer.addView(mUnityPlayer);
		}
	}

	public void attachUnityPlayer() {
		if (ViewTools.containsView(mUnityContainer, mUnityPlayer))
			return;
		if (Product3DFragment.instance != null)
			Product3DFragment.instance.detachUnityPlayer();
		// ViewTools.removeViewParent(mUnityPlayer);
		mUnityContainer.addView(mUnityPlayer);
		mUnityPlayer.setVisibility(View.VISIBLE);
		mUnityPlayer.resume();
	}

	public void detachUnityPlayer() {
		if (ViewTools.containsView(mUnityContainer, mUnityPlayer))
			mUnityContainer.removeView(mUnityPlayer);
	}

	long millisTime;
	Runnable startMainRunnable;

	private void ensureSplashView() {
		startMainRunnable = new Runnable() {
			@Override
			public void run() {
				if (TextUtils.isEmpty(JPushInterface
						.getRegistrationID(getApplicationContext()))) {
					AndroidUtils.MainHandler.postDelayed(startMainRunnable,
							1000L);
					return;
				}
				LogUtil.v(TAG, "startMainRunnable...");
				setCurrentUI(Define.UI_MAIN_UI, null);
				LaunchImagesHelper.checkAndShow(instance, new Callback() {
					@Override
					public void onShowAnimationEnd() {
						String url = Config.WebLogin
								+ "?pushRegId="
								+ JPushInterface
										.getRegistrationID(getApplicationContext());
						BaseFragment.add(instance, new MainWebViewFragment(url,
								"", 0), false);
					}

					@Override
					public void onHideAnimationEnd() {
						LoginHelper.checkVersion(instance, true);
					}
				});
			}
		};

		int id = ResourceUtil.getId(getApplicationContext(), "splash_layout");
		mSplashView = findViewById(id);
		mSplashView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 捕获掉触屏事件
				return true;
			}
		});
	}

	private void ensureMainUI() {
		if (mMainUI == null) {
			// int layout = ResourceUtil.getLayoutId(getApplicationContext(),
			// "default_fragment_layout");
			// mMainUI = View.inflate(getApplicationContext(), layout, null);
			// addContentView(mMainUI, new
			// LayoutParams(LayoutParams.MATCH_PARENT,
			// LayoutParams.MATCH_PARENT));

			int id = ResourceUtil.getId(getApplicationContext(),
					"fragment_container");
			mMainUI = findViewById(id);
		}
	}

	private void showMainUI(final AnimatorListener listener) {
		AndroidUtils.MainHandler.post(new Runnable() {
			@Override
			public void run() {
				mMainUI.setVisibility(View.VISIBLE);
				if (mMainUI.getTranslationX() != 0) {
					ObjectAnimator oa = ObjectAnimator.ofFloat(mMainUI,
							"translationX", mMainUI.getTranslationX(), 0f);
					ReflectHelper.setDeclaredFieldValue(oa,
							"android.animation.ValueAnimator",
							"sDurationScale", 1.0f);
					oa.setInterpolator(new LinearInterpolator());
					oa.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if (listener != null)
								listener.onAnimationEnd(animation);
						}
					});
					oa.start();
				} else {
					if (listener != null)
						listener.onAnimationEnd(null);
				}
			}
		});
	}

	private void hideMainUI(final AnimatorListener listener) {
		AndroidUtils.MainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mMainUI.getTranslationX() == 0) {
					ObjectAnimator oa = ObjectAnimator.ofFloat(mMainUI,
							"translationX", 0f, -mMainUI.getWidth());
					ReflectHelper.setDeclaredFieldValue(oa,
							"android.animation.ValueAnimator",
							"sDurationScale", 1.0f);
					// oa.setDuration(300L);
					oa.setInterpolator(new LinearInterpolator());
					oa.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							animation.removeListener(this);
							mMainUI.setVisibility(View.INVISIBLE);
							if (listener != null)
								listener.onAnimationEnd(animation);
						}
					});
					oa.start();
				} else {
					mMainUI.setVisibility(View.INVISIBLE);
					if (listener != null)
						listener.onAnimationEnd(null);
				}
			}
		});
	}

	public void backToMainUI(final AnimatorListener listener) {
		if (mCurrentUI.equals(Define.UI_MAIN_UI)) {
			if (listener != null)
				listener.onAnimationEnd(null);
			return;
		}
		mBackPressed = true;

		// hideLayerList();
		// shrinkToolBar();
		// hideToolBarLayout();
		// hideCategoryLayout();
		// hideProductList();
		// hideEditLayout();
		// hideModelLayout(false);
		// hideTipsFullscreen();
		// hideTipsDoubleFinger();
		// hideTipsIk();
		// hideTipsMove();

		// mScreenShotAfter = ScreenShotAfter.None;
		// mCheckedGoodsList.clear();

		hideLoadingIndicator();

		// doFullscreen(false);
		// AndroidUtils.MainHandler.postDelayed(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		setCurrentUI(Define.UI_MAIN_UI, new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (Product3DFragment.instance != null)
					Product3DFragment.instance.attachUnityPlayer();
				else
					UnityPlayer.UnitySendMessage("PlatformMessageHandler",
							"NotifyBackToWaiting", "");

				mBackPressed = false;
			}
		});
		// }
		// }, 1000L);
	}

	public void onUnityStarted() {
		LogUtil.d(TAG, "onUnityStarted...");
		long time = System.currentTimeMillis() - millisTime;
		long delay = 5000L - time;
		if (delay > 0) {
			AndroidUtils.MainHandler.postDelayed(startMainRunnable,
					5000L - time);
		} else {
			AndroidUtils.MainHandler.post(startMainRunnable);
		}
	}

	/**
	 * Unity通知平台Unity场景加载已完成
	 * @param scene
	 */
	public void onSceneStarted(String scene) {
		LogUtil.d(TAG, "onSceneStarted: " + scene);
		// mNeedScreenshot = true;
		// if (scene.equals(Define.Scene_CombineTryOn))
		// {
		// if (mTryonPoseData == null)
		// {
		// readDefaultPoseData();
		// }
		// else
		// {
		// mTryonPoseData.printData(MainActivity.TAG, 0);
		//
		// UnityPlayer.UnitySendMessage("PlatformMessageHandler",
		// "NotifyLoadImage",
		// UnityImageInfo.toJson(mTryonPoseData.PERSON_IMAGE));
		//
		// loadStartFrom();
		// }
		// }
		// else if (scene.equals(Define.Scene_3DShow))
		// {
		// loadStartFrom();
		// }
	}

	/**
	 * Unity通知平台当前UI状态已改变
	 * @param state
	 */
	public void onUIStateChanged(final String state) {
		LogUtil.v(TAG, "onUIStateChanged: " + state + "; " + mUIState);
		// if (mUIState != null && mUIState.equals(state))
		// return;
		// mNeedScreenshot = true;
		// AndroidUtils.MainHandler.post(new Runnable()
		// {
		// @Override
		// public void run()
		// {
		// mUIState = state;
		// if (state.equals(Define.STATE_NORMAL))
		// {
		// showToolbar();
		// showCategoryLayout();
		// hideEraseLayout();
		// }
		// else if (state.equals(Define.STATE_ACTION))
		// {
		// showToolbar();
		// showCategoryLayout();
		// hideEraseLayout();
		// }
		// else if (state.equals(Define.STATE_ERASURE))
		// {
		// hideToolbar();
		// hideCategoryLayout();
		// showEraseLayout();
		// }
		// }
		// });
	}

	/**
	 * Unity通知平台全屏状态变化
	 * @param fullScreen
	 */
	public void onFullScreenChanged(boolean fullScreen) {
		LogUtil.v(TAG, "onFullScreenChanged: " + fullScreen);
	}

	public void onIKBounds(String bodyBounds, String btnBounds) {
	}

	public void onModelTouched(String id, float screenX, float screenY) {
	}

	public void onSelectedModelChanged(String id) {
		// LogUtil.v(TAG, "onSelectedModelChanged: " + id + "; " + mSelectedId);
		// mNeedScreenshot = true;
		// if (mSelectedId != null && mSelectedId.equals(id))
		// return;
		// mSelectedId = id;
	}

	public void onModelRemoved(String id) {
		// LogUtil.v(TAG, "onModelRemoved: " + id + "; " + mSelectedId);
		// mNeedScreenshot = true;
		// if (mSelectedId != null && mSelectedId.equals(id))
		// mSelectedId = null;
	}

	public void onModelLayerChanged(String id, int layer) {
		LogUtil.v(TAG, "onModelLayerChanged: " + id + ", " + layer);
		// mNeedScreenshot = true;
	}

	public void onModelLoadStarted(String id) {
		LogUtil.v(TAG, "onModelLoadStarted: " + id);
		showLoadingIndicator(10 * 1000L);
	}

	public void onModelLoadProgress(String id, float progress) {
		LogUtil.v(TAG, "onModelLoadProgress: " + id + "; " + progress);
	}

	public void onModelLoadFinished(String id, int layer, String faceTag,
			boolean isClothes) {
		LogUtil.v(TAG, "onModelLoadFinished: " + id + "; " + layer + "; "
				+ faceTag + ";" + isClothes);
		hideLoadingIndicator();
		// mNeedScreenshot = true;
	}

	public void onModelLoadFailed(String id, String error) {
		LogUtil.v(TAG, "onModelLoadFailed: " + id + "; " + error);
		hideLoadingIndicator();
		showToast(error);
	}

	public void onImageLoadFinished(String tag, String path) {
		LogUtil.v(TAG, "onImageLoadFinished: " + tag + "; " + path);
		// mNeedScreenshot = true;
	}

	public void onModelVisibleChanged(String id, boolean visible) {
		LogUtil.v(TAG, "onModelVisibleChanged: " + id + "; " + visible);
	}

	public void onModelMaterialChanged(String id, String materialTag) {
		LogUtil.v(TAG, "onModelMaterialChanged: " + id + "; " + materialTag);
	}

	public void onSaveModelActData(String json) {
		LogUtil.v(TAG, "onSaveModelActData...");
	}

	public void onSaveTryonData(String json) {
		LogUtil.v(TAG, "onSaveTryonData...");
		final TryonPoseData poseData = TryonPoseData.fromJson(json);
		poseData.printData(TAG, 0);
	}

	public String newScreenshotFile() {
		String name = FileUtils.makeNameInCurrentTime();
		File file = FileUtils.getFile(getApplicationContext(), "screenshot",
				name + ".png");
		return file.getAbsolutePath();
	}

	public void onScreenShotSucceed(String path) {
		// LogUtil.d(TAG, "onScreenShotSucceed: " + path + "; " +
		// mScreenShotAfter);
		// mNeedScreenshot = false;
		// AndroidUtils.sendScanFileBroadcast(this, path);
		// hideLoadingIndicatorDialog();
		// mScreenshotPath = path;
		// showToast("截屏成功,文件保存在:" + mScreenshotPath);
		// switch (mScreenShotAfter)
		// {
		// case None:
		// break;
		// case Save:
		// case Share:
		// UnityPlayer.UnitySendMessage("PlatformMessageHandler",
		// "NotifySaveTryonData", "");
		// break;
		// default:
		// break;
		// }
	}

	public void onScreenShotFailed(int errorCode) {
		// LogUtil.d(TAG, "onScreenShotFailed: " + errorCode);
		// hideLoadingIndicatorDialog();
		// mScreenshotPath = null;
		// switch (errorCode)
		// {
		// case 0:
		// showToast("截屏失败!");
		// break;
		// case 1:
		// showToast("截屏出错,文件保存失败!");
		// break;
		// }
	}

	public void onTryonObjectAttributeChanged(String attr, float percent) {
		LogUtil.v(TAG, "onTryonObjectAttributeChanged: " + attr + "; "
				+ percent);
		// mNeedScreenshot = true;
	}

	public void onErasureStateChanged(String state) {
		LogUtil.v(TAG, "onErasureStateChanged: " + state);
		// updateEraseStateAndSize(state, mEraseBrushSizePercent);
	}

	public void onErasureBrushSizeChanged(float percent) {
		LogUtil.v(TAG, "onErasureBrushSizeChanged: " + percent);
		// updateEraseStateAndSize(mEraseState, percent);
	}

	/** 返回首页 */
	public static void backToHomepage(Activity activity, int index,
			AnimatorListener listener) {
		if (activity != instance)
			activity.finish();
		if (instance != null)
			instance.backToHomepage(index, listener);
	}

	/** 返回首页 */
	public static void backToHomepage(Activity activity, int index) {
		backToHomepage(activity, index, null);
	}

	/** 返回首页 */
	public static void backToHomepage(Activity activity) {
		backToHomepage(activity, 0);
	}

	public void backToHomepage(final int index, final AnimatorListener listener) {
		backToMainUI(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				if (listener != null)
					listener.onAnimationEnd(animation);
				LogUtil.d(TAG, "backToHomepage: "
						+ getSupportFragmentManager().getBackStackEntryCount());
				if (Product3DFragment.instance != null)
					Product3DFragment.instance.onBackPressed();
				if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
					getSupportFragmentManager().popBackStack(null,
							FragmentManager.POP_BACK_STACK_INCLUSIVE);
				}
				if (MainFragment.instance != null)
					MainFragment.instance.setCurrentFragment(index);
			}
		});
	}
}
