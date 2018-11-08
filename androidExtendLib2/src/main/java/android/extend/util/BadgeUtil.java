package android.extend.util;

import java.lang.reflect.Field;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class BadgeUtil
{
	public static final String TAG = BadgeUtil.class.getSimpleName();

	public static void sendBadgeNumber(Context context, int number, String lancherActivityClassName)
	{
		LogUtil.d(TAG, "Build.MANUFACTURER: " + Build.MANUFACTURER);
		if (Build.MANUFACTURER.toLowerCase().contains("xiaomi"))
		{
			sendToXiaoMi(context, number, lancherActivityClassName);
		}
		else if (Build.MANUFACTURER.toLowerCase().contains("samsung"))
		{
			sendToSamsumg(context, number, lancherActivityClassName);
		}
		else if (Build.MANUFACTURER.toLowerCase().contains("sony"))
		{
			sendToSony(context, number, lancherActivityClassName);
		}
		else
		{
			LogUtil.w(TAG, "unsupported sendBadgeNumber.");
		}
	}

	private static void sendToXiaoMi(Context context, int number, String lancherActivityClassName)
	{
		String numStr = String.valueOf(number);
		if (number < 1)
		{
			numStr = "";
		}
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = null;
		boolean isMiUIV6 = true;
		try
		{
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			// builder.setContentTitle("您有" + number + "未读消息");
			// builder.setTicker("您有" + number + "未读消息");
			builder.setAutoCancel(true);
			// builder.setSmallIcon(R.drawable.);
			builder.setDefaults(Notification.DEFAULT_LIGHTS);
			notification = builder.build();
			Class<?> miuiNotificationClass = Class.forName("android.app.MiuiNotification");
			Object miuiNotification = miuiNotificationClass.newInstance();
			Field field = miuiNotification.getClass().getDeclaredField("messageCount");
			field.setAccessible(true);
			field.set(miuiNotification, numStr);// 设置信息数
			field = notification.getClass().getField("extraNotification");
			field.setAccessible(true);
			field.set(notification, miuiNotification);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// miui 6之前的版本
			isMiUIV6 = false;
			Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
			localIntent.putExtra("android.intent.extra.update_application_component_name", context.getPackageName()
					+ "/" + lancherActivityClassName);
			localIntent.putExtra("android.intent.extra.update_application_message_text", numStr);
			context.sendBroadcast(localIntent);
		}
		finally
		{
			if (notification != null && isMiUIV6)
			{
				// miui6以上版本需要使用通知发送
				nm.notify(101010, notification);
			}
		}

	}

	private static void sendToSony(Context context, int number, String lancherActivityClassName)
	{
		boolean isShow = true;
		String numStr = String.valueOf(number);
		if (number < 1)
		{
			isShow = false;
			numStr = "";
		}
		Intent localIntent = new Intent();
		localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);// 是否显示
		localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
		localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", lancherActivityClassName);// 启动页
		localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", numStr);// 数字
		localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.getPackageName());// 包名
		context.sendBroadcast(localIntent);
	}

	private static void sendToSamsumg(Context context, int number, String lancherActivityClassName)
	{
		String numStr = String.valueOf(number);
		if (number < 1)
		{
			numStr = "";
		}
		Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
		localIntent.putExtra("badge_count", numStr);// 数字
		localIntent.putExtra("badge_count_package_name", context.getPackageName());// 包名
		localIntent.putExtra("badge_count_class_name", lancherActivityClassName); // 启动页
		context.sendBroadcast(localIntent);
	}
}
