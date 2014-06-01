package kz.virtex.htc.tweaker.mods;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Media
{
	public static void hookMPTNotification(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod
		(
			"com.android.providers.media.MtpService", 
			paramLoadPackageParam.classLoader, 
			"updateMTPNotification", 
			new XC_MethodReplacement()
			{
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
				{
					return null;
				}
			}
		);
	}
}
