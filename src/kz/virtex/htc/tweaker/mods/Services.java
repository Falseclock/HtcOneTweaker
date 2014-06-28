package kz.virtex.htc.tweaker.mods;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Services
{

	public static void hookInputMethodManagerService()
	{
		try
		{
			final Class<?> traceClass = XposedHelpers.findClass("com.android.server.InputMethodManagerService", null);

			XposedHelpers.findAndHookMethod(traceClass, "systemRunning", "com.android.server.StatusBarManagerService", new XC_MethodHook()
			{
				protected void afterHookedMethod(MethodHookParam param) throws Throwable
				{
					XposedHelpers.setObjectField(param.thisObject, "mNotificationManager", null);
				}
			});
		}
		catch (Throwable t)
		{
			XposedBridge.log(t);
		}
	}
	
	public static void hookFlashDuringPlugged()
	{
		try
		{
			final Class<?> traceClass = XposedHelpers.findClass("com.android.server.NotificationManagerService", null);

			XposedHelpers.findAndHookMethod(traceClass, "updateBatteryStatus", "android.content.Intent", new XC_MethodHook()
			{
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					XposedHelpers.setBooleanField(param.thisObject, "mFlashDuringPlugged", true);
				}
			});
		}
		catch (Throwable t)
		{
			XposedBridge.log(t);
		}
	}

	public static void hookForceSetFlashing(final int timeOut)
	{
		try
		{
			final Class<?> traceClass = XposedHelpers.findClass("com.android.server.NotificationManagerService", null);

			XposedHelpers.findAndHookMethod(traceClass, "forceSetFlashing", int.class, int.class, int.class, int.class, new XC_MethodHook()
			{
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable
				{
					int type = (Integer) param.args[1];
					if (type == 2)
					{
						param.args[2] = timeOut;
					}
				}
			});
		}
		catch (Throwable t)
		{
			XposedBridge.log(t);
		}
	}

	public static void hookUdateBatteryLight()
	{
		try
		{
			final Class<?> traceClass = XposedHelpers.findClass("com.android.server.NotificationManagerService", null);

			XposedHelpers.findAndHookMethod(traceClass, "updateBatteryLight", new XC_MethodReplacement()
			{
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
				{
					return XposedHelpers.getBooleanField(param.thisObject, "mCablePlugged");
				}
			});
		}
		catch (Throwable t)
		{
			XposedBridge.log(t);
		}
	}

	public static void hookUpdateAdbNotification()
	{
		try
		{
			final Class<?> traceClass = XposedHelpers.findClass("com.android.server.usb.UsbDeviceManager.UsbHandler", null);

			XposedHelpers.findAndHookMethod(traceClass, "updateAdbNotification", new XC_MethodReplacement()
			{

				@Override
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
				{
					return null;
				}

			});
		}
		catch (Throwable t)
		{
			XposedBridge.log(t);
		}
	}
}
