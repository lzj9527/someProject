package android.extend.widget;

import java.util.List;

import android.content.Context;
import android.extend.BasicConfig;
import android.extend.util.AndroidUtils;
import android.extend.util.LogUtil;
import android.extend.widget.ViewObservable.OnViewObserver;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.OnZoomChangeListener;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements ViewObservable.IViewObservable, SurfaceHolder.Callback
{
	public static final String TAG = CameraPreview.class.getSimpleName();

	private SurfaceHolder mHolder;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private int mCameraId = CameraInfo.CAMERA_FACING_BACK;
	private int mCameraRotation;
	private int mDisplayOrientation;
	private Camera mCamera;
	private boolean mCameraLocked = false;
	private int mMinPreviewWidth = BasicConfig.CameraMinPreviewWidth;
	private int mMinPreviewHeight = BasicConfig.CameraMinPreviewHeight;
	private int mMinPictureWidth = BasicConfig.CameraMinPictureWidth;
	private int mMinPictureHeight = BasicConfig.CameraMinPictureHeight;
	private int mExpectedPictureWidth;
	private int mExpectedPictureHeight;
	private String mFlashMode = Parameters.FLASH_MODE_AUTO;
	private ViewObservable mViewObservable = new ViewObservable(this);
	private int mRetryCount = 0;
	private int mMaxRetryCount = 5;

	private ErrorCallback mErrorCallback = new ErrorCallback()
	{
		@Override
		public void onError(int error, Camera camera)
		{
			LogUtil.w(TAG, "onError: " + error + "; " + camera);
		}
	};

	private OnZoomChangeListener mZoomChangeListener = new OnZoomChangeListener()
	{
		@Override
		public void onZoomChange(int zoomValue, boolean stopped, Camera camera)
		{
			LogUtil.i(TAG, "onZoomChange: " + zoomValue + "; " + stopped);
		}
	};

	public CameraPreview(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public CameraPreview(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public CameraPreview(Context context)
	{
		super(context);
		init();
	}

	@Override
	public boolean isInEditMode()
	{
		return true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		LogUtil.d(TAG, "surfaceCreated: " + holder);

		// The Surface has been created, now tell the camera where to draw the preview.
		ensureCamera();
		startPreviewImpl();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		LogUtil.d(TAG, "surfaceChanged: " + holder + "; " + format + "; " + width + "; " + height);

		mSurfaceWidth = width;
		mSurfaceHeight = height;

		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.
		if (mHolder.getSurface() == null)
		{
			// preview surface does not exist
			return;
		}

		ensureCameraParameters();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		LogUtil.d(TAG, "surfaceDestroyed: " + holder);

		closeCamera();
	}

	public boolean isPortrait()
	{
		return mSurfaceWidth < mSurfaceHeight;
	}

	private void init()
	{
		try
		{
			ensureHolder();
			ensureCamera();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void ensureHolder()
	{
		if (mHolder == null)
		{
			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
	}

	private void ensureCamera()
	{
		// if (mCamera == null)
		// {
		// try
		// {
		// mCamera = Camera.open();
		// mCameraId = CameraInfo.CAMERA_FACING_BACK;
		// mCamera.setErrorCallback(mErrorCallback);
		// mCamera.setZoomChangeListener(mZoomChangeListener);
		// printCameraSupportParameters(mCamera.getParameters());
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// closeCamera();
		// }
		// }
		ensureCamera(CameraInfo.CAMERA_FACING_BACK);
	}

	private void ensureCamera(int cameraId)
	{
		if (mCamera == null)
		{
			try
			{
				mCamera = Camera.open(cameraId);
				mCameraId = cameraId;
				mCamera.setErrorCallback(mErrorCallback);
				mCamera.setZoomChangeListener(mZoomChangeListener);
				printCameraSupportParameters(mCamera.getParameters());
			}
			catch (Exception e)
			{
				e.printStackTrace();
				closeCamera();
			}
		}
	}

	private void closeCamera()
	{
		if (mCamera != null)
		{
			LogUtil.v(TAG, "closeCamera...");
			try
			{
				mCamera.cancelAutoFocus();
				mCamera.stopPreview();
				mCamera.release();
				mCameraId = -1;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			mCamera = null;
		}
	}

	public boolean checkCameraSupported(int cameraId)
	{
		int numberOfCameras = Camera.getNumberOfCameras();
		CameraInfo cameraInfo = new CameraInfo();
		for (int i = 0; i < numberOfCameras; i++)
		{
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == cameraId)
			{
				return true;
			}
		}
		return false;
	}

	public boolean checkFacingBackCameraSupported()
	{
		return checkCameraSupported(CameraInfo.CAMERA_FACING_BACK);
	}

	public boolean checkFacingFrontCameraSupported()
	{
		return checkCameraSupported(CameraInfo.CAMERA_FACING_FRONT);
	}

	public void openFacingBackCamera()
	{
		openCamera(CameraInfo.CAMERA_FACING_BACK);
	}

	public void openFacingFrontCamera()
	{
		openCamera(CameraInfo.CAMERA_FACING_FRONT);
	}

	public void openCamera(int cameraId)
	{
		if (!checkCameraSupported(cameraId))
			return;
		closeCamera();
		ensureCamera(cameraId);
		ensureCameraParameters();
	}

	public void openCamera()
	{
		openCamera(CameraInfo.CAMERA_FACING_BACK);
	}

	public void startPreview()
	{
		if (mCamera == null || mHolder == null)
			return;
		startPreviewImpl();
	}

	private void startPreviewImpl()
	{
		try
		{
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		}
		catch (Exception e)
		{
			LogUtil.e(TAG, "Error starting camera preview", e);
		}
	}

	public void stopPreview()
	{
		if (mCamera == null)
			return;
		stopPreviewImpl();
	}

	private void stopPreviewImpl()
	{
		try
		{
			mCamera.stopPreview();
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "stop camera preview error", e);
		}
	}

	private void printCameraSupportParameters(Parameters parameters)
	{
		if (BasicConfig.DebugMode)
		{
			List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
			for (Size size : supportedPreviewSizes)
			{
				LogUtil.v(TAG, "supportedPreviewSizes: " + size.width + "x" + size.height);
			}
			List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
			for (Size size : supportedPictureSizes)
			{
				LogUtil.v(TAG, "supportedPictureSizes: " + size.width + "x" + size.height);
			}
			// LogUtil.v(TAG, "" + parameters.getMaxZoom() + "; " + parameters.getZoom());
			// List<Integer> ratios = parameters.getZoomRatios();
			// for (Integer ratio : ratios)
			// {
			// LogUtil.v(TAG, "ratio: " + ratio);
			// }
			List<String> supportedFocusModes = parameters.getSupportedFocusModes();
			for (String value : supportedFocusModes)
			{
				LogUtil.v(TAG, "supportedFocusModes: " + value);
			}
			List<Integer> supportedPictureFormats = parameters.getSupportedPictureFormats();
			for (Integer value : supportedPictureFormats)
			{
				LogUtil.v(TAG, "supportedPictureFormats: " + value);
			}
			List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
			for (Integer value : supportedPreviewFormats)
			{
				LogUtil.v(TAG, "supportedPreviewFormats: " + value);
			}
		}
	}

	private void printCameraParameters(Parameters parameters)
	{
		if (BasicConfig.DebugMode)
		{
			Size size = parameters.getPreviewSize();
			LogUtil.i(TAG, "getPreviewSize: " + size.width + "x" + size.height);
			size = parameters.getPictureSize();
			LogUtil.i(TAG, "getPictureSize: " + size.width + "x" + size.height);
			int format = parameters.getPreviewFormat();
			LogUtil.v(TAG, "getPreviewFormat: " + format);
			format = parameters.getPictureFormat();
			LogUtil.v(TAG, "getPictureFormat: " + format);
		}
	}

	private void ensureCameraParameters()
	{
		if (mCamera == null)
			return;
		if (mSurfaceWidth == 0 || mSurfaceHeight == 0)
			return;
		// stop preview before making changes
		stopPreviewImpl();
		// 是否竖屏
		boolean portrait = isPortrait();
		// make any resize, rotate or reformatting changes here
		Parameters parameters = mCamera.getParameters();
		// 设置预浏尺寸
		int expectedWidth, expectedHeight;
		if (portrait)
		{
			expectedWidth = Math.max(mSurfaceHeight, mMinPreviewWidth);
			expectedHeight = Math.max(mSurfaceWidth, mMinPreviewHeight);
		}
		else
		{
			expectedWidth = Math.max(mSurfaceWidth, mMinPreviewWidth);
			expectedHeight = Math.max(mSurfaceHeight, mMinPreviewHeight);
		}
		computeOptimalPreviewSize(parameters, expectedWidth, expectedHeight);
		// 设置预览格式
		// parameters.setPreviewFormat(ImageFormat.JPEG);
		// 设置照片分辨率
		if (mExpectedPictureWidth > 0)
			expectedWidth = mExpectedPictureWidth;
		else
			expectedWidth = Math.max(expectedWidth, mMinPictureWidth);
		if (mExpectedPictureHeight > 0)
			expectedHeight = mExpectedPictureHeight;
		else
			expectedHeight = Math.max(expectedHeight, mMinPictureHeight);
		computeOptimalPictureSize(parameters, expectedWidth, expectedHeight);
		// 设置照片格式
		parameters.setPictureFormat(ImageFormat.JPEG);
		// 设置照片质量
		parameters.setJpegQuality(100);

		CameraInfo info = new android.hardware.Camera.CameraInfo();
		Camera.getCameraInfo(mCameraId, info);
		int rotation = AndroidUtils.getWindowManager(getContext()).getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation)
		{
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			mCameraRotation = mDisplayOrientation = (info.orientation + degrees) % 360;
			mDisplayOrientation = (360 - mDisplayOrientation) % 360; // compensate the mirror
		}
		else
		{ // back-facing
			mCameraRotation = mDisplayOrientation = (info.orientation - degrees + 360) % 360;
		}
		LogUtil.v(TAG, "CameraRotation: " + mCameraRotation + "; " + mDisplayOrientation);
		mCamera.setDisplayOrientation(mDisplayOrientation);
		parameters.setRotation(mDisplayOrientation);

		// if (portrait)
		// {
		// // parameters.set("orientation", "portrait");
		// // 在2.2以上可以使用
		// mCamera.setDisplayOrientation(90);
		// parameters.setRotation(90);
		// }
		// else
		// {
		// // parameters.set("orientation", "landscape");
		// // 在2.2以上可以使用
		// mCamera.setDisplayOrientation(0);
		// parameters.setRotation(0);
		// }

		// start preview with new settings
		try
		{
			mCamera.setParameters(parameters);
			// 设置闪光灯模式
			tryFlashMode();
			// 设置对焦模式
			tryAutoFocusMode();
			startPreviewImpl();
			printCameraParameters(mCamera.getParameters());
		}
		catch (Exception e)
		{
			LogUtil.e(TAG, "Error starting camera preview", e);
		}
	}

	private Size getNearestSize(List<Size> sizes, int w, int h)
	{
		Size nearestSize = null;
		int minDiff = Integer.MAX_VALUE;
		for (Size size : sizes)
		{
			int diff = Math.abs(size.width - w) + Math.abs(size.height - h);
			// LogUtil.i(TAG, "getApproximateSize: " + diff + "; " + minDiff + "; " + size.width + "x" + size.height);
			if (diff < minDiff)
			{
				nearestSize = size;
				minDiff = diff;
			}
		}
		return nearestSize;
	}

	private Size getApproximateSize(List<Size> sizes, int w, int h)
	{
		Size approximateSize = null;
		double targetRatio = (double)w / (double)h;
		double minDiff = Double.MAX_VALUE;
		for (Size size : sizes)
		{
			double ratio = (double)size.width / (double)size.height;
			double diff = Math.abs(targetRatio - ratio);
			if (diff < minDiff)
			{
				approximateSize = size;
				minDiff = diff;
			}
		}
		return approximateSize;
	}

	public int getCameraId()
	{
		return mCameraId;
	}

	public int getCameraRotation()
	{
		return mCameraRotation;
	}

	public int getDisplayOrientation()
	{
		return mDisplayOrientation;
	}

	public Camera getCamera()
	{
		ensureCamera();
		return mCamera;
	}

	public CameraInfo getCameraInfo()
	{
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(mCameraId, info);
		return info;
	}

	/** 设置闪光灯模式 */
	public void setFlashMode(String value)
	{
		LogUtil.d(TAG, "setFlashMode: " + value);
		if (TextUtils.isEmpty(value))
			return;
		mFlashMode = value;
		ensureCamera();
		Parameters parameters = mCamera.getParameters();
		parameters.setFlashMode(value);
		mCamera.setParameters(parameters);
	}

	public String getFlashMode()
	{
		return mFlashMode;
	}

	// 设置期望的照片大小
	public void setExpectedPictureSize(int width, int height)
	{
		LogUtil.v(TAG, "setExpectedPictureSize: " + width + "x" + height);
		mExpectedPictureWidth = width;
		mExpectedPictureHeight = height;
		ensureCamera();
		Parameters parameters = mCamera.getParameters();
		computeOptimalPictureSize(parameters, width, height);
		mCamera.setParameters(parameters);
	}

	public Size getPictureSize()
	{
		ensureCamera();
		return mCamera.getParameters().getPictureSize();
	}

	public void setMinPreviewSize(int minWidth, int minHeight)
	{
		LogUtil.v(TAG, "setMinPreviewSize: " + minWidth + "x" + minHeight);
		mMinPreviewWidth = minWidth;
		mMinPreviewHeight = minHeight;
		ensureCamera();
		Parameters parameters = mCamera.getParameters();
		int expectedWidth, expectedHeight;
		if (isPortrait())
		{
			expectedWidth = Math.max(mSurfaceHeight, mMinPreviewWidth);
			expectedHeight = Math.max(mSurfaceWidth, mMinPreviewHeight);
		}
		else
		{
			expectedWidth = Math.max(mSurfaceWidth, mMinPreviewWidth);
			expectedHeight = Math.max(mSurfaceHeight, mMinPreviewHeight);
		}
		computeOptimalPictureSize(parameters, expectedWidth, expectedHeight);
		mCamera.setParameters(parameters);
	}

	public Size getPreviewSize()
	{
		ensureCamera();
		return mCamera.getParameters().getPreviewSize();
	}

	private Size computeOptimalPictureSize(Parameters parameters, int expectedWidth, int expectedHeight)
	{
		LogUtil.d(TAG, "computeOptimalPictureSize: expectedSize=" + expectedWidth + "x" + expectedHeight);
		List<Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
		Size optimalSize = getNearestSize(supportedPictureSizes, expectedWidth, expectedHeight);
		LogUtil.v(TAG, "computeOptimalPictureSize: optimalSize=" + optimalSize.width + "x" + optimalSize.height);
		parameters.setPictureSize(optimalSize.width, optimalSize.height);
		return optimalSize;
	}

	private Size computeOptimalPreviewSize(Parameters parameters, int expectedWidth, int expectedHeight)
	{
		LogUtil.d(TAG, "computeOptimalPreviewSize: expectedSize=" + expectedWidth + "x" + expectedHeight);
		List<Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getApproximateSize(supportedPreviewSizes, expectedWidth, expectedHeight);
		LogUtil.v(TAG, "computeOptimalPreviewSize: optimalSize=" + optimalSize.width + "x" + optimalSize.height);
		parameters.setPreviewSize(optimalSize.width, optimalSize.height);
		return optimalSize;
	}

	// 尝试设置闪光灯模式
	private void tryFlashMode()
	{
		Parameters params = mCamera.getParameters();
		try
		{
			params.setFlashMode(mFlashMode);
			mCamera.setParameters(params);
			LogUtil.i(TAG, "setFlashMode to " + mFlashMode + " success!");
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "setFlashMode to " + mFlashMode + " failed.");
		}
	}

	// 尝试设置对焦模式
	private void tryAutoFocusMode()
	{
		Parameters params = mCamera.getParameters();
		try
		{
			params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			mCamera.setParameters(params);
			LogUtil.i(TAG, "setFocusMode to FOCUS_MODE_CONTINUOUS_PICTURE success!");
		}
		catch (Exception e)
		{
			LogUtil.w(TAG, "setFocusMode to FOCUS_MODE_CONTINUOUS_PICTURE failed.");
			// try
			// {
			// params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			// mCamera.setParameters(params);
			// LogUtil.i(TAG, "setFocusMode to FOCUS_MODE_CONTINUOUS_VIDEO success!");
			// }
			// catch (Exception e1)
			// {
			// LogUtil.w(TAG, "setFocusMode to FOCUS_MODE_CONTINUOUS_VIDEO failed.");
			try
			{
				params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
				mCamera.setParameters(params);
				LogUtil.i(TAG, "setFocusMode to FOCUS_MODE_AUTO success!");
			}
			catch (Exception e2)
			{
				LogUtil.w(TAG, "setFocusMode to FOCUS_MODE_AUTO failed.");
				LogUtil.i(TAG, "getFocusMode is " + params.getFocusMode());
			}
			// }
		}
	}

	public String getAutoFocusMode()
	{
		ensureCamera();
		return mCamera.getParameters().getFocusMode();
	}

	public void autoFocus(AutoFocusCallback callback)
	{
		if (mCamera != null)
		{
			try
			{
				LogUtil.v(TAG, "autoFocus...");
				mCamera.autoFocus(callback);
				return;
			}
			catch (Exception e)
			{
				LogUtil.w(TAG, "autoFocus error", e);
				if (callback != null)
					callback.onAutoFocus(false, mCamera);
			}
		}
	}

	public void cancelAutoFocus()
	{
		if (mCamera != null)
			mCamera.cancelAutoFocus();
	}

	public void setPreviewCallback(PreviewCallback callback)
	{
		if (mCamera != null)
			mCamera.setPreviewCallback(callback);
	}

	public void setPreviewCallbackWithBuffer(PreviewCallback callback)
	{
		if (mCamera != null)
			mCamera.setPreviewCallbackWithBuffer(callback);
	}

	public void setOneShotPreviewCallback(PreviewCallback callback)
	{
		if (mCamera != null)
			mCamera.setOneShotPreviewCallback(callback);
	}

	public void takePicture(ShutterCallback shutter, PictureCallback raw, PictureCallback postview, PictureCallback jpeg)
	{
		LogUtil.v(TAG, "takePicture...");
		if (mCamera != null)
		{
			try
			{
				mCamera.takePicture(shutter, raw, postview, jpeg);
			}
			catch (Exception e)
			{
				LogUtil.w(TAG, "takePicture error", e);
			}
		}
	}

	public void autoFocusAndOneShotPreview(final AutoFocusCallback autoFocus, final PreviewCallback preview)
	{
		LogUtil.v(TAG, "autoFocusAndOneShotPreview: " + mCameraLocked);
		if (mCameraLocked)
			return;
		mCameraLocked = true;
		if (mCameraId == CameraInfo.CAMERA_FACING_FRONT)
		{
			setOneShotPreviewCallback(preview);
			mCameraLocked = false;
		}
		else
		{
			autoFocus(new AutoFocusCallback()
			{
				@Override
				public void onAutoFocus(boolean success, Camera camera)
				{
					if (autoFocus != null)
						autoFocus.onAutoFocus(success, camera);
					if (!success)
					{
						cancelAutoFocus();
						mRetryCount++;
						LogUtil.w(TAG, "autoFocus failed, retry..." + mRetryCount);
						if (mRetryCount < mMaxRetryCount)
						{
							autoFocus(this);
						}
						else
						{
							setOneShotPreviewCallback(preview);
							mCameraLocked = false;
						}
					}
					else
					{
						setOneShotPreviewCallback(preview);
						mCameraLocked = false;
					}
				}
			});
		}
	}

	public void autoFocusAndTakePicture(final AutoFocusCallback callback, final ShutterCallback shutter,
			final PictureCallback raw, final PictureCallback postview, final PictureCallback jpeg)
	{
		LogUtil.v(TAG, "autoFocusAndTakePicture: " + mCameraLocked);
		if (mCameraLocked)
			return;
		mCameraLocked = true;
		mRetryCount = 0;
		if (mCameraId == CameraInfo.CAMERA_FACING_FRONT)
		{
			takePicture(shutter, raw, postview, jpeg);
			mCameraLocked = false;
		}
		else
		{
			autoFocus(new AutoFocusCallback()
			{
				@Override
				public void onAutoFocus(boolean success, Camera camera)
				{
					if (callback != null)
						callback.onAutoFocus(success, camera);
					if (!success)
					{
						cancelAutoFocus();
						mRetryCount++;
						LogUtil.w(TAG, "autoFocus failed, retry..." + mRetryCount);
						if (mRetryCount < mMaxRetryCount)
						{
							autoFocus(this);
						}
						else
						{
							takePicture(shutter, raw, postview, jpeg);
							mCameraLocked = false;
						}
					}
					else
					{
						takePicture(shutter, raw, postview, jpeg);
						mCameraLocked = false;
					}
				}
			});
		}
	}

	@Override
	public void registerObserver(OnViewObserver observer)
	{
		mViewObservable.registerObserver(observer);
	}

	@Override
	public void unregisterObserver(OnViewObserver observer)
	{
		mViewObservable.unregisterObserver(observer);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewObservable.notifyOnMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
		mViewObservable.notifyOnLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mViewObservable.notifyOnSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		mViewObservable.clear();
	}
}
