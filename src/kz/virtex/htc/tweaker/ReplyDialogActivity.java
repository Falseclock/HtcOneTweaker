package kz.virtex.htc.tweaker;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import com.htc.widget.HtcAlertDialog;
import com.htc.widget.HtcAlertDialog.Builder;
import com.htc.widget.HtcEditText;

public class ReplyDialogActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        showReplyDialog();
    }
    
    private void showReplyDialog()
    {
		Builder builder = new HtcAlertDialog.Builder(this);
		builder.setCancelable(true);
		builder.setTitle("Title");
		
		HtcEditText input = new HtcEditText(this);
		input.setMaxLines(5);
		builder.setView(input);
		 
		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		HtcAlertDialog alert = builder.create();
		alert.show();
    }
}
