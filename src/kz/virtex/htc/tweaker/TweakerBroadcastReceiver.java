package kz.virtex.htc.tweaker;

import java.text.NumberFormat;
import java.text.ParsePosition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Vibrator;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.util.Log;

public class TweakerBroadcastReceiver extends BroadcastReceiver
{

	public static final String ACTION_DELETE_MESSAGE = "tweaker.intent.action.DELETE_MESSAGE";
	public static final String ACTION_CALL_TO_CONTACT = "tweaker.intent.action.CALL_TO_CONTACT";
	public static final String ACTION_MARK_THREAD_READ = "tweaker.intent.action.MARK_THREAD_READ";
	public static final String ACTION_REPLY_MESSAGE = "tweaker.intent.action.REPLY_MESSAGE";
	private Context mContext;

	public static boolean isNumeric(String str)
	{
		if (str == null) return false;
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
	}

	public void getPhoneNumbers(String id)
	{
		Log.d("getPhoneNumbers", "looking data for contact id: " + id);
		Cursor pCur = mContext.getContentResolver().query(Data.CONTENT_URI, new String[]
		{ Data._ID, Phone.NUMBER, Phone.TYPE, Phone.LABEL }, Data.CONTACT_ID + "=?" + " AND "
                + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'", new String[]
		{ String.valueOf(id) }, null);

		while (pCur.moveToNext())
		{
			String phoneNumber = pCur.getString(1);
			String phoneType = pCur.getString(2);
			String phoneLabel = pCur.getString(3);

			if (isNumeric(phoneType))
			{
				Log.d("getPhoneNumbers", "phoneNumber: " + phoneNumber + ", phoneType: " + phoneType + ", phoneLabel: " + phoneLabel);
			}
		}
		pCur.close();
		return;
	}

	@Override
	public void onReceive(Context paramContext, Intent paramIntent)
	{
		mContext = paramContext;

		Vibrator vibrator = (Vibrator) paramContext.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(10);

		if (paramIntent != null)
		{
			String action = paramIntent.getAction();

			if (action == null)
				action = "UNKNOWN";
		}

		Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		paramContext.sendBroadcast(it);

		Long ContactId = paramIntent.getLongExtra("ContactId", 0L);

		// Toast.makeText(paramContext, "ContactId: " + ContactId + ", Sender: "
		// + paramIntent.getStringExtra("Sender"), Toast.LENGTH_SHORT).show();

		getPhoneNumbers(ContactId.toString());

		// Intent intent = new Intent(Intent.ACTION_VIEW,
		// Uri.parse("content://contacts/people/" +
		// paramIntent.getLongExtra("ContactId", 0L)));
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// paramContext.startActivity(intent);

		/*
		 * Intent intent = new Intent(paramContext, ReplyDialogActivity.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 * 
		 * paramContext.startActivity(intent);
		 * paramContext.sendBroadcast(intent);
		 */

		// paramContext.unregisterReceiver(this);
	}
}
