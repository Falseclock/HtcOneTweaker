package kz.virtex.htc.tweaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context paramContext, Intent paramIntent)
	{
		paramContext.startService(new Intent(paramContext, TweakerService.class));
	}
}
