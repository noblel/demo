package cn.noblel.demo.gl.shape

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.util.Log
import cn.noblel.demo.utils.createFloatBuffer
import cn.noblel.demo.utils.createImageTexture
import cn.noblel.demo.utils.loadShader
import java.nio.FloatBuffer

/**
 * @author noblel
 * @date 2020/9/8
 */
class Image(context: Context, resId: Int) {
    private val mProgram: Int
    private val mVertexBuffer: FloatBuffer
    private val mTextureBuffer: FloatBuffer
    private var mTexCoordHandle = 0
    private var mPositionHandle = 0
    private var mMVPMatrixHandle = 0
    val imageTexture: Int
    private val mImageWidth: Int
    private val mImageHeight: Int

    fun onSizeChange(width: Int, height: Int) {
        mTextureBuffer.clear()
        val ratioWidth = mImageWidth * 1.0f / width
        val ratioHeight = mImageHeight * 1.0f / height
        val cube = floatArrayOf(
                TEXTURE[0] / ratioWidth, TEXTURE[1] / ratioHeight,
                TEXTURE[2] / ratioWidth, TEXTURE[3] / ratioHeight,
                TEXTURE[4] / ratioWidth, TEXTURE[5] / ratioHeight,
                TEXTURE[6] / ratioWidth, TEXTURE[7] / ratioHeight
        )
        mTextureBuffer.put(cube).position(0)
    }

    fun draw(mvpMatrix: FloatArray?) {
        GLES20.glUseProgram(mProgram)
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition")
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")
        val textureHandle = GLES20.glGetAttribLocation(mProgram, "uTexture")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        // 传入顶点坐标
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        // 传入纹理坐标
        GLES20.glEnableVertexAttribArray(mTexCoordHandle)
        GLES20.glVertexAttribPointer(mTexCoordHandle, COORDS_PER_TEXTURE, GLES20.GL_FLOAT, false, 0, mTextureBuffer)
        // 激活纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 绑定纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
        // 把选定的纹理单元传给片段着色器
        GLES20.glUniform1i(textureHandle, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTEX.size / COORDS_PER_VERTEX)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordHandle)
        GLES20.glUseProgram(0)
    }

    //@formatter:off
    //language=GLSL
    private val vertexShaderCode = "uniform mat4 uMVPMatrix;attribute vec4 aPosition;attribute vec2 aTextureCoord;varying vec2 vTextureCoord;void main() {  gl_Position = uMVPMatrix * aPosition;  vTextureCoord = aTextureCoord;}"

    //language=GLSL
    private val fragmentShaderCode = ("precision mediump float;varying vec2 vTextureCoord;uniform sampler2D uTexture;void main() {  gl_FragColor = texture2D(uTexture, vTextureCoord);}")

    companion object {
        private const val COORDS_PER_VERTEX = 2
        private const val COORDS_PER_TEXTURE = 2
        private val VERTEX = floatArrayOf(-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)
        private val TEXTURE = floatArrayOf(0f, 1f, 1f, 1f, 0f, 0f, 1f, 0f)
        private const val TAG = "Image"
    }

    init {
        val vertexShader: Int = loadShader(vertexShaderCode, GLES20.GL_VERTEX_SHADER)
        val fragmentShader: Int = loadShader(fragmentShaderCode, GLES20.GL_FRAGMENT_SHADER)
        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
            Log.e(TAG, "link program error: " + GLES20.glGetProgramInfoLog(mProgram))
            GLES20.glDeleteProgram(mProgram)
            throw RuntimeException("unable to link program")
        }
        mVertexBuffer = createFloatBuffer(VERTEX)
        mTextureBuffer = createFloatBuffer(TEXTURE)
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        mImageWidth = bitmap.width
        mImageHeight = bitmap.height
        imageTexture = createImageTexture(bitmap)
    }
}