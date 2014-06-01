package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class LockScreen
{
	public static void hookOperatorName(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.htc.lockscreen.ui.HeadBar", paramLoadPackageParam.classLoader, "updateOperatorName", "java.lang.CharSequence", "java.lang.CharSequence", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				if (param.args[0] != null) 
					XposedBridge.log("1: " + param.args[0].toString());
				if (param.args[1] != null) 
					XposedBridge.log("2: " + param.args[1].toString());
			}
		});
		
		findAndHookMethod("com.htc.lockscreen.HtcKeyguardViewStateManager", paramLoadPackageParam.classLoader, "updateOperatorName", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				if (param.args[0] != null) 
					XposedBridge.log("1: " + param.args[0].toString());
				if (param.args[1] != null) 
					XposedBridge.log("2: " + param.args[1].toString());
			}
		});
	}
}
