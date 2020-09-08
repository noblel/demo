package cn.noblel.demo.gl.shape

import android.opengl.GLES20
import cn.noblel.demo.utils.checkGlError
import cn.noblel.demo.utils.createFloatBuffer
import cn.noblel.demo.utils.loadShader
import java.nio.FloatBuffer

/**
 * @author noblel
 * @date 2020/9/8
 */
class Triangle {
    private val vertexBuffer: FloatBuffer
    private val mProgram: Int
    private var mPositionHandle = 0
    private var mColorHandle = 0
    private var mMVPMatrixHandle = 0
    private val vertexCount = triangleCoords.size / COORDS_PER_VERTEX
    private val vertexStride = COORDS_PER_VERTEX * 4
    var color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 0.0f)
    fun draw(mvpMatrix: FloatArray?) {
        GLES20.glUseProgram(mProgram)
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride,
                vertexBuffer)
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        checkGlError("glGetUniformLocation")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    private val vertexShaderCode = ("uniform mat4 uMVPMatrix;attribute vec4 vPosition;void main() {  gl_Position = uMVPMatrix * vPosition;}")

    private val fragmentShaderCode = ("precision mediump float;uniform vec4 vColor;void main() {  gl_FragColor = vColor;}")

    companion object {
        const val COORDS_PER_VERTEX = 3
        var triangleCoords = floatArrayOf(0.0f, 0.622008459f, 0.0f, -0.5f, -0.311004243f, 0.0f, 0.5f, -0.311004243f, 0.0f)
    }

    init {
        vertexBuffer = createFloatBuffer(triangleCoords)
        val vertexShader: Int = loadShader(vertexShaderCode, GLES20.GL_VERTEX_SHADER)
        val fragmentShader: Int = loadShader(fragmentShaderCode, GLES20.GL_FRAGMENT_SHADER)
        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)
    }
}