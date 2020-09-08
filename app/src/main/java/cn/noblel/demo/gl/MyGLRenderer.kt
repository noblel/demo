package cn.noblel.demo.gl

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import cn.noblel.demo.R
import cn.noblel.demo.gl.filter.PanoramaFilter
import cn.noblel.demo.gl.shape.Square
import cn.noblel.demo.gl.shape.Triangle
import cn.noblel.demo.utils.NO_TEXTURE
import cn.noblel.demo.utils.loadTexture
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author noblel
 * @date 2020/9/8
 */
class MyGLRenderer(private val mContext: Context) : GLSurfaceView.Renderer {
    private var mTriangle: Triangle? = null
    private var mSquare: Square? = null
    private var mPanoramaFilter: PanoramaFilter? = null
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)
    var angle = 0f
    private var mTexture = 0
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        mTriangle = Triangle()
        mSquare = Square()
        mPanoramaFilter = PanoramaFilter()
        val bitmap = BitmapFactory.decodeResource(mContext.resources, R.drawable.panorama_sky)
        mTexture = loadTexture(bitmap, NO_TEXTURE)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
        mPanoramaFilter!!.onOutputSizeChanged(width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        val scratch = FloatArray(16)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        mSquare!!.draw(mMVPMatrix)
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, 0f, 1.0f)
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0)
        mTriangle!!.draw(scratch)
        mPanoramaFilter!!.onDraw(mTexture)
    }

    fun setAngle(x: Float, y: Float, z: Float) {
        if (mPanoramaFilter != null) {
            mPanoramaFilter!!.setAngle(x, y, z)
        }
    }

}