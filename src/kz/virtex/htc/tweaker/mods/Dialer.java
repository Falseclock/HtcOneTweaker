package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.Arrays;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.TweakerService;
import kz.virtex.htc.tweaker.XMain;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.content.res.XModuleResources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Dialer
{
	public static String[] Btn =
	{};
	public static String[] Lat =
	{};

	private static final BroadcastReceiver br = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent)
		{
			// TODO: 
		}
	};

	@SuppressWarnings("unused")
	private static Object getSettings(Context context, String name, String type, String deflt) throws NameNotFoundException
	{
		// Registering our receiver to get key press state

		IntentFilter intFilt = new IntentFilter(TweakerService.ACTION_VOLUME_KEY_PRESS);
		context.registerReceiver(br, intFilt);
		Misc.x("Dialer, BroadcastReceiver registered");

		Context tweakContext = context.createPackageContext(Const.PACKAGE_NAME, Context.CONTEXT_IGNORE_SECURITY);
		Intent intent = new Intent(tweakContext, TweakerService.class).setAction(TweakerService.ACTION_VOLUME_KEY_PRESS);
		tweakContext.startService(intent);
		Misc.x("Dialer, Service requested");

		return new Object();
	}

	private static String[] push(String[] array, String push)
	{
		String[] longer = new String[array.length + 1];
		System.arraycopy(array, 0, longer, 0, array.length);
		longer[array.length] = push;

		return longer;
	}
	/*
	public static void hookBigDialKeypad(InitPackageResourcesParam resparam, String path)
	{
		XmlResourceParser specific_htc_keypad = resparam.res.getLayout(resparam.res.getIdentifier("specific_htc_keypad", "layout", resparam.packageName));
		resparam.res.setReplacement(resparam.packageName, "layout", "specific_htc_keypad_big", specific_htc_keypad);

		int keypad_btn_layout = resparam.res.getInteger(resparam.res.getIdentifier("keypad_btn_layout", "id", resparam.packageName));
		resparam.res.setReplacement(resparam.packageName, "id", "keypad_btn_layout_big", keypad_btn_layout);

		XposedBridge.log(resparam.packageName);
	}
	
	public static void hookBigDialKeypad(final LoadPackageParam paramLoadPackageParam)
	{
		
		findAndHookMethod("com.htc.htcdialer.widget.DialerKeypad", paramLoadPackageParam.classLoader, "initKeypadButton", View.class, int.class, int.class, new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				View sss = (View) param.args[0];
				LayoutParams params = sss.getLayoutParams();
				params.height = 150;

				sss.setLayoutParams(params);
			}
		});
		
		XposedHelpers.findAndHookConstructor("com.htc.htcdialer.widget.keypadbtn.MeasureUtils", paramLoadPackageParam.classLoader, boolean.class, new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				param.args[0] = Boolean.valueOf(false);
			}
		});
	}
*/
	public static void hookSimCallButton(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.htc.htcdialer.Dialer", paramLoadPackageParam.classLoader, "updateDMDSCallButton", boolean.class, boolean.class, new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");

				boolean showSim1 = Misc.toBoolean(Misc.getSystemSettingsInt(mContext, Const.TWEAK_SHOW_SIM_CARD_DIAL + "_sim1", 1));
				boolean showSim2 = Misc.toBoolean(Misc.getSystemSettingsInt(mContext, Const.TWEAK_SHOW_SIM_CARD_DIAL + "_sim2", 1));

				boolean sim1hide = (Boolean) param.args[0];
				boolean sim2hide = (Boolean) param.args[0];

				// If user choosed to hide SIM 1 (Kcell)
				if (!showSim1) {
					// if SIM 2 (Beeline) also going to be disabled by the
					// system
					if (sim2hide) {
						// Let's get what we should do
						int action = Misc.getSystemSettingsInt(mContext, Const.TWEAK_SHOW_SIM_CARD_DIAL_ACTION, 0);
						switch (action)
						{
						// show another sim
							case 0:
							default:
								// if SIM 1 also not available
								if (sim1hide) {
									// do nothing, both SIMs are not visible
									return;
								} else {
									param.args[0] = Boolean.valueOf(true);
								}
							case 1:
								param.args[0] = Boolean.valueOf(true);
						}
					} else {
						param.args[0] = Boolean.valueOf(true);
					}
				}

				// If user choosed to hide SIM 2 (Beeline)
				if (!showSim2) {
					// if SIM 1 (Kcell) also going to be disabled by the system
					if (sim1hide) {
						// Let's get what we should do
						int action = Misc.getSystemSettingsInt(mContext, "tweak_show_sim_card_dial_action", 0);
						switch (action)
						{
						// show another sim
							case 0:
							default:
								// if SIM 2 also not available
								if (sim2hide) {
									// do nothing, both SIMs are not visible
									return;
								} else {
									param.args[1] = Boolean.valueOf(true);
								}
							case 1:
								param.args[1] = Boolean.valueOf(true);
						}
					} else {
						param.args[1] = Boolean.valueOf(true);
					}
				}
			}
		});
	}

	// ----------------------------------------------------
	// Replaces background on Dialer
	// ----------------------------------------------------
	public static void hookSpecificHtcShowKeypad(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.htc.htcdialer.widget.keypadbtn.HtcKeypadBgBtn", paramLoadPackageParam.classLoader, "setEnabled", "boolean", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				float alpha = (Float) XposedHelpers.callMethod(param.thisObject, "getAlpha");
				if (alpha <= 0.4F) // to avoid background set every time
				{
					View btn = (View) param.thisObject;
					View parent = (View) btn.getParent();
					View main = (View) parent.getParent();

					XModuleResources modRes = XModuleResources.createInstance(XMain.MODULE_PATH, null);
					Drawable bg = modRes.getDrawable(R.drawable.phone_keypad_bg);
					main.setBackground(bg);
				}
			}
		});

		XposedHelpers.findAndHookConstructor("com.htc.htcdialer.widget.DividerDrawable", paramLoadPackageParam.classLoader, "android.content.Context", "android.graphics.drawable.Drawable", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				XModuleResources modRes = XModuleResources.createInstance(XMain.MODULE_PATH, null);

				Drawable bg = modRes.getDrawable(R.drawable.phone_keypad_bg);

				XposedHelpers.setObjectField(param.thisObject, "mDrawable", bg);
			}
		});

		findAndHookMethod("com.htc.htcdialer.widget.DividerDrawable", paramLoadPackageParam.classLoader, "setDividerColor", "int", "int", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				int[] mColorValue = (int[]) XposedHelpers.getObjectField(param.thisObject, "mColorValue");
				int index = (Integer) param.args[0];

				if ((Integer) param.args[1] == Color.parseColor("#2f2f2f")) {
					mColorValue[index] = Color.parseColor("#444444");
					XposedHelpers.setObjectField(param.thisObject, "mColorValue", mColorValue);
				}
			}
		});
	}

	public static void hookCallButtons(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.htc.htcdialer.widget.DividerDrawable", paramLoadPackageParam.classLoader, "setDividerColor", "int", "int", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				int paramInt1 = (Integer) param.args[0];
				int paramInt2 = (Integer) param.args[1];

				if (paramInt1 == 4) {
					if (paramInt2 == -13388315) // #FF33B5E5 FIXME: get color
												// from resources
						param.args[1] = Misc.colorTransform(paramInt2, Misc.getHueValue(XMain.pref.getInt(Const.TWEAK_SLOT1_COLOR, 0)));

					if (paramInt2 == -13128336) // #FF37AD70 FIXME: get color
												// from resources
						param.args[1] = Misc.colorTransform(Color.parseColor("#33e5b1"), Misc.getHueValue(XMain.pref.getInt(Const.TWEAK_SLOT2_COLOR, 0)));
				}
			}
		});

		findAndHookMethod("com.htc.htcdialer.widget.keypadbtn.ScaledString", paramLoadPackageParam.classLoader, "setText", "java.lang.CharSequence", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				String text = null;

				if ((param.args[0] instanceof String))
					text = (String) param.args[0];
				if ((param.args[0] instanceof CharSequence))
					text = ((CharSequence) param.args[0]).toString();
				if (text == null)
					return;

				String slot1 = XMain.pref.getString("slot_1_user_text", "");
				String slot2 = XMain.pref.getString("slot_2_user_text", "");

				if (text.toLowerCase().contains(slot1.toLowerCase()) || text.toLowerCase().contains(slot2.toLowerCase())) {
					String pref = Const.TWEAK_SLOT1_COLOR;
					int color = -13388315; // FIXME:
					if (text.toLowerCase().contains(slot2.toLowerCase())) {
						pref = Const.TWEAK_SLOT2_COLOR;
						color = Color.parseColor("#33e5b1"); // FIXME:
					}
					TextPaint mTextPaint = (TextPaint) XposedHelpers.getObjectField(param.thisObject, "mTextPaint");
					int tweak = Misc.colorTransform(color, Misc.getHueValue(XMain.pref.getInt(pref, 0)));
					mTextPaint.setColor(tweak);
				}
			}
		});
	}

	public static void handleCallDirections(InitPackageResourcesParam resparam, String path)
	{
		XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);

		resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_incoming_dark_s", modRes.fwd(R.drawable.icon_indicator_incoming_light_s));
		resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_outgoing_dark_s", modRes.fwd(R.drawable.icon_indicator_outgoing_light_s));
	}

	public static void hookDialerButtons(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.htc.htcdialer.widget.keypadbtn.HtcKeypadBgBtn", paramLoadPackageParam.classLoader, "init", "android.content.Context", "android.util.AttributeSet", "int", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Context paramContext = (Context) param.args[0];
				AttributeSet paramAttributeSet = (AttributeSet) param.args[1];
				int paramInt = (Integer) param.args[2];

				TypedArray dialer = paramContext.obtainStyledAttributes(paramAttributeSet, Const.HtcDialerAnimationButtonMode, paramInt, 0);

				String phonetic = dialer.getString(5);
				String latin = dialer.getString(3);

				if (!TextUtils.isEmpty(phonetic)) {
					if (!Arrays.asList(Btn).contains(phonetic)) {
						Btn = push(Btn, phonetic);
					}
				}
				if (!TextUtils.isEmpty(latin)) {
					if (!Arrays.asList(Lat).contains(latin)) {
						Lat = push(Lat, latin);
					}
				}
				dialer.recycle();
			}
		});

		findAndHookMethod("com.htc.htcdialer.widget.keypadbtn.ScaledString", paramLoadPackageParam.classLoader, "init", "android.content.Context", "java.lang.String", "int", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				String paramString = (String) param.args[1];
				if (Arrays.asList(Btn).contains(paramString)) {
					Object TextPaint = XposedHelpers.getObjectField(param.thisObject, "mTextPaint");
					XposedHelpers.callMethod(TextPaint, "setColor", XMain.pref.getInt(Const.TWEAK_DIALER_BUTTON_COLOR, Const.DIALER_BUTTON_STOCK_COLOR));
					XposedHelpers.callMethod(TextPaint, "setTextSize", XMain.pref.getFloat(Const.TWEAK_DIALER_BUTTON_SIZE, Const.DIALER_BUTTON_STOCK_SIZE));
				}
				if (Arrays.asList(Lat).contains(paramString)) {
					Object TextPaint = XposedHelpers.getObjectField(param.thisObject, "mTextPaint");
					XposedHelpers.callMethod(TextPaint, "setColor", XMain.pref.getInt(Const.TWEAK_DIALER_BUTTON_COLOR_LAT, Const.DIALER_BUTTON_STOCK_COLOR));
					XposedHelpers.callMethod(TextPaint, "setTextSize", XMain.pref.getFloat(Const.TWEAK_DIALER_BUTTON_SIZE_LAT, Const.DIALER_BUTTON_STOCK_SIZE));
				}
			}
		});

		findAndHookMethod("com.htc.htcdialer.widget.keypadbtn.ScaledString", paramLoadPackageParam.classLoader, "setTouchState", "boolean", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				CharSequence mString = (CharSequence) XposedHelpers.getObjectField(param.thisObject, "mString");

				if (Arrays.asList(Btn).contains(mString.toString())) {
					Object TextPaint = XposedHelpers.getObjectField(param.thisObject, "mTextPaint");
					XposedHelpers.callMethod(TextPaint, "setColor", XMain.pref.getInt(Const.TWEAK_DIALER_BUTTON_COLOR, Const.DIALER_BUTTON_STOCK_COLOR));
					XposedHelpers.callMethod(TextPaint, "setTextSize", XMain.pref.getFloat(Const.TWEAK_DIALER_BUTTON_SIZE, Const.DIALER_BUTTON_STOCK_SIZE));
				}
				if (Arrays.asList(Lat).contains(mString.toString())) {
					Object TextPaint = XposedHelpers.getObjectField(param.thisObject, "mTextPaint");
					XposedHelpers.callMethod(TextPaint, "setColor", XMain.pref.getInt(Const.TWEAK_DIALER_BUTTON_COLOR_LAT, Const.DIALER_BUTTON_STOCK_COLOR));
					XposedHelpers.callMethod(TextPaint, "setTextSize", XMain.pref.getFloat(Const.TWEAK_DIALER_BUTTON_SIZE_LAT, Const.DIALER_BUTTON_STOCK_SIZE));
				}
			}
		});
	}
}
