package com.longhui.customview;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class CoverFlow extends Gallery {
	private static final float SCALE_SIZE = 1.25f;
	private static final String TAG = "CoverFlow";
	private Camera mCamera = new Camera();
	private int mMaxRotationAngle = 20;			//50
	private int mCoveflowCenter;
	private boolean mAlphaMode = true;		//true
	private boolean mCircleMode = false;		//false
	
	PointF mTouch = new PointF();
	
	
	public CoverFlow(Context context) {
		super(context);
		
		setStaticTransformationsEnabled(true);
		setClickable(false);
	}

	public CoverFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		setStaticTransformationsEnabled(true);
	}

	public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setStaticTransformationsEnabled(true);
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
            // Helper method for lifted finger
			mTouch.set(event.getX(), event.getY());
        }
		return super.onTouchEvent(event);
	}
	
	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	public void setMaxRotationAngle(int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	public boolean getCircleMode() {
		return mCircleMode;
	}

	public void setCircleMode(boolean isCircle) {
		mCircleMode = isCircle;
	}

	public boolean getAlphaMode() {
		return mAlphaMode;
	}

	public void setAlphaMode(boolean isAlpha) {
		mAlphaMode = isAlpha;
	}
	
	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2
				+ getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	protected boolean getChildStaticTransformation(View child, Transformation t) {
		final int childCenter = getCenterOfView(child);
		float rotationAngle = 0;
		float scaleFactor = SCALE_SIZE;
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);
		if (childCenter == mCoveflowCenter) {
			transformImageBitmap((ImageView) child, t, rotationAngle, scaleFactor);
			((ImageView) child).clearColorFilter();
		} else {
			final float halfW = getWidth()/2;
			final float axialDis = mCoveflowCenter - childCenter;
			final int maxAngle = mMaxRotationAngle;
			final float radio = axialDis / halfW;

			((ImageView) child).setColorFilter(((int)(Math.abs(radio) * 0x40)) << 24 , Mode.DARKEN);
			
			rotationAngle = radio * maxAngle;
			scaleFactor *= (halfW - Math.abs(axialDis)/2.5f) / halfW;
			
			transformImageBitmap((ImageView) child, t, rotationAngle,scaleFactor);
		}
		return true;
	}

	/**
	 * This is called during layout when the size of this view has changed. If
	 * you were just added to the view hierarchy, you're called with the old
	 * values of 0.
	 * 
	 * @param w
	 *            Current width of this view.
	 * @param h
	 *            Current height of this view.
	 * @param oldw
	 *            Old width of this view.
	 * @param oldh
	 *            Old height of this view.
	 */
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * Transform the Image Bitmap by the Angle passed
	 * 
	 * @param imageView
	 *            ImageView the ImageView whose bitmap we want to rotate
	 * @param t
	 *            transformation
	 * @param rotationAngle
	 *            the Angle by which to rotate the Bitmap
	 */
	private void transformImageBitmap(ImageView child, Transformation t,
			float rotationAngle,float scaleFactor) {
//		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
//		final int rotation = Math.abs(rotationAngle);
//		mCamera.translate(0.0f, 0.0f, 100.0f);
		// As the angle of the view gets less, zoom in
//		if (rotation <= mMaxRotationAngle) {
			/*
			float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
			mCamera.translate(0.0f, 0.0f, zoomAmount);
			if (mCircleMode) {
				if (rotation < 40)
					mCamera.translate(0.0f, 155, 0.0f);
				else
					mCamera.translate(0.0f, (255 - rotation * 2.5f), 0.0f);
			}
			*/
//			if (mAlphaMode) {
//				((ImageView) (child)).setAlpha((int) (255 - rotation * 2.5));
//			}
//		}
		//mCamera.rotateY(rotationAngle);
//		mCamera.getMatrix(imageMatrix);
//		Log.i(TAG,"=========scaleFactor:"+scaleFactor+"=========");
		//imageMatrix.setScale(1.0f, 1.0f);
		imageMatrix.setScale(scaleFactor, scaleFactor);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		
//		Log.i(TAG,"=========rotationAngle:"+rotationAngle+"=========");
		imageMatrix.postTranslate(rotationAngle * 6f, 
				/*10 - Math.abs(rotationAngle)*0.3f*/0 );
		
//		mCamera.restore();
	}
	
	@Override
    protected int getChildDrawingOrder(int childCount, int i) {
		int selectedIndex = getSelectedItemPosition()
				- getFirstVisiblePosition();
		if (selectedIndex < 0)
			return i;
		
		if (i == childCount - 1) {
			return selectedIndex;
		} else if (i >= selectedIndex) {
			return childCount - 1 - (i - selectedIndex);
		} else {
			return i;
		}
	}
	
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	public Rect getLogoGlobalRect(View view, int position) {
		Rect rect  = new Rect();		
		view.getGlobalVisibleRect(rect);
		
		rect = scaleRect(rect);
		
		return rect;
	}

	private Rect scaleRect(Rect rect) {
		if(rect.isEmpty()) {
			return rect;
		}
		
		float w = rect.width() * SCALE_SIZE;
		float h = rect.height() * SCALE_SIZE;
		
		RectF rf = new RectF(rect.exactCenterX()-w/2, rect.exactCenterY()-h/2,
				rect.exactCenterX()+w/2, rect.exactCenterY()+h/2);
		
		rf.roundOut(rect);		
		return rect;
	}
}