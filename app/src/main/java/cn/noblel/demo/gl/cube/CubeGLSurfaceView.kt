package cn.noblel.demo.gl.cube

import android.content.Context
import android.opengl.GLSurfaceView

/**
 * @author noblel
 * @date 2020/9/8
 */
class CubeGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val mRenderer: Renderer

    init {
        setEGLContextClientVersion(2)
        mRenderer = CubeRenderer()
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}