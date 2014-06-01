package kz.virtex.htc.tweaker.interfaces;

import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class ColorPreferenceView extends LinearLayout implements SeekBarView.OnSeekMoveListener
{
	private SeekBarView hueSeek;
	private SeekBarView satSeek;
	private SeekBarView litSeek;
	private SeekBarView conSeek;

	public int hueValue = 0;
	public int satValue = 0;
	public int litValue = 0;
	public int conValue = 0;

	private OnColorChangeListener mListener;

	public ColorPreferenceView(Context paramContext)
	{
		super(paramContext);
		setLayoutParams(new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		setOrientation(LinearLayout.VERTICAL);
	}
	
	private void setup()
	{
		satSeek = new SeekBarView(getContext());
		satSeek.setOnSeekMoveListener(this);
		satSeek.setValue(satValue, 200, getContext().getString(R.string.saturation_settings));
		satSeek.setId(1);

		hueSeek = new SeekBarView(getContext());
		hueSeek.setOnSeekMoveListener(this);
		hueSeek.setValue(hueValue, 360, getContext().getString(R.string.hue_settings));
		hueSeek.setId(2);

		litSeek = new SeekBarView(getContext());
		litSeek.setOnSeekMoveListener(this);
		litSeek.setValue(litValue, 200, getContext().getString(R.string.light_settings));
		litSeek.setId(3);

		conSeek = new SeekBarView(getContext());
		conSeek.setOnSeekMoveListener(this);
		conSeek.setValue(conValue, 200, getContext().getString(R.string.contrast_settings));
		conSeek.setId(4);
		conSeek.setVisibility(GONE);

		View lineView = new View(getContext());
		LayoutParams lineViewParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 1);
		lineViewParams.setMargins(Misc.densify(16), Misc.densify(16), Misc.densify(16), Misc.densify(16));
		lineView.setLayoutParams(lineViewParams);
		lineView.setPadding(0, 0, 0, 0);

		addView(hueSeek);
		addView(satSeek);
		addView(litSeek);
		addView(conSeek);
		addView(lineView);
	}

	@Override
	public void onSeekMove(SeekBarView paramSeekBar, int value)
	{
		switch (paramSeekBar.getId())
		{
		case 1:
			satValue = value;
			break;
		case 2:
			hueValue = value;
			break;
		case 3:
			litValue = value;
			break;
		case 4:
			conValue = value;
			break;
		default:
			return;
		}

		mListener.onColorChange(litValue, conValue, satValue, hueValue);
	}

	public interface OnColorChangeListener
	{
		public void onColorChange(int litValue, int conValue, int satValue, int hueValue);
	}

	public void setColorChangeListener(OnColorChangeListener listener)
	{
		mListener = listener;
	}

	public void init(int hue, int sat, int lit, int con)
	{
		hueValue = hue;
		satValue = sat;
		litValue = lit;
		conValue = con;
		
		setup();
	}
}
