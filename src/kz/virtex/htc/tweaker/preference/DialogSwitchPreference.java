package kz.virtex.htc.tweaker.preference;

import kz.virtex.htc.tweaker.Misc;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.htc.preference.HtcPreference;
import com.htc.widget.HtcToggleButtonLight;
import com.htc.widget.HtcToggleButtonLight.OnCheckedChangeListener;

public final class DialogSwitchPreference extends HtcPreference implements HtcPreference.OnPreferenceClickListener
{
	private String mTitle;
	private String mSummary;
	private HtcToggleButtonLight mToggle;
	private IconsColorPreference mDialog;

	public DialogSwitchPreference(Context paramContext)
	{
		this(paramContext, null);
	}

	public DialogSwitchPreference(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);

		TypedArray attr = paramContext.obtainStyledAttributes(paramAttributeSet, android.R.styleable.Preference);
		mTitle = attr.getString(android.R.styleable.Preference_title);
		mSummary = attr.getString(android.R.styleable.Preference_summary);
		attr.recycle();

		mDialog = new IconsColorPreference(paramContext, paramAttributeSet);
		// setOnPreferenceChangeListener(this);
		// setOnPreferenceClickListener(this);
		init();
	}

	private void init()
	{

	}

	@Override
	protected void onBindView(View paramView)
	{
		super.onBindView(paramView);

		final LinearLayout widgetFrameView = ((LinearLayout) paramView.findViewById(android.R.id.widget_frame));
		widgetFrameView.setPadding(Misc.densify(0), Misc.densify(0), Misc.densify(-10), Misc.densify(0));

		mToggle = new HtcToggleButtonLight(getContext());
		mToggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(HtcToggleButtonLight arg0, boolean arg1)
			{
				Log.d("onCheckedChanged", "value: " + arg1);

			}
		});

		widgetFrameView.addView(mToggle);

		TextView title = (TextView) paramView.findViewById(android.R.id.title);
		title.setText(mTitle);

		TextView summary = (TextView) paramView.findViewById(android.R.id.summary);
		summary.setText(mSummary);

		paramView.setOnTouchListener(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1)
			{
				Log.d("setOnTouchListener", "setOnTouchListener: ");
				
				mDialog.callClickDialog();
				
				return false;
			}
		});
	}

	@Override
	public boolean onPreferenceClick(HtcPreference arg0)
	{
		return false;
	}
}
