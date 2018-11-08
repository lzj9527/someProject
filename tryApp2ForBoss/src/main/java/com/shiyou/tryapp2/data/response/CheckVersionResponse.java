package com.shiyou.tryapp2.data.response;

import android.extend.data.BaseData;

public class CheckVersionResponse extends BaseResponse
{
	public VersionInfo datas;

	public static class VersionInfo extends BaseData
	{
		public String versionCode;
		public String versionName;
		public String platform;
		public String channel;
		public String Url;
		public int isMust;
	}
}
