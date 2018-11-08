package com.shiyou.tryapp2.app.util;

import java.io.File;
import java.util.ArrayList;

public interface ImageDownLoadCallBack {
	void onDownLoadSuccess(final ArrayList<File> file_list);
	void onDownLoadFailed();
}
