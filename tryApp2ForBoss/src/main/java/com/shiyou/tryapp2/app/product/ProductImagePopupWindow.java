package com.shiyou.tryapp2.app.product;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.extend.ErrorInfo;
import android.extend.app.BaseFragment;
import android.extend.loader.BitmapLoader.DecodeMode;
import android.extend.util.AndroidUtils;
import android.extend.util.FileUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.util.ViewTools;
import android.extend.widget.ExtendImageView;
import android.extend.widget.SpinnerPopupWindow;
import android.extend.widget.TouchImageView;
import android.extend.widget.TouchImageView.GestureMode;
import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.BasePagerAdapter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.shiyou.tryapp2.FileDownloadHelper;
import com.shiyou.tryapp2.FileDownloadHelper.OnFileDownloadCallback;
import com.shiyou.tryapp2.boss.zsa.R;
import com.shiyou.tryapp2.data.FileInfo;
import com.shiyou.tryapp2.data.ImageInfo;

public class ProductImagePopupWindow
{
	public static final String TAG = ProductImagePopupWindow.class.getSimpleName();

	private Activity mActivity;
	private PopupWindow mPopupWindow;
	private ImageInfo[] mImageInfos;

	private View mContentView;
	private ViewPager mViewPager;
	private BasePagerAdapter<AbsAdapterItem> mPagerAdapter;
	private ViewGroup mDotContainer;
	
	private BaseFragment mFramgent;

	public ProductImagePopupWindow(Activity activity, ImageInfo[] imageInfos)
	{
		mActivity = activity;
		mImageInfos = imageInfos;
	}
	
	public ProductImagePopupWindow(BaseFragment fragment, Activity activity, ImageInfo[] imageInfos)
	{
		mActivity = activity;
		mImageInfos = imageInfos;
		mFramgent = fragment;
	}

	private void ensureContentView()
	{
		int layout = ResourceUtil.getLayoutId(mActivity, "product_image_pw");
		mContentView = View.inflate(mActivity, layout, null);

		int id = ResourceUtil.getId(mActivity, "dot_container");
		mDotContainer = (ViewGroup)mContentView.findViewById(id);
		ensureDots(mImageInfos.length);

		id = ResourceUtil.getId(mActivity, "viewpager");
		mViewPager = (ViewPager)mContentView.findViewById(id);
		mPagerAdapter = new BasePagerAdapter<AbsAdapterItem>();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				LogUtil.v(TAG, "onPageSelected: " + position);
				setSelectdDot(position);
				int count = mPagerAdapter.getCount();
				for (int i = 0; i < count; i++)
				{
					if (i == position)
						continue;
					View view = mPagerAdapter.getItemView(position);
					if (view == null)
						continue;
					TouchImageView imageView = (TouchImageView)view;
					imageView.resetMatrixToSource();
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
			{
			}

			@Override
			public void onPageScrollStateChanged(int state)
			{
			}
		});
		mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				int width = mViewPager.getWidth();
				int height = mViewPager.getHeight();
				if (width == 0 || height == 0)
					return;
				LogUtil.v(TAG, "mViewPager size: " + width + "x" + height);
				mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				LayoutParams params = mViewPager.getLayoutParams();
				params.width = width;
				params.height = height;
				mViewPager.setLayoutParams(params);
			}
		});

		for (ImageInfo imageInfo : mImageInfos)
		{
			mPagerAdapter.addItem(new PagerAdapterItem(imageInfo));
		}
	}

	private void ensureDots(final int length)
	{
		// if (mAdvertisementResponse != null && mAdvertisementResponse.datas !=
		// null
		// && mAdvertisementResponse.datas.adv_list != null)
		// {
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				mDotContainer.removeAllViews();
				for (int i = 0; i < length; i++)
				{
					ImageView view = new ImageView(mActivity);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.topMargin = AndroidUtils.dp2px(mActivity, 2);
					params.bottomMargin = AndroidUtils.dp2px(mActivity, 2);
					params.leftMargin = AndroidUtils.dp2px(mActivity, 20);
					params.rightMargin = AndroidUtils.dp2px(mActivity, 20);
					view.setLayoutParams(params);
					int dotUnfocusId = ResourceUtil.getDrawableId(mActivity, "dot_container_bg1");
					view.setImageResource(dotUnfocusId);
					view.setScaleType(ScaleType.CENTER);
					mDotContainer.addView(view);
				}
				setSelectdDot(0);
			}
		});
		// }
	}

	private void setSelectdDot(final int index)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				LogUtil.d(TAG, "setSelectdDot: " + index);
				int dotFocusId = ResourceUtil.getDrawableId(mActivity, "dot_container_bg");
				int dotUnfocusId = ResourceUtil.getDrawableId(mActivity, "dot_container_bg1");
				int count = mDotContainer.getChildCount();
				for (int i = 0; i < count; i++)
				{
					ImageView child = (ImageView)mDotContainer.getChildAt(i);
					if (i == index)
					{
						child.setImageResource(dotFocusId);
					}
					else
					{
						child.setImageResource(dotUnfocusId);
					}
				}
			}
		});
	}

	public void show(final int index)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				LogUtil.d(TAG, "show: " + index);
				ensureContentView();
				mPopupWindow = new PopupWindow(mContentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				mPopupWindow.setBackgroundDrawable(new ColorDrawable());
				mPopupWindow.setFocusable(true);
				mPopupWindow.setTouchable(true);
				mPopupWindow.setOutsideTouchable(true);
				mPopupWindow.setClippingEnabled(true);
				mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
				mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				mPopupWindow.showAtLocation(ViewTools.getActivityDecorView(mActivity), Gravity.CENTER, 0, 0);
				Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.grow_in_center);
				mContentView.startAnimation(anim);
				AndroidUtils.MainHandler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						mViewPager.setCurrentItem(index, false);
					}
				}, 100L);
			}
		});
	}

	public void hide()
	{
		if (mPopupWindow != null)
		{
			Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.shrink_out_center);
			anim.setAnimationListener(new AnimationListener()
			{
				@Override
				public void onAnimationStart(Animation animation)
				{
				}

				@Override
				public void onAnimationRepeat(Animation animation)
				{
				}

				@Override
				public void onAnimationEnd(Animation animation)
				{
					mPopupWindow.dismiss();
					mPopupWindow = null;
				}
			});
			mContentView.startAnimation(anim);
		}
	}

	private SpinnerPopupWindow mPopMenuWindow;

	private void showPopMenuWindow(final ImageInfo imageInfo)
	{
		if (mPopMenuWindow == null)
		{
			List<String> list = new ArrayList<String>();
			list.add("保  存");
//			list.add("不 保 存");
			mPopMenuWindow = new SpinnerPopupWindow(mActivity, R.layout.popup_menu_dialog, R.layout.popup_menu_item,
					list);
		}
		mPopMenuWindow.setOnItemClickListener(new SpinnerPopupWindow.OnItemClickListener()
		{
			@Override
			public void onItemClick(int position)
			{
				switch (position)
				{
					case 0:
						saveImage(imageInfo);
						break;
//					case 1:
//						Toast.makeText(mActivity, "默认Toast样式",
//							     Toast.LENGTH_SHORT).show();
//						break;
				}
			}
		});
		mPopMenuWindow.setWidth(mActivity.getWindow().getDecorView().getWidth() / 2);
		mPopMenuWindow.showAtLocation(mActivity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
	}

	private void saveImage(final ImageInfo imageInfo)
	{
		FileDownloadHelper.checkAndDownloadIfNeed(mActivity, imageInfo, imageInfo, new OnFileDownloadCallback()
		{
			@Override
			public void onDownloadStarted(Object tag, FileInfo fileInfo, String localPath)
			{
			}

			@Override
			public void onDownloadProgress(Object tag, FileInfo fileInfo, String localPath, long count, long length,
					float speed)
			{
			}

			@Override
			public void onDownloadFinished(Object tag, FileInfo fileInfo, String localPath)
			{
				String fileName = FileUtils.getFileFullName(imageInfo.url);
				File file = FileUtils.getFile(mActivity, "images", fileName);
				if (FileUtils.copyFile(new File(localPath), file))
				{
					AndroidUtils.showToast(mActivity, "图片已保存至" + file.getAbsolutePath(), Toast.LENGTH_LONG);
					//  这个广播的目的就是更新图库	
					Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
					Uri uri = Uri.fromFile(file);
					intent.setData(uri);
					mActivity.sendBroadcast(intent);
				}
				else
					AndroidUtils.showToast(mActivity, "图片保存失败，请检查SDCard");
			}

			@Override
			public void onDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error)
			{
				AndroidUtils.showToast(mActivity, "图片下载失败，请检查网络");
			}

			@Override
			public void onDownloadCanceled(Object tag, FileInfo fileInfo)
			{
			}
		}, false);
	}

	private class PagerAdapterItem extends AbsAdapterItem
	{
		private ImageInfo mImageInfo;

		public PagerAdapterItem(ImageInfo imageInfo)
		{
			mImageInfo = imageInfo;
		}

		@Override
		public View onCreateView(int position, ViewGroup parent)
		{
			Context context = parent.getContext();
			final TouchImageView view = new TouchImageView(context);
			view.setBackgroundColor(context.getResources().getColor(android.R.color.black));
			view.setLayoutParams(new ViewPager.LayoutParams());
			view.setScaleType(ScaleType.FIT_CENTER);
			view.setGestureMode(GestureMode.VIEWPAGER);
			view.setOnlyMoveOutOfViewBounds(true);
			// view.setAutoRecyleOnDetachedFromWindow(true);
			view.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					hide();
				}
			});
			view.setOnLongClickListener(new View.OnLongClickListener()
			{
				@Override
				public boolean onLongClick(View v)
				{
					showPopMenuWindow(mImageInfo);
					return true;
				}
			});
			return view;
		}

		@Override
		public void onUpdateView(View view, int position, ViewGroup parent)
		{
			Log.i("ProductImagePuputWindow", "onUpdateView");
		}

		@Override
		public void onLoadViewResource(View view, int position, ViewGroup parent)
		{
			Log.i("ProductImagePuputWindow", "onLoadViewResource");
			ExtendImageView imageView = (ExtendImageView)view;
			if (mImageInfo != null)
				imageView.setImageDataSource(mImageInfo.url, mImageInfo.filemtime, DecodeMode.FIT_WIDTH);
			imageView.setImageLoadListener(new ExtendImageView.OnImageLoadListener() {
				 
				@Override
				public void onImageLoadStarted(ExtendImageView imageView) {
					// TODO Auto-generated method stub
//					dialog.show();
					mFramgent.showLoadingIndicator();
				}
				
				@Override
				public void onImageLoadFinished(ExtendImageView imageView, boolean success) {
					// TODO Auto-generated method stub
//					dialog.hide();
					mFramgent.hideLoadingIndicator();
				}
				
				@Override
				public void onImageLoadCanceled(ExtendImageView imageView) {
					// TODO Auto-generated method stub
					mFramgent.hideLoadingIndicator();
				}
			});
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageView.startImageLoad();
		}

		@Override
		public void onRecycleViewResource(View view, int position, ViewGroup parent)
		{
			Log.i("ProductImagePuputWindow", "onRecycleViewResource");
//			ExtendImageView imageView = (ExtendImageView)view;
//			imageView.recyleBitmapImage();
		}
	}
}
