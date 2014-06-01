package kz.virtex.htc.tweaker.interfaces;

import kz.virtex.htc.tweaker.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class DialerButtonView extends LinearLayout
{
	TextView digitTextView;
	LinearLayout alphabetLinearLayout;
	public TextView latinTextView;
	public TextView phoneticTextView;
	
	private String base_font_face = "sans-serif-condensed";
	private String digit_font_color = "#e8e5de";
	private int alphabet_stock_color = 0xffa0a0a0;
	private float alphabet_stock_size = 40.0F;
	private String latin_alphabet = "TUV";
	private String phonetic_alphabet = "ШЩЪЫ";
	private int button_width = 356;
	private int button_height = 148;

	public DialerButtonView(Context paramContext)
	{
		super(paramContext);
		init();
	}
	
	public DialerButtonView setParams(int color, float size, int colorLat, float sizeLat)
	{
		phoneticTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		phoneticTextView.setTextColor(color);

		latinTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeLat);
		latinTextView.setTextColor(colorLat);
		
		return this;
	}

	private void init()
	{
		this.setLayoutParams(new LinearLayout.LayoutParams(button_width, button_height));
		this.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		this.setBackgroundResource(R.drawable.phone_keypad_bg);
		
		createDigit();
		createAlphabet();
		createSymbols();
		
		this.addView(digitTextView);

		alphabetLinearLayout.addView(phoneticTextView);
		alphabetLinearLayout.addView(latinTextView);

		this.addView(alphabetLinearLayout);

	}

	private void createDigit()
	{
		digitTextView = new TextView(this.mContext);
		digitTextView.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0F));
		digitTextView.setTypeface(Typeface.create(base_font_face, Typeface.NORMAL));
		digitTextView.setPadding(0, 0, 15, 0); // left,top,right, bottom
		digitTextView.setText("8");
		digitTextView.setTextColor(Color.parseColor(digit_font_color));
		digitTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 100);
		digitTextView.setGravity(Gravity.RIGHT);
	}
	
	private void createAlphabet()
	{
		alphabetLinearLayout = new LinearLayout(this.mContext);
		alphabetLinearLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, 1.0F));
		alphabetLinearLayout.setOrientation(LinearLayout.VERTICAL);
	}
	
	private void createSymbols()
	{
		latinTextView = createSymbol(latin_alphabet, Gravity.TOP, LayoutParams.WRAP_CONTENT, 0.94F);
		phoneticTextView = createSymbol(phonetic_alphabet,Gravity.BOTTOM, LayoutParams.MATCH_PARENT, 1.06f);
	}
	
	private TextView createSymbol(String text, int gravity, int width, float weight)
	{
		TextView alphabetTextView = new TextView(this.mContext);
		
		alphabetTextView.setLayoutParams(new TableLayout.LayoutParams(width, LayoutParams.FILL_PARENT, weight));
		alphabetTextView.setGravity(gravity);
		alphabetTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, alphabet_stock_size);
		alphabetTextView.setTextColor(alphabet_stock_color);
		alphabetTextView.setTypeface(Typeface.create(base_font_face, Typeface.NORMAL));
		alphabetTextView.setText(text);
		
		alphabetTextView.setLineSpacing(0.2F, 0.8F);
		
		return alphabetTextView;
	}
}
