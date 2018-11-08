package com.shiyou.tryapp2.data;

import android.extend.data.BaseData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RectBounds extends BaseData
{
	public float centerX;
	public float centerY;
	public float width;
	public float height;

	public static String toJson(RectBounds info)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.toJson(info);
	}

	public static RectBounds fromJson(String json)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.fromJson(json, RectBounds.class);
	}
}
