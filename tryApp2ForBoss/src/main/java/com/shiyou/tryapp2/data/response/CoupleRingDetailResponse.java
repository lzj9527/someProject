package com.shiyou.tryapp2.data.response;

import android.extend.data.BaseData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shiyou.tryapp2.data.FileInfo;
import com.shiyou.tryapp2.data.ImageInfo;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse.ErpGoods;
import com.shiyou.tryapp2.data.response.GoodsDetailResponse.ParamItem;

public class CoupleRingDetailResponse extends BaseResponse
{
	public GoodsDetail datas;

	public static class GoodsDetail extends BaseData
	{
		public String id;
		public String title;
		public String sku;
		public String m_sku;
		public String w_sku;
		public String goodssn;
		public String marketprice;
		public ImageInfo thumb;// 主图
		public ImageInfo[] thumb_url;// 相册
		public ParamItem[] param;// 商品属性
		public ErpDetail erp;
		public int count;
		public ModelInfos model_infos;// 对戒模型文件

		public static String toJson(GoodsDetail info)
		{
			GsonBuilder gb = new GsonBuilder();
			Gson gson = gb.create();
			return gson.toJson(info);
		}

		public static GoodsDetail fromJson(String json)
		{
			GsonBuilder gb = new GsonBuilder();
			Gson gson = gb.create();
			return gson.fromJson(json, GoodsDetail.class);
		}
	}

	public static class ErpDetail extends BaseData
	{
		public ErpGoods[] men;// 男戒
		public ErpGoods[] wmen;// 女戒
	}

	public static class ModelInfos extends BaseData
	{
		public FileInfo men;
		public FileInfo wmen;
	}
}
