package com.shiyou.tryapp2;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.extend.ErrorInfo;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.util.AndroidUtils;
import android.extend.util.FileUtils;
import android.extend.util.LogUtil;
import android.text.format.DateFormat;

import com.shiyou.tryapp2.FileDownloadHelper.DownloadStatus;
import com.shiyou.tryapp2.FileDownloadHelper.OnFileDownloadCallback;
import com.shiyou.tryapp2.FileDownloadHelper.OnMultiFileDownloadCallback;
import com.shiyou.tryapp2.RequestManager.RequestCallback;
import com.shiyou.tryapp2.app.login.LoginHelper;
import com.shiyou.tryapp2.data.FileInfo;
import com.shiyou.tryapp2.data.response.BaseResponse;
import com.shiyou.tryapp2.data.response.CoupleRingDetailResponse;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse;
import com.shiyou.tryapp2.data.response.GoodsListResponse;

public class ResourceHelper
{
	public static final String TAG = "ResourceHelper";

	public static File getResourceDirectory(Context context)
	{
		return FileUtils.getDirectory(context, "resource");
	}

	public static File getGoodsImageDirectory(Context context)
	{
		return FileUtils.getDirectory(context, "resource/image");
	}

	public static File getCombineImageDirectory(Context context)
	{
		return FileUtils.getDirectory(context, "resource/combine");
	}

	public static File getTempFileDirectory(Context context)
	{
		return FileUtils.getDirectory(context, "resource/temp_file");
	}

	public static File getTryModelDirectory(Context context)
	{
		return FileUtils.getDirectory(context, "resource/try_model");
	}

	public static void cleanResource(Context context)
	{
		FileUtils.deleteFilesInChildren(getResourceDirectory(context));
	}

	//
	// private static ResourceHelper instance = null;
	//
	// public static ResourceHelper newInstance(Activity activity, String shopId)
	// {
	// instance = new ResourceHelper(activity, shopId);
	// return instance;
	// }
	//
	// public static ResourceHelper getInstance()
	// {
	// return instance;
	// }

	public interface OnResourceDownloadCallback
	{
		public void onDownloadFinished(Object data);
	}

	private static final CharSequence DATEFORMAT = "yyyyMMdd";
	private Activity mActivity;
	private String mShopId;
	private int mDownloadStep = 0;
	private ProgressDialog mProgressDialog;
	private OnResourceDownloadCallback mCallback;

	private static final String Pref_Name = "resouce_downloaded";

	public ResourceHelper(Activity activity)
	{
		mActivity = activity;
		mShopId = activity.getPackageName();
		mDownloadStep = 0;
	}

	private void showProgressDialog()
	{
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (mProgressDialog == null)
				{
					mProgressDialog = new ProgressDialog(mActivity);
					mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					mProgressDialog.setCancelable(false);
					mProgressDialog.setCanceledOnTouchOutside(false);
					mProgressDialog.show();
					mProgressDialog.setButton(Dialog.BUTTON_NEGATIVE, "取消", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							// dialog.dismiss();
						}
					});
				}
			}
		});
	}

	private String mTitle;

	private void updateProgressDialogTitle(final String title)
	{
		mTitle = title;
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (mProgressDialog != null)
					mProgressDialog.setTitle(title);
			}
		});
	}

	// private void updateProgressDialogMessage(final String message)
	// {
	// mActivity.runOnUiThread(new Runnable()
	// {
	// @Override
	// public void run()
	// {
	// if (mProgressDialog != null)
	// mProgressDialog.setMessage(message);
	// }
	// });
	// }

	private void updateProgressDialog(final int progress, final int max)
	{
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (mProgressDialog != null)
				{
					mProgressDialog.setMax(max);
					mProgressDialog.setProgress(progress);
					mProgressDialog.setMessage(mTitle + " " + progress + "/" + max);
				}
			}
		});
	}

	private void hideProgressDialog()
	{
		mActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				if (mProgressDialog != null)
				{
					mProgressDialog.dismiss();
					mProgressDialog = null;
				}
			}
		});
	}

	private abstract class MyRequestCallback implements RequestCallback
	{
		@Override
		public void onRequestResult(int requestCode, long taskId, BaseResponse response, DataFrom from)
		{
			if (response.resultCode != BaseResponse.RESULT_OK)
			{
				// AndroidUtils.showToast(mActivity, response.error);
				onResponseFail(response, null);
			}
			else
			{
				onResponseSuccess(response);
			}
		}

		@Override
		public void onRequestError(int requestCode, long taskId, ErrorInfo error)
		{
			// AndroidUtils.showToast(mActivity, "网络错误: " + error.errorCode);
			onResponseFail(null, error);
		}

		abstract void onResponseSuccess(BaseResponse response);

		abstract void onResponseFail(BaseResponse response, ErrorInfo error);
	}

	private String getCurrentDate()
	{
		return DateFormat.format(DATEFORMAT, System.currentTimeMillis()).toString();
	}

	private synchronized boolean checkResourceDownloaded()
	{
		SharedPreferences pref = mActivity.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE);
		String value = pref.getString(mShopId, "");
		String date = getCurrentDate();
		boolean result = date.equals(value);
		LogUtil.i(TAG, "checkResourceDownloaded: " + mShopId + "; " + result);
		return result;
	}

	private synchronized void setResourceDownloaded()
	{
		SharedPreferences pref = mActivity.getSharedPreferences(Pref_Name, Context.MODE_PRIVATE);
		Editor edit = pref.edit();
		edit.putString(mShopId, getCurrentDate());
		edit.commit();
	}

	public void checkAndLoadResource(OnResourceDownloadCallback callback)
	{
		if (checkResourceDownloaded())
		{
			if (callback != null)
				callback.onDownloadFinished(TAG);
			return;
		}
		mCallback = callback;
		startDonwloadNextResource();
	}

	public void loadResource(OnResourceDownloadCallback callback)
	{
		mCallback = callback;
		startDonwloadNextResource();
	}

	private void startDonwloadNextResource()
	{
		LogUtil.d(TAG, "startDonwloadNextResource: " + mDownloadStep);
		switch (mDownloadStep)
		{
			case 0:
				showProgressDialog();
				mDownloadStep++;
				loadShopAllGoodsList();
				break;
			case 1:
				mDownloadStep++;
				setResourceDownloaded();
				hideProgressDialog();
				if (mCallback != null)
					mCallback.onDownloadFinished(TAG);
				break;
		}
	}

	// 获取门店所有商品列表
	private void loadShopAllGoodsList()
	{
		updateProgressDialogTitle("下载商品");

		RequestManager.loadGoodsList(mActivity, new MyRequestCallback()
		{
			@Override
			void onResponseSuccess(BaseResponse response)
			{
				GoodsListResponse glResponse = (GoodsListResponse)response;
				if (glResponse.datas != null && glResponse.datas.list != null && glResponse.datas.list.length > 0)
				{
					AndroidUtils.MainHandler.post(new LoadGoodsListDetailRunnable(glResponse.datas.list));
				}
				else
					startDonwloadNextResource();
			}

			@Override
			void onResponseFail(BaseResponse response, ErrorInfo error)
			{
				startDonwloadNextResource();
			}
		});
	}

	// 获取商品列表详情
	private class LoadGoodsListDetailRunnable implements Runnable
	{
		int count = 0;
		GoodsListResponse.GoodsItem[] goodsList;

		public LoadGoodsListDetailRunnable(List<GoodsListResponse.GoodsItem> list)
		{
			goodsList = new GoodsListResponse.GoodsItem[list.size()];
			goodsList = list.toArray(goodsList);
		}

		public LoadGoodsListDetailRunnable(GoodsListResponse.GoodsItem[] goodsList)
		{
			this.goodsList = goodsList;
		}

		@Override
		public void run()
		{
			updateProgressDialog(count, goodsList.length);
			if (count < goodsList.length)
			{
				GoodsListResponse.GoodsItem item = goodsList[count];
				LogUtil.d(TAG, "startDownloadGoodsListRes: " + item.id);
				count++;
				if (item.tag.equals(GoodsListResponse.GoodsItem.TAG_RING))
					RequestManager.loadGoodsDetail(mActivity, LoginHelper.getUserKey(), item.id, new MyRequestCallback()
					{
						@Override
						void onResponseSuccess(BaseResponse response)
						{
							GoodsDetailResponse gdResponse = (GoodsDetailResponse)response;
							AndroidUtils.MainHandler.post(new DownloadGoodsDetailResourceRunnable(gdResponse.datas,
									new OnResourceDownloadCallback()
									{
										@Override
										public void onDownloadFinished(Object data)
										{
											AndroidUtils.MainHandler.post(LoadGoodsListDetailRunnable.this);
										}
									}));
						}

						@Override
						void onResponseFail(BaseResponse response, ErrorInfo error)
						{
							AndroidUtils.MainHandler.post(LoadGoodsListDetailRunnable.this);
						}
					});
				else if (item.tag.equals(GoodsListResponse.GoodsItem.TAG_COUPLE))
				{
					RequestManager.loadCoupleRingDetail(mActivity, LoginHelper.getUserKey(), item.id, new MyRequestCallback()
					{
						@Override
						void onResponseSuccess(BaseResponse response)
						{
							CoupleRingDetailResponse crdResponse = (CoupleRingDetailResponse)response;
							AndroidUtils.MainHandler.post(new DownloadGoodsDetailResourceRunnable(crdResponse.datas,
									new OnResourceDownloadCallback()
									{
										@Override
										public void onDownloadFinished(Object data)
										{
											AndroidUtils.MainHandler.post(LoadGoodsListDetailRunnable.this);
										}
									}));
						}

						@Override
						void onResponseFail(BaseResponse response, ErrorInfo error)
						{
							AndroidUtils.MainHandler.post(LoadGoodsListDetailRunnable.this);
						}
					});
				}
			}
			else
			{
				startDonwloadNextResource();
			}
		}
	}

	// 下载单个商品相关资源
	private class DownloadGoodsDetailResourceRunnable implements Runnable
	{
		GoodsDetailResponse.GoodsDetail goodsDetail;
		CoupleRingDetailResponse.GoodsDetail coupleRingGoodsDetail;
		OnResourceDownloadCallback callback;
		int downloadStep = 0;

		public DownloadGoodsDetailResourceRunnable(GoodsDetailResponse.GoodsDetail goodsDetail,
				OnResourceDownloadCallback callback)
		{
			this.goodsDetail = goodsDetail;
			this.callback = callback;
		}

		public DownloadGoodsDetailResourceRunnable(CoupleRingDetailResponse.GoodsDetail goodsDetail,
				OnResourceDownloadCallback callback)
		{
			this.coupleRingGoodsDetail = goodsDetail;
			this.callback = callback;
		}

		@Override
		public void run()
		{
			if (goodsDetail != null)
			{
				LogUtil.d(TAG, "startDownloadGoodsDetailRes goods_id=" + goodsDetail.id + "; downloadStep="
						+ downloadStep);
				switch (downloadStep)
				{
					case 0:// 下载商品首图
						downloadStep++;
						if (goodsDetail.thumb != null)
						{
							FileInfo fileInfo = goodsDetail.thumb;
							fileInfo.path = getGoodsImageDirectory(mActivity).getAbsolutePath() + File.separator
									+ FileUtils.getFileName(fileInfo.url);
							FileDownloadHelper.checkAndDownloadIfNeed(mActivity, goodsDetail, fileInfo,
									new OnFileDownloadCallback()
									{
										@Override
										public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
										{
										}

										@Override
										public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath,
												long count, long length, float speed)
										{
										}

										@Override
										public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadCanceled(Object tag, FileInfo fileInfo)
										{
										}
									}, false);
						}
						else
						{
							AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
						}
						break;
					case 1:// 下载商品相册图
						downloadStep++;
						if (goodsDetail.thumb_url != null && goodsDetail.thumb_url.length > 0)
						{
							FileInfo[] fileInfos = goodsDetail.thumb_url;
							for (FileInfo fileInfo : fileInfos)
							{
								fileInfo.path = getGoodsImageDirectory(mActivity).getAbsolutePath() + File.separator
										+ FileUtils.getFileName(fileInfo.url);
							}
							FileDownloadHelper.startMultiDownload(fileInfos, mActivity, fileInfos,
									new OnMultiFileDownloadCallback()
									{
										public void onMultiDownloadStarted(Object tag, FileInfo fileInfo,
												String localPath, int downloadIndex)
										{
										}

										public void onMultiDownloadProgress(Object tag, FileInfo fileInfo,
												String localPath, long count, long length, float speed,
												int downloadIndex)
										{
										}

										public void onMultiDownloadFinished(Object tag, FileInfo fileInfo,
												String localPath, int downloadIndex)
										{
										}

										public void onMultiDownloadCanceled(Object tag, FileInfo fileInfo,
												int downloadIndex)
										{
										}

										public void onMultiDownloadFailed(Object tag, FileInfo fileInfo,
												ErrorInfo error, int downloadIndex)
										{
										}

										public void onMultiAllDownloadFinished(Object tag, FileInfo[] fileInfos,
												DownloadStatus[] status)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}
									}, false, false);
						}
						else
						{
							AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
						}
						break;
					case 2:// 下载商品模型资源
						downloadStep++;
						if (goodsDetail.model_info != null)
						{
							FileInfo fileInfo = goodsDetail.model_info;
							fileInfo.path = getTempFileDirectory(mActivity).getAbsolutePath() + File.separator
									+ FileUtils.getFileName(fileInfo.url);
							FileDownloadHelper.checkAndDownloadIfNeed(mActivity, goodsDetail, fileInfo,
									new OnFileDownloadCallback()
									{
										@Override
										public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
										{
										}

										@Override
										public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath,
												long count, long length, float speed)
										{
										}

										@Override
										public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadCanceled(Object tag, FileInfo fileInfo)
										{
										}
									}, false);
						}
						else
						{
							AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
						}
						break;
					case 3:
						if (callback != null)
							callback.onDownloadFinished(goodsDetail);
						break;
				}
			}
			else if (coupleRingGoodsDetail != null)
			{
				LogUtil.d(TAG, "startDownloadCoupleRingGoodsDetailRes goods_id=" + coupleRingGoodsDetail.id
						+ "; downloadStep=" + downloadStep);
				switch (downloadStep)
				{
					case 0:// 下载商品首图
						downloadStep++;
						if (coupleRingGoodsDetail.thumb != null)
						{
							FileInfo fileInfo = coupleRingGoodsDetail.thumb;
							fileInfo.path = getGoodsImageDirectory(mActivity).getAbsolutePath() + File.separator
									+ FileUtils.getFileName(fileInfo.url);
							FileDownloadHelper.checkAndDownloadIfNeed(mActivity, coupleRingGoodsDetail, fileInfo,
									new OnFileDownloadCallback()
									{
										@Override
										public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
										{
										}

										@Override
										public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath,
												long count, long length, float speed)
										{
										}

										@Override
										public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadCanceled(Object tag, FileInfo fileInfo)
										{
										}
									}, false);
						}
						else
						{
							AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
						}
						break;
					case 1:// 下载商品相册图
						downloadStep++;
						if (coupleRingGoodsDetail.thumb_url != null && coupleRingGoodsDetail.thumb_url.length > 0)
						{
							FileInfo[] fileInfos = coupleRingGoodsDetail.thumb_url;
							for (FileInfo fileInfo : fileInfos)
							{
								fileInfo.path = getGoodsImageDirectory(mActivity).getAbsolutePath() + File.separator
										+ FileUtils.getFileName(fileInfo.url);
							}
							FileDownloadHelper.startMultiDownload(fileInfos, mActivity, fileInfos,
									new OnMultiFileDownloadCallback()
									{
										public void onMultiDownloadStarted(Object tag, FileInfo fileInfo,
												String localPath, int downloadIndex)
										{
										}

										public void onMultiDownloadProgress(Object tag, FileInfo fileInfo,
												String localPath, long count, long length, float speed,
												int downloadIndex)
										{
										}

										public void onMultiDownloadFinished(Object tag, FileInfo fileInfo,
												String localPath, int downloadIndex)
										{
										}

										public void onMultiDownloadCanceled(Object tag, FileInfo fileInfo,
												int downloadIndex)
										{
										}

										public void onMultiDownloadFailed(Object tag, FileInfo fileInfo,
												ErrorInfo error, int downloadIndex)
										{
										}

										public void onMultiAllDownloadFinished(Object tag, FileInfo[] fileInfos,
												DownloadStatus[] status)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}
									}, false, false);
						}
						else
						{
							AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
						}
						break;
					case 2:// 下载男戒模型资源
						downloadStep++;
						if (coupleRingGoodsDetail.model_infos != null && coupleRingGoodsDetail.model_infos.men != null)
						{
							FileInfo fileInfo = coupleRingGoodsDetail.model_infos.men;
							fileInfo.path = getTempFileDirectory(mActivity).getAbsolutePath() + File.separator
									+ FileUtils.getFileName(fileInfo.url);
							FileDownloadHelper.checkAndDownloadIfNeed(mActivity, coupleRingGoodsDetail, fileInfo,
									new OnFileDownloadCallback()
									{
										@Override
										public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
										{
										}

										@Override
										public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath,
												long count, long length, float speed)
										{
										}

										@Override
										public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadCanceled(Object tag, FileInfo fileInfo)
										{
										}
									}, false);
						}
						else
						{
							AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
						}
						break;
					case 3:// 下载女戒模型资源
						downloadStep++;
						if (coupleRingGoodsDetail.model_infos != null && coupleRingGoodsDetail.model_infos.wmen != null)
						{
							FileInfo fileInfo = coupleRingGoodsDetail.model_infos.wmen;
							fileInfo.path = getTempFileDirectory(mActivity).getAbsolutePath() + File.separator
									+ FileUtils.getFileName(fileInfo.url);
							FileDownloadHelper.checkAndDownloadIfNeed(mActivity, coupleRingGoodsDetail, fileInfo,
									new OnFileDownloadCallback()
									{
										@Override
										public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
										{
										}

										@Override
										public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath,
												long count, long length, float speed)
										{
										}

										@Override
										public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error)
										{
											AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
										}

										@Override
										public void onDownloadCanceled(Object tag, FileInfo fileInfo)
										{
										}
									}, false);
						}
						else
						{
							AndroidUtils.MainHandler.post(DownloadGoodsDetailResourceRunnable.this);
						}
						break;
					case 4:
						if (callback != null)
							callback.onDownloadFinished(coupleRingGoodsDetail);
						break;
				}
			}
		}
	}
}
