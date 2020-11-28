package cn.noblel.demo.utils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author noblel
 * @date 2020/9/8
 */
fun createFloatBuffer(data: FloatArray): FloatBuffer {
    val byteBuffer = ByteBuffer.allocateDirect(data.size * 4)
    byteBuffer.order(ByteOrder.nativeOrder())
    val floatBuffer = byteBuffer.asFloatBuffer()
    floatBuffer.put(data)
    floatBuffer.position(0)
    return floatBuffer
}

fun createByteBuffer(data: ByteArray): ByteBuffer {
    val byteBuffer = ByteBuffer.allocateDirect(data.size * 4)
    byteBuffer.order(ByteOrder.nativeOrder())
    byteBuffer.put(data)
    byteBuffer.position(0)
    return byteBuffer
}