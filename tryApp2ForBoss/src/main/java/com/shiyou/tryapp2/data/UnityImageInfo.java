package com.shiyou.tryapp2.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class UnityImageInfo extends ImageInfo
{
	public String tag;

	/**
	 * 试戴相关属性，从保存的试戴数据中读取
	 */
	public byte needReadRecord;

	public double xPosition = 0f;
	public double yPosition = 0f;
	public double localScale = 1f;
	public double childXScale = 1f;
	public double childYScale = 1f;

	public UnityImageInfo()
	{
	}

	public UnityImageInfo(ImageInfo source)
	{
		super.copyFrom(source);
	}

	public UnityImageInfo(UnityImageInfo source)
	{
		copyFrom(source);
	}

	public void copyFrom(UnityImageInfo source)
	{
		if (source != null)
		{
			super.copyFrom(source);
			tag = source.tag;
		}
	}

	public static String toJson(UnityImageInfo info)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.toJson(info);
	}

	public static UnityImageInfo fromJson(String json)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.fromJson(json, UnityImageInfo.class);
	}
}
