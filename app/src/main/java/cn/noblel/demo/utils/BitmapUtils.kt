package cn.noblel.demo.utils

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.util.Log

/**
 * @author noblel
 * @date 2020/9/8
 */
private const val TAG: String = "BitmapUtils"

fun createImageTexture(bmp: Bitmap?): Int {
    if (bmp == null) {
        return 0
    }
    val textureHandles = IntArray(1)
    GLES20.glGenTextures(1, textureHandles, 0)
    checkGlError("glGenTextures")
    if (textureHandles[0] == 0) {
        Log.w(TAG, "Could not generate a new OpenGL texture object.")
        return 0
    }
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandles[0])
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT.toFloat())
    GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT.toFloat())
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)
    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
    return textureHandles[0]
}