package kz.virtex.htc.tweaker.utils;

import android.util.Log;
import de.robv.android.xposed.XposedBridge;

public class ParseExceptionContents
{
	final static String newLine = System.getProperty("line.separator");
	final static String headerLine = "---------------------------------------------------------------------";
	final static String headerTitlePortion = "-- StackTraceElement Index #";

	public static void xposed(StackTraceElement[] stackTrace)
	{
		XposedBridge.log(build(stackTrace));
	}

	public static void logd(StackTraceElement[] stackTrace)
	{
		Log.d("ParseExceptionContents", build(stackTrace));
	}

	private static String build(StackTraceElement[] stackTrace)
	{
		int index = 0;
		StringBuilder builder = new StringBuilder();

		builder.append(headerLine + newLine);
		for (StackTraceElement element : stackTrace)
		{
			final String exceptionMsg 
					= "ClassName:     " + element.getClassName() + newLine 
					+ "MethodName:    " + element.getMethodName() + newLine 
					+ "FileName:      " + element.getFileName() + newLine 
					+ "LineNumber:    " + element.getLineNumber() + newLine 
					+ "\t" + newLine
			;

			builder.append(headerTitlePortion + index++ + newLine);
			builder.append(exceptionMsg);
		}

		return builder.toString();
	}

}
