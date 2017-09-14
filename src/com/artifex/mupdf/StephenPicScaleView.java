package com.artifex.mupdf;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class StephenPicScaleView  extends ImageView{
	private BaseActivity activity;
	public static final int NONE = 0;// 表示当前没有状态
	public static final int DRAG = 1; // 表示当前处于移动状态
	public static final int ZOOM = 2; // 表示当前处于缩放状态
	public static final int BIGGER = 3; // 表示放大图片
	public static final int SMALLER = 4; // 表示缩小图片
	public static final int RESET = 5; // 表示重置图片
	private int mode = NONE; // mode用于标示当前处于什么状态

	private float beforeLenght; // 第一次触摸两点的距离
	private float afterLenght; // 移动后两点的距离
	private float scale = 0.04f; // 缩放因子

	private int screenW;// 下面两句图片的移动范围，及ViewArea的范围，也就是linearLayout的范围，也就是屏幕方位（都是填满父控件属性）
	private int screenH;

	private int start_x;// 开始触摸点
	private int start_y;
	private int stop_x;// 结束触摸点
	private int stop_y;
	private TranslateAnimation trans; // 回弹动画

	public StephenPicScaleView(BaseActivity activity, Bitmap img, int imgDisplayW,int imgDisplayH) {
		super(activity);
		this.activity = activity;
		this.setPadding(0, 0, 0, 0);
		screenW = imgDisplayW;
		screenH = imgDisplayH;
		setImageBitmap(img);// 给我们的自定义imageView设置要显示的图片
		setScaleType(ImageView.ScaleType.FIT_CENTER);
	}

	// 用来计算2个触摸点的距离
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			stop_x = (int) event.getRawX();// 表示相对于屏幕左上角为原点的坐标
			stop_y = (int) event.getRawY();// 同上
			start_x = stop_x - this.getLeft();
			start_y = stop_y - this.getTop();
			if (event.getPointerCount() == 2){
				beforeLenght = spacing(event);
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (spacing(event) > 10f) {
				mode = ZOOM;
				beforeLenght = spacing(event);
			}
			break;
		case MotionEvent.ACTION_UP:
			int disX = 0;
			int disY = 0;
			if (getHeight() <= screenH){
				if (this.getTop() < 0) {
					disY = getTop();
					this.layout(this.getLeft(), 0, this.getRight(),0 + this.getHeight());
				} else if (this.getBottom() >= screenH) {
					disY = getHeight() - screenH + getTop();
					this.layout(this.getLeft(), screenH - getHeight(),this.getRight(), screenH);
				}
			} else {
				int Y1 = getTop();
				int Y2 = getHeight() - screenH + getTop();
				if (Y1 > 0) {
					disY = Y1;
					this.layout(this.getLeft(), 0, this.getRight(),0 + this.getHeight());
				} else if (Y2 < 0) {
					disY = Y2;
					this.layout(this.getLeft(), screenH - getHeight(),this.getRight(), screenH);
				}
			}
			if (getWidth() <= screenW) {
				if (this.getLeft() < 0) {
					disX = getLeft();
					this.layout(0, this.getTop(), 0 + getWidth(),this.getBottom());
				} else if (this.getRight() > screenW) {
					disX = getWidth() - screenW + getLeft();
					this.layout(screenW - getWidth(), this.getTop(), screenW,this.getBottom());
				}
			} else {
				int X1 = getLeft();
				int X2 = getWidth() - screenW + getLeft();
				if (X1 > 0) {
					disX = X1;
					this.layout(0, this.getTop(), 0 + getWidth(),this.getBottom());
				} else if (X2 < 0) {
					disX = X2;
					this.layout(screenW - getWidth(), this.getTop(), screenW,this.getBottom());
				}
			}
			while (getHeight() < 100 || getWidth() < 100) {// 如果图片缩放到宽高任意一个小于100，那么自动放大，直到大于100.
				setScale(BIGGER,scale);
			}
			if (disX != 0 || disY != 0) {// 根据disX和disY的偏移量采用移动动画回弹归位，动画时间为500毫秒。
				trans = new TranslateAnimation(disX, 0, disY, 0);
				trans.setDuration(500);
				this.startAnimation(trans);
			}
			mode = NONE;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {// 执行拖动事件的时，不断变换自定义imageView的位置从而达到拖动效果			
				this.setPosition(stop_x - start_x, stop_y - start_y, stop_x+ this.getWidth() - start_x,stop_y - start_y + this.getHeight());
				stop_x = (int) event.getRawX();
				stop_y = (int) event.getRawY();
			} else if (mode == ZOOM) {
				if (spacing(event) > 10f) {
					afterLenght = spacing(event);
					float gapLenght = afterLenght - beforeLenght;
					if (gapLenght == 0) {
						break;
					}else if (Math.abs(gapLenght) > 5f && getWidth() > 70) {// 图片宽度（也就是自定义imageView）必须大于70才可以缩放
						if (gapLenght > 0) {
							this.setScale(BIGGER,scale);
						} else {
							this.setScale(SMALLER,scale);
						}
						beforeLenght = afterLenght; // 这句不能少。
					}
				}
			}
			break;
		}
		return true;
	}

	public void setScale(int flag,float scale) {
		switch (flag) {
		case BIGGER:
			this.setFrame(this.getLeft() - (int) (scale * this.getWidth()),this.getTop() - (int) (scale * this.getHeight()),
					this.getRight() + (int) (scale * this.getWidth()),this.getBottom() + (int) (scale * this.getHeight()));
			break;
		case SMALLER:
			this.setFrame(this.getLeft() + (int) (scale * this.getWidth()),this.getTop() + (int) (scale * this.getHeight()),
					this.getRight() - (int) (scale * this.getWidth()),this.getBottom() - (int) (scale * this.getHeight()));
			break;
		case RESET:
			this.setFrame(0,0,screenW,screenH);
			break;
		default:
			break;
		}
	}

	private void setPosition(int left, int top, int right, int bottom) {
		this.layout(left, top, right, bottom);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		BitmapDrawable drawable = (BitmapDrawable)this.getDrawable();
		if(null != drawable){
			Bitmap bmp = drawable.getBitmap();
			if (null != bmp && !bmp.isRecycled()){
				bmp.recycle();
				bmp = null;
			}//end of if
		}//end of if
	}
}