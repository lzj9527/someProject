package com.shiyou.tryapp2.data.response;

import android.extend.data.BaseData;

public class ShopListResponse extends BaseResponse
{
	public ShopList datas;

	public static class ShopList extends BaseData
	{
		public ShopInfo[] list;
	}

	public static class ShopInfo extends BaseData
	{
		public String id;
		public String nickname;
	}
}
