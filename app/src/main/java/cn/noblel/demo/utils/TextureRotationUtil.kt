package cn.noblel.demo.utils

/**
 * @author noblel
 * @date 2020/9/8
 */
val TEXTURE_NO_ROTATION: FloatArray = floatArrayOf(
        0.0f, 1.0f,  //左下
        1.0f, 1.0f,  //右下
        0.0f, 0.0f,  //左上
        1.0f, 0.0f)  //右上

val TEXTURE_ROTATED_90 = floatArrayOf(
        1.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        0.0f, 0.0f)

val TEXTURE_ROTATED_180 = floatArrayOf(
        1.0f, 0.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        0.0f, 1.0f
)

val TEXTURE_ROTATED_270 = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        1.0f, 1.0f
)

fun getRotation(rotation: Rotation?, flipHorizontal: Boolean,
                flipVertical: Boolean): FloatArray {
    var rotatedTex: FloatArray
    rotatedTex = when (rotation) {
        Rotation.ROTATION_90 -> TEXTURE_ROTATED_90
        Rotation.ROTATION_180 -> TEXTURE_ROTATED_180
        Rotation.ROTATION_270 -> TEXTURE_ROTATED_270
        Rotation.NORMAL -> TEXTURE_NO_ROTATION
        else -> TEXTURE_NO_ROTATION
    }
    if (flipHorizontal) {
        rotatedTex = floatArrayOf(flip(rotatedTex[0]), rotatedTex[1], flip(rotatedTex[2]), rotatedTex[3],
                flip(rotatedTex[4]),
                rotatedTex[5], flip(rotatedTex[6]), rotatedTex[7])
    }
    if (flipVertical) {
        rotatedTex = floatArrayOf(rotatedTex[0], flip(rotatedTex[1]), rotatedTex[2], flip(rotatedTex[3]),
                rotatedTex[4],
                flip(rotatedTex[5]), rotatedTex[6], flip(rotatedTex[7]))
    }
    return rotatedTex
}

fun flip(i: Float): Float {
    return if (i == 0.0f) {
        1.0f
    } else 0.0f
}