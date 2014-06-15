package kz.virtex.htc.tweaker;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.io.File;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.util.Log;
import kz.virtex.htc.tweaker.mods.Android;
import kz.virtex.htc.tweaker.mods.Camera;
import kz.virtex.htc.tweaker.mods.Contacts;
import kz.virtex.htc.tweaker.mods.Dialer;
import kz.virtex.htc.tweaker.mods.HTCSync;
import kz.virtex.htc.tweaker.mods.Keyboard;
import kz.virtex.htc.tweaker.mods.Launcher;
import kz.virtex.htc.tweaker.mods.LockScreen;
import kz.virtex.htc.tweaker.mods.Media;
import kz.virtex.htc.tweaker.mods.Messaging;
import kz.virtex.htc.tweaker.mods.Phone;
import kz.virtex.htc.tweaker.mods.Recorder;
import kz.virtex.htc.tweaker.mods.Services;
import kz.virtex.htc.tweaker.mods.Settings;
import kz.virtex.htc.tweaker.mods.SystemUI;
import kz.virtex.htc.tweaker.mods.Tweaker;
import kz.virtex.htc.tweaker.mods.Weather;
import kz.virtex.htc.tweaker.utils.ColorFilterGenerator;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

@SuppressWarnings("unused")
public class XMain implements IXposedHookInitPackageResources, IXposedHookZygoteInit, IXposedHookLoadPackage
{
	public static XSharedPreferences pref;
	public static String MODULE_PATH;
	public static String weather_apk;

	public void initZygote(StartupParam startupParam) throws Throwable
	{
		pref = new XSharedPreferences(Const.PACKAGE_NAME, Const.PREFERENCE_FILE);
		
		weather_apk = pref.getString(Const.WEATHER_PACKAGE_APK, null);

		MODULE_PATH = startupParam.modulePath;

		if (pref.getBoolean(Const.TWEAK_ADB_NOTIFY, false))
		{
			Services.hookUpdateAdbNotification();
		}

		XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, null);

		if (pref.getBoolean(Const.TWEAK_CHARGING_LED, false))
		{
			Services.hookUdateBatteryLight();
		}

		if (pref.getBoolean(Const.TWEAK_CHARGING_FLASH, false))
		{
			Services.hookFlashDuringPlugged();
		}

		Services.hookForceSetFlashing(pref.getInt(Const.TWEAK_FLASH_TIMEOUT, 5));

		if (Android.hookWeatherBitmapPreload())
		{
			Android.hookWeatherBitmap();
		}
		if (pref.getBoolean(Const.TWEAK_FIX_SDCARD_PERMISSION, false))
		{
			Android.hookSDcardPermission();
		}
		
		Messaging.hookSetBadgeImageResource();

	}

	public void handleLoadPackage(LoadPackageParam paramLoadPackageParam) throws Throwable
	{
		String packageName = paramLoadPackageParam.packageName;
		// TODO: hook own package to check is it active or not for Main activity
		// usage and checking
		if (packageName.equals("kz.virtex.htc.tweaker"))
		{
			
		}
		
		if (packageName.equals("android.net.sip"))
		{
			if (pref.getBoolean(Const.TWEAK_ENABLE_SIP, false))
				Phone.hookSIP2(paramLoadPackageParam);
		}
				
		if (packageName.equals("com.android.phone"))
		{
			if (pref.getBoolean(Const.TWEAK_ENABLE_SIP, false))
				Phone.hookSIP(paramLoadPackageParam);
			
			if (pref.getBoolean(Const.TWEAK_DISABLE_DATA_ROAM_NOTIFY, false))
				Phone.hookShowDataDisconnectedRoaming(paramLoadPackageParam);
			
			/*----------------*/
			/* CALL RECORDING */
			/*----------------*/
			Recorder.hookEnableCallRecording(paramLoadPackageParam);
			Recorder.hookAutomateCallRecording(paramLoadPackageParam);
			Recorder.hookAutomateCallRecordingFilename(paramLoadPackageParam);
		}
		if (packageName.equals("com.htc.soundrecorder"))
		{
			/*--------------------*/
			/* RECORDING FILENAME */
			/*--------------------*/
			Recorder.getStorageRoot(paramLoadPackageParam);
			Recorder.hookPausableAudioRecorderStart(paramLoadPackageParam);
		}
		if (packageName.equals("com.htc.htcdialer"))
		{
			/*----------------*/
			/* DIALER BUTTONS */
			/*----------------*/
			if (pref.getBoolean(Const.TWEAK_DIALER_BUTTON, false))
				Dialer.hookDialerButtons(paramLoadPackageParam);
		}
		if (packageName.equals("com.android.mms") || packageName.equals("com.htc.sense.mms"))
		{
			if (pref.getBoolean(Const.TWEAK_SMS_UNREAD_HIGHLIGHT, false))
				Messaging.hookUnread(paramLoadPackageParam, packageName);
			
			if (pref.getBoolean(Const.TWEAK_SMS_NOTIFY_TO_DIALOG, false))
				Messaging.hookUpdateNotification(paramLoadPackageParam, packageName);
			
			/*-----------------------*/
			/* DELIVERY NOTIFICATION */
			/*-----------------------*/
			if (pref.getBoolean(Const.TWEAK_DELIVERY_NOTIFICATION, false))
				Messaging.hookMessageNotification(paramLoadPackageParam, packageName);
		}
		if (packageName.equals("com.android.systemui"))
		{
			/*----------------*/
			/* QUICK PULLDOWN */
			/*----------------*/
			if (pref.getBoolean(Const.TWEAK_QUICK_PULLDOWN, false))
				SystemUI.hookOnTouchEvent(paramLoadPackageParam);

			/*------------------------*/
			/* EXPANDED NOTIFICATIONS */
			/*------------------------*/
			if (pref.getBoolean(Const.TWEAK_EXPANDED_NOTIFICATIONS, false))
				SystemUI.hookExpandedNotifications(paramLoadPackageParam);
		}
		if (packageName.equals("com.nero.android.htc.sync"))
		{
			if (pref.getBoolean(Const.TWEAK_SYNC_NOTIFY, false))
				HTCSync.hookHTCSyncNotification(paramLoadPackageParam);
		}

		if (packageName.equals("com.android.settings"))
		{
			if (pref.getBoolean(Const.TWEAK_USB_NOTIFY, false))
				Settings.hookUSBNotification(paramLoadPackageParam);
		}
		if (packageName.equals("com.android.providers.media"))
		{
			if (pref.getBoolean(Const.TWEAK_MTP_NOTIFY, false))
				Media.hookMPTNotification(paramLoadPackageParam);
		}

		if (packageName.equals("com.android.camera"))
		{
			//Camera.hookCameraActivity(paramLoadPackageParam);
			
			if (pref.getBoolean(Const.TWEAK_ENABLE_PHOTO_PREFIX, false))
				Camera.hookCameraPrefix(paramLoadPackageParam);
		}
		if (packageName.equals("com.htc.lockscreen"))
		{
			// LockScreen.hookOperatorName(paramLoadPackageParam);
		}

	}

	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable
	{
		if (resparam.packageName.equals("com.htc.launcher"))
		{
			//Launcher.execHook_HomeScreenGridSize(resparam, MODULE_PATH);
		}
		
		if (resparam.packageName.equals("com.android.mms") || resparam.packageName.equals("com.htc.sense.mms"))
		{
			if (pref.getBoolean(Const.TWEAK_SMS_HIDE_BADGE, false))
				Messaging.hookContactBadge(resparam);
			
			if (pref.getInt(Const.TWEAK_SLOT1_COLOR, 0) != 0)
				Phone.handleSlotIndicator1(resparam, MODULE_PATH, pref.getInt(Const.TWEAK_SLOT1_COLOR, 0));
			
			if (pref.getInt(Const.TWEAK_SLOT2_COLOR, 0) != 0)
				Phone.handleSlotIndicator2(resparam, MODULE_PATH, pref.getInt(Const.TWEAK_SLOT2_COLOR, 0));
		}
		
		if (resparam.packageName.equals("com.android.phone"))
		{
			if (pref.getInt(Const.TWEAK_SLOT1_COLOR, 0) != 0)
				Phone.handleSlotIndicator1(resparam, MODULE_PATH, pref.getInt(Const.TWEAK_SLOT1_COLOR, 0));
			
			if (pref.getInt(Const.TWEAK_SLOT2_COLOR, 0) != 0)
				Phone.handleSlotIndicator2(resparam, MODULE_PATH, pref.getInt(Const.TWEAK_SLOT2_COLOR, 0));
		}
		
		if (resparam.packageName.equals("com.htc.htcdialer"))
		{
			if (pref.getBoolean(Const.TWEAK_COLOR_CALL_INDICATOR, false))
				Dialer.handleCallDirections(resparam, MODULE_PATH);
			
			if (pref.getInt(Const.TWEAK_SLOT1_COLOR, 0) != 0)
				Phone.handleSlotIndicator1(resparam, MODULE_PATH, pref.getInt(Const.TWEAK_SLOT1_COLOR, 0));
			
			if (pref.getInt(Const.TWEAK_SLOT2_COLOR, 0) != 0)
				Phone.handleSlotIndicator2(resparam, MODULE_PATH, pref.getInt(Const.TWEAK_SLOT2_COLOR, 0));
		}
		
		if (resparam.packageName.equals("com.htc.contacts"))
		{
			if (pref.getInt(Const.TWEAK_SLOT1_COLOR, 0) != 0)
				Phone.handleSlotIndicator1(resparam, MODULE_PATH, pref.getInt(Const.TWEAK_SLOT1_COLOR, 0));
			
			if (pref.getInt(Const.TWEAK_SLOT2_COLOR, 0) != 0)
				Phone.handleSlotIndicator2(resparam, MODULE_PATH, pref.getInt(Const.TWEAK_SLOT2_COLOR, 0));	
			
			if (pref.getBoolean(Const.TWEAK_COLOR_CALL_INDICATOR, false))
				Contacts.handleCallDirections(resparam, MODULE_PATH);
		}
		
		if (resparam.packageName.equals("com.htc.weather.res"))
		{
			if (pref.getBoolean(Const.TWEAK_COLORED_WEATHER, false))
			{
				Weather.handleColorWeather(resparam, weather_apk);
			}
		}

		if (resparam.packageName.equals("com.htc.android.htcime"))
		{
			if (pref.getBoolean(Const.TWEAK_POPUP_KEYBOARD, false))
				Keyboard.handlePopup(resparam, MODULE_PATH);
		}
		
		
		if (resparam.packageName.equals("com.android.systemui"))
		{
			if (pref.getBoolean(Const.TWEAK_COLORED_SIM, false))
				SystemUI.handleColoredSIM(resparam, MODULE_PATH);

			if (pref.getBoolean(Const.TWEAK_COLORED_WIFI, false))
				SystemUI.handleColoredWiFi(resparam, MODULE_PATH);

			if (pref.getBoolean(Const.TWEAK_DATA_ICONS, false))
				SystemUI.handleColoredData(resparam, MODULE_PATH);

		}
	}
}
