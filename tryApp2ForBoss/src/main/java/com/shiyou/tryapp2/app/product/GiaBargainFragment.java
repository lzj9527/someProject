package com.shiyou.tryapp2.app.product;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
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
import android.extend.widget.adapter.ScrollListView;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.shiyou.tryapp2.Define;
import com.shiyou.tryapp2.GlideModelConfig;
import com.shiyou.tryapp2.RequestManager;
import com.shiyou.tryapp2.RequestManager.RequestCallback;
import com.shiyou.tryapp2.app.MainActivity;
import com.shiyou.tryapp2.app.MainFragment;
import com.shiyou.tryapp2.app.login.LoginHelper;
import com.shiyou.tryapp2.app.product.ProductDetailsFragment.SpeciationFragment;
import com.shiyou.tryapp2.boss.zsa.R;
import com.shiyou.tryapp2.data.ImageInfo;
import com.shiyou.tryapp2.data.db.BrowseHistoryDBHelper;
import com.shiyou.tryapp2.data.response.BaseResponse;
import com.shiyou.tryapp2.data.response.CoupleRingDetailResponse;
import com.shiyou.tryapp2.data.response.GiaBargainResponse;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse.ErpGoods;

public class GiaBargainFragment extends BaseFragment {

	private TextView style;
	private TextView weight;
	private TextView color;
	private TextView clarity;
	private TextView cut;
	private TextView polish;
	private TextView symmetry;
	private TextView coffee;
	private TextView milk;
	private TextView fluorescence;
	private TextView price;
	private TextView details_number;
	
	private ImageView select_photo;

	private ImageView boss_add_shopping;
	
	GiaBargainResponse gitBargainResponse;

	public static GiaBargainFragment instance = null;
	
	View view;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		if (instance == null){
			instance = this;
			showLoadingIndicator();
		}
		int layout = ResourceUtil.getLayoutId(getActivity(),
				"gia_bargain_layout");
		view = View.inflate(getActivity(), layout, null);

		style = (TextView) view.findViewById(R.id.style);
		weight = (TextView) view.findViewById(R.id.weight);
		color = (TextView) view.findViewById(R.id.color);
		clarity = (TextView) view.findViewById(R.id.clarity);
		cut = (TextView) view.findViewById(R.id.cut);
		polish = (TextView) view.findViewById(R.id.polish);
		symmetry = (TextView) view.findViewById(R.id.symmetry);
		coffee = (TextView) view.findViewById(R.id.coffee);
		milk = (TextView) view.findViewById(R.id.milk);
		fluorescence = (TextView) view.findViewById(R.id.fluorescence);
		price = (TextView) view.findViewById(R.id.price);
		details_number = (TextView) view.findViewById(R.id.details_number);
		
		select_photo = (ImageView) view.findViewById(R.id.select_photo);
		
		boss_add_shopping = (ImageView) view.findViewById(R.id.boss_add_shopping);
		
		
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
				// MainFragment.instance.onBackPressed();
			}
		});
		RequestManager.giaBargain(getActivity(), LoginHelper.getUserKey(),
				new RequestCallback() {

					@Override
					public void onRequestResult(int requestCode, long taskId,
							BaseResponse response, DataFrom from) {
						// TODO Auto-generated method stub
						hideLoadingIndicator();
						gitBargainResponse = (GiaBargainResponse) response;
						
						AndroidUtils.MainHandler.post(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									style.setText(gitBargainResponse.datas.style);
									weight.setText(gitBargainResponse.datas.weight);
									color.setText(gitBargainResponse.datas.color);
									clarity.setText(gitBargainResponse.datas.clarity);
									cut.setText(gitBargainResponse.datas.cut);
									polish.setText(gitBargainResponse.datas.polish);
									symmetry.setText(gitBargainResponse.datas.symmetry);
									coffee.setText(gitBargainResponse.datas.coffee);
									milk.setText(gitBargainResponse.datas.milk);
									fluorescence.setText(gitBargainResponse.datas.fluorescence);
									price.setText(gitBargainResponse.datas.price);
									details_number.setText(gitBargainResponse.datas.certno);
									if (gitBargainResponse.datas.status==0){
										boss_add_shopping.setImageResource(R.drawable.boss_product_specification_addshopping_no);
										boss_add_shopping.setEnabled(false);
									}
									Glide
									.with(getActivity())
									.load(gitBargainResponse.datas.image)
//									.load(R.drawable.ic_launcher)
									.into(select_photo);
								} catch (Exception e) {
									Log.i("1", e.toString());
								}
							}
							
						});

					}

					@Override
					public void onRequestError(int requestCode, long taskId,
							ErrorInfo error) {
						// TODO Auto-generated method stub
						hideLoadingIndicator();
						showToast("网络错误: " + error.errorCode);
					}
				});
		
		boss_add_shopping.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Toast.makeText(getContext(), Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID), Toast.LENGTH_LONG).show();
				AddShoppingCart();
			}
		});

		return view;
	}
	
	String GoodsId = null;
	String erpid[];
	int size[];
	int erpnum = 0;

	public void AddShoppingCart()
	{
			showLoadingIndicator();
			RequestManager.appendShoppingcart(getActivity(), LoginHelper.getUserKey(), gitBargainResponse,
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
				pw.showAtLocation(view, Gravity.CENTER, 0, 0);
			}
		});
	}

}
