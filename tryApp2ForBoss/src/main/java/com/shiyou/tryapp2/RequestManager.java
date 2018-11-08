package com.shiyou.tryapp2;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.cookie.BasicExpiresHandler;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.extend.ErrorInfo;
import android.extend.loader.BaseJsonParser;
import android.extend.loader.BaseParser.DataFrom;
import android.extend.loader.BasicHttpLoadParams;
import android.extend.loader.HttpFileUploadParams;
import android.extend.loader.HttpLoader.HttpLoadParams;
import android.extend.loader.Loader.CacheMode;
import android.extend.loader.Loader.LoadParams;
import android.extend.loader.UrlLoader;
import android.extend.util.LogUtil;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shiyou.tryapp2.data.response.BaseResponse;
import com.shiyou.tryapp2.data.response.CheckVersionResponse;
import com.shiyou.tryapp2.data.response.CoupleRingDetailResponse;
import com.shiyou.tryapp2.data.response.GiaBargainResponse;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse;
import com.shiyou.tryapp2.data.response.GoodsListResponse;
import com.shiyou.tryapp2.data.response.ShopListResponse;
import com.shiyou.tryapp2.data.response.ShoppingcartListResponse;
import com.shiyou.tryapp2.data.response.UploadFileResponse;

public class RequestManager
{
	public interface RequestCallback
	{
		void onRequestError(int requestCode, long taskId, ErrorInfo error);

		void onRequestResult(int requestCode, long taskId, BaseResponse response, DataFrom from);
	}

	/**
	 * 获取商品列表
	 * */
	public static long loadGoodsList(Context context, RequestCallback callback)
	{
		int requestCode = RequestCode.product_list;
		String url = Config.LoadGoodsListUrl;
		BasicHttpLoadParams params = new BasicHttpLoadParams(false);
		params.addRequestParam(new BasicNameValuePair("page", "1"));
		params.addRequestParam(new BasicNameValuePair("psize", "" + Integer.MAX_VALUE));

		return UrlLoader.getDefault().startLoad(context, url, params,
				new MyJsonParser<GoodsListResponse>(context, requestCode, callback, GoodsListResponse.class),
				CacheMode.PERFER_MEMORY_OR_NETWORK);
	}

	/**
	 * 获取商品详情
	 * */
	public static long loadGoodsDetail(Context context, String userKey, String goodsId, RequestCallback callback)
	{
		int requestCode = RequestCode.product_info;
		String url = Config.LoadGoodsDetailUrl;
		BasicHttpLoadParams params = new BasicHttpLoadParams(false);
		params.addRequestParam(new BasicNameValuePair("id", goodsId));
		params.addRequestParam(new BasicNameValuePair("key", userKey));

		return UrlLoader.getDefault().startLoad(context, url, params,
				new MyJsonParser<GoodsDetailResponse>(context, requestCode, callback, GoodsDetailResponse.class),
				CacheMode.PERFER_NETWORK);
	}

	/**
	 * 获取对戒详情
	 * */
	public static long loadCoupleRingDetail(Context context, String userKey, String goodsId, RequestCallback callback)
	{
		int requestCode = RequestCode.product_info;
		String url = Config.LoadGoodsDetailUrl;
		BasicHttpLoadParams params = new BasicHttpLoadParams(false);
		params.addRequestParam(new BasicNameValuePair("id", goodsId));
		params.addRequestParam(new BasicNameValuePair("key", userKey));

		return UrlLoader.getDefault().startLoad(
				context,
				url,
				params,
				new MyJsonParser<CoupleRingDetailResponse>(context, requestCode, callback,
						CoupleRingDetailResponse.class), CacheMode.PERFER_NETWORK);
	}

	/**
	 * 添加购物车
	 * 
	 * @param userKey 用户登录后key
	 * @param id 商品id
	 * @param erpid erpid
	 * @param size 戒指手寸
	 * */
	public static long appendShoppingcart(Context context, String userKey, String id, String[] erpids, int[] sizes,
			RequestCallback callback)
	{
		int requestCode = RequestCode.shoppingcart_add;
		String url = Config.AppendShoppingcartUrl;
		BasicHttpLoadParams params = new BasicHttpLoadParams(false);
		params.addRequestParam(new BasicNameValuePair("key", userKey));
		params.addRequestParam(new BasicNameValuePair("id", id));
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < erpids.length; i++)
		{
			if (i > 0)
				sb.append(',');
			// if (nums != null)
			sb.append(erpids[i]).append('|').append(sizes[i]);
			// else
			// sb.append(ids[i]).append('|').append(1);
		}
		params.addRequestParam(new BasicNameValuePair("erpsize", Uri.encode(sb.toString())));
		// params.addRequestParam(new BasicNameValuePair("erpid", erpid));
		// params.addRequestParam(new BasicNameValuePair("size", "" + size));

		return UrlLoader.getDefault().startLoad(context, url, params,
				new MyJsonParser<BaseResponse>(context, requestCode, callback, BaseResponse.class), CacheMode.NO_CACHE);
	}
	
	//添加裸石到购物车
	public static long appendShoppingcart(Context context, String userKey, GiaBargainResponse gia,
			RequestCallback callback)
	{
		int requestCode = RequestCode.shoppingcart_add;
		String url = Config.AppendShoppingcartUrl;
		BasicHttpLoadParams params = new BasicHttpLoadParams(false);
		params.addRequestParam(new BasicNameValuePair("key", userKey));
		params.addRequestParam(new BasicNameValuePair("gia[sn]", gia.datas.sn));
		params.addRequestParam(new BasicNameValuePair("gia[certno]", gia.datas.certno));
		params.addRequestParam(new BasicNameValuePair("gia[style]", gia.datas.style));
		params.addRequestParam(new BasicNameValuePair("gia[weight]", gia.datas.weight));
		params.addRequestParam(new BasicNameValuePair("gia[color]", gia.datas.color));
		params.addRequestParam(new BasicNameValuePair("gia[clarity]", gia.datas.clarity));
		params.addRequestParam(new BasicNameValuePair("gia[cut]", gia.datas.cut));
		params.addRequestParam(new BasicNameValuePair("gia[polish]", gia.datas.polish));
		params.addRequestParam(new BasicNameValuePair("gia[symmetry]", gia.datas.symmetry));
		params.addRequestParam(new BasicNameValuePair("gia[fluorescence]", gia.datas.fluorescence));
		params.addRequestParam(new BasicNameValuePair("gia[certtype]", "GIA"));
		params.addRequestParam(new BasicNameValuePair("gia[price]", gia.datas.price));
		params.addRequestParam(new BasicNameValuePair("price", gia.datas.price));
		return UrlLoader.getDefault().startLoad(context, url, params,
				new MyJsonParser<BaseResponse>(context, requestCode, callback, BaseResponse.class), CacheMode.NO_CACHE);
	}

	/**
	 * 对戒添加购物车
	 * 
	 * @param userKey 用户登录后key
	 * @param id 商品id
	 * @param erpid 男戒erpid
	 * @param erpid2 女戒erpid
	 * @param size 男戒手寸
	 * @param size2 女戒手寸
	 * */
	// public static long appendShoppingcart(Context context, String userKey, String id, String erpid, String erpid2,
	// int size, int size2, RequestCallback callback)
	// {
	// int requestCode = RequestCode.shoppingcart_add;
	// String url = Config.AppendShoppingcartUrl;
	// BasicHttpLoadParams params = new BasicHttpLoadParams(false);
	// params.addRequestParam(new BasicNameValuePair("key", userKey));
	// params.addRequestParam(new BasicNameValuePair("id", id));
	// params.addRequestParam(new BasicNameValuePair("erpid", erpid));
	// params.addRequestParam(new BasicNameValuePair("erpid2", erpid2));
	// params.addRequestParam(new BasicNameValuePair("size", "" + size));
	// params.addRequestParam(new BasicNameValuePair("size2", "" + size2));
	//
	// return UrlLoader.getDefault().startLoad(context, url, params,
	// new MyJsonParser<BaseResponse>(context, requestCode, callback, BaseResponse.class), CacheMode.NO_CACHE);
	// }

	/**
	 * 门店列表
	 * 
	 * @param userKey 用户登录后key
	 * */
	public static long getShopList(Context context, String userKey, RequestCallback callback)
	{
		int requestCode = RequestCode.store_list;
		String url = Config.GetShopListUrl;
		BasicHttpLoadParams params = new BasicHttpLoadParams(false);
		params.addRequestParam(new BasicNameValuePair("key", userKey));
		return UrlLoader.getDefault().startLoad(context, url, params,
				new MyJsonParser<ShopListResponse>(context, requestCode, callback, ShopListResponse.class),
				CacheMode.PERFER_NETWORK);
	}
	
	/**
	 * 读取购物车列表
	 * 
	 * @param userKey 用户登录后key
	 * */
	public static long loadShoppingcartList(Context context, String userKey, RequestCallback callback)
	{
		int requestCode = RequestCode.shoppingcart_list;
		String url = Config.LoadShoppingcartListUrl;

		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("reqeustCode", "" + requestCode));
		pairs.add(new BasicNameValuePair("key", userKey));

		HttpLoadParams params = new BasicHttpLoadParams(false, pairs);

		return UrlLoader.getDefault().startLoad(
				context,
				url,
				params,
				new MyJsonParser<ShoppingcartListResponse>(context, requestCode, callback,
						ShoppingcartListResponse.class), CacheMode.NO_CACHE);
	}

	public static long checkVersion(Context context, RequestCallback callback)
	{
		int requestCode = RequestCode.user_check_version;
		String url = Config.CheckVersionUrl;
		BasicHttpLoadParams params = new BasicHttpLoadParams(false);
		params.addRequestParam(new BasicNameValuePair("reqeustCode", "" + requestCode));
		try
		{
			PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			params.addRequestParam(new BasicNameValuePair("versionCode", "" + pi.versionCode));
			params.addRequestParam(new BasicNameValuePair("versionName", pi.versionName));
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		params.addRequestParam(new BasicNameValuePair("channel", "" + Config.CHANNEL));
		params.addRequestParam(new BasicNameValuePair("platform", "" + Config.PLATFORM));
		return UrlLoader.getDefault().startLoad(context, url, params,
				new MyJsonParser<CheckVersionResponse>(context, requestCode, callback, CheckVersionResponse.class),
				CacheMode.NO_CACHE);
	}

	/** 上传分享图片 */
	public static long uploadShareImage(Context context, String imagePath, RequestCallback callback)
	{
		int requestCode = RequestCode.upload_share_image;
		String url = Config.UploadShareImageUrl;
		HttpFileUploadParams params = new HttpFileUploadParams(null, "share_img", imagePath);

		return UrlLoader.getDefault().startLoad(context, url, params,
				new MyJsonParser<UploadFileResponse>(context, requestCode, callback, UploadFileResponse.class), null);
	}

	//特价裸石接口
	public static long giaBargain(Context context, String userKey, RequestCallback callback){
		
		int requestCode = 0;
		String url = Config.GiaBargainUrl;
		BasicHttpLoadParams params = new BasicHttpLoadParams(false);
		params.addRequestParam(new BasicNameValuePair("key", "" + userKey));
		Log.i("1","userKey:" + userKey);
		return UrlLoader.getDefault().startLoad(context, url, params,	
				new MyJsonParser<GiaBargainResponse>(context, requestCode, callback, GiaBargainResponse.class),
				CacheMode.NO_CACHE);
	}
	
	
	
	
	public static class MyJsonParser<T extends BaseResponse> extends BaseJsonParser
	{
		private int mRequestCode;
		private RequestCallback mCallback;
		private Class<T> mClass;

		// private String mCacheKey;
		// private Map mCacheMap;

		// public MyJsonParser(Context context, int requestCode, RequestCallback callback, Class<T> classz,
		// String cacheKey, Map cacheMap)
		// {
		// super(context);
		// mRequestCode = requestCode;
		// mCallback = callback;
		// mClass = classz;
		// mCacheKey = cacheKey;
		// mCacheMap = cacheMap;
		// }

		public MyJsonParser(Context context, int requestCode, RequestCallback callback, Class<T> classz)
		{
			// this(context, requestCode, callback, classz, null, null);
			super(context);
			mRequestCode = requestCode;
			mCallback = callback;
			mClass = classz;
		}

		@Override
		public void onJsonParse(String json, String url, String cacheKey, LoadParams params, DataFrom from)
		{
			json = json.trim();
			int idx = json.indexOf('{');
			if (idx > 0)
				json = json.substring(idx);
			LogUtil.i(TAG, "json: " + json);
			// for (int i = 0; i < json.length(); i++)
			// {
			// LogUtil.d(TAG, "json.charAt(" + i + ")=" + json.charAt(i));
			// }
			GsonBuilder gb = new GsonBuilder();
			Gson gson = gb.create();
			T response = gson.fromJson(json, mClass);
			// if (response.resultCode == BaseResponse.RESULT_OK && mCacheMap != null)
			// {
			// if (!TextUtils.isEmpty(mCacheKey))
			// {
			// mCacheMap.put(mCacheKey, response);
			
			// }
			// }
			response.printData(TAG, 2);
			if (mCallback != null)
				mCallback.onRequestResult(mRequestCode, mTaskId, response, from);
		}

		@Override
		public void onError(String url, LoadParams params, ErrorInfo error)
		{
			if (mCallback != null)
				mCallback.onRequestError(mRequestCode, mTaskId, error);
		}
	}
}
