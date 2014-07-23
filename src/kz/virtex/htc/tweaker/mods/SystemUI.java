package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.util.ArrayList;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.XMain;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class SystemUI
{
	private static RelativeLayout miuiBar;
	private static RelativeLayout miuiBarCharging;
	private static RelativeLayout miuiBarBattery;
	private static int BarHeight = 3;

	public static void handleColoredSIM(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.systemui.statusbar.policy.NetworkControllerDual", paramLoadPackageParam.classLoader, "updateIconBySlot", int.class, new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Class<?> TelephonyIconsDual = XposedHelpers.findClass("com.android.systemui.statusbar.policy.TelephonyIconsDual", paramLoadPackageParam.classLoader);

				int[] HTC_SIGNAL_S1_5LEVEL = (int[]) XposedHelpers.getStaticObjectField(TelephonyIconsDual, "HTC_SIGNAL_S1_5LEVEL");
				int[] HTC_SIGNAL_S2_5LEVEL = (int[]) XposedHelpers.getStaticObjectField(TelephonyIconsDual, "HTC_SIGNAL_S2_5LEVEL");
				int[] HTC_SIGNAL_S1_5LEVEL_R = (int[]) XposedHelpers.getStaticObjectField(TelephonyIconsDual, "HTC_SIGNAL_S1_5LEVEL_R");
				int[] HTC_SIGNAL_S2_5LEVEL_R = (int[]) XposedHelpers.getStaticObjectField(TelephonyIconsDual, "HTC_SIGNAL_S2_5LEVEL_R");

				XposedHelpers.setStaticObjectField(TelephonyIconsDual, "HTC_SIGNAL_S1_4LEVEL", HTC_SIGNAL_S1_5LEVEL);
				XposedHelpers.setStaticObjectField(TelephonyIconsDual, "HTC_SIGNAL_S2_4LEVEL", HTC_SIGNAL_S2_5LEVEL);
				XposedHelpers.setStaticObjectField(TelephonyIconsDual, "HTC_SIGNAL_S1_4LEVEL_R", HTC_SIGNAL_S1_5LEVEL_R);
				XposedHelpers.setStaticObjectField(TelephonyIconsDual, "HTC_SIGNAL_S2_4LEVEL_R", HTC_SIGNAL_S2_5LEVEL_R);
			}
		});
	}

	public static void hookStatusBarMIUIBattery(LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.systemui.statusbar.phone.PhoneStatusBar", paramLoadPackageParam.classLoader, "makeStatusBarView", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				ViewGroup mStatusBarView = (ViewGroup) XposedHelpers.getObjectField(param.thisObject, "mStatusBarView");
				Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");

				miuiBar = new RelativeLayout(mContext);
				RelativeLayout.LayoutParams miuiBarParams = new RelativeLayout.LayoutParams(getScreenWidth(mContext), LayoutParams.WRAP_CONTENT);
				miuiBarParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				miuiBarParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				miuiBar.setLayoutParams(miuiBarParams);
				miuiBar.setBackgroundColor(Color.TRANSPARENT);

				miuiBarCharging = new RelativeLayout(mContext);
				miuiBarCharging.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, BarHeight));
				miuiBarCharging.setBackgroundColor(Color.TRANSPARENT);

				miuiBarBattery = new RelativeLayout(mContext);
				miuiBarBattery.setLayoutParams(new RelativeLayout.LayoutParams(0, BarHeight));
				miuiBarBattery.setBackgroundColor(Color.TRANSPARENT);

				miuiBar.addView(miuiBarCharging);
				miuiBar.addView(miuiBarBattery);

				mStatusBarView.addView(miuiBar, 0, miuiBarParams);
			}
		});

		findAndHookMethod("com.android.systemui.statusbar.policy.BatteryController", paramLoadPackageParam.classLoader, "onReceive", Context.class, Intent.class, new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
				boolean plugged = XposedHelpers.getBooleanField(param.thisObject, "plugged");

				int level = XposedHelpers.getIntField(param.thisObject, "level");

				// Misc.x("Battery level: " + level);
				// Misc.x("Is plugged: " + plugged);

				RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(getScreenWidth(mContext) - getSideWidth(mContext, level) * 2, BarHeight);
				layoutparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
				miuiBarBattery.setLayoutParams(layoutparams);

				float[] hsv = new float[3];
				if (level >= 50) {
					hsv[0] = 100;
					hsv[1] = 0.5F;
				} else {
					hsv[0] = level > 0 ? ((float) level * 2.0F) - 2.0F : 0.0F;
					hsv[1] = level > 0 ? (1.0F - ((float) level * 0.5F / (float) 50)) : 1.0F;
				}
				hsv[2] = 1.0F;
				int color = Color.HSVToColor(hsv);

				miuiBarBattery.setBackgroundColor(color);

				if (plugged && level <= 98) {
					AnimationDrawable animation = createAnim(mContext, level, color);
					animation.setOneShot(false);
					miuiBarCharging.setBackgroundDrawable(animation);
					animation.start();
				} else {
					miuiBarCharging.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)));
					miuiBarCharging.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)));
				}
			}
		});
	}

	private static AnimationDrawable createAnim(Context context, int level, int color)
	{
		int side = getSideWidth(context, level);
		int screenWidth = getScreenWidth(context);
		float gravitAccel = 9.81F;
		// Скорость падения в конце
		// u = sqrt(2gh)
		int endSpeed = (int) Math.sqrt(2 * gravitAccel * side);
		// Misc.d("side: " + side + ", End speed: " + endSpeed +
		// ", Drop width: " + (side / 10));

		AnimationDrawable anim = new AnimationDrawable();
		Drawable[] layers = new Drawable[2];
		layers[0] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(screenWidth, BarHeight, Bitmap.Config.ARGB_8888));
		layers[1] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(screenWidth, BarHeight, Bitmap.Config.ARGB_8888));
		LayerDrawable layerDrawable = new LayerDrawable(layers);
		anim.addFrame(layerDrawable, endSpeed * 3);

		int dropWidth = side / 20;
		int position = -dropWidth;

		while (position < side) {
			layers = new Drawable[2];
			layers[0] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(screenWidth, BarHeight, Bitmap.Config.ARGB_8888));
			layers[1] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(screenWidth, BarHeight, Bitmap.Config.ARGB_8888));

			Canvas mCanvas = new Canvas(Misc.drawableToBitmap(layers[1]));
			Paint mPoint = new Paint();
			mPoint.setStyle(Paint.Style.FILL);
			mPoint.setColor(color);

			mCanvas.drawRect(position, 0, position + dropWidth, BarHeight, mPoint);
			mCanvas.drawRect(screenWidth - position - dropWidth, 0, screenWidth - position, BarHeight, mPoint);

			layerDrawable = new LayerDrawable(layers);

			int speed = (int) Math.sqrt(2 * gravitAccel * position);

			anim.addFrame(layerDrawable, endSpeed - speed);

			position += dropWidth;
			// Misc.d("Position: " + position + "speed: " + (endSpeed - speed));
		}

		return anim;
	}

	private static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		Configuration config = context.getResources().getConfiguration();
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			return size.x;
		} else {
			return size.y;
		}
	}

	private static int getSideWidth(Context context, int level)
	{
		int screenWidth = getScreenWidth(context);
		return (screenWidth - (level * getScreenWidth(context) / 100)) / 2;
	}

	@SuppressLint("DefaultLocale")
	public static void hookDateCase(LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.systemui.statusbar.policy.DateView", paramLoadPackageParam.classLoader, "updateClock", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				TextView date = (TextView) param.thisObject;
				CharSequence text = date.getText();

				// Misc.x("updateClock is happen: " +
				// DateFormat.format("EEE MMM d HH:mm:ss zz yyyy", new
				// Date()).toString());

				date.setText(String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length()));
				date.setAllCaps(false);
			}
		});
	}

	public static void hookBarFont(LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.systemui.statusbar.policy.Clock", paramLoadPackageParam.classLoader, "updateClock", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				XposedHelpers.callMethod(param.thisObject, "setTypeface", Typeface.create("sans-serif-condensed", Typeface.NORMAL));
			}
		});

		findAndHookMethod("com.android.systemui.statusbar.policy.BatteryController", paramLoadPackageParam.classLoader, "onReceive", Context.class, Intent.class, new XC_MethodHook()
		{
			@SuppressWarnings("unchecked")
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				ArrayList<TextView> mLabelViews = (ArrayList<TextView>) XposedHelpers.getObjectField(param.thisObject, "mLabelViews");
				TextView localObject = (TextView) mLabelViews.get(0);
				localObject.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
			}
		});
	}

	public static void hookDisableStockBattery(LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.systemui.statusbar.policy.BatteryController", paramLoadPackageParam.classLoader, "onReceive", Context.class, Intent.class, new XC_MethodHook()
		{
			@SuppressWarnings("unchecked")
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				ArrayList<ImageView> mIconViews = (ArrayList<ImageView>) XposedHelpers.getObjectField(param.thisObject, "mIconViews");

				ImageView localImageView = (ImageView) mIconViews.get(0);
				localImageView.setVisibility(ImageView.GONE);
			}
		});
	}

	public static void hookBatteryController(LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.systemui.statusbar.policy.BatteryController", paramLoadPackageParam.classLoader, "onReceive", Context.class, Intent.class, new XC_MethodHook()
		{
			@SuppressWarnings("unchecked")
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				ArrayList<ImageView> mIconViews = (ArrayList<ImageView>) XposedHelpers.getObjectField(param.thisObject, "mIconViews");

				int level = XposedHelpers.getIntField(param.thisObject, "level");

				ImageView localImageView = (ImageView) mIconViews.get(0);
				Drawable battery = localImageView.getDrawable();

				float[] hsv = new float[3];
				if (level >= 50) {
					hsv[0] = 100;
					hsv[1] = 0.5F;
				} else {
					hsv[0] = level > 0 ? ((float) level * 2.0F) - 2.0F : 0.0F;
					hsv[1] = level > 0 ? (1.0F - ((float) level * 0.5F / (float) 50)) : 1.0F;
				}
				hsv[2] = 1.0F;
				int color = Color.HSVToColor(hsv);

				battery.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_ATOP);

			}
		});
	}

	public static void hookUseHeadsUp(LoadPackageParam paramLoadPackageParam)
	{
		/*
		 * XposedHelpers.findAndHookConstructor(
		 * "com.android.systemui.statusbar.BaseStatusBar",
		 * paramLoadPackageParam.classLoader, new XC_MethodHook() {
		 * 
		 * @Override protected void afterHookedMethod(MethodHookParam param)
		 * throws Throwable { XposedHelpers.setBooleanField(param.thisObject,
		 * "mUseHeadsUp", true); } });
		 */
		XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.BaseStatusBar", paramLoadPackageParam.classLoader, "start", new XC_MethodHook()
		{
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				Context mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
				android.provider.Settings.Global.putInt(mContext.getContentResolver(), "heads_up_enabled", 1);
			}
		});

		XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.BaseStatusBar", paramLoadPackageParam.classLoader, "shouldInterrupt", "android.service.notification.StatusBarNotification", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				Boolean replace = true;
				return replace;
			}
		});
		/*
		 * XposedHelpers.findAndHookMethod(
		 * "com.android.systemui.statusbar.policy.HeadsUpNotificationView",
		 * paramLoadPackageParam.classLoader, "setNotification",
		 * "com.android.systemui.statusbar.NotificationData.Entry", new
		 * XC_MethodReplacement() {
		 * 
		 * @Override protected Object replaceHookedMethod(MethodHookParam param)
		 * throws Throwable { XposedHelpers.setObjectField(param.thisObject,
		 * "mHeadsUp", param.args[0]); Object mHeadsUp =
		 * XposedHelpers.getObjectField(param.thisObject, "mHeadsUp"); Object
		 * row = XposedHelpers.getObjectField(mHeadsUp, "row");
		 * XposedHelpers.callMethod(row, "setExpanded", true);
		 * 
		 * Object mContentHolder =
		 * XposedHelpers.getObjectField(param.thisObject, "mContentHolder"); if
		 * (mContentHolder == null) { return Boolean.valueOf(false); }
		 * XposedHelpers.callMethod(mContentHolder, "setX", 0.0F);
		 * 
		 * XposedHelpers.callMethod(mContentHolder, "setX", 0.0F);
		 * XposedHelpers.callMethod(mContentHolder, "setVisibility", 0);
		 * XposedHelpers.callMethod(mContentHolder, "setAlpha", 1.0F);
		 * XposedHelpers.callMethod(mContentHolder, "removeAllViews");
		 * XposedHelpers.callMethod(mContentHolder, "addView", row);
		 * 
		 * Object mSwipeHelper = XposedHelpers.getObjectField(param.thisObject,
		 * "mSwipeHelper"); Object mContentSlider =
		 * XposedHelpers.getObjectField(param.thisObject, "mContentSlider");
		 * XposedHelpers.callMethod(mSwipeHelper, "snapChild", mContentSlider,
		 * 1.0F);
		 * 
		 * long mTouchSensitivityDelay = (Long)
		 * XposedHelpers.getObjectField(param.thisObject,
		 * "mTouchSensitivityDelay");
		 * 
		 * XposedHelpers.setLongField(param.thisObject, "mStartTouchTime",
		 * System.currentTimeMillis() + mTouchSensitivityDelay);
		 * 
		 * this.mHeadsUp = paramEntry; this.mHeadsUp.row.setExpanded(false); if
		 * (this.mContentHolder == null) { return false; }
		 * this.mContentHolder.setX(0.0F); this.mContentHolder.setVisibility(0);
		 * this.mContentHolder.setAlpha(1.0F);
		 * this.mContentHolder.removeAllViews();
		 * this.mContentHolder.addView(this.mHeadsUp.row);
		 * this.mSwipeHelper.snapChild(this.mContentSlider, 1.0F);
		 * this.mStartTouchTime = (System.currentTimeMillis() +
		 * this.mTouchSensitivityDelay); return true;
		 * 
		 * return Boolean.valueOf(true); } });
		 */
	}

	public static void hookOnTouchEvent(LoadPackageParam paramLoadPackageParam, final String string)
	{
		findAndHookMethod("com.android.systemui.statusbar.phone.NotificationPanelView", paramLoadPackageParam.classLoader, "onTouchEvent", "android.view.MotionEvent", new XC_MethodHook()
		{
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				MotionEvent paramMotionEvent = (MotionEvent) param.args[0];

				// TODO: landscape mode

				Context context = (Context) XposedHelpers.callMethod(param.thisObject, "getContext");
				int screenWidth = getScreenWidth(context);

				float option = Float.parseFloat(string);

				int side = Integer.parseInt(XMain.pref.getString(Const.TWEAK_QUICK_SETTINGS_SIDE, "0"));

				if (side == 0) {
					float limit = screenWidth - (screenWidth * option);

					if (paramMotionEvent.getY(0) <= 100 && paramMotionEvent.getX(0) >= limit) {
						Object StatusBar = XposedHelpers.getObjectField(param.thisObject, "mStatusBar");
						XposedHelpers.callMethod(StatusBar, "flipToSettings");
					}
				} else {
					float limit = (screenWidth * option);

					if (paramMotionEvent.getY(0) <= 100 && paramMotionEvent.getX(0) <= limit) {
						Object StatusBar = XposedHelpers.getObjectField(param.thisObject, "mStatusBar");
						XposedHelpers.callMethod(StatusBar, "flipToSettings");
					}
				}
			}
		});
	}

	public static void hookExpandedNotifications(LoadPackageParam paramLoadPackageParam)
	{
		if (Build.VERSION.SDK_INT < 19) {
			XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.NotificationData", paramLoadPackageParam.classLoader, "getUserExpanded", View.class, new XC_MethodReplacement()
			{
				@Override
				protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
				{
					return Boolean.valueOf(true);
				}
			});
			return;
		}

		XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.ExpandableNotificationRow", paramLoadPackageParam.classLoader, "isUserExpanded", new XC_MethodReplacement()
		{
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable
			{
				return Boolean.valueOf(true);
			}
		});
	}

	public static void handleColoredWiFi(InitPackageResourcesParam resparam, String path)
	{
		final XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);

		resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_0", new XResources.DrawableLoader()
		{
			public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
			{
				return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_0), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
			}
		});
		resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_1", new XResources.DrawableLoader()
		{
			public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
			{
				return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_1), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
			}
		});
		resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_2", new XResources.DrawableLoader()
		{
			public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
			{
				return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_2), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
			}
		});
		resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_3", new XResources.DrawableLoader()
		{
			public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
			{
				return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_3), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
			}
		});
		resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_4", new XResources.DrawableLoader()
		{
			public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
			{
				return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_4), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
			}
		});

		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_connected_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_0), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_in_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_0), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_inandout_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_0), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_out_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_0), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_connected_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_1), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_in_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_1), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_inandout_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_1), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_out_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_1), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_connected_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_2), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_in_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_2), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_inandout_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_2), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_out_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_2), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_connected_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_3), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_in_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_3), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_inandout_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_3), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_out_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_3), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_connected_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_4), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_in_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_4), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_inandout_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_4), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_wifi_signal_out_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_wifi_signal_4), Const.TWEAK_COLORED_WIFI_COLOR, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
	}

	public static void handleColoredData(InitPackageResourcesParam resparam, String path)
	{
		final XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);

		// DATA 2G
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_connected_2g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_2g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_inandout_2g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_2g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_in_2g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_2g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_out_2g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_2g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		// END DATA 2G

		// DATA 3G
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_sys_data_connected_3g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_3g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_sys_data_inandout_3g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_3g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_sys_data_in_3g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_3g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_sys_data_out_3g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_3g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_connected_3g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_3g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_inandout_3g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_3g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_in_3g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_3g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_out_3g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_3g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		// END DATA 3G

		// DATA 4G
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_connected_4g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_4g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_inandout_4g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_4g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_in_4g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_4g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_out_4g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_4g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END DATA 4G

		// DATA E
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_connected_e", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_e), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_inandout_e", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_e), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_in_e", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_e), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_out_e", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_e), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END DATA E

		// DATA G
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_connected_g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_inandout_g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_in_g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_out_g", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_g), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END DATA G

		// DATA H
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_connected_h", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_h), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_inandout_h", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_h), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_in_h", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_h), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_out_h", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_h), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END DATA H

		// DATA H+
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_connected_hplus", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_hplus), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_inandout_hplus", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_hplus), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_in_hplus", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_hplus), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_out_hplus", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_hplus), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END DATA H+

		// DATA LTE
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_connected_lte", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_connected_lte), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_inandout_lte", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_inandout_lte), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_in_lte", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_in_lte), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_data_out_lte", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_data_out_lte), Const.TWEAK_DATA_ICONS_COLOR, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END DATA LTE
	}

	public static void handleColoredSIM(InitPackageResourcesParam resparam, String path)
	{
		final XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);

		// 802w KK
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_5signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s1_5signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_5signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s1_5signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_5signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s1_5signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_5signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s1_5signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_5signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s1_5signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_5signal_5", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s1_5signal_5), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_5signal_null", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s1_5signal_null), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_5signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s2_5signal_0), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_5signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s2_5signal_1), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_5signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s2_5signal_2), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_5signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s2_5signal_3), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_5signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s2_5signal_4), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_5signal_5", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s2_5signal_5), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_5signal_null", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.cdma_stat_sys_s2_5signal_null), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END 802W KK

		// JB ROAMING
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_roaming_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_roaming_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_roaming_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_roaming_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_roaming_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s1_roaming_5", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_5), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_roaming_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_0), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_roaming_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_1), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_roaming_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_2), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_roaming_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_3), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_roaming_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_4), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "cdma_stat_sys_s2_roaming_5", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_5), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END JB ROAMING

		// KitKat ROAMING
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s1_r_5signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s1_r_5signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s1_r_5signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s1_r_5signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s1_r_5signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s1_r_5signal_5", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s1_r_5signal_5), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s2_r_5signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_0), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s2_r_5signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_1), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s2_r_5signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_2), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s2_r_5signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_3), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s2_r_5signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_4), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_s2_r_5signal_5", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_s2_r_5signal_5), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END KK ROAMING

		// 802D KK SIM1
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_4signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_4signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_4signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_4signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_4signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_4signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_4signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_4signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_4signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_4signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END 802W KK SIM1

		// 802W KK SIM2
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_4signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_4signal_0), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_4signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_4signal_1), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_4signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_4signal_2), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_4signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_4signal_3), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_4signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_4signal_4), Const.TWEAK_COLOR_SIM2, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		// END 802W KK SIM2

		// 802 KK SIM 1
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_5signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_5signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_5signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_5signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_5signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_5signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_5signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_5signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_5signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_5signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_5signal_5", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_5signal_5), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_5signal_null", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_5signal_null), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
		} catch (Throwable t) {

		}
		// END 802 KK

		// 802 KK SIM 1 ROAMING
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_r_5signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_r_5signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_r_5signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_r_5signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_r_5signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_r_5signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_r_5signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_r_5signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_r_5signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_r_5signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_r_5signal_5", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_r_5signal_5), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		// END 802 KK SIM 1 ROAMING

		// 802d KK 2GR
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_r_4signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_r_4signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_r_4signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_r_4signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_r_4signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_r_4signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_r_4signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_r_4signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_2g_r_4signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_2g_r_4signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		// 802d KK 2GR END

		// 802d KK 3GR
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g_r_4signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g_r_4signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g_r_4signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g_r_4signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g_r_4signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g_r_4signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g_r_4signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g_r_4signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g_r_4signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g_r_4signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		// 802d KK 3GR END

		// 802d KK 1XR
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_r_4signal_0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_r_4signal_0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_r_4signal_1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_r_4signal_1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_r_4signal_2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_r_4signal_2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_r_4signal_3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_r_4signal_3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_1x_r_4signal_4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_1x_r_4signal_4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		// 802d KK 1XR END

		// 802d KK 3GR1XR
		try {
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_r_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_r_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_r_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_r_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_r_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_r_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_r_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_r_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g0_r_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g0_r_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_r_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_r_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_r_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_r_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_r_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_r_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_r_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_r_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g1_r_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g1_r_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_r_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_r_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_r_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_r_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_r_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_r_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_r_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_r_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g2_r_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g2_r_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_r_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_r_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_r_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_r_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_r_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_r_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_r_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_r_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g3_r_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g3_r_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_r_1x4signal0", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_r_1x4signal0), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_r_1x4signal1", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_r_1x4signal1), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_r_1x4signal2", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_r_1x4signal2), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_r_1x4signal3", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_r_1x4signal3), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});
			resparam.res.setReplacement(resparam.packageName, "drawable", "stat_sys_3g4_r_1x4signal4", new XResources.DrawableLoader()
			{
				public Drawable newDrawable(XResources paramAnonymousXResources, int paramAnonymousInt) throws Throwable
				{
					return Misc.applyTheme(modRes.getDrawable(R.drawable.stat_sys_3g4_r_1x4signal4), Const.TWEAK_COLOR_SIM1, XMain.pref);
				}
			});

		} catch (Throwable t) {

		}
		// 802d KK 3GR1XR END
	}
}
