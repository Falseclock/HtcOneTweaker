package kz.virtex.htc.tweaker;

public class Const
{
	public static final boolean DEBUG = false;
	public static final String TAG = "HTC_Tweaker";
	
	public static final String PACKAGE_NAME = "kz.virtex.htc.tweaker";
	public static final String WEATHER_PACKAGE_NAME = "kz.virtex.htc.tweaker.weather";
	public static final String WEATHER_PACKAGE_APK = "tweak_weather_pack_apk";
	
	public static final String PREFERENCE_FILE = "kz.virtex.htc.tweaker_preferences";

	// No need restart tweaks
	public static final String TWEAK_CALL_REC = "tweak_call_recording"; 
	public static final String TWEAK_CALL_REC_AUTO = "tweak_call_recording_auto"; 
	public static final String TWEAK_CALL_REC_AUTO_STORAGE = "tweak_call_recording_auto_storage"; 
	public static final String TWEAK_CALL_REC_AUTO_DEL_CNT = "tweak_call_recording_auto_delete_count"; 
	public static final String TWEAK_CALL_REC_AUTO_FILTER = "tweak_call_recording_auto_filter"; 
	public static final String TWEAK_CALL_REC_AUTO_FILTER_IN = "tweak_call_recording_auto_filter_in"; 
	public static final String TWEAK_CALL_REC_AUTO_FILTER_OUT = "tweak_call_recording_auto_filter_out"; 
	public static final String TWEAK_CALL_REC_AUTO_CALLER = "tweak_call_recording_auto_caller"; 
	public static final String TWEAK_CALL_REC_AUTO_DELETE = "tweak_call_recording_auto_delete"; 
	public static final String TWEAK_CALL_REC_AUTO_DELETE_INTERVAL = "tweak_call_recording_auto_delete_interval"; 
	public static final String TWEAK_CALL_REC_AUTO_DELETE_COUNT = "tweak_call_recording_auto_delete_count"; 
	public static final String TWEAK_CALL_REC_AUTO_LAST_DELETE = "tweak_call_recording_auto_last_delete"; 
	
	// FULL restart tweaks
	public static final String TWEAK_ENABLE_ALL_LANGUAGES= "tweak_enable_all_languages";
	public static final String TWEAK_FIX_SDCARD_PERMISSION = "tweak_fix_sdcard_permission"; 
	public static final String TWEAK_ADB_NOTIFY = "tweak_adb_notify";
	public static final String TWEAK_SYNC_NOTIFY = "tweak_sync_notify";
	public static final String TWEAK_USB_NOTIFY = "tweak_usb_notify";
	public static final String TWEAK_MTP_NOTIFY = "tweak_mtp_notify";
	public static final String TWEAK_CHARGING_LED = "tweak_charging_led";
	public static final String TWEAK_CHARGING_FLASH = "tweak_charging_flash";
	public static final String TWEAK_FLASH_TIMEOUT = "tweak_flash_timeout";
	public static final String TWEAK_INPUT_METHOD_NOTIFY = "tweak_input_method_notify";
	public static final String TWEAK_DISABLE_ALL_CAPS = "tweak_disable_all_caps";
	public static final String TWEAK_MEDIA_KEY_UP = "tweak_media_key_up";
	public static final String TWEAK_MEDIA_KEY_DOWN = "tweak_media_key_down";
	public static final String TWEAK_MEDIA_OPTION = "tweak_media_option";
	public static final String TWEAK_LOGCAT_FILTER = "tweak_logcat_filter";
	
	
	
	// SOFT restart tweaks
	public static final String TWEAK_ENABLE_SIP = "tweak_enable_SIP"; 
	public static final String TWEAK_DISABLE_DATA_ROAM_NOTIFY = "tweak_roaming_data_notify"; 
	public static final String TWEAK_SLOT1_COLOR = "tweak_slot1_color"; 
	public static final String TWEAK_SLOT2_COLOR = "tweak_slot2_color"; 
	public static final String TWEAK_HEADS_UP_NOTIFICATION = "tweak_heads_up_notifications"; 
	public static final String TWEAK_DIALER_BUTTON = "tweak_dialer_btn"; 
	public static final String TWEAK_DIALER_BUTTON_COLOR = "tweak_dialer_btn_color"; 
	public static final String TWEAK_DIALER_BUTTON_SIZE = "tweak_dialer_btn_size"; 
	public static final String TWEAK_DIALER_BUTTON_COLOR_LAT = "tweak_dialer_btn_color_lat"; 
	public static final String TWEAK_DIALER_BUTTON_SIZE_LAT = "tweak_dialer_btn_size_lat"; 
	public static final String TWEAK_COLORED_WEATHER = "tweak_colored_weather";
	public static final String TWEAK_POPUP_KEYBOARD = "tweak_popup_keyboard";
	public static final String TWEAK_COLORED_SIM = "tweak_colored_sim";
	public static final String TWEAK_DELIVERY_NOTIFICATION = "tweak_delivery_notification";
	public static final String TWEAK_EXPANDED_NOTIFICATIONS = "tweak_expanded_notifications";
	public static final String TWEAK_COLOR_SIM1 = "tweak_color_sim1";
	public static final String TWEAK_COLOR_SIM2 = "tweak_color_sim2";
	public static final String TWEAK_QUICK_PULLDOWN = "tweak_quick_pulldown";
	public static final String TWEAK_DATA_ICONS = "tweak_data_icons";
	public static final String TWEAK_DATA_ICONS_COLOR = "tweak_data_icons_color";
	public static final String TWEAK_COLORED_WIFI = "tweak_colored_wifi";
	public static final String TWEAK_COLORED_WIFI_COLOR = "tweak_colored_wifi_color";
	public static final String TWEAK_SMS_UNREAD_HIGHLIGHT = "tweak_sms_unread_highlight";
	public static final String TWEAK_SMS_NOTIFY_TO_DIALOG = "tweak_sms_notify_to_dialog";
	public static final String TWEAK_SMS_HIDE_BADGE = "tweak_sms_hide_badge";
	public static final String TWEAK_COLOR_CALL_INDICATOR= "tweak_color_call_indicator";
	public static final String TWEAK_ENABLE_PHOTO_PREFIX= "tweak_enable_photo_prefix";
	public static final String TWEAK_OLD_SENSE_DIALER= "tweak_old_sense_dialer";
	public static final String TWEAK_COLORED_BATTERY= "tweak_colored_battery";
	public static final String TWEAK_STATUSBAR_CONDENSED = "tweak_statusbar_condensed";
	public static final String TWEAK_MIUI_BATTERY = "tweak_miui_battery";
	
	
	public static final String GUI_SCREEN_KEY = "gui_screen_key";
	public static final String DATA_SCREEN_KEY = "data_screen_key";
	public static final String CONTACT_DATA_SCREEN_KEY = "contact_data_screen_key";
	public static final String DUAL_SETTINGS_SCREEN_KEY = "dual_settings_screen";
	public static final String SYSTEM_SETTINGS_SCREEN_KEY = "system_settings_screen";
	public static final String TWEAK_CALL_REC_AUTO_SCREEN = "autorecording_settings_screen"; 
	public static final String TWEAK_CALL_REC_AUTO_DELETE_CAT = "autorecording_delete_cat"; 
	public static final String MEDIA_CONTROL_CAT = "media_control_cat"; 
	
	
	
	
	public static final int[] tweak_color_sim1 = { R.drawable.cdma_stat_sys_s1_5signal_0, R.drawable.cdma_stat_sys_s1_5signal_1, R.drawable.cdma_stat_sys_s1_5signal_2, R.drawable.cdma_stat_sys_s1_5signal_3, R.drawable.cdma_stat_sys_s1_5signal_4, R.drawable.cdma_stat_sys_s1_5signal_5, R.drawable.cdma_stat_sys_s1_5signal_null };
	public static final int[] tweak_color_sim2 = { R.drawable.cdma_stat_sys_s2_5signal_0, R.drawable.cdma_stat_sys_s2_5signal_1, R.drawable.cdma_stat_sys_s2_5signal_2, R.drawable.cdma_stat_sys_s2_5signal_3, R.drawable.cdma_stat_sys_s2_5signal_4, R.drawable.cdma_stat_sys_s2_5signal_5, R.drawable.cdma_stat_sys_s2_5signal_null };
	public static final int[] tweak_data_icons_color = { R.drawable.stat_sys_data_out_2g, R.drawable.stat_sys_data_out_3g, R.drawable.stat_sys_data_out_4g, R.drawable.stat_sys_data_out_e, R.drawable.stat_sys_data_out_g, R.drawable.stat_sys_data_out_h, R.drawable.stat_sys_data_out_hplus, R.drawable.stat_sys_data_out_lte };
	public static final int[] tweak_colored_wifi_color = { R.drawable.stat_sys_wifi_signal_0, R.drawable.stat_sys_wifi_signal_1, R.drawable.stat_sys_wifi_signal_2, R.drawable.stat_sys_wifi_signal_3, R.drawable.stat_sys_wifi_signal_4 };
	
	public static final String COLOR_SIM_SCREEN = "color_sim_screen";
	public static final String OTHER_SETTINGS_SCREEN_KEY = "other_settings_screen";
	public static final String ICON_INDICATOR_SLOT_SCREEN= "icon_indicator_slot_screen";
	
		
	public static final String AUTO_REC_MAIN = "Automatic records/";
	public static final String AUTO_REC_INCOMING = AUTO_REC_MAIN + "/Incoming/";
	public static final String AUTO_REC_OUTGOING = AUTO_REC_MAIN + "/Outgoing/";
	
	
	public static float DENSITY = 3.0F;
	
	public static final int DIALER_BUTTON_STOCK_COLOR = 0xffa0a0a0;
	public static final float DIALER_BUTTON_STOCK_SIZE = 40.0F;
	
	public static final int[] HtcDialerAnimationButtonMode = { R.attr.backgroundMode, R.attr.number, R.attr.number_style, R.attr.alphabet, R.attr.alphabet_style, R.attr.phoneticAlphabet, R.attr.primaryIcon, R.attr.secondaryIcon, R.attr.isForthRow, R.attr.isContentMultiply,
		R.attr.mutiplyBackgroundColor, R.attr.measureSelf, R.attr.primaryAllCaps };

	public static final String[] weather = {"moon", "sun", "cloud_l", "cloud_l_mask", "cloud_m", "cloud_m_mask", "cloud_s", "cloud_s_mask", "cloud_solid_l", "cloud_solid_m", "cloud_solid_s", "cloud_xl", "cloud_xl_mask", "cloud_xs", "cloud_xs_mask", "freezing_rain", "full_moon", "haze", "leaf", "raindrop", "showerdrop", "sleet", "snow", "sun_hot", "sun_hot_shadow", "sun_mask", "sun_shadow", "thunder_m", "thunder_s" };
	
	public static int[] getImg(String name)
	{
		if (name.equals("tweak_color_sim1"))
			return tweak_color_sim1;
		
		if (name.equals("tweak_color_sim2"))
			return tweak_color_sim2;
		
		if (name.equals("tweak_data_icons_color"))
			return tweak_data_icons_color;
		
		if (name.equals("tweak_colored_wifi_color"))
			return tweak_colored_wifi_color;
		
		return new int[0];
	}
}
