package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import android.content.Context;
import android.content.Intent;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.drawable.Drawable;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Phone
{
	private static Object DualCallSettingsPreference;
	private static Object CallFeaturesSetting;

	public static void hookCopyDialExtra(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.phone.PhoneUtils", paramLoadPackageParam.classLoader, "copyDialExtra", Intent.class, Intent.class, new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				Intent paramIntent2 = (Intent) param.args[1];
				
				Class<?> PhoneGlobals = XposedHelpers.findClass("com.android.phone.PhoneGlobals", paramLoadPackageParam.classLoader);
				Object getInstance = XposedHelpers.callStaticMethod(PhoneGlobals, "getInstance");
				Context mContext = (Context) XposedHelpers.callMethod(getInstance, "getApplicationContext");

				int force = Misc.getSystemSettingsInt(mContext, Const.TWEAK_FORCE_DIAL, 0);

				if (force != 0) {
					Class<?> PhoneUtils = XposedHelpers.findClass("com.android.phone.PhoneUtils", paramLoadPackageParam.classLoader);
					
					int action = Misc.getSystemSettingsInt(mContext, Const.TWEAK_FORCE_DIAL_ACTION, 0);
					int phoneType;

					if (force == 1)
						phoneType = (Integer) XposedHelpers.callStaticMethod(PhoneUtils, "getSlot1PhoneType");
					else
						phoneType = (Integer) XposedHelpers.callStaticMethod(PhoneUtils, "getSlot2PhoneType");

					// dial through available
					if (action == 0) {
						// check if slot is available
						if ((Boolean) XposedHelpers.callStaticMethod(PhoneUtils, "isSimReady", phoneType)) {
							// dial through desired slot
							paramIntent2.putExtra("phone_type", phoneType);
						} else {
							// slot not available, so lets get state of another slot
							int anotherSlot;
							if (force == 1)
								anotherSlot = (Integer) XposedHelpers.callStaticMethod(PhoneUtils, "getSlot2PhoneType");
							else
								anotherSlot = (Integer) XposedHelpers.callStaticMethod(PhoneUtils, "getSlot1PhoneType");
							
							// If another slot is ready
							if ((Boolean) XposedHelpers.callStaticMethod(PhoneUtils, "isSimReady", anotherSlot))
							{
								// then dial through another
								paramIntent2.putExtra("phone_type", anotherSlot);
							} else {
								// otherwise get failed on desired slot
								paramIntent2.putExtra("phone_type", phoneType);
							}
						}
					} else {
						// Dial and we do not care
						paramIntent2.putExtra("phone_type", phoneType);
					}
				}
			}
		});
	}

	public static void handleSlotIndicator1(InitPackageResourcesParam resparam, String path, final int value)
	{
		final XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);
		if (resparam.packageName.equals("com.htc.sense.mms")) {
			try {
				resparam.res.setReplacement(resparam.packageName, "drawable", "l_icon_indicator_slot1_s", new XResources.DrawableLoader()
				{
					public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
					{
						return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot1_s), Misc.getHueValue(value));
					}
				});
			} catch (Throwable t) {
			}

			try {
				resparam.res.setReplacement(resparam.packageName, "drawable", "l_icon_indicator_slot1", new XResources.DrawableLoader()
				{
					public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
					{
						return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot1), Misc.getHueValue(value));
					}
				});
			} catch (Throwable t) {
			}

		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_slot1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot1), Misc.getHueValue(value));
				}
			});
		} catch (Throwable t) {
			// XposedBridge.log(t);
		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_slot1_dark_s", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot1_s), Misc.getHueValue(value));
				}
			});
		} catch (Throwable t) {

		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_slot1_light_s", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot1_s), Misc.getHueValue(value));
				}
			});
		} catch (Throwable t) {

		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_slot1_s", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot1_s), Misc.getHueValue(value));
				}
			});
		} catch (Throwable t) {

		}
	}

	public static void handleSlotIndicator2(InitPackageResourcesParam resparam, String path, final int value)
	{
		final XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);
		if (resparam.packageName.equals("com.htc.sense.mms")) {
			try {
				resparam.res.setReplacement(resparam.packageName, "drawable", "l_icon_indicator_slot2_s", new XResources.DrawableLoader()
				{
					public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
					{
						return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot2_s), Misc.getHueValue(value));
					}
				});
			} catch (Throwable t) {
			}

			try {
				resparam.res.setReplacement(resparam.packageName, "drawable", "l_icon_indicator_slot2", new XResources.DrawableLoader()
				{
					public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
					{
						return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot2), Misc.getHueValue(value));
					}
				});
			} catch (Throwable t) {
			}
		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_slot2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot2), Misc.getHueValue(value));
				}
			});
		} catch (Throwable t) {
			// XposedBridge.log(t);
		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_slot2_dark_s", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot2_s), Misc.getHueValue(value));
				}
			});
		} catch (Throwable t) {

		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_slot2_light_s", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot2_s), Misc.getHueValue(value));
				}
			});
		} catch (Throwable t) {

		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_slot2_s", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.adjustHue(modRes.getDrawable(R.drawable.icon_indicator_slot2_s), Misc.getHueValue(value));
				}
			});
		} catch (Throwable t) {

		}
	}

	public static void hookShowDataDisconnectedRoaming(LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.phone.NotificationMgr", paramLoadPackageParam.classLoader, "showDataDisconnectedRoaming", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				return null;
			}
		});
	}

	public static void hookSIP(final LoadPackageParam paramLoadPackageParam)
	{
		Class<?> Features = XposedHelpers.findClass("com.android.phone.HtcFeatureList", paramLoadPackageParam.classLoader);
		XposedHelpers.setStaticBooleanField(Features, "FEATURE_SUPPORT_SIP_CALL_SETTINGS", true);

		findAndHookMethod("com.android.phone.CallFeaturesSetting", paramLoadPackageParam.classLoader, "onCreate", "android.os.Bundle", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				CallFeaturesSetting = param.thisObject;
				// XposedHelpers.callMethod(param.thisObject,
				// "createSipCallSettings");
			}
		});

		findAndHookMethod("com.android.phone.dualsim.DualCallSettingsPreference", paramLoadPackageParam.classLoader, "onCreate", "android.os.Bundle", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				DualCallSettingsPreference = param.thisObject;
				XposedHelpers.callMethod(CallFeaturesSetting, "createSipCallSettings");
			}
		});

		findAndHookMethod("com.android.phone.CallFeaturesSetting", paramLoadPackageParam.classLoader, "createSipCallSettings", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				/*
				 * 
				 * this.mSipManager = SipManager.newInstance(this);
				 * this.mSipSharedPreferences = new SipSharedPreferences(this);
				 * addPreferencesFromResource(2131034157);
				 * this.mButtonSipCallOptions = getSipCallOptionPreference();
				 * this.mButtonSipCallOptions
				 * .setOnPreferenceChangeListener(this);
				 * this.mButtonSipCallOptions
				 * .setValueIndex(this.mButtonSipCallOptions .findIndexOfValue
				 * (this.mSipSharedPreferences.getSipCallOption()));
				 * this.mButtonSipCallOptions.setSummary(this.
				 * mButtonSipCallOptions.getEntry());
				 */
				final Class<?> SipManager = XposedHelpers.findClass("android.net.sip.SipManager", paramLoadPackageParam.classLoader);
				Object mSipManager = XposedHelpers.callStaticMethod(SipManager, "newInstance", param.thisObject);
				XposedHelpers.setObjectField(param.thisObject, "mSipManager", mSipManager);

				final Class<?> SipSharedPreferences = XposedHelpers.findClass("com.android.phone.sip.SipSharedPreferences", paramLoadPackageParam.classLoader);
				Object mSipSharedPreferences = XposedHelpers.newInstance(SipSharedPreferences, param.thisObject);
				XposedHelpers.setObjectField(param.thisObject, "mSipSharedPreferences", mSipSharedPreferences);

				String package_path = (String) XposedHelpers.callMethod(param.thisObject, "getPackageResourcePath");
				XModuleResources phoneRes = XModuleResources.createInstance(package_path, null);
				int res_id = phoneRes.getIdentifier("sip_settings_category", "xml", "com.android.phone");

				XposedHelpers.callMethod(DualCallSettingsPreference, "addPreferencesFromResource", res_id);

				Object mButtonSipCallOptions;

				Object localHtcListPreference1 = XposedHelpers.callMethod(DualCallSettingsPreference, "findPreference", "sip_call_options_key");
				Object localHtcListPreference2 = XposedHelpers.callMethod(DualCallSettingsPreference, "findPreference", "sip_call_options_wifi_only_key");
				Object localHtcPreferenceGroup = XposedHelpers.callMethod(DualCallSettingsPreference, "findPreference", "sip_settings_category_key");
				Boolean isSipWifiOnly = (Boolean) XposedHelpers.callStaticMethod(SipManager, "isSipWifiOnly", param.thisObject);
				if (isSipWifiOnly) {
					XposedHelpers.callMethod(localHtcPreferenceGroup, "removePreference", localHtcListPreference1);

					mButtonSipCallOptions = localHtcListPreference2;
					XposedHelpers.setObjectField(param.thisObject, "mButtonSipCallOptions", localHtcListPreference2);
				} else {
					XposedHelpers.callMethod(localHtcPreferenceGroup, "removePreference", localHtcListPreference2);

					mButtonSipCallOptions = localHtcListPreference1;
					XposedHelpers.setObjectField(param.thisObject, "mButtonSipCallOptions", localHtcListPreference1);
				}
				/*
				 * private HtcListPreference getSipCallOptionPreference() {
				 * HtcListPreference localHtcListPreference1 =
				 * (HtcListPreference)findPreference ("sip_call_options_key");
				 * HtcListPreference localHtcListPreference2 =
				 * (HtcListPreference)findPreference
				 * ("sip_call_options_wifi_only_key"); HtcPreferenceGroup
				 * localHtcPreferenceGroup = (HtcPreferenceGroup
				 * )findPreference("sip_settings_category_key"); if
				 * (SipManager.isSipWifiOnly(this)) { localHtcPreferenceGroup
				 * .removePreference(localHtcListPreference1); return
				 * localHtcListPreference2; }
				 * localHtcPreferenceGroup.removePreference
				 * (localHtcListPreference2); return localHtcListPreference1; }
				 */

				XposedHelpers.callMethod(mButtonSipCallOptions, "setOnPreferenceChangeListener", param.thisObject);

				Object getSipCallOption = XposedHelpers.callMethod(mSipSharedPreferences, "getSipCallOption");
				Object findIndexOfValue = XposedHelpers.callMethod(mButtonSipCallOptions, "findIndexOfValue", getSipCallOption);
				XposedHelpers.callMethod(mButtonSipCallOptions, "setValueIndex", findIndexOfValue);

				Object getEntry = XposedHelpers.callMethod(mButtonSipCallOptions, "getEntry");
				XposedHelpers.callMethod(mButtonSipCallOptions, "setSummary", getEntry);
			}
		});
	}

	public static void hookSIP2(LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("android.net.sip.SipManager", paramLoadPackageParam.classLoader, "isVoipSupported", "android.content.Context", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				Boolean ret = true;
				return ret;
			}
		});

		findAndHookMethod("android.net.sip.SipManager", paramLoadPackageParam.classLoader, "isApiSupported", "android.content.Context", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				Boolean ret = true;
				return ret;
			}
		});
	}
}
