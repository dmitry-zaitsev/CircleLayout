package ru.biovamp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class CircleLayout extends ViewGroup {

	public static final int LAYOUT_NORMAL = 1;
	public static final int LAYOUT_PIE = 2;
	
	private int mLayoutMode = LAYOUT_NORMAL;
	
	private Drawable mInnerCircle;
	
	private float mAngleOffset;
	private float mDividerWidth;
	private int mInnerRadius;
	
	private Paint mDividerPaint;
	private Paint mSelectorPaint;
	private Paint mCirclePaint;
	
	private RectF mBounds = new RectF();
	
	private Path mDrawingPath = new Path();
	
	public CircleLayout(Context context) {
		this(context, null);
	}
	
	public CircleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mDividerPaint = new Paint();
		mSelectorPaint = new Paint();
		mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleLayout, 0, 0);
		
		try {
			int dividerColor = a.getColor(R.styleable.CircleLayout_divider, android.R.color.darker_gray);
			int selectorColor = a.getColor(R.styleable.CircleLayout_selector, android.R.color.white);
			mInnerCircle = a.getDrawable(R.styleable.CircleLayout_innerCircle);
			
			if(mInnerCircle instanceof ColorDrawable) {
				int innerColor = a.getColor(R.styleable.CircleLayout_innerCircle, android.R.color.white);
				mCirclePaint.setColor(innerColor);
			}
			
			mDividerPaint.setColor(dividerColor);
			mSelectorPaint.setColor(selectorColor);
			
			mAngleOffset = a.getFloat(R.styleable.CircleLayout_angleOffset, 90f);
			mDividerWidth = a.getDimensionPixelSize(R.styleable.CircleLayout_dividerWidth, 1);
			mInnerRadius = a.getDimensionPixelSize(R.styleable.CircleLayout_innerRadius, 80);
			
			mLayoutMode = a.getColor(R.styleable.CircleLayout_layoutMode, LAYOUT_NORMAL);
		} finally {
			a.recycle();
		}
		
		mDividerPaint.setStrokeWidth(mDividerWidth);
		
		if(Build.VERSION.SDK_INT >= 11) {
			setLayerType(LAYER_TYPE_SOFTWARE, null);
		}
	}
	
	public void setLayoutMode(int mode) {
		mLayoutMode = mode;
		requestLayout();
		invalidate();
	}
	
	public int getLayoutMode() {
		return mLayoutMode;
	}
	
	public int getRadius() {
		final int width = getWidth();
		final int height = getHeight();
		
		final float minDimen = width > height ? height : width;
		
		float radius = (minDimen - mInnerRadius)/2f;
		
		return (int) radius;
	}
	
	public void getCenter(PointF p) {
		p.set(getWidth()/2f, getHeight()/2);
	}
	
	public void setAngleOffset(float offset) {
		mAngleOffset = offset;
		requestLayout();
		invalidate();
	}
	
	public float getAngleOffset() {
		return mAngleOffset;
	}
	
	public void setInnerRadius(int radius) {
		mInnerRadius = radius;
		requestLayout();
		invalidate();
	}
	
	public int getInnerRadius() {
		return mInnerRadius;
	}
	
	public void setInnerCircle(Drawable d) {
		mInnerCircle = d;
		requestLayout();
		invalidate();
	}
	
	public void setInnerCircle(int res) {
		mInnerCircle = getContext().getResources().getDrawable(res);
		requestLayout();
		invalidate();
	}
	
	public void setInnerCircleColor(int color) {
		mInnerCircle = new ColorDrawable(color);
		requestLayout();
		invalidate();
	}
	
	public Drawable getInnerCircle() {
		return mInnerCircle;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int count = getChildCount();
		
		int maxHeight = 0;
		int maxWidth = 0;
		
		// Find rightmost and bottommost child
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				measureChild(child, widthMeasureSpec, heightMeasureSpec);
				maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
				maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
			}
		}

		// Check against our minimum height and width
		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

		setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
	}
	
	private LayoutParams layoutParams(View child) {
		return (LayoutParams) child.getLayoutParams();
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childs = getChildCount();
		
		float totalWeight = 0f;
		
		for(int i=0; i<childs; i++) {
			final View child = getChildAt(i);
			
			LayoutParams lp = layoutParams(child);
			
			totalWeight += lp.weight;
		}
		
		final int width = getWidth();
		final int height = getHeight();
		
		final float minDimen = width > height ? height : width;
		final float radius = (minDimen - mInnerRadius)/2f;
		
		mBounds.set(width/2 - minDimen/2, height/2 - minDimen/2, width/2 + minDimen/2, height/2 + minDimen/2);
		
		float startAngle = mAngleOffset;
		
		for(int i=0; i<childs; i++) {
			final View child = getChildAt(i);
			
			final LayoutParams lp = layoutParams(child);
			
			final float angle = 360/totalWeight * lp.weight;
			
			final float centerAngle = startAngle + angle/2f;
			final int x = (int) (radius * FloatMath.cos((float) Math.toRadians(centerAngle))) + width/2;
			final int y = (int) (radius * FloatMath.sin((float) Math.toRadians(centerAngle))) + height/2;
			
			final int childWidth = child.getMeasuredWidth();
			final int childHeight = child.getMeasuredHeight();
			
			child.layout(x - childWidth/2, y - childHeight/2, x + childWidth/2, y + childHeight/2);
			
			lp.startAngle = startAngle;
			
			startAngle += angle;
			
			lp.endAngle = startAngle;
		}
	}
	
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		LayoutParams lp = new LayoutParams(p.width, p.height);
		
		if(p instanceof LinearLayout.LayoutParams) {
			lp.weight = ((LinearLayout.LayoutParams) p).weight;
		}
		
		return lp;
	}
	
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new LayoutParams(getContext(), attrs);
	}
	
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof LayoutParams;
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if(mLayoutMode == LAYOUT_NORMAL) {
			super.dispatchDraw(canvas);
			return;
		}
		
		final int childs = getChildCount();
		final float halfWidth = getWidth()/2f;
		final float halfHeight = getHeight()/2f;
		
		final float radius = halfWidth > halfHeight ? halfHeight : halfWidth;
		
		for(int i=0; i<childs; i++) {
			final View child = getChildAt(i);
			
			LayoutParams lp = layoutParams(child);
			
			canvas.save();
			
			mDrawingPath.rewind();
			mDrawingPath.moveTo(halfWidth, halfHeight);
			mDrawingPath.arcTo(mBounds, lp.startAngle, lp.endAngle - lp.startAngle);
			
			canvas.clipPath(mDrawingPath, Op.REPLACE);
			
			child.draw(canvas);
			
			canvas.restore();
			
			canvas.drawLine(halfWidth, halfHeight,
					radius*FloatMath.cos((float) Math.toRadians(lp.startAngle)) + halfWidth,
					radius*FloatMath.sin((float) Math.toRadians(lp.startAngle)) + halfHeight,
					mDividerPaint);
		}
		
		if(mInnerCircle != null) {
			if(!(mInnerCircle instanceof ColorDrawable)) {
				mInnerCircle.setBounds(
						(int) halfWidth - mInnerRadius,
						(int) halfHeight - mInnerRadius,
						(int) halfWidth + mInnerRadius,
						(int) halfHeight + mInnerRadius);
				
				mInnerCircle.draw(canvas);
			} else {
				canvas.drawCircle(halfWidth, halfHeight, mInnerRadius, mCirclePaint);
			}
		}
	}
	
	public static class LayoutParams extends ViewGroup.LayoutParams {

		private float startAngle;
		private float endAngle;
		
		public float weight = 1f;
		
		public LayoutParams(int width, int height) {
			super(width, height);
		}
		
		public LayoutParams(Context context, AttributeSet attrs) {
			super(context, attrs);
		}
	}

}
