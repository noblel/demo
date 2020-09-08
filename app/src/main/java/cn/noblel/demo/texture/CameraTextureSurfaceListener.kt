package cn.noblel.demo.texture

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import android.view.TextureView

/**
 * @author noblel
 * @date 2020/9/8
 */
class CameraTextureSurfaceListener : TextureView.SurfaceTextureListener {
    private var mCamera: Camera? = null
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (mCamera == null) {
            try {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
                mCamera?.setDisplayOrientation(90)
                mCamera?.setPreviewTexture(surface)
                mCamera?.startPreview()
            } catch (e: Exception) {
                Log.e(TAG, "start " + Camera.CameraInfo.CAMERA_FACING_BACK + " preview error " + e.message)
            }
        } else {
            Log.i(TAG, "camera opened ")
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        if (mCamera != null) {
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}

    companion object {
        private const val TAG = "CameraSurfaceListener"
    }
}