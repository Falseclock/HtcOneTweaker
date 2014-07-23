package kz.virtex.htc.tweaker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class Test extends Activity
{
	private Context mContext;

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);

		mContext = getBaseContext();
		int level = 59;

		setContentView(R.layout.test);

		RelativeLayout charging = (RelativeLayout) findViewById(R.id.test2);
		RelativeLayout center = (RelativeLayout) findViewById(R.id.test1);

		//left.setLayoutParams(new LayoutParams(getSideWidth(mContext, level), 4));
		RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(getScreenWidth(mContext) - getSideWidth(mContext, level) * 2, 3);
		layoutparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		center.setLayoutParams(layoutparams);
		
		//right.setLayoutParams(new LayoutParams(getSideWidth(mContext, level), 4));

		AnimationDrawable animation = createAnim(getBaseContext(), level);
		animation.setOneShot(false);
		charging.setBackgroundDrawable(animation);
		animation.start();

	}

	private AnimationDrawable createAnim(Context context, int level)
	{
		int side = getSideWidth(context, level);
		int screenWidth = getScreenWidth(context);
		float gravitAccel = 9.81F;
		// Скорость падения в конце
		// u = sqrt(2gh)
		int endSpeed = (int) Math.sqrt(2 * gravitAccel * side);
		Misc.d("width: " + screenWidth + ", End speed: " + endSpeed + ", Drop width: " + (side/10));

		AnimationDrawable anim = new AnimationDrawable();
		Drawable[] layers = new Drawable[2];
		layers[0] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(side, 3, Bitmap.Config.ARGB_8888));
		layers[1] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(side, 3, Bitmap.Config.ARGB_8888));
		LayerDrawable layerDrawable = new LayerDrawable(layers);
		anim.addFrame(layerDrawable, endSpeed * 3);

		int dropWidth = side/20;
		int position = -dropWidth;

		while (position < side)
		{
			layers = new Drawable[2];
			layers[0] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(screenWidth, 3, Bitmap.Config.ARGB_8888));
			layers[1] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(screenWidth, 3, Bitmap.Config.ARGB_8888));

			Canvas mCanvas = new Canvas(Misc.drawableToBitmap(layers[1]));
			Paint mPoint = new Paint();
			mPoint.setStyle(Paint.Style.FILL);
			mPoint.setColor(Color.GREEN);

			mCanvas.drawRect(position, 0, position + dropWidth, 4, mPoint);
			mCanvas.drawRect(screenWidth - position - dropWidth, 0, screenWidth - position, 4, mPoint);

			layerDrawable = new LayerDrawable(layers);

			int speed = (int) Math.sqrt(2 * gravitAccel * position);
			
			anim.addFrame(layerDrawable, endSpeed - speed);

			position += dropWidth;
			//Misc.d("Position: " + position + "speed: " + (endSpeed - speed));
		}

		return anim;
	}

	private int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);

		return size.x;
	}

	private int getSideWidth(Context context, int level)
	{
		int screenWidth = getScreenWidth(context);
		return (screenWidth - (level * getScreenWidth(context) / 100)) / 2;
	}
}
