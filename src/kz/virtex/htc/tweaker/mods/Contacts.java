package kz.virtex.htc.tweaker.mods;

import kz.virtex.htc.tweaker.R;
import android.content.res.XModuleResources;
import android.view.View;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class Contacts
{
	public static void handleCallDirections(InitPackageResourcesParam resparam, String path)
	{
		XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);
		
		resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_incoming_light_s", modRes.fwd(R.drawable.icon_indicator_incoming_light_s));
		resparam.res.setReplacement(resparam.packageName, "drawable", "icon_indicator_outgoing_light_s", modRes.fwd(R.drawable.icon_indicator_outgoing_light_s));
	}
	
	
	public static void hookContactBadge(final InitPackageResourcesParam resparam) throws Throwable
	{
		resparam.res.hookLayout(resparam.packageName, "layout", "common_block_conversation", new XC_LayoutInflated()
		{
			@Override
			public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable
			{
				View photo = (View) liparam.view.findViewById(liparam.res.getIdentifier("thread_list_Badge", "id", resparam.packageName));
				photo.setVisibility(View.GONE);
			}
		});
	}
}
