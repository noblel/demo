package cn.noblel.demo.gl.shape

import android.opengl.GLES20
import cn.noblel.demo.utils.checkGlError
import cn.noblel.demo.utils.createFloatBuffer
import cn.noblel.demo.utils.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 * @author noblel
 * @date 2020/11/23
 */
class Cube {
    private val vertexBuffer: FloatBuffer?
    private val drawListBuffer: ShortBuffer
    private val mProgram: Int
    private var mPositionHandle = 0
    private var mColorHandle = 0
    private var mMVPMatrixHandle = 0
    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    var color = floatArrayOf(0.2f, 0.709803922f, 0.898039216f, 1.0f)
    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                vertexStride,
                vertexBuffer)
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")
        GLES20.glUniform4fv(mColorHandle, 1, color, 0)
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        checkGlError("glGetUniformLocation")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size, GLES20.GL_UNSIGNED_SHORT,
                drawListBuffer)
        GLES20.glDisableVertexAttribArray(mPositionHandle)
    }

    private val vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "void main() " +
                    "{  " +
                    "   gl_Position = uMVPMatrix * vPosition;" +
                    "}"

    private val fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() " +
                    "{  " +
                    "   gl_FragColor = vColor;" +
                    "}"

    companion object {
        const val COORDS_PER_VERTEX = 3
        var squareCoords = floatArrayOf(-0.5f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f, 0.5f,
                0.5f, 0.0f)
    }

    init {
        vertexBuffer = createFloatBuffer(squareCoords)
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)
        val vertexShader = loadShader(vertexShaderCode, GLES20.GL_VERTEX_SHADER)
        val fragmentShader = loadShader(fragmentShaderCode, GLES20.GL_FRAGMENT_SHADER)
        mProgram = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgram, vertexShader)
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)
    }
}