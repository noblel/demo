package cn.noblel.demo.gl

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * @author noblel
 * @date 2020/9/8
 */
class GLViewActivity : AppCompatActivity() {
    private var mGLSurfaceView: GLSurfaceView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLSurfaceView = MyGLSurfaceView(this)
        setContentView(mGLSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView!!.onResume()
    }
}