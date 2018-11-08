package android.extend.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileData extends BaseData
{
	public String url;// 文件下载地址
	public long size;// 文件大小
	public long filemtime;// 文件修改时间

	public FileData()
	{
	}

	public FileData(String url, long size, long filemtime)
	{
		this.url = url;
		this.size = size;
		this.filemtime = filemtime;
	}

	public FileData(FileData source)
	{
		copyFrom(source);
	}

	public void copyFrom(FileData source)
	{
		if (source != null)
		{
			url = source.url;
			size = source.size;
			filemtime = source.filemtime;
		}
	}

	public boolean equals(FileData other)
	{
		if (!url.equals(other.url))
			return false;
		if (filemtime != other.filemtime)
			return false;
		return true;
	}

	public static String toJson(FileData info)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.toJson(info);
	}

	public static FileData fromJson(String json)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.fromJson(json, FileData.class);
	}
}
