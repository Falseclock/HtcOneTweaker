package kz.virtex.htc.tweaker.preference;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Main;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.interfaces.ColorPreferenceView;
import kz.virtex.htc.tweaker.interfaces.IconsRowView;
import kz.virtex.htc.tweaker.utils.ColorFilterGenerator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.htc.preference.HtcDialogPreference;

public class IconsColorPreference extends HtcDialogPreference implements ColorPreferenceView.OnColorChangeListener
{
	private String mKey;
	private ColorPreferenceView colorView;
	public IconsRowView iconRowView;
	public SharedPreferences prefs;
	private int color = Color.parseColor("#000000");

	public IconsColorPreference(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		mKey = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "key");
	}
	public void setColor(String clr)
	{
		color = Color.parseColor(clr);
	}

	protected View onCreateDialogView()
	{
		LinearLayout dialogLayout = new LinearLayout(getContext());
		dialogLayout.setOrientation(LinearLayout.VERTICAL);

		prefs = Main.preferences;
		
		int hueValue = prefs.getInt(mKey + "_hueValue", 0);
		int satValue = prefs.getInt(mKey + "_satValue", 0);
		int litValue = prefs.getInt(mKey + "_lightValue", 0);
		int conValue = prefs.getInt(mKey + "_conValue", 0);

		iconRowView = new IconsRowView(getContext(), null);
		iconRowView.init(Const.getImg(mKey));
		iconRowView.setBackgroundColor(color);
		dialogLayout.addView(iconRowView);
		
		colorView = new ColorPreferenceView(getContext());
		colorView.init(hueValue, satValue , litValue, conValue);
		colorView.setColorChangeListener(this);
		
		dialogLayout.addView(colorView);
		
		applyTheme(litValue, satValue, hueValue, conValue );

		return dialogLayout;
	}
	
	public void callClickDialog()
	{
		showDialog(this.getExtras());
	}
	
	private void applyTheme(int litValue, int satValue, int hueValue, int conValue)
	{
		ColorFilter localColorFilter = ColorFilterGenerator.adjustColor(litValue, conValue, satValue, hueValue);
		
		for (int i = 0; i < iconRowView.images.length; i++)
		{
			iconRowView.images[i].setColorFilter(new PorterDuffColorFilter(Color.CYAN, PorterDuff.Mode.SRC_ATOP));
			iconRowView.images[i].setColorFilter(localColorFilter);
		}
	}

	public void onColorChange(int lit, int con, int sat, int hue)
	{
		applyTheme(lit,sat,hue,con);
	}
		
	public void onClick(DialogInterface paramDialogInterface, int paramInt)
	{
		super.onClick(paramDialogInterface, paramInt);
		if (paramInt == -1)
		{
			Main.putInt(mKey + "_hueValue", colorView.hueValue);
			Main.putInt(mKey + "_satValue", colorView.satValue);
			Main.putInt(mKey + "_lightValue", colorView.litValue);
			Main.putInt(mKey + "_conValue", colorView.conValue);

			Misc.applyTheme(getPreferenceManager().findPreference(mKey).getIcon(), mKey, Main.preferences);
		}
		callChangeListener(this);
	}
}