package com.shiyou.tryapp2.app.product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.extend.ErrorInfo;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.util.AndroidUtils;
import android.extend.util.ResourceUtil;
import android.extend.widget.SpinnerPopupWindow;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shiyou.tryapp2.Config;
import com.shiyou.tryapp2.RequestManager;
import com.shiyou.tryapp2.RequestManager.RequestCallback;
import com.shiyou.tryapp2.app.MainActivity;
import com.shiyou.tryapp2.app.MainFragment;
import com.shiyou.tryapp2.app.WebViewFragment;
import com.shiyou.tryapp2.app.login.LoginHelper;
import com.shiyou.tryapp2.boss.zsa.R;
import com.shiyou.tryapp2.data.response.BaseResponse;
import com.shiyou.tryapp2.data.response.ShopListResponse;
import com.shiyou.tryapp2.data.response.ShopListResponse.ShopInfo;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("ValidFragment")
public class MainWebViewFragment extends WebViewFragment
{
	public static long lastClickTime;
	public static MainWebViewFragment instance = null;
	private String title;
	private int index;
	private boolean flag = false;
	public TextView boss_details_title;
//	private String uri;
	private  WebViewFragment webViewFragment;

	public MainWebViewFragment(String firstUrl, String title, int index)
	{
		super(firstUrl);
		instance = this;
		this.title = title;
		this.index = index;
	}
	public MainWebViewFragment(String firstUri,String title,int index,WebViewFragment webViewFragment){
		super(firstUri);
		instance = this;
		this.title = title;
		this.index = index;
//		this.uri=firstUri;
		this.webViewFragment=webViewFragment;
	}

	String realName = null;

	public MainWebViewFragment(String firstUrl, String title, int index, String realName)
	{
		super(firstUrl);
		instance = this;
		this.title = title;
		this.index = index;
		this.realName = realName;
	}

	public MainWebViewFragment(String firstUrl, String title, int index, boolean flag)
	{
		super(firstUrl);
		instance = this;
		this.title = title;
		this.index = index;
		this.flag = flag;
	}

	public MainWebViewFragment mWebInterfaceFragment;

	private View navigationbar;
	private View shopping_and_more;
	private TextView shoppingcart_num;
	private View boss_main_del;
	private View boss_main_seach;
	private View save_and_help;
	private View boss_details_back;
	private View boss_save_help;
	private View boss_main_sx;
	private View boss_main_add;
	private View store_address_layout;
	private View download_gia;
	private TextView store_address;
	private List<String> store_address_list = new ArrayList<String>();

	private boolean help_flag = false;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		mLayoutResID = ResourceUtil.getLayoutId(getActivity(), "main_webview_layout");
		final View view = super.onCreateView(inflater, container, savedInstanceState);

		int id = ResourceUtil.getId(getActivity(), "navigationbar");
		navigationbar = view.findViewById(id);

		// 返回
		id = ResourceUtil.getId(getActivity(), "boss_details_back");
		boss_details_back = view.findViewById(id);
		boss_details_back.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				// if (flag)
				if (onBackPressed())
					return;
				else
					getActivity().onBackPressed();
				// else
				// MainFragment.instance.onBackPressed();
			}
		});

		//下载gia
		id=ResourceUtil.getId(getActivity(),"boss_detail_download");
		download_gia=(Button)view.findViewById(id);
		download_gia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				if (!AndroidUtils.isFastClick()) {
					getWebView().loadUrl("javascript:window.local_obj.getGia('"+title+"',document.getElementById('downloadPDF').href)");
				} else {
					return;
				}
			}
		});

		// 标题
		id = ResourceUtil.getId(getActivity(), "boss_details_title");
		boss_details_title = (TextView)view.findViewById(id);
		boss_details_title.setText(title);
		boss_details_title.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				refresh();
			}
		});

		id = ResourceUtil.getId(getActivity(), "boss_main_sx");
		boss_main_sx = (TextView)view.findViewById(id);
		boss_main_sx.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				refresh();
			}
		});

		// 更多and购物车
		id = ResourceUtil.getId(getActivity(), "shopping_and_more");
		shopping_and_more = view.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "boss_details_shopping");
		View boss_details_shopping = view.findViewById(id);
		boss_details_shopping.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				MainActivity.backToHomepage(getActivity(), 1);
			}
		});

		id = ResourceUtil.getId(getContext(), "shoppingcart_num");
		shoppingcart_num = (TextView)view.findViewById(id);
		shoppingcart_num.setVisibility(View.GONE);

		// 搜索
		id = ResourceUtil.getId(getActivity(), "boss_main_seach");
		boss_main_seach = view.findViewById(id);
		boss_main_seach.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				// add(MainFragment.instance, MainFragment.instance.fragmentC1ID, new MainWebViewFragment(
				// Config.WebBossSearch, "搜索", 2, false), true);
				// MainFragment.instance.addFragmentToCurrent(
				// new MainWebViewFragment(Config.WebBossSearch, "搜索", 2, false), false);
				add(getActivity(), new MainWebViewFragment(Config.WebBossSearch, "搜索", 2, false), true);
			}
		});

		// 删除
		id = ResourceUtil.getId(getActivity(), "boss_main_del");
		boss_main_del = view.findViewById(id);
		boss_main_del.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AndroidUtils.MainHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						if (AndroidUtils.isFastClick())
							return;
						deleteShoppingCart();
					}
				});
			}
		});

		// 保存and帮助
		id = ResourceUtil.getId(getActivity(), "save_and_help");
		save_and_help = view.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "boss_pricre_help");
		View boss_pricre_help = view.findViewById(id);
		boss_pricre_help.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				help_flag = !help_flag;
				if (help_flag)
					boss_save_help.setVisibility(View.VISIBLE);
				else
					boss_save_help.setVisibility(View.GONE);
			}
		});

		id = ResourceUtil.getId(getActivity(), "boss_pricre_save");
		View boss_pricre_save = view.findViewById(id);
		boss_pricre_save.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				saveShopPrice();
			}
		});

		id = ResourceUtil.getId(getActivity(), "boss_save_help");
		boss_save_help = view.findViewById(id);

		id = ResourceUtil.getId(getContext(), "store_address_layout");
		store_address_layout = view.findViewById(id);

		id = ResourceUtil.getId(getActivity(), "store_address");
		store_address = (TextView)view.findViewById(id);
		if (realName != null)
		{
			store_address.setText(realName);
		}

		id = ResourceUtil.getId(getContext(), "boss_main_add");
		boss_main_add = view.findViewById(id);
		boss_main_add.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (AndroidUtils.isFastClick())
					return;
				applyForAddShop();
			}
		});

		HiddenDisplayTitle();
//		webView.setWebViewClient(new WebViewClient(){
//			@Override
//			public void onPageFinished(WebView view, String url) {
//				super.onPageFinished(view, url);
//				Log.d(TAG, "onPageFinished3:执行");
//				showError.setVisibility(View.GONE);
//			}
//		});

		return view;

	}

	@Override
	protected void onAddShoppingCartSucceed()
	{
		if (MainFragment.instance != null)
			MainFragment.instance.updateShoppingcartBadgeNum(shoppingcart_num);
	}

	private ShopListResponse mShopListResponse;

	public void ShopList()
	{
		RequestManager.getShopList(getActivity(), LoginHelper.getUserKey(), new RequestCallback()
		{
			@Override
			public void onRequestResult(int requestCode, long taskId, BaseResponse response, DataFrom from)
			{
				if (response.resultCode == BaseResponse.RESULT_OK)
				{
					mShopListResponse = (ShopListResponse)response;
					if (mShopListResponse != null && mShopListResponse.datas.list != null
							&& mShopListResponse.datas.list.length > 0)
					{
						store_address_list.clear();
						store_address_list.add("全部门店");
						for (ShopInfo list : mShopListResponse.datas.list)
						{
							store_address_list.add(list.nickname);
						}
						mSpinerPopWindowHand = new SpinnerWindow(getContext(), store_address_list);
						// mSpinerPopWindowHand.refreshData(store_address_list, 0);
						store_address.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v)
							{
								showSpinWindow(mSpinerPopWindowHand, store_address);
							}
						});
						mSpinerPopWindowHand.setOnItemClickListener(new SpinnerWindow.OnItemClickListener()
						{
							@Override
							public void onItemClick(int position)
							{
								setMaterialHero(position, store_address_list, store_address);
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
			}
		});
	}

	private SpinnerWindow mSpinerPopWindowHand;

	private void showSpinWindow(SpinnerPopupWindow mSpinerPopWindow, View anchor)
	{
		Log.e("", "showSpinWindow" + anchor);
		// mSpinerPopWindow.setWidth(anchor.getWidth());
//		mSpinerPopWindow.showAsDropDown(anchor);     //lhy 2017.12.5 适配下拉菜单为上拉或者下拉
		showCustomSpinWindow(mSpinerPopWindow, anchor);
	}
	
	private void showCustomSpinWindow(SpinnerPopupWindow mSpinerPopWindow, View mProduct){
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

	private void setMaterialHero(int pos, List<String> mList, TextView mProduct)
	{
		if (pos >= 0 && pos <= mList.size())
		{
			String value = mList.get(pos);
			mProduct.setText(value);
			String id = Chenk(value);
			if (id == null)
			{
				replace(MainFragment.instance, new MainWebViewFragment(Config.WebBossOrder, "订单列表", 7, value), false);
			}
			else
			{
				replace(MainFragment.instance, new MainWebViewFragment(Config.WebBossOrder + "?id=" + id, "订单列表", 7,
						value), false);
			}
		}
	}

	public String Chenk(String value)
	{
		String id = null;
		if (value.equals("全部门店"))
		{
			return null;
		}
		else
		{
			for (ShopInfo list : mShopListResponse.datas.list)
			{
				if (list.nickname.equals(value))
				{
					id = list.id;
					break;
				}
			}
		}
		return id;
	}

	public void HiddenDisplayTitle()
	{
		boss_save_help.setVisibility(View.GONE);
		store_address_layout.setVisibility(View.GONE);
		shopping_and_more.setVisibility(View.GONE);
		boss_main_del.setVisibility(View.GONE);
		boss_main_seach.setVisibility(View.GONE);
		save_and_help.setVisibility(View.GONE);
		boss_details_back.setVisibility(View.GONE);
		boss_main_sx.setVisibility(View.GONE);
		boss_main_add.setVisibility(View.GONE);
		download_gia.setVisibility(View.GONE);
		switch (index)
		{
			case 0:// 全部关闭
				navigationbar.setVisibility(View.GONE);
				break;
			case 1:// 搜索
				navigationbar.setVisibility(View.VISIBLE);
				// boss_main_seach.setVisibility(View.VISIBLE);
				boss_details_back.setVisibility(View.VISIBLE);
				break;
			case 2:// 右边关闭
				navigationbar.setVisibility(View.VISIBLE);
				boss_details_back.setVisibility(View.VISIBLE);
				break;
			case 3:// 购物车和更多
				boss_details_back.setVisibility(View.VISIBLE);
				navigationbar.setVisibility(View.VISIBLE);
				shopping_and_more.setVisibility(View.VISIBLE);
				MainFragment.updateShoppingcartBadgeNum(getActivity(), shoppingcart_num);
				break;
			case 4:// 购物车的删除
				navigationbar.setVisibility(View.VISIBLE);
				boss_main_del.setVisibility(View.VISIBLE);
				break;
			case 5:// 门店价格设置save_and_help
				navigationbar.setVisibility(View.VISIBLE);
				save_and_help.setVisibility(View.VISIBLE);
				boss_details_back.setVisibility(View.VISIBLE);
				break;
			case 6:// 只剩下标题
				navigationbar.setVisibility(View.VISIBLE);
				break;
			case 7:// 订单详情
				navigationbar.setVisibility(View.VISIBLE);
				store_address_layout.setVisibility(View.VISIBLE);
				ShopList();
				break;
			case 8:// 下载gia
				Log.d(TAG, "HiddenDisplayTitle: 执行");
				WebView webView=getWebView();
				webView.setBackgroundColor(0);
				WebSettings webSettings=webView.getSettings();
				webSettings.setJavaScriptEnabled(true);
				webView.addJavascriptInterface(new JavaScriptInterface(),"local_obj");
				navigationbar.setVisibility(View.VISIBLE);
				boss_details_back.setVisibility(View.VISIBLE);
				download_gia.setVisibility(View.VISIBLE);
				break;
			case 9:// 裸砖
				navigationbar.setVisibility(View.VISIBLE);
				boss_details_back.setVisibility(View.VISIBLE);
				boss_main_sx.setVisibility(View.VISIBLE);
				break;
			case 10:// 门店管理
				navigationbar.setVisibility(View.VISIBLE);
				boss_details_back.setVisibility(View.VISIBLE);
				boss_main_add.setVisibility(View.VISIBLE);
				break;

		}
	}
}


class WebAppBridge {

	private OauthLoginImpl oauthLogin;

	public WebAppBridge(OauthLoginImpl oauthLogin) {
		this.oauthLogin = oauthLogin;
	}


	@JavascriptInterface
	public void getResult(String str) {
		if (oauthLogin != null)
			oauthLogin.getResult(str);
	}

	public interface OauthLoginImpl {
		void getResult(String s);
	}
}