package kz.virtex.htc.tweaker.mods;

import kz.virtex.htc.tweaker.R;
import android.content.res.XModuleResources;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class Launcher
{
	public static void execHook_HomeScreenGridSize(InitPackageResourcesParam paramRes, String paramString)
	{
		try
		{
			XModuleResources localResources = XModuleResources.createInstance(paramString, paramRes.res);
			
			int i = paramRes.res.getIdentifier("cell_count_y", "integer", "com.htc.launcher");
			paramRes.res.setReplacement(i, Integer.valueOf(6)); // height 4

			i = paramRes.res.getIdentifier("cell_count_x", "integer", "com.htc.launcher");
			paramRes.res.setReplacement(i, Integer.valueOf(5)); // width 4
			
			i = paramRes.res.getIdentifier("workspace_cell_height_port", "dimen", "com.htc.launcher");
			paramRes.res.setReplacement(i, localResources.fwd(R.dimen.workspace_cell_height_port6));
			
			i = paramRes.res.getIdentifier("workspace_cell_width_port", "dimen", "com.htc.launcher");
			paramRes.res.setReplacement(i, localResources.fwd(R.dimen.workspace_cell_width_port5));

		}
		catch (Throwable localThrowable)
		{
			XposedBridge.log(localThrowable);
		}
	}
}
