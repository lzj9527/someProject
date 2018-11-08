package android.extend;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.extend.util.LogUtil;
import android.text.TextUtils;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

public class BasicConfig
{
	public static Context ApplicationContext;
	public static boolean DebugMode = true;
	public static boolean LogToFile = false;
	public static int HttpMaxTotalConnections = 50;
	public static int HttpMaxConnectionsPerRoute = 50;
	public static int HttpTimeout = 10 * 1000;
	public static int HttpConnectionTimeout = 10 * 1000;
	public static int HttpSoTimeout = 10 * 1000;
	public static int HttpRequestRetryCount = 2;
	public static int LoaderMaxTaskCount = 5;
	public static int BitmapLoaderMaxTaskCount = 3;
	public static boolean UseFileCache = true;
	public static long DefaultFileCacheTime = 60 * 60 * 24 * 3;
	public static int CacheDBVersion = 4;
	public static boolean UseBitmapMemoryCache = true;
	public static String DefaultEncoding = "UTF-8";
	public static boolean HideExtName = true;
	public static long FastClickTime = 1000L;

	public static boolean UseUMengAnalytics = false;
	public static boolean UMengDebugMode = true;
	public static String UMengAppKey = "";
	public static String UMengChannel = "";

	public static String ActivityStartEnterAnim = "slide_in_right";
	public static String ActivityStartExitAnim = "";// "slide_out_left";
	public static String ActivityFinishEnterAnim = "";// "slide_in_left";
	public static String ActivityFinishExitAnim = "slide_out_right";

	public static String FragmentOpenEnterAnim = "slide_in_right";
	public static String FragmentOpenExitAnim = "slide_out_left";
	public static String FragmentCloseEnterAnim = "slide_in_left";
	public static String FragmentCloseExitAnim = "slide_out_right";

	public static int CameraMinPreviewWidth = 1280;
	public static int CameraMinPreviewHeight = 720;
	public static int CameraMinPictureWidth = 1280;
	public static int CameraMinPictureHeight = 720;

	// public static final int CAMERA_AUTOFOCUS_RETRYCOUNT = 10;

	public static void init(Context context)
	{
		ApplicationContext = context.getApplicationContext();
		InputStream is = null;
		try
		{
			is = context.getAssets().open("Config.xml");
			// String value = DataUtils.readString(is);
			// LogUtil.d("Config", "read config value: ");
			// LogUtil.v("Config", value);
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(is, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT)
			{
				switch (eventType)
				{
					case XmlPullParser.START_TAG:
						if (parser.getName().equals("Config"))
						{
							int count = parser.getAttributeCount();
							for (int i = 0; i < count; i++)
							{
								String name = parser.getAttributeName(i);
								String value = parser.getAttributeValue(i);
								LogUtil.d("Config", "read attribute: " + name + " = " + value);
								if (name.equals("DebugMode"))
								{
									DebugMode = Boolean.parseBoolean(value);
								}
								else if (name.equals("LogToFile"))
								{
									LogToFile = Boolean.parseBoolean(value);
								}
								else if (name.equals("HttpMaxTotalConnections"))
								{
									HttpMaxTotalConnections = Integer.parseInt(value);
								}
								else if (name.equals("HttpMaxConnectionsPerRoute"))
								{
									HttpMaxConnectionsPerRoute = Integer.parseInt(value);
								}
								else if (name.equals("HttpTimeout"))
								{
									HttpTimeout = Integer.parseInt(value);
								}
								else if (name.equals("HttpConnectionTimeout"))
								{
									HttpConnectionTimeout = Integer.parseInt(value);
								}
								else if (name.equals("HttpSoTimeout"))
								{
									HttpSoTimeout = Integer.parseInt(value);
								}
								else if (name.equals("HttpRequestRetryCount"))
								{
									HttpRequestRetryCount = Integer.parseInt(value);
								}
								else if (name.equals("LoaderMaxTaskCount"))
								{
									LoaderMaxTaskCount = Integer.parseInt(value);
								}
								else if (name.equals("BitmapLoaderMaxTaskCount"))
								{
									BitmapLoaderMaxTaskCount = Integer.parseInt(value);
								}
								else if (name.equals("UseFileCache"))
								{
									UseFileCache = Boolean.parseBoolean(value);
								}
								else if (name.equals("DefaultFileCacheTime"))
								{
									DefaultFileCacheTime = Long.parseLong(value);
								}
								else if (name.equals("CacheDBVersion"))
								{
									CacheDBVersion = Integer.parseInt(value);
								}
								else if (name.equals("UseBitmapMemoryCache"))
								{
									UseBitmapMemoryCache = Boolean.parseBoolean(value);
								}
								else if (name.equals("DefaultEncoding"))
								{
									DefaultEncoding = value;
								}
								else if (name.equals("HideExtName"))
								{
									HideExtName = Boolean.parseBoolean(value);
								}
								else if (name.equals("FastClickTime"))
								{
									FastClickTime = Long.parseLong(value);
								}
								else if (name.equals("UMengDebugMode"))
								{
									UMengDebugMode = Boolean.parseBoolean(value);
								}
								else if (name.equals("UseUMengAnalytics"))
								{
									UseUMengAnalytics = Boolean.parseBoolean(value);
								}
								else if (name.equals("UMengAppKey"))
								{
									UMengAppKey = value;
								}
								else if (name.equals("UMengChannel"))
								{
									UMengChannel = value;
								}
								else if (name.equals("ActivityStartEnterAnim"))
								{
									ActivityStartEnterAnim = value;
								}
								else if (name.equals("ActivityStartExitAnim"))
								{
									ActivityStartExitAnim = value;
								}
								else if (name.equals("ActivityFinishEnterAnim"))
								{
									ActivityFinishEnterAnim = value;
								}
								else if (name.equals("ActivityFinishExitAnim"))
								{
									ActivityFinishExitAnim = value;
								}
								else if (name.equals("FragmentOpenEnterAnim"))
								{
									FragmentOpenEnterAnim = value;
								}
								else if (name.equals("FragmentOpenExitAnim"))
								{
									FragmentOpenExitAnim = value;
								}
								else if (name.equals("FragmentCloseEnterAnim"))
								{
									FragmentCloseEnterAnim = value;
								}
								else if (name.equals("FragmentCloseExitAnim"))
								{
									FragmentCloseExitAnim = value;
								}
								else if (name.equals("CameraMinPreviewWidth"))
								{
									CameraMinPreviewWidth = Integer.parseInt(value);
								}
								else if (name.equals("CameraMinPreviewHeight"))
								{
									CameraMinPreviewHeight = Integer.parseInt(value);
								}
								else if (name.equals("CameraMinPictureWidth"))
								{
									CameraMinPictureWidth = Integer.parseInt(value);
								}
								else if (name.equals("CameraMinPictureHeight"))
								{
									CameraMinPictureHeight = Integer.parseInt(value);
								}
							}
						}
						break;
				}
				eventType = parser.next();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (UseUMengAnalytics)
			{
				if (!TextUtils.isEmpty(UMengAppKey))
				{
					// AnalyticsConfig.setAppkey(UMengAppKey);
					MobclickAgent.startWithConfigure(new UMAnalyticsConfig(context, UMengAppKey, UMengChannel,
							EScenarioType.E_UM_NORMAL, true));
				}
				// if (!TextUtils.isEmpty(UMengChannel))
				// {
				// AnalyticsConfig.setChannel(UMengChannel);
				// }
				MobclickAgent.setDebugMode(UMengDebugMode);
				MobclickAgent.setCatchUncaughtExceptions(true);
				MobclickAgent.openActivityDurationTrack(true);
				// MobclickAgent.updateOnlineConfig(context);
			}
		}
	}
}
