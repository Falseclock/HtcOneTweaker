package kz.virtex.htc.tweaker.mods;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Settings
{
	public static void hookUSBNotification(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod
		(
			"com.android.settings.PSService", 
			paramLoadPackageParam.classLoader, 
			"SetUSBNotification", 
			"android.content.Context",
			boolean.class, 
			new XC_MethodReplacement()
			{
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
				{
					return null;
				}
			}
		);
	}
	public static void hookGoogleSupport(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod
		(
			"com.htc.launcher.util.SettingUtil", 
			paramLoadPackageParam.classLoader, 
			"isGoogleApplicationsSupport", 
			"android.content.Context",
			new XC_MethodReplacement()
			{
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
				{
					Boolean ret = true;
					return ret;
				}
			}
		);
	}
}
