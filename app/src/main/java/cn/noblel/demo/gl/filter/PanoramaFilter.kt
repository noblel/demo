package cn.noblel.demo.gl.filter

import android.opengl.GLES20
import android.opengl.Matrix
import cn.noblel.demo.utils.NO_TEXTURE
import cn.noblel.demo.utils.loadProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author noblel
 * @date 2020/9/8
 */
class PanoramaFilter {
    private var cubeBuffer: FloatBuffer? = null
    private var textureBuffer: FloatBuffer? = null
    private var vertexSize = 0
    private val projectMatrix = FloatArray(16)
    private var glUniformModel = 0
    private var glUniformView = 0
    private var glUniformProjection = 0
    private val identity = FloatArray(16)
    private val angle = FloatArray(3)
    private var glProgId = 0
    private val curr = FloatArray(16)
    private val mvp = FloatArray(16)
    private var glAttribPosition = 0
    private var glAttribTextureCoordinate = 0
    private var glUniformTexture = 0
    private fun init() {
        glProgId = loadProgram(VERTEX_SHADER, FRAGMENT_SHADER)
        glAttribPosition = GLES20.glGetAttribLocation(glProgId, "position")
        glUniformTexture = GLES20.glGetUniformLocation(glProgId, "inputImageTexture")
        glAttribTextureCoordinate = GLES20.glGetAttribLocation(glProgId, "inputTextureCoordinate")
        Matrix.setIdentityM(curr, 0)
        Matrix.setIdentityM(identity, 0)
        glUniformModel = GLES20.glGetUniformLocation(glProgId, "model")
        glUniformView = GLES20.glGetUniformLocation(glProgId, "view")
        glUniformProjection = GLES20.glGetUniformLocation(glProgId, "projection")
    }

    fun setAngle(xAngle: Float, yAngle: Float, zAngle: Float) {
        angle[0] = xAngle
        angle[1] = yAngle
        angle[2] = zAngle
    }

    fun onOutputSizeChanged(width: Int, height: Int) {
        val ratio = width / height.toFloat()
        Matrix.frustumM(projectMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 20f)
        Matrix.translateM(projectMatrix, 0, 0f, 0f, -2f)
        Matrix.scaleM(projectMatrix, 0, 4f, 4f, 4f)
    }

    fun onDraw(textureId: Int) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_STENCIL_BUFFER_BIT)
        GLES20.glUseProgram(glProgId)
        cubeBuffer!!.position(0)
        GLES20.glVertexAttribPointer(glAttribPosition, 3, GLES20.GL_FLOAT, false, 0, cubeBuffer)
        GLES20.glEnableVertexAttribArray(glAttribPosition)
        textureBuffer!!.position(0)
        GLES20.glVertexAttribPointer(glAttribTextureCoordinate, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(glAttribTextureCoordinate)
        if (textureId != NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(glUniformTexture, 0)
        }
        Matrix.setIdentityM(curr, 0)
        Matrix.rotateM(curr, 0, -angle[0], 1f, 0f, 0f)
        Matrix.rotateM(curr, 0, -angle[1], 0f, 1f, 0f)
        Matrix.rotateM(curr, 0, -angle[2], 0f, 0f, 1f)
        Matrix.multiplyMM(mvp, 0, projectMatrix, 0, curr, 0)
        GLES20.glUniformMatrix4fv(glUniformModel, 1, false, identity, 0)
        GLES20.glUniformMatrix4fv(glUniformView, 1, false, identity, 0)
        GLES20.glUniformMatrix4fv(glUniformProjection, 1, false, mvp, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexSize)
        GLES20.glDisableVertexAttribArray(glAttribPosition)
        GLES20.glDisableVertexAttribArray(glAttribTextureCoordinate)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDisable(GLES20.GL_CULL_FACE)
    }

    private fun generateVertex() {
        val perVertex = 36
        val perRadius = 2 * Math.PI / perVertex.toFloat()
        val perW: Double = 1.0f / perVertex.toDouble()
        val perH: Double = 1.0f / perVertex.toDouble()
        val vertexList = ArrayList<Float>()
        val textureList = ArrayList<Float>()
        for (a in 0 until perVertex) {
            for (b in 0 until perVertex) {
                val w1 = (a * perH).toFloat()
                val h1 = (b * perW).toFloat()
                val w2 = ((a + 1) * perH).toFloat()
                val h2 = (b * perW).toFloat()
                val w3 = ((a + 1) * perH).toFloat()
                val h3 = ((b + 1) * perW).toFloat()
                val w4 = (a * perH).toFloat()
                val h4 = ((b + 1) * perW).toFloat()
                textureList.add(h1)
                textureList.add(w1)
                textureList.add(h2)
                textureList.add(w2)
                textureList.add(h3)
                textureList.add(w3)
                textureList.add(h3)
                textureList.add(w3)
                textureList.add(h4)
                textureList.add(w4)
                textureList.add(h1)
                textureList.add(w1)
                val x1 = (sin(a * perRadius / 2) * cos(b
                        * perRadius)).toFloat()
                val z1 = (sin(a * perRadius / 2) * sin(b
                        * perRadius)).toFloat()
                val y1 = cos(a * perRadius / 2).toFloat()
                val x2 = (sin((a + 1) * perRadius / 2) * cos(b * perRadius)).toFloat()
                val z2 = (sin((a + 1) * perRadius / 2) * sin(b * perRadius)).toFloat()
                val y2 = cos((a + 1) * perRadius / 2).toFloat()
                val x3 = (sin((a + 1) * perRadius / 2) * cos((b + 1) * perRadius)).toFloat()
                val z3 = (sin((a + 1) * perRadius / 2) * sin((b + 1) * perRadius)).toFloat()
                val y3 = cos((a + 1) * perRadius / 2).toFloat()
                val x4 = (sin(a * perRadius / 2) * cos(x = (b + 1) * perRadius)).toFloat()
                val z4 = (sin(a * perRadius / 2) * sin((b + 1) * perRadius)).toFloat()
                val y4 = cos(a * perRadius / 2).toFloat()
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
                vertexList.add(x2)
                vertexList.add(y2)
                vertexList.add(z2)
                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                vertexList.add(x3)
                vertexList.add(y3)
                vertexList.add(z3)
                vertexList.add(x4)
                vertexList.add(y4)
                vertexList.add(z4)
                vertexList.add(x1)
                vertexList.add(y1)
                vertexList.add(z1)
            }
        }
        vertexSize = vertexList.size / 3
        val uv = FloatArray(vertexSize * 2)
        for (i in uv.indices) {
            uv[i] = textureList[i]
        }
        textureBuffer = ByteBuffer.allocateDirect(uv.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureBuffer!!.put(uv)
        textureBuffer!!.position(0)
        val vertex = FloatArray(vertexSize * 3)
        for (i in vertex.indices) {
            vertex[i] = vertexList[i]
        }
        cubeBuffer = ByteBuffer.allocateDirect(vertex.size * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        cubeBuffer!!.put(vertex)
        cubeBuffer!!.position(0)
    }

    companion object {
        private const val VERTEX_SHADER = "" +
                "attribute vec4 position;\n" +
                "attribute vec4 inputTextureCoordinate;\n" +
                " \n" +
                "varying vec2 textureCoordinate;\n" +
                "uniform mat4 model; \n" +
                "uniform mat4 view; \n" +
                "uniform mat4 projection; \n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = projection * view * model * position;\n" +
                "    textureCoordinate = inputTextureCoordinate.xy;\n" +
                "}"
        private const val FRAGMENT_SHADER = "" +
                "varying highp vec2 textureCoordinate;\n" +
                " \n" +
                "uniform sampler2D inputImageTexture;\n" +
                " \n" +
                "void main()\n" +
                "{\n" +
                "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
                "}"
    }

    init {
        init()
        generateVertex()
        setAngle(0.0f, -90f, 0.0f)
    }
}