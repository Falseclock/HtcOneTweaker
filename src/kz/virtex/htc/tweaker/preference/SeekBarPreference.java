package kz.virtex.htc.tweaker.preference;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.htc.preference.HtcSeekBarPreference;
import com.htc.widget.HtcSeekBar;

public class SeekBarPreference extends HtcSeekBarPreference implements
		SeekBar.OnSeekBarChangeListener {

	private HtcSeekBar mHtcSeekBar;
	private String mKey;
	private int mMax;
	private OnSeekBarTrackListener mListener;

	public interface OnSeekBarTrackListener {
		public void onSeekBarTrack(SeekBar paramSeekBar, int value);
	}

	public void setOnSeekBarTrackListener(OnSeekBarTrackListener listener) {
		mListener = listener;
	}

	public SeekBarPreference(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		mKey = paramAttributeSet.getAttributeValue(
				"http://schemas.android.com/apk/res/android", "key");

		TypedArray localTypedArray = paramContext.obtainStyledAttributes(
				paramAttributeSet, android.R.styleable.ProgressBar, 0, 0);
		mMax = localTypedArray.getInt(2, 5);

		localTypedArray.recycle();
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

		getAllChildren(view);

		mHtcSeekBar.setOnSeekBarChangeListener(this);
		mHtcSeekBar.setMax(mMax);
		mHtcSeekBar.setProgress(prefs.getInt(mKey, 0));
		mHtcSeekBar.setEnabled(isEnabled());

	}


	private ArrayList<View> getAllChildren(View v) {
		if (!(v instanceof ViewGroup)) {
			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			return viewArrayList;
		}

		ArrayList<View> result = new ArrayList<View>();

		ViewGroup vg = (ViewGroup) v;
		for (int i = 0; i < vg.getChildCount(); i++) {

			View child = vg.getChildAt(i);
			if (child instanceof HtcSeekBar) {
				mHtcSeekBar = (HtcSeekBar) child;
			}
			ArrayList<View> viewArrayList = new ArrayList<View>();
			viewArrayList.add(v);
			viewArrayList.addAll(getAllChildren(child));

			result.addAll(viewArrayList);
		}
		return result;
	}

	public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean arg2) {
		if (mListener != null) {
			mListener.onSeekBarTrack(paramSeekBar, paramInt);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar paramSeekBar) {

		getPreferenceManager().getSharedPreferences().edit().putInt(mKey, paramSeekBar.getProgress()).commit();
	}
}
