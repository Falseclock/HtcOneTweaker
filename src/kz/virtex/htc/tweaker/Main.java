package kz.virtex.htc.tweaker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import kz.virtex.htc.tweaker.preference.IconsColorPreference;
import kz.virtex.htc.tweaker.preference.MultiCheckPreference;
import kz.virtex.htc.tweaker.preference.MultiCheckPreference.Row;
import kz.virtex.htc.tweaker.preference.NumberPickerPreference;
import kz.virtex.htc.tweaker.preference.SeekBarPreference;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.htc.preference.HtcEditTextPreference;
import com.htc.preference.HtcListPreference;
import com.htc.preference.HtcPreference;
import com.htc.preference.HtcPreference.OnPreferenceFirstBindViewListener;
import com.htc.preference.HtcPreferenceActivity;
import com.htc.preference.HtcPreferenceCategory;
import com.htc.preference.HtcPreferenceScreen;
import com.htc.preference.HtcSwitchPreference;
import com.htc.widget.HtcAlertDialog;

@SuppressLint("DefaultLocale")
public class Main extends HtcPreferenceActivity implements HtcPreference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener
{
	public static SharedPreferences preferences;
	private TweakerBroadcastReceiver broadcastReceiverInstance = null;
	private static ArrayList<String> mSettingsChanges = new ArrayList<String>();
	private static NotificationManager mNotifyMgr;
	private static String DEBUG = "Main";

	@SuppressWarnings("unused")
	private void test()
	{
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(TweakerBroadcastReceiver.ACTION_DELETE_MESSAGE);
		intentFilter.addAction(TweakerBroadcastReceiver.ACTION_CALL_TO_CONTACT);
		intentFilter.addAction(TweakerBroadcastReceiver.ACTION_REPLY_MESSAGE);

		broadcastReceiverInstance = new TweakerBroadcastReceiver();
		registerReceiver(broadcastReceiverInstance, intentFilter);

		Intent intentCallToContact = new Intent();
		intentCallToContact.setAction(TweakerBroadcastReceiver.ACTION_CALL_TO_CONTACT);
		intentCallToContact.putExtra("ContactId", Long.parseLong(String.valueOf("123")));
		intentCallToContact.putExtra("Sender", "Wow!");
		PendingIntent mPintentCallToContact = PendingIntent.getBroadcast(this, 12345, intentCallToContact, 0);

		Notification.Builder localBuilder = new Notification.Builder(this);
		localBuilder.setSmallIcon(R.drawable.notification_icon);
		localBuilder.setContentTitle("Test Title");
		localBuilder.setContentText("message text gere");
		localBuilder.setStyle(new Notification.BigTextStyle().bigText("message text gere"));
		localBuilder.addAction(0, "Удалить", mPintentCallToContact);
		Notification localNotification = localBuilder.build();

		mNotifyMgr.notify(12345, localNotification);

	}

	@SuppressLint(
	{ "WorldReadableFiles", "WorldWriteableFiles", "DefaultLocale" })
	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);

		mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		preferences = getSharedPreferences(Const.PREFERENCE_FILE, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);

		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

		addPreferencesFromResource(R.xml.settings);

		// If Xposed not installed, let's do not confuse
		// users with working application and non working tweaks
		if (!Misc.isPackageInstalled("de.robv.android.xposed.installer", this)) {
			new HtcAlertDialog.Builder(this).setTitle(R.string.app_error).setMessage(R.string.no_xposed).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					Uri uriUrl = Uri.parse("http://forum.xda-developers.com/showthread.php?p=53011963");
					Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
					startActivity(launchBrowser);
					Main.this.finish();
				}
			}).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					Main.this.finish();
				}
			}).show();
		} else {
			startService(new Intent(this, TweakerService.class).setAction(TweakerService.ACTION_CLEANUP_RECORDS));
			init();
		}
	}

	public boolean onCreateOptionsMenu(Menu paramMenu)
	{
		super.onCreateOptionsMenu(paramMenu);

		paramMenu.add(Menu.NONE, 101, Menu.NONE, "Info").setIcon(R.drawable.ic_menu_info_details).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem paramMenuItem)
	{
		switch (paramMenuItem.getItemId())
		{
			case 101:
				Intent intent = new Intent(this, AboutActivity.class);
				startActivity(intent);
				break;
		}
		return true;
	}

	private void init()
	{
		// Log.d(DEBUG,"init");
		// test();

		setupCallRecording();
		setupRecordingTypeFilter();
		setupRecordingAutoCaller();
		setupRecordingAutoSlot();
		setupRecordingDelete();

		setupSimSettings();
		setupDataIcons();
		setupWeather();
		setupWiFiIcons();
		storeWeatherApkPath();
		setupNetworkManager("slot_1_user_text", R.string.preferred_network_1);
		setupNetworkManager("slot_2_user_text", R.string.preferred_network_2);
		//setupNetworkManager("spn_1_user_text", R.string.spn_default);
		//setupNetworkManager("spn_2_user_text", R.string.spn_default);
		setupSense6();
		setupDUAL();
		setupCamera();
		setupSlotSaturation();
		// setupMediaKey();
		// setupLogact();
		setupBattery();
		setupQS();
		if (Misc.isDual()) {
			catchSimShowAction(Const.TWEAK_SHOW_SIM_CARD_DIAL_ACTION);
			catchSimShowAction(Const.TWEAK_SHOW_SIM_CARD_MESS_ACTION);

			setupSimShow(Const.TWEAK_SHOW_SIM_CARD_DIAL, Const.SIM_CARD_SEL_DIAL_SCREEN, Const.TWEAK_SHOW_SIM_CARD_DIAL_ACTION);
			setupSimShow(Const.TWEAK_SHOW_SIM_CARD_MESS, Const.SIM_CARD_SEL_MESS_SCREEN, Const.TWEAK_SHOW_SIM_CARD_MESS_ACTION);

			setupForceActivity(Const.SIM_CARD_FORCE_MESS_CAT, Const.TWEAK_FORCE_MESS, Const.TWEAK_FORCE_MESS_ACTION, Const.TWEAK_SHOW_SIM_CARD_MESS, Const.TWEAK_SHOW_SIM_CARD_MESS_ACTION);
			setupForceActivity(Const.SIM_CARD_FORCE_DIAL_CAT, Const.TWEAK_FORCE_DIAL, Const.TWEAK_FORCE_DIAL_ACTION, Const.TWEAK_SHOW_SIM_CARD_DIAL, Const.TWEAK_SHOW_SIM_CARD_DIAL_ACTION);
		}
	}

	private void setupForceActivity(String category_key, final String force_key, final String action_key, String multi_key, final String multi_key_action)
	{
		final HtcPreferenceCategory category = (HtcPreferenceCategory) findPreference(category_key);
		HtcListPreference force_pref = (HtcListPreference) findPreference(force_key);
		final HtcListPreference action_pref = (HtcListPreference) findPreference(action_key);
		// set on change listener

		final MultiCheckPreference multiSelector = (MultiCheckPreference) findPreference(multi_key);
		final HtcListPreference multiSelectorAct = (HtcListPreference) findPreference(multi_key_action);

		int force_value = Settings.System.getInt(getContentResolver(), force_key, 0);
		int action_value = Settings.System.getInt(getContentResolver(), action_key, 0);

		if (force_value == 0) {
			category.removePreference(action_pref);
		} else {
			// if forece value not null, then action value is in place
			if (multiSelectorAct != null) {
				multiSelectorAct.setValue(String.valueOf(action_value));
				multiSelectorAct.setEnabled(false);
			}
		}
		Settings.System.putInt(getBaseContext().getContentResolver(), multi_key_action, action_value);

		refreshDependSelector(multiSelector, multiSelectorAct, force_value, category, action_pref);

		force_pref.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(HtcPreference pref, Object object)
			{
				int value = Integer.parseInt(object.toString());
				refreshDependSelector(multiSelector, multiSelectorAct, value, category, action_pref);
				Settings.System.putInt(pref.getContext().getContentResolver(), force_key, value);
				return true;
			}
		});

		action_pref.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(HtcPreference pref, Object object)
			{
				int value = Integer.parseInt(object.toString());
				if (multiSelectorAct != null) {
					Settings.System.putInt(getBaseContext().getContentResolver(), multi_key_action, value);
					multiSelectorAct.setValue(String.valueOf(value));
				}
				Settings.System.putInt(pref.getContext().getContentResolver(), action_key, value);
				return true;
			}
		});
	}

	private void refreshDependSelector(MultiCheckPreference multiSelector, HtcListPreference multiSelectorAct, int value, HtcPreferenceCategory category, HtcListPreference action_pref)
	{
		if (value == 0) {
			category.removePreference(action_pref);
			// We do not remember how previously was set
			multiSelector.setAllChecked();
			multiSelector.setEnabled(true);
			if (multiSelectorAct != null)
				multiSelectorAct.setEnabled(true);
		} else {
			category.addPreference(action_pref);
			// Set filtering value and disable option
			multiSelector.setAllUnChecked();
			multiSelector.setChecked(value - 1);
			multiSelector.setEnabled(false);
			if (multiSelectorAct != null)
				multiSelectorAct.setEnabled(false);
		}
	}

	private void catchSimShowAction(final String option_key)
	{
		final HtcListPreference action = (HtcListPreference) findPreference(option_key);

		action.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				int index = Integer.parseInt(object.toString());
				Settings.System.putInt(preference.getContext().getContentResolver(), option_key, index);
				return true;
			}
		});
	}

	private void setupSimShow(final String key, final String screen_key, final String option_key)
	{
		final String[] optional = new String[2];
		String slot1_name = Settings.System.getString(getContentResolver(), "slot_1_user_text");
		String slot2_name = Settings.System.getString(getContentResolver(), "slot_2_user_text");

		if (TextUtils.isEmpty(slot1_name))
			optional[0] = getResources().getString(R.string.preferred_network_1);
		else
			optional[0] = slot1_name;

		if (TextUtils.isEmpty(slot2_name))
			optional[1] = getResources().getString(R.string.preferred_network_2);
		else
			optional[1] = slot2_name;

		final MultiCheckPreference pref = (MultiCheckPreference) findPreference(key);
		int total = changeFilterSummary(pref, pref.rows, optional);
		final HtcListPreference action = (HtcListPreference) findPreference(option_key);

		final HtcPreferenceCategory screen = (HtcPreferenceCategory) findPreference(screen_key);

		if (total == pref.rows.size()) {
			screen.removePreference(action);
		}

		pref.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			@SuppressWarnings("unchecked")
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				int total = changeFilterSummary((MultiCheckPreference) preference, (ArrayList<Row>) object, optional);

				if (total == pref.rows.size()) {
					screen.removePreference(action);
				} else {
					screen.addPreference(action);
					catchSimShowAction(option_key);
				}

				ArrayList<Row> rows = (ArrayList<Row>) object;

				for (int i = 0; i < rows.size(); i++) {
					Settings.System.putInt(getContentResolver(), key + "_" + rows.get(i).getKey(), Misc.toInt(rows.get(i).getState()));
				}

				return true;
			}
		});
	}

	private void setupQS()
	{
		final HtcPreferenceCategory screen = (HtcPreferenceCategory) findPreference(Const.QUICK_SETTINGS_CAT);
		final HtcListPreference side = (HtcListPreference) findPreference(Const.TWEAK_QUICK_SETTINGS_SIDE);
		HtcListPreference qs = (HtcListPreference) findPreference(Const.TWEAK_QUICK_SETTINGS);
		if (preferences.getString(Const.TWEAK_QUICK_SETTINGS, "0").equals("0")) {
			screen.removePreference(side);
		}

		qs.setOnPreferenceChangeListener(new HtcListPreference.OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(HtcPreference arg0, Object object)
			{
				if (object.toString().equals("0"))
					screen.removePreference(side);
				else
					screen.addPreference(side);
				return true;
			}
		});
	}

	private void setupBattery()
	{
		HtcSwitchPreference colorBat = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_BATTERY);
		colorBat.setDependency(Const.TWEAK_STOCK_BATTERY);
	}

	private void setupLogact()
	{
		MultiCheckPreference logcat = (MultiCheckPreference) findPreference(Const.TWEAK_LOGCAT_FILTER);
		logcat.unsetMinimum();
	}

	private void setupMediaKey()
	{
		final HtcPreferenceCategory mediaCategory = (HtcPreferenceCategory) findPreference(Const.MEDIA_CONTROL_CAT);
		int mediaOptionVal = Integer.parseInt(preferences.getString(Const.TWEAK_MEDIA_OPTION, "0"));
		final HtcListPreference mediaOptionPref = (HtcListPreference) findPreference(Const.TWEAK_MEDIA_OPTION);
		final HtcListPreference mediaKeyUpPref = (HtcListPreference) findPreference(Const.TWEAK_MEDIA_KEY_UP);
		final HtcListPreference mediaKeyDownPref = (HtcListPreference) findPreference(Const.TWEAK_MEDIA_KEY_DOWN);

		if (mediaOptionVal == 0) {
			mediaCategory.removePreference(mediaKeyUpPref);
			mediaCategory.removePreference(mediaKeyDownPref);
		}
		mediaOptionPref.setOnPreferenceChangeListener(new HtcListPreference.OnPreferenceChangeListener()
		{
			@Override
			public boolean onPreferenceChange(HtcPreference arg0, Object object)
			{
				Integer value = Integer.parseInt(object.toString());

				if (value != 0) {
					mediaCategory.addPreference(mediaKeyUpPref);
					mediaCategory.addPreference(mediaKeyDownPref);
				} else {
					mediaCategory.removePreference(mediaKeyUpPref);
					mediaCategory.removePreference(mediaKeyDownPref);
				}
				return true;
			}
		});
	}

	private void setupSlotSaturation()
	{
		if (Misc.isSense6() && Misc.isDual()) {
			final SeekBarPreference slot1 = (SeekBarPreference) findPreference(Const.TWEAK_SLOT1_COLOR);

			Misc.adjustHue(slot1.getIcon(), Misc.getHueValue(preferences.getInt(Const.TWEAK_SLOT1_COLOR, 0)));

			slot1.setOnSeekBarTrackListener(new SeekBarPreference.OnSeekBarTrackListener()
			{
				@Override
				public void onSeekBarTrack(SeekBar paramSeekBar, int value)
				{
					Misc.adjustHue(slot1.getIcon(), Misc.getHueValue(value));
					// paramSeekBar.setBackgroundColor(Misc.colorTransform(-13388315,
					// Misc.getHueValue(value)));
				}
			});

			final SeekBarPreference slot2 = (SeekBarPreference) findPreference(Const.TWEAK_SLOT2_COLOR);
			Misc.adjustHue(slot2.getIcon(), Misc.getHueValue(preferences.getInt(Const.TWEAK_SLOT2_COLOR, 0)));

			slot2.setOnSeekBarTrackListener(new SeekBarPreference.OnSeekBarTrackListener()
			{
				@Override
				public void onSeekBarTrack(SeekBar paramSeekBar, int value)
				{
					Misc.adjustHue(slot2.getIcon(), Misc.getHueValue(value));
					// paramSeekBar.setBackgroundColor(Misc.colorTransform(-13128336,
					// Misc.getHueValue(value)));
				}
			});
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void setupCamera()
	{
		final HtcPreferenceCategory screen = (HtcPreferenceCategory) findPreference(Const.SCREEN_MEDIA);
		final HtcListPreference tweak = (HtcListPreference) findPreference(Const.TWEAK_PHOTO_PREFIX);
		final HtcListPreference position = (HtcListPreference) findPreference(Const.TWEAK_PHOTO_PREFIX_POSITION);

		tweak.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				String value = object.toString();

				if (!value.equals("0")) {
					screen.addPreference(position);
					String date = new SimpleDateFormat(value).format(new Date());
					int posVal = Integer.parseInt(position.getValue());
					String[] tokens = getResources().getString(R.string.enablePhotoPrefixSummary).split("\\.(?=[^\\.]+$)");
					String ext = tokens[1];
					String file = tokens[0];

					if (posVal == 0) {
						tweak.setSummary(date + "_" + file + "." + ext);
					} else {
						tweak.setSummary(file + "_" + date + "." + ext);
					}
				} else {
					tweak.setSummary(getResources().getString(R.string.prefix_option_0));
					screen.removePreference(position);
				}

				return true;
			}
		});

		position.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				int posVal = Integer.parseInt(object.toString());
				String date = new SimpleDateFormat(tweak.getValue()).format(new Date());

				String[] tokens = getResources().getString(R.string.enablePhotoPrefixSummary).split("\\.(?=[^\\.]+$)");
				String ext = tokens[1];
				String file = tokens[0];

				if (posVal == 0) {
					tweak.setSummary(date + "_" + file + "." + ext);
				} else {
					tweak.setSummary(file + "_" + date + "." + ext);
				}
				return true;
			}
		});

		String value = preferences.getString(Const.TWEAK_PHOTO_PREFIX, "0");
		if (!value.equals("0")) {
			String date = new SimpleDateFormat(value).format(new Date());
			int posVal = Integer.parseInt(position.getValue());
			String[] tokens = getResources().getString(R.string.enablePhotoPrefixSummary).split("\\.(?=[^\\.]+$)");
			String ext = tokens[1];
			String file = tokens[0];

			if (posVal == 0) {
				tweak.setSummary(date + "_" + file + "." + ext);
			} else {
				tweak.setSummary(file + "_" + date + "." + ext);
			}
		} else {
			screen.removePreference(position);
		}
	}

	private void setupSense6()
	{
		if (!Misc.isSense6()) {
			HtcPreferenceScreen preferenceScreen = (HtcPreferenceScreen) findPreference(Const.DUAL_SETTINGS_SCREEN_KEY);
			preferenceScreen.removePreference(findPreference(Const.ICON_INDICATOR_SLOT_SCREEN));

			HtcPreferenceCategory preferenceCat = (HtcPreferenceCategory) findPreference(Const.OTHER_SETTINGS_SCREEN_KEY);
			preferenceCat.removePreference(findPreference(Const.TWEAK_OLD_SENSE_DIALER));
		}
	}

	private void setupDUAL()
	{
		if (!Misc.isDual()) {
			HtcPreferenceCategory preferenceCat = (HtcPreferenceCategory) findPreference(Const.OTHER_SETTINGS_SCREEN_KEY);
			preferenceCat.removePreference(findPreference(Const.TWEAK_ENABLE_SIP));

			preferenceCat = (HtcPreferenceCategory) findPreference(Const.SETTINGS_KEY_CAT);
			preferenceCat.removePreference(findPreference(Const.DUAL_SETTINGS_SCREEN_KEY));
		}
	}

	private void storeWeatherApkPath()
	{
		PackageManager pm = getPackageManager();
		ApplicationInfo ai;
		try {
			ai = pm.getApplicationInfo(Const.WEATHER_PACKAGE_NAME, 0);
			String sourceApk = ai.publicSourceDir;
			preferences.edit().putString(Const.WEATHER_PACKAGE_APK, sourceApk).commit();

		} catch (NameNotFoundException e) {

		}
	}

	protected void onResume()
	{
		// Log.d(DEBUG,"onResume");
		super.onResume();

		// remove restart notification on resume
		mNotifyMgr.cancel(001);

		if (broadcastReceiverInstance != null) {
			unregisterReceiver(broadcastReceiverInstance);
		}

		final HtcSwitchPreference weatherPref = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_WEATHER);

		if (Misc.isPackageInstalled(Const.WEATHER_PACKAGE_NAME, weatherPref.getContext())) {
			// weatherPref.setChecked(true);
			// putBoolean(Const.TWEAK_COLORED_WEATHER, true);
			storeWeatherApkPath();
		} else {
			// weatherPref.setChecked(false);
			// putBoolean(Const.TWEAK_COLORED_WEATHER, false);
		}
	}

	protected void onPause()
	{
		// Log.d(DEBUG,"onPause");
		if (broadcastReceiverInstance != null) {
			// unregisterReceiver(broadcastReceiverInstance);
		}
		super.onPause();
		checkRestartRequired();
	}

	protected void onStop()
	{
		// Log.d(DEBUG,"onStop");
		super.onStop();
	}

	protected void onDestroy()
	{
		// Log.d(DEBUG,"onDestroy");
		super.onDestroy();
		checkRestartRequired();
	}

	private void setupWeather()
	{
		final HtcSwitchPreference weatherPref = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_WEATHER);

		weatherPref.setOnPreferenceFirstBindViewListener(new OnPreferenceFirstBindViewListener()
		{
			public void onPreferenceFirstBindView(HtcPreference preference)
			{
				if (!Misc.isPackageInstalled(Const.WEATHER_PACKAGE_NAME, preference.getContext())) {
					// weatherPref.setEnabled(false);
					weatherPref.setChecked(false);
					putBoolean(Const.TWEAK_COLORED_WEATHER, false);
				} else {
					storeWeatherApkPath();
				}
			}
		});

		weatherPref.setOnPreferenceClickListener(new HtcPreference.OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(HtcPreference preference)
			{
				if (!Misc.isPackageInstalled(Const.WEATHER_PACKAGE_NAME, preference.getContext())) {
					new AlertDialog.Builder(preference.getContext()).setTitle(R.string.dialog_weather_title).setMessage(R.string.dialog_weather_alert).setNeutralButton(R.string.dialog_download, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface arg0, int arg1)
						{

							Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + Const.WEATHER_PACKAGE_NAME));
							marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							startActivity(marketIntent);

						}
					}).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface arg0, int arg1)
						{

						}
					}).show();

					weatherPref.setChecked(false);
					putBoolean(Const.TWEAK_COLORED_WEATHER, false);
					return false;
				}
				return true;
			}
		});
	}

	@SuppressWarnings("unused")
	private void setupRecordingDeleteCount()
	{
		final NumberPickerPreference saveLast = (NumberPickerPreference) findPreference(Const.TWEAK_CALL_REC_AUTO_DELETE_COUNT);
	}

	private void setupWiFiIcons()
	{
		Misc.applyTheme(findPreference(Const.TWEAK_COLORED_WIFI_COLOR).getIcon(), Const.TWEAK_COLORED_WIFI_COLOR, preferences);
		Misc.applyTheme(findPreference(Const.TWEAK_COLORED_WIFI_COLOR).getIcon(), Const.TWEAK_COLORED_WIFI_COLOR, preferences);

		final HtcPreferenceCategory preferenceCategory = (HtcPreferenceCategory) findPreference(Const.SCREEN_ICONS_COLORING);

		HtcSwitchPreference wifiTweak = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_WIFI);
		final IconsColorPreference wifiTweakColor = (IconsColorPreference) findPreference(Const.TWEAK_COLORED_WIFI_COLOR);

		wifiTweakColor.setColor("#FFFFFF");

		if (!wifiTweak.isChecked()) {
			preferenceCategory.removePreference(wifiTweakColor);
		}

		wifiTweak.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				Integer value = 0;
				if (object.toString() == "true")
					value = 1;

				if (value == 1) {
					preferenceCategory.addPreference(wifiTweakColor);
				} else {
					preferenceCategory.removePreference(wifiTweakColor);
				}
				return true;
			}
		});
	}

	private void setupSimSettings()
	{
		final HtcPreferenceCategory preferenceCategory = (HtcPreferenceCategory) findPreference(Const.SCREEN_ICONS_COLORING);

		final HtcPreferenceScreen colorSimScreen = (HtcPreferenceScreen) findPreference(Const.COLOR_SIM_SCREEN);
		HtcSwitchPreference tweakColorSim = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_SIM);

		if (!tweakColorSim.isChecked()) {
			preferenceCategory.removePreference(colorSimScreen);
		} else {
			Misc.applyTheme(findPreference(Const.COLOR_SIM_SCREEN).getIcon(), Const.TWEAK_COLOR_SIM1, preferences);
			Misc.applyTheme(findPreference(Const.TWEAK_COLOR_SIM1).getIcon(), Const.TWEAK_COLOR_SIM1, preferences);
			Misc.applyTheme(findPreference(Const.TWEAK_COLOR_SIM2).getIcon(), Const.TWEAK_COLOR_SIM2, preferences);
		}
		/*
		 * findPreference(Const.TWEAK_COLOR_SIM1).setOnPreferenceChangeListener(new
		 * HtcPreference.OnPreferenceChangeListener() {
		 * 
		 * @Override public boolean onPreferenceChange(HtcPreference arg0,
		 * Object arg1) {
		 * Misc.applyTheme(findPreference(Const.COLOR_SIM_SCREEN).getIcon(),
		 * Const.TWEAK_COLOR_SIM1); return false; } });
		 */
		tweakColorSim.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				Integer value = 0;
				if (object.toString() == "true")
					value = 1;

				if (value == 1) {
					preferenceCategory.addPreference(colorSimScreen);
				} else {
					preferenceCategory.removePreference(colorSimScreen);
				}
				return true;
			}
		});
	}

	private void setupDataIcons()
	{
		Misc.applyTheme(findPreference(Const.TWEAK_DATA_ICONS_COLOR).getIcon(), Const.TWEAK_DATA_ICONS_COLOR, preferences);

		final HtcPreferenceCategory preferenceCategory = (HtcPreferenceCategory) findPreference(Const.SCREEN_ICONS_COLORING);

		HtcSwitchPreference dataTweak = (HtcSwitchPreference) findPreference(Const.TWEAK_DATA_ICONS);
		final IconsColorPreference dataTweakColor = (IconsColorPreference) findPreference(Const.TWEAK_DATA_ICONS_COLOR);

		dataTweakColor.setColor("#FFFFFF");

		if (!dataTweak.isChecked()) {
			preferenceCategory.removePreference(dataTweakColor);
		}

		dataTweak.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				Integer value = 0;
				if (object.toString() == "true")
					value = 1;

				if (value == 1) {
					preferenceCategory.addPreference(dataTweakColor);
				} else {
					preferenceCategory.removePreference(dataTweakColor);
				}
				return true;
			}
		});
	}

	private void setupRecordingDelete()
	{
		final NumberPickerPreference countPreference = (NumberPickerPreference) findPreference(Const.TWEAK_CALL_REC_AUTO_DELETE_COUNT);
		countPreference.setEnabled(true);
		final NumberPickerPreference intervalPreference = (NumberPickerPreference) findPreference(Const.TWEAK_CALL_REC_AUTO_DELETE_INTERVAL);
		intervalPreference.setEnabled(true);

		final HtcPreferenceCategory preferenceScreen = (HtcPreferenceCategory) findPreference(Const.TWEAK_CALL_REC_AUTO_DELETE_CAT);
		preferenceScreen.removePreference(countPreference);
		preferenceScreen.removePreference(intervalPreference);

		int deleteValue = Integer.parseInt(preferences.getString(Const.TWEAK_CALL_REC_AUTO_DELETE, "0"));
		final String[] RecordingDelete = getResources().getStringArray(R.array.RecordingDelete);
		findPreference(Const.TWEAK_CALL_REC_AUTO_DELETE).setSummary(RecordingDelete[deleteValue]);

		switch (deleteValue)
		{
			case 1:
				preferenceScreen.addPreference(countPreference);
				countPreference.setEnabled(true);
				break;
			case 2:
				preferenceScreen.addPreference(intervalPreference);
				intervalPreference.setEnabled(true);
				break;
		}

		findPreference(Const.TWEAK_CALL_REC_AUTO_DELETE).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				int index = Integer.parseInt(object.toString());
				preference.setSummary(RecordingDelete[index]);
				Settings.System.putInt(preference.getContext().getContentResolver(), Const.TWEAK_CALL_REC_AUTO_DELETE, index);

				switch (index)
				{
					case 0:
						preferenceScreen.removePreference(countPreference);
						preferenceScreen.removePreference(intervalPreference);
						break;

					case 1:
						preferenceScreen.addPreference(countPreference);
						countPreference.setEnabled(true);
						preferenceScreen.removePreference(intervalPreference);
						break;

					case 2:
						preferenceScreen.removePreference(countPreference);
						preferenceScreen.addPreference(intervalPreference);
						intervalPreference.setEnabled(true);
						break;
				}
				return true;
			}
		});
	}

	private void setupRecordingAutoCaller()
	{
		findPreference(Const.TWEAK_CALL_REC_AUTO_CALLER).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				int index = Integer.parseInt(object.toString());
				Settings.System.putInt(preference.getContext().getContentResolver(), Const.TWEAK_CALL_REC_AUTO_CALLER, index);
				return true;
			}
		});
	}

	private void setupRecordingAutoSlot()
	{
		if (Misc.isDual()) {
			findPreference(Const.TWEAK_CALL_REC_AUTO_SLOT).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
			{
				public boolean onPreferenceChange(HtcPreference preference, Object object)
				{
					int index = Integer.parseInt(object.toString());
					Settings.System.putInt(preference.getContext().getContentResolver(), Const.TWEAK_CALL_REC_AUTO_SLOT, index);
					return true;
				}
			});
		} else {
			HtcPreferenceCategory preferenceCategory = (HtcPreferenceCategory) findPreference(Const.RECORDING_FILTER_CAT);
			preferenceCategory.removePreference(findPreference(Const.TWEAK_CALL_REC_AUTO_SLOT));
		}
	}

	private void setupCallRecording()
	{
		findPreference(Const.TWEAK_CALL_REC_AUTO).setDependency(Const.TWEAK_CALL_REC);

		findPreference(Const.TWEAK_CALL_REC_AUTO).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				Integer value = 0;
				if (object.toString() == "true")
					value = 1;
				Settings.System.putInt(preference.getContext().getContentResolver(), Const.TWEAK_CALL_REC_AUTO, value);

				findPreference(Const.TWEAK_CALL_REC_AUTO_SCREEN).setEnabled(object.toString() == "true");
				return true;
			}
		});

		final HtcSwitchPreference auto_rec = (HtcSwitchPreference) findPreference(Const.TWEAK_CALL_REC_AUTO);

		findPreference(Const.TWEAK_CALL_REC_AUTO_SCREEN).setEnabled(auto_rec.isChecked());
		if (!auto_rec.isEnabled()) {
			findPreference(Const.TWEAK_CALL_REC_AUTO_SCREEN).setEnabled(false);
		}

		findPreference(Const.TWEAK_CALL_REC).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				Integer value = 0;
				if (object.toString() == "true")
					value = 1;
				Settings.System.putInt(preference.getContext().getContentResolver(), Const.TWEAK_CALL_REC, value);

				findPreference(Const.TWEAK_CALL_REC_AUTO_SCREEN).setEnabled(object.toString() == "true");

				if (value == 1) {
					findPreference(Const.TWEAK_CALL_REC_AUTO_SCREEN).setEnabled(auto_rec.isChecked());
				}
				return true;
			}
		});

		int storage = Integer.parseInt(preferences.getString(Const.TWEAK_CALL_REC_AUTO_STORAGE, "1"));
		final String[] RecordingStorage = getResources().getStringArray(R.array.RecordingStorage);

		findPreference(Const.TWEAK_CALL_REC_AUTO_STORAGE).setSummary(RecordingStorage[storage]);

		findPreference(Const.TWEAK_CALL_REC_AUTO_STORAGE).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				int index = Integer.parseInt(object.toString());
				preference.setSummary(RecordingStorage[index]);

				Settings.System.putInt(getContentResolver(), Const.TWEAK_CALL_REC_AUTO_STORAGE, index);

				return true;
			}
		});
	}

	private void setupRecordingTypeFilter()
	{
		final MultiCheckPreference pref = (MultiCheckPreference) findPreference(Const.TWEAK_CALL_REC_AUTO_FILTER);
		changeFilterSummary(pref, pref.rows);

		findPreference(Const.TWEAK_CALL_REC_AUTO_FILTER).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			@SuppressWarnings("unchecked")
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				changeFilterSummary((MultiCheckPreference) preference, (ArrayList<Row>) object);

				ArrayList<Row> rows = (ArrayList<Row>) object;

				for (int i = 0; i < rows.size(); i++) {
					Settings.System.putInt(getContentResolver(), Const.TWEAK_CALL_REC_AUTO_FILTER + "_" + rows.get(i).getKey(), Misc.toInt(rows.get(i).getState()));
				}

				return true;
			}
		});
	}

	private int changeFilterSummary(MultiCheckPreference pref, ArrayList<Row> rows, String... optional)
	{
		int total = 0;
		ArrayList<String> summs = new ArrayList<String>();

		for (int i = 0; i < rows.size(); i++) {
			if (rows.get(i).getState() == true) {
				total++;
				if (optional.length > 0 && optional[i] != null)
					summs.add(optional[i]);
				else
					summs.add(rows.get(i).getSummary());
			}
		}
		StringBuilder builder = new StringBuilder();
		pref.setSummary(builder.append(summs));

		return total;
	}

	private void setupNetworkManager(final String key, final int standart)
	{
		HtcEditTextPreference slot = (HtcEditTextPreference) findPreference(key);

		String slot_name = Settings.System.getString(getContentResolver(), key);

		if (TextUtils.isEmpty(slot_name)) {
			slot.setSummary(standart);
			putString(key, getResources().getString(standart)); // save for
																// coloring
		} else {
			slot.setSummary(slot_name);
			putString(key, slot_name); // save for coloring
		}

		findPreference(key).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				String name = object.toString();
				if (name.length() > 0) {
					preference.setSummary(name);
					Settings.System.putString(getContentResolver(), key, name);
					putString(key, name);
				} else {
					preference.setSummary(standart);
					Settings.System.putString(getContentResolver(), key, null);
					preferences.edit().remove(name);
				}
				return true;
			}
		});
	}

	public static void putBoolean(String name, int value)
	{
		preferences.edit().putBoolean(name, Misc.toBoolean(value)).commit();
	}

	public static void putBoolean(String name, boolean value)
	{
		preferences.edit().putBoolean(name, value).commit();
	}

	public static void putInt(String name, int value)
	{
		preferences.edit().putInt(name, value).commit();
	}

	public static void putFloat(String name, float value)
	{
		preferences.edit().putFloat(name, value).commit();
	}

	public static void putString(String name, String value)
	{
		preferences.edit().putString(name, value).commit();
	}

	@Override
	public boolean onPreferenceChange(HtcPreference preference, Object object)
	{
		return false;
	}

	private void checkRestartRequired()
	{
		if (mSettingsChanges.size() > 0) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.notification_icon).setContentTitle(getResources().getString(R.string.restartRequired)).setContentText(getResources().getString(R.string.restartRequiredSummary))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(getResources().getString(R.string.restartRequiredSummary)));

			mNotifyMgr.notify(001, mBuilder.build());
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pref, String key)
	{
		// Call recording works on the fly
		if (!key.contains("tweak_call_rec") && !key.contains("tweak_show_sim")) { // &&
																					// !key.contains("tweak_force")
			// let's remove cause we are switched back
			if (mSettingsChanges.contains(key)) {
				// before removing we should check if it is not integer
				// if param not exist, then value will be -99999999
				// then we should delete it
				int intChecker;
				long longChecker;
				float floatChecker;
				String strChecker;

				// surraund with try/catch case we can not intercast
				try {
					intChecker = pref.getInt(key, -99976999);
				} catch (ClassCastException e) {
					intChecker = -99976999;
				}

				try {
					longChecker = pref.getLong(key, 12345678910L);
				} catch (ClassCastException e) {
					longChecker = 12345678910L;
				}

				try {
					floatChecker = pref.getFloat(key, -99976999.0F);
				} catch (ClassCastException e) {
					floatChecker = -99976999.0F;
				}

				try {
					strChecker = pref.getString(key, "UnaVaiLabLe");
				} catch (ClassCastException e) {
					strChecker = "UnaVaiLabLe";
				}

				if (intChecker == -99976999 && longChecker == 12345678910L && strChecker.equals("UnaVaiLabLe") && floatChecker == -99976999.0F) {
					// it means we changing boolean value
					// so let's remove
					mSettingsChanges.remove(key);
					mSettingsChanges.trimToSize();
				}
			} else {
				mSettingsChanges.add(key);
			}
		}
		// Log.d("onPreferenceChange", key);
	}
}
