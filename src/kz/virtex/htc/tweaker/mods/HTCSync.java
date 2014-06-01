package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HTCSync
{
	public static void hookHTCSyncNotification(final LoadPackageParam paramLoadPackageParam)
	{
		try
		{
			findAndHookMethod
			(
				"com.nero.android.htc.sync.CDMountReceiver", 
				paramLoadPackageParam.classLoader, 
				"showNotification", 
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
		catch (Throwable t)
		{

		}
	}
}
