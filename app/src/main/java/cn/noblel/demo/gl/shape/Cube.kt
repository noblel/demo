package cn.noblel.demo.gl.shape

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES30.*
import android.opengl.Matrix
import cn.noblel.demo.utils.createFloatBuffer
import cn.noblel.demo.utils.createImageTexture
import cn.noblel.demo.utils.loadShader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer

/**
 * @author noblel
 * @date 2020/11/23
 */
class Cube(context: Context, tex1: Bitmap, tex2: Bitmap, private val width: Int, private val height: Int) {

    private val mProgram: Int
    private var texture1 = 0
    private var texture2 = 0
    private var mPositionHandle = 0
    private var mTexCoordHandle = 0
    private var model = FloatArray(16)
    private var view = FloatArray(16)
    private var projection = FloatArray(16)

    private val vertices = floatArrayOf(
            // positions
            0.5f, 0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            -0.5f, 0.5f, 0.0f,
    )
    private val vertexBuffer = createFloatBuffer(vertices)

    private val texCoord = floatArrayOf(
            // texture coords
            1.0f, 1.0f, // top right
            1.0f, 0.0f, // bottom right
            0.0f, 0.0f, // bottom left
            0.0f, 1.0f  // top left
    )

    private val texCoordBuffer = createFloatBuffer(texCoord)
    private val drawOrder = shortArrayOf(0, 1, 3, 1, 2, 3) // order to draw vertices
    private val drawListBuffer: ShortBuffer

    init {
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)
        texture1 = createImageTexture(tex1)
        texture2 = createImageTexture(tex2)
        val vertexShader = context.assets.open("cube.vs.glsl").use {
            val vertexShaderCode = InputStreamReader(it).readText()
            loadShader(vertexShaderCode, GL_VERTEX_SHADER)
        }
        val fragmentShader = context.assets.open("cube.fs.glsl").use {
            val fragmentShaderCode = InputStreamReader(it).readText()
            loadShader(fragmentShaderCode, GL_FRAGMENT_SHADER)
        }
        mProgram = glCreateProgram()
        glAttachShader(mProgram, vertexShader)
        glAttachShader(mProgram, fragmentShader)
        glLinkProgram(mProgram)
        glUseProgram(mProgram)
        val texture1Location = glGetUniformLocation(mProgram, "texture1")
        val texture2Location = glGetUniformLocation(mProgram, "texture2")
        glUniform1i(texture1Location, texture1)
        glUniform1i(texture2Location, texture2)
    }

    fun draw() {
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, texture1)
        glActiveTexture(GL_TEXTURE2)
        glBindTexture(GL_TEXTURE_2D, texture2)
        glUseProgram(mProgram)
        mPositionHandle = glGetAttribLocation(mProgram, "aPos")
        glEnableVertexAttribArray(mPositionHandle)
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 3 * 4, vertexBuffer)
        mTexCoordHandle = glGetAttribLocation(mProgram, "aTexCoord")
        glEnableVertexAttribArray(mTexCoordHandle)
        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, 2 * 4, texCoordBuffer)
        Matrix.setIdentityM(model, 0)
        Matrix.setIdentityM(view, 0)
        Matrix.setIdentityM(projection, 0)
        val ratio = width.toFloat() / height
        Matrix.rotateM(model, 0, -55f, 1.0f, 0.0f, 0.0f)
        Matrix.translateM(view, 0, 0.0f, 0.0f, -3.0f)
        Matrix.perspectiveM(projection, 0, 45f, ratio, 0.1f, 100f)
        glUniformMatrix4fv(glGetUniformLocation(mProgram, "model"), 1, false, model, 0)
        glUniformMatrix4fv(glGetUniformLocation(mProgram, "view"), 1, false, view, 0)
        glUniformMatrix4fv(glGetUniformLocation(mProgram, "projection"), 1, false, projection, 0)
        glDrawElements(GL_TRIANGLES, drawOrder.size, GL_UNSIGNED_SHORT, drawListBuffer)
        glDisableVertexAttribArray(mPositionHandle)
        glDisableVertexAttribArray(mTexCoordHandle)
    }
}