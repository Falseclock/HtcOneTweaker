package kz.virtex.htc.tweaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.Contacts.People;
import android.widget.Toast;

public class TweakerBroadcastReceiver extends BroadcastReceiver {

	public static final String ACTION_DELETE_MESSAGE = "tweaker.intent.action.DELETE_MESSAGE";
	public static final String ACTION_CALL_TO_CONTACT = "tweaker.intent.action.CALL_TO_CONTACT";
	public static final String ACTION_MARK_THREAD_READ = "tweaker.intent.action.MARK_THREAD_READ";
	public static final String ACTION_REPLY_MESSAGE = "tweaker.intent.action.REPLY_MESSAGE";

	@Override
	public void onReceive(Context paramContext, Intent paramIntent)
	{
		
		Vibrator vibrator = (Vibrator) paramContext.getSystemService(Context.VIBRATOR_SERVICE);
	    vibrator.vibrate(10);

		if (paramIntent != null) {
			String action = paramIntent.getAction();

			if (action == null)
				action = "UNKNOWN";
		}

		Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		paramContext.sendBroadcast(it);

		Toast.makeText(paramContext,
				"ContactId: " + paramIntent.getLongExtra("ContactId", 0L) + ", Sender: " + paramIntent.getStringExtra("Sender"),
				Toast.LENGTH_SHORT).show();
		
		Uri localUri1 = Uri.parse("content://mms-sms-v2/get-contactID");
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/" + paramIntent.getLongExtra("ContactId", 0L)));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		paramContext.startActivity(intent);

		 
		/*
		Intent intent = new Intent(paramContext, ReplyDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		paramContext.startActivity(intent);
		paramContext.sendBroadcast(intent);
		*/
		
		// paramContext.unregisterReceiver(this);
	}
}
