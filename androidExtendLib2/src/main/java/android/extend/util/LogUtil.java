package android.extend.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.content.Context;
import android.extend.BasicConfig;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class LogUtil
{
	static
	{
		HandlerThread handlerThread = new HandlerThread("LogToFile");
		handlerThread.start();
		Looper looper = handlerThread.getLooper();
		mLogFileHandler = new Handler(looper)
		{
			@Override
			public void handleMessage(Message msg)
			{
				LogFileInfo info = (LogFileInfo)msg.obj;
				writeLogFile(BasicConfig.ApplicationContext, info.level, info.logFileName, info.tag, info.message,
						info.throwable);
			}
		};
	}

	private static final Handler mLogFileHandler;
	private static final String LogFileName = FileUtils.makeNameInCurrentTime() + ".txt";

	private static class LogFileInfo
	{
		// public Context context;
		public int level = Log.DEBUG;
		public String logFileName;
		public String tag;
		public String message;
		public Throwable throwable;
	}

	private static final class LogFileWriter extends FileWriter
	{
		private String mPath;
		private boolean mClosed = false;

		public LogFileWriter(File file, boolean append) throws IOException
		{
			super(file, append);
			mPath = file.getAbsolutePath();
		}

		public LogFileWriter(File file) throws IOException
		{
			super(file);
			mPath = file.getAbsolutePath();
		}

		public LogFileWriter(String filename, boolean append) throws IOException
		{
			super(filename, append);
			mPath = filename;
		}

		public LogFileWriter(String filename) throws IOException
		{
			super(filename);
			mPath = filename;
		}

		public String getFilePath()
		{
			return mPath;
		}

		@Override
		public void close() throws IOException
		{
			super.close();
			mClosed = true;
		}

		public boolean isClosed()
		{
			return mClosed;
		}
	}

	// private static final CharSequence DATEFORMAT = "yyyy-MM-dd_kk:mm:ss";
	// private static final int MSG_PRINT = 0x01;
	// private static final int MSG_LOG = 0x02;

	// private static File mLogFile = null;
	// private static Context mContext = null;
	// private static LogFileWriter mLogWriter = null;
	// private static Handler mHandler = null;
	// private static Thread mThread = null;

	public static File getLogDirectory(Context context)
	{
		return FileUtils.getDirectory(context, "log");
	}

	public static synchronized void deleteAllLogFiles(Context context)
	{
		File directory = FileUtils.getExternalDirectory(context, "log");
		FileUtils.deleteFiles(directory);
		directory = FileUtils.getInternalDirectory(context, "log");
		FileUtils.deleteFiles(directory);
	}

	public final static String getLevelText(int level)
	{
		switch (level)
		{
			case Log.VERBOSE:
				return "VERBOSE";
			case Log.DEBUG:
				return "DEBUG";
			case Log.INFO:
				return "INFO";
			case Log.WARN:
				return "WARN";
			case Log.ERROR:
				return "ERROR";
			default:
				return "UNKNOW";
		}
	}

	public static void logHeaders(String tag, String msg, Header[] headers)
	{
		if (BasicConfig.DebugMode && headers != null)
		{
			int k = 0;
			for (Header header : headers)
			{
				android.util.Log.v(tag, msg + " Header[" + (++k) + "] " + header.getName() + " : " + header.getValue());
			}
		}
	}

	public static void i(String tag, String msg)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.i(tag, msg == null ? "" : msg);
			logToFile(Log.INFO, tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.i(tag, msg == null ? "" : msg, tr);
			logToFile(Log.INFO, tag, msg, tr);
		}
	}

	public static void d(String tag, String msg)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.d(tag, msg == null ? "" : msg);
			logToFile(Log.DEBUG, tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.d(tag, msg == null ? "" : msg, tr);
			logToFile(Log.DEBUG, tag, msg, tr);
		}
	}

	public static void e(String tag, String msg)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.e(tag, msg == null ? "" : msg);
			logToFile(Log.ERROR, tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.e(tag, msg == null ? "" : msg, tr);
			logToFile(Log.ERROR, tag, msg, tr);
		}
	}

	public static void v(String tag, String msg)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.v(tag, msg == null ? "" : msg);
			logToFile(Log.VERBOSE, tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.v(tag, msg == null ? "" : msg, tr);
			logToFile(Log.VERBOSE, tag, msg, tr);
		}
	}

	public static void w(String tag, String msg)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.w(tag, msg == null ? "" : msg);
			logToFile(Log.WARN, tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr)
	{
		if (BasicConfig.DebugMode)
		{
			android.util.Log.w(tag, msg == null ? "" : msg, tr);
			logToFile(Log.WARN, tag, msg, tr);
		}
	}

	public static void logToFile(String logFileName, int level, String tag, String message, Throwable throwable)
	{
		if (BasicConfig.LogToFile)
		{
			LogFileInfo info = new LogFileInfo();
			info.logFileName = logFileName;
			info.level = level;
			info.tag = tag;
			info.message = message;
			info.throwable = throwable;
			Message msg = mLogFileHandler.obtainMessage(0, info);
			msg.sendToTarget();
		}
	}

	public static void logToFile(String logFileName, int level, String tag, String message)
	{
		logToFile(logFileName, level, tag, message, null);
	}

	public static void logToFile(String logFileName, String tag, String message)
	{
		logToFile(logFileName, Log.DEBUG, tag, message, null);
	}

	public static void logToFile(int level, String tag, String message, Throwable throwable)
	{
		logToFile(LogFileName, level, tag, message, throwable);
	}

	public static void logToFile(int level, String tag, String message)
	{
		logToFile(LogFileName, level, tag, message);
	}

	public static void logToFile(String tag, String message)
	{
		logToFile(LogFileName, tag, message);
	}

	private static Map<String, LogFileWriter> mWriterMap = Collections
			.synchronizedMap(new HashMap<String, LogFileWriter>());

	private static FileWriter ensureFileWriter(Context context, String logFileName)
	{
		LogFileWriter writer = mWriterMap.get(logFileName);
		if (writer == null || writer.isClosed())
		{
			try
			{
				File file = FileUtils.getFile(context, "log", logFileName);
				LogUtil.d("ensureFileWriter", file.getAbsolutePath() + "; " + file.exists());
				writer = new LogFileWriter(file, true);
				mWriterMap.put(logFileName, writer);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return writer;
	}

	private static void writeLogFile(FileWriter writer, String text)
	{
		if (writer != null)
		{
			try
			{
				StringBuffer sb = new StringBuffer();
				sb.append(text).append('\n');
				writer.write(sb.toString());
				writer.flush();
				// LogUtil.v("writeLogFile", text);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				try
				{
					writer.close();
				}
				catch (IOException e1)
				{
				}
			}
		}
	}

	private static void writeLogFile(FileWriter writer, int level, String tag, String msg, Throwable throwable,
			long millisTime)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(FileUtils.makeNameInTime(millisTime)).append("     ");
		// sb.append(getLevelText(level)).append("    ");
		sb.append(tag).append("    ");
		sb.append(msg);
		if (throwable != null)
		{
			sb.append('\n');
			sb.append(throwable.toString());
		}
		writeLogFile(writer, sb.toString());
	}

	private static void writeLogFile(Context context, int level, String logFileName, String tag, String message,
			Throwable throwable)
	{
		FileWriter writer = ensureFileWriter(context, logFileName);
		writeLogFile(writer, level, tag, message, throwable, System.currentTimeMillis());
	}

	// private static void createSaveLogThread()
	// {
	// if (mThread == null)
	// {
	// mThread = new Thread()
	// {
	// @Override
	// public void run()
	// {
	// Looper.prepare();
	// mHandler = new Handler()
	// {
	// @Override
	// public void handleMessage(Message msg)
	// {
	// if (isInterrupted())
	// {
	// Looper.myLooper().quit();
	// return;
	// }
	// switch (msg.what)
	// {
	// case MSG_PRINT:
	// String text = (String)msg.obj;
	// writeLogFile(text);
	// break;
	// case MSG_LOG:
	// LogInfo info = (LogInfo)msg.obj;
	// writeLogFile(info.level, info.tag, info.msg, System.currentTimeMillis());
	// break;
	// }
	// }
	// };
	// Looper.loop();
	// }
	// };
	// mThread.start();
	// }
	// }

	// private static void openLogWriter()
	// {
	// if (mLogWriter == null)
	// {
	// String dirPath = AppConfig.getLogDirPath(mContext);
	// if (TextUtils.isEmpty(dirPath))
	// {
	// return;
	// }
	// CharSequence date = DateFormat.format(DATEFORMAT, System.currentTimeMillis());
	// String filePath = dirPath + date + ".log";
	// try
	// {
	// mLogWriter = new LogFileWriter(filePath, true);
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// mLogWriter = null;
	// }
	// }
	// }

	// private static void closeLogWriter()
	// {
	// if (mLogWriter != null)
	// {
	// try
	// {
	// mLogWriter.flush();
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// try
	// {
	// mLogWriter.close();
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// mLogWriter = null;
	// }
	// }

	// private static void checkLogFile()
	// {
	// if (mLogWriter != null)
	// {
	// String path = mLogWriter.getFilePath();
	// File file = new File(path);
	// if (file.length() > 1024 * 1024)
	// {
	// closeLogWriter();
	// }
	// }
	// if (mLogWriter == null)
	// {
	// openLogWriter();
	// }
	// }

	// private static void saveLogFile(String text)
	// {
	// if (TextUtils.isEmpty(text))
	// {
	// return;
	// }
	// if (isSaveLog)
	// {
	// createSaveLogThread();
	// if (mHandler != null)
	// {
	// mHandler.obtainMessage(MSG_PRINT, text).sendToTarget();
	// }
	// }
	// }

	// private static void saveLogFile(int level, String tag, String msg)
	// {
	// if (TextUtils.isEmpty(tag) && TextUtils.isEmpty(msg))
	// {
	// return;
	// }
	// if (BasicConfig.WriteLogToFile)
	// {
	// createSaveLogThread();
	// if (mHandler != null)
	// {
	// LogInfo info = new LogInfo();
	// info.level = level;
	// info.tag = tag;
	// info.msg = msg;
	// mHandler.obtainMessage(MSG_LOG, info).sendToTarget();
	// }
	// }
	// }

	// private static void writeLogFile(String text)
	// {
	// checkLogFile();
	// if (mLogWriter != null)
	// {
	// try
	// {
	// StringBuffer sb = new StringBuffer();
	// sb.append(text).append('\n');
	// mLogWriter.write(sb.toString());
	// mLogWriter.flush();
	// }
	// catch (IOException e)
	// {
	// e.printStackTrace();
	// closeLogWriter();
	// }
	// }
	// }

	// private static void writeLogFile(int level, String tag, String msg, long millisTime)
	// {
	// StringBuffer sb = new StringBuffer();
	// sb.append(FileUtils.makeNameInTime(millisTime)).append("     ");
	// sb.append(getLevelText(level)).append("    ");
	// sb.append(tag).append("    ");
	// sb.append(msg);
	// writeLogFile(sb.toString());
	// }
}
