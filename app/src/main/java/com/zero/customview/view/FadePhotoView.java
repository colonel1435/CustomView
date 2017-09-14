package com.zero.customview.view;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import uk.co.senab.photoview.IPhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * Created by Administrator on 2016/12/19.
 */

public class FadePhotoView extends ImageView implements IPhotoView{
    private static final String TAG = "wumin";
    private static final int STATE_NORMAL = 0;
    private static final int STATE_TRANSFORM_IN = 1;
    private static final int STATE_TRANSFORM_OUT = 2;
    private int mOriginalWidth;
    private int mOriginalHeight;
    private int mOriginalLocationX;
    private int mOriginalLocationY;
    private int mState = STATE_NORMAL;
    private Matrix mSmoothMatrix;
    private static Bitmap mBitmap;
    private boolean mTransformStart = false;
    private Transfrom mTransfrom;
    private final int mBgColor = 0xFF000000;
    private int mBgAlpha = 0;
    private Paint mPaint;

    Matrix matrix = null;
    Matrix savedMatrix = null;
    Bitmap bmp = null;
    /** Screen res */
    private DisplayMetrics dm;
    /** minimum scale ratio */
    static final float MIN_SCALE = 0.5f;
    /** maximum scale ratio */
    static final float MAX_SCALE = 15f;
    /** init status */
    static final int NONE = 0;
    /** drag */
    static final int DRAG = 1;
    /** scale */
    static final int ZOOM = 2;
    /** current mode */
    int mode = NONE;

    private PhotoViewAttacher mAttacher = null;
    private ScaleType mPendingScaleType = null;
    
    /** Save res-x & res-y */
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;
    
    public FadePhotoView(Context context) {
        this(context, null);
    }

    public FadePhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FadePhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setScaleType(ScaleType.MATRIX);
        mAttacher = new PhotoViewAttacher(this);
        if (null != mPendingScaleType) {
            setScaleType(mPendingScaleType);
            mPendingScaleType = null;
        }
        
        initView();
    }


    /*
     *  @description    Init image view
     *  @param []
     *  @return void
     *  @author Mr.wumin
     *  @time 2017/12/19 10:37
     */
    private void initView() {
        mSmoothMatrix = new Matrix();
        mPaint=new Paint();
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.FILL);
    }
    
    public void setOriginalInfo(int width, int height, int locationX, int locationY) {
        mOriginalWidth = width;
        mOriginalHeight = height;
        mOriginalLocationX = locationX;
        mOriginalLocationY = locationY;
        // 因为是屏幕坐标，所以要转换为该视图内的坐标，因为我所用的该视图是MATCH_PARENT，所以不用定位该视图的位置,如果不是的话，还需要定位视图的位置，然后计算mOriginalLocationX和mOriginalLocationY
        mOriginalLocationY = mOriginalLocationY - getStatusBarHeight(getContext());
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        java.lang.reflect.Field field = null;
        int x = 0;
        int statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 用于开始进入的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    public void transformIn() {
        mState = STATE_TRANSFORM_IN;
        mTransformStart = true;
        invalidate();
    }

    /**
     * 用于开始退出的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    public void transformOut() {
        mState = STATE_TRANSFORM_OUT;
        mTransformStart = true;
        invalidate();
    }

    private class Transfrom {
        float startScale;// 图片开始的缩放值
        float endScale;// 图片结束的缩放值
        float scale;// 属性ValueAnimator计算出来的值
        LocationSizeF startRect;// 开始的区域
        LocationSizeF endRect;// 结束的区域
        LocationSizeF rect;// 属性ValueAnimator计算出来的值

        void initStartIn() {
            scale = startScale;
            try {
                rect = (LocationSizeF) startRect.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        void initStartOut() {
            scale = endScale;
            try {
                rect = (LocationSizeF) endRect.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Init params
     */
    private void initTransform() {
        if (getDrawable() == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
        //防止mTransfrom重复的做同样的初始化
        if (mTransfrom != null) {
            return;
        }
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        mTransfrom = new Transfrom();

        /* 计算初始的缩放值，初始值因为是CENTR_CROP效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个大于 */
        float xSScale = mOriginalWidth / ((float) mBitmap.getWidth());
        float ySScale = mOriginalHeight / ((float) mBitmap.getHeight());
        float startScale = xSScale > ySScale ? xSScale : ySScale;
        mTransfrom.startScale = startScale;
        /* 计算结束时候的缩放值，结束值因为要达到FIT_CENTER效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个小于 */
        float xEScale = getWidth() / ((float) mBitmap.getWidth());
        float yEScale = getHeight() / ((float) mBitmap.getHeight());
        float endScale = xEScale < yEScale ? xEScale : yEScale;
        mTransfrom.endScale = endScale;

        /**
         * 下面计算Canvas Clip的范围，也就是图片的显示的范围，因为图片是慢慢变大，并且是等比例的，所以这个效果还需要裁减图片显示的区域
         * ，而显示区域的变化范围是在原始CENTER_CROP效果的范围区域
         * ，到最终的FIT_CENTER的范围之间的，区域我用LocationSizeF更好计算
         * ，他就包括左上顶点坐标，和宽高，最后转为Canvas裁减的Rect.
         */
        /* 开始区域 */
        mTransfrom.startRect = new LocationSizeF();
        mTransfrom.startRect.left = mOriginalLocationX;
        mTransfrom.startRect.top = mOriginalLocationY;
        mTransfrom.startRect.width = mOriginalWidth;
        mTransfrom.startRect.height = mOriginalHeight;
        /* 结束区域 */
        mTransfrom.endRect = new LocationSizeF();
        float bitmapEndWidth = mBitmap.getWidth() * mTransfrom.endScale;// 图片最终的宽度
        float bitmapEndHeight = mBitmap.getHeight() * mTransfrom.endScale;// 图片最终的宽度
        mTransfrom.endRect.left = (getWidth() - bitmapEndWidth) / 2;
        mTransfrom.endRect.top = (getHeight() - bitmapEndHeight) / 2;
        mTransfrom.endRect.width = bitmapEndWidth;
        mTransfrom.endRect.height = bitmapEndHeight;

        mTransfrom.rect = new LocationSizeF();
    }

    private class LocationSizeF implements Cloneable{
        float left;
        float top;
        float width;
        float height;
        @Override
        public String toString() {
            return "[left:"+left+" top:"+top+" width:"+width+" height:"+height+"]";
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            // TODO Auto-generated method stub
            return super.clone();
        }

    }

    /*
     *  @description    Get bitmap maxtrix params
     *  @param []
     *  @return void
     *  @author Mr.wumin
     *  @time 2017/12/19 10:31
     */
    private void getBmpMatrix() {
        if (getDrawable() == null) {
            return;
        }
        if (mTransfrom == null) {
            return;
        }
        if (mBitmap == null || mBitmap.isRecycled()) {
            mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        }
        /* CENTER_CROP */
        mSmoothMatrix.setScale(mTransfrom.scale, mTransfrom.scale);
        mSmoothMatrix.postTranslate(-(mTransfrom.scale * mBitmap.getWidth() / 2 - mTransfrom.rect.width / 2),
                -(mTransfrom.scale * mBitmap.getHeight() / 2 - mTransfrom.rect.height / 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return; // couldn't resolve the URI
        }

        if (mState == STATE_TRANSFORM_IN || mState == STATE_TRANSFORM_OUT) {
            if (mTransformStart) {
                initTransform();
            }
            if (mTransfrom == null) {
                super.onDraw(canvas);
                return;
            }

            if (mTransformStart) {
                if (mState == STATE_TRANSFORM_IN) {
                    mTransfrom.initStartIn();
                } else {
                    mTransfrom.initStartOut();
                }
            }

            if(mTransformStart){
                Log.d("Dean", "mTransfrom.startScale:"+mTransfrom.startScale);
                Log.d("Dean", "mTransfrom.startScale:"+mTransfrom.endScale);
                Log.d("Dean", "mTransfrom.scale:"+mTransfrom.scale);
                Log.d("Dean", "mTransfrom.startRect:"+mTransfrom.startRect.toString());
                Log.d("Dean", "mTransfrom.endRect:"+mTransfrom.endRect.toString());
                Log.d("Dean", "mTransfrom.rect:"+mTransfrom.rect.toString());
            }

            mPaint.setAlpha(mBgAlpha);
            canvas.drawPaint(mPaint);

            int saveCount = canvas.getSaveCount();
            canvas.save();
            // 先得到图片在此刻的图像Matrix矩阵
            getBmpMatrix();
            canvas.translate(mTransfrom.rect.left, mTransfrom.rect.top);
            canvas.clipRect(0, 0, mTransfrom.rect.width, mTransfrom.rect.height);
            canvas.concat(mSmoothMatrix);
            getDrawable().draw(canvas);
            canvas.restoreToCount(saveCount);
            if (mTransformStart) {
                mTransformStart=false;
                startTransform(mState);
            }
        } else {
            //当Transform In变化完成后，把背景改为黑色，使得Activity不透明
            mPaint.setAlpha(255);
            canvas.drawPaint(mPaint);
            super.onDraw(canvas);
        }
    }

    /***    Set transform animation     ***/
    private void startTransform(final int state) {
        if (mTransfrom == null) {
            return;
        }
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (state == STATE_TRANSFORM_IN) {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom.startScale, mTransfrom.endScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", mTransfrom.startRect.left, mTransfrom.endRect.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", mTransfrom.startRect.top, mTransfrom.endRect.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", mTransfrom.startRect.width, mTransfrom.endRect.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", mTransfrom.startRect.height, mTransfrom.endRect.height);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 0, 255);
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        } else {
            PropertyValuesHolder scaleHolder = PropertyValuesHolder.ofFloat("scale", mTransfrom.endScale, mTransfrom.startScale);
            PropertyValuesHolder leftHolder = PropertyValuesHolder.ofFloat("left", mTransfrom.endRect.left, mTransfrom.startRect.left);
            PropertyValuesHolder topHolder = PropertyValuesHolder.ofFloat("top", mTransfrom.endRect.top, mTransfrom.startRect.top);
            PropertyValuesHolder widthHolder = PropertyValuesHolder.ofFloat("width", mTransfrom.endRect.width, mTransfrom.startRect.width);
            PropertyValuesHolder heightHolder = PropertyValuesHolder.ofFloat("height", mTransfrom.endRect.height, mTransfrom.startRect.height);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofInt("alpha", 255, 0);
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder, alphaHolder);
        }

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public synchronized void onAnimationUpdate(ValueAnimator animation) {
                mTransfrom.scale = (Float) animation.getAnimatedValue("scale");
                mTransfrom.rect.left = (Float) animation.getAnimatedValue("left");
                mTransfrom.rect.top = (Float) animation.getAnimatedValue("top");
                mTransfrom.rect.width = (Float) animation.getAnimatedValue("width");
                mTransfrom.rect.height = (Float) animation.getAnimatedValue("height");
                mBgAlpha = (Integer) animation.getAnimatedValue("alpha");
                invalidate();
                ((Activity)getContext()).getWindow().getDecorView().invalidate();
            }
        });
        valueAnimator.addListener(new ValueAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                /*
                 * 如果是进入的话，当然是希望最后停留在center_crop的区域。但是如果是out的话，就不应该是center_crop的位置了
                 * ， 而应该是最后变化的位置，因为当out的时候结束时，不回复视图是Normal，要不然会有一个突然闪动回去的bug
                 */
                // TODO 这个可以根据实际需求来修改
                if (state == STATE_TRANSFORM_IN) {
                    mState = STATE_NORMAL;
                }
                if (mTransformListener != null) {
                    mTransformListener.onTransformComplete(state);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    public void setOnTransformListener(TransformListener listener) {
        mTransformListener = listener;
    }

    private TransformListener mTransformListener;

    public static interface TransformListener {
        /**
         *
         * @param mode
         *            STATE_TRANSFORM_IN 1 ,STATE_TRANSFORM_OUT 2
         */
        void onTransformComplete(int mode);// mode 1
    }

	@Override
	public boolean canZoom() {
		// TODO Auto-generated method stub
		return mAttacher.canZoom();
	}

	@Override
	public Matrix getDisplayMatrix() {
		// TODO Auto-generated method stub
		return mAttacher.getDisplayMatrix();
	}

	@Override
	public RectF getDisplayRect() {
		// TODO Auto-generated method stub
		return mAttacher.getDisplayRect();
	}

	@Override
	public IPhotoView getIPhotoViewImplementation() {
		// TODO Auto-generated method stub
		return mAttacher.getIPhotoViewImplementation();
	}

	@Override
	public float getMaxScale() {
		// TODO Auto-generated method stub
		return mAttacher.getMaxScale();
	}

	@Override
	public float getMaximumScale() {
		// TODO Auto-generated method stub
		return mAttacher.getMaximumScale();
	}

	@Override
	public float getMediumScale() {
		// TODO Auto-generated method stub
		return mAttacher.getMediumScale();
	}

	@Override
	public float getMidScale() {
		// TODO Auto-generated method stub
		return mAttacher.getMidScale();
	}

	@Override
	public float getMinScale() {
		// TODO Auto-generated method stub
		return mAttacher.getMinScale();
	}

	@Override
	public float getMinimumScale() {
		// TODO Auto-generated method stub
		return mAttacher.getMinimumScale();
	}

	@Override
	public OnPhotoTapListener getOnPhotoTapListener() {
		// TODO Auto-generated method stub
		return mAttacher.getOnPhotoTapListener();
	}

	@Override
	public OnViewTapListener getOnViewTapListener() {
		// TODO Auto-generated method stub
		return mAttacher.getOnViewTapListener();
	}

	@Override
	public float getScale() {
		// TODO Auto-generated method stub
		return mAttacher.getScale();
	}

	@Override
	public Bitmap getVisibleRectangleBitmap() {
		// TODO Auto-generated method stub
		return mAttacher.getVisibleRectangleBitmap();
	}

	@Override
	public void setAllowParentInterceptOnEdge(boolean allow) {
		// TODO Auto-generated method stub
		mAttacher.setAllowParentInterceptOnEdge(allow);
	}

	@Override
	public boolean setDisplayMatrix(Matrix finalMatrix) {
		// TODO Auto-generated method stub
		return mAttacher.setDisplayMatrix(finalMatrix);
	}

	@Override
	public void setMaxScale(float maxScale) {
		// TODO Auto-generated method stub
		mAttacher.setMaxScale(maxScale);
	}

	@Override
	public void setMaximumScale(float maximumScale) {
		// TODO Auto-generated method stub
		 mAttacher.setMaximumScale(maximumScale);
	}

    @Override
    public void setScaleLevels(float minimumScale, float mediumScale, float maximumScale) {
        mAttacher.setScaleLevels(minimumScale, mediumScale, maximumScale);
    }

    @Override
	public void setMediumScale(float mediumScale) {
		// TODO Auto-generated method stub
		 mAttacher.setMediumScale(mediumScale);
	}

	@Override
	public void setMidScale(float midScale) {
		// TODO Auto-generated method stub
		mAttacher.setMidScale(midScale);
	}

	@Override
	public void setMinScale(float minScale) {
		// TODO Auto-generated method stub
		mAttacher.setMinScale(minScale);
	}

	@Override
	public void setMinimumScale(float minimumScale) {
		// TODO Auto-generated method stub
		mAttacher.setMinimumScale(minimumScale);
	}

	@Override
	public void setOnDoubleTapListener(OnDoubleTapListener arg0) {
		// TODO Auto-generated method stub
		mAttacher.setOnDoubleTapListener(arg0);
	}

    @Override
    public void setOnScaleChangeListener(PhotoViewAttacher.OnScaleChangeListener onScaleChangeListener) {
        mAttacher.setOnScaleChangeListener(onScaleChangeListener);
    }

    @Override
	public void setOnMatrixChangeListener(OnMatrixChangedListener arg0) {
		// TODO Auto-generated method stub
		mAttacher.setOnMatrixChangeListener(arg0);
	}

	@Override
	public void setOnPhotoTapListener(OnPhotoTapListener arg0) {
		// TODO Auto-generated method stub
		mAttacher.setOnPhotoTapListener(arg0);
	}

	@Override
	public void setOnViewTapListener(OnViewTapListener arg0) {
		// TODO Auto-generated method stub
		mAttacher.setOnViewTapListener(arg0);
	}

	@Override
	public void setPhotoViewRotation(float arg0) {
		// TODO Auto-generated method stub
		mAttacher.setPhotoViewRotation(arg0);
	}

	@Override
	public void setRotationBy(float arg0) {
		// TODO Auto-generated method stub
		mAttacher.setRotationBy(arg0);
	}

	@Override
	public void setRotationTo(float arg0) {
		// TODO Auto-generated method stub
		mAttacher.setRotationTo(arg0);
	}

	@Override
	public void setScale(float arg0) {
		// TODO Auto-generated method stub
		mAttacher.setScale(arg0);
	}

	@Override
	public void setScale(float arg0, boolean arg1) {
		// TODO Auto-generated method stub
		mAttacher.setScale(arg0, arg1);
	}

	@Override
	public void setScale(float arg0, float arg1, float arg2, boolean arg3) {
		// TODO Auto-generated method stub
		mAttacher.setScale(arg0, arg1, arg2, arg3);
	}

	@Override
	public void setZoomTransitionDuration(int arg0) {
		// TODO Auto-generated method stub
		mAttacher.setZoomTransitionDuration(arg0);
	}

	@Override
	public void setZoomable(boolean arg0) {
		// TODO Auto-generated method stub
		mAttacher.setZoomable(arg0);
	}
	 @Override
	    public void setImageResource(int resId) {
	        // TODO Auto-generated method stub
	        super.setImageResource(resId);
	        if (null != mAttacher) {
	            mAttacher.update();
	        }
	    }

	    @Override
	    public void setImageURI(Uri uri) {
	        // TODO Auto-generated method stub
	        super.setImageURI(uri);
	        if (null != mAttacher) {
	            mAttacher.update();
	        }
	    }

	    @Override
	    public void setImageDrawable(Drawable drawable) {
	        // TODO Auto-generated method stub
	        super.setImageDrawable(drawable);
	        if(null !=mAttacher){
	            mAttacher.update();
	        }
	    }

	    @Override
	    public void setImageBitmap(Bitmap bm) {
	        // TODO Auto-generated method stub
	        super.setImageBitmap(bm);
	        if(null !=mAttacher){
	            mAttacher.update();
	        }
	    }
}
