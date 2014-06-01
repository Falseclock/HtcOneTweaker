package kz.virtex.htc.tweaker.mods;

import android.content.res.XModuleResources;
import kz.virtex.htc.tweaker.R;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class Keyboard
{
	public static void handlePopup(InitPackageResourcesParam resparam, String path)
	{
		XModuleResources modRes = XModuleResources.createInstance(path, resparam.res);
		
		resparam.res.setReplacement(resparam.packageName, "xml", "qwerty_rus", modRes.fwd(R.xml.qwerty_rus));
		resparam.res.setReplacement(resparam.packageName, "xml", "row_rus1", modRes.fwd(R.xml.row_rus1));
		resparam.res.setReplacement(resparam.packageName, "xml", "row_rus3", modRes.fwd(R.xml.row_rus3));
		resparam.res.setReplacement(resparam.packageName, "xml", "row_rus5", modRes.fwd(R.xml.row_rus5));

		resparam.res.setReplacement(resparam.packageName, "xml", "land_qwerty_rus", modRes.fwd(R.xml.land_qwerty_rus));
		resparam.res.setReplacement(resparam.packageName, "xml", "row_land_rus1", modRes.fwd(R.xml.row_land_rus1));
		resparam.res.setReplacement(resparam.packageName, "xml", "row_land_rus3", modRes.fwd(R.xml.row_land_rus3));
		resparam.res.setReplacement(resparam.packageName, "xml", "row_land_rus5", modRes.fwd(R.xml.row_land_rus5));
	}
}
