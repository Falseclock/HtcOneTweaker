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
		int level = 30;
		int level2 = 95;

		setContentView(R.layout.test);

		//-------------------------
		
		RelativeLayout charging = (RelativeLayout) findViewById(R.id.test2);
		RelativeLayout center = (RelativeLayout) findViewById(R.id.test1);

		RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(getScreenWidth(mContext) - getSideWidth(mContext, level) * 2, 3);
		layoutparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		center.setLayoutParams(layoutparams);
		
		AnimationDrawable animation = createAnim(getBaseContext(), level);
		animation.setOneShot(false);
		charging.setBackgroundDrawable(animation);
		animation.start();
		
		
		//-------------------------
		RelativeLayout charging2 = (RelativeLayout) findViewById(R.id.test4);
		
		AnimationDrawable animation2 = createAnim2(getBaseContext(), level2);
		animation2.setOneShot(false);
		charging2.setBackgroundDrawable(animation2);
		animation2.start();

	}
	
	private AnimationDrawable createAnim2(Context context, int level)
	{
		// Screen width for any screen compatibility
		int screenWidth = getScreenWidth(context);
		Misc.d("screenWidth: " + screenWidth);
		
		// Center point of screen
		int screenMiddle = screenWidth / 2;

		// width to occupy for animation
		int levelWidth = (int) ((float)screenWidth / 100.0F * (float)level);
		Misc.d("levelWidth: " + levelWidth);

		// Gravity acceleration speed
		float gravitAccel = 9.81F;

		// End speed: u = sqrt(2gh)
		int endSpeed = (int) Math.sqrt(2 * gravitAccel * levelWidth / 2);

		AnimationDrawable anim = new AnimationDrawable();
		
		// Animation position start
		int position = screenMiddle;
		
		// starting from 1px for first frame
		int frameWidth = 1;

		// use max 20 animations per left and right animation
		int step = levelWidth/2/20;
		Misc.d("step: " + step);

		// while 2 frame smaller then total level width
		while (frameWidth * 2 < levelWidth+step)
		{
			Misc.d("frameWidth: " + frameWidth);
			Drawable[] layers = new Drawable[2];
			layers[0] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(screenWidth, 3, Bitmap.Config.ARGB_8888));
			layers[1] = new BitmapDrawable(context.getResources(), Bitmap.createBitmap(screenWidth, 3, Bitmap.Config.ARGB_8888));
			
			Canvas mCanvas = new Canvas(Misc.drawableToBitmap(layers[1]));
			Paint mPoint = new Paint();
			mPoint.setStyle(Paint.Style.FILL);
			mPoint.setColor(Color.GREEN);
			
			mCanvas.drawRect(screenMiddle-frameWidth, 0, screenMiddle, 3, mPoint);
			mCanvas.drawRect(screenMiddle, 0, screenMiddle + frameWidth, 3, mPoint);

			LayerDrawable layerDrawable = new LayerDrawable(layers);
			
			frameWidth += step;

			int speed = endSpeed -(int) Math.sqrt(2 * gravitAccel * frameWidth);

			anim.addFrame(layerDrawable, (frameWidth * 2 < levelWidth+step) ? speed : 500);
		}
		
		return anim;
	}
	
	private AnimationDrawable createAnim(Context context, int level)
	{
		int side = getSideWidth(context, level);
		int screenWidth = getScreenWidth(context);
		float gravitAccel = 9.81F;
		// Скорость падения в конце
		// u = sqrt(2gh)
		int endSpeed = (int) Math.sqrt(2 * gravitAccel * side);
		//Misc.d("width: " + screenWidth + ", End speed: " + endSpeed + ", Drop width: " + (side/10));

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

			mCanvas.drawRect(position, 0, position + dropWidth, 3, mPoint);
			mCanvas.drawRect(screenWidth - position - dropWidth, 0, screenWidth - position, 3, mPoint);

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
