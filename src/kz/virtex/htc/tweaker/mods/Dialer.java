package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.ArrayList;
import java.util.Arrays;

import com.htc.widget.HtcSeekBar;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.XMain;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XModuleResources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Dialer
{
	public static String[] Btn =
	{};
	public static String[] Lat =
	{};

	private static String[] push(String[] array, String push)
	{
		String[] longer = new String[array.length + 1];
		System.arraycopy(array, 0, longer, 0, array.length);
		longer[array.length] = push;

		return longer;
	}
	
	public static void hookSpecificHtcShowKeypad(final LoadPackageParam paramLoadPackageParam)
	{
		if (1 == 1)
			return;
		findAndHookMethod("com.htc.htcdialer.widget.keypadbtn.HtcKeypadBgBtn.BackgroundDrawable", paramLoadPackageParam.classLoader, "draw", "android.graphics.Canvas", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Paint mBgPaint = (Paint) XposedHelpers.getObjectField(param.thisObject, "mBgPaint");
				mBgPaint.setColor(Color.argb(0, 0, 0, 0));
			}
		});
	}
	
	public static void hookSpecificHtcShowKeypad(final InitPackageResourcesParam resparam, String path)
	{
		if (1 == 1)
			return;
		resparam.res.hookLayout(resparam.packageName, "layout", "specific_htc_show_keypad", new XC_LayoutInflated() {
	        @Override
	        public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
	        	int show_keypad = liparam.res.getIdentifier("show_keypad", "id", resparam.packageName);

	        	FrameLayout keypad = (FrameLayout) liparam.view.findViewById(show_keypad);
	        	
	        	keypad.setBackgroundColor(Color.argb(0, 0, 0, 0));
	        	keypad.setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
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

				if (paramInt1 == 4)
				{
					if (paramInt2 == -13388315) // #FF33B5E5 FIXME: get color from resources
						param.args[1] = Misc.colorTransform(paramInt2, Misc.getHueValue(XMain.pref.getInt(Const.TWEAK_SLOT1_COLOR, 0)));

					if (paramInt2 == -13128336) // #FF37AD70 FIXME: get color from resources
						param.args[1] = Misc.colorTransform(paramInt2, Misc.getHueValue(XMain.pref.getInt(Const.TWEAK_SLOT2_COLOR, 0)));
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
				
				if (text.toLowerCase().contains(slot1.toLowerCase()) || text.toLowerCase().contains(slot2.toLowerCase()))
				{
					String pref = Const.TWEAK_SLOT1_COLOR;
					int color = -13388315; //FIXME:
					if (text.toLowerCase().contains(slot2.toLowerCase())) {
						pref = Const.TWEAK_SLOT2_COLOR;
						color = -13128336; //FIXME:
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

				if (!TextUtils.isEmpty(phonetic))
				{
					if (!Arrays.asList(Btn).contains(phonetic))
					{
						Btn = push(Btn, phonetic);
					}
				}
				if (!TextUtils.isEmpty(latin))
				{
					if (!Arrays.asList(Lat).contains(latin))
					{
						Lat = push(Lat, latin);
					}
				}
				dialer.recycle();
			}
			
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				if (1 == 1)
					return;
				XposedHelpers.callMethod(param.thisObject, "setBackground", new ColorDrawable(Color.argb(0, 0, 0, 0)));
			}
		});
		
		findAndHookMethod("com.htc.htcdialer.widget.keypadbtn.ScaledString", paramLoadPackageParam.classLoader, "init", "android.content.Context", "java.lang.String", "int", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				String paramString = (String) param.args[1];
				if (Arrays.asList(Btn).contains(paramString))
				{
					Object TextPaint = XposedHelpers.getObjectField(param.thisObject, "mTextPaint");
					XposedHelpers.callMethod(TextPaint, "setColor", XMain.pref.getInt(Const.TWEAK_DIALER_BUTTON_COLOR, Const.DIALER_BUTTON_STOCK_COLOR));
					XposedHelpers.callMethod(TextPaint, "setTextSize", XMain.pref.getFloat(Const.TWEAK_DIALER_BUTTON_SIZE, Const.DIALER_BUTTON_STOCK_SIZE));
				}
				if (Arrays.asList(Lat).contains(paramString))
				{
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

				if (Arrays.asList(Btn).contains(mString.toString()))
				{
					Object TextPaint = XposedHelpers.getObjectField(param.thisObject, "mTextPaint");
					XposedHelpers.callMethod(TextPaint, "setColor", XMain.pref.getInt(Const.TWEAK_DIALER_BUTTON_COLOR, Const.DIALER_BUTTON_STOCK_COLOR));
					XposedHelpers.callMethod(TextPaint, "setTextSize", XMain.pref.getFloat(Const.TWEAK_DIALER_BUTTON_SIZE, Const.DIALER_BUTTON_STOCK_SIZE));
				}
				if (Arrays.asList(Lat).contains(mString.toString()))
				{
					Object TextPaint = XposedHelpers.getObjectField(param.thisObject, "mTextPaint");
					XposedHelpers.callMethod(TextPaint, "setColor", XMain.pref.getInt(Const.TWEAK_DIALER_BUTTON_COLOR_LAT, Const.DIALER_BUTTON_STOCK_COLOR));
					XposedHelpers.callMethod(TextPaint, "setTextSize", XMain.pref.getFloat(Const.TWEAK_DIALER_BUTTON_SIZE_LAT, Const.DIALER_BUTTON_STOCK_SIZE));
				}
			}
		});
	}
}
