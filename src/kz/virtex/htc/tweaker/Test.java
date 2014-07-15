package kz.virtex.htc.tweaker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class Test extends Activity
{
	private Context mContext;

	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);

		mContext = getBaseContext();
		int level = 10;

		setContentView(R.layout.test);

		RelativeLayout left = (RelativeLayout) findViewById(R.id.test1);
		RelativeLayout center = (RelativeLayout) findViewById(R.id.test2);
		center.setBackgroundColor(Color.GREEN);
		RelativeLayout right = (RelativeLayout) findViewById(R.id.test3);

		left.setLayoutParams(new LayoutParams(getSideWidth(mContext, level), 4));
		center.setLayoutParams(new LayoutParams(getScreenWidth(mContext) - getSideWidth(mContext, level) * 2, 4));
		right.setLayoutParams(new LayoutParams(getSideWidth(mContext, level), 4));

		AnimationDrawable animLeft = createAnim(getBaseContext(), level, true);
		animLeft.setOneShot(false);
		AnimationDrawable animRight = createAnim(getBaseContext(), level, false);
		animRight.setOneShot(false);

		left.setBackgroundDrawable(animLeft);
		right.setBackgroundDrawable(animRight);

		animLeft.start();
		animRight.start();

	}

	private AnimationDrawable createAnim(Context context, int level, boolean isLeft)
	{
		int side = getSideWidth(context, level);
		float gravitAccel = 9.81F;
		// Скорость падения в конце
		// u = sqrt(2gh)
		int endSpeed = (int) Math.sqrt(2 * gravitAccel * side);
		Misc.d("side: " + side + ", End speed: " + endSpeed + ", Drop width: " + (side/10));

		AnimationDrawable anim = new AnimationDrawable();
		Drawable[] layers = new Drawable[2];
		layers[0] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(side, 4, Bitmap.Config.ARGB_8888));
		layers[1] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(side, 4, Bitmap.Config.ARGB_8888));
		LayerDrawable layerDrawable = new LayerDrawable(layers);
		anim.addFrame(layerDrawable, endSpeed * 3);

		int dropWidth = side/20;
		int position = -dropWidth;

		while (position < side)
		{
			layers = new Drawable[2];
			layers[0] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(side, 4, Bitmap.Config.ARGB_8888));
			layers[1] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(side, 4, Bitmap.Config.ARGB_8888));

			Canvas mCanvas = new Canvas(Misc.drawableToBitmap(layers[1]));
			Paint mPoint = new Paint();
			mPoint.setStyle(Paint.Style.FILL);
			mPoint.setColor(Color.GREEN);

			if (isLeft)
				mCanvas.drawRect(position, 0, position + dropWidth, 4, mPoint);
			else
				mCanvas.drawRect(side - position - dropWidth, 0, side - position, 4, mPoint);

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
