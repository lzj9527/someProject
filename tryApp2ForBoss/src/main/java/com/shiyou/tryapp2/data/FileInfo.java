package com.shiyou.tryapp2.data;

import android.extend.data.BaseData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileInfo extends BaseData
{
	public String url;// 文件下载地址
	public long size;// 文件大小
	public int filemtime;// 文件修改时间
	public String path;// 文件本地地址

	public FileInfo()
	{
	}

	public FileInfo(FileInfo source)
	{
		copyFrom(source);
	}

	public void copyFrom(FileInfo source)
	{
		if (source != null)
		{
			url = source.url;
			size = source.size;
			filemtime = source.filemtime;
			path = source.path;
		}
	}

	public boolean equals(FileInfo other)
	{
		if (!url.equals(other.url))
			return false;
		if (filemtime != other.filemtime)
			return false;
		return true;
	}

	public static String toJson(FileInfo info)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.toJson(info);
	}

	public static FileInfo fromJson(String json)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.fromJson(json, FileInfo.class);
	}
}
