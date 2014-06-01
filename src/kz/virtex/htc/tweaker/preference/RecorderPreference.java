package kz.virtex.htc.tweaker.preference;

import com.htc.preference.HtcPreference;
import com.htc.preference.HtcSwitchPreference;

import android.content.Context;

import android.provider.Settings;
import android.util.AttributeSet;

public final class RecorderPreference extends HtcSwitchPreference implements HtcSwitchPreference.OnPreferenceChangeListener
{
	private String mKey;
	HtcSwitchPreference AutoRecording;

	public RecorderPreference(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		mKey = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "key");
		this.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(HtcPreference preference, Object object)
	{
		Integer value = 0;
		if (object.toString() == "true")
			value = 1;
		Settings.System.putInt(preference.getContext().getContentResolver(), mKey, value);

		return true;
	}
}
