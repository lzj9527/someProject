package com.shiyou.tryapp2.app;

import android.content.Intent;
import android.extend.util.LogUtil;
import android.net.Uri;
import android.os.Bundle;

public class ReceiveEventActivity extends BaseAppActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		receiveEvent();
	}

	private void receiveEvent()
	{
		Intent intent = getIntent();
		if (Intent.ACTION_VIEW.equals(intent.getAction()))
		{
			Uri uri = intent.getData();
			LogUtil.d(TAG, "receiveEvent: " + uri);
			if (uri != null)
			{
				String event = uri.getQueryParameter("event");
				LogUtil.i(TAG, "receiveEvent: event=" + event);
				if (event != null)
				{
					if (event.equals("return_wap"))
					{
						// if (WebViewActivity.instance != null)
						// WebViewActivity.instance.finish();
						// else
						if (MainActivity.instance != null)
							MainActivity.instance.onBackPressed();
					}
					else if (event.equals("start"))
					{
						if (MainActivity.instance == null)
							startActivity(new Intent(this, MainActivity.class));
					}
					// else if (event.equals("payment_finished"))
					// {
					// if (PaymentActivity.instance != null)
					// PaymentActivity.instance.finish(RESULT_OK, true);
					// }
					// else if (event.equals("payment_failed"))
					// {
					// if (PaymentActivity.instance != null)
					// PaymentActivity.instance.finish(RESULT_OK, false);
					// }
				}
				String combine_id = uri.getQueryParameter("combine_id");
				if (combine_id != null)
				{
					showToast("接受到wap信息，combine_id=" + combine_id);
				}
				finish();
			}
		}
	}
}
