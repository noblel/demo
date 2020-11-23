package cn.noblel.demo.surface

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import cn.noblel.demo.R
import cn.noblel.demo.base.BaseActivity

/**
 * @author noblel
 * @date 2020/9/8
 */
class SurfaceViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surface_view)
        val mCameraView = findViewById<SurfaceView>(R.id.sv_camera)
        val holder = mCameraView.holder
        holder.addCallback(CameraSurfaceViewCallback())
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        findViewById<View>(R.id.btn_play).setOnClickListener {
            val rotationY = PropertyValuesHolder.ofFloat("rotationY", 0.0f, 360.0f, 0.0f)
            val scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.5f, 1.0f)
            val scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.5f, 1.0f)
            val objectAnimator = ObjectAnimator
                    .ofPropertyValuesHolder(mCameraView, rotationY, scaleX, scaleY)
            objectAnimator.setDuration(5000).start()
        }
    }
}