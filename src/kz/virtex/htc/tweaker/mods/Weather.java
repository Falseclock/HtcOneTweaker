package kz.virtex.htc.tweaker.mods;

import java.io.File;
import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Misc;
import android.content.res.XModuleResources;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class Weather
{

	public static void handleColorWeatherIcon(InitPackageResourcesParam resparam, String path)
	{
		resparam.res.hookLayout("com.htc.Weather", "layout", "specific_tab_list_item_forecast", new XC_LayoutInflated() {
	        @Override
	        public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
	        	
	        	ImageView icon = (ImageView) liparam.view.findViewById( liparam.res.getIdentifier("forecast_icon", "id", "com.htc.Weather"));
	        	
	        	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(196, 196);
	        	layoutParams.setMargins(Misc.densify(7), Misc.densify(0), Misc.densify(0), Misc.densify(0));
	        	icon.setLayoutParams(layoutParams);        	
	        }
	    });
		
		resparam.res.hookLayout("com.htc.Weather", "layout", "specific_tab_list_item_hour", new XC_LayoutInflated() {
	        @Override
	        public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {
	        	ImageView icon = (ImageView) liparam.view.findViewById( liparam.res.getIdentifier("hour_icon", "id", "com.htc.Weather"));
	        	
	        	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(196, 196);
	        	layoutParams.setMargins(Misc.densify(7), Misc.densify(0), Misc.densify(0), Misc.densify(0));
	        	icon.setLayoutParams(layoutParams);        	
	        }
	    });
	}
	
	public static void handleColorWeather(InitPackageResourcesParam resparam, String path)
	{
		try
		{
			if (path != null)
			{
				File file = new File(path);

				if (file.exists())
				{
					XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);

					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_01", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_01", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_02", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_02", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_03", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_03", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_04", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_04", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_05", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_05", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_06", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_06", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_07", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_07", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_08", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_08", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_11", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_11", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_12", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_12", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_13", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_13", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_14", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_14", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_15", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_15", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_16", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_16", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_17", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_17", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_18", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_18", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_19", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_19", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_20", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_20", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_21", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_21", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_22", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_22", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_23", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_23", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_24", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_24", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_25", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_25", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_26", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_26", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_29", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_29", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_30", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_30", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_31", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_31", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_32", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_32", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_33", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_33", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_34", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_34", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_35", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_35", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_36", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_36", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_37", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_37", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_38", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_38", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_39", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_39", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_40", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_40", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_41", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_41", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_42", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_42", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_43", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_43", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_44", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_44", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_51", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_51", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_52", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_52", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_53", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_53", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_colorgraphic_xl_54", modRes.fwd(modRes.getIdentifier("weather_colorgraphic_xl_54", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_01", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_01", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_02", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_02", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_03", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_03", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_04", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_04", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_05", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_05", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_06", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_06", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_07", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_07", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_08", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_08", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_11", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_11", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_12", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_12", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_13", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_13", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_14", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_14", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_15", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_15", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_16", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_16", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_17", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_17", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_18", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_18", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_19", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_19", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_20", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_20", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_21", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_21", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_22", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_22", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_23", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_23", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_24", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_24", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_25", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_25", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_26", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_26", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_29", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_29", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_30", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_30", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_31", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_31", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_32", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_32", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_33", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_33", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_34", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_34", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_35", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_35", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_36", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_36", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_37", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_37", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_38", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_38", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_39", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_39", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_40", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_40", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_41", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_41", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_42", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_42", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_43", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_43", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_44", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_44", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_51", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_51", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_52", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_52", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_53", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_53", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_l_54", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_l_54", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_01", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_01", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_02", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_02", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_03", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_03", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_04", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_04", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_05", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_05", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_06", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_06", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_07", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_07", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_08", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_08", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_11", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_11", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_12", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_12", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_13", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_13", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_14", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_14", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_15", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_15", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_16", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_16", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_17", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_17", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_18", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_18", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_19", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_19", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_20", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_20", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_21", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_21", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_22", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_22", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_23", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_23", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_24", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_24", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_25", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_25", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_26", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_26", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_29", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_29", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_30", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_30", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_31", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_31", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_32", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_32", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_33", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_33", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_34", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_34", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_35", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_35", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_36", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_36", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_37", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_37", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_38", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_38", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_39", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_39", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_40", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_40", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_41", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_41", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_42", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_42", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_43", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_43", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_44", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_44", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_51", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_51", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_52", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_52", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_53", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_53", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_dark_xl_54", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_dark_xl_54", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_01", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_01", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_02", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_02", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_03", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_03", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_04", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_04", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_05", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_05", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_06", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_06", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_07", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_07", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_08", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_08", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_11", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_11", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_12", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_12", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_13", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_13", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_14", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_14", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_15", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_15", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_16", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_16", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_17", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_17", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_18", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_18", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_19", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_19", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_20", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_20", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_21", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_21", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_22", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_22", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_23", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_23", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_24", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_24", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_25", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_25", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_26", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_26", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_29", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_29", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_30", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_30", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_31", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_31", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_32", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_32", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_33", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_33", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_34", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_34", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_35", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_35", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_36", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_36", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_37", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_37", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_38", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_38", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_39", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_39", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_40", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_40", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_41", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_41", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_42", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_42", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_43", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_43", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_44", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_44", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_51", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_51", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_52", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_52", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_53", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_53", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_l_54", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_54", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_01", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_01", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_02", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_02", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_03", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_03", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_04", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_04", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_05", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_05", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_06", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_06", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_07", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_07", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_08", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_08", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_11", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_11", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_12", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_12", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_13", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_13", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_14", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_14", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_15", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_15", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_16", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_16", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_17", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_17", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_18", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_18", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_19", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_19", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_20", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_20", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_21", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_21", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_22", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_22", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_23", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_23", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_24", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_24", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_25", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_25", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_26", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_26", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_29", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_29", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_30", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_30", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_31", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_31", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_32", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_32", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_33", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_33", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_34", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_34", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_35", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_35", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_36", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_36", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_37", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_37", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_38", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_38", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_39", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_39", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_40", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_40", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_41", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_41", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_42", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_42", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_43", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_43", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_44", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_44", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_51", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_51", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_52", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_52", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_53", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_53", "drawable", Const.WEATHER_PACKAGE_NAME)));
					resparam.res.setReplacement(resparam.packageName, "drawable", "weather_vectorgraphic_light_m_54", modRes.fwd(modRes.getIdentifier("weather_vectorgraphic_light_l_54", "drawable", Const.WEATHER_PACKAGE_NAME)));

				}
			}
		}
		catch (Throwable t)
		{
			XposedBridge.log(t);
		}
	}
}
