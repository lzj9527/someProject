package com.shiyou.tryapp2.app;

import java.io.File;
import java.util.List;

import org.apache.http.NameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.extend.ErrorInfo;
import android.extend.app.BaseFragment;
import android.extend.app.fragment.SwipeRefreshWebViewFragment;
import android.extend.cache.FileCacheManager;
import android.extend.loader.HttpLoader;
import android.extend.util.AndroidUtils;
import android.extend.util.FileUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.widget.ExtendWebView;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.shiyou.tryapp2.Config;
import com.shiyou.tryapp2.Define;
import com.shiyou.tryapp2.FileDownloadHelper;
import com.shiyou.tryapp2.FileDownloadHelper.OnFileDownloadCallback;
import com.shiyou.tryapp2.LaunchImagesHelper;
import com.shiyou.tryapp2.app.login.LoginHelper;
import com.shiyou.tryapp2.app.product.GiaBargainFragment;
import com.shiyou.tryapp2.app.product.MainWebViewFragment;
import com.shiyou.tryapp2.app.product.PDFViewerFragment;
import com.shiyou.tryapp2.app.product.Product3DFragment;
import com.shiyou.tryapp2.app.product.ProductDetailsFragment;
import com.shiyou.tryapp2.data.FileInfo;
import com.unity3d.player.UnityPlayer;

public class WebViewFragment extends SwipeRefreshWebViewFragment
{
	public static WebViewFragment instance = null;

	public static File getAppCacheDirectory(Context context)
	{
		return FileUtils.getDirectory(context, "appcache");
	}

	boolean mPriceSetChanged = false;
	boolean mShowPriceSetDialog = false;
	Dialog mPriceSetDialog;

	public WebViewFragment(String firstUrl, List<NameValuePair> firstRequestPairs, List<NameValuePair> baseRequestPairs)
	{
		super(firstUrl, firstRequestPairs, baseRequestPairs);
		instance = this;
	}

	public WebViewFragment(String firstUrl)
	{
		super(firstUrl);
		instance = this;
	}

	
	

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);

		initWebView();

		return view;
	}

	@Override
	public boolean onBackPressed()
	{
		LogUtil.d(TAG, "onBackPressed...");
		if (mPriceSetChanged)
		{
			showPriceSetDialog();
			return true;
		}
		return false;
	}

	private void showPriceSetDialog()
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (mPriceSetDialog == null)
				{
					// AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					// builder.setIcon(android.R.drawable.ic_dialog_alert);
					// builder.setTitle("提示");
					// builder.setMessage("门店价格系数已改变，是否需要保存？");
					// builder.setPositiveButton("是", new DialogInterface.OnClickListener()
					// {
					// @Override
					// public void onClick(DialogInterface dialog, int which)
					// {
					// hidePriceSetDialog();
					// showLoadingIndicator(3 * 1000L);
					// saveShopPrice();
					// }
					// });
					// builder.setNegativeButton("否", new DialogInterface.OnClickListener()
					// {
					// @Override
					// public void onClick(DialogInterface dialog, int which)
					// {
					// mPriceSetChanged = false;
					// hidePriceSetDialog();
					// getActivity().onBackPressed();
					// }
					// });
					// mPriceSetDialog = builder.create();
					// mPriceSetDialog.show();

					int layout = ResourceUtil.getLayoutId(getContext(), "confirm_dialog");
					View view = View.inflate(getContext(), layout, null);
					mPriceSetDialog = AndroidUtils.createDialog(getActivity(), view, true, true);
					int id = ResourceUtil.getId(getContext(), "message");
					TextView message = (TextView)view.findViewById(id);
					message.setText("门店价格系数已改变，是否需要保存？");
					id = ResourceUtil.getId(getContext(), "confirm");
					TextView confirm = (TextView)view.findViewById(id);
					confirm.setText("是");
					confirm.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							hidePriceSetDialog();
							showLoadingIndicator(3 * 1000L);
							saveShopPrice();
						}
					});
					id = ResourceUtil.getId(getContext(), "cancel");
					TextView cancel = (TextView)view.findViewById(id);
					cancel.setText("否");
					cancel.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							mPriceSetChanged = false;
							hidePriceSetDialog();
							getActivity().onBackPressed();
						}
					});
					mPriceSetDialog.show();

					mShowPriceSetDialog = true;
				}
			}
		});
	}

	private void hidePriceSetDialog()
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()                            
			{
				if (mPriceSetDialog != null)
				{
					mPriceSetDialog.dismiss();
					mPriceSetDialog = null;
				}
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK)
		{
			switch (requestCode)
			{
				case Define.REQ_LOGIN_FROM_MY:
					refresh();
					break;
			}
		}
	}

	@Override
	public void onPageLoadStarted(ExtendWebView webView, String url)
	{
		super.onPageLoadStarted(webView, url);
		// showLoadingIndicator();
	}

	@Override
	public void onPageLoadFinished(ExtendWebView webView, String url)
	{
		super.onPageLoadFinished(webView, url);
		// hideLoadingIndicator();
	}

	//  从父类获取创建的WebView,初始化并设置参数
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView()
	{
		getWebView().getSettings().setJavaScriptEnabled(true);
		getWebView().addJavascriptInterface(new JavaScriptInterface(), "android");

		String appCacheDirPath = getAppCacheDirectory(getActivity()).getAbsolutePath();
		LogUtil.e(TAG, "appCacheDirPath=" + appCacheDirPath);
		getWebView().getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		getWebView().getSettings().setDomStorageEnabled(true);
		// getWebView().getSettings().setDatabasePath(databasePath);
		getWebView().getSettings().setDatabaseEnabled(true);
		getWebView().getSettings().setAppCachePath(appCacheDirPath);
		getWebView().getSettings().setAppCacheEnabled(true);
		getWebView().getSettings().setAllowContentAccess(true);
		getWebView().getSettings().setAllowFileAccess(true);
		getWebView().getSettings().setAllowFileAccessFromFileURLs(true);
		getWebView().getSettings().setAllowUniversalAccessFromFileURLs(true);
	}

	public void clearCache()
	{
		getWebView().clearCache(true);
		FileUtils.deleteDirectory(getAppCacheDirectory(getActivity()));
	}

	// 购物车删除
	public void deleteShoppingCart()
	{
		LogUtil.w(TAG, "删除购物车");
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				getWebView().loadUrl("javascript:deleteShoppingCartFromNative()");
			}
		});
	}

	// 门店价格设置保存
	public void saveShopPrice()
	{
		LogUtil.w(TAG, "门店价格设置保存");
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				getWebView().loadUrl("javascript:saveShopGiaPriceFromNative()");
				getWebView().loadUrl("javascript:saveShopPriceFromNative()");
			}
		});
	}

	// 申请添加门店
	public void applyForAddShop()
	{
		LogUtil.w(TAG, "申请添加门店");
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				getWebView().loadUrl("javascript:applyForAddShopFromNative()");
			}
		});
	}
	// 添加购物车
	private void showAddShoppingcartSuccessDialog()
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				LayoutInflater mLayoutInflater = (LayoutInflater)getContext().getSystemService("layout_inflater");
				int layout = ResourceUtil.getLayoutId(getContext(), "add_shoppingcart_success_dialog");
				View product_information = mLayoutInflater.inflate(layout, null);
				final PopupWindow pw = new PopupWindow(product_information, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);

				int id = ResourceUtil.getId(getContext(), "loke_shopping");
				View loke_shopping = product_information.findViewById(id);
				loke_shopping.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						if (AndroidUtils.isFastClick())
							return;
						pw.dismiss();
						MainActivity.backToHomepage(getActivity(), 1);
					}
				});

				id = ResourceUtil.getId(getContext(), "loke");
				View loke = product_information.findViewById(id);
				loke.setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						if (AndroidUtils.isFastClick())
							return;
						pw.dismiss();
					}
				});

				product_information.setFocusable(true);
				product_information.setFocusableInTouchMode(true);
				pw.setBackgroundDrawable(new ColorDrawable(0x00));
				pw.setFocusable(true);
				pw.setTouchable(true);
				pw.setOutsideTouchable(true);
				pw.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
				pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				pw.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			}
		});
	}

	protected void onAddShoppingCartSucceed()
	{
	}

	public void downloadGIACerFile(String title, String url)
	{
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
		{
			Log.d(TAG, "downloadGIACerFile: 执行");
			add(getActivity(), new PDFViewerFragment(title, url), true);
			return;
		}
		showLoadingIndicator();
		FileInfo fileInfo = new FileInfo();
		fileInfo.url = url;
		fileInfo.path = PDFViewerFragment.getPDFDirectoryPath(getContext()) + File.separatorChar + title + ".pdf";
		FileDownloadHelper.checkAndDownloadIfNeed(getContext(), TAG, fileInfo, new OnFileDownloadCallback()
		{
			@Override
			public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
			{
			}

			@Override
			public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath, long count, long length,
					float speed)
			{
			}

			@Override
			public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath)
			{
				hideLoadingIndicator();
				AndroidUtils.launchPDFFile(getContext(), localPath);
				Log.d(TAG, "onDownloadFinished: activity="+getActivity().toString());
			}

			@Override
			public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error)
			{
				hideLoadingIndicator();
				showToast("下载GIA证书失败，错误码: " + error.errorCode);
			}

			@Override
			public void onDownloadCanceled(Object tag, FileInfo fileInfo)
			{
			}
		}, false);
	}

	public class JavaScriptInterface
	{
		// 打开新页面
		@JavascriptInterface
		public void openWindow(final int index, final String title, final String url)
		{
			LogUtil.v(TAG, "openWindow: " + index + "; " + title + "; " + url);
			AndroidUtils.MainHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					String actualUrl = url;
					if (url.contains("/app/default"))
					{
						actualUrl = Config.BaseWebUrl + url.substring(url.indexOf("/app/default"));
					}
					LogUtil.d(TAG, "openWindow: actualUrl=" + actualUrl);
					switch (index)
					{
						case 1:// 登录
							actualUrl = Config.WebLogin + "?pushRegId="
									+ JPushInterface.getRegistrationID(getContext());
							replace(getActivity(), new MainWebViewFragment(actualUrl, "", 0), false);
							break;
						case 2:// 设置
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 2, true), true);
							break;
						case 3:// 搜索进产品列表1
								// add(MainFragment.instance, MainFragment.instance.fragmentC1ID, new
								// MainWebViewFragment(
								// actualUrl, title, 2), true);
								// MainFragment.instance.addFragmentToCurrent(new MainWebViewFragment(actualUrl, title,
								// 2), false);
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 2), true);
							break;
						case 7:// 我的购物车
						case 4:
							MainActivity.backToHomepage(getActivity(), 1);
							if (Product3DFragment.instance != null)
							{
								Product3DFragment.instance.onBackPressed();
							}
							break;
						case 5:// 确认订单
							add(getActivity(), new MainWebViewFragment(actualUrl, "确认订单", 9, true), true);
							break;
						case 6:// 分类进产品列表1
								// add(MainFragment.instance, MainFragment.instance.fragmentC1ID, new
								// MainWebViewFragment(
								// actualUrl, title, 1), true);
								// MainFragment.instance.addFragmentToCurrent(new MainWebViewFragment(actualUrl, title,
								// 1), false);
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 1), true);
							break;
						case 8:// 订单
								// String[] stutas = url.split("=");
								// if (stutas.length == 2)
								// {
							MainFragment.instance.setCurrentFragmentToOrder(actualUrl);
							// }
							// else
							// {
							// MainFragment.instance.setCurrentFragment(2);
							// }
							break;
						case 9:// 门店价格设置
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 5, true), true);
							break;
						case 10:// 裸钻
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 3, true), true);
							break;
						case 11:// 门店地址管理
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 10, true), true);
							break;
						case 12:// 店铺公告管理
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 2, true), true);
							break;
						case 13:// 销售记录
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 2, true), true);
							break;
						case 14:// 新建收货信息
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 2, true), true);
							break;
						case 15:// 添加收货信息
							getActivity().onBackPressed();
							break;
						case 16:// 订单详情
							add(getActivity(), new MainWebViewFragment(actualUrl, title , 2, true), true);
							break;
						case 17:// 订单
							MainActivity.backToHomepage(getActivity(), 2);
							break;
						case 18://
							if (MainWebViewFragment.instance.boss_details_title != null)
							{
								MainWebViewFragment.instance.boss_details_title.setText(title);
							}
							break;
						case 19:// 报价表
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 2, true), true);
							break;
							
						case 20:// 积分
							add(getActivity(), new MainWebViewFragment(actualUrl, title, 2, true), true);
							break;
					}
				}
			});
		}

		// 打开弹出页面
		@JavascriptInterface
		public void openPopWindow(final int index, final String title, final String url)
		{
			LogUtil.v(TAG, "openPopWindow: " + index + "; " + title + "; " + url);
		}

		private int[] priceRange;
		private float[] weightRange;
		private String colorCondition;
		private String clarityCondition;

		@JavascriptInterface
		public void setFilterCondition(final String price, final String weight, final String color, final String clarity)
		{
			LogUtil.v(TAG, "setFilterCondition: " + price + "; " + weight + "; " + color + "; " + clarity);
			priceRange = null;
			weightRange = null;
			colorCondition = null;
			clarityCondition = null;
			if (!TextUtils.isEmpty(price))
			{
				if (!price.equals("0"))
				{
					priceRange = new int[2];
					if (price.contains("-"))
					{
						String[] split = price.split("-");
						priceRange[0] = Integer.parseInt(split[0]);
						priceRange[1] = Integer.parseInt(split[1]);
					}
					else
					{
						priceRange[0] = Integer.parseInt(price);
						priceRange[1] = Integer.MAX_VALUE;
					}
				}
			}
			if (!TextUtils.isEmpty(weight))
			{
				if (!weight.equals("0"))
				{
					weightRange = new float[2];
					if (weight.contains("-"))
					{
						String[] split = weight.split("-");
						weightRange[0] = Float.parseFloat(split[0]);
						weightRange[1] = Float.parseFloat(split[1]);
					}
					else
					{
						weightRange[0] = Float.parseFloat(weight);
						weightRange[1] = Float.MAX_VALUE;
					}
				}
			}
			colorCondition = color;
			clarityCondition = clarity;
		}

		// 打开详情页
		@JavascriptInterface
		public void openDetailWindow(final String goodsId, final String url)
		{
			LogUtil.v(TAG, "openDetailWindow: " + goodsId + "; " + url);
			add(getActivity(), new ProductDetailsFragment(goodsId, false, priceRange, weightRange, colorCondition,
					clarityCondition), true);
		}

		// 打开对戒详情页
		@JavascriptInterface
		public void openDetailWindowInCoupleRing(final String goodsId, final String url)
		{
			LogUtil.v(TAG, "openDetailWindowInCoupleRing: " + goodsId + "; " + url);
			add(getActivity(), new ProductDetailsFragment(goodsId, true, priceRange, weightRange, colorCondition,
					clarityCondition), true);
		}

		// JIA选钻后打开详情页
		@JavascriptInterface
		public void openDetailWindowFromJIA(final String goodsId, final String url, final String jiaJson)
		{
			LogUtil.v(TAG, "openDetailWindowFromJIA: " + goodsId + "; " + url + "; " + jiaJson);
			// add(MainFragment.instance, MainFragment.instance.fragmentC1ID, new RingsDetailsFragment(goodsId,
			// jiaJson),
			// true);
			MainFragment.instance.invalidateMenuBar();
		}

		// JIA选钻后添加至购物车
		@JavascriptInterface
		public void appendShoppingCartFromJIA(final String goodsId, final String jiaJson)
		{
			LogUtil.v(TAG, "appendShoppingCartFromJIA: " + goodsId + "; " + jiaJson);
		}

		// 登录或注册完成
		@JavascriptInterface
		public void onLoginFinished(final String userName, final String userKey, final String isEmployee)
		{
			LogUtil.v(TAG, "onLoginFinished: " + userName + "; " + userKey);
			LoginHelper.onLoginFinished(getContext(), userName, userKey);
			if (getActivity() != MainActivity.instance)
				getActivity().finish();
			else
			{
				// new ResourceHelper(getActivity()).checkAndLoadResource(new OnResourceDownloadCallback()
				// {
				// @Override
				// public void onDownloadFinished(Object data)
				// {
				// AndroidUtils.MainHandler.post(new Runnable()
				// {
				// @Override
				// public void run()
				// {
				BaseFragment.replace(getActivity(), new MainFragment(), false);
				// }
				// });
				// }
				// });
			}
		}

		// 登录或注册失败
		@JavascriptInterface
		public void onLoginFailed(final String errorText)
		{
			LogUtil.v(TAG, "onLoginFailed: " + errorText);
			showToast(errorText);
		}

		@JavascriptInterface
		public void getGia(String title,String s){
			downloadGIACerFile(title,s);
		}

		// 门店价格设置有改变
		@JavascriptInterface
		public void onPriceSetChanged()
		{
			LogUtil.v(TAG, "onPriceSetChanged...");
			mPriceSetChanged = true;
		}

		// 门店价格设置成功
		@JavascriptInterface
		public void onPriceSetSucceed()
		{
			LogUtil.v(TAG, "onPriceSetSucceed...");
			hideLoadingIndicator();
			mPriceSetChanged = false;
			if (mShowPriceSetDialog)
			{
				mShowPriceSetDialog = false;
				AndroidUtils.MainHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						getActivity().onBackPressed();
					}
				});
			}
		}
		
		// 单纯的返回  20171225
		@JavascriptInterface
		public void onBackPressed(){
			AndroidUtils.MainHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					getActivity().onBackPressed();
				}
			});
		}

		// 购物车添加成功
		@JavascriptInterface
		public void onAddShoppingCartSucceed()
		{
			LogUtil.v(TAG, "onAddShoppingCartSucceed...");
			hideLoadingIndicator();
			if (MainFragment.instance != null)
				MainFragment.instance.updateShoppingcartBadgeNum();
			showAddShoppingcartSuccessDialog();
			WebViewFragment.this.onAddShoppingCartSucceed();
		}

		// 购物车删除成功
		@JavascriptInterface
		public void onDeleteShoppingCartSucceed()
		{
			LogUtil.v(TAG, "onDeleteShoppingCartSucceed...");
			hideLoadingIndicator();
			if (MainFragment.instance != null)
				MainFragment.instance.updateShoppingcartBadgeNum();
		}

		// 下载JIA证书
		@JavascriptInterface
		public void downloadJIACer(final String url)
		{
			LogUtil.v(TAG, "downloadJIACer: " + url);
			// String temp =
			// "https://www.gia.edu/otmm_wcs_int/proxy-pdf/?ReportNumber=5243390559&url=https://myapps.gia.edu/RptChkClient/reportClient.do?ReportNumber=FCD06BADED5FE6200724F654063A16A5";
			// add(getActivity(), new PDFViewerFragment("5243390559", temp), true);
			WebViewFragment.this.downloadGIACerFile("-", url);
		}

		// 下载GIA证书
		@JavascriptInterface
		public void downloadGIACer(final String title, final String url)
		{
			LogUtil.v(TAG, "downloadGIACer: " + title + "; " + url);
			// add(getActivity(), new PDFViewerFragment(title, url), true);

			add(getActivity(),new MainWebViewFragment(url,title,8,instance),true);
		}

		// 清理缓存
		@JavascriptInterface
		public void clearCache()
		{
			LogUtil.v(TAG, "clearCache...");
			showLoadingIndicator();
			AndroidUtils.MainHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					LogUtil.d(TAG, "start clear cache...");
					final long startTime = System.currentTimeMillis();
					WebViewFragment.this.clearCache();
					try
					{
						UnityPlayer.UnitySendMessage("PlatformMessageHandler", "NotifyCleanCache", "");
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							LaunchImagesHelper.clearCache();
							HttpLoader.clearMemoryCache();
							FileCacheManager.clearAllCaches(getActivity());
							FileDownloadHelper.clearAllDownloadedFile(getContext());
							long time = System.currentTimeMillis() - startTime;
							LogUtil.v(TAG, "clear cache finished, time = " + time);
							hideLoadingIndicator();
							showToast("缓存清理已完成.");
						}
					}).start();
				}
			});
		}

		// 登录
		@JavascriptInterface
		public void login()
		{
			LogUtil.v(TAG, "login...");
			// AndroidUtils.MainHandler.post(new Runnable()
			// {
			// @Override
			// public void run()
			// {
			// }
			// });
		}

		// 注册
		@JavascriptInterface
		public void register()
		{
			LogUtil.v(TAG, "register...");
			// AndroidUtils.MainHandler.post(new Runnable()
			// {
			// @Override
			// public void run()
			// {
			// }
			// });
		}

		// 检查更新
		@JavascriptInterface
		public void checkVersion()
		{
			LogUtil.v(TAG, "checkVersion...");
			LoginHelper.checkVersion(getActivity(), false);
		}

		// 提示信息
		@JavascriptInterface
		public void showToast(String text)
		{
			AndroidUtils.showToast(getActivity(), text);
			// if (text.contains("购物车")) {
			// showAddShoppingcartSuccessDialog();
			// }
		}

		// 刷新当前页面
		@JavascriptInterface
		public void refresh()
		{
			LogUtil.v(TAG, "refresh...");
			AndroidUtils.MainHandler.post(new Runnable()
			{
				@Override
				public void run()
				{
					WebViewFragment.this.refresh();
				}
			});
		}

		// 显示加载动画
		@JavascriptInterface
		public void showLoadingIndicator()
		{
			WebViewFragment.this.showLoadingIndicator(true);
		}

		// 隐藏加载动画
		@JavascriptInterface
		public void hideLoadingIndicator()
		{
			WebViewFragment.this.hideLoadingIndicator();
		}
		
		//主页轮播图片跳转
		@JavascriptInterface
		public void openDetailWindowInDiamond(String a, String b){
			LogUtil.v(TAG, "openDetailWindowInDiamond(). ");
			add(getActivity(), new GiaBargainFragment(), true);
		}
		
		@JavascriptInterface
		public void openHrefToWechat(){
			showToast("openHrefToWechat");
			Intent intent= new Intent();        
		    intent.setAction("android.intent.action.VIEW");    
		    Uri content_url = Uri.parse("https://u.wechat.com/MIzHs7iDh93KDL_CFOiwN4M");   
		    intent.setData(content_url);  
		    startActivity(intent);
		}
	}
	
}
