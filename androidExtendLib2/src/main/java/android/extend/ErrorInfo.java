package android.extend;

import android.util.Log;

public class ErrorInfo
{
	public static final int ERROR_UNKNOWN = -100;// 未知错误
	public static final int ERROR_PROTOCOL = -101;// 协议错误
	public static final int ERROR_SOCKETTIMEOUT = -102;// 链接超时
	public static final int ERROR_UNKNOWHOST = -103;// 域名解析错误
	public static final int ERROR_SOCKET = -104;// 链接错误
	public static final int ERROR_RESPONSECODE = -106;// 返回码错误
	public static final int ERROR_OUTOFMEMORY = -107;// 内存溢出
	public static final int ERROR_READCACHE = -108;// 读取缓存错误
	public static final int ERROR_NOTFOUNDCACHE = -109;// 未找到缓存

	public static final int ERROR_EXCEPTION = -200;// 未知异常
	public static final int ERROR_SECURITYEXCEPTION = -201;// 权限异常
	public static final int ERROR_IOEXCEPTION = -202;// 读取异常
	public static final int ERROR_JSONEXCEPTION = -203;// JSON解析异常
	public static final int ERROR_FILENOTFOUNDEXCEPTION = -204;// 文件未找到异常
	public static final int ERROR_NULLPOINTEREXCEPTION = -205;// 空指针异常
	public static final int ERROR_UNSUPPORTEDENCODING = -206;// 不支持的编码异常

	public int errorCode = ERROR_UNKNOWN;
	public String description;
	public Throwable throwable;

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("ErrorInfo: ");
		sb.append("errorCode=").append(errorCode).append("; ");
		sb.append("description=").append(description).append("; ");
		sb.append("throwable=").append(throwable == null ? null : Log.getStackTraceString(throwable)).append("; ");
		return sb.toString();
	}
}
