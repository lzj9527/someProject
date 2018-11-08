package android.extend.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import android.extend.util.ZipUtil.OnUnZipCallback;

/**
 * 解压tar.gz文件包
 */
public class GZipUtil
{
	public static final String TAG = "GZipUtil";

	// private BufferedOutputStream bufferedOutputStream;
	//
	// String zipfileName = null;
	//
	// public GZipUtil(String fileName)
	// {
	// this.zipfileName = fileName;
	// }
	//
	// /*
	// * 执行入口,rarFileName为需要解压的文件路径(具体到文件),destDir为解压目标路径
	// */
	// public static void unTargzFile(String rarFileName, String destDir)
	// {
	// GZipUtil gzip = new GZipUtil(rarFileName);
	// String outputDirectory = destDir;
	// File file = new File(outputDirectory);
	// if (!file.exists())
	// {
	// file.mkdir();
	// }
	// gzip.unzipOarFile(outputDirectory);
	//
	// }

	// public void unzipOarFile(String outputDirectory)
	// {
	// FileInputStream fis = null;
	// ArchiveInputStream in = null;
	// BufferedInputStream bufferedInputStream = null;
	// try
	// {
	// fis = new FileInputStream(zipfileName);
	// GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(fis));
	// in = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
	// bufferedInputStream = new BufferedInputStream(in);
	// TarArchiveEntry entry = (TarArchiveEntry)in.getNextEntry();
	// while (entry != null)
	// {
	// String name = entry.getName();
	// String[] names = name.split("/");
	// String fileName = outputDirectory;
	// for (int i = 0; i < names.length; i++)
	// {
	// String str = names[i];
	// fileName = fileName + File.separator + str;
	// }
	// if (name.endsWith("/"))
	// {
	// mkFolder(fileName);
	// }
	// else
	// {
	// File file = mkFile(fileName);
	// bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
	// int b;
	// while ((b = bufferedInputStream.read()) != -1)
	// {
	// bufferedOutputStream.write(b);
	// }
	// bufferedOutputStream.flush();
	// bufferedOutputStream.close();
	// }
	// entry = (TarArchiveEntry)in.getNextEntry();
	// }
	//
	// }
	// catch (FileNotFoundException e)
	// {
	// e.printStackTrace();
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// catch (ArchiveException e)
	// {
	// e.printStackTrace();
	// }
	// finally
	// {
	// try
	// {
	// if (bufferedInputStream != null)
	// {
	// bufferedInputStream.close();
	// }
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }
	// }

	public static void unGZipFileAsyn(final String gzipFilePath, final String outputFolderPath,
			final OnUnZipCallback callback)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				unGZipFile(gzipFilePath, outputFolderPath, callback);
			}
		}).start();
	}

	public static boolean unGZipFile(String gzipFilePath, String outputFolderPath, final OnUnZipCallback callback)
	{
		LogUtil.d(TAG, "unGZipFile: gzipFilePath=" + gzipFilePath + "; outputFolderPath=" + outputFolderPath);
		FileInputStream fis = null;
		ArchiveInputStream ais = null;
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try
		{
			int size = getGzipFilesNum(gzipFilePath);
			LogUtil.v(TAG, "getGzipFilesNum=" + size);
			File outputFolder = new File(outputFolderPath);
			outputFolder.mkdirs();
			if (!outputFolder.isDirectory())
				throw new IllegalArgumentException("the " + outputFolderPath + " is not directory!");
			fis = new FileInputStream(gzipFilePath);
			GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(fis));
			ais = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
			bufferedInputStream = new BufferedInputStream(ais);
			TarArchiveEntry entry = (TarArchiveEntry)ais.getNextEntry();
			int count = 0;
			while (entry != null)
			{
				count++;
				String name = entry.getName();
				String[] names = name.split("/");
				String fileName = outputFolderPath;
				for (int i = 0; i < names.length; i++)
				{
					String str = names[i];
					fileName = fileName + File.separator + str;
				}
				if (name.endsWith("/"))
				{
					mkFolder(fileName);
				}
				else
				{
					File file = mkFile(fileName);
					bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
					int c;
					byte[] buffer = new byte[1024];
					while ((c = bufferedInputStream.read(buffer, 0, buffer.length)) != -1)
					{
						bufferedOutputStream.write(buffer, 0, c);
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
					LogUtil.v(TAG, "unZip to " + file.getAbsolutePath() + " succeed; file.length=" + file.length()
							+ "; zipEntry.getSize=" + entry.getSize());
					if (callback != null)
						callback.onUnZipProgress(gzipFilePath, file.getAbsolutePath(), count, size);
				}
				entry = (TarArchiveEntry)ais.getNextEntry();
			}
			if (callback != null)
				callback.onUnZipFinished(gzipFilePath, outputFolderPath);
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			if (callback != null)
				callback.onUnZipFailed(gzipFilePath, outputFolderPath, ex);
		}
		finally
		{
			if (bufferedInputStream != null)
			{
				try
				{
					bufferedInputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (bufferedInputStream != null)
			{
				try
				{
					bufferedInputStream.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (ais != null)
			{
				try
				{
					ais.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static int getGzipFilesNum(String filePath) throws ArchiveException, IOException
	{
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		GZIPInputStream gis = null;
		ArchiveInputStream ais = null;
		int count = 0;
		try
		{
			fis = new FileInputStream(filePath);
			bis = new BufferedInputStream(fis);
			gis = new GZIPInputStream(bis);
			ais = new ArchiveStreamFactory().createArchiveInputStream("tar", gis);
			ArchiveEntry entry = ais.getNextEntry();
			while (entry != null)
			{
				count++;
				entry = ais.getNextEntry();
			}
		}
		finally
		{
			if (ais != null)
				ais.close();
			if (gis != null)
				gis.close();
			if (bis != null)
				bis.close();
			if (fis != null)
				fis.close();
		}
		return count;
	}

	private static void mkFolder(String path)
	{
		File f = new File(path);
		if (!f.exists())
		{
			f.mkdir();
		}
	}

	private static File mkFile(String path)
	{
		File f = new File(path);
		try
		{
			if (f.exists())
				f.delete();
			f.createNewFile();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return f;
	}
}
