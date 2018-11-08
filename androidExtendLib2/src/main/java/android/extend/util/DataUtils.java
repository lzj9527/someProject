package android.extend.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.protocol.HTTP;

public class DataUtils
{
	public static final int KB = 1024;
	public static final int MB = 1024 * 1024;

	public static byte[] readInStreamData(InputStream is, int bufferKBSize) throws IOException
	{
		if (bufferKBSize <= 0)
		{
			bufferKBSize = 10;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			byte[] buffer = new byte[bufferKBSize * KB];
			int readCount;
			while ((readCount = is.read(buffer)) != -1)
			{
				baos.write(buffer, 0, readCount);
			}
			byte[] data = baos.toByteArray();
			return data;
		}
		finally
		{
			if (baos != null)
			{
				baos.close();
			}
		}
	}

	public static byte[] readInStreamData(InputStream is, final int length, int bufferKBSize) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (bufferKBSize <= 0)
		{
			bufferKBSize = 10;
		}
		byte[] buffer = new byte[bufferKBSize * KB];
		int readLength = 0;
		int readCount = 0;
		while ((readCount = is.read(buffer)) != -1)
		{
			baos.write(buffer, 0, readCount);
			readLength += readCount;
			if (readLength >= length)
			{
				break;
			}
		}
		byte[] data = baos.toByteArray();
		return data;
	}

	public static void writeInStreamDataToOutStream(InputStream is, OutputStream os, int bufferKBSize)
			throws IOException
	{
		byte[] buffer = new byte[bufferKBSize * KB];
		int readCount;
		while ((readCount = is.read(buffer)) != -1)
		{
			os.write(buffer, 0, readCount);
		}
		os.flush();
	}

	public static String readString(InputStream is) throws UnsupportedEncodingException, IOException
	{
		return readString(is, HTTP.UTF_8);
	}

	public static String readString(InputStream is, String encoding) throws UnsupportedEncodingException, IOException
	{
		InputStreamReader isr = null;
		BufferedReader br = null;
		try
		{
			StringBuffer sb = new StringBuffer();
			isr = new InputStreamReader(is, encoding);
			br = new BufferedReader(isr);
			String value = null;
			while ((value = br.readLine()) != null)
			{
				sb.append(value).append("\n");
			}
			return sb.toString();
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			if (isr != null)
			{
				try
				{
					isr.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static byte[] shortToByte(short value, boolean reverse) throws Exception
	{
		byte[] bytes = new byte[2];
		if (!reverse)
		{
			bytes[0] = (byte)((value >> 8) & 0xff);
			bytes[1] = (byte)(value & 0xff);
		}
		else
		{
			bytes[0] = (byte)(value & 0xff);
			bytes[1] = (byte)((value >> 8) & 0xff);
		}
		return bytes;
	}

	public static short byteToShort(byte[] bytes, int index, boolean reverse) throws Exception
	{
		if (!reverse)
		{
			return (short)(((bytes[index] & 0xff) << 8) | (bytes[index + 1] & 0xff));
		}
		else
		{
			return (short)(((bytes[index + 1] & 0xff) << 8) | (bytes[index] & 0xff));
		}
	}

	public static byte[] intToByte(int value, boolean reverse) throws Exception
	{
		byte[] bytes = new byte[4];
		if (!reverse)
		{
			bytes[0] = (byte)((value >> 24) & 0xff);
			bytes[1] = (byte)((value >> 16) & 0xff);
			bytes[2] = (byte)((value >> 8) & 0xff);
			bytes[3] = (byte)(value & 0xff);
		}
		else
		{
			bytes[3] = (byte)((value >> 24) & 0xff);
			bytes[2] = (byte)((value >> 16) & 0xff);
			bytes[1] = (byte)((value >> 8) & 0xff);
			bytes[0] = (byte)(value & 0xff);
		}
		return bytes;
	}

	public static int byteToInt(byte[] bytes, int index, boolean reverse) throws Exception
	{
		if (!reverse)
		{
			return (((bytes[index + 0] & 0xff) << 24) | ((bytes[index + 1] & 0xff) << 16)
					| ((bytes[index + 2] & 0xff) << 8) | (bytes[index + 3] & 0xff));
		}
		else
		{
			return (((bytes[index + 3] & 0xff) << 24) | ((bytes[index + 2] & 0xff) << 16)
					| ((bytes[index + 1] & 0xff) << 8) | (bytes[index + 0] & 0xff));
		}
	}

	public static byte[] longToByte(long value, boolean reverse) throws Exception
	{
		byte[] bytes = new byte[8];
		if (!reverse)
		{
			bytes[0] = (byte)((value >> 56) & 0xff);
			bytes[1] = (byte)((value >> 48) & 0xff);
			bytes[2] = (byte)((value >> 40) & 0xff);
			bytes[3] = (byte)((value >> 32) & 0xff);
			bytes[4] = (byte)((value >> 24) & 0xff);
			bytes[5] = (byte)((value >> 16) & 0xff);
			bytes[6] = (byte)((value >> 8) & 0xff);
			bytes[7] = (byte)(value & 0xff);
		}
		else
		{
			bytes[7] = (byte)((value >> 56) & 0xff);
			bytes[6] = (byte)((value >> 48) & 0xff);
			bytes[5] = (byte)((value >> 40) & 0xff);
			bytes[4] = (byte)((value >> 32) & 0xff);
			bytes[3] = (byte)((value >> 24) & 0xff);
			bytes[2] = (byte)((value >> 16) & 0xff);
			bytes[1] = (byte)((value >> 8) & 0xff);
			bytes[0] = (byte)(value & 0xff);
		}
		return bytes;
	}

	/**
	 * 从byte数组指定位置获取一个long数值
	 */
	public static long byteToLong(byte[] bytes, int index, boolean reverse) throws Exception
	{
		if (!reverse)
		{
			return (((bytes[index + 0] & 0xff) << 56) | ((bytes[index + 1] & 0xff) << 48)
					| ((bytes[index + 2] & 0xff) << 40) | ((bytes[index + 3] & 0xff) << 32)
					| ((bytes[index + 4] & 0xff) << 24) | ((bytes[index + 5] & 0xff) << 16)
					| ((bytes[index + 6] & 0xff) << 8) | (bytes[index + 7] & 0xff));
		}
		else
		{
			return (((bytes[index + 7] & 0xff) << 56) | ((bytes[index + 6] & 0xff) << 48)
					| ((bytes[index + 5] & 0xff) << 40) | ((bytes[index + 4] & 0xff) << 32)
					| ((bytes[index + 3] & 0xff) << 24) | ((bytes[index + 2] & 0xff) << 16)
					| ((bytes[index + 1] & 0xff) << 8) | (bytes[index + 0] & 0xff));
		}
	}

	/**
	 * 转换byte为无符号整型
	 */
	public static int byteToUnsignedInt(byte b)
	{
		return b & 0xff;
	}

	public static String byteToHex(byte[] b)
	{
		String stmp = "";
		StringBuilder sb = new StringBuilder();
		for (int k = 0; k < b.length; k++)
		{
			stmp = Integer.toHexString(b[k] & 0xff);
			if (stmp.length() == 1)
				sb.append("0");
			sb.append(stmp);
		}
		return sb.toString();
	}

	public static byte[] hexToByte(String hex)
	{
		int len = hex.length() / 2;
		int offset = 0;
		byte[] b = new byte[len];
		String stemp = "";
		for (int k = 0; k < len; k++)
		{
			offset = k << 1;
			stemp = hex.substring(offset, offset + 2);
			try
			{
				b[k] = (byte)(Integer.parseInt(stemp, 16) & 0xff);
			}
			catch (NumberFormatException e)
			{
				b[k] = 0;
			}
		}
		return b;
	}
}
