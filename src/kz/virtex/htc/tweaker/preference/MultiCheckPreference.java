package kz.virtex.htc.tweaker.preference;

import java.util.ArrayList;

import com.htc.preference.HtcDialogPreference;
import com.htc.widget.HtcCheckBox;

import kz.virtex.htc.tweaker.Main;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.interfaces.CheckboxPreferenceView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MultiCheckPreference extends HtcDialogPreference implements CheckboxPreferenceView.OnCheckListener
{
	public CharSequence[] titles;
	public CharSequence[] keys;
	public CharSequence[] summs;
	public String mKey;
	public ArrayList<Row> rows;
	public SharedPreferences prefs;
	public boolean useAtLeastOne = true;

	public MultiCheckPreference(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		prefs = Main.preferences;

		mKey = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "key");

		TypedArray a = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.MultiCheckbox);
		titles = a.getTextArray(R.styleable.MultiCheckbox_entryTitles);
		keys = a.getTextArray(R.styleable.MultiCheckbox_entryKeys);
		summs = a.getTextArray(R.styleable.MultiCheckbox_entrySummaries);

		if (summs == null) {
			summs = new CharSequence[titles.length];
		}
		a.recycle();

		rows = new ArrayList<Row>();
		for (int i = 0; i < titles.length; i++) {
			rows.add(new Row(titles[i], summs[i], keys[i]));
		}
	}

	public void setAllChecked()
	{
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).setState(true);
		}
		callChangeListener(rows);
	}

	public void setAllUnChecked()
	{
		for (int i = 0; i < rows.size(); i++) {
			rows.get(i).setState(false);
		}
		callChangeListener(rows);
	}

	public void setChecked(int i)
	{
		rows.get(i).setState(true);
		callChangeListener(rows);
	}

	public void unsetMinimum()
	{
		useAtLeastOne = false;
	}

	public void setMinimum()
	{
		useAtLeastOne = true;
	}

	@Override
	public void onCheck(String key, HtcCheckBox checkBox)
	{
		rows.get(checkBox.getId()).setState(checkBox.isChecked());

		if (useAtLeastOne) {
			if (checkBox.isChecked() == false) {
				boolean found = false;
				for (int i = 0; i < rows.size(); i++) {
					if (rows.get(i).getState() == true) {
						found = true;
					}
				}
				if (found == false) {
					rows.get(checkBox.getId()).setState(true);
					checkBox.setChecked(true);
					return;
				}
			}
		}
	}

	public void onClick(DialogInterface paramDialogInterface, int paramInt)
	{
		super.onClick(paramDialogInterface, paramInt);
		if (paramInt == DialogInterface.BUTTON_POSITIVE) {
			for (int i = 0; i < rows.size(); i++) {
				Main.putBoolean(mKey + "_" + rows.get(i).getKey(), rows.get(i).getState());
			}
		}
		callChangeListener(rows);
	}

	protected View onCreateDialogView()
	{
		ListView list = new ListView(getContext());
		list.setAdapter(new CheckPreferenceAdapter(this, rows));
		list.setDividerHeight(2);

		return list;
	}

	public class Row
	{
		public String mTitle;
		public String mSummary;
		public String mKey;
		public boolean mState;

		public Row(CharSequence title, CharSequence summary, CharSequence key)
		{
			mTitle = title.toString();
			mSummary = summary == null ? null : summary.toString();
			mKey = key.toString();
			mState = MultiCheckPreference.this.prefs.getBoolean(MultiCheckPreference.this.mKey + "_" + mKey, true);
		}

		public String getKey()
		{
			return mKey;
		}

		public String getSummary()
		{
			return mSummary;
		}

		public String getTitle()
		{
			return mTitle;
		}

		public boolean getState()
		{
			return mState;
		}

		public void setState(boolean state)
		{
			mState = state;
		}
	}

	public class CheckPreferenceAdapter extends BaseAdapter
	{
		ArrayList<Row> data = new ArrayList<Row>();
		Context context;

		public CheckPreferenceAdapter(MultiCheckPreference multiCheckPreference, ArrayList<Row> paramRow)
		{
			if (paramRow != null) {
				data = paramRow;
			}
			this.context = multiCheckPreference.getContext();
		}

		public int getCount()
		{
			return data.size();
		}

		public Object getItem(int paramInt)
		{
			return data.get(paramInt);
		}

		public long getItemId(int paramInt)
		{
			return paramInt;
		}

		public View getView(int position, View convertView, ViewGroup parent)
		{
			CheckboxPreferenceView returnView = new CheckboxPreferenceView(context);
			returnView.setOnCheckListener(MultiCheckPreference.this);
			returnView.checkBox.setChecked(data.get(position).mState);
			returnView.checkBox.setId(position);

			TextView title = returnView.textTitle;
			title.setText(data.get(position).mTitle);
			title.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));

			if (data.get(position).mSummary != null) {
				TextView summary = returnView.textSummary;
				summary.setText(data.get(position).mSummary);
				summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, Misc.densify(5));
			}

			returnView.key = data.get(position).mKey;

			return returnView;
		}
	}
}
