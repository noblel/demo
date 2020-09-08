package cn.noblel.demo.gl.preview

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import cn.noblel.demo.utils.Rotation
import cn.noblel.demo.utils.TEXTURE_NO_ROTATION
import cn.noblel.demo.utils.getRotation
import cn.noblel.demo.utils.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author noblel
 * @date 2020/9/8
 */
class CameraRenderer(private val mGLSurfaceView: GLSurfaceView?) : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private var mProgram = 0
    @Volatile
    private var mInit = false
    @Volatile
    var surfaceTexture: SurfaceTexture?
    private var mTexCoordinate: FloatArray = TEXTURE_NO_ROTATION
    private val mPosCoordinate = floatArrayOf(-1f, -1f, -1f, 1f, 1f, -1f, 1f, 1f)
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        createProgram()
        activeProgram()
    }

    private fun activeProgram() {
        GLES20.glUseProgram(mProgram)
        surfaceTexture!!.setOnFrameAvailableListener(this)
        //获取顶点句柄
        val uPosHandle = GLES20.glGetAttribLocation(mProgram, "position")
        val aTexHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate")
        val posBuffer = ByteBuffer.allocateDirect(mPosCoordinate.size * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        posBuffer.put(mPosCoordinate).position(0)
        val texBuffer = ByteBuffer.allocateDirect(mTexCoordinate.size * 4).order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        texBuffer.put(mTexCoordinate).position(0)
        GLES20.glVertexAttribPointer(uPosHandle, 2, GLES20.GL_FLOAT, false, 0, posBuffer)
        GLES20.glVertexAttribPointer(aTexHandle, 2, GLES20.GL_FLOAT, false, 0, texBuffer)

        //enable 顶点
        GLES20.glEnableVertexAttribArray(uPosHandle)
        GLES20.glEnableVertexAttribArray(aTexHandle)
    }

    private fun createProgram() {
        val vertexShader: Int = loadShader(VERTEX_SHADER_CODE, GLES20.GL_VERTEX_SHADER)
        val fragmentShader: Int = loadShader(FRAGMENT_SHADER_CODE, GLES20.GL_FRAGMENT_SHADER)
        mProgram = GLES20.glCreateProgram()
        //添加顶点着色器
        GLES20.glAttachShader(mProgram, vertexShader)
        //添加片段着色器
        GLES20.glAttachShader(mProgram, fragmentShader)
        GLES20.glLinkProgram(mProgram)
        //释放shader
        GLES20.glDeleteShader(vertexShader)
        GLES20.glDeleteShader(fragmentShader)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        if (mInit) {
            activeProgram()
            mInit = false
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        surfaceTexture?.updateTexImage()
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mPosCoordinate.size / 2)
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        mGLSurfaceView!!.requestRender()
    }

    fun setRotation(rotation: Rotation?, flipHorizontal: Boolean, flipVertical: Boolean) {
        mTexCoordinate = getRotation(rotation, flipHorizontal, flipVertical)
    }

    fun reInit() {
        mInit = true
    }

    companion object {
        private fun createTexture(): Int {
            val tex = IntArray(1)
            //生成纹理
            GLES20.glGenTextures(1, tex, 0)
            //绑定外部纹理
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
            //设置纹理过滤参数
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat())
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())
            return tex[0]
        }

        private const val VERTEX_SHADER_CODE = ("attribute vec2 inputTextureCoordinate;           \n"
                + "attribute vec4 position;                         \n"
                + "varying   vec2 textureCoordinate;                \n"
                + "void main() {                                    \n"
                + "    gl_Position = position;                      \n"
                + "    textureCoordinate = inputTextureCoordinate;  \n"
                + "}")

        private const val FRAGMENT_SHADER_CODE = ("#extension GL_OES_EGL_image_external : require               \n"
                + "precision mediump float;                                     \n"
                + "uniform samplerExternalOES oesTexure;                        \n"
                + "varying vec2 textureCoordinate;                              \n"
                + "void main() {                                                \n"
                + "    vec4 tc = texture2D(oesTexure, textureCoordinate);       \n"
                + "    float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;    \n"
                + "    gl_FragColor = vec4(color, color, color, 1.0);           \n"
                + "}")
    }

    init {
        val texName = createTexture()
        surfaceTexture = SurfaceTexture(texName)
    }
}