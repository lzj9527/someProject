package android.extend.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class NetworkManager
{
	public static final String TAG = "NetworkManager";

	public static final String APN_NAME_CMMM = "cmmm";
	public static final String APN_NAME_CMWAP = "cmwap";
	public static final String APN_NAME_CMNET = "cmnet";

	public static String getIMEI(Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		LogUtil.v(TAG, "getIMEI: " + imei);
		return imei;
	}

	public static String getIMSI(Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		LogUtil.v(TAG, "getIMSI: " + imsi);
		return imsi;
	}

	public static String getSimOperator(Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = tm.getSimOperator();
		LogUtil.v(TAG, "getSimOperator: " + operator);
		return operator;
	}

	public static String getNetworkOperator(Context context)
	{
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String operator = tm.getNetworkOperator();
		LogUtil.v(TAG, "getNetworkOperator: " + operator);
		return operator;
	}

	public static String getWifiSsid(Context context)
	{
		WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wm.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		LogUtil.v(TAG, "getWifiSsid: " + ssid);
		return ssid;
	}

	public static boolean isChinaNetwork(Context context)
	{
		return isChinaNetwork(getNetworkOperator(context));
	}

	public static boolean isChinaNetwork(String operator)
	{
		String mcc = "";
		// String mnc = "";
		if (operator != null && operator.length() > 4)
		{
			mcc = operator.substring(0, 3);
			// mnc = operator.substring(3, 5);
		}
		if (mcc.equals("460"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static boolean isChinaMobileNet(Context context)
	{
		return isChinaMobileNet(getSimOperator(context));
	}

	public static boolean isChinaMobileNet(String operator)
	{
		String mcc = "";
		String mnc = "";
		if (operator != null && operator.length() > 4)
		{
			mcc = operator.substring(0, 3);
			mnc = operator.substring(3, 5);
		}
		if (mcc.equals("460") && (mnc.equals("00") || mnc.equals("02") || mnc.equals("07")))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static NetworkInfo getActiveNetworkInfo(Context context)
	{
		final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		LogUtil.d(TAG, "getActiveNetworkInfo: " + info);
		return info;
	}

	public static NetworkInfo[] getAllActiveNetworkInfo(Context context)
	{
		final ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		ArrayList<NetworkInfo> infoList = new ArrayList<NetworkInfo>();
		for (NetworkInfo info : cm.getAllNetworkInfo())
		{
			if (info != null && info.isAvailable() && info.isConnected())
			{
				LogUtil.d(TAG, "getAllActiveNetworkInfo add: " + info);
				infoList.add(info);
			}
		}
		return (NetworkInfo[])infoList.toArray();
	}

	public static boolean isWLANNetwork(Context context)
	{
		NetworkInfo ni = getActiveNetworkInfo(context);
		return isWLANNetwork(ni);
	}

	public static boolean isWLANNetwork(NetworkInfo ni)
	{
		if (ni == null || ni.getType() != ConnectivityManager.TYPE_WIFI)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public static boolean isCMMMNetwork(Context context)
	{
		NetworkInfo ni = getActiveNetworkInfo(context);
		return isCMMMNetwork(ni);
	}

	public static boolean isCMMMNetwork(NetworkInfo ni)
	{
		if (ni == null)
			return false;
		String typename = ni.getTypeName();
		String extraInfo = ni.getExtraInfo();
		if (typename == null)
			typename = "";
		if (extraInfo == null)
			extraInfo = "";
		if (typename.toLowerCase().contains(APN_NAME_CMMM))
			return true;
		if (extraInfo.toLowerCase().contains(APN_NAME_CMMM))
			return true;
		else
			return false;
	}

	public static boolean isCMWAPNetwork(Context context)
	{
		NetworkInfo ni = getActiveNetworkInfo(context);
		return isCMWAPNetwork(ni);
	}

	public static boolean isCMWAPNetwork(NetworkInfo ni)
	{
		if (ni == null)
			return false;
		String typename = ni.getTypeName();
		String extraInfo = ni.getExtraInfo();
		if (typename == null)
			typename = "";
		if (extraInfo == null)
			extraInfo = "";
		if (typename.toLowerCase().contains(APN_NAME_CMWAP))
			return true;
		if (extraInfo.toLowerCase().contains(APN_NAME_CMWAP))
			return true;
		else
			return false;
	}

	public static boolean isCMNETNetwork(Context context)
	{
		NetworkInfo ni = getActiveNetworkInfo(context);
		return isCMNETNetwork(ni);
	}

	public static boolean isCMNETNetwork(NetworkInfo ni)
	{
		if (ni == null)
			return false;
		String typename = ni.getTypeName();
		String extraInfo = ni.getExtraInfo();
		if (typename == null)
			typename = "";
		if (extraInfo == null)
			extraInfo = "";
		if (typename.toLowerCase().contains(APN_NAME_CMNET))
			return true;
		if (extraInfo.toLowerCase().contains(APN_NAME_CMNET))
			return true;
		else
			return false;
	}

	public static HttpHost getDefaultProxy(Context context)
	{
		return getDefaultProxy(context, getActiveNetworkInfo(context));
	}

	public static HttpHost getDefaultProxy(Context context, NetworkInfo ni)
	{
		String host = null;
		int port = 0;
		if (isCMMMNetwork(ni))
		{
			host = "192.168.11.5";
			port = 80;
		}
		else if (isCMWAPNetwork(ni))
		{
			host = "10.0.0.172";
			port = 80;
		}
		// if (TextUtils.isEmpty(host))
		// {
		// host = Proxy.getHost(context);
		// port = Proxy.getPort(context);
		// }
		// if (TextUtils.isEmpty(host))
		// {
		// host = Proxy.getDefaultHost();
		// port = Proxy.getDefaultPort();
		// }
		if (!TextUtils.isEmpty(host))
		{
			return new HttpHost(host, port);
		}
		return null;
	}

	public static class NetworkConnectivityListener
	{
		public static final String ACTION_ANY_DATA_CONNECTION_STATE_CHANGED = "android.intent.action.ANY_DATA_STATE";

		private static Context mContext = null;
		private static boolean mListening = false;
		private static ConnectivityBroadcastReceiver mReceiver = new ConnectivityBroadcastReceiver();
		private static List<OnNetworkHandler> mHandlers = Collections
				.synchronizedList(new ArrayList<OnNetworkHandler>());

		// public NetworkConnectivityListener(Context context){}

		public static synchronized boolean isListening()
		{
			return mListening;
		}

		public static synchronized void startListening(Context context)
		{
			if (!mListening)
			{
				IntentFilter filter = new IntentFilter();
				filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
				filter.addAction(ACTION_ANY_DATA_CONNECTION_STATE_CHANGED);
				mContext = context;
				mContext.registerReceiver(mReceiver, filter);
				mListening = true;
			}
		}

		public static synchronized void stopListening()
		{
			if (mListening)
			{
				mContext.unregisterReceiver(mReceiver);
				mListening = false;
			}
		}

		public static void registerHandler(OnNetworkHandler handler)
		{
			mHandlers.add(handler);
		}

		public static void unregisterHandler(OnNetworkHandler handler)
		{
			mHandlers.remove(handler);
		}

		public static void clear()
		{
			mHandlers.clear();
		}

		private static void notifyNetworkChanged(final NetworkInfo ni, final NetworkInfo other)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					synchronized (mHandlers)
					{
						for (OnNetworkHandler handler : mHandlers)
						{
							handler.onNetworkChanged(ni, other);
						}
					}
				}
			}).start();
		}

		private static void notifyAnyDataConnectionChanged(final String apn, final String iface)
		{
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					synchronized (mHandlers)
					{
						for (OnNetworkHandler handler : mHandlers)
						{
							handler.onAnyDataConnectionChanged(apn, iface);
						}
					}
				}
			}).start();
		}

		private static class ConnectivityBroadcastReceiver extends BroadcastReceiver
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				LogUtil.d(TAG, "ConnectivityBroadcastReceive: " + intent);
				String action = intent.getAction();
				if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action))
				{
					NetworkInfo networkInfo = (NetworkInfo)intent
							.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
					NetworkInfo otherNetworkInfo = (NetworkInfo)intent
							.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);
					LogUtil.v(TAG, "networkInfo = " + networkInfo);
					LogUtil.v(TAG, "otherNetworkInfo = " + otherNetworkInfo);
					notifyNetworkChanged(networkInfo, otherNetworkInfo);
				}
				else if (ACTION_ANY_DATA_CONNECTION_STATE_CHANGED.equals(action))
				{
					String apn = intent.getStringExtra("apn");
					String iface = intent.getStringExtra("iface");
					LogUtil.v(TAG, "apn: " + apn + " iface: " + iface);
					notifyAnyDataConnectionChanged(apn, iface);
				}
			}
		}
	}

	public interface OnNetworkHandler
	{
		public void onNetworkChanged(NetworkInfo ni, NetworkInfo other);

		public void onAnyDataConnectionChanged(String apn, String iface);
	}
}
