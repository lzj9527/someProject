package android.extend.app.fragment;

import java.util.List;

import org.apache.http.NameValuePair;

import android.extend.util.ResourceUtil;
import android.extend.widget.ExtendWebView;
import android.extend.widget.ExtendWebView.OnWebViewListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SwipeRefreshWebViewFragment extends ExtendWebViewFragment implements OnRefreshListener, OnWebViewListener
{
	protected SwipeRefreshLayout mSwipeRefreshLayout;

	public SwipeRefreshWebViewFragment(String firstUrl, List<NameValuePair> firstRequestPairs,
			List<NameValuePair> baseRequestPairs, long firstDelay)
	{
		super(firstUrl, firstRequestPairs, baseRequestPairs, firstDelay);
	}

	public SwipeRefreshWebViewFragment(String firstUrl, List<NameValuePair> firstRequestPairs,
			List<NameValuePair> baseRequestPairs)
	{
		super(firstUrl, firstRequestPairs, baseRequestPairs);
	}

	public SwipeRefreshWebViewFragment(String firstUrl)
	{
		super(firstUrl);
	}

	public SwipeRefreshLayout getSwipeRefreshLayout()
	{
		return mSwipeRefreshLayout;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		if (mLayoutResID == 0)
			mLayoutResID = ResourceUtil.getLayoutId(getContext(), "default_swiperefresh_webview");
		View view = super.onCreateView(inflater, container, savedInstanceState);

		mWebView.setOnWebViewListener(this);

		if (view instanceof SwipeRefreshLayout)
		{
			mSwipeRefreshLayout = (SwipeRefreshLayout)view;
		}
		else
		{
			int id = ResourceUtil.getId(getContext(), "swipe_refresh");
			mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(id);
		}
		if (mSwipeRefreshLayout != null)
		{
			mSwipeRefreshLayout
					.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
							android.R.color.holo_orange_light, android.R.color.holo_green_light);
			mSwipeRefreshLayout.setOnRefreshListener(this);
		}
		return view;
	}

	@Override
	public void onPageLoadStarted(ExtendWebView webView, String url)
	{
		if (mSwipeRefreshLayout != null && !mSwipeRefreshLayout.isRefreshing())
			mSwipeRefreshLayout.setRefreshing(true);
	}

	@Override
	public void onPageLoadFinished(ExtendWebView webView, String url)
	{
		if (mSwipeRefreshLayout != null)
			mSwipeRefreshLayout.setRefreshing(false);
	}

	@Override
	public void onLoadProgressChanged(ExtendWebView webView, int newProgress)
	{
	}

	@Override
	public void onScaleChanged(ExtendWebView webView, float oldScale, float newScale)
	{
	}

	@Override
	public void onReceivedError(ExtendWebView webView, int errorCode, String description, String failingUrl)
	{
	}

	@Override
	public void onRefresh()
	{
		refresh();
	}

	public void setRefreshing(boolean refreshing)
	{
		if (mSwipeRefreshLayout != null)
			mSwipeRefreshLayout.setRefreshing(refreshing);
	}
}
