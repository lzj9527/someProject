package android.extend.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ImageData extends FileData
{
	public int width;
	public int height;

	public ImageData()
	{
	}

	public ImageData(FileData source)
	{
		super.copyFrom(source);
	}

	public ImageData(ImageData source)
	{
		copyFrom(source);
	}

	public void copyFrom(ImageData source)
	{
		if (source != null)
		{
			super.copyFrom(source);
			width = source.width;
			height = source.height;
		}
	}

	public static String toJson(ImageData info)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.toJson(info);
	}

	public static ImageData fromJson(String json)
	{
		GsonBuilder gb = new GsonBuilder();
		Gson gson = gb.create();
		return gson.fromJson(json, ImageData.class);
	}
}
