package kz.virtex.htc.tweaker;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import kz.virtex.htc.tweaker.utils.MediaStorageMgr;
import kz.virtex.htc.tweaker.utils.ParseExceptionContents;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class TweakerService extends Service
{
	public static final String ACTION_CLEANUP_RECORDS = "tweaker.intent.action.CLEANUP_RECORDS";
	public static final String ACTION_GET_SETTINGS = "tweaker.intent.action.SETTINGS";
	public static final String ACTION_VOLUME_KEY_PRESS = "tweaker.intent.action.VOLUME_KEY";

	private SharedPreferences preferences;
	private static ArrayList<Ls> mFileList = new ArrayList<Ls>();
	private final int deleteCooldown = 60 * 60; // One in an hour
	private static boolean isVolumePressed = false;

	public void onCreate()
	{
		super.onCreate();
	}

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (intent != null)
		{
			String action = intent.getAction();
			// Toast.makeText(this, intent.getAction(),
			// Toast.LENGTH_LONG).show();

			if (action == null)
				action = "UNKNOWN";

			Misc.d("TweakerService received action: " + action);
			if (action.equals(ACTION_CLEANUP_RECORDS))
			{
				cleanUpRecords();
			}
			if (action.equals(ACTION_VOLUME_KEY_PRESS))
			{
				processVolKeyPress(intent);
			}
			if (action.equals(ACTION_GET_SETTINGS))
			{
				processGetSettings(intent);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
	private void processGetSettings(Intent intent)
	{
		preferences = getBaseContext().getSharedPreferences(Const.PREFERENCE_FILE, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		
		Bundle extras = intent.getExtras();
		String type = extras.getString("type");
		String deflt = extras.getString("default");
		String name = extras.getString("name");
		Object value = null;
		
		if (type.equals("string"))
			value = preferences.getString(name, deflt);
		if (type.equals("float"))
			value = preferences.getFloat(name, Float.valueOf(deflt));
		if (type.equals("int"))
			value = preferences.getInt(name, Integer.valueOf(deflt));
		if (type.equals("boolean"))
			value = preferences.getBoolean(name, Boolean.valueOf(deflt));
		if (type.equals("long"))
			value = preferences.getLong(name, Long.valueOf(deflt));

		Intent brIntent = new Intent(ACTION_GET_SETTINGS);
		brIntent.putExtra("value", String.valueOf(value));
		sendBroadcast(brIntent);
		Misc.d("TweakerService processGetSettings, sending broadcast for " + name + " = " + value);
	}
	
	private void processVolKeyPress(Intent intent)
	{
		Bundle extras = intent.getExtras();
		String task = "get";
		Boolean status = false;

		if (extras != null)
		{
			task = extras.getString("task");
			status = extras.getBoolean("status");
		}
		Misc.d("TweakerService processVolKeyPress, task is " + task);
		if (task.equals("set"))
		{
			Misc.d("TweakerService processVolKeyPress, setting isVolumePressed to " + status);
			isVolumePressed = status;
		}
		else
		{
			Intent brIntent = new Intent(ACTION_VOLUME_KEY_PRESS);
			brIntent.putExtra("status", isVolumePressed);
			sendBroadcast(brIntent);
			Misc.d("TweakerService processVolKeyPress, sending broadcast with isVolumePressed = " + isVolumePressed);
		}
	}

	@SuppressLint(
	{ "WorldReadableFiles", "WorldWriteableFiles" })
	private void cleanUpRecords()
	{
		preferences = getBaseContext().getSharedPreferences(Const.PREFERENCE_FILE, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);

		int deleteType = Integer.parseInt(preferences.getString(Const.TWEAK_CALL_REC_AUTO_DELETE, "0"));
		int deleteCount = preferences.getInt(Const.TWEAK_CALL_REC_AUTO_DELETE_COUNT, 50);
		int deleteInterval = preferences.getInt(Const.TWEAK_CALL_REC_AUTO_DELETE_INTERVAL, 30);
		long lastDelete = preferences.getLong(Const.TWEAK_CALL_REC_AUTO_LAST_DELETE, 0);
		long now = System.currentTimeMillis() / 1000l;

		if (deleteType != 0 && (now - lastDelete) > deleteCooldown)
		{
			Log.d("TweakerService", "last delete was performed seconds ago: " + (now - lastDelete));
			preferences.edit().putLong(Const.TWEAK_CALL_REC_AUTO_LAST_DELETE, now).commit();

			getRecords();

			switch (deleteType)
			{
				case 0:
					return; // ubreachable code
				case 1:
					cleanUpRecordsByCount(deleteCount);
					break;
				case 2:
					cleanUpRecordsByInterval(deleteInterval);
					break;
			}
		}
	}

	private void cleanUpRecordsByInterval(int deleteInterval)
	{
		Log.d("TweakerService", "deleting by interval: " + deleteInterval);

		Calendar now = Calendar.getInstance();
		Date date = new Date();
		now.setTime(date);

		for (int i = 0; i < mFileList.size(); i++)
		{
			Calendar old = Calendar.getInstance();
			old.setTime(mFileList.get(i).getDate());

			if (getElapsedDaysText(old, now) > deleteInterval)
			{
				try
				{
					Log.d("TweakerService", "deleting " + mFileList.get(i).getName());

					File file = new File(mFileList.get(i).getPath());
					file.delete();
				}
				catch (Exception e)
				{
					ParseExceptionContents.loge(e.getStackTrace());
				}
			}
		}
	}

	private long getElapsedDaysText(Calendar c1, Calendar c2)
	{
		long milliSeconds1 = c1.getTimeInMillis();
		long milliSeconds2 = c2.getTimeInMillis();
		long periodSeconds = (milliSeconds2 - milliSeconds1) / 1000;
		long elapsedDays = periodSeconds / 60 / 60 / 24;
		return elapsedDays;
	}

	private void cleanUpRecordsByCount(int deleteCount)
	{
		Log.d("TweakerService", "deleting by count: " + deleteCount);

		for (int i = 0; i < mFileList.size(); i++)
		{
			if (deleteCount <= 0)
			{
				try
				{
					Log.d("TweakerService", "deleting " + mFileList.get(i).getName());

					File file = new File(mFileList.get(i).getPath());
					file.delete();
				}
				catch (Exception e)
				{
					ParseExceptionContents.loge(e.getStackTrace());
				}
			}
			deleteCount--;
		}
	}

	private void getRecords()
	{
		mFileList = new ArrayList<Ls>();

		getRecordsList(MediaStorageMgr.getSDCardFullPath() + "/" + Const.AUTO_REC_MAIN);
		getRecordsList(MediaStorageMgr.getPhoneStorageFullPath() + "/" + Const.AUTO_REC_MAIN);

		Collections.sort(mFileList, new Comparator<Ls>()
		{
			public int compare(Ls rowOne, Ls rowTwo)
			{
				if (rowOne.getTime() > rowTwo.getTime())
				{
					return -1;
				}
				else
					if (rowOne.getTime() < rowTwo.getTime())
					{
						return 1;
					}
				return 0;
			}
		});
	}

	private void getRecordsList(String path)
	{
		File file = new File(path);
		File list[] = file.listFiles();

		if (list != null && list.length > 0)
		{
			for (File row : list)
			{
				if (!row.isDirectory() && !row.getName().equals(".nomedia"))
				{
					mFileList.add(new Ls(row.getAbsolutePath(), row.getName(), row.lastModified()));
				}
				else
				{
					getRecordsList(row.getAbsolutePath());
				}
			}
		}
		return;

	}

	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	public class Ls
	{
		String mPath;
		String mName;
		long mTimestamp;
		Date mDate;

		public Ls(String path, String name, long date)
		{
			mPath = path;
			mName = name;
			mTimestamp = date;
			mDate = new Date(date);
		}

		public String getName()
		{
			return mName;
		}

		public String getPath()
		{
			return mPath;
		}

		public long getTime()
		{
			return mTimestamp;
		}

		public Date getDate()
		{
			return mDate;
		}
	}
}
