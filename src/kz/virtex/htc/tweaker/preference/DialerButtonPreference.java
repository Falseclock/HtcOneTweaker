package kz.virtex.htc.tweaker.preference;

import kz.virtex.htc.tweaker.Const;
import kz.virtex.htc.tweaker.Main;
import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import kz.virtex.htc.tweaker.interfaces.ColorPickerView;
import kz.virtex.htc.tweaker.interfaces.DialerButtonView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.htc.preference.HtcDialogPreference;

public class DialerButtonPreference extends HtcDialogPreference implements ColorPickerView.OnColorChangedListener, ColorPickerView.OnPinchListener
{
	private String mKey;
	private Context mContext;
	private SharedPreferences prefs = null;
	private DialerButtonView mOldButton;
	private DialerButtonView mNewButton;
	private String key = "latin";

	private int textCurrColor;
	private float textCurrSize;
	private int textCurrColorLat;
	private float textCurrSizeLat;

	private float textMinSize = Const.DIALER_BUTTON_STOCK_SIZE;
	private float textMaxSize = 60.0F;

	public DialerButtonPreference(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
		mContext = paramContext;
		mKey = paramAttributeSet.getAttributeValue("http://schemas.android.com/apk/res/android", "key");
	}

	protected View onCreateDialogView()
	{
		Toast.makeText(mContext, R.string.DialerButtonToast, Toast.LENGTH_LONG).show();

		prefs = getPreferenceManager().getSharedPreferences();
		textCurrSize = prefs.getFloat(mKey + "_size", textMinSize);
		textCurrColor = prefs.getInt(mKey + "_color", Const.DIALER_BUTTON_STOCK_COLOR);

		textCurrSizeLat = prefs.getFloat(mKey + "_size_lat", textMinSize);
		textCurrColorLat = prefs.getInt(mKey + "_color_lat", Const.DIALER_BUTTON_STOCK_COLOR);

		// DIALOG MAIN
		LinearLayout mainLayout = new LinearLayout(mContext);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mainLayout.setPadding(Misc.densify(2), Misc.densify(6), Misc.densify(2), Misc.densify(8));

		// picker
		final ColorPickerView picker = new ColorPickerView(mContext);
		picker.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		picker.setLayerType(View.LAYER_TYPE_SOFTWARE, new Paint());
		picker.setColor(textCurrColorLat, true);
		picker.setAlphaSliderVisible(true);
		picker.setOnColorChangedListener(this);
		picker.setOnSizeChangedListener(this);

		// colorLayout
		LinearLayout colorLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(Misc.densify(7), 0, Misc.densify(7), 0);
		colorLayout.setOrientation(LinearLayout.HORIZONTAL);
		colorLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

		TextView separator = new TextView(mContext);
		separator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0F);
		separator.setText("→");
		separator.setGravity(Gravity.CENTER);

		// Button
		mOldButton = new DialerButtonView(mContext).setParams(textCurrColor, textCurrSize, textCurrColorLat, textCurrSizeLat);
		mNewButton = new DialerButtonView(mContext).setParams(textCurrColor, textCurrSize, textCurrColorLat, textCurrSizeLat);

		mOldButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View View3)
			{
				if (key.equals("latin"))
				{
					key = "phonetic";
					Toast.makeText(mContext, R.string.PhoneticSet, Toast.LENGTH_SHORT).show();
					picker.setColor(textCurrColorLat, true);
				}
				else
				{
					key = "latin";
					Toast.makeText(mContext, R.string.LatinSet, Toast.LENGTH_SHORT).show();
					picker.setColor(textCurrColor, true);
				}
			}
		});

		mainLayout.addView(picker);

		colorLayout.addView(mOldButton);
		colorLayout.addView(separator);
		colorLayout.addView(mNewButton);

		mainLayout.addView(colorLayout, layoutParams);

		return mainLayout;
	}

	public void onClick(DialogInterface paramDialogInterface, int paramInt)
	{
		super.onClick(paramDialogInterface, paramInt);

		if (paramInt == -1)
		{
			Main.putInt(mKey + "_color", textCurrColor);
			Main.putFloat(mKey + "_size", textCurrSize);
			Main.putInt(mKey + "_color_lat", textCurrColorLat);
			Main.putFloat(mKey + "_size_lat", textCurrSizeLat);

			if (textCurrSize != Const.DIALER_BUTTON_STOCK_SIZE || textCurrColor != Const.DIALER_BUTTON_STOCK_COLOR || textCurrSizeLat != Const.DIALER_BUTTON_STOCK_SIZE || textCurrColorLat != Const.DIALER_BUTTON_STOCK_COLOR)
				Main.putBoolean(mKey, true);
			else
				Main.putBoolean(mKey, false);
		}
	}

	@Override
	public void onColorChanged(int color)
	{
		if (key.equals("phonetic"))
		{
			textCurrColor = color;
			mNewButton.phoneticTextView.setTextColor(color);
		} else {
			textCurrColorLat = color;
			mNewButton.latinTextView.setTextColor(color);
		}
	}

	private double initialVectorSize;

	@Override
	public void onPinchChanged(MotionEvent event)
	{
		switch (event.getAction())
		{
		case MotionEvent.ACTION_POINTER_2_UP:
			initialVectorSize = 0;
			break;

		case MotionEvent.ACTION_POINTER_2_DOWN:
			initialVectorSize = getVectorLength(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
			break;

		case MotionEvent.ACTION_MOVE:

			break;
		}

		double size;

		if (event != null && initialVectorSize != 0 && event.getPointerCount() == 2)
		{
			double currentLength = getVectorLength(event.getX(0), event.getY(0), event.getX(1), event.getY(1));

			float sizer;
			
			if (key.equals("phonetic"))
			{
				sizer = textCurrSize;
			} else {
				sizer = textCurrSizeLat;
			}
			
			// Увеличение
			if (currentLength > initialVectorSize * 1.05)
			{
				size = sizer + 1;
				initialVectorSize *= 1.05;
			}
			else if (currentLength < initialVectorSize / 1.05)
			{
				size = sizer - 1;
				initialVectorSize /= 1.05;
			}
			else
			{
				size = sizer;
			}

			if (size > textMaxSize)
				size = textMaxSize;

			if (size < textMinSize)
				size = textMinSize;

			if (key.equals("phonetic"))
			{
				this.textCurrSize = (float) size;
				mNewButton.phoneticTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) size);
			} else {
				this.textCurrSizeLat = (float) size;
				mNewButton.latinTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) size);			
			}
		}
	}

	private double getVectorLength(float x1, float y1, float x2, float y2)
	{
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

}
