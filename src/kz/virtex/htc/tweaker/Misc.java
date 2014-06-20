package kz.virtex.htc.tweaker;

import java.util.ArrayList;

import kz.virtex.htc.tweaker.utils.ColorFilterGenerator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import com.android.internal.telephony.HtcMessageHelper;
import com.htc.customization.HtcCustomizationManager;
import com.htc.customization.HtcCustomizationReader;

public class Misc
{
	public static ArrayList<View> getAllChildren(View v) {
		if (!(v instanceof ViewGroup)) {
			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			return viewArrayList;
		}

		ArrayList<View> result = new ArrayList<View>();

		ViewGroup vg = (ViewGroup) v;
		for (int i = 0; i < vg.getChildCount(); i++) {

			View child = vg.getChildAt(i);
			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			viewArrayList.addAll(getAllChildren(child));

			result.addAll(viewArrayList);
		}
		return result;
	}


	public static Drawable adjustHue(Drawable paramDrawable, int hue)
	{
		ColorFilter localColorFilter = ColorFilterGenerator.adjustHue(hue);

		paramDrawable.clearColorFilter();
		
		if (localColorFilter != null)
			paramDrawable.setColorFilter(localColorFilter);

		return paramDrawable;
	}
	
	// fucking color matrix
	public static int colorTransform(int intColor, int value)
	{
		
		int rIn = Color.red(intColor);
		int gIn = Color.green(intColor);
		int bIn = Color.blue(intColor);
		/*
		float hsv[] = new float[3];;
		
		Color.RGBToHSV(rIn,gIn,bIn,hsv);
		hsv[0] += value;

		if (1 == 1)
			return Color.HSVToColor(hsv);
		*/
		float degree = cleanValue(value, 180.0F) / 180f * (float) Math.PI;

		float cosVal = (float) Math.cos(degree);
		float sinVal = (float) Math.sin(degree);

		float LUMA_R = 0.212671f;
		float LUMA_G = 0.715160f;
		float LUMA_B = 0.072169F;

		float rOut = ((LUMA_R + (cosVal * (1 - LUMA_R))) + (sinVal * -(LUMA_R))) * rIn + ((LUMA_G + (cosVal * -(LUMA_G))) + (sinVal * -(LUMA_G))) * gIn + ((LUMA_B + (cosVal * -(LUMA_B))) + (sinVal * (1 - LUMA_B))) * bIn;

		float gOut = ((LUMA_R + (cosVal * -(LUMA_R))) + (sinVal * 0.143F)) * rIn + ((LUMA_G + (cosVal * (1 - LUMA_G))) + (sinVal * 0.140F)) * gIn + ((LUMA_B + (cosVal * -(LUMA_B))) + (sinVal * -0.283F)) * bIn;

		float bOut = ((LUMA_R + (cosVal * -(LUMA_R))) + (sinVal * -((1 - LUMA_R)))) * rIn + ((LUMA_G + (cosVal * -(LUMA_G))) + (sinVal * LUMA_G)) * gIn + ((LUMA_B + (cosVal * (1 - LUMA_B))) + (sinVal * LUMA_B)) * bIn;

		return Color.rgb(clamp(rOut), clamp(gOut), clamp(bOut));
	}
	
	protected static float cleanValue(float p_val, float p_limit)
	{
		return Math.min(p_limit, Math.max(-p_limit, p_val));
	}

	private static int clamp(float v)
	{
		if (v < 0)
			return 0;
		if (v > 255)
			return 255;

		return (int) (v + 0.5);
	}

	public static boolean isDual()
	{
		if (HtcMessageHelper.isDualSlotDevice())
		{
			return true;
		}
		return false;
	}

	public static int getHueValue(int value)
	{
		if (value == 0)
		{
			return 0;
		} else if (value > 180)
		{
			value = -180 + (value - 180);
		}
		return value;
	}

	public static boolean isPackageInstalled(String packagename, Context context)
	{
		PackageManager pm = context.getPackageManager();
		try
		{
			pm.getPackageInfo(packagename, PackageManager.GET_META_DATA);
			return true;
		} catch (NameNotFoundException e)
		{
			return false;
		}
	}

	public static boolean toBoolean(int paramInt)
	{
		return paramInt > 0 ? true : false;
	}

	public static int toInt(boolean paramBoolean)
	{
		return paramBoolean == true ? 1 : 0;
	}

	public static float densify(float f)
	{
		return Const.DENSITY * f;
	}

	public static int densify(int paramInt)
	{
		return Math.round(Const.DENSITY * paramInt);
	}

	public static Drawable applyTheme(Drawable paramDrawable, String key, SharedPreferences preferences)
	{
		int light = preferences.getInt(key + "_lightValue", 0);
		int sat = preferences.getInt(key + "_satValue", 0);
		int hue = preferences.getInt(key + "_hueValue", 0);
		int con = preferences.getInt(key + "_conValue", 0);

		return applyFilter(paramDrawable, light, con, sat, hue);
	}


	private static Drawable applyFilter(Drawable paramDrawable, int light, int con, int sat, int hue)
	{
		ColorFilter localColorFilter = ColorFilterGenerator.adjustColor(light, con, sat, hue);

		paramDrawable.clearColorFilter();
		if (localColorFilter != null)
			paramDrawable.setColorFilter(localColorFilter);

		return paramDrawable;
	}

	public static void cleanUp()
	{
		try
		{
			Main.preferences.edit().remove("TweakColoredSimLevel").commit();
			Main.preferences.edit().remove("AutoRecordingVibroKey").commit();
			Main.preferences.edit().remove("enablePopupCharKey").commit();
			Main.preferences.edit().remove("enableDialerHighLightKey_color").commit();
			Main.preferences.edit().remove("TweakEnableCallRecording").commit();
			Main.preferences.edit().remove("enableColoredWiFiKey").commit();
			Main.preferences.edit().remove("TweakColoredWeather").commit();
			Main.preferences.edit().remove("enableDialerHighLightKey_size").commit();
			Main.preferences.edit().remove("enableCallRecordingKey").commit();
			Main.preferences.edit().remove("TweakAllNotificationsExpanded").commit();
			Main.preferences.edit().remove("disableDeliveryNotificationKey").commit();
			Main.preferences.edit().remove("enableColoredWeatherKey").commit();
			Main.preferences.edit().remove("allNotificationsExpandedKey").commit();
			Main.preferences.edit().remove("enableColoredSimLevelKey").commit();
			Main.preferences.edit().remove("TweakColoredWiFi").commit();
			Main.preferences.edit().remove("enableAutoRecordingKey").commit();
			Main.preferences.edit().remove("TweakEnableAutoRecording").commit();
			Main.preferences.edit().remove("TweakColoredSimLevel").commit();
			Main.preferences.edit().remove("TweakColoredSimLevel").commit();
			Main.preferences.edit().remove("TweakColoredSimLevel").commit();
			Main.preferences.edit().remove("TweakColoredSimLevel").commit();
			Main.preferences.edit().remove("TweakColoredSimLevel").commit();
			Main.preferences.edit().remove("TweakPopupChar").commit();
			Main.preferences.edit().remove("ColorSIM1_hueValue").commit();
			Main.preferences.edit().remove("ColorSIM2_hueValue").commit();
			Main.preferences.edit().remove("ColorSIM1_satValue").commit();
			Main.preferences.edit().remove("ColorSIM2_satValue").commit();
			Main.preferences.edit().remove("ColorSIM1_lightValue").commit();
			Main.preferences.edit().remove("ColorSIM2_lightValue").commit();

		} catch (Exception e)
		{

		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		if (drawable instanceof BitmapDrawable)
		{
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static boolean isSense6()
	{
		String SENSE_VERSION = "5.0";
		
		HtcCustomizationManager localHtcCustomizationManager = HtcCustomizationManager.getInstance();
		if (localHtcCustomizationManager != null)
		{
			HtcCustomizationReader localHtcCustomizationReader = localHtcCustomizationManager.getCustomizationReader("system", 1, false);
			if (localHtcCustomizationReader != null)
			{
				SENSE_VERSION = localHtcCustomizationReader.readString("sense_version", "5.0");
			}
		}
		return SENSE_VERSION.equals("6.0");
	}
}
