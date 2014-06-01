package kz.virtex.htc.tweaker.interfaces;

import kz.virtex.htc.tweaker.Misc;
import com.htc.widget.HtcSeekBar;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

public class SeekBarView  extends LinearLayout implements SeekBar.OnSeekBarChangeListener
{
	private HtcSeekBar seekSlider;
	private static int seekValue = 0;
	private TextView sliderTitleVal;
	private OnSeekMoveListener mListener;
	private int maxValue;
	
	public SeekBarView(Context paramContext)
	{
		super(paramContext);
		setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		setOrientation(LinearLayout.VERTICAL);
	}

	public void setValue(int value, int max, String title)
	{
		createLayout(value, max, title);
	}
	
	public int getVal()
	{
		return seekValue;
	}

	private void createLayout(int value, int max, String text)
	{
		maxValue = max;
				
		RelativeLayout textValueLayout = new RelativeLayout(getContext());
		textValueLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		textValueLayout.setPadding(0, Misc.densify(4), 0, Misc.densify(4));
		
		
		TextView sliderTextVal = new TextView(getContext());
		sliderTextVal.setText(text);
		sliderTextVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, Misc.densify(6));
		sliderTextVal.setPadding(Misc.densify(16), Misc.densify(4), Misc.densify(4), Misc.densify(4));
		sliderTextVal.setTextColor(Color.parseColor("#2c2e2f"));
		RelativeLayout.LayoutParams localTextViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		localTextViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		sliderTextVal.setLayoutParams(localTextViewParams);

		
		sliderTitleVal = new TextView(getContext());
		sliderTitleVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, Misc.densify(6));
		
	
		sliderTitleVal.setText(String.valueOf(value));
		sliderTitleVal.setTextColor(Color.parseColor("#2c2e2f"));
		sliderTitleVal.setPadding(0, Misc.densify(4), Misc.densify(16), Misc.densify(4));
		RelativeLayout.LayoutParams hueTitleValParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		hueTitleValParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		sliderTitleVal.setLayoutParams(hueTitleValParams);
		
			
		LinearLayout hueSliderLayout = new LinearLayout(getContext());
		hueSliderLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		seekSlider = new HtcSeekBar(getContext());
		seekSlider.setMax(max);
		seekSlider.setOnSeekBarChangeListener(this);
		seekSlider.setProgress(value + (maxValue/2));
		seekSlider.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		textValueLayout.addView(sliderTextVal);
		textValueLayout.addView(sliderTitleVal);
		hueSliderLayout.addView(seekSlider);
		
		addView(textValueLayout);
		addView(hueSliderLayout);		
		
		return;
	}
	
	public void onProgressChanged(SeekBar paramSeekBar, int value, boolean arg2)
	{
		sliderTitleVal.setText(String.valueOf(value - (maxValue/2)));
		seekValue = value - (maxValue/2);
		
		mListener.onSeekMove(this, seekValue);
	}

	public interface OnSeekMoveListener
	{
		public void onSeekMove(SeekBarView paramSeekBar, int value);
	}
	
	public void setOnSeekMoveListener(OnSeekMoveListener listener)
	{
		mListener = listener;
	}
	
	public void onStartTrackingTouch(SeekBar arg0)
	{

	}

	public void onStopTrackingTouch(SeekBar arg0)
	{

	}
}
