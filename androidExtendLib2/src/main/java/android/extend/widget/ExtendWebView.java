package android.extend.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.content.Context;
import android.extend.util.HttpUtils;
import android.extend.util.LogUtil;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.ConsoleMessage;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ExtendWebView extends WebView
{
	public interface OnWebViewListener
	{
		public void onPageLoadStarted(ExtendWebView webView, String url);

		public void onPageLoadFinished(ExtendWebView webView, String url);

		public void onLoadProgressChanged(ExtendWebView webView, int newProgress);

		public void onScaleChanged(ExtendWebView webView, float oldScale, float newScale);

		public void onReceivedError(ExtendWebView webView, int errorCode, String description, String failingUrl);
	}

	public final String TAG = getClass().getSimpleName();

	private OnWebViewListener mListener;
	private List<NameValuePair> mBaseUrlRequestPairs = new ArrayList<NameValuePair>();

	private WebViewClient mExtWebViewClient;
	private WebChromeClient mExtWebChromeClient;

	@SuppressWarnings("deprecation")
	public ExtendWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing)
	{
		super(context, attrs, defStyle, privateBrowsing);
		init();
	}

	public ExtendWebView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public ExtendWebView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public ExtendWebView(Context context)
	{
		super(context);
		init();
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void init()
	{
		try
		{
			// setInitialScale(100);
			getSettings().setJavaScriptEnabled(true);
			getSettings().setSupportZoom(true);
			getSettings().setBuiltInZoomControls(true);
			getSettings().setDisplayZoomControls(false);
			// getSettings().setDefaultZoom(ZoomDensity.CLOSE);
			getSettings().setUseWideViewPort(true);
			getSettings().setLoadWithOverviewMode(true);
			getSettings().setAllowContentAccess(true);
			getSettings().setAllowFileAccess(true);
			super.setWebViewClient(mWebViewClient);
			super.setWebChromeClient(mWebChromeClient);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setOnWebViewListener(OnWebViewListener listener)
	{
		mListener = listener;
	}

	public void setWebViewClient(WebViewClient client)
	{
		mExtWebViewClient = client;
	}

	public void setWebChromeClient(WebChromeClient client)
	{
		mExtWebChromeClient = client;
	}

	public void addBaseUrlRequestPair(String name, String value)
	{
		mBaseUrlRequestPairs.add(new BasicNameValuePair(name, value));
	}

	public void addBaseUrlRequestPair(NameValuePair pair)
	{
		mBaseUrlRequestPairs.add(pair);
	}

	public void addBaseUrlRequestPairs(Collection<? extends NameValuePair> pairs)
	{
		mBaseUrlRequestPairs.addAll(pairs);
	}

	public void removeBaseUrlRequestPair(int location)
	{
		mBaseUrlRequestPairs.remove(location);
	}

	public void removeBaseUrlRequestPair(String name)
	{
		for (NameValuePair pair : mBaseUrlRequestPairs)
		{
			if (pair.getName().equals(name))
			{
				mBaseUrlRequestPairs.remove(pair);
				return;
			}
		}
	}

	public void clearBaseUrlRequestPairs()
	{
		mBaseUrlRequestPairs.clear();
	}

	public NameValuePair[] getBaseUrlRequestPairs()
	{
		if (mBaseUrlRequestPairs.isEmpty())
			return null;
		NameValuePair[] pairs = new NameValuePair[mBaseUrlRequestPairs.size()];
		return mBaseUrlRequestPairs.toArray(pairs);
	}

	public List<NameValuePair> getBaseUrlRequestPairsList()
	{
		return mBaseUrlRequestPairs;
	}

	@Override
	public void loadUrl(String url, Map<String, String> additionalHttpHeaders)
	{
		url = HttpUtils.makeHttpGetUrl(url, mBaseUrlRequestPairs);
		super.loadUrl(url, additionalHttpHeaders);
	}

	public void loadUrl(String url, Map<String, String> additionalHttpHeaders, List<NameValuePair> urlRequestPairs)
	{
		url = HttpUtils.makeHttpGetUrl(url, urlRequestPairs);
		loadUrl(url, additionalHttpHeaders);
	}

	@Override
	public void loadUrl(String url)
	{
		url = HttpUtils.makeHttpGetUrl(url, mBaseUrlRequestPairs);
		super.loadUrl(url);
	}

	public void loadUrl(String url, List<NameValuePair> urlRequestPairs)
	{
		url = HttpUtils.makeHttpGetUrl(url, urlRequestPairs);
		loadUrl(url);
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		// stopLoading();
		destroy();
	}

	@Override
	public void destroy()
	{
		try
		{
			// View rootView = getRootView();
			// if (rootView != null && rootView instanceof ViewGroup)
			// AndroidUtils.removeAllViewsInChildren((ViewGroup)rootView);
			// View parentView = (View)getParent();
			// if (parentView != null && rootView instanceof ViewGroup)
			// AndroidUtils.removeAllViewsInChildren((ViewGroup)parentView);
			this.removeAllViews();
			super.stopLoading();
			super.destroy();
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "", e);
		}
	}

	private WebViewClient mWebViewClient = new WebViewClient()
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			LogUtil.v(TAG, "shouldOverrideUrlLoading: " + url);
			// if (url.startsWith("http") || url.startsWith("https"))
			// {
			// // url = HttpUtils.makeHttpGetUrl(url, mBaseUrlRequestPairs);
			// view.loadUrl(url);
			// }
			// else
			// {
			// AndroidUtils.launchBrowser(getContext(), url);
			// }
			if (mExtWebViewClient != null)
				return mExtWebViewClient.shouldOverrideUrlLoading(view, url);
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			LogUtil.v(TAG, "onPageStarted: " + url);
			super.onPageStarted(view, url, favicon);
			if (mListener != null)
				mListener.onPageLoadStarted(ExtendWebView.this, url);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			LogUtil.v(TAG, "onPageFinished: " + url);
			super.onPageFinished(view, url);
			if (mListener != null)
				mListener.onPageLoadFinished(ExtendWebView.this, url);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onPageFinished(view, url);
		}

		@Override
		public void onLoadResource(WebView view, String url)
		{
			LogUtil.v(TAG, "onLoadResource: " + url);
			super.onLoadResource(view, url);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onLoadResource(view, url);
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url)
		{
			LogUtil.v(TAG, "shouldInterceptRequest: " + url);
			if (mExtWebViewClient != null)
				return mExtWebViewClient.shouldInterceptRequest(view, url);
			return super.shouldInterceptRequest(view, url);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			LogUtil.v(TAG, "onReceivedError: " + errorCode + "; " + description + "; " + failingUrl);
			super.onReceivedError(view, errorCode, description, failingUrl);
			if (mListener != null)
				mListener.onReceivedError(ExtendWebView.this, errorCode, description, failingUrl);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onFormResubmission(WebView view, Message dontResend, Message resend)
		{
			LogUtil.v(TAG, "onFormResubmission: " + dontResend + "; " + resend);
			super.onFormResubmission(view, dontResend, resend);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onFormResubmission(view, dontResend, resend);
		}

		@Override
		public void doUpdateVisitedHistory(WebView view, String url, boolean isReload)
		{
			LogUtil.v(TAG, "doUpdateVisitedHistory: " + url + "; " + isReload);
			super.doUpdateVisitedHistory(view, url, isReload);
			if (mExtWebViewClient != null)
				mExtWebViewClient.doUpdateVisitedHistory(view, url, isReload);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
		{
			LogUtil.v(TAG, "onReceivedSslError: " + handler + "; " + error);
			super.onReceivedSslError(view, handler, error);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onReceivedSslError(view, handler, error);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm)
		{
			LogUtil.v(TAG, "onReceivedHttpAuthRequest: " + handler + "; " + host + "; " + realm);
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event)
		{
			LogUtil.v(TAG, "shouldOverrideKeyEvent: " + event);
			if (mExtWebViewClient != null)
				return mExtWebViewClient.shouldOverrideKeyEvent(view, event);
			return super.shouldOverrideKeyEvent(view, event);
		}

		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event)
		{
			LogUtil.v(TAG, "onUnhandledKeyEvent: " + event);
			super.onUnhandledKeyEvent(view, event);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onUnhandledKeyEvent(view, event);
		}

		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale)
		{
			LogUtil.v(TAG, "onScaleChanged: " + oldScale + "; " + newScale);
			super.onScaleChanged(view, oldScale, newScale);
			if (mListener != null)
				mListener.onScaleChanged(ExtendWebView.this, oldScale, newScale);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onScaleChanged(view, oldScale, newScale);
		}

		@Override
		public void onReceivedLoginRequest(WebView view, String realm, String account, String args)
		{
			LogUtil.v(TAG, "onReceivedLoginRequest: " + realm + "; " + account + "; " + args);
			super.onReceivedLoginRequest(view, realm, account, args);
			if (mExtWebViewClient != null)
				mExtWebViewClient.onReceivedLoginRequest(view, realm, account, args);
		}
	};

	private WebChromeClient mWebChromeClient = new WebChromeClient()
	{
		@Override
		public void onProgressChanged(WebView view, int newProgress)
		{
			LogUtil.v(TAG, "onProgressChanged: " + newProgress);
			super.onProgressChanged(view, newProgress);
			if (mListener != null && newProgress < 100)
				mListener.onLoadProgressChanged(ExtendWebView.this, newProgress);
		}

		// @Override
		// public void onReceivedTitle(WebView view, String title)
		// {
		// LogUtil.v(TAG, "onReceivedTitle: " + title);
		// super.onReceivedTitle(view, title);
		// }
		//
		// @Override
		// public void onReceivedIcon(WebView view, Bitmap icon)
		// {
		// LogUtil.v(TAG, "onReceivedTitle: " + icon);
		// super.onReceivedIcon(view, icon);
		// }
		//
		// @Override
		// public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed)
		// {
		// LogUtil.v(TAG, "onReceivedTouchIconUrl: " + url + "; " + precomposed);
		// super.onReceivedTouchIconUrl(view, url, precomposed);
		// }
		//
		// @Override
		// public void onShowCustomView(View view, CustomViewCallback callback)
		// {
		// LogUtil.v(TAG, "onShowCustomView: " + view + "; " + callback);
		// super.onShowCustomView(view, callback);
		// }
		//
		// @Override
		// public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback)
		// {
		// LogUtil.v(TAG, "onShowCustomView: " + view + ";" + requestedOrientation + "; " + callback);
		// super.onShowCustomView(view, requestedOrientation, callback);
		// }
		//
		// @Override
		// public void onHideCustomView()
		// {
		// LogUtil.v(TAG, "onHideCustomView");
		// super.onHideCustomView();
		// }
		//
		// @Override
		// public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg)
		// {
		// LogUtil.v(TAG, "onCreateWindow: " + isDialog + "; " + isUserGesture + "; " + resultMsg);
		// return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
		// }
		//
		// @Override
		// public void onRequestFocus(WebView view)
		// {
		// LogUtil.v(TAG, "onRequestFocus: " + view);
		// super.onRequestFocus(view);
		// }
		//
		// @Override
		// public void onCloseWindow(WebView window)
		// {
		// LogUtil.v(TAG, "onCloseWindow");
		// super.onCloseWindow(window);
		// }
		//
		// @Override
		// public boolean onJsAlert(WebView view, String url, String message, JsResult result)
		// {
		// LogUtil.v(TAG, "onJsAlert: " + url + "; " + message + "; " + result);
		// return super.onJsAlert(view, url, message, result);
		// }
		//
		// @Override
		// public boolean onJsConfirm(WebView view, String url, String message, JsResult result)
		// {
		// LogUtil.v(TAG, "onJsConfirm: " + url + "; " + message + "; " + result);
		// return super.onJsConfirm(view, url, message, result);
		// }
		//
		// @Override
		// public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult
		// result)
		// {
		// LogUtil.v(TAG, "onJsPrompt: " + url + "; " + message + "; " + defaultValue + "; " + result);
		// return super.onJsPrompt(view, url, message, defaultValue, result);
		// }
		//
		// @Override
		// public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result)
		// {
		// LogUtil.v(TAG, "onJsBeforeUnload: " + url + "; " + message + "; " + result);
		// return super.onJsBeforeUnload(view, url, message, result);
		// }
		//
		// @Override
		// public void onGeolocationPermissionsShowPrompt(String origin, Callback callback)
		// {
		// LogUtil.v(TAG, "onGeolocationPermissionsShowPrompt: " + origin + "; " + callback);
		// super.onGeolocationPermissionsShowPrompt(origin, callback);
		// }
		//
		// @Override
		// public void onGeolocationPermissionsHidePrompt()
		// {
		// LogUtil.v(TAG, "onGeolocationPermissionsHidePrompt");
		// super.onGeolocationPermissionsHidePrompt();
		// }
		//
		// @Override
		// public boolean onJsTimeout()
		// {
		// LogUtil.v(TAG, "onJsTimeout");
		// return super.onJsTimeout();
		// }

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage)
		{
			LogUtil.v(TAG, "onConsoleMessage: " + consoleMessage.message());
			return super.onConsoleMessage(consoleMessage);
		}
	};
}
