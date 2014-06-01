package kz.virtex.htc.tweaker.utils;

import java.io.File;
import android.os.Environment;
import com.htc.wrap.android.os.HtcWrapEnvironment;

public class MediaStorageMgr
{
	private static boolean isExternalStorageRemovable = false;
	private static boolean hasRemovableStorageSlot;
	private static boolean hasPhoneStorage;
	private static File sSDCardDirectory;
	private static File sPhoneStorageDirectory;

	static
	{
		hasPhoneStorage = false;
		hasRemovableStorageSlot = false;
		sSDCardDirectory = null;
		sPhoneStorageDirectory = null;
		isExternalStorageRemovable = Environment.isExternalStorageRemovable();
		hasPhoneStorage = HtcWrapEnvironment.hasPhoneStorage();
		hasRemovableStorageSlot = HtcWrapEnvironment.hasRemovableStorageSlot();

		if (isExternalStorageRemovable)
		{
			sSDCardDirectory = Environment.getExternalStorageDirectory();
			if (hasPhoneStorage)
			{
				sPhoneStorageDirectory = HtcWrapEnvironment.getPhoneStorageDirectory();
			}
		}
		else
		{
			sPhoneStorageDirectory = Environment.getExternalStorageDirectory();
			if (hasRemovableStorageSlot)
			{
				sSDCardDirectory = HtcWrapEnvironment.getRemovableStorageDirectory();
			}
		}
	}

	public MediaStorageMgr()
	{
	}

	public static String getSDCardFullPath()
	{
		File localFile = sSDCardDirectory;
		String str1 = null;
		if (localFile != null)
		{
			String str2 = smartAddslash(sSDCardDirectory.getPath());
			str1 = str2 + "My Documents/My Recordings";
		}
		return str1;
	}

	public static String getPhoneStorageFullPath()
	{
		File localFile = sPhoneStorageDirectory;
		String str1 = null;
		if (localFile != null)
		{
			String str2 = smartAddslash(sPhoneStorageDirectory.getPath());
			str1 = str2 + "My Documents/My Recordings";
		}
		return str1;
	}

	public static String smartAddslash(String paramString)
	{
		if ((paramString == null) || (paramString.length() == 0) || (paramString.endsWith("/")))
		{
			return paramString;
		}
		return paramString + '/';
	}
}
