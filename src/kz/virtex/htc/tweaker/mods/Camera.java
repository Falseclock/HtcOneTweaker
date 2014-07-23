package kz.virtex.htc.tweaker.mods;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import java.text.SimpleDateFormat;
import java.util.Date;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.XMain;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

@SuppressLint("SimpleDateFormat")
public class Camera
{
	public static void hookCameraPrefix(final LoadPackageParam paramLoadPackageParam, String packageName, final String format)
	{
		XposedHelpers.findAndHookConstructor(packageName + ".io.DCFPath", paramLoadPackageParam.classLoader, "java.lang.String", "java.lang.String", packageName + ".io.FileCounter", packageName+".io.FileCounter", new XC_MethodHook()
		{
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable
			{
				String file = (String) param.args[1];

				String[] tokens = file.split("\\.(?=[^\\.]+$)");
				String ext = tokens[1];
				String name = tokens[0];
				String date = new SimpleDateFormat(format).format(new Date());

				int posVal = Integer.parseInt(XMain.pref.getString(Const.TWEAK_PHOTO_PREFIX_POSITION, "0"));
				
				if (posVal == 0) {
					param.args[1] = date + "_" + name + "." + ext;
				} else {
					param.args[1] = name + "_" + date + "." + ext;						
				}
			}
		});
	}

	public static void hookCameraActivity(final LoadPackageParam paramLoadPackageParam)
	{
		findAndHookMethod("com.android.camera.HTCCamera", paramLoadPackageParam.classLoader, "onCreate", "android.os.Bundle", new XC_MethodHook()
		{
			@SuppressWarnings("unused")
			protected void afterHookedMethod(MethodHookParam param) throws Throwable
			{
				Context context = (Context) XposedHelpers.callMethod(param.thisObject, "getApplicationContext");
				Window window = (Window) XposedHelpers.callMethod(param.thisObject, "getWindow");
				View view = window.getDecorView().getRootView();

				TextView widget = new TextView(context);
				widget.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48.0F);
				widget.setTextColor(Color.parseColor("#ff0000"));
				widget.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
				widget.setGravity(Gravity.CENTER);
				widget.setText("Oba NA! Ugol Show!");

				int resID = context.getResources().getIdentifier("camera_layout", "id", context.getPackageName());
				XposedBridge.log("camera_layout: " + resID);
				RelativeLayout layout = (RelativeLayout) view.findViewById(resID);

				// layout.addView(widget);

			}
		});
	}
}
