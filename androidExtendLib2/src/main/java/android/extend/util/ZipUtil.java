package android.extend.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil
{
	public static final String TAG = "ZipUtil";

	public interface OnUnZipCallback
	{
		public void onUnZipStarted(String zipFilePath, String outputFolderPath);

		public void onUnZipProgress(String zipFilePath, String unZipPath, int unZipedNum, int totalNum);

		public void onUnZipFinished(String zipFilePath, String outputFolderPath);

		public void onUnZipFailed(String zipFilePath, String outputFolderPath, Exception ex);
	}

	public static void upZipFileAsyn(final String zipFilePath, final String outputFolderPath,
			final OnUnZipCallback callback)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				ZipUtil.unZipFile(zipFilePath, outputFolderPath, callback);
			}
		}).start();
	}

	@SuppressWarnings("rawtypes")
	public static boolean unZipFile(String zipFilePath, String outputFolderPath, final OnUnZipCallback callback)
	{
		LogUtil.d(TAG, "unZipFile: zipFilePath=" + zipFilePath + "; outputFolderPath=" + outputFolderPath);
		ZipFile zipFile = null;
		InputStream in = null;
		FileOutputStream out = null;
		try
		{
			if (callback != null)
				callback.onUnZipStarted(zipFilePath, outputFolderPath);
			zipFile = new ZipFile(zipFilePath);
			Enumeration e = zipFile.entries();
			ZipEntry zipEntry = null;
			File outputFolder = new File(outputFolderPath);
			outputFolder.mkdirs();
			if (!outputFolder.isDirectory())
				throw new IllegalArgumentException("the " + outputFolderPath + " is not directory!");
			int size = zipFile.size();
			LogUtil.v(TAG, "zipFile.size=" + size);
			int count = 0;
			while (e.hasMoreElements())
			{
				zipEntry = (ZipEntry)e.nextElement();
				String entryName = zipEntry.getName();
				LogUtil.v(TAG, "find zipEntry=" + entryName + "; isDirectory=" + zipEntry.isDirectory());
				if (zipEntry.isDirectory())
				{
					String name = entryName.substring(0, entryName.length() - 1);
					File file = new File(outputFolderPath + File.separator + name);
					LogUtil.v(TAG, "need mkdirs: " + file.getAbsolutePath() + "; exists=" + file.exists());
					file.mkdirs();
					count++;
					if (callback != null)
						callback.onUnZipProgress(zipFilePath, file.getAbsolutePath(), count, size);
				}
				else
				{
					int index = entryName.lastIndexOf("\\");
					if (index != -1)
					{
						File file = new File(outputFolderPath + File.separator + entryName.substring(0, index));
						LogUtil.v(TAG, "need mkdirs: " + file.getAbsolutePath() + "; exists=" + file.exists());
						file.mkdirs();
					}
					index = entryName.lastIndexOf("/");
					if (index != -1)
					{
						File file = new File(outputFolderPath + File.separator + entryName.substring(0, index));
						LogUtil.v(TAG, "need mkdirs: " + file.getAbsolutePath() + "; exists=" + file.exists());
						file.mkdirs();
					}
					String path = outputFolderPath + File.separator + entryName;
					File tempFile = new File(path + ".temp");
					if (tempFile.exists())
					{
						// LogUtil.w(TAG, "the " + tempFile.getAbsolutePath() + " file exists, do delete.");
						tempFile.delete();
					}
					tempFile.createNewFile();
					in = zipFile.getInputStream(zipEntry);
					out = new FileOutputStream(tempFile);
					int c;
					byte[] buffer = new byte[1024];
					while ((c = in.read(buffer)) != -1)
					{
						out.write(buffer, 0, c);
					}
					out.flush();
					out.close();
					out = null;
					in.close();
					in = null;
					File file = new File(path);
					if (file.exists())
						file.delete();
					tempFile.renameTo(file);
					LogUtil.v(TAG, "unZip to " + file.getAbsolutePath() + " succeed; file.length=" + file.length()
							+ "; zipEntry.getSize=" + zipEntry.getSize());
					count++;
					if (callback != null)
						callback.onUnZipProgress(zipFilePath, file.getAbsolutePath(), count, size);
				}
			}
			if (callback != null)
				callback.onUnZipFinished(zipFilePath, outputFolderPath);
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			if (callback != null)
				callback.onUnZipFailed(zipFilePath, outputFolderPath, ex);
		}
		finally
		{
			if (zipFile != null)
				try
				{
					zipFile.close();
				}
				catch (IOException ex)
				{
				}
			if (out != null)
				try
				{
					out.close();
				}
				catch (IOException ex)
				{
				}
			if (in != null)
				try
				{
					in.close();
				}
				catch (IOException ex)
				{
				}
		}
		return false;
	}

	/** 检查文件是否已经被解压 */
	@SuppressWarnings("rawtypes")
	public static boolean checkFileUnZiped(String zipFilePath, String outputFolderPath)
	{
		LogUtil.d(TAG, "checkFileUnZiped: zipFilePath=" + zipFilePath + "; outputFolderPath=" + outputFolderPath);
		ZipFile zipFile = null;
		InputStream in = null;
		FileOutputStream out = null;
		try
		{
			zipFile = new ZipFile(zipFilePath);
			Enumeration e = zipFile.entries();
			ZipEntry zipEntry = null;
			File outputFolder = new File(outputFolderPath);
			if (!outputFolder.exists())
			{
				LogUtil.w(TAG, "check folder: " + outputFolder.getAbsolutePath() + "; exists=" + outputFolder.exists());
				return false;
			}
			while (e.hasMoreElements())
			{
				zipEntry = (ZipEntry)e.nextElement();
				String entryName = zipEntry.getName();
				LogUtil.v(TAG, "find zipEntry=" + entryName + "; isDirectory=" + zipEntry.isDirectory());
				if (zipEntry.isDirectory())
				{
					String name = entryName.substring(0, entryName.length() - 1);
					File file = new File(outputFolderPath + File.separator + name);
					if (!file.exists())
					{
						LogUtil.w(TAG, "check folder: " + file.getAbsolutePath() + "; exists=" + file.exists());
						return false;
					}
				}
				else
				{
					int index = entryName.lastIndexOf("\\");
					if (index != -1)
					{
						File file = new File(outputFolderPath + File.separator + entryName.substring(0, index));
						if (!file.exists())
						{
							LogUtil.w(TAG, "check folder: " + file.getAbsolutePath() + "; exists=" + file.exists());
							return false;
						}
					}
					index = entryName.lastIndexOf("/");
					if (index != -1)
					{
						File file = new File(outputFolderPath + File.separator + entryName.substring(0, index));
						if (!file.exists())
						{
							LogUtil.w(TAG, "check folder: " + file.getAbsolutePath() + "; exists=" + file.exists());
							return false;
						}
					}
					String path = outputFolderPath + File.separator + entryName;
					File file = new File(path);
					if (file.length() != zipEntry.getSize())
					{
						LogUtil.w(TAG, "check file: " + file.getAbsolutePath() + "; file.length=" + file.length()
								+ "; zipEntry.getSize=" + zipEntry.getSize());
						return false;
					}
					long checksum = FileUtils.checksumCrc32(file);
					if (checksum != zipEntry.getCrc())
					{
						LogUtil.w(TAG, "check file: " + file.getAbsolutePath() + "; file.checksum=" + checksum
								+ "; zipEntry.getCrc=" + zipEntry.getCrc());
						return false;
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (zipFile != null)
				try
				{
					zipFile.close();
				}
				catch (IOException ex)
				{
				}
			if (out != null)
				try
				{
					out.close();
				}
				catch (IOException ex)
				{
				}
			if (in != null)
				try
				{
					in.close();
				}
				catch (IOException ex)
				{
				}
		}
	}

	public static boolean zipFile(String src, String outputFilePath)
	{
		// 提供了一个数据项压缩成一个ZIP归档输出流
		ZipOutputStream out = null;
		try
		{
			File fileOrDirectory = new File(src);// 源文件或者目录
			File outFile = new File(outputFilePath);// 压缩文件路径
			if (outFile.exists())
				outFile.delete();
			outFile.createNewFile();
			out = new ZipOutputStream(new FileOutputStream(outFile));
			// 如果此文件是一个文件，否则为false。
			if (fileOrDirectory.isFile())
			{
				return zipFileOrDirectory(out, fileOrDirectory, "");
			}
			else
			{
				// 返回一个文件或空阵列。
				File[] files = fileOrDirectory.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					// 递归压缩，更新curPaths
					if (!zipFileOrDirectory(out, files[i], ""))
					{
						return false;
					}
				}
			}
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			// 关闭输出流
			if (out != null)
				try
				{
					out.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
		}
		return false;
	}

	private static boolean zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory, String curPath)
	{
		// 从文件中读取字节的输入流
		FileInputStream in = null;
		try
		{
			// 如果此文件是一个目录，否则返回false。
			if (fileOrDirectory.isFile())
			{
				// 压缩文件
				byte[] buffer = new byte[4096];
				int read;
				in = new FileInputStream(fileOrDirectory);
				// 实例代表一个条目内的ZIP归档
				ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
				// 条目的信息写入底层流
				out.putNextEntry(entry);
				while ((read = in.read(buffer)) != -1)
				{
					out.write(buffer, 0, read);
				}
				out.closeEntry();
				return true;
			}
			else
			{
				// 压缩目录
				File[] files = fileOrDirectory.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					// 递归压缩，更新curPath
					if (!zipFileOrDirectory(out, files[i], curPath + fileOrDirectory.getName() + File.separatorChar))
					{
						return false;
					}
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (in != null)
				try
				{
					in.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
		}
		return false;
	}
}
