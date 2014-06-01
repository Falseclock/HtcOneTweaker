package kz.virtex.htc.tweaker.interfaces;

import com.htc.widget.HtcCheckBox;

import kz.virtex.htc.tweaker.Misc;
import kz.virtex.htc.tweaker.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class CheckboxPreferenceView extends LinearLayout
{

	public TextView textTitle;
	public TextView textSummary;
	public String key;

	private OnCheckListener mListener;
	public HtcCheckBox checkBox;

	public interface OnCheckListener
	{
		public void onCheck(String key, HtcCheckBox checkBox);
	}

	public CheckboxPreferenceView(Context paramContext)
	{
		super(paramContext);
		init();
	}

	public void setOnCheckListener(OnCheckListener listener)
	{
		mListener = listener;
	}

	private void init()
	{
		RelativeLayout textValueLayout = new RelativeLayout(getContext());
		textValueLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		textValueLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.common_list_item_background));

		LinearLayout textLayout = new LinearLayout(this.mContext);
		textLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
		textLayout.setOrientation(LinearLayout.VERTICAL);
		textLayout.setPadding(Misc.densify(10), Misc.densify(0), Misc.densify(4), Misc.densify(0));
		textLayout.setGravity(Gravity.CENTER_VERTICAL);

		textTitle = new TextView(getContext());
		textTitle.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		textTitle.setGravity(Gravity.CENTER_VERTICAL);
		textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, Misc.densify(6.5F));
		textTitle.setTextColor(Color.parseColor("#2c2e2f"));
		textTitle.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));

		textSummary = new TextView(getContext());
		textSummary.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		textSummary.setGravity(Gravity.CENTER_VERTICAL);
		textSummary.setTextSize(TypedValue.COMPLEX_UNIT_SP, Misc.densify(0));
		textSummary.setTextColor(Color.parseColor("#888888"));

		textLayout.addView(textTitle);
		textLayout.addView(textSummary);

		checkBox = new HtcCheckBox(getContext());
		checkBox.setPadding(Misc.densify(12), Misc.densify(20), Misc.densify(12), Misc.densify(20));
		RelativeLayout.LayoutParams hueTitleValParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		hueTitleValParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		checkBox.setLayoutParams(hueTitleValParams);

		checkBox.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View box)
			{
				if (mListener != null)
				{
					mListener.onCheck(key, checkBox);
				}
			}
		});

		textValueLayout.addView(textLayout);
		textValueLayout.addView(checkBox);

		addView(textValueLayout);
	}
}
