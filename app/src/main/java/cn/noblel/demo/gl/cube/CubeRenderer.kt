package cn.noblel.demo.gl.cube

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import cn.noblel.demo.gl.shape.Cube
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author noblel
 * @date 2020/9/8
 */
class CubeRenderer(private var context: Context, private var tex1: Bitmap, private var tex2: Bitmap) : GLSurfaceView.Renderer {
    private lateinit var mCube: Cube

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        mCube = Cube(context, tex1, tex2, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        mCube.draw()
    }
}