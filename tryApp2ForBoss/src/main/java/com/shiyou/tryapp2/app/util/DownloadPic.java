package com.shiyou.tryapp2.app.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.shiyou.tryapp2.data.ImageInfo;

import android.content.Context;
import android.os.Environment;

public class DownloadPic implements Runnable {

	private ImageInfo[] urls;
	private Context context;
	private ImageDownLoadCallBack callBack;

	public DownloadPic(Context context, ImageInfo[] urls,
			ImageDownLoadCallBack callBack) {
		this.urls = urls;
		this.callBack = callBack;
		this.context = context;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		ArrayList<File> file_list = new ArrayList<File>();

		for (ImageInfo info : urls) {

			URL url;
			try {
				url = new URL(info.url);
				final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getNameFromPath(info.url));
				
				if (!file.exists()){
					// 获取连接对象，并没有建立连接
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					// 设置连接和读取超时
					conn.setConnectTimeout(5000);
					conn.setReadTimeout(5000);
					// 设置请求方法，注意必须大写
					conn.setRequestMethod("GET");
					// 建立连接，发送get请求
					// conn.connect();
					// 建立连接，然后获取响应吗，200说明请求成功
					conn.getResponseCode();

					// 1.拿到服务器返回的输入流
					InputStream is = conn.getInputStream();
					// 2.把流里的数据读取出来，并构造成图片

					
					FileOutputStream fos = new FileOutputStream(file);
					byte[] b = new byte[1024];
					int len = 0;
					while ((len = is.read(b)) != -1) {
						fos.write(b, 0, len);
					}
				}
				file_list.add(file);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		callBack.onDownLoadSuccess(file_list);
	}
	
	 public String getNameFromPath(String path){
	        int index = path.lastIndexOf("/");
	        return path.substring(index + 1);
	    }
}
