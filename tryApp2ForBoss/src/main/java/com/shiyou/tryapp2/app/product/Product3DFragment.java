package com.shiyou.tryapp2.app.product;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.extend.ErrorInfo;
import android.extend.app.BaseFragment;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.extend.util.ResourceUtil;
import android.extend.util.ViewTools;
import android.extend.widget.MenuBar;
import android.extend.widget.MenuBar.OnMenuListener;
import android.extend.widget.MenuView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.shiyou.tryapp2.Config;
import com.shiyou.tryapp2.Define;
import com.shiyou.tryapp2.FileDownloadHelper;
import com.shiyou.tryapp2.FileDownloadHelper.DownloadStatus;
import com.shiyou.tryapp2.FileDownloadHelper.OnMultiFileDownloadCallback;
import com.shiyou.tryapp2.app.MainActivity;
import com.shiyou.tryapp2.data.FileInfo;
import com.shiyou.tryapp2.data.UnityModelInfo;
import com.shiyou.tryapp2.data.response.CoupleRingDetailResponse;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse.GoodsDetail;
import com.unity3d.player.UnityPlayer;

@SuppressLint("ValidFragment")
public class Product3DFragment extends BaseFragment
{
	private GoodsDetailResponse mGDResponse = null;
	private CoupleRingDetailResponse mCoupleRingDetailResponse = null;

	private View view;
	private FrameLayout unity_container;
	private TextView title;
	private MenuBar product_details_3d_material;

	public static Product3DFragment instance = null;

	public Product3DFragment(GoodsDetailResponse mGDResponse)
	{
		this.mGDResponse = mGDResponse;
	}

	public Product3DFragment(CoupleRingDetailResponse mCoupleRingDetailResponse)
	{
		this.mCoupleRingDetailResponse = mCoupleRingDetailResponse;
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
		mLayoutResID = ResourceUtil.getLayoutId(getContext(), "boss_product_3d_layout");
		view = super.onCreateView(inflater, container, savedInstanceState);

		int id = ResourceUtil.getId(getActivity(), "boss_details_back");
		View back = view.findViewById(id);
		back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				onBackPressed();
				getActivity().onBackPressed();
			}
		});

		id = ResourceUtil.getId(getActivity(), "boss_details_title");
		title = (TextView)view.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "boss_details_shopping");
		View shopping = view.findViewById(id);
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

		id = ResourceUtil.getId(getContext(), "product_details_3d_material");
		product_details_3d_material = (MenuBar)view.findViewById(id);
		product_details_3d_material.setOnMenuListener(new OnMenuListener()
		{
			@Override
			public void onMenuUnSelected(MenuBar menuBar, MenuView menuView, int menuIndex)
			{
			}

			@Override
			public void onMenuSelected(MenuBar menuBar, MenuView menuView, int menuIndex)
			{
				switch (menuIndex)
				{
					case 0:
						UnityPlayer.UnitySendMessage("PlatformMessageHandler", "NotifyChangeAllModelMaterial",
								Define.MATERIAL_WHITE_KGOLD);
						break;
					case 1:
						UnityPlayer.UnitySendMessage("PlatformMessageHandler", "NotifyChangeAllModelMaterial",
								Define.MATERIAL_RED_KGOLD);
						break;
					case 2:
						UnityPlayer.UnitySendMessage("PlatformMessageHandler", "NotifyChangeAllModelMaterial",
								Define.MATERIAL_YELLOW_KGOLD);
						break;
				}
			}
		});
		product_details_3d_material.setCurrentMenu(0);

		ensureUnityPlayer();

		showLoadingIndicator();
		if (mGDResponse != null)
		{
			title.setText(mGDResponse.datas.title);
			loadStartFrom(mGDResponse.datas);
		}
		else if (mCoupleRingDetailResponse != null)
		{
			title.setText(mCoupleRingDetailResponse.datas.title);
			loadCoupleStartFrom(mCoupleRingDetailResponse.datas);
		}

		return view;
	}

	public void ensureUnityPlayer()
	{
		int id = ResourceUtil.getId(getContext(), "unity_container");
		unity_container = (FrameLayout)view.findViewById(id);

		if (!ViewTools.containsView(unity_container, MainActivity.instance.mUnityPlayer))
		{
			MainActivity.instance.detachUnityPlayer();
			unity_container.addView(MainActivity.instance.mUnityPlayer);
			MainActivity.instance.mUnityPlayer.setVisibility(View.VISIBLE);
		}
		Product3DFragment.launchTryon(getActivity());
	}

	public static void launchTryon(Activity activity)
	{
		UnityPlayer.UnitySendMessage("PlatformMessageHandler", "NotifyEnter3DShowScene", "");
	}

	@Override
	public void onDestroyView()
	{
		onBackPressed();
		super.onDestroyView();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		instance = null;
	}

	@Override
	public boolean onBackPressed()
	{
		// hideLoadingIndicator();
		UnityPlayer.UnitySendMessage("PlatformMessageHandler", "NotifyBackToWaiting", "");
		detachUnityPlayer();
		MainActivity.instance.attachUnityPlayer();
		return false;
	}

	public void attachUnityPlayer()
	{
		ensureUnityPlayer();
		if (mGDResponse != null)
		{
			showLoadingIndicator();
			loadStartFrom(mGDResponse.datas);
		}
		if (mCoupleRingDetailResponse != null)
		{
			showLoadingIndicator();
			loadCoupleStartFrom(mCoupleRingDetailResponse.datas);
		}

	}

	public void detachUnityPlayer()
	{
		if (ViewTools.containsView(unity_container, MainActivity.instance.mUnityPlayer))
			unity_container.removeView(MainActivity.instance.mUnityPlayer);
	}

	private void loadCoupleStartFrom(com.shiyou.tryapp2.data.response.CoupleRingDetailResponse.GoodsDetail datas)
	{
		if (datas.model_infos != null)
		{
			if (datas.model_infos.wmen == null && datas.model_infos.men == null)
			{
				showToast("没有模型");
				hideLoadingIndicator();
				return;
			}
			else
			{
				List<UnityModelInfo> models = new ArrayList<UnityModelInfo>();
				if (datas.model_infos.wmen != null)
				{
					UnityModelInfo wmenmodel = new UnityModelInfo(datas.model_infos.wmen);
					wmenmodel.id = datas.id;
					wmenmodel.type = Config.Type_CoupleRing_FeMale;
					wmenmodel.weight = 100;
					models.add(wmenmodel);
				}
				else
				{
					showToast("没有女戒模型");
				}
				if (datas.model_infos.men != null)
				{
					UnityModelInfo menmodel = new UnityModelInfo(datas.model_infos.men);
					menmodel.id = datas.id;
					menmodel.type = Config.Type_CoupleRing_Male;
					menmodel.weight = 100;
					models.add(menmodel);
				}
				else
				{
					showToast("没有男戒模型");
				}
				startModelDownload(models);
			}
		}
		else
		{
			showToast("没有模型");
			hideLoadingIndicator();
			return;
		}
	}

	private void loadStartFrom(GoodsDetail detail)
	{
		// FileDownloadHelper.startMultiDownload(null, getActivity(), new
		// FileInfo[] { mGDResponse.datas.model_info },
		// null, false);
		if (detail.model_info == null)
		{
			showToast("没有模型");
			hideLoadingIndicator();
			return;
		}
		UnityModelInfo model = new UnityModelInfo(detail.model_info);
		model.id = detail.id;
		if (detail.tagname != null && detail.tagname.contains(Define.TAGNAME_PENDANT))
			model.type = Config.Type_Necklace;
		else
			model.type = Config.Type_Ring;
		model.weight = 100;
		List<UnityModelInfo> models = new ArrayList<UnityModelInfo>();
		models.add(model);
		startModelDownload(models);

		// String modelJson = FileInfo.toJson(mGDResponse.datas.model_info);
		// LogUtil.w(TAG, "modelJson:"+modelJson);
		// UnityPlayer.UnitySendMessage("PlatformMessageHandler",
		// "NotifyAddModel", modelJson);
	}

	public ProductDetailsFragment onItemUnSelected(View adapterView, ViewGroup parent, View view, int position, long id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void startModelDownload(List<UnityModelInfo> models)
	{
		// hideLoadingIndicator();
		if (models == null || models.isEmpty())
		{
			hideLoadingIndicator();
			return;
		}
		List<FileInfo> list = new ArrayList<FileInfo>();
		for (UnityModelInfo model : models)
			list.add(model);
		FileInfo[] infos = new FileInfo[list.size()];
		infos = list.toArray(infos);
		FileDownloadHelper.startMultiDownload(models, getActivity(), infos, mModelFileDownloadCallback, true, true);
	}

	private OnMultiFileDownloadCallback mModelFileDownloadCallback = new OnMultiFileDownloadCallback()
	{
		public void onMultiDownloadStarted(Object tag, FileInfo fileInfo, String localPath, int downloadIndex)
		{
			hideLoadingIndicator();
		}

		public void onMultiDownloadProgress(Object tag, FileInfo fileInfo, String localPath, long count, long length,
				float speed, int downloadIndex)
		{
		}

		public void onMultiDownloadFinished(Object tag, FileInfo fileInfo, String localPath, int downloadIndex)
		{
		}

		public void onMultiDownloadCanceled(Object tag, FileInfo fileInfo, int downloadIndex)
		{
		}

		public void onMultiDownloadFailed(Object tag, FileInfo fileInfo, ErrorInfo error, int downloadIndex)
		{
			showToast("网络异常:" + error.errorCode);
		}

		public void onMultiAllDownloadFinished(Object tag, FileInfo[] fileInfos, DownloadStatus[] status)
		{
			// hideLoadingIndicatorDialog();
			boolean loadingIndicatorsShown = false;
			@SuppressWarnings("unchecked")
			List<UnityModelInfo> models = (List<UnityModelInfo>)tag;
			for (int i = 0; i < models.size(); i++)
			{
				if (status[i] == null || status[i] != DownloadStatus.FINISHED)
					continue;
				if (!loadingIndicatorsShown)
				{
					showLoadingIndicator(10 * 1000L);
					loadingIndicatorsShown = true;
				}
				UnityModelInfo model = models.get(i);
				String modelJson = UnityModelInfo.toJson(model);
				LogUtil.w(TAG, "modelJson:" + modelJson);
				UnityPlayer.UnitySendMessage("PlatformMessageHandler", "NotifyAddModel", modelJson);
			}
		}
	};
}
