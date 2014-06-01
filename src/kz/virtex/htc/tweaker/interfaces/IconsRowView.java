package kz.virtex.htc.tweaker.interfaces;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import kz.virtex.htc.tweaker.Misc;

public class IconsRowView extends LinearLayout
{
	private int[] icons;
	public ImageView[] images;

	public IconsRowView(Context paramContext, AttributeSet paramAttributeSet)
	{
		super(paramContext, paramAttributeSet);
	}

	public void init(int[] array)
	{
		icons = array;
		
		setPadding(Misc.densify(16), Misc.densify(6), Misc.densify(16), Misc.densify(4));
		setGravity(17);
		setBackgroundColor(Color.parseColor("#000000"));
		getBackground().setAlpha(205);

		for (int icon : icons)
		{
			ImageView res = new ImageView(getContext());
			res.setImageResource(icon);
			res.setPadding(0, 0, Misc.densify(7), 0);

			images = push(images, res);
		}

		for (ImageView res : images)
		{
			addView(res);
		}
	}

	private static ImageView[] push(ImageView[] array, ImageView push)
	{
		int length = 0;
		try
		{
			length = array.length;
		}
		catch (Exception e)
		{

		}

		ImageView[] longer = new ImageView[length + 1];

		for (int i = 0; i < length; i++)
		{
			longer[i] = array[i];
		}

		longer[length] = push;
		return longer;
	}
}
