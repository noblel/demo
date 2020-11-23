package cn.noblel.demo.gl

import android.opengl.GLSurfaceView
import android.os.Bundle
import cn.noblel.demo.base.BaseActivity

/**
 * @author noblel
 * @date 2020/9/8
 */
class GLViewActivity : BaseActivity() {
    private lateinit var mGLSurfaceView: GLSurfaceView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLSurfaceView = MyGLSurfaceView(this)
        setContentView(mGLSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView.onResume()
    }
}