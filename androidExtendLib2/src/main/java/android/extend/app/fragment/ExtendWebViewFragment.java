package android.extend.app.fragment;

import java.util.List;

import org.apache.http.NameValuePair;

import android.extend.app.BaseFragment;
import android.extend.util.AndroidUtils;
import android.extend.util.ResourceUtil;
import android.extend.widget.ExtendWebView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class ExtendWebViewFragment extends BaseFragment
{
	protected String mFirstUrl;
	protected List<NameValuePair> mFirstRequestPairs;
	protected List<NameValuePair> mBaseRequestPairs;
	protected long mFirstDelay = 0L;
	protected ExtendWebView mWebView;

	public ExtendWebViewFragment(String firstUrl, List<NameValuePair> firstRequestPairs,
			List<NameValuePair> baseRequestPairs, long firstDelay)
	{
		mFirstUrl = firstUrl;
		mFirstRequestPairs = firstRequestPairs;
		mBaseRequestPairs = baseRequestPairs;
		mFirstDelay = firstDelay;
	}

	public ExtendWebViewFragment(String firstUrl, List<NameValuePair> firstRequestPairs,
			List<NameValuePair> baseRequestPairs)
	{
		this(firstUrl, firstRequestPairs, baseRequestPairs, 0L);
	}

	public ExtendWebViewFragment(String firstUrl)
	{
		this(firstUrl, null, null);
	}

	public ExtendWebView getWebView()
	{
		return mWebView;
	}

	public void refresh()
	{
		if (mWebView != null)
			mWebView.reload();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (mWebView != null)
		{
			mWebView.destroy();
		}
		if (view != null)
		{
			int id = ResourceUtil.getId(getContext(), "webview");
			mWebView = (ExtendWebView)view.findViewById(id);
		}
		if (mWebView == null)
		{
			mWebView = new ExtendWebView(getContext());
			LayoutParams params = mWebView.getLayoutParams();
			if (params == null)
			{
				params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			}
			else
			{
				params.width = LayoutParams.MATCH_PARENT;
				params.height = LayoutParams.MATCH_PARENT;
			}
			mWebView.setLayoutParams(params);
			view = mWebView;
		}
		if (mWebView != null)
			if (mBaseRequestPairs != null && !mBaseRequestPairs.isEmpty())
				mWebView.addBaseUrlRequestPairs(mBaseRequestPairs);
		return view;
	}

	@Override
	public void onFirstStart()
	{
		super.onFirstStart();
		if (mFirstDelay > 0)
		{
			AndroidUtils.MainHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					if (mFirstRequestPairs != null)
						mWebView.loadUrl(mFirstUrl, mFirstRequestPairs);
					else
						mWebView.loadUrl(mFirstUrl);
				}
			}, mFirstDelay);
		}
		else
		{
			if (mFirstRequestPairs != null)
				mWebView.loadUrl(mFirstUrl, mFirstRequestPairs);
			else
				mWebView.loadUrl(mFirstUrl);
		}
	}

	/**
	 * Called when the fragment is visible to the user and actively running. Resumes the WebView.
	 */
	@Override
	public void onPause()
	{
		super.onPause();
		mWebView.onPause();
	}

	/**
	 * Called when the fragment is no longer resumed. Pauses the WebView.
	 */
	@Override
	public void onResume()
	{
		mWebView.onResume();
		super.onResume();
	}

	/**
	 * Called when the WebView has been detached from the fragment.
	 * The WebView is no longer available after this time.
	 */
	// @Override
	// public void onDestroyView()
	// {
	// // mIsWebViewAvailable = false;
	// super.onDestroyView();
	// }

	/**
	 * Called when the fragment is no longer in use. Destroys the internal state of the WebView.
	 */
	// @Override
	// public void onDestroy()
	// {
	// super.onDestroy();
	// }

	// @Override
	// public void onDetach()
	// {
	// super.onDetach();
	// if (mWebView != null)
	// {
	// mWebView.destroy();
	// mWebView = null;
	// }
	// }

	@Override
	public boolean onBackPressed()
	{
		if (mWebView != null && mWebView.canGoBack())
		{
			mWebView.goBack();
			return true;
		}
		return super.onBackPressed();
	}
}
