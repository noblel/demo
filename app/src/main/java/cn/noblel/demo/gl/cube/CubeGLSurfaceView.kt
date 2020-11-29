package cn.noblel.demo.gl.cube

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import cn.noblel.demo.R

/**
 * @author noblel
 * @date 2020/9/8
 */
class CubeGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val mRenderer: Renderer

    init {
        setEGLContextClientVersion(3)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        val options = BitmapFactory.Options()
        options.inScaled = false
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val tex1 = BitmapFactory.decodeResource(context.resources, R.drawable.container, options)
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val tex2 = BitmapFactory.decodeResource(context.resources, R.drawable.awesomeface, options)
        mRenderer = CubeRenderer(context, tex1, tex2)
        setRenderer(mRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }
}