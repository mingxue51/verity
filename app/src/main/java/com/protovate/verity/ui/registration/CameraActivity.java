package com.protovate.verity.ui.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.protovate.verity.R;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by Yan on 5/21/15.
 */
public class CameraActivity extends Activity {
    @InjectView(R.id.cameraPreview)
    FrameLayout mCameraPreview;
    @InjectView(R.id.btnCapture)
    ImageButton mBtnCapture;

    private Camera mCamera;
    private CameraPreview mPreview;
    private int midScreenHeight, midScreenWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.inject(this);

        mCamera = getCameraInstance();

        // Create the Camera preview view
        mPreview = new CameraPreview(this, mCamera);
        mCameraPreview.addView(mPreview);

        Display display = getWindowManager().getDefaultDisplay();
        midScreenHeight = display.getHeight() / 2;
        midScreenWidth = display.getWidth() / 2;

        // TODO: remove this and enable faceDetectionListener
        mBtnCapture.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btnCapture) void capture() {
//        mCamera.setFaceDetectionListener(faceDetectionListener);
        mCamera.takePicture(() -> {
        }, null, mPicture);
    }

    final Camera.PictureCallback mPicture = (data, camera) -> {
        System.out.println("data = " + data.length);
        Intent intent = new Intent();
        intent.putExtra("data", data);
        setResult(RESULT_OK, intent);
        finish();
    };

    final Camera.FaceDetectionListener faceDetectionListener = (faces, camera) -> {
        try {
            Timber.v("face: " + faces[0].rect.centerX() + " | " + faces[0].rect.centerY());
            int posX = midScreenWidth - faces[0].rect.centerX();
            int posY = midScreenHeight + faces[0].rect.centerY();

            mBtnCapture.setVisibility(View.VISIBLE);

            System.out.println("posY = " + posY);
            System.out.println("posX = " + posX);

        } catch (ArrayIndexOutOfBoundsException e) {
            mBtnCapture.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        mPreview.stopPreview();
    }

    /**
     * A safe way to get an instance of the Camera object.
     * <p>
     * Returns null if camera is unavailable
     */
    private static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception ignore) {
            // Camera is not available (in use or does not exist). Do nothing.
        }
        return c;
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        private List<Camera.Size> mSupportedPreviewSizes;
        private Camera.Size mPreviewSize;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview
            try {
                if (mCamera != null) {
                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                    //  mCamera.setFaceDetectionListener(faceDetectionListener);

                    // mCamera.startFaceDetection();
                }
            } catch (IOException e) {
                // Do nothing
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }

        private void stopPreview() {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // Stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception ignore) {
                // Tried to stop a non-existent preview. Do nothing.
            }

            try {
                // Set preview size and make any resize, rotate or reformatting changes here
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

                Camera.Parameters parameters = mCamera.getParameters();

                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (Exception ignored) {
            }

            // Start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception ignore) {
                // Do nothing
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
            setMeasuredDimension(width, height);

            if (mSupportedPreviewSizes != null) {
                mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            }
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - h) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - h);
                }
            }
        }
        return optimalSize;
    }
}
