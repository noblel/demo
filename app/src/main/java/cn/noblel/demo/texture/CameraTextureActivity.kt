package cn.noblel.demo.texture

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import cn.noblel.demo.R

/**
 * @author noblel
 * @date 2020/9/8
 */
class CameraTextureActivity : AppCompatActivity() {
    private var mCameraView: TextureView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        mCameraView = findViewById(R.id.tv_camera)
        findViewById<View>(R.id.btn_play).setOnClickListener {
            val translationXHolder = PropertyValuesHolder.ofFloat("translationX", 0.0f, 0.0f)
            val scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.3f, 1.0f)
            val scaleYHolder = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.3f, 1.0f)
            val rotationXHolder = PropertyValuesHolder.ofFloat("rotationX", 0.0f, 2 * 360.0f, 0.0f)
            val rotationYHolder = PropertyValuesHolder.ofFloat("rotationY", 0.0f, 2 * 360.0f, 0.0f)
            val alphaHolder = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.7f, 1.0f)
            val animator = ObjectAnimator
                    .ofPropertyValuesHolder(mCameraView!!, translationXHolder, scaleXHolder, scaleYHolder, rotationXHolder,
                            rotationYHolder, alphaHolder)
            animator.duration = 5000
            animator.start()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_CODE)
            mCameraView!!.visibility = View.GONE
        } else {
            initCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera()
            } else {
                Toast.makeText(this, "no camera permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initCamera() {
        mCameraView!!.surfaceTextureListener = CameraTextureSurfaceListener()
        mCameraView!!.visibility = View.VISIBLE
    }

    companion object {
        const val REQUEST_CAMERA_CODE = 1
    }
}