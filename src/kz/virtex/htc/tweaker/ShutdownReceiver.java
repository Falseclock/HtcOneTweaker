package kz.virtex.htc.tweaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ShutdownReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context arg0, Intent arg1)
	{
		Log.d("kz.virtex.htc.tweaker","ShutdownReceiver");
	}
}
