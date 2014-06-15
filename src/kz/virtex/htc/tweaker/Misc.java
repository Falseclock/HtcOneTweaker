package kz.virtex.htc.tweaker;

import kz.virtex.htc.tweaker.utils.ColorFilterGenerator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.android.internal.telephony.HtcMessageHelper;

public class Misc
{
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
		if (value == 0) {
			return 0;
		} else if (value > 180) {
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
		}
		catch (NameNotFoundException e)
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
		// TODO Auto-generated method stub
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


	public static Drawable applyTheme(Drawable paramDrawable, int light, int con, int sat, int hue) {
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

		}
		catch (Exception e)
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
}
