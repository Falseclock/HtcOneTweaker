package kz.virtex.htc.tweaker.preference;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.htc.preference.HtcDialogPreference;
import com.htc.widget.HtcNumberPicker;
import com.htc.widget.HtcNumberPicker.OnScrollIdleStateListener;

import kz.virtex.htc.tweaker.Main;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class NumberPickerPreference extends HtcDialogPreference implements OnScrollIdleStateListener
{
	private SharedPreferences prefs;
	private String mKey;
	private int rangeStart;
	private int rangeEnd;
	private String hint;
	private CharSequence[] entries;
	public TextView widget;
	public HtcNumberPicker picker;
	private int pickerIndex = 0;
	private String[] pickerEntries;
	private String[] pickerScrolledEntries;
	private int storedValue;
	private boolean dataScrolled = false;
	private int defaultValue;

	public NumberPickerPreference(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);

		mKey = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "key");

		prefs = Main.preferences;

		TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.NumberPicker);
		rangeStart = a.getInt(R.styleable.NumberPicker_rangeStart, 0);
		rangeEnd = a.getInt(R.styleable.NumberPicker_rangeEnd, 10);
		
		defaultValue = a.getInt(R.styleable.NumberPicker_defaultValue, 0);
		storedValue = prefs.getInt(mKey, defaultValue);

		hint = a.getString(R.styleable.NumberPicker_hint);
		a.recycle();

		a = paramContext.obtainStyledAttributes(paramAttributeSet, android.R.styleable.ListView);
		entries = a.getTextArray(android.R.styleable.ListView_entries);

		a.recycle();
	}

	protected void onBindView(View view)
	{
		super.onBindView(view);

		LinearLayout widgetFrameView = ((LinearLayout) view.findViewById(android.R.id.widget_frame));
		widgetFrameView.setBackgroundResource(R.drawable.btn_gray);
		widgetFrameView.setLayoutParams(new LinearLayout.LayoutParams(170, 110));
		widgetFrameView.setGravity(Gravity.CENTER_VERTICAL);

		widget = new TextView(getContext());
		widget.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28.0F);
		widget.setTextColor(Color.parseColor("#4c5052"));
		widget.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
		widget.setGravity(Gravity.CENTER);
		widget.setText(String.valueOf(storedValue));
		widgetFrameView.addView(widget);
	}
	

	protected View onCreateDialogView()
	{
		//Log.d("onCreateDialogView", "onCreateDialogView : storedValue = " + storedValue);

		LinearLayout dialogLayout = new LinearLayout(getContext());
		dialogLayout.setOrientation(LinearLayout.VERTICAL);
		dialogLayout.setPadding(Misc.densify(0), Misc.densify(10), Misc.densify(0), Misc.densify(5));

		picker = new HtcNumberPicker(getContext());
		picker.setRepeatEnable(true);
		picker.setOnScrollIdleStateListener(this);

		if (entries != null && entries.length > 0)
		{
			pickerEntries = new String[entries.length];
			pickerScrolledEntries = new String[entries.length];
			int i = 0;
			for (CharSequence ch : entries)
			{
				pickerEntries[i] = ch.toString();
				pickerScrolledEntries[i] = ch.toString();
				i++;
			}
			rangeStart = 0;
			rangeEnd = i-1;
		}
		else
		{
			pickerEntries = new String[rangeEnd - rangeStart + 1];
			pickerScrolledEntries = new String[rangeEnd - rangeStart + 1];

			int i = 0;
			while (i <= rangeEnd - rangeStart)
			{
				pickerEntries[i] = String.valueOf(rangeStart + i);
				pickerScrolledEntries[i] = String.valueOf(rangeStart + i);
				i++;
			}
		}
		
		List <String> list = Arrays.asList(pickerScrolledEntries);
		Collections.reverse(list);
		pickerScrolledEntries = (String[]) list.toArray();

		picker.setRange(rangeStart, rangeEnd, pickerEntries);
		setCenter(pickerEntries);
		
		TextView hintView = new TextView(getContext());
		hintView.setText(hint);
		hintView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Misc.densify(4));
		hintView.setPadding(Misc.densify(0), Misc.densify(4), Misc.densify(0), Misc.densify(4));
		hintView.setTextColor(Color.GRAY);
		hintView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		hintView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

		dialogLayout.addView(picker);
		dialogLayout.addView(hintView);

		return dialogLayout;
	}

	private void setCenter(String[] mEntriesString)
	{
		for (int i = 0; i < mEntriesString.length; i++)
		{
			if (Integer.parseInt(mEntriesString[i]) == storedValue)
			{
				picker.setCenter(i);
				pickerIndex = i;

				//Log.d("setCenter", "setCenter : index = " + pickerIndex);
			}
		}
	}

	public void onClick(DialogInterface paramDialogInterface, int paramInt)
	{
		super.onClick(paramDialogInterface, paramInt);

		//Log.d("onClick", "onClick : pickerIndex = " + pickerIndex);

		if (paramInt == DialogInterface.BUTTON_POSITIVE)
		{
			if (pickerEntries != null && pickerEntries.length > 0)
			{
				if (dataScrolled)
				{
					storedValue = Integer.parseInt((String) pickerScrolledEntries[pickerIndex]);
				}
				else
				{
					storedValue = Integer.parseInt((String) pickerEntries[pickerIndex]);
				}
				//Log.d("onClick", "onClick : storedValue = " + storedValue);

				Main.putInt(mKey, storedValue);
				widget.setText(String.valueOf(storedValue));
			}
		}
		dataScrolled = false;
	}
	@Override
	public void onDataSet(HtcNumberPicker obj, int index)
	{
		dataScrolled = true;
		pickerIndex = index;
		//Log.d("onDataSet", "onDataSet : index = " + index);

		Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(20);
	}
}
