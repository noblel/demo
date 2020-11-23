package cn.noblel.demo.gl.preview

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.pm.PackageManager
import android.hardware.Camera
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import cn.noblel.demo.R
import cn.noblel.demo.base.BaseActivity
import cn.noblel.demo.texture.CameraTextureActivity
import cn.noblel.demo.utils.Rotation
import java.io.IOException

/**
 * @author noblel
 * @date 2020/9/8
 */
class GLPreviewActivity : BaseActivity() {
    private var mRenderer: CameraRenderer? = null
    private var mGlSurfaceView: GLSurfaceView? = null
    private var mCamera: Camera? = null
    private var mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT
    private var mAnimatorSet: ObjectAnimator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gl_preview)
        mGlSurfaceView = findViewById(R.id.gl_camera)
        mGlSurfaceView?.setEGLContextClientVersion(2)
        mRenderer = CameraRenderer(mGlSurfaceView)
        mGlSurfaceView?.setRenderer(mRenderer)
        mGlSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        findViewById<View>(R.id.btn_switch).setOnClickListener { switchCamera() }
        findViewById<View>(R.id.btn_play).setOnClickListener { playAnimation() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_CODE)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CameraTextureActivity.REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "no camera permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun playAnimation() {
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.5f, 1.0f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.5f, 1.0f)
        val rotationY = PropertyValuesHolder.ofFloat("rotationY", 0.0f, 360.0f, 0.0f)
        mAnimatorSet = ObjectAnimator.ofPropertyValuesHolder(mGlSurfaceView!!, scaleX, scaleY, rotationY)
        mAnimatorSet!!.setDuration(3000).start()
    }

    override fun onResume() {
        super.onResume()
        cancelAnimation()
    }

    private fun cancelAnimation() {
        if (mAnimatorSet != null) {
            mAnimatorSet!!.setAutoCancel(true)
            mAnimatorSet!!.cancel()
        }
    }

    override fun onStop() {
        super.onStop()
        closeCamera()
    }

    private fun switchCameraId() {
        mCameraId = if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Camera.CameraInfo.CAMERA_FACING_BACK
        } else {
            Camera.CameraInfo.CAMERA_FACING_FRONT
        }
    }

    private fun switchCameraRotation() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mRenderer!!.setRotation(Rotation.NORMAL, flipHorizontal = false, flipVertical = false)
        } else {
            mRenderer!!.setRotation(Rotation.NORMAL, true, flipVertical = false)
        }
    }

    private fun closeCamera() {
        if (mCamera != null) {
            try {
                mCamera!!.stopPreview()
                mCamera!!.setPreviewTexture(null)
                mCamera!!.release()
                mCamera = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun openCamera() {
        if (mCamera != null) {
            return
        }
        try {
            mCamera = Camera.open(mCameraId)
            mCamera?.setPreviewTexture(mRenderer?.surfaceTexture)
            mCamera?.startPreview()
            switchCameraRotation()
        } catch (e: Exception) {
            Log.e(TAG, "initCamera error " + e.message)
        }
    }

    private fun switchCamera() {
        mCamera?.stopPreview()
        mCamera?.release()
        //做切换
        switchCameraId()
        switchCameraRotation()
        try {
            mCamera = Camera.open(mCameraId)
            mCamera?.setPreviewTexture(mRenderer?.surfaceTexture)
            mCamera?.startPreview()
        } catch (e: Exception) {
            //恢复之前的状态
            switchCameraId()
            switchCameraRotation()
            Toast.makeText(this, "open camera failed!!!", Toast.LENGTH_SHORT).show()
        } finally {
            mRenderer!!.reInit()
        }
    }

    companion object {
        private const val TAG = "GLPreviewActivity"
        const val REQUEST_CAMERA_CODE = 1
    }
}