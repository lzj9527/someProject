package com.shiyou.tryapp2;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.extend.ErrorInfo;
import android.extend.cache.FileCacheManager;
import android.extend.util.AndroidUtils;
import android.extend.util.FileUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.util.SimpleDownloader;
import android.extend.widget.ProgressBar;
import android.extend.widget.ProgressBar.ChangeProgressMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.shiyou.tryapp2.data.FileInfo;
import com.shiyou.tryapp2.data.db.FileDownloadDBHelper;

public class FileDownloadHelper
{
	public interface OnFileDownloadCallback
	{
		public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath);

		public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath, long count, long length,
				float speed);

		public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath);

		public void onDownloadCanceled(Object tag, FileInfo fileInfo);

		public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error);
	}

	public interface OnMultiFileDownloadCallback
	{
		public void onMultiDownloadStarted(Object tag, FileInfo fileInfo, String localPath, int downloadIndex);

		public void onMultiDownloadProgress(Object tag, FileInfo fileInfo, String localPath, long count, long length,
				float speed, int downloadIndex);

		public void onMultiDownloadFinished(Object tag, FileInfo fileInfo, String localPath, int downloadIndex);

		public void onMultiDownloadCanceled(Object tag, FileInfo fileInfo, int downloadIndex);

		public void onMultiDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error, int downloadIndex);

		public void onMultiAllDownloadFinished(Object tag, FileInfo[] fileInfos, DownloadStatus[] status);
	}

	public enum DownloadStatus
	{
		UNDOWNLOAD, DOWNLOADING, FAILED, CANCELED, FINISHED,
	}

	public static final String TAG = FileDownloadHelper.class.getSimpleName();

	private static View mDownloadProgressView;
	private static Dialog mDownloadProgressDialog;

	public static void cancelAllDownload(Context context)
	{
		SimpleDownloader.cancelAllDownload();
	}

	public static void clearAllDownloadedFile(Context context)
	{
		List<FileInfo> fileList = FileDownloadDBHelper.getInstance().queryAll(context);
		for (FileInfo info : fileList)
		{
			File file = new File(info.path);
			if (file.exists())
				file.delete();
		}
		FileDownloadDBHelper.getInstance().deleteAll(context);
		FileUtils.deleteDirectory(SimpleDownloader.getDownloadDirectory(context));
	}

	/** 检查文件是否需要下载 */
	public static boolean checkFileNeedDownload(Context context, FileInfo fileInfo)
	{
		LogUtil.d(TAG, "checkFileNeedDownload: url=" + fileInfo.url + "; filemtime=" + fileInfo.filemtime);
		FileInfo savedInfo = FileDownloadDBHelper.getInstance().query(context, fileInfo.url);
		if (savedInfo == null)
			return true;
		LogUtil.v(TAG, "query savedInfo: path=" + savedInfo.path + "; filemtime=" + savedInfo.filemtime);
		if (TextUtils.isEmpty(savedInfo.path))
			return true;
		File file = new File(savedInfo.path);
		LogUtil.v(TAG, "check local file exists: " + file.exists());
		if (!file.exists())
			return true;
		if (fileInfo.filemtime != savedInfo.filemtime)
		{
			file.delete();
			return true;
		}
		fileInfo.path = savedInfo.path;
		return false;
	}

	/** 开始下载 */
	public static void startDownload(Context context, Object tag, FileInfo fileInfo, OnFileDownloadCallback callback,
			boolean showProgressDialog)
	{
		startDownload(context, tag, fileInfo, null, callback, showProgressDialog);
	}

	/** 开始下载 */
	public static void startDownload(final Context context, final Object tag, final FileInfo fileInfo,
			final String localPath, final OnFileDownloadCallback callback, final boolean showProgressDialog)
	{
		startDownload(context, tag, fileInfo, localPath, callback, showProgressDialog, false);
	}

	/** 开始下载 */
	public static void startDownload(final Context context, final Object tag, final FileInfo fileInfo,
			final String localPath, final OnFileDownloadCallback callback, final boolean showProgressDialog,
			final boolean continued)
	{
		String url = fileInfo.url;
		LogUtil.d(TAG, "start download: url=" + url + "; localPath=" + localPath);
		SimpleDownloader.startDownload(context, tag, url, localPath, null, new SimpleDownloader.OnDownloadListener()
		{
			@Override
			public void onDownloadStarted(Object tag, String url, String localPath)
			{
				LogUtil.v(TAG, "onDownloadStarted: " + tag + "; " + url + "; " + localPath);
				if (showProgressDialog && context instanceof Activity)
					showDownloadProgressDialog((Activity)context, url);
				if (callback != null)
					callback.onDownloadStarted(tag, fileInfo, localPath);
			}

			@Override
			public void onDownloadProgress(Object tag, String url, String localPath, long count, long length,
					float speed)
			{
				// Log.v(tag, "onDownloadProgress: " + tag + "; " + url + "; " + localPath + "; " + count + "; "
				// +
				// length + "; " + speed);
				if (showProgressDialog)
					updateProgressBar(count, length, speed);
				if (callback != null)
					callback.onDownloadProgress(tag, fileInfo, localPath, count, length, speed);
			}

			@Override
			public void onDownloadFinished(Object tag, String url, String localPath, long totalTime)
			{
				LogUtil.v(TAG, "onDownloadFinished: " + tag + "; " + url + "; " + localPath + "; " + totalTime);
				fileInfo.path = localPath;
				FileDownloadDBHelper.getInstance().put(context, fileInfo);
				FileCacheManager.updateCacheRecord(context, url, localPath, -1, fileInfo.filemtime);
				if (showProgressDialog)
					hideProgressBar();
				if (callback != null)
					callback.onDownloadFinished(tag, fileInfo, localPath);
			}

			@Override
			public void onDownloadCanceled(Object tag, String url, String localPath)
			{
				LogUtil.v(TAG, "onDownloadCanceled: " + tag + "; " + url + "; " + localPath);
				hideProgressBar();
				// AndroidUtils.showToast(activity, "下载已取消");
				if (callback != null)
					callback.onDownloadCanceled(tag, fileInfo);
			}

			@Override
			public void onDownloadError(Object tag, String url, String localPath, ErrorInfo error)
			{
				LogUtil.w(TAG, "onDownloadError: " + tag + "; " + url + "; " + localPath, error.throwable);
				hideProgressBar();
				// AndroidUtils.showToast(activity, "下载失败，请重试");
				if (callback != null)
					callback.onDownloadFailed(tag, fileInfo, error);
			}
		}, continued);
	}

	/** 开始下载 */
	public static void startDownload(final Context context, final Object tag, final FileInfo fileInfo,
			final String directoryPath, final String fileName, final OnFileDownloadCallback callback,
			final boolean showProgressDialog)
	{
		startDownload(context, tag, fileInfo, directoryPath, fileName, callback, showProgressDialog, false);
	}

	/** 开始下载 */
	public static void startDownload(final Context context, final Object tag, final FileInfo fileInfo,
			final String directoryPath, final String fileName, final OnFileDownloadCallback callback,
			final boolean showProgressDialog, final boolean continued)
	{
		// String tag = activity.getClass().getSimpleName();
		String url = fileInfo.url;
		// String fileName = FileUtils.getFileName(url);
		// try
		// {
		// fileName = SecurityUtils.toMD5(fileName) + ".cache";
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// String path = SimpleDownloader.getDownloadDirectory(activity).getAbsolutePath() + File.separatorChar +
		// fileName;
		LogUtil.d(TAG, "start download: url=" + url + "; directoryPath=" + directoryPath + "; fileName=" + fileName);
		SimpleDownloader.startDownload(context, tag, url, directoryPath, fileName, null,
				new SimpleDownloader.OnDownloadListener()
				{
					@Override
					public void onDownloadStarted(Object tag, String url, String localPath)
					{
						LogUtil.v(TAG, "onDownloadStarted: " + tag + "; " + url + "; " + localPath);
						if (showProgressDialog && context instanceof Activity)
							showDownloadProgressDialog((Activity)context, url);
						if (callback != null)
							callback.onDownloadStarted(tag, fileInfo, localPath);
					}

					@Override
					public void onDownloadProgress(Object tag, String url, String localPath, long count, long length,
							float speed)
					{
						// Log.v(tag, "onDownloadProgress: " + tag + "; " + url + "; " + localPath + "; " + count + "; "
						// +
						// length + "; " + speed);
						if (showProgressDialog)
							updateProgressBar(count, length, speed);
						if (callback != null)
							callback.onDownloadProgress(tag, fileInfo, localPath, count, length, speed);
					}

					@Override
					public void onDownloadFinished(Object tag, String url, String localPath, long totalTime)
					{
						LogUtil.v(TAG, "onDownloadFinished: " + tag + "; " + url + "; " + localPath + "; " + totalTime);
						fileInfo.path = localPath;
						FileDownloadDBHelper.getInstance().put(context, fileInfo);
						FileCacheManager.updateCacheRecord(context, url, localPath, -1, fileInfo.filemtime);
						if (showProgressDialog)
							hideProgressBar();
						if (callback != null)
							callback.onDownloadFinished(tag, fileInfo, localPath);
					}

					@Override
					public void onDownloadCanceled(Object tag, String url, String localPath)
					{
						LogUtil.v(TAG, "onDownloadCanceled: " + tag + "; " + url + "; " + localPath);
						hideProgressBar();
						// AndroidUtils.showToast(activity, "下载已取消");
						if (callback != null)
							callback.onDownloadCanceled(tag, fileInfo);
					}

					@Override
					public void onDownloadError(Object tag, String url, String localPath, ErrorInfo error)
					{
						LogUtil.w(TAG, "onDownloadError: " + tag + "; " + url + "; " + localPath, error.throwable);
						hideProgressBar();
						// AndroidUtils.showToast(activity, "下载失败，请重试");
						if (callback != null)
							callback.onDownloadFailed(tag, fileInfo, error);
					}
				}, continued);
	}

	/** 取消下载 */
	public static void cancelDownload(String url)
	{
		LogUtil.d(TAG, "cancel download " + url);
		SimpleDownloader.cancelDownload(url);
	}

	/** 检查并且下载文件 */
	public static void checkAndDownloadIfNeed(Context context, Object tag, FileInfo fileInfo,
			OnFileDownloadCallback callback, boolean showProgressDialog)
	{
		checkAndDownloadIfNeed(context, tag, fileInfo, null, callback, showProgressDialog);
	}

	/** 检查并且下载文件 */
	public static void checkAndDownloadIfNeed(Context context, Object tag, FileInfo fileInfo, String directoryPath,
			OnFileDownloadCallback callback, boolean showProgressDialog)
	{
		checkAndDownloadIfNeed(context, tag, fileInfo, directoryPath, callback, showProgressDialog, false);
	}

	/** 检查并且下载文件 */
	public static void checkAndDownloadIfNeed(Context context, Object tag, FileInfo fileInfo, String directoryPath,
			OnFileDownloadCallback callback, boolean showProgressDialog, boolean continued)
	{
		// if (!TextUtils.isEmpty(fileInfo.path))
		// {
		// File file = new File(fileInfo.path);
		// if (file.exists())
		// {
		// if (callback != null)
		// callback.onDownloadFinished(tag, fileInfo, fileInfo.path);
		// return;
		// }
		// }
		if (checkFileNeedDownload(context, fileInfo))
		{
			if (TextUtils.isEmpty(fileInfo.path))
				startDownload(context, tag, fileInfo, directoryPath, null, callback, showProgressDialog, continued);
			else
				startDownload(context, tag, fileInfo, fileInfo.path, callback, showProgressDialog, continued);
		}
		else if (callback != null)
		{
			String localPath = FileDownloadDBHelper.getInstance().query(context, fileInfo.url).path;
			callback.onDownloadFinished(tag, fileInfo, localPath);
		}
	}

	/** 删除下载文件 */
	public static void deleteDownloadFile(Context context, String url)
	{
		FileInfo savedInfo = FileDownloadDBHelper.getInstance().query(context, url);
		if (savedInfo == null)
			return;
		File file = new File(savedInfo.path);
		if (file.exists())
			file.delete();
		FileDownloadDBHelper.getInstance().delete(context, url);
	}

	public static void startMultiDownload(Object tag, Context context, FileInfo[] fileInfos,
			OnMultiFileDownloadCallback callback, boolean stopDownloadWithFail, boolean showProgressDialog)
	{
		startMultiDownload(tag, context, fileInfos, null, callback, stopDownloadWithFail, showProgressDialog);
	}

	public static void startMultiDownload(Object tag, Context context, FileInfo[] fileInfos, String directoryPath,
			OnMultiFileDownloadCallback callback, boolean stopDownloadWithFail, boolean showProgressDialog)
	{
		startMultiDownload(tag, context, fileInfos, directoryPath, callback, stopDownloadWithFail, showProgressDialog,
				false);
	}

	public static void startMultiDownload(Object tag, Context context, FileInfo[] fileInfos, String directoryPath,
			OnMultiFileDownloadCallback callback, boolean stopDownloadWithFail, boolean showProgressDialog,
			boolean continued)
	{
		new MultiDownloadTask(tag, context, fileInfos, directoryPath, callback, stopDownloadWithFail,
				showProgressDialog, continued).start();
	}

	private static class MultiDownloadTask
	{
		private Object mTag;
		private Context mContext;
		private FileInfo[] mFileInfos;
		private DownloadStatus[] mStatus;
		private String mDirectoryPath;
		private OnMultiFileDownloadCallback mFileDownloadCallback;
		private boolean mStopDownloadWithFail;
		private boolean mShowProgressDialog;
		private boolean mContinued;
		private int mDownloadIndex;

		public MultiDownloadTask(Object tag, Context context, FileInfo[] fileInfos, String directoryPath,
				OnMultiFileDownloadCallback callback, boolean stopDownloadWithFail, boolean showProgressDialog,
				boolean continued)
		{
			mTag = tag;
			mContext = context;
			mFileInfos = fileInfos;
			initDownloadStatus();
			mDirectoryPath = directoryPath;
			mFileDownloadCallback = callback;
			mStopDownloadWithFail = stopDownloadWithFail;
			mShowProgressDialog = showProgressDialog;
			mContinued = continued;
			mDownloadIndex = 0;
		}

		private void initDownloadStatus()
		{
			if (mFileInfos == null || mFileInfos.length == 0)
				return;
			mStatus = new DownloadStatus[mFileInfos.length];
			for (int i = 0; i < mStatus.length; i++)
			{
				mStatus[i] = DownloadStatus.UNDOWNLOAD;
			}
		}

		public void start()
		{
			if (mFileInfos == null || mFileInfos.length == 0)
				return;
			final int length = mFileInfos.length;
			if (mDownloadIndex < length)
			{
				checkAndDownloadIfNeed(mContext, mTag, mFileInfos[mDownloadIndex], mDirectoryPath,
						new OnFileDownloadCallback()
						{
							@Override
							public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
							{
								mStatus[mDownloadIndex] = DownloadStatus.DOWNLOADING;
								if (mFileDownloadCallback != null)
									mFileDownloadCallback.onMultiDownloadStarted(tag, fileInfo, localPath,
											mDownloadIndex);
							}

							@Override
							public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath, long count,
									long length, float speed)
							{
								if (mFileDownloadCallback != null)
									mFileDownloadCallback.onMultiDownloadProgress(tag, fileInfo, localPath, count,
											length, speed, mDownloadIndex);
							}

							@Override
							public void onDownloadFinished(Object tag, FileInfo info, String localPath)
							{
								mStatus[mDownloadIndex] = DownloadStatus.FINISHED;
								if (mFileDownloadCallback != null)
									mFileDownloadCallback.onMultiDownloadFinished(tag, info, localPath, mDownloadIndex);
								mDownloadIndex++;
								if (mDownloadIndex < length)
								{
									LogUtil.d(TAG, "onMultiDownloadIndex: " + mDownloadIndex);
									checkAndDownloadIfNeed(mContext, tag, mFileInfos[mDownloadIndex], mDirectoryPath,
											this, mShowProgressDialog);
								}
								else
								{
									LogUtil.v(TAG, "onMultiAllDownloadFinished: " + tag);
									if (mFileDownloadCallback != null)
										mFileDownloadCallback.onMultiAllDownloadFinished(tag, mFileInfos, mStatus);
								}
							}

							@Override
							public void onDownloadFailed(Object tag, FileInfo info, ErrorInfo error)
							{
								mStatus[mDownloadIndex] = DownloadStatus.FAILED;
								if (mFileDownloadCallback != null)
									mFileDownloadCallback.onMultiDownloadFailed(tag, info, error, mDownloadIndex);
								mDownloadIndex++;
								if (mStopDownloadWithFail || mDownloadIndex >= length)
								{
									LogUtil.v(TAG, "onMultiAllDownloadFinished: " + tag);
									if (mFileDownloadCallback != null)
										mFileDownloadCallback.onMultiAllDownloadFinished(tag, mFileInfos, mStatus);
									return;
								}
								LogUtil.d(TAG, "onMultiDownloadIndex: " + mDownloadIndex);
								checkAndDownloadIfNeed(mContext, tag, mFileInfos[mDownloadIndex], mDirectoryPath, this,
										mShowProgressDialog);
							}

							@Override
							public void onDownloadCanceled(Object tag, FileInfo info)
							{
								mStatus[mDownloadIndex] = DownloadStatus.CANCELED;
								if (mFileDownloadCallback != null)
									mFileDownloadCallback.onMultiDownloadCanceled(tag, info, mDownloadIndex);
								// if (mFileDownloadCallback != null)
								// mFileDownloadCallback.onMultiAllDownloadFinished(tag, mFileInfos, mStatus);
							}
						}, mShowProgressDialog, mContinued);
			}
		}
	}

	private static void showDownloadProgressDialog(final Activity activity, final String url)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (mDownloadProgressDialog == null)
				{
					try
					{
						int layout = ResourceUtil.getLayoutId(activity, "download_progress_dialog");
						mDownloadProgressView = View.inflate(activity, layout, null);

						int id = ResourceUtil.getId(activity, "title");
						TextView title = (TextView)mDownloadProgressView.findViewById(id);
						title.setText("文件下载中，请稍候...");

						id = ResourceUtil.getId(activity, "progressBar");
						ProgressBar progressBar = (ProgressBar)mDownloadProgressView.findViewById(id);
						progressBar.setProgress(0);
						progressBar.setChangeProgressMode(ChangeProgressMode.NONE);
						progressBar.setSliderVisible(false);

						id = ResourceUtil.getId(activity, "percent");
						TextView percent = (TextView)mDownloadProgressView.findViewById(id);
						percent.setText("0%");

						id = ResourceUtil.getId(activity, "cancel");
						View cancel = mDownloadProgressView.findViewById(id);
						cancel.setVisibility(View.VISIBLE);
						cancel.setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								cancelDownload(url);
							}
						});

						mDownloadProgressDialog = AndroidUtils.createDialog(activity, mDownloadProgressView, false,
								false);
						mDownloadProgressDialog.show();
					}
					catch (Throwable th)
					{
						th.printStackTrace();
					}
				}
			}
		});
	}

	private static void hideProgressBar()
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (mDownloadProgressDialog != null)
				{
					mDownloadProgressDialog.dismiss();
					mDownloadProgressDialog = null;
					mDownloadProgressView = null;
				}
			}
		});
	}

	private static void updateProgressBar(final float percent, final float speed)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (mDownloadProgressView != null)
				{
					int id = ResourceUtil.getId(mDownloadProgressView.getContext(), "speed");
					TextView speedView = (TextView)mDownloadProgressView.findViewById(id);
					String msg = "当前速度 " + (int)speed + "k/s";
					speedView.setText(msg);

					id = ResourceUtil.getId(mDownloadProgressView.getContext(), "progressBar");
					ProgressBar progressBar = (ProgressBar)mDownloadProgressView.findViewById(id);
					progressBar.setProgress(percent);

					id = ResourceUtil.getId(mDownloadProgressView.getContext(), "percent");
					TextView percentView = (TextView)mDownloadProgressView.findViewById(id);
					percentView.setText((int)percent + "%");
				}
			}
		});
	}

	private static void updateProgressBar(final long count, final long length, final float speed)
	{
		float percent = count * 100 / length;
		updateProgressBar(percent, speed);
	}
}
