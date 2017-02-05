package com.sky.facedetectiontrackerdemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import pl.droidsonroids.gif.GifDrawable;

public class BitmapUtil {
	private static BitmapUtil instance = null;
	public static Bitmap leftEarBitmap;
	public Bitmap bearBitmap;
	public Bitmap maskBitmap;
	public Bitmap helmetBitmap;
	public Bitmap faceModelBitmap;
	

	public static BitmapUtil getInstance() {
		if (instance == null)
			instance = new BitmapUtil();
		return instance;
	}

	public BitmapUtil() {

	}



	// 缩放图片(width & height)
	public Bitmap getScaleBitmap(Bitmap bitmap, int w, int h) {
		float wScake, hScake;
		int width = bitmap.getWidth();
		int hight = bitmap.getHeight();
		Matrix matrix = new Matrix();
		if (w == 0) {//根据高缩放
			hScake = ((float) h / hight);
			wScake = hScake;
		} else if (h == 0) {//根据宽缩放
			wScake = ((float) w / width);
			hScake = wScake;
		} else {//根据高和宽缩放
			wScake = ((float) w / width);
			hScake = ((float) h / hight);
		}
		matrix.postScale(wScake, hScake);
		return Bitmap.createBitmap(bitmap, 0, 0, width, hight, matrix, true);
	}
	
//	// 缩放图片(height)
//	private Bitmap getReduceBitmap(Bitmap bitmap ,int h){
//	    int     width     =     bitmap.getWidth();
//	    int     hight     =     bitmap.getHeight();
//	    Matrix     matrix     =     new Matrix();
//	    float     hScake     =     ((float)h/hight);        
//	    matrix.postScale(hScake, hScake);  
//	    return Bitmap.createBitmap(bitmap, 0,0,width,hight,matrix,true);
//	}
//	// 缩放图片(width)
//	private Bitmap getReduceBitmap(Bitmap bitmap ,int h){
//		    int     width     =     bitmap.getWidth();
//		    int     hight     =     bitmap.getHeight();
//		    Matrix     matrix     =     new Matrix();
//		    float     hScake     =     ((float)h/hight);        
//		    matrix.postScale(hScake, hScake);  
//		    return Bitmap.createBitmap(bitmap, 0,0,width,hight,matrix,true);
//		}


	public Bitmap drawableToBitmap(GifDrawable drawable) // drawable 转换成 bitmap
	{
		int width = drawable.getIntrinsicWidth();   // 取 drawable 的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;         // 取 drawable 的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);     // 建立对应 bitmap
		return bitmap;
	}

	public Drawable zoomDrawable(GifDrawable drawable, int w, int h, float ratate)
	{
		int width = drawable.getIntrinsicWidth();
		int height= drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap
		Matrix matrix = new Matrix();   // 创建操作图片用的 Matrix 对象
		float scaleWidth = ((float)w / width);   // 计算缩放比例
		float scaleHeight = ((float)h / height);
		matrix.postScale(scaleWidth, scaleHeight);         // 设置缩放比例
		matrix.postRotate(ratate);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);       // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
		return new BitmapDrawable(newbmp);       // 把 bitmap 转换成 drawable 并返回
	}


	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
	 * 
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	private Bitmap decodeThumbBitmapForFile(String path, int viewWidth,
			int viewHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// 设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);

		// 设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
	 * 
	 * @param options
	 * @param width
	 * @param height
	 */
	private int computeScale(BitmapFactory.Options options, int viewWidth,
			int viewHeight) {
		int inSampleSize = 1;
		if (viewWidth == 0 || viewHeight == 0) {
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;

		// 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
			int widthScale = Math
					.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight
					/ (float) viewWidth);

			// 为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}

}
