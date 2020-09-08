package cn.noblel.demo.surface

import android.graphics.ImageFormat
import android.hardware.Camera
import android.util.Log
import android.view.SurfaceHolder

/**
 * @author noblel
 * @date 2020/9/8
 */
class CameraSurfaceViewCallback : SurfaceHolder.Callback {
    private var mCamera: Camera? = null
    override fun surfaceCreated(holder: SurfaceHolder) {
        try {
            mCamera = Camera.open()
            mCamera?.setDisplayOrientation(90)
            mCamera?.setPreviewDisplay(holder)
            mCamera?.startPreview()
        } catch (e: Exception) {
            Log.e(TAG, "openCamera failed " + e.message)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        if (mCamera != null) {
            mCamera!!.autoFocus { success: Boolean, camera: Camera ->
                if (success) {
                    val parameters = camera.parameters
                    parameters.pictureFormat = ImageFormat.JPEG
                    //持续对焦
                    try {
                        parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                        camera.parameters = parameters
                    } catch (e: Exception) {
                        e.printStackTrace()
                        camera.cancelAutoFocus()
                    }
                    camera.startPreview()
                }
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    companion object {
        private const val TAG = "CameraCallback"
    }
}