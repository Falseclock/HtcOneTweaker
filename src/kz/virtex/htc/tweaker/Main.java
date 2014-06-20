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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;

import com.htc.preference.HtcEditTextPreference;
import com.htc.preference.HtcPreference;
import com.htc.preference.HtcPreference.OnPreferenceFirstBindViewListener;
import com.htc.preference.HtcPreferenceActivity;
import com.htc.preference.HtcPreferenceCategory;
import com.htc.preference.HtcPreferenceScreen;
import com.htc.preference.HtcSwitchPreference;
import com.htc.widget.HtcAlertDialog;

@SuppressLint("DefaultLocale")
public class Main extends HtcPreferenceActivity implements HtcPreference.OnPreferenceChangeListener
{
	public static SharedPreferences preferences;
	private Context mContext;
	@SuppressLint(
	{ "WorldReadableFiles", "WorldWriteableFiles", "DefaultLocale" })
	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);
		mContext = this;
		
		preferences = getSharedPreferences(Const.PREFERENCE_FILE, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
		addPreferencesFromResource(R.xml.settings);

		// If Xposed not installet, let's do not confuse
		// users with working application and non working tweaks
		if (!Misc.isPackageInstalled("de.robv.android.xposed.installer", this))
		{
			new HtcAlertDialog.Builder(this)
		    .setTitle(R.string.app_error)
		    .setMessage(R.string.no_xposed)
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	((Activity) mContext).finish();
		        }
		     })
		     .show();
		}
		else
		{
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
		setupCallRecording();
		setupRecordingTypeFilter();
		setupRecordingAutoCaller();
		setupRecordingDelete();

		setupSimSettings();
		setupDataIcons();
		setupWeather();
		setupWiFiIcons();
		storeWeatherApkPath();
		setupNetworkManager("slot_1_user_text", R.string.preferred_network_1);
		setupNetworkManager("slot_2_user_text", R.string.preferred_network_2);
		setupSense6();
		setupDUAL();
		setupCamera();
		setupSlotSaturation();
		Misc.cleanUp();
	}

	private void setupSlotSaturation()
	{
		if (Misc.isSense6())
		{
			final SeekBarPreference slot1 = (SeekBarPreference) findPreference(Const.TWEAK_SLOT1_COLOR);
			final SeekBarPreference slot2 = (SeekBarPreference) findPreference(Const.TWEAK_SLOT2_COLOR);

			Misc.adjustHue(slot1.getIcon(), Misc.getHueValue(preferences.getInt(Const.TWEAK_SLOT1_COLOR, 0)));
			Misc.adjustHue(slot2.getIcon(), Misc.getHueValue(preferences.getInt(Const.TWEAK_SLOT2_COLOR, 0)));

			slot1.setOnSeekBarTrackListener(new SeekBarPreference.OnSeekBarTrackListener()
			{
				@Override
				public void onSeekBarTrack(SeekBar paramSeekBar, int value)
				{
					Misc.adjustHue(slot1.getIcon(), Misc.getHueValue(value));
					//paramSeekBar.setBackgroundColor(Misc.colorTransform(-13388315, Misc.getHueValue(value)));
				}
			});

			slot2.setOnSeekBarTrackListener(new SeekBarPreference.OnSeekBarTrackListener()
			{
				@Override
				public void onSeekBarTrack(SeekBar paramSeekBar, int value)
				{
					Misc.adjustHue(slot2.getIcon(), Misc.getHueValue(value));
					//paramSeekBar.setBackgroundColor(Misc.colorTransform(-13128336, Misc.getHueValue(value)));
				}
			});
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void setupCamera()
	{
		HtcSwitchPreference tweak = (HtcSwitchPreference) findPreference(Const.TWEAK_ENABLE_PHOTO_PREFIX);
		CharSequence summ = tweak.getSummary();
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		tweak.setSummary(date + "_" + summ);
	}

	private void setupSense6()
	{
		if (!Misc.isSense6())
		{
			HtcPreferenceScreen preferenceScreen = (HtcPreferenceScreen) findPreference(Const.DUAL_SETTINGS_SCREEN_KEY);
			preferenceScreen.removePreference(findPreference(Const.ICON_INDICATOR_SLOT_SCREEN));
		}
	}

	private void setupDUAL()
	{
		if (!Misc.isDual())
		{
			HtcPreferenceScreen preferenceScreen = (HtcPreferenceScreen) findPreference(Const.OTHER_SETTINGS_SCREEN_KEY);
			preferenceScreen.removePreference(findPreference(Const.TWEAK_ENABLE_SIP));

			preferenceScreen = (HtcPreferenceScreen) findPreference(Const.SYSTEM_SETTINGS_SCREEN_KEY);
			preferenceScreen.removePreference(findPreference(Const.DUAL_SETTINGS_SCREEN_KEY));
		}
	}

	private void storeWeatherApkPath()
	{
		PackageManager pm = getPackageManager();
		ApplicationInfo ai;
		try
		{
			ai = pm.getApplicationInfo(Const.WEATHER_PACKAGE_NAME, 0);
			String sourceApk = ai.publicSourceDir;
			preferences.edit().putString(Const.WEATHER_PACKAGE_APK, sourceApk).commit();

		} catch (NameNotFoundException e)
		{

		}
	}

	protected void onResume()
	{
		super.onResume();

		final HtcSwitchPreference weatherPref = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_WEATHER);
		
		if (Misc.isPackageInstalled(Const.WEATHER_PACKAGE_NAME, weatherPref.getContext()))
		{
			weatherPref.setChecked(true);
			putBoolean(Const.TWEAK_COLORED_WEATHER, true);
			storeWeatherApkPath();

		} else
		{
			weatherPref.setChecked(false);
			putBoolean(Const.TWEAK_COLORED_WEATHER, false);
		}
		
	}

	private void setupWeather()
	{
		final HtcSwitchPreference weatherPref = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_WEATHER);

		weatherPref.setOnPreferenceFirstBindViewListener(new OnPreferenceFirstBindViewListener()
		{
			public void onPreferenceFirstBindView(HtcPreference preference)
			{
				if (!Misc.isPackageInstalled(Const.WEATHER_PACKAGE_NAME, preference.getContext()))
				{
					// weatherPref.setEnabled(false);
					weatherPref.setChecked(false);
					putBoolean(Const.TWEAK_COLORED_WEATHER, false);
				} else
				{
					storeWeatherApkPath();
				}
			}
		});

		weatherPref.setOnPreferenceClickListener(new HtcPreference.OnPreferenceClickListener()
		{
			public boolean onPreferenceClick(HtcPreference preference)
			{
				if (!Misc.isPackageInstalled(Const.WEATHER_PACKAGE_NAME, preference.getContext()))
				{
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

		final HtcPreferenceScreen preferenceScreen = (HtcPreferenceScreen) findPreference(Const.DATA_SCREEN_KEY);

		HtcSwitchPreference wifiTweak = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_WIFI);
		final IconsColorPreference wifiTweakColor = (IconsColorPreference) findPreference(Const.TWEAK_COLORED_WIFI_COLOR);

		wifiTweakColor.setColor("#FFFFFF");

		if (!wifiTweak.isChecked())
		{
			preferenceScreen.removePreference(wifiTweakColor);
		}

		wifiTweak.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				Integer value = 0;
				if (object.toString() == "true")
					value = 1;

				if (value == 1)
				{
					preferenceScreen.addPreference(wifiTweakColor);
				} else
				{
					preferenceScreen.removePreference(wifiTweakColor);
				}
				return true;
			}
		});
	}

	private void setupSimSettings()
	{
		final HtcPreferenceScreen preferenceScreen = (HtcPreferenceScreen) findPreference(Const.DATA_SCREEN_KEY);

		final HtcPreferenceScreen colorSimScreen = (HtcPreferenceScreen) findPreference(Const.COLOR_SIM_SCREEN);
		HtcSwitchPreference tweakColorSim = (HtcSwitchPreference) findPreference(Const.TWEAK_COLORED_SIM);

		if (!tweakColorSim.isChecked())
		{
			preferenceScreen.removePreference(colorSimScreen);
		} else
		{
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

				if (value == 1)
				{
					preferenceScreen.addPreference(colorSimScreen);
				} else
				{
					preferenceScreen.removePreference(colorSimScreen);
				}
				return true;
			}
		});
	}

	private void setupDataIcons()
	{
		Misc.applyTheme(findPreference(Const.TWEAK_DATA_ICONS_COLOR).getIcon(), Const.TWEAK_DATA_ICONS_COLOR, preferences);

		final HtcPreferenceScreen preferenceScreen = (HtcPreferenceScreen) findPreference(Const.DATA_SCREEN_KEY);

		HtcSwitchPreference dataTweak = (HtcSwitchPreference) findPreference(Const.TWEAK_DATA_ICONS);
		final IconsColorPreference dataTweakColor = (IconsColorPreference) findPreference(Const.TWEAK_DATA_ICONS_COLOR);

		dataTweakColor.setColor("#FFFFFF");

		if (!dataTweak.isChecked())
		{
			preferenceScreen.removePreference(dataTweakColor);
		}

		dataTweak.setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				Integer value = 0;
				if (object.toString() == "true")
					value = 1;

				if (value == 1)
				{
					preferenceScreen.addPreference(dataTweakColor);
				} else
				{
					preferenceScreen.removePreference(dataTweakColor);
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
		int caller = Integer.parseInt(preferences.getString(Const.TWEAK_CALL_REC_AUTO_CALLER, "0"));
		final String[] RecordingCaller = getResources().getStringArray(R.array.RecordingCaller);
		findPreference(Const.TWEAK_CALL_REC_AUTO_CALLER).setSummary(RecordingCaller[caller]);

		findPreference(Const.TWEAK_CALL_REC_AUTO_CALLER).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				int index = Integer.parseInt(object.toString());
				preference.setSummary(RecordingCaller[index]);
				Settings.System.putInt(preference.getContext().getContentResolver(), Const.TWEAK_CALL_REC_AUTO_CALLER, index);
				return true;
			}
		});
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
		if (!auto_rec.isEnabled())
		{
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

				if (value == 1)
				{
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

				for (int i = 0; i < rows.size(); i++)
				{
					Settings.System.putInt(getContentResolver(), Const.TWEAK_CALL_REC_AUTO_FILTER + "_" + rows.get(i).getKey(), Misc.toInt(rows.get(i).getState()));
				}

				return false;
			}
		});
	}

	private void changeFilterSummary(MultiCheckPreference pref, ArrayList<Row> rows)
	{
		ArrayList<String> summs = new ArrayList<String>();

		for (int i = 0; i < rows.size(); i++)
		{
			if (rows.get(i).getState() == true)
			{
				summs.add(rows.get(i).getSummary());
			}
		}
		StringBuilder builder = new StringBuilder();
		pref.setSummary(builder.append(summs));
	}

	private void setupNetworkManager(final String key, int standart)
	{
		HtcEditTextPreference slot = (HtcEditTextPreference) findPreference(key);

		String slot_name = Settings.System.getString(getContentResolver(), key);

		if (TextUtils.isEmpty(slot_name))
		{
			slot.setSummary(standart);
			putString(key, getResources().getString(standart)); // save for
																// coloring
		} else
		{
			slot.setSummary(slot_name);
			putString(key, slot_name); // save for coloring
		}

		findPreference(key).setOnPreferenceChangeListener(new HtcPreference.OnPreferenceChangeListener()
		{
			public boolean onPreferenceChange(HtcPreference preference, Object object)
			{
				String name = object.toString();
				preference.setSummary(name);
				Settings.System.putString(getContentResolver(), key, name);
				putString(key, name);
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
}
