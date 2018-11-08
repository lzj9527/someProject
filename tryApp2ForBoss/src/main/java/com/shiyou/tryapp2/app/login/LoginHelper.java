package com.shiyou.tryapp2.app.login;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.extend.ErrorInfo;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.util.AndroidUtils;
import android.extend.util.ResourceUtil;
import android.extend.widget.ExtendDialog;
import android.extend.widget.ProgressBar;
import android.extend.widget.ProgressBar.ChangeProgressMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.shiyou.tryapp2.FileDownloadHelper;
import com.shiyou.tryapp2.FileDownloadHelper.OnFileDownloadCallback;
import com.shiyou.tryapp2.RequestManager;
import com.shiyou.tryapp2.RequestManager.RequestCallback;
import com.shiyou.tryapp2.app.MainActivity;
import com.shiyou.tryapp2.boss.zsa.R;
import com.shiyou.tryapp2.data.FileInfo;
import com.shiyou.tryapp2.data.response.BaseResponse;
import com.shiyou.tryapp2.data.response.CheckVersionResponse;
import com.umeng.analytics.MobclickAgent;


//  登录用   这里可以吧userName和userkey存储
public class LoginHelper
{
	public static final String TAG = "LoginHelper";

	private static String mUserName;
	private static String mUserKey;

	public static String getUserName()
	{
		return mUserName;
	}

	public static String getUserKey()
	{
		return mUserKey;
	}

	
	//  登录完成
	public static void onLoginFinished(final Context context, final String userName, final String userKey)
	{
		mUserName = userName;
		mUserKey = userKey;
		Map<String, String> map = new HashMap<String, String>();
		map.put("userName", userName);
		map.put("userKey", userKey);
		map.put("deviceId", android.os.Build.SERIAL);
		map.put("deviceName", android.os.Build.MODEL);
		map.put("pushRegId", JPushInterface.getRegistrationID(context));
		MobclickAgent.onEvent(context, "login", map);
	}
	//  是否已经登陆过
	public static boolean isLogined()
	{
		if (TextUtils.isEmpty(mUserKey))
		{
			return false;
		}
		return true;
	}

	private static boolean mNewVersonDownloading = false;
	
	//  检测版本更新
	public static void checkVersion(final Activity activity, final boolean fromStart)
	{
		if (mNewVersonDownloading)
		{
			AndroidUtils.showToast(activity, "新版本正在下载中，请稍候.");
			return;
		}
		RequestManager.checkVersion(activity, new RequestCallback()
		{
			@Override
			public void onRequestResult(int requestCode, long taskId, BaseResponse response, DataFrom from)
			{
				if (response.resultCode == BaseResponse.RESULT_OK)
				{
					final CheckVersionResponse cvResponse = (CheckVersionResponse)response;
					if (cvResponse.datas != null)
					{
						switch (cvResponse.datas.isMust)
						{
							case 0:// 可选升级
								activity.runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										int idlayout = ResourceUtil.getLayoutId(activity, "version_update_dialog");
										View layout = View.inflate(activity, idlayout, null);
										int id = ResourceUtil.getId(activity, "immediately_update");
										TextView immediately_update = (TextView)layout.findViewById(id);
										id = ResourceUtil.getId(activity, "later_update");
										View later_update = layout.findViewById(id);

										final Dialog dialog = AndroidUtils.createDialog(activity, layout, true, true);

										immediately_update.setOnClickListener(new View.OnClickListener()
										{
											@Override
											public void onClick(View v)
											{
												// AndroidUtils.launchBrowser(activity, cvResponse.datas.Url);
												FileInfo fileInfo = new FileInfo();
												fileInfo.url = cvResponse.datas.Url;
												FileDownloadHelper.checkAndDownloadIfNeed(activity, null, fileInfo,
														null, new OnFileDownloadCallback()
														{
															@Override
															public void onDownloadStarted(Object tag,
																	FileInfo fileInfo, String localPath)
															{
																mNewVersonDownloading = true;
															}

															@Override
															public void onDownloadProgress(Object tag,
																	FileInfo fileInfo, String localPath, long count,
																	long length, float speed)
															{
																UpdateNotification(activity,
																		(int)(count * 100 / length), speed, null);
															}

															@Override
															public void onDownloadFinished(Object tag,
																	FileInfo fileInfo, String localPath)
															{
																mNewVersonDownloading = false;
																try
																{
																	CancelNotification(activity);
																	AndroidUtils.installPackage(activity, localPath);
																}
																catch (FileNotFoundException e)
																{
																	e.printStackTrace();
																}
																catch (Exception e)
																{
																	e.printStackTrace();
																}
															}

															@Override
															public void onDownloadFailed(Object tag,
																	final FileInfo fileInfo, ErrorInfo error)
															{
																mNewVersonDownloading = false;
																downloadFailedAndRetry(activity, fileInfo, this);
															}

															@Override
															public void onDownloadCanceled(Object tag, FileInfo fileInfo)
															{
																mNewVersonDownloading = false;
															}
														}, false, false);
												dialog.dismiss();
											}
										});

										later_update.setOnClickListener(new View.OnClickListener()
										{
											@Override
											public void onClick(View v)
											{
												dialog.dismiss();
											}
										});

										dialog.show();
									}
								});
								break;
							case 1:// 必须升级
								activity.runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										int idlayout = ResourceUtil.getLayoutId(activity, "version_update_dialog");
										View layout = View.inflate(activity, idlayout, null);
										int id = ResourceUtil.getId(activity, "immediately_update");
										TextView immediately_update = (TextView)layout.findViewById(id);
										id = ResourceUtil.getId(activity, "later_update");
										View later_update = layout.findViewById(id);
										later_update.setVisibility(View.GONE);

										final Dialog dialog = AndroidUtils.createDialog(activity, layout, false, false);

										immediately_update.setOnClickListener(new View.OnClickListener()
										{
											@Override
											public void onClick(View v)
											{
												// AndroidUtils.launchBrowser(activity, cvResponse.datas.Url);
												FileInfo fileInfo = new FileInfo();
												fileInfo.url = cvResponse.datas.Url;
												FileDownloadHelper.checkAndDownloadIfNeed(activity, null, fileInfo,
														null, new OnFileDownloadCallback()
														{
															@Override
															public void onDownloadStarted(Object tag,
																	FileInfo fileInfo, String localPath)
															{
																showDownloadDialog(activity);
															}

															@Override
															public void onDownloadProgress(Object tag,
																	FileInfo fileInfo, String localPath, long count,
																	long length, float speed)
															{
																updateDonwloadProgress(count, length, speed);
															}

															@Override
															public void onDownloadFinished(Object tag,
																	FileInfo fileInfo, String localPath)
															{
																// updateDonwloadProgress(100);
																try
																{
																	AndroidUtils.installPackage(activity, localPath);
																	dismissDownloadDialog();
																	MainActivity.quit();
																}
																catch (Exception e)
																{
																	e.printStackTrace();
																}
															}

															@Override
															public void onDownloadFailed(Object tag, FileInfo fileInfo,
																	ErrorInfo error)
															{
																mNewVersonDownloading = false;
																downloadFailedAndRetry(activity, fileInfo, this);
															}

															@Override
															public void onDownloadCanceled(Object tag, FileInfo fileInfo)
															{
																mNewVersonDownloading = false;
															}
														}, false, false);
												dialog.dismiss();
											}
										});
										dialog.show();
									}
								});
								break;
						}
					}
					else if (!fromStart)
					{
						AndroidUtils.showToast(activity, "当前已是最新版.");
					}
				}
				else
				{
					AndroidUtils.showToast(activity, response.error);
				}
			}

			@Override
			public void onRequestError(int requestCode, long taskId, ErrorInfo error)
			{
				AndroidUtils.showToast(activity, "网络错误:" + error.errorCode);
			}
		});
	}

	private static void downloadFailedAndRetry(final Activity activity, final FileInfo fileInfo,
			final OnFileDownloadCallback callback)
	{
		// 下载失败，延迟3秒重试
		AndroidUtils.MainHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				FileDownloadHelper.checkAndDownloadIfNeed(activity, null, fileInfo, null, callback, false, true);
			}
		}, 3000L);
	}

	// private int count=0;
	private static long mTime;
	private static Notification mNotification;

	public static void UpdateNotification(Context context, int progress, float speed, String url)
	{
		long time = System.currentTimeMillis();
		if (time - mTime < 1000L)
			return;
		mTime = time;

		// 从系统服务中获得通知管理器
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (mNotification == null)
		{
			// 定义notification
			Builder mBuilder = new Builder(context);
			mBuilder.setProgress(100, progress, false);
			// mBuilder.setLargeIcon(icon)
			mBuilder.setSmallIcon(R.drawable.ic_launcher);
			mBuilder.setTicker("新版本正在下载中...");
			mBuilder.setContentTitle("新版本下载");
			mBuilder.setContentText("新版本正在下载中...");
			mBuilder.setOngoing(false);
			mBuilder.setAutoCancel(false);
			mNotification = mBuilder.build();
		}
		else
		{
			mNotification.contentView.setProgressBar(android.R.id.progress, 100, progress, false);
			// Class<?> innerClass = ReflectHelper.getInnerClass("com.android.internal.R", "id");
			// int id = (Integer)ReflectHelper.getStaticFieldValue(innerClass, "text");
			// mNotification.contentView.setTextViewText(android.R.id.text2, (int)speed + "k/s");
		}
		// 执行通知
		nm.notify(1, mNotification);
	}

	public static void CancelNotification(Context context)
	{
		NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(1);
		mNotification = null;
	}

	private static ExtendDialog mDownloadDialog;

	private static void showDownloadDialog(final Activity activity)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (mDownloadDialog == null)
				{
					int layout = ResourceUtil.getLayoutId(activity, "download_progress_dialog");
					View view = View.inflate(activity, layout, null);
					mDownloadDialog = AndroidUtils.createDialog(activity, view, false, false);

					int id = ResourceUtil.getId(activity, "title");
					TextView title = (TextView)mDownloadDialog.findViewById(id);
					title.setText("新版本下载中，请稍候...");

					id = ResourceUtil.getId(activity, "progressBar");
					ProgressBar progressBar = (ProgressBar)mDownloadDialog.findViewById(id);
					progressBar.setChangeProgressMode(ChangeProgressMode.NONE);
					progressBar.setProgress(0);

					id = ResourceUtil.getId(activity, "percent");
					TextView percent = (TextView)mDownloadDialog.findViewById(id);
					percent.setText("0%");

					id = ResourceUtil.getId(activity, "cancel");
					View cancel = mDownloadDialog.findViewById(id);
					cancel.setVisibility(View.GONE);

					mDownloadDialog.show();
				}
			}
		});
	}

	private static void updateDonwloadProgress(final long count, final long length, final float speed)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (mDownloadDialog != null)
				{
					float percentF = (100 * count) / length;

					int id = ResourceUtil.getId(mDownloadDialog.getContext(), "progressBar");
					ProgressBar progressBar = (ProgressBar)mDownloadDialog.findViewById(id);
					progressBar.setChangeProgressMode(ChangeProgressMode.NONE);
					progressBar.setProgress(percentF);

					id = ResourceUtil.getId(mDownloadDialog.getContext(), "percent");
					TextView percent = (TextView)mDownloadDialog.findViewById(id);
					percent.setText((int)percentF + "%");

					id = ResourceUtil.getId(mDownloadDialog.getContext(), "speed");
					TextView speedText = (TextView)mDownloadDialog.findViewById(id);
					speedText.setText((int)speed + "k/s");
				}
			}
		});
	}

	private static void dismissDownloadDialog()
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (mDownloadDialog != null)
				{
					mDownloadDialog.dismiss();
					mDownloadDialog = null;
				}
			}
		});
	}
}
