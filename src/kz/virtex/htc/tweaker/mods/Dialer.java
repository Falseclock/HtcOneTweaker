package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import java.util.Arrays;
import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.XMain;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XModuleResources;
import android.text.TextUtils;
import android.util.AttributeSet;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Dialer
{
	public static String[] Btn = {};
	public static String[] Lat = {};
	
	private static String[] push(String[] array, String push)
	{
		String[] longer = new String[array.length + 1];
		System.arraycopy(array, 0, longer, 0, array.length);
		longer[array.length] = push;

		return longer;
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
