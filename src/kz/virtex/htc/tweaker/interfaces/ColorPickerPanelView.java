package kz.virtex.htc.tweaker.interfaces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * This class draws a panel which which will be filled with a color which can be
 * set. It can be used to show the currently selected color which you will get
 * from the {@link ColorPickerView}.
 * 
 * @author Daniel Nilsson
 * 
 */
public class ColorPickerPanelView extends View
{

	/**
	 * The width in pixels of the border surrounding the color panel.
	 */
	private final static float BORDER_WIDTH_PX = 1;

	private float mDensity = 1f;

	private int mBorderColor = 0xff6E6E6E;
	private int mColor = 0xff000000;

	private Paint mBorderPaint;
	private Paint mColorPaint;

	private RectF mDrawingRect;
	private RectF mColorRect;

	private AlphaPatternDrawable mAlphaPattern;

	public ColorPickerPanelView(Context context)
	{
		this(context, null);
	}

	public ColorPickerPanelView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ColorPickerPanelView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		this.mBorderPaint = new Paint();
		this.mColorPaint = new Paint();
		this.mDensity = getContext().getResources().getDisplayMetrics().density;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{

		final RectF rect = this.mColorRect;

		if (BORDER_WIDTH_PX > 0)
		{
			this.mBorderPaint.setColor(this.mBorderColor);
			canvas.drawRect(this.mDrawingRect, this.mBorderPaint);
		}

		if (this.mAlphaPattern != null)
		{
			this.mAlphaPattern.draw(canvas);
		}

		this.mColorPaint.setColor(this.mColor);

		canvas.drawRect(rect, this.mColorPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{

		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		this.mDrawingRect = new RectF();
		this.mDrawingRect.left = getPaddingLeft();
		this.mDrawingRect.right = w - getPaddingRight();
		this.mDrawingRect.top = getPaddingTop();
		this.mDrawingRect.bottom = h - getPaddingBottom();

		setUpColorRect();

	}

	private void setUpColorRect()
	{
		final RectF dRect = this.mDrawingRect;

		float left = dRect.left + BORDER_WIDTH_PX;
		float top = dRect.top + BORDER_WIDTH_PX;
		float bottom = dRect.bottom - BORDER_WIDTH_PX;
		float right = dRect.right - BORDER_WIDTH_PX;

		this.mColorRect = new RectF(left, top, right, bottom);

		this.mAlphaPattern = new AlphaPatternDrawable((int) (5 * this.mDensity));

		this.mAlphaPattern.setBounds(Math.round(this.mColorRect.left), Math.round(this.mColorRect.top), Math.round(this.mColorRect.right), Math.round(this.mColorRect.bottom));

	}

	/**
	 * Set the color that should be shown by this view.
	 * 
	 * @param color
	 */
	public void setColor(int color)
	{
		this.mColor = color;
		invalidate();
	}

	/**
	 * Get the color currently show by this view.
	 * 
	 * @return
	 */
	public int getColor()
	{
		return this.mColor;
	}

	/**
	 * Set the color of the border surrounding the panel.
	 * 
	 * @param color
	 */
	public void setBorderColor(int color)
	{
		this.mBorderColor = color;
		invalidate();
	}

	/**
	 * Get the color of the border surrounding the panel.
	 */
	public int getBorderColor()
	{
		return this.mBorderColor;
	}

}