package cn.noblel.demo.gl.shape

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES30.*
import android.opengl.Matrix
import cn.noblel.demo.utils.createFloatBuffer
import cn.noblel.demo.utils.createImageTexture
import cn.noblel.demo.utils.loadShader
import java.io.InputStreamReader

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
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f
    )
    private val vertexBuffer = createFloatBuffer(vertices)
    private var VAO = intArrayOf(0)
    private var VBO = intArrayOf(0)
    private var initFrameDrawingTime = 0L
    private val rotateSpeed = 1.0f / 10

    init {
        initFrameDrawingTime = System.currentTimeMillis()
        glEnable(GL_DEPTH_TEST)
        glGenVertexArrays(1, VAO, 0)
        glGenBuffers(1, VBO, 0)
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

    private val cubePositions = floatArrayOf(
            0.0f, 0.0f, 0.0f,
            2.0f, 5.0f, -15.0f,
            -1.5f, -2.2f, -2.5f,
            -3.8f, -2.0f, -12.3f,
            2.4f, -0.4f, -3.5f,
            -1.7f, 3.0f, -7.5f,
            1.3f, -2.0f, -2.5f,
            1.5f, 2.0f, -2.5f,
            1.5f, 0.2f, -1.5f,
            -1.3f, 1.0f, -1.5f
    )

    fun draw() {
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glActiveTexture(GL_TEXTURE1)
        glBindTexture(GL_TEXTURE_2D, texture1)
        glActiveTexture(GL_TEXTURE2)
        glBindTexture(GL_TEXTURE_2D, texture2)
        glUseProgram(mProgram)
        glBindBuffer(GL_ARRAY_BUFFER, VBO[0])
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer.remaining() * 4, vertexBuffer, GL_STATIC_DRAW)
        mPositionHandle = glGetAttribLocation(mProgram, "aPos")
        glEnableVertexAttribArray(mPositionHandle)
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 5 * 4, 0)
        mTexCoordHandle = glGetAttribLocation(mProgram, "aTexCoord")
        glEnableVertexAttribArray(mTexCoordHandle)
        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, 5 * 4, 3 * 4)
        val ratio = width.toFloat() / height
        val angle = getRotationAngle()
        Matrix.setIdentityM(view, 0)
        Matrix.translateM(view, 0, 0.0f, 0.0f, -3.0f)
        Matrix.setIdentityM(projection, 0)
        Matrix.perspectiveM(projection, 0, 45f, ratio, 0.1f, 100f)
        glUniformMatrix4fv(glGetUniformLocation(mProgram, "view"), 1, false, view, 0)
        glUniformMatrix4fv(glGetUniformLocation(mProgram, "projection"), 1, false, projection, 0)
        glBindVertexArray(VAO[0])
        for (i in 0..9) {
            Matrix.setIdentityM(model, 0)
            Matrix.translateM(model, 0, cubePositions[i * 3], cubePositions[i * 3 + 1], cubePositions[i * 3 + 2])
            Matrix.rotateM(model, 0, angle, 1.0f, 0.3f, 0.5f)
            glUniformMatrix4fv(glGetUniformLocation(mProgram, "model"), 1, false, model, 0)
            glDrawArrays(GL_TRIANGLES, 0, 36)
        }
        glDisableVertexAttribArray(mPositionHandle)
        glDisableVertexAttribArray(mTexCoordHandle)
    }

    private fun getRotationAngle(): Float {
        val angle: Float
        val now = System.currentTimeMillis()
        if (initFrameDrawingTime == 0L) {
            angle = 0.0f
            initFrameDrawingTime = now
        } else {
            val deltaTime: Long = now - initFrameDrawingTime
            angle = deltaTime * rotateSpeed
        }
        return angle
    }
}