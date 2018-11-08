package com.shiyou.tryapp2.app.product;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.extend.ErrorInfo;
import android.extend.app.BaseFragment;
import android.extend.loader.Loader;
import android.extend.util.AndroidUtils;
import android.extend.util.FileUtils;
import android.extend.util.ResourceUtil;
import android.extend.widget.PDFRendererView;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shiyou.tryapp2.FileDownloadHelper;
import com.shiyou.tryapp2.FileDownloadHelper.OnFileDownloadCallback;
import com.shiyou.tryapp2.data.FileInfo;

@SuppressLint("ValidFragment")
public class PDFViewerFragment extends BaseFragment
{
	public static String getPDFDirectoryPath(Context context)
	{
		return FileUtils.getDirectory(context, "pdf").getAbsolutePath();
	}

	private String mTitle;
	private String mUrl;

	private PDFRendererView mPdfRendererView;

	public PDFViewerFragment(String title, String url)
	{
		mTitle = title;
		mUrl = url;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		mLayoutResID = ResourceUtil.getLayoutId(getContext(), "pdfviewer_layout");
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		int id = ResourceUtil.getId(getContext(), "pdfView");
		mPdfRendererView = (PDFRendererView)view.findViewById(id);

		id = ResourceUtil.getId(getContext(), "boss_details_back");
		View boss_details_back = view.findViewById(id);
		boss_details_back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				getActivity().onBackPressed();
			}
		});
		id=ResourceUtil.getId(getContext(),"boss_share_gia");
		ImageView shareGia= (ImageView) view.findViewById(id);
//		mPdfRendererView.setVisibility(View.GONE);

//		id=ResourceUtil.getId(getContext(),"gia_png");
//		ImageView imageView= (ImageView) view.findViewById(id);
//		imageView.setImageBitmap(bitmap);

		shareGia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "onClick: 执行");
				Bitmap bitmap=mPdfRendererView.getBitmap();
				Log.d(TAG, "onClick: bitmap="+bitmap);
				Uri uri=Uri.parse(MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, mTitle,"image/*"));
				Log.d(TAG, "onClick: uri="+uri);
//                Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//				mulIntent.putExtra(Intent.EXTRA_STREAM,uri);
//				mulIntent.setType("image/*");
//				startActivity(Intent.createChooser(mulIntent,"GIA分享"));
//				Log.d(TAG, "onClick: 完成");
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_SEND);//设置分享行为
				intent.setType("image/*");//设置分享内容的类型
				intent.putExtra(Intent.EXTRA_STREAM, uri);
				intent = Intent.createChooser(intent, "分享");
				startActivity(intent);
			}
		});

		id = ResourceUtil.getId(getContext(), "boss_details_title");
		TextView boss_details_title = (TextView)view.findViewById(id);
		boss_details_title.setText(mTitle);

		return view;
	}

	@Override
	public void onFirstStart()
	{
		super.onFirstStart();
		if (!TextUtils.isEmpty(mUrl))
		{
			if (Loader.isAssetUrl(mUrl))
			{
				String fileName = mUrl.substring(Loader.PROTOCOL_ASSETS.length());
				try
				{
					mPdfRendererView.openFromAsset(fileName);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					showToast("读取文件失败，请检查存储卡");
				}
			}
			else if (Loader.isHttpUrl(mUrl))
			{
				// showLoadingIndicator(true);
				FileInfo fileInfo = new FileInfo();
				fileInfo.url = mUrl;
				fileInfo.path = getPDFDirectoryPath(getContext()) + File.separator + mTitle + ".pdf";
				FileDownloadHelper.checkAndDownloadIfNeed(getContext(), TAG, fileInfo, new OnFileDownloadCallback()
				{
					@Override
					public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
					{
					}

					@Override
					public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath, long count,
							long length, float speed)
					{
					}

					@Override
					public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath)
					{
						// hideLoadingIndicator();
						openFromFile(localPath);
					}

					@Override
					public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error)
					{
						// hideLoadingIndicator();
						showToast("网络错误: " + error.errorCode);
					}
					//测试git
					//测试git2
					@Override
					public void onDownloadCanceled(Object tag, FileInfo fileInfo)
					{
					}
				}, true);
			}
			else
			{
				openFromFile(mUrl);
			}
		}
	}

	private void openFromFile(final String path)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					mPdfRendererView.openFromFile(path);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
//					try
//					{
//						mPdfRendererView.closeRenderer();
//					}
//					catch (Throwable e1)
//					{
//						e1.printStackTrace();
//					}
					showToast("未找到文件,path=" + path);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					showToast("加载文件失败，请稍后重试");
//					try
//					{
//						mPdfRendererView.closeRenderer();
//					}
//					catch (Throwable e1)
//					{
//						e1.printStackTrace();
//					}
					FileUtils.deleteFile(path);
				}
			}
		});
	}
	public Intent getPdfFileIntent(String pdfUrl) {
		Intent intent = new Intent(Intent.ACTION_VIEW );
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(pdfUrl));
		//intent.putExtra(Intent.EXTRA_STREAM, uri);
		//intent.setType("application/*");
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}
}
