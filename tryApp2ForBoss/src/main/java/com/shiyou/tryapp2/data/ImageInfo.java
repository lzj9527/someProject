package com.shiyou.tryapp2.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ImageInfo extends FileInfo
{
	public int width;
	public int height;

	public ImageInfo()
	{
	}

	public ImageInfo(FileInfo source)
	{
		super.copyFrom(source);
	}

	public ImageInfo(ImageInfo source)
	{
		copyFrom(source);
	}

	public void copyFrom(ImageInfo source)
	{
		if (source != null)
		{
			super.copyFrom(source);
			width = source.width;
			height = source.height;
		}
	}

	public static String toJson(ImageInfo info)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.toJson(info);
	}

	public static ImageInfo fromJson(String json)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.fromJson(json, ImageInfo.class);
	}
}
