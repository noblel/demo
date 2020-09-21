package cn.noblel.demo.utils

import android.content.res.Resources

/**
 * @author noblel
 * @date 2020/9/21
 */
val Int.dp: Float get() = (this / Resources.getSystem().displayMetrics.density)
val Int.px: Float get() = (this * Resources.getSystem().displayMetrics.density)
val Int.sp: Float get() = (this * Resources.getSystem().displayMetrics.scaledDensity)