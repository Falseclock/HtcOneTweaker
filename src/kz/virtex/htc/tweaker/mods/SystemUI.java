package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.XMain;
import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class SystemUI
{
	public static void hookOnTouchEvent(LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.systemui.statusbar.phone.NotificationPanelView", paramLoadPackageParam.classLoader, "onTouchEvent", "android.view.MotionEvent", new XC_MethodHook()
		{
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				MotionEvent paramMotionEvent = (MotionEvent) param.args[0];

				// XposedBridge.log("X" + paramMotionEvent.getX(0));
				// XposedBridge.log("Y" + paramMotionEvent.getY(0));

				if (paramMotionEvent.getY(0) < 100 && paramMotionEvent.getX(0) > 1000)
				{
					Object StatusBar = XposedHelpers.getObjectField(param.thisObject, "mStatusBar");
					XposedHelpers.callMethod(StatusBar, "flipToSettings");
				}
			}
		});
	}

	public static void hookExpandedNotifications(LoadPackageParam paramLoadPackageParam)
	{
		if (Build.VERSION.SDK_INT < 19)
		{
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
		
		try
		{
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
	        
		}
		catch (Throwable t)
		{

		}
	}

	public static void handleColoredData(InitPackageResourcesParam resparam, String path)
	{
		final XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);

		// DATA 2G
		try
		{
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

		}
		catch (Throwable t)
		{

		}
		// END DATA 2G

		// DATA 3G
		try
		{
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

		}
		catch (Throwable t)
		{

		}
		try
		{
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

		}
		catch (Throwable t)
		{

		}
		// END DATA 3G
		
        // DATA 4G
		try
		{
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
		}
		catch (Throwable t)
		{

		}
        // END DATA 4G
		
        // DATA E
		try
		{
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
		}
		catch (Throwable t)
		{

		}
        // END DATA E
		
        // DATA G
		try
		{
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
		}
		catch (Throwable t)
		{

		}
        // END DATA G
		
        // DATA H
		try
		{
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
		}
		catch (Throwable t)
		{

		}
        // END DATA H
		
        // DATA H+
		try
		{
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
		}
		catch (Throwable t)
		{

		}
        // END DATA H+
		
        // DATA LTE
		try
		{
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
		}
		catch (Throwable t)
		{

		}
        // END DATA LTE
	}

	public static void handleColoredSIM(InitPackageResourcesParam resparam, String path)
	{
		final XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);

		// 802w KK
		try
		{
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
		}
		catch (Throwable t)
		{

		}
		// END 802W KK

		// JB ROAMING
		try
		{
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
		}
		catch (Throwable t)
		{

		}
		// END JB ROAMING

		// KitKat ROAMING
		try
		{
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
		}
		catch (Throwable t)
		{

		}
		// END KK ROAMING

		// 802D KK SIM1
		try
		{
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
		}
		catch (Throwable t)
		{

		}
		// END 802W KK SIM1

		// 802W KK SIM2
		try
		{
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

		}
		catch (Throwable t)
		{

		}
		// END 802W KK SIM2

		// 802 KK SIM 1
		try
		{
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
		}
		catch (Throwable t)
		{

		}
		// END 802 KK

		// 802 KK SIM 1 ROAMING
		try
		{
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

		}
		catch (Throwable t)
		{

		}
		// END 802 KK SIM 1 ROAMING
		
        // 802d KK 2GR
		try
		{
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

		}
		catch (Throwable t)
		{

		}
        // 802d KK 2GR END
		
        // 802d KK 3GR
		try
		{
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

		}
		catch (Throwable t)
		{

		}
        // 802d KK 3GR END
	}
}
