package kz.virtex.htc.tweaker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Settings extends Activity {

	public void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);

		Intent intent = new Intent(this, Main.class);
	    startActivity(intent);
	    
	    this.finish();
	}
}
