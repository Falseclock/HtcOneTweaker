package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.lang.reflect.Constructor;

import kz.virtex.htc.tweaker.Misc;
import android.content.Context;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Settings
{
	public static void hookSystemLocales()
	{
		try
		{
			final Class<?> HtcCustomizedStorage = XposedHelpers.findClass("com.htc.customize.storage.HtcCustomizedStorage", null);

			findAndHookMethod(HtcCustomizedStorage, "get", Context.class, String.class, String.class, new XC_MethodHook()
			{
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					String str1 = (String) param.args[1];
					String str2 = (String) param.args[2];

					Misc.x("HtcCustomizedStorage invoke: " + str1 + "_" + str2);

					if (str1.equals("system") && str2.equals("locale"))
					{
						Misc.x("HtcCustomizedStorage invoke: hooking system_language");

						Class<?> HtcCustomData = XposedHelpers.findClass("com.htc.customize.storage.customize.HtcCustomizedData", null);

						Constructor<?> ctor = HtcCustomData.getConstructor(Bundle.class);

						Object object = ctor.newInstance(new Object[]
						{ null });

						param.setResult(object);
					}
				}
			});
		}
		catch (Throwable t)
		{
			XposedBridge.log(t);
		}
	}

	public static void hookUSBNotification(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.settings.PSService", paramLoadPackageParam.classLoader, "SetUSBNotification", "android.content.Context", boolean.class, new XC_MethodReplacement()
		{
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				return null;
			}
		});
	}

	public static void hookGoogleSupport(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.htc.launcher.util.SettingUtil", paramLoadPackageParam.classLoader, "isGoogleApplicationsSupport", "android.content.Context", new XC_MethodReplacement()
		{
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				Boolean ret = true;
				return ret;
			}
		});
	}
}
