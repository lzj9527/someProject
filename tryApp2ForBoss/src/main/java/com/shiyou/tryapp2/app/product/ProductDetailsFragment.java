package com.shiyou.tryapp2.app.product;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.extend.ErrorInfo;
import android.extend.app.BaseFragment;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.loader.BitmapLoader.DecodeMode;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.widget.ExtendImageView;
import android.extend.widget.MenuBar;
import android.extend.widget.MenuBar.OnMenuListener;
import android.extend.widget.MenuView;
import android.extend.widget.adapter.AbsAdapterItem;
import android.extend.widget.adapter.BaseAdapter;
import android.extend.widget.adapter.BasePagerAdapter;
import android.extend.widget.adapter.ListView;
import android.extend.widget.adapter.ScrollListView;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.shiyou.tryapp2.Define;
import com.shiyou.tryapp2.RequestManager;
import com.shiyou.tryapp2.RequestManager.RequestCallback;
import com.shiyou.tryapp2.app.MainActivity;
import com.shiyou.tryapp2.app.MainFragment;
import com.shiyou.tryapp2.app.login.LoginHelper;
import com.shiyou.tryapp2.app.util.DownloadPic;
import com.shiyou.tryapp2.app.util.ImageDownLoadCallBack;
import com.shiyou.tryapp2.boss.zsa.R;
import com.shiyou.tryapp2.data.ImageInfo;
import com.shiyou.tryapp2.data.db.BrowseHistoryDBHelper;
import com.shiyou.tryapp2.data.response.BaseResponse;
import com.shiyou.tryapp2.data.response.CoupleRingDetailResponse;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse.ErpGoods;

@SuppressLint("ValidFragment")
public class ProductDetailsFragment extends BaseFragment
{
	private String goodsId;
	private boolean isCoupleRing;
	private int[] priceRange;
	private float[] weightRange;
	private String colorCondition;
	private String clarityCondition;

	public static ProductDetailsFragment instance = null;

	private GoodsDetailResponse mGDResponse = null;
	private CoupleRingDetailResponse mCoupleRingDetailResponse = null;

	private View mBossDetailsLayout;
	private View boss_product_specification, boss_couple_product_specification;

//	private TextView shoppingcart_num;

	private ExtendImageView productImage;

	private int pages;
	private ViewPager mViewPager;
	private BasePagerAdapter<AbsAdapterItem> mPagerAdapter;
	private LinearLayout mDotContainer;
	
	private ImageView share_icon;   //分享按钮

	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			setSelectdDot(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
		{
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{
		}
	};
	
	/**
     * 单线程列队执行
     */
    private static ExecutorService singleExecutor = null;


    /**
     * 执行单线程列队执行
     */
    public void runOnQueue(Runnable runnable) {
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

	private TextView title;

	ProductImagePopupWindow mPopupWindow;

	public ProductDetailsFragment(String goodsId, boolean isCoupleRing, int[] priceRange, float[] weightRange,
			String colorCondition, String clarityCondition)
	{
		LogUtil.d(TAG, "ProductDetailsFragment: " + goodsId + "; " + isCoupleRing);
		LogUtil.v(TAG, "priceRange=" + (priceRange == null ? "null" : (priceRange[0] + "-" + priceRange[1])));
		LogUtil.v(TAG, "weightRange=" + (weightRange == null ? "null" : (weightRange[0] + "-" + weightRange[1])));
		LogUtil.v(TAG, "colorCondition=" + colorCondition + "; clarityCondition=" + clarityCondition);
		this.goodsId = goodsId;
		this.isCoupleRing = isCoupleRing;
		this.priceRange = priceRange;
		this.weightRange = weightRange;
		this.colorCondition = colorCondition;
		this.clarityCondition = clarityCondition;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		instance = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{ 
		mLayoutResID = ResourceUtil.getLayoutId(getContext(), "boss_product_details_layout");
		mBossDetailsLayout = super.onCreateView(inflater, container, savedInstanceState);

		int id = ResourceUtil.getId(getActivity(), "boss_details_back");
		View back = mBossDetailsLayout.findViewById(id);
		back.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				onBackPressed();
				getActivity().onBackPressed();
				// MainFragment.instance.onBackPressed();
			}
		});

		id = ResourceUtil.getId(getActivity(), "boss_details_title");
		title = (TextView)mBossDetailsLayout.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "boss_details_shopping");
		View shopping = mBossDetailsLayout.findViewById(id);
		shopping.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				onBackPressed();
				MainActivity.backToHomepage(getActivity(), 1);
			}
		});

		id = ResourceUtil.getId(getContext(), "shoppingcart_num");
//		shoppingcart_num = (TextView)mBossDetailsLayout.findViewById(id);
//		shoppingcart_num.setVisibility(View.GONE);
//		MainFragment.updateShoppingcartBadgeNum(getActivity(), shoppingcart_num);

		// ensureUnityPlayer();
		ensureDetailsMiddle();
		ensureDetailsButton();
		//
		ProductDetailsData();
//		productShareicon();
		
		id = ResourceUtil.getId(getContext(), "share");
		share_icon = (ImageView)mBossDetailsLayout.findViewById(id);
		
		return mBossDetailsLayout;
	}
	
	private void ensureDetailsMiddle()
	{
		int id = ResourceUtil.getId(getContext(), "product_photo");
		final View view = mBossDetailsLayout.findViewById(id);
		view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener()
		{
			@Override
			public void onGlobalLayout()
			{
				int width = view.getWidth();
				if (width == 0)
					return;
				view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				LayoutParams params = view.getLayoutParams();
				params.height = width;
				view.setLayoutParams(params);
			}
		});

		id = ResourceUtil.getId(getContext(), "select_photo");
		mViewPager = (ViewPager)mBossDetailsLayout.findViewById(id);
		mPagerAdapter = new BasePagerAdapter<AbsAdapterItem>();
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.addOnPageChangeListener(mPageChangeListener);
		// mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(
		// new OnGlobalLayoutListener() {
		// @Override
		// public void onGlobalLayout() {
		// mViewPager.getViewTreeObserver()
		// .removeOnGlobalLayoutListener(this);
		// int width = mViewPager.getWidth();
		// int height = mViewPager.getHeight();
		// LayoutParams params = mViewPager.getLayoutParams();
		// params.width = width;
		// params.height = height;
		// mViewPager.setLayoutParams(params);
		// }
		// });

		id = ResourceUtil.getId(getContext(), "dot_container");
		mDotContainer = (LinearLayout)mBossDetailsLayout.findViewById(id);
	}

	private TextView details_name;
	private TextView details_number;
	private TextView details_on_price;
	private TextView details_up_price;
	ImageView boss_add_shopping;
	View boss_add_3d;

	public void ensureDetailsButton()
	{
		int id = ResourceUtil.getId(getActivity(), "details_name");
		details_name = (TextView)mBossDetailsLayout.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "details_number");
		details_number = (TextView)mBossDetailsLayout.findViewById(id);

		// id = ResourceUtil.getId(getActivity(), "details_select_ring");
		// View details_select_ring = mBossDetailsLayout.findViewById(id);
		// details_select_ring.setOnClickListener(new View.OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v)
		// {
		// if (AndroidUtils.isFastClick())
		// return;
		// add(getActivity(), new WebRingsInterfaceFragment(Config.WebBossRings, "全球选钻"), true);
		// }
		// });

		// id = ResourceUtil.getId(getActivity(), "details_on_price");
		// details_on_price = (TextView)mBossDetailsLayout.findViewById(id);

		// id = ResourceUtil.getId(getActivity(), "details_up_price");
		// details_up_price = (TextView)mBossDetailsLayout.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "boss_product_specification");
		boss_product_specification = mBossDetailsLayout.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "boss_couple_product_specification");
		boss_couple_product_specification = mBossDetailsLayout.findViewById(id);

		if (!isCoupleRing)
		{
			boss_product_specification.setVisibility(View.VISIBLE);
			boss_couple_product_specification.setVisibility(View.GONE);
		}
		else
		{
			boss_product_specification.setVisibility(View.GONE);
			boss_couple_product_specification.setVisibility(View.VISIBLE);
		}

		id = ResourceUtil.getId(getActivity(), "boss_add_shopping");
		boss_add_shopping = (ImageView)mBossDetailsLayout.findViewById(id);
		if (isCoupleRing)
		{
			boss_add_shopping.setImageResource(R.drawable.boss_product_specification_addshopping2);
		}
		boss_add_shopping.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				// showLoadingIndicatorDialog();
				if (mGDResponse != null)
				{
					AddShoppingCart();
				}
				else if (mCoupleRingDetailResponse != null)
				{
					AddCoupleShoppingCart();
				}
				else
				{
					showToast("没有产品");
					// hideLoadingIndicatorDialog();
				}
			}
		});

		id = ResourceUtil.getId(getActivity(), "boss_add_3d");
		boss_add_3d = mBossDetailsLayout.findViewById(id);
		if (isCoupleRing)
			boss_add_3d.setVisibility(View.GONE);
		boss_add_3d.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				if (mGDResponse != null)
				{
					add(getActivity(), new Product3DFragment(mGDResponse), true);
				}
				else if (mCoupleRingDetailResponse != null)
				{
					add(getActivity(), new Product3DFragment(mCoupleRingDetailResponse), true);
				}
				else
				{
					showToast("该产品暂时没有模型");
				}
			}
		});

		BossProductSpecification();
		BossCoupleProductSpecification();

	}

	private ScrollListView selectSpecification;
	private BaseAdapter<AbsAdapterItem> selectSpecificationAdapter;

	private TextView productNumber;
	private TextView productNum;

	public void BossProductSpecification()
	{
		int id = ResourceUtil.getId(getActivity(), "product_number");
		productNumber = (TextView)boss_product_specification.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "select_specification");
		selectSpecification = (ScrollListView)boss_product_specification.findViewById(id);
		selectSpecification.setHorizontalDividerHeight(10);
		selectSpecificationAdapter = new BaseAdapter<AbsAdapterItem>();
		selectSpecification.setAdapter(selectSpecificationAdapter);
		selectSpecificationAdapter.clear();

		id = ResourceUtil.getId(getActivity(), "product_num");
		productNum = (TextView)boss_product_specification.findViewById(id);
	}

	private TextView CoupleNum;
	private ScrollListView selectMenSpecification;
	private BaseAdapter<AbsAdapterItem> selectMenSpecificationAdapter;

	private ScrollListView selectWmenSpecification;
	private BaseAdapter<AbsAdapterItem> selectWmenSpecificationAdapter;

	private TextView productMenNumber;
	private TextView productWmenNumber;

	private View wmen_specification_v;
	private View men_specification_v;

	public void BossCoupleProductSpecification()
	{
		int id = ResourceUtil.getId(getActivity(), "select_men_specification");
		selectMenSpecification = (ScrollListView)boss_couple_product_specification.findViewById(id);
		selectMenSpecification.setHorizontalDividerHeight(10);
		selectMenSpecificationAdapter = new BaseAdapter<AbsAdapterItem>();
		selectMenSpecification.setAdapter(selectMenSpecificationAdapter);
		selectMenSpecificationAdapter.clear();

		id = ResourceUtil.getId(getActivity(), "select_wmen_specification");
		selectWmenSpecification = (ScrollListView)boss_couple_product_specification.findViewById(id);
		selectWmenSpecification.setHorizontalDividerHeight(10);
		selectWmenSpecificationAdapter = new BaseAdapter<AbsAdapterItem>();
		selectWmenSpecification.setAdapter(selectWmenSpecificationAdapter);
		selectWmenSpecificationAdapter.clear();

		id = ResourceUtil.getId(getActivity(), "product_num");
		CoupleNum = (TextView)boss_couple_product_specification.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "men_product_num");
		productMenNumber = (TextView)boss_couple_product_specification.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "wmen_product_num");
		productWmenNumber = (TextView)boss_couple_product_specification.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "men_specification_v");
		men_specification_v = boss_couple_product_specification.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "wmen_specification_v");
		wmen_specification_v = boss_couple_product_specification.findViewById(id);

		id = ResourceUtil.getId(getContext(), "menubar");
		MenuBar mMenuBar = (MenuBar)boss_couple_product_specification.findViewById(id);
		mMenuBar.setOnMenuListener(new OnMenuListener()
		{
			@Override
			public void onMenuUnSelected(MenuBar menuBar, MenuView menuView, int menuIndex)
			{

			}

			@Override
			public void onMenuSelected(MenuBar menuBar, MenuView menuView, int menuIndex)
			{
				LogUtil.d(TAG, "onMenuSelected: " + menuIndex);
				setCurrentFragmentImpl(menuIndex);
			}
		});
		mMenuBar.setCurrentMenu(0);
	}

	public void setCurrentFragmentImpl(int index)
	{
		if (index == 0)
		{
			men_specification_v.setVisibility(View.VISIBLE);
			wmen_specification_v.setVisibility(View.GONE);
		}
		else
		{
			men_specification_v.setVisibility(View.GONE);
			wmen_specification_v.setVisibility(View.VISIBLE);
		}
	}

	private void setSelectdDot(final int index)
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (isDetached() || getContext() == null)
					return;
				LogUtil.d(TAG, "setSelectdDot: " + index);
				int dotFocusId = ResourceUtil.getDrawableId(getContext(), "dot_container_bg");
				int dotUnfocusId = ResourceUtil.getDrawableId(getContext(), "dot_container_bg1");
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

	private void ensureDots()
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				if (isDetached() || getContext() == null)
					return;
				mDotContainer.removeAllViews();
				int count = pages;
				for (int i = 0; i < count; i++)
				{
					if (isDetached() || getContext() == null)
						return;
					ImageView view = new ImageView(getContext());
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.leftMargin = AndroidUtils.dp2px(getContext(), 5);
					params.rightMargin = AndroidUtils.dp2px(getContext(), 5);
					view.setLayoutParams(params);
					int dotUnfocusId = ResourceUtil.getDrawableId(getContext(), "dot_container_bg1");
					view.setImageResource(dotUnfocusId);
					view.setScaleType(ScaleType.CENTER);
					mDotContainer.addView(view);
				}
				setSelectdDot(0);
			}
		});

	}

	public void ProductDetailsData()
	{
		showLoadingIndicator();
		if (!isCoupleRing)
		{
			RequestManager.loadGoodsDetail(getContext(), LoginHelper.getUserKey(), goodsId, new RequestCallback()
			{
				@Override
				public void onRequestResult(int requestCode, long taskId, BaseResponse response, DataFrom from)
				{
					hideLoadingIndicator();
					if (response.resultCode == BaseResponse.RESULT_OK)
					{
//						showLoadingIndicator();
						mGDResponse = (GoodsDetailResponse)response;
						if (mGDResponse != null)
						{
							BrowseHistoryDBHelper.getInstance().put(getContext(), mGDResponse.datas);
							AndroidUtils.MainHandler.post(new Runnable()
							{
								@Override
								public void run()
								{
									GoodsId = mGDResponse.datas.id;
									title.setText(mGDResponse.datas.title);
									details_name.setText(mGDResponse.datas.title);
									productNumber.setText(mGDResponse.datas.sku);

									if (mGDResponse.datas.thumb_url != null)
									{
										selectPhoto(mGDResponse.datas.thumb_url);
										DownloadPic service = new DownloadPic(getContext(),
								                mGDResponse.datas.thumb_url,
								                new ImageDownLoadCallBack() {
													
													@Override
													public void onDownLoadSuccess(final ArrayList<File> files) {
														hideLoadingIndicator();
														// TODO Auto-generated method stub
														share_icon.setOnClickListener(new OnClickListener() {
															
															@Override
															public void onClick(View v) {
																// TODO Auto-generated method stub
																
																ArrayList<Uri> imageUris = new ArrayList<Uri>();
																for (File file : files){
																	Uri uri = Uri.fromFile(file);
																	imageUris.add(uri);
																}
																
																ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
														        // 将文本内容放到系统剪贴板里。
														        cm.setText(mGDResponse.datas.title + " 款号" + mGDResponse.datas.sku);
														      
														        Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
														        mulIntent.putExtra(Intent.EXTRA_STREAM, imageUris);
														        mulIntent.setType("image/*");
														        startActivity(Intent.createChooser(mulIntent,"多文件分享"));
															}
														});
													}
													
													@Override
													public void onDownLoadFailed() {
														// TODO Auto-generated method stub
														
													}
												});
								        //启动图片下载线程
								        runOnQueue(service);
										
									}

									if (mGDResponse.datas.model_info == null)
									{
										boss_add_3d.setVisibility(View.GONE);
										boss_add_shopping
												.setImageResource(R.drawable.boss_product_specification_addshopping2);
									}

									sSpeci();
								}
							});
						}
					}
					else
					{
						showToast(response.error);
					}
				}

				@Override
				public void onRequestError(int requestCode, long taskId, ErrorInfo error)
				{
					hideLoadingIndicator();
					showToast("网络错误: " + error.errorCode);
				}
			});
		}
		else
		{
			RequestManager.loadCoupleRingDetail(getContext(), LoginHelper.getUserKey(), goodsId, new RequestCallback()
			{
				@Override
				public void onRequestResult(int requestCode, long taskId, BaseResponse response, DataFrom from)
				{
					hideLoadingIndicator();
					if (response.resultCode == BaseResponse.RESULT_OK)
					{
						mCoupleRingDetailResponse = (CoupleRingDetailResponse)response;
						if (mCoupleRingDetailResponse != null)
						{
							AndroidUtils.MainHandler.post(new Runnable()
							{

								@Override
								public void run()
								{
									GoodsId = mCoupleRingDetailResponse.datas.id;
									title.setText(mCoupleRingDetailResponse.datas.title);
									details_name.setText(mCoupleRingDetailResponse.datas.title);
									productMenNumber.setText("款号" + mCoupleRingDetailResponse.datas.m_sku);
									productWmenNumber.setText("款号" + mCoupleRingDetailResponse.datas.w_sku);

									coupleSpeci();

									if (mCoupleRingDetailResponse.datas.thumb_url != null)
									{
										selectPhoto(mCoupleRingDetailResponse.datas.thumb_url);
										DownloadPic service = new DownloadPic(getContext(),
												mCoupleRingDetailResponse.datas.thumb_url,
								                new ImageDownLoadCallBack() {
													
													@Override
													public void onDownLoadSuccess(final ArrayList<File> files) {
														// TODO Auto-generated method stub
														hideLoadingIndicator();
														share_icon.setOnClickListener(new OnClickListener() {
															
															@Override
															public void onClick(View v) {
																// TODO Auto-generated method stub															
																ArrayList<Uri> imageUris = new ArrayList<Uri>();
																for (File file : files){
																	Uri uri = Uri.fromFile(file);
																	imageUris.add(uri);
																}
																
																ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
														        // 将文本内容放到系统剪贴板里。
														        cm.setText(mCoupleRingDetailResponse.datas.title + "\n款号:女:" 
														        + mCoupleRingDetailResponse.datas.w_sku + "男:" 
														        		+ mCoupleRingDetailResponse.datas.m_sku);
														      
														        Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
														        mulIntent.putExtra(Intent.EXTRA_STREAM, imageUris);
														        mulIntent.setType("image/*");
														        startActivity(Intent.createChooser(mulIntent,"多文件分享"));
															}
														});
													}
													
													@Override
													public void onDownLoadFailed() {
														// TODO Auto-generated method stub
														
													}
												});
								        //启动图片下载线程
								        runOnQueue(service);
									}

									if (mCoupleRingDetailResponse.datas.model_infos == null)
									{
										boss_add_3d.setVisibility(View.GONE);
										boss_add_shopping
												.setImageResource(R.drawable.boss_product_specification_addshopping2);
									}
								}
							});
						}
					}
					else
					{
						showToast(response.error);
					}
				}

				@Override
				public void onRequestError(int requestCode, long taskId, ErrorInfo error)
				{
					hideLoadingIndicator();
					showToast("网络错误: " + error.errorCode);
				}
			});
		}
	}

	private boolean checkNeedAdd(ErpGoods erp)
	{
		boolean add = true;
		if (add && priceRange != null && priceRange.length > 1)
		{
			if (!TextUtils.isEmpty(erp.p5))
			{
				int price = 0;
				try {
					erp.p5 = erp.p5.substring(0, erp.p5.indexOf('.'));
					price = Integer.parseInt(erp.p5);
				} catch (NumberFormatException e) {
					// 写入异常日志
				}

				if (price < priceRange[0] || price > priceRange[1])
					add = false;
			}
			else
				add = false;
		}
		if (add && weightRange != null && weightRange.length > 1)
		{
			if (!TextUtils.isEmpty(erp.p7))
			{
				if (erp.p7.endsWith("ct"))
				{
					int end = erp.p7.indexOf("ct");
					erp.p7 = erp.p7.substring(0, end);
				}
				float weight = Float.parseFloat(erp.p7);
				if (weight < weightRange[0] || weight > weightRange[1])
					add = false;
			}
			else
				add = false;
		}
		if (add && !TextUtils.isEmpty(colorCondition))
		{
			if (!TextUtils.isEmpty(erp.p3))
			{
				if (!erp.p3.equals(colorCondition))
					add = false;
			}
			else
				add = false;
		}
		if (add && !TextUtils.isEmpty(clarityCondition))
		{
			if (!TextUtils.isEmpty(erp.p2))
			{
				if (!erp.p2.equalsIgnoreCase(clarityCondition))
					add = false;
			}
			else
				add = false;
		}
		return add;
	}

	List<AbsAdapterItem> mlist;

	public void sSpeci()
	{
		mlist = new ArrayList<AbsAdapterItem>();
		if (mGDResponse.datas != null && mGDResponse.datas.erp != null && mGDResponse.datas.erp.length > 0)
		{
			List<ErpGoods> list = new ArrayList<ErpGoods>();
			for (ErpGoods erp : mGDResponse.datas.erp)
			{
				if (checkNeedAdd(erp))
					list.add(erp);
			}

			if (!list.isEmpty())
			{
				ErpGoods[] erps = new ErpGoods[list.size()];
				erps = list.toArray(erps);

				// details_on_price.setText(erps[0].p5);
				// details_up_price.setText(erps[0].p5);

				for (ErpGoods erp : erps)
				{
					SpeciationFragment mSpeciationFragment = new SpeciationFragment(erp, false);
					selectSpecificationAdapter.addItem(mSpeciationFragment);
					mlist.add(mSpeciationFragment);
				}
			}
			else
				showToast("没有产品");
		}
		else
		{
			showToast("没有产品");
		}
	}

	public class SpeciationFragment extends AbsAdapterItem
	{
		private ErpGoods mErp;
		public View delectprice;
		// 手寸
		private TextView productHand;
		private List<String> handList = new ArrayList<String>();

		public boolean selected;
		public String erpId;

		public SpeciationFragment(ErpGoods mErp, boolean flag)
		{
			this.mErp = mErp;
			this.selected = flag;
			erpId = mErp.erpid;
		}

		@Override
		public View onCreateView(int position, ViewGroup parent)
		{
			int layout = ResourceUtil.getLayoutId(getActivity(), "boss_product_specification_selectprice");
			View view = View.inflate(getActivity(), layout, null);

			int id = ResourceUtil.getId(getActivity(), "delectprice");
			delectprice = view.findViewById(id);

			id = ResourceUtil.getId(getActivity(), "product_main_stone_num");
			productHand = (TextView)view.findViewById(id);
			productHand.setText(mErp.p128);
			for (int i = 7; i < 26; i++)
			{
				handList.add(i + "");
			}
			mSpinerPopWindowHand = new SpinnerWindow(getContext(), handList);
			// mSpinerPopWindowHand.refreshData(handList, 0);
			// setMaterialHero(0, handList, productHand);
			if (mGDResponse.datas.tagname != null && mGDResponse.datas.tagname.contains(Define.TAGNAME_PENDANT))
			{

			}
			else
				productHand.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						showSpinWindow(mSpinerPopWindowHand, productHand);
					}
				});
			mSpinerPopWindowHand.setOnItemClickListener(new SpinnerWindow.OnItemClickListener()
			{
				@Override
				public void onItemClick(int position)
				{
					setMaterialHero(position, handList, productHand);
				}
			});

			return view;
		}

		@Override
		public void onUpdateView(View view, int position, ViewGroup parent)
		{
			int id = ResourceUtil.getId(getContext(), "product_main_stone");
			TextView product_main_stone = (TextView)view.findViewById(id);
			if (mErp.erpid != null)
			{
				product_main_stone.setText(mErp.erpid);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_jingdu");
			TextView product_main_stone_jingdu = (TextView)view.findViewById(id);
			if (mErp.p1 != null)
			{
				product_main_stone_jingdu.setText(mErp.p1);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_color");
			TextView product_main_stone_color = (TextView)view.findViewById(id);
			if (mErp.p4 != null)
			{
				product_main_stone_color.setText(mErp.p4);
			}

			id = ResourceUtil.getId(getContext(), "product_fu_stone_num");
			TextView product_fu_stone_num = (TextView)view.findViewById(id);
			if (mErp.p10 != null)
			{
				product_fu_stone_num.setText(mErp.p10);
			}

			id = ResourceUtil.getId(getContext(), "product_fu_stone_color");
			TextView product_fu_stone_color = (TextView)view.findViewById(id);
			if (mErp.p3 != null)
			{
				product_fu_stone_color.setText(mErp.p3);
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_num");
			TextView product_zhu_stone_num = (TextView)view.findViewById(id);
			if (mErp.p7 != null)
			{
				product_zhu_stone_num.setText(mErp.p7 + "ct/1");
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_xz");
			TextView product_zhu_stone_xz = (TextView)view.findViewById(id);
			if (mErp.p3 != null)
			{
				product_zhu_stone_xz.setText(mErp.p3);
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_jd");
			TextView product_zhu_stone_jd = (TextView)view.findViewById(id);
			if (mErp.p2 != null)
			{
				product_zhu_stone_jd.setText(mErp.p2);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_price");
			TextView product_main_stone_price = (TextView)view.findViewById(id);
			if (mErp.p5 != null)
			{
				product_main_stone_price.setText("￥" + mErp.p5);
			}
		}

		@Override
		public void onLoadViewResource(View view, int position, ViewGroup parent)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecycleViewResource(View view, int position, ViewGroup parent)
		{
			// TODO Auto-generated method stub

		}

		public void onItemClick(View adapterView, ViewGroup parent, View view, int position, long layid)
		{
			int num = 0;
			selected = !selected;
			for (int i = 0; i < mlist.size(); i++)
			{
				if (((SpeciationFragment)mlist.get(i)).selected)
				{
					((SpeciationFragment)mlist.get(i)).delectprice.setVisibility(View.VISIBLE);
					num++;
				}
				else
				{
					((SpeciationFragment)mlist.get(i)).delectprice.setVisibility(View.GONE);
				}

			}
			erpnum = num;
			productNum.setText("" + num);
		}

		private SpinnerWindow mSpinerPopWindowHand;

		private void showSpinWindow(SpinnerWindow mSpinerPopWindow, TextView mProduct)
		{
			Log.e("", "showSpinWindow" + mProduct.getText());
			// mSpinerPopWindow.setWidth(mProduct.getWidth());
			
//			mSpinerPopWindow.showAsDropDown(mProduct);         //lhy 2017.12.5 适配下拉菜单为上拉或者下拉
			showCustomSpinWindow(mSpinerPopWindow, mProduct);
		}

		private void setMaterialHero(int pos, List<String> mList, TextView mProduct)
		{
			if (pos >= 0 && pos <= mList.size())
			{
				String value = mList.get(pos);
				mProduct.setText(value);
			}
		}

	}
	
	private void showCustomSpinWindow(SpinnerWindow mSpinerPopWindow, TextView mProduct){
		//获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
		mProduct.getLocationInWindow(location);
		mSpinerPopWindow.setWidth(mProduct.getWidth());
		View view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_window_layout, null);
		int windowHeight = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
		if (location[1]<windowHeight/2){
			mSpinerPopWindow.showAtLocation(mProduct, Gravity.NO_GRAVITY, location[0], location[1]+mProduct.getHeight());
		}else {
			android.widget.ListView mSpinerListView = mSpinerPopWindow.getListView();
			mSpinerPopWindow.showAtLocation(mProduct, Gravity.NO_GRAVITY, location[0], location[1]-dip2px(getContext(),280));
		}
	}
	
	/** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    private int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale+0.5f);
    }  

	List<AbsAdapterItem> menlist;
	List<AbsAdapterItem> wmenlist;

	public void coupleSpeci()
	{
		menlist = new ArrayList<AbsAdapterItem>();
		wmenlist = new ArrayList<AbsAdapterItem>();
		if (mCoupleRingDetailResponse.datas != null && mCoupleRingDetailResponse.datas.erp != null)
		{
			if (mCoupleRingDetailResponse.datas.erp.men != null && mCoupleRingDetailResponse.datas.erp.men.length > 0)
			{
				List<ErpGoods> list = new ArrayList<ErpGoods>();
				for (ErpGoods erp : mCoupleRingDetailResponse.datas.erp.men)
				{
					if (checkNeedAdd(erp))
						list.add(erp);
				}

				if (!list.isEmpty())
				{
					ErpGoods[] erps = new ErpGoods[list.size()];
					erps = list.toArray(erps);

					// details_on_price.setText(erps[0].p5);
					// details_up_price.setText(erps[0].p5);
					for (ErpGoods erp : erps)
					{
						SpeciationMenFragment mSpeciationMenFragment = new SpeciationMenFragment(erp, false);
						selectMenSpecificationAdapter.addItem(mSpeciationMenFragment);
						menlist.add(mSpeciationMenFragment);
					}
				}
				else
					showToast("没有男戒产品");
			}
			else
			{
				showToast("没有男戒产品");
			}
			if (mCoupleRingDetailResponse.datas.erp.wmen != null && mCoupleRingDetailResponse.datas.erp.wmen.length > 0)
			{
				List<ErpGoods> list = new ArrayList<ErpGoods>();
				for (ErpGoods erp : mCoupleRingDetailResponse.datas.erp.wmen)
				{
					if (checkNeedAdd(erp))
						list.add(erp);
				}

				if (!list.isEmpty())
				{
					ErpGoods[] erps = new ErpGoods[list.size()];
					erps = list.toArray(erps);
					for (ErpGoods erp : erps)
					{
						SpeciationWmenFragment mSpeciationWmenFragment = new SpeciationWmenFragment(erp, false);
						selectWmenSpecificationAdapter.addItem(mSpeciationWmenFragment);
						wmenlist.add(mSpeciationWmenFragment);
					}
				}
				else
					showToast("没有女戒产品");
			}
			else
			{
				showToast("没有女戒产品");
			}
		}
		else
		{
			showToast("没有产品");
		}
	}

	int selectmennum = 0;
	int selectwmennum = 0;

	public class SpeciationMenFragment extends AbsAdapterItem
	{
		private ErpGoods mErp;
		public View delectprice;
		// 手寸
		private TextView productHand;
		private List<String> handList = new ArrayList<String>();
		public boolean selected;
		public String erpId;

		public SpeciationMenFragment(ErpGoods mErp, boolean flag)
		{
			this.mErp = mErp;
			this.selected = flag;
			erpId = mErp.erpid;
		}

		@Override
		public View onCreateView(int position, ViewGroup parent)
		{
			int layout = ResourceUtil.getLayoutId(getActivity(), "boss_product_specification_selectprice");
			View view = View.inflate(getActivity(), layout, null);

			int id = ResourceUtil.getId(getActivity(), "delectprice");
			delectprice = view.findViewById(id);

			id = ResourceUtil.getId(getActivity(), "product_main_stone_num");
			productHand = (TextView)view.findViewById(id);
			productHand.setText(mErp.p128);
			for (int i = 7; i < 26; i++)
			{
				handList.add(i + "");
			}
			mSpinerPopWindowHand = new SpinnerWindow(getContext(), handList);        
			// mSpinerPopWindowHand.refreshData(handList, 0);
			// setMaterialHero(0, handList, productHand);
			productHand.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					showSpinWindow(mSpinerPopWindowHand, productHand);
				}
			});
			mSpinerPopWindowHand.setOnItemClickListener(new SpinnerWindow.OnItemClickListener()
			{

				@Override
				public void onItemClick(int position)
				{
					setMaterialHero(position, handList, productHand);
				}
			});
			return view;
		}

		@Override
		public void onUpdateView(View view, int position, ViewGroup parent)
		{

			int id = ResourceUtil.getId(getContext(), "product_main_stone");
			TextView product_main_stone = (TextView)view.findViewById(id);
			if (mErp.erpid != null)
			{
				product_main_stone.setText(mErp.erpid);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_jingdu");
			TextView product_main_stone_jingdu = (TextView)view.findViewById(id);
			if (mErp.p1 != null)
			{
				product_main_stone_jingdu.setText(mErp.p1);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_color");
			TextView product_main_stone_color = (TextView)view.findViewById(id);
			if (mErp.p4 != null)
			{
				product_main_stone_color.setText(mErp.p4);
			}

			id = ResourceUtil.getId(getContext(), "product_fu_stone_num");
			TextView product_fu_stone_num = (TextView)view.findViewById(id);
			if (mErp.p10 != null)
			{
				product_fu_stone_num.setText(mErp.p10);
			}

			id = ResourceUtil.getId(getContext(), "product_fu_stone_color");
			TextView product_fu_stone_color = (TextView)view.findViewById(id);
			if (mErp.p3 != null)
			{
				product_fu_stone_color.setText(mErp.p3);
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_num");
			TextView product_zhu_stone_num = (TextView)view.findViewById(id);
			if (mErp.p7 != null)
			{
				product_zhu_stone_num.setText(mErp.p7 + "ct/1");
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_xz");
			TextView product_zhu_stone_xz = (TextView)view.findViewById(id);
			if (mErp.p3 != null)
			{
				product_zhu_stone_xz.setText(mErp.p3);
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_jd");
			TextView product_zhu_stone_jd = (TextView)view.findViewById(id);
			if (mErp.p2 != null)
			{
				product_zhu_stone_jd.setText(mErp.p2);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_price");
			TextView product_main_stone_price = (TextView)view.findViewById(id);
			if (mErp.p5 != null)
			{
				product_main_stone_price.setText("￥" + mErp.p5);
			}

		}

		@Override
		public void onLoadViewResource(View view, int position, ViewGroup parent)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecycleViewResource(View view, int position, ViewGroup parent)
		{
			// TODO Auto-generated method stub

		}

		public void onItemClick(View adapterView, ViewGroup parent, View view, int position, long layid)
		{
			int num = 0;
			selected = !selected;
			for (int i = 0; i < menlist.size(); i++)
			{
				if (((SpeciationMenFragment)menlist.get(i)).selected)
				{
					((SpeciationMenFragment)menlist.get(i)).delectprice.setVisibility(View.VISIBLE);
					num++;
				}
				else
				{
					((SpeciationMenFragment)menlist.get(i)).delectprice.setVisibility(View.GONE);
				}

			}
			selectmennum = num;

			CoupleNum.setText("" + (selectmennum + selectwmennum));

		}

		private SpinnerWindow mSpinerPopWindowHand;

		private void showSpinWindow(SpinnerWindow mSpinerPopWindow, TextView mProduct)
		{
			Log.e("", "showSpinWindow" + mProduct.getText());
			// mSpinerPopWindow.setWidth(mProduct.getWidth());
//			mSpinerPopWindow.showAsDropDown(mProduct);             //改成自定义上拉或者下拉SpinerWindow
			showCustomSpinWindow(mSpinerPopWindow, mProduct);
		}

		private void setMaterialHero(int pos, List<String> mList, TextView mProduct)
		{
			if (pos >= 0 && pos <= mList.size())
			{
				String value = mList.get(pos);
				mProduct.setText(value);
			}
		}

	}
	

	public class SpeciationWmenFragment extends AbsAdapterItem
	{

		private ErpGoods mErp;
		public View delectprice;
		// 手寸
		private TextView productHand;
		private List<String> handList = new ArrayList<String>();
		public boolean selected;
		public String erpId;

		public SpeciationWmenFragment(ErpGoods mErp, boolean flag)
		{
			this.mErp = mErp;
			this.selected = flag;
			erpId = mErp.erpid;
		}

		@Override
		public View onCreateView(int position, ViewGroup parent)
		{
			int layout = ResourceUtil.getLayoutId(getActivity(), "boss_product_specification_selectprice");
			View view = View.inflate(getActivity(), layout, null);

			int id = ResourceUtil.getId(getActivity(), "delectprice");
			delectprice = view.findViewById(id);

			id = ResourceUtil.getId(getActivity(), "product_main_stone_num");
			productHand = (TextView)view.findViewById(id);
			productHand.setText(mErp.p128);
			for (int i = 7; i < 25; i++)
			{
				handList.add(i + "");
			}
			mSpinerPopWindowHand = new SpinnerWindow(getContext(), handList);
			// mSpinerPopWindowHand.refreshData(handList, 0);
			// setMaterialHero(0, handList, productHand);
			productHand.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					showSpinWindow(mSpinerPopWindowHand, productHand);
				}
			});
			mSpinerPopWindowHand.setOnItemClickListener(new SpinnerWindow.OnItemClickListener()
			{
				@Override
				public void onItemClick(int position)
				{
					setMaterialHero(position, handList, productHand);
				}
			});

			return view;
		}

		@Override
		public void onUpdateView(View view, int position, ViewGroup parent)
		{

			int id = ResourceUtil.getId(getContext(), "product_main_stone");
			TextView product_main_stone = (TextView)view.findViewById(id);
			if (mErp.erpid != null)
			{
				product_main_stone.setText(mErp.erpid);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_jingdu");
			TextView product_main_stone_jingdu = (TextView)view.findViewById(id);
			if (mErp.p1 != null)
			{
				product_main_stone_jingdu.setText(mErp.p1);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_color");
			TextView product_main_stone_color = (TextView)view.findViewById(id);
			if (mErp.p4 != null)
			{
				product_main_stone_color.setText(mErp.p4);
			}

			id = ResourceUtil.getId(getContext(), "product_fu_stone_num");
			TextView product_fu_stone_num = (TextView)view.findViewById(id);
			if (mErp.p10 != null)
			{
				product_fu_stone_num.setText(mErp.p10);
			}

			id = ResourceUtil.getId(getContext(), "product_fu_stone_color");
			TextView product_fu_stone_color = (TextView)view.findViewById(id);
			if (mErp.p3 != null)
			{
				product_fu_stone_color.setText(mErp.p3);
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_num");
			TextView product_zhu_stone_num = (TextView)view.findViewById(id);
			if (mErp.p7 != null)
			{
				product_zhu_stone_num.setText(mErp.p7 + "ct/1");
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_xz");
			TextView product_zhu_stone_xz = (TextView)view.findViewById(id);
			if (mErp.p3 != null)
			{
				product_zhu_stone_xz.setText(mErp.p3);
			}

			id = ResourceUtil.getId(getContext(), "product_zhu_stone_jd");
			TextView product_zhu_stone_jd = (TextView)view.findViewById(id);
			if (mErp.p2 != null)
			{
				product_zhu_stone_jd.setText(mErp.p2);
			}

			id = ResourceUtil.getId(getContext(), "product_main_stone_price");
			TextView product_main_stone_price = (TextView)view.findViewById(id);
			if (mErp.p5 != null)
			{
				product_main_stone_price.setText("￥" + mErp.p5);
			}

		}

		@Override
		public void onLoadViewResource(View view, int position, ViewGroup parent)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecycleViewResource(View view, int position, ViewGroup parent)
		{
			// TODO Auto-generated method stub

		}

		public void onItemClick(View adapterView, ViewGroup parent, View view, int position, long layid)
		{
			int num = 0;
			selected = !selected;
			for (int i = 0; i < wmenlist.size(); i++)
			{
				if (((SpeciationWmenFragment)wmenlist.get(i)).selected)
				{
					((SpeciationWmenFragment)wmenlist.get(i)).delectprice.setVisibility(View.VISIBLE);
					num++;
				}
				else
				{
					((SpeciationWmenFragment)wmenlist.get(i)).delectprice.setVisibility(View.GONE);
				}

			}
			selectwmennum = num;

			CoupleNum.setText("" + (selectmennum + selectwmennum));

		}

		private SpinnerWindow mSpinerPopWindowHand;

		private void showSpinWindow(SpinnerWindow mSpinerPopWindow, TextView mProduct)
		{
			Log.e("", "showSpinWindow" + mProduct.getText());
			mSpinerPopWindow.setWidth(mProduct.getWidth());
//			mSpinerPopWindow.showAsDropDown(mProduct);       改成自定义上拉或者下拉SpinerWindow
			showCustomSpinWindow(mSpinerPopWindow, mProduct);
		}

		private void setMaterialHero(int pos, List<String> mList, TextView mProduct)
		{
			if (pos >= 0 && pos <= mList.size())
			{
				String value = mList.get(pos);
				mProduct.setText(value);
			}
		}

	}

	String GoodsId = null;
	String erpid[];
	int size[];
	int erpnum = 0;

	public void AddShoppingCart()
	{
		if (erpnum == 0)
		{
			showToast("请选择产品");
		}
		else
		{
			int index = 0;
			erpid = new String[erpnum];
			size = new int[erpnum];
			for (int i = 0; i < mlist.size(); i++)
			{
				if (((SpeciationFragment)mlist.get(i)).delectprice.getVisibility() == View.VISIBLE)
				{
					erpid[index] = ((SpeciationFragment)mlist.get(i)).erpId;
					String sizenum = ((SpeciationFragment)mlist.get(i)).productHand.getText().toString();
					try
					{
						size[index] = Integer.parseInt(sizenum);
					}
					catch (NumberFormatException e)
					{
						e.printStackTrace();
						size[index] = 0;
					}
					index++;
				}
			}

			LogUtil.w(TAG, "id=" + GoodsId + ",erpid=" + erpid + ",size=" + size);
			showLoadingIndicator();
			RequestManager.appendShoppingcart(getActivity(), LoginHelper.getUserKey(), GoodsId, erpid, size,
					new RequestCallback()
					{

						@Override
						public void onRequestResult(int requestCode, long taskId, BaseResponse response, DataFrom from)
						{
							hideLoadingIndicator();
							if (response.resultCode == BaseResponse.RESULT_OK)
							{
								showToast("加入购物车成功！");
								showAddShoppingcartSuccessDialog();
//								if (MainFragment.instance != null)
//									MainFragment.instance.updateShoppingcartBadgeNum(shoppingcart_num);
							}
							else
							{
								showToast(response.error);
							}
						}

						@Override
						public void onRequestError(int requestCode, long taskId, ErrorInfo error)
						{
							hideLoadingIndicator();
							showToast("网络错误: " + error.errorCode);
						}
					});
		}

	}

	public void AddCoupleShoppingCart()
	{
		erpnum = selectmennum + selectwmennum;
		if (erpnum == 0)
		{
			showToast("请选择产品");
			return;
		}
		else
		{
			int index = 0;
			erpid = new String[erpnum];
			size = new int[erpnum];
			for (int i = 0; i < menlist.size(); i++)
			{
				if (((SpeciationMenFragment)menlist.get(i)).delectprice.getVisibility() == View.VISIBLE)
				{
					erpid[index] = ((SpeciationMenFragment)menlist.get(i)).erpId;
					String sizenum = ((SpeciationMenFragment)menlist.get(i)).productHand.getText().toString();
					if (sizenum.equals(""))
					{
						sizenum = ((SpeciationMenFragment)menlist.get(i)).productHand.getHint().toString();
					}
					size[index] = Integer.parseInt(sizenum);
					index++;
				}
			}
			for (int i = 0; i < wmenlist.size(); i++)
			{
				if (((SpeciationWmenFragment)wmenlist.get(i)).delectprice.getVisibility() == View.VISIBLE)
				{
					erpid[index] = ((SpeciationWmenFragment)wmenlist.get(i)).erpId;
					String sizenum = ((SpeciationWmenFragment)wmenlist.get(i)).productHand.getText().toString();
					if (sizenum.equals(""))
					{
						sizenum = ((SpeciationWmenFragment)wmenlist.get(i)).productHand.getHint().toString();
					}
					size[index] = Integer.parseInt(sizenum);
					index++;
				}
			}
		}

		LogUtil.w(TAG, "id=" + GoodsId + ",erpid=" + erpid + ",size=" + size);
		showLoadingIndicator();
		RequestManager.appendShoppingcart(getActivity(), LoginHelper.getUserKey(), GoodsId, erpid, size,
				new RequestCallback()
				{

					@Override
					public void onRequestResult(int requestCode, long taskId, BaseResponse response, DataFrom from)
					{
						hideLoadingIndicator();
						if (response.resultCode == BaseResponse.RESULT_OK)
						{
							showToast("加入购物车成功！");
							showAddShoppingcartSuccessDialog();
//							if (MainFragment.instance != null)
//								MainFragment.instance.updateShoppingcartBadgeNum(shoppingcart_num);
						}
						else
						{
							showToast(response.error);
						}
					}

					@Override
					public void onRequestError(int requestCode, long taskId, ErrorInfo error)
					{
						hideLoadingIndicator();
						showToast("网络错误: " + error.errorCode);
					}
				});
	}

	private void showAddShoppingcartSuccessDialog()
	{
		AndroidUtils.MainHandler.post(new Runnable()
		{
			@Override
			public void run()
			{
				LayoutInflater mLayoutInflater = (LayoutInflater)getContext().getSystemService("layout_inflater");
				int layout = ResourceUtil.getLayoutId(getContext(), "add_shoppingcart_success_dialog");
				View product_information = mLayoutInflater.inflate(layout, null);
				final PopupWindow pw = new PopupWindow(product_information, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);

				int id = ResourceUtil.getId(getContext(), "loke_shopping");
				View loke_shopping = product_information.findViewById(id);
				loke_shopping.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if (AndroidUtils.isFastClick())
							return;
						pw.dismiss();
						MainActivity.backToHomepage(getActivity(), 1);
					}
				});

				id = ResourceUtil.getId(getContext(), "loke");
				View loke = product_information.findViewById(id);
				loke.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						if (AndroidUtils.isFastClick())
							return;
						pw.dismiss();
						boss_add_shopping.setEnabled(true);
					}
				});

				product_information.setFocusable(true);
				product_information.setFocusableInTouchMode(true);
				pw.setBackgroundDrawable(new ColorDrawable(0x00));
				pw.setFocusable(true);
				pw.setTouchable(true);
				pw.setOutsideTouchable(true);
				pw.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
				pw.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
				pw.showAtLocation(mBossDetailsLayout, Gravity.CENTER, 0, 0);
			}
		});
	}

	public void selectPhoto(ImageInfo[] mImageInfo)
	{
		if (mImageInfo != null && mImageInfo.length > 0)
		{
			pages = mImageInfo.length;
			for (ImageInfo image : mImageInfo)
			{
				mPagerAdapter.addItem(new ImagePagerAdapterItem(image));
			}
			mPopupWindow = new ProductImagePopupWindow(this, getActivity(), mImageInfo);
			ensureDots();
		}
	}

	private class ImagePagerAdapterItem extends AbsAdapterItem
	{
		private ImageInfo mImageInfo;

		public ImagePagerAdapterItem(ImageInfo imageInfo)
		{
			mImageInfo = imageInfo;
		}

		@Override
		public View onCreateView(int position, ViewGroup parent)
		{
			ExtendImageView imageView = new ExtendImageView(getContext());
			ViewPager.LayoutParams params = new ViewPager.LayoutParams();
			imageView.setLayoutParams(params);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			// if (mImageInfo != null)
			// imageView.setImageDataSource(mImageInfo.url,
			// mImageInfo.filemtime, DecodeMode.NONE);
			// imageView.setAutoRecyleBitmap(true);
			return imageView;
		}

		@Override
		public void onUpdateView(View view, int position, ViewGroup parent)
		{

		}

		@Override
		public void onLoadViewResource(View view, final int position, ViewGroup parent)
		{
			ExtendImageView imageView = (ExtendImageView)view;
			if (mImageInfo != null)
				imageView.setImageDataSource(mImageInfo.url, mImageInfo.filemtime, DecodeMode.FIT_WIDTH);
			imageView.startImageLoad(false);
			imageView.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (AndroidUtils.isFastClick())
						return;
					if (mPopupWindow != null)
						mPopupWindow.show(position);
				}
			});
		}

		@Override
		public void onRecycleViewResource(View view, int position, ViewGroup parent)
		{
			ExtendImageView imageView = (ExtendImageView)view;
			imageView.recyleBitmapImage();
		}
	}
}
