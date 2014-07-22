package kz.virtex.htc.tweaker;

import java.util.ArrayList;

import kz.virtex.htc.tweaker.utils.ColorFilterGenerator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Align;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.android.internal.telephony.HtcMessageHelper;
import com.htc.customization.HtcCustomizationManager;
import com.htc.customization.HtcCustomizationReader;
import com.htc.service.HtcTelephonyManager;

import de.robv.android.xposed.XposedBridge;

@SuppressLint("DefaultLocale")
public class Misc
{
	public static int getIccState(int i)
	{
		String s;
		if (i == 10) {
			if (HtcTelephonyManager.dualPhoneEnable()) {
				if (HtcTelephonyManager.isDualGCPhone())
					s = SystemProperties.get("gsm.icc.sim.state");
				else
					s = SystemProperties.get("gsm.icc.uim.state");
			} else if (HtcTelephonyManager.dualGSMPhoneEnable())
				s = SystemProperties.get("gsm.icc.sim.state");
			else
				s = SystemProperties.get("gsm.sim.state");
		} else if (i == 11) {
			if (HtcTelephonyManager.dualPhoneEnable()) {
				if (HtcTelephonyManager.isDualGCPhone())
					s = SystemProperties.get("gsm.icc.uim.state");
				else
					s = SystemProperties.get("gsm.icc.sim.state");
			} else if (HtcTelephonyManager.dualGSMPhoneEnable())
				s = SystemProperties.get("gsm.icc.sub.state");
			else
				s = SystemProperties.get("gsm.sim.state");
		} else if (i == 1)
			s = SystemProperties.get("gsm.icc.sim.state");
		else if (i == 3)
			s = SystemProperties.get("gsm.icc.sub.state");
		else if (i == 2)
			s = SystemProperties.get("gsm.icc.uim.state");
		else
			s = SystemProperties.get("gsm.sim.state");

		if ("ABSENT".equals(s))
			return HtcTelephonyManager.SIM_STATE_ABSENT;
		if ("PIN_REQUIRED".equals(s))
			return HtcTelephonyManager.SIM_STATE_PIN_REQUIRED;
		if ("PUK_REQUIRED".equals(s))
			return HtcTelephonyManager.SIM_STATE_PUK_REQUIRED;
		if ("NETWORK_LOCKED".equals(s))
			return HtcTelephonyManager.SIM_STATE_NETWORK_LOCKED;
		return !"READY".equals(s) ? HtcTelephonyManager.SIM_STATE_UNKNOWN : HtcTelephonyManager.SIM_STATE_READY;
	}

	public static int getSystemSettingsInt(Context context, String key, int default_value)
	{
		int value = Settings.System.getInt(context.getContentResolver(), key, default_value);

		return value;
	}

	public static void x(StackTraceElement[] stackTrace)
	{
		if (Const.DEBUG) {
			for (StackTraceElement ste : stackTrace) {
				XposedBridge.log(ste.toString());
			}
		}
	}

	public static void x(String string)
	{
		if (Const.DEBUG)
			XposedBridge.log(string);
	}

	public static void d(String string)
	{
		if (Const.DEBUG)
			Log.d(Const.TAG, string);
	}

	public static Drawable createMarkerIcon(Drawable image, String text)
	{
		image.setColorFilter(Color.parseColor("#c2ffb6"), android.graphics.PorterDuff.Mode.SRC_ATOP);

		final int width = image.getIntrinsicWidth();
		final int height = image.getIntrinsicHeight();

		Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// Create a canvas, that will draw on to canvasBitmap.
		Canvas imageCanvas = new Canvas(canvasBitmap);

		// Set up the paint for use with our Canvas
		Paint imagePaint = new Paint();
		imagePaint.setAntiAlias(true);
		imagePaint.setTextAlign(Align.CENTER);
		imagePaint.setTextSize(28f);
		imagePaint.setColor(Color.parseColor("#ffffff"));
		imagePaint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
		// imagePaint.setShadowLayer(3F, 0, 0, Color.parseColor("#ffffff"));

		Paint mTextPaintOutline = new Paint();
		mTextPaintOutline.setAntiAlias(true);
		mTextPaintOutline.setTextAlign(Align.CENTER);
		mTextPaintOutline.setTextSize(28f);
		mTextPaintOutline.setStrokeMiter(6.0f);
		mTextPaintOutline.setColor(0xFF003800);
		mTextPaintOutline.setStyle(Paint.Style.STROKE);
		mTextPaintOutline.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
		mTextPaintOutline.setStrokeWidth(2F);

		// Draw the image to our canvas
		image.draw(imageCanvas);

		// Draw the text on top of our image
		imageCanvas.drawText(text, width / 1.9F, height / 1.6F, imagePaint);
		imageCanvas.drawText(text, width / 1.9F, height / 1.6F, mTextPaintOutline);

		// Combine background and text to a LayerDrawable
		LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]
		{ image, new BitmapDrawable(canvasBitmap) });

		return layerDrawable;
	}

	public static ArrayList<View> getAllChildren(View v)
	{
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
		 * float hsv[] = new float[3];;
		 * 
		 * Color.RGBToHSV(rIn,gIn,bIn,hsv); hsv[0] += value;
		 * 
		 * if (1 == 1) return Color.HSVToColor(hsv);
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
		try {
			if (HtcMessageHelper.isDualSlotDevice()) { return true; }
		} catch (NoSuchMethodError e) {
			return false;
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
		try {
			pm.getPackageInfo(packagename, PackageManager.GET_META_DATA);
			return true;
		} catch (NameNotFoundException e) {
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

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		if (drawable instanceof BitmapDrawable) { return ((BitmapDrawable) drawable).getBitmap(); }

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
		if (localHtcCustomizationManager != null) {
			HtcCustomizationReader localHtcCustomizationReader = localHtcCustomizationManager.getCustomizationReader("system", 1, false);
			if (localHtcCustomizationReader != null) {
				SENSE_VERSION = localHtcCustomizationReader.readString("sense_version", "5.0");
			}
		}
		return SENSE_VERSION.equals("6.0");
	}

	/*
	 * Licensed to the Apache Software Foundation (ASF) under one or more
	 * contributor license agreements. See the NOTICE file distributed with this
	 * work for additional information regarding copyright ownership. The ASF
	 * licenses this file to You under the Apache License, Version 2.0 (the
	 * "License"); you may not use this file except in compliance with the
	 * License. You may obtain a copy of the License at
	 * 
	 * http://www.apache.org/licenses/LICENSE-2.0
	 * 
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 */

	public static String capitalizeFully(CharSequence str)
	{
		return capitalizeFully(str.toString(), null);
	}

	public static String capitalizeFully(String str)
	{
		return capitalizeFully(str, null);
	}

	@SuppressLint("DefaultLocale")
	public static String capitalizeFully(String str, char... delimiters)
	{
		int delimLen = delimiters == null ? -1 : delimiters.length;
		if (str.isEmpty() || delimLen == 0) { return str; }
		str = str.toLowerCase();
		return capitalize(str, delimiters);
	}

	public static String capitalize(String str)
	{
		return capitalize(str, null);
	}

	public static String capitalize(String str, char... delimiters)
	{
		int delimLen = delimiters == null ? -1 : delimiters.length;
		if (str.isEmpty() || delimLen == 0) { return str; }
		char[] buffer = str.toCharArray();
		boolean capitalizeNext = true;
		for (int i = 0; i < buffer.length; i++) {
			char ch = buffer[i];
			if (isDelimiter(ch, delimiters)) {
				capitalizeNext = true;
			} else if (capitalizeNext) {
				buffer[i] = Character.toTitleCase(ch);
				capitalizeNext = false;
			}
		}
		return new String(buffer);
	}

	private static boolean isDelimiter(char ch, char[] delimiters)
	{
		if (delimiters == null) { return Character.isWhitespace(ch); }
		for (char delimiter : delimiters) {
			if (ch == delimiter) { return true; }
		}
		return false;
	}
}
