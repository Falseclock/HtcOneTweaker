package kz.virtex.htc.tweaker;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.util.Log;
import kz.virtex.htc.tweaker.mods.Android;
import kz.virtex.htc.tweaker.mods.Bugs;
import kz.virtex.htc.tweaker.mods.Camera;
import kz.virtex.htc.tweaker.mods.Contacts;
import kz.virtex.htc.tweaker.mods.Dialer;
import kz.virtex.htc.tweaker.mods.HTCSync;
import kz.virtex.htc.tweaker.mods.Keyboard;
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

	@SuppressLint("SdCardPath")
	public void initZygote(StartupParam startupParam) throws Throwable
	{
		pref = new XSharedPreferences(Const.PACKAGE_NAME, Const.PREFERENCE_FILE);

		weather_apk = pref.getString(Const.WEATHER_PACKAGE_APK, null);

		MODULE_PATH = startupParam.modulePath;

		if (pref.getBoolean(Const.TWEAK_ADB_NOTIFY, false))
			Services.hookUpdateAdbNotification();

		XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, null);

		if (pref.getBoolean(Const.TWEAK_CHARGING_LED, false))
			Services.hookUdateBatteryLight();

		if (pref.getBoolean(Const.TWEAK_CHARGING_FLASH, false))
			Services.hookFlashDuringPlugged();

		Services.hookForceSetFlashing(pref.getInt(Const.TWEAK_FLASH_TIMEOUT, 5));

		// Android.hookAndroidLog();

		if (Android.hookWeatherBitmapPreload())
			Android.hookWeatherBitmap();

		if (pref.getBoolean(Const.TWEAK_FIX_SDCARD_PERMISSION, false))
			Android.hookSDcardPermission();

		if (XMain.pref.getInt(Const.TWEAK_SLOT1_COLOR, 0) != 0 || XMain.pref.getInt(Const.TWEAK_SLOT2_COLOR, 0) != 0)
			Messaging.hookSetBadgeImageResource();

		if (pref.getBoolean(Const.TWEAK_INPUT_METHOD_NOTIFY, false))
			Services.hookInputMethodManagerService();

		if (pref.getBoolean(Const.TWEAK_DISABLE_ALL_CAPS, false))
			Android.hookAllCapsLocale();

		// KEEP this at least 3 version to remove old traces
		// System should'nt be touched
		try
		{
			SQLiteDatabase mydb = SQLiteDatabase.openDatabase("/data/data/com.htc.provider.CustomizationSettings/databases/customization_settings.db", null, SQLiteDatabase.OPEN_READWRITE);
			mydb.execSQL("UPDATE SettingTable set key='system_locale' WHERE key='tweak_system_locale'");
			mydb.close();
		}
		catch (SQLiteException e)
		{
			XposedBridge.log(e);
		}

		if (pref.getBoolean(Const.TWEAK_ENABLE_ALL_LANGUAGES, false))
			Settings.hookSystemLocales();

		if (pref.getBoolean(Const.TWEAK_DEBUG_ON, false))
			Android.hookDebugFlag();
		// Control.hookVolumeMediaButtons();
		
		if (Misc.isDual())
			Messaging.hookSendTextMessage();
		
		//Bugs.killServicesLocked();
		
		//Recorder.hookAudioRecord();
	}

	public void handleLoadPackage(LoadPackageParam paramLoadPackageParam) throws Throwable
	{
		String packageName = paramLoadPackageParam.packageName;

		// if (paramLoadPackageParam.processName.equals("android") &&
		// Integer.parseInt(XMain.pref.getString(Const.TWEAK_MEDIA_OPTION, "0"))
		// != 0) {
		// Control.execHook_VoluhookVolumeMediaButtonsmeMediaButtons(paramLoadPackageParam,
		// Integer.parseInt(XMain.pref.getString(Const.TWEAK_MEDIA_OPTION,
		// "0")));
		// Control.hookVolumeMediaButtons(paramLoadPackageParam);
		// }

		// TODO: hook own package to check is it active or not for Main activity
		// usage and checking
		if (packageName.equals("kz.virtex.htc.tweaker"))
		{

		}

		if (packageName.equals("com.android.internal.policy.impl"))
			Misc.x("-------com.android.internal.policy.impl");

		if (packageName.equals("com.android.internal.policy"))
			Misc.x("-------com.android.internal.policy");

		if (packageName.equals("com.android.internal"))
			Misc.x("-------com.android.internal");

		if (packageName.equals("com.android"))
			Misc.x("-------com.android");

		if (packageName.equals("android.net.sip"))
		{
			if (pref.getBoolean(Const.TWEAK_ENABLE_SIP, false))
				Phone.hookSIP2(paramLoadPackageParam);
		}

		if (packageName.equals("com.android.phone"))
		{
			if (Misc.isDual())
				Phone.hookCopyDialExtra(paramLoadPackageParam);
			
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
			//Recorder.hookIsEnableAudioRecord(paramLoadPackageParam);
		}

		if (packageName.equals("com.htc.htcdialer"))
		{
			if (XMain.pref.getInt(Const.TWEAK_SLOT1_COLOR, 0) != 0 || XMain.pref.getInt(Const.TWEAK_SLOT2_COLOR, 0) != 0)
				Dialer.hookCallButtons(paramLoadPackageParam);

			if (pref.getBoolean(Const.TWEAK_OLD_SENSE_DIALER, false))
				Dialer.hookSpecificHtcShowKeypad(paramLoadPackageParam);

			/*----------------*/
			/* DIALER BUTTONS */
			/*----------------*/
			if (pref.getBoolean(Const.TWEAK_DIALER_BUTTON, false))
				Dialer.hookDialerButtons(paramLoadPackageParam);

			if (Misc.isDual())
				Dialer.hookSimCallButton(paramLoadPackageParam);
		}

		if (packageName.equals("com.android.mms") || packageName.equals("com.htc.sense.mms"))
		{
			
			if (Misc.isDual())
				Messaging.hookSendSMSButton(paramLoadPackageParam, packageName);

			// Messaging.hookNotificationRemove(paramLoadPackageParam,
			// packageName);

			if (pref.getBoolean(Const.TWEAK_SMS_UNREAD_HIGHLIGHT, false))
				Messaging.hookUnread(paramLoadPackageParam, packageName);

			if (pref.getBoolean(Const.TWEAK_SMS_NOTIFY_TO_DIALOG, false))
				Messaging.hookUpdateNotification(paramLoadPackageParam, packageName);

			/*-----------------------*/
			/* DELIVERY NOTIFICATION */
			/*-----------------------*/
			if (pref.getBoolean(Const.TWEAK_DELIVERY_NOTIFICATION, false))
				Messaging.hookMessageNotification(paramLoadPackageParam, packageName);

			if (XMain.pref.getInt(Const.TWEAK_SLOT1_COLOR, 0) != 0 || XMain.pref.getInt(Const.TWEAK_SLOT2_COLOR, 0) != 0)
				Messaging.hookDualModeButtonStyle(paramLoadPackageParam, packageName);
		}

		if (packageName.equals("com.android.systemui"))
		{
			if (pref.getBoolean(Const.TWEAK_COLORED_SIM, false))
				SystemUI.handleColoredSIM(paramLoadPackageParam);

			if (pref.getBoolean(Const.TWEAK_MIUI_BATTERY, false))
				SystemUI.hookStatusBarMIUIBattery(paramLoadPackageParam);

			if (pref.getBoolean(Const.TWEAK_DISABLE_ALL_CAPS, false))
				SystemUI.hookDateCase(paramLoadPackageParam);

			if (pref.getBoolean(Const.TWEAK_STATUSBAR_CONDENSED, false))
				SystemUI.hookBarFont(paramLoadPackageParam);

			if (!pref.getBoolean(Const.TWEAK_STOCK_BATTERY, true))
				SystemUI.hookDisableStockBattery(paramLoadPackageParam);

			if (pref.getBoolean(Const.TWEAK_COLORED_BATTERY, false) && pref.getBoolean(Const.TWEAK_STOCK_BATTERY, true))
				SystemUI.hookBatteryController(paramLoadPackageParam);

			if (pref.getBoolean(Const.TWEAK_HEADS_UP_NOTIFICATION, false))
				SystemUI.hookUseHeadsUp(paramLoadPackageParam);

			/*----------------*/
			/* QUICK PULLDOWN */
			/*----------------*/
			if (!pref.getString(Const.TWEAK_QUICK_SETTINGS, "0").equals("0"))
				SystemUI.hookOnTouchEvent(paramLoadPackageParam, pref.getString(Const.TWEAK_QUICK_SETTINGS, "0"));

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

		if (packageName.equals("com.android.camera") || packageName.equals("com.htc.camera"))
		{
			// Camera.hookCameraActivity(paramLoadPackageParam);

			if (!pref.getString(Const.TWEAK_PHOTO_PREFIX, "0").equals("0"))
				Camera.hookCameraPrefix(paramLoadPackageParam, packageName, pref.getString(Const.TWEAK_PHOTO_PREFIX, "0"));
		}

		if (packageName.equals("com.htc.lockscreen"))
		{
			// LockScreen.hookOperatorName(paramLoadPackageParam);
		}
	}

	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable
	{
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
				Weather.handleColorWeather(resparam, weather_apk);
		}

		if (resparam.packageName.equals("com.htc.android.htcime") || resparam.packageName.equals("com.htc.sense.ime"))
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
