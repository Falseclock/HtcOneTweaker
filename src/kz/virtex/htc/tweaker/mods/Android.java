package kz.virtex.htc.tweaker.mods;

import java.util.HashMap;

import com.android.internal.util.ArrayUtils;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.XMain;
import android.annotation.SuppressLint;
import android.content.res.XModuleResources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

@SuppressLint("UseSparseArrays")
public class Android
{
	private static HashMap<String, Integer> mapTweak;
	private static HashMap<Integer, String> mapWeather;

	public static void hookAndroidLog()
	{
		final Class<?> Log = XposedHelpers.findClass("android.util.Log", null);
		
		XposedHelpers.findAndHookMethod(Log, "println_native", int.class, int.class, String.class, String.class, new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				int priority = (Integer) param.args[1];
				switch (priority)
				{
					case android.util.Log.VERBOSE:
						if (XMain.pref.getBoolean(Const.TWEAK_LOGCAT_FILTER + "_Verbose", false))
						{
							param.setResult(0);
							return;
						}
					case android.util.Log.DEBUG:
						if (XMain.pref.getBoolean(Const.TWEAK_LOGCAT_FILTER + "_Debug", false))
						{
							param.setResult(0);
							return;
						}
					case android.util.Log.INFO:
						if (XMain.pref.getBoolean(Const.TWEAK_LOGCAT_FILTER + "_Info", false))
						{
							param.setResult(0);
							return;
						}
					case android.util.Log.WARN:
						if (XMain.pref.getBoolean(Const.TWEAK_LOGCAT_FILTER + "_Warn", false))
						{
							param.setResult(0);
							return;
						}
					case android.util.Log.ERROR:
						if (XMain.pref.getBoolean(Const.TWEAK_LOGCAT_FILTER + "_Error", false))
						{
							param.setResult(0);
							return;
						}
				}
				param.setResult(0);
			}
		});
	}
	
	public static void hookAllCapsLocale()
	{
		final Class<?> PowerManagerService = XposedHelpers.findClass("com.htc.util.res.HtcResUtil", null);

		XposedHelpers.findAndHookMethod(PowerManagerService, "isInAllCapsLocale", "android.content.Context", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				return Boolean.valueOf(false);
			}
		});
	}

	public static void hookEnableSkypeCall()
	{
		final Class<?> PowerManagerService = XposedHelpers.findClass("com.android.internal.telephony.enableSkypeCall", null);

		XposedHelpers.findAndHookMethod(PowerManagerService, "startWatchingForBootAnimationFinished", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				Boolean ret = true;
				return ret;
			}
		});
	}

	public static void hookBootSound()
	{
		final Class<?> PowerManagerService = XposedHelpers.findClass("com.android.server.power.PowerManagerService", null);

		XposedHelpers.findAndHookMethod(PowerManagerService, "startWatchingForBootAnimationFinished", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				XposedBridge.log("startWatchingForBootAnimationFinished");
			}
		});

		final Class<?> AudioService = XposedHelpers.findClass("android.media.AudioService", null);

		XposedHelpers.findAndHookMethod(AudioService, "onSetStreamVolume", "int", "int", "int", "int", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				XposedBridge.log("onSetStreamVolume");
				XposedBridge.log("0: " + param.args[0]);
				XposedBridge.log("1: " + param.args[1]);
				XposedBridge.log("2: " + param.args[2]);
				XposedBridge.log("3: " + param.args[3]);

			}
		});

		XposedHelpers.findAndHookConstructor(AudioService, "android.content.Context", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				XposedBridge.log("AudioService");

				XposedBridge.log("STREAM_SYSTEM      : " + XposedHelpers.callMethod(param.thisObject, "getStreamVolume", AudioManager.STREAM_SYSTEM));
				XposedBridge.log("STREAM_NOTIFICATION: " + XposedHelpers.callMethod(param.thisObject, "getStreamVolume", AudioManager.STREAM_NOTIFICATION));

				// AudioService.XposedHelpers.callMethod(param.thisObject,
				// "onSetStreamVolume", 1,0,0,2);
				// XposedHelpers.callMethod(param.thisObject,
				// "onSetStreamVolume", 5,0,0,2);

				XposedBridge.log("STREAM_SYSTEM      : " + XposedHelpers.callMethod(param.thisObject, "getStreamVolume", AudioManager.STREAM_SYSTEM));
				XposedBridge.log("STREAM_NOTIFICATION: " + XposedHelpers.callMethod(param.thisObject, "getStreamVolume", AudioManager.STREAM_NOTIFICATION));
			}
		});
	}

	public static void hookCDROMMount()
	{
		Class<?> MountService = XposedHelpers.findClass("com.android.server.MountService", null);
		XposedHelpers.findAndHookMethod(MountService, "setMountISOEnabled", "boolean", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				param.args[0] = false;
			}
		});
		XposedHelpers.findAndHookMethod(MountService, "setPCtoolISOEnabled", "boolean", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				param.args[0] = false;
			}
		});
	}

	public static void hookSDcardPermission()
	{
		final Class<?> pms = XposedHelpers.findClass("com.android.server.pm.PackageManagerService", null);

		XposedHelpers.findAndHookMethod(pms, "readPermission", "org.xmlpull.v1.XmlPullParser", "java.lang.String", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				String permission = (String) param.args[1];

				if (permission.equals("android.permission.WRITE_EXTERNAL_STORAGE"))
				{
					Class<?> process = XposedHelpers.findClass("android.os.Process", null);
					int gid = (Integer) XposedHelpers.callStaticMethod(process, "getGidForName", "media_rw");
					Object mSettings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
					Object mPermissions = XposedHelpers.getObjectField(mSettings, "mPermissions");
					Object bp = XposedHelpers.callMethod(mPermissions, "get", permission);
					int[] bp_gids = (int[]) XposedHelpers.getObjectField(bp, "gids");
					XposedHelpers.setObjectField(bp, "gids", ArrayUtils.appendInt(bp_gids, gid));
				}
			}
		});
	}

	public static boolean hookWeatherBitmapPreload()
	{
		if (!XMain.pref.getBoolean(Const.TWEAK_COLORED_WEATHER, false))
			return false;

		if (XMain.weather_apk == null)
			return false;

		try
		{
			mapTweak = new HashMap<String, Integer>();
			mapWeather = new HashMap<Integer, String>();

			XModuleResources weatherRes = XModuleResources.createInstance("/system/framework/com.htc.android.home.res.apk", null);
			XModuleResources tweakRes = XModuleResources.createInstance(XMain.weather_apk, null);

			for (String item : Const.weather)
			{
				mapTweak.put(item, tweakRes.getIdentifier(item, "drawable", Const.WEATHER_PACKAGE_NAME));
				mapWeather.put(weatherRes.getIdentifier(item, "drawable", "com.htc.android.home.res"), item);
			}
		}
		catch (Throwable t)
		{
			return false;
		}

		return true;
	}

	public static void hookWeatherBitmap()
	{
		final XModuleResources tweakRes = XModuleResources.createInstance(XMain.weather_apk, null);

		final Class<?> traceClass = XposedHelpers.findClass("android.graphics.BitmapFactory", null);

		XposedHelpers.findAndHookMethod(traceClass, "decodeResource", "android.content.res.Resources", "int", "android.graphics.BitmapFactory.Options", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				try
				{
					int drwbl = (Integer) param.args[1];

					if (mapWeather.containsKey(drwbl))
					{
						StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

						if (stacktrace[6].getClassName().contains("com.htc.android.animation.timeline.weather"))
						{
							Drawable replace = tweakRes.getDrawable(mapTweak.get(mapWeather.get(drwbl)));
							if (replace != null)
								param.setResult(((BitmapDrawable) replace).getBitmap());
						}
					}
				}
				catch (Throwable t)
				{
					// XposedBridge.log(t);
				}
			}
		});

	}
}
