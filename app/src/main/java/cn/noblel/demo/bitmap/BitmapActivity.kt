package cn.noblel.demo.bitmap

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import cn.noblel.demo.R
import cn.noblel.demo.base.BaseActivity
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import java.util.ArrayList

/**
 * @author noblel
 * @date 2020/9/9
 */
class BitmapActivity : BaseActivity() {
    companion object {
        init {
            System.loadLibrary("opencv_java3")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bitmap)
        val imageView = findViewById<ImageView>(R.id.iv_test)
        Thread {
            val src = BitmapFactory.decodeResource(resources, R.drawable.sample)
            val mask = BitmapFactory.decodeResource(resources, R.drawable.mask)
            val maskWidth = 256
            val maskHeight = 256
            val dst = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(dst)
            canvas.drawFilter = PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            //获取一张图的边缘矩形，去除透明度
            val bound = getBound(mask, 1.6f, maskWidth.toFloat(), maskHeight.toFloat())
            canvas.drawBitmap(src, bound, RectF(0F, 0F, dst.width.toFloat(), dst.height.toFloat()), null)
            runOnUiThread {
                findViewById<View>(R.id.progressBar).visibility = View.GONE
                imageView.visibility = View.VISIBLE
                imageView.setImageBitmap(dst)
            }
        }.start()
    }

    private fun getBound(bitmap: Bitmap, scale: Float, bb_width: Float, bb_height: Float): Rect {
        val copy = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(copy)
        val p = Paint()
        p.color = Color.RED
        canvas.drawRect(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat(), p)
        p.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, p)
        val rect = Rect()
        val source = Mat()
        Utils.bitmapToMat(copy, source)
        //二值化处理   cv_8uc1 8位单通道格式
        val binaryMat = Mat(source.height(), source.width(), CvType.CV_8UC1)
        Imgproc.cvtColor(source, binaryMat, Imgproc.COLOR_RGB2GRAY)
        Imgproc.threshold(binaryMat, binaryMat, 0.0, 255.0, Imgproc.THRESH_BINARY or Imgproc.THRESH_OTSU)
        val contours: List<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(binaryMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE,
                Point(0.0, 0.0))
        // 循环找到的所有轮廓
        for (i in contours.indices) {
            val boundRect = Imgproc.boundingRect(contours[i])
            if (i == 0) {
                rect.left = boundRect.tl().x.toInt()
                rect.top = boundRect.tl().y.toInt()
                rect.right = boundRect.br().x.toInt()
                rect.bottom = boundRect.br().y.toInt()
            } else {
                if (boundRect.tl().x < rect.left) {
                    rect.left = boundRect.tl().x.toInt()
                }
                if (boundRect.tl().y < rect.top) {
                    rect.top = boundRect.tl().y.toInt()
                }
                if (boundRect.br().x > rect.right) {
                    rect.right = boundRect.br().x.toInt()
                }
                if (boundRect.br().y > rect.bottom) {
                    rect.bottom = boundRect.br().y.toInt()
                }
            }
        }
        copy.recycle()
        val tempH = rect.height() * scale
        val tempW = rect.width() * scale
        val dh: Int
        dh = if (tempH < bb_height) {
            ((bb_height + 1) / 2).toInt() * 2
        } else {
            (tempH + 1).toInt() / 2 * 2
        }
        val dw: Int
        dw = if (tempW < bb_width) {
            (bb_width + 1).toInt() / 2 * 2
        } else {
            (tempW + 1).toInt() / 2 * 2
        }
        val point = Point(rect.centerX(), rect.centerY())
        rect.left = point.x - dw / 2
        rect.right = rect.left + dw
        rect.top = point.y - dh / 2
        rect.bottom = rect.top + dh
        if (rect.left < 0) {
            rect.left = 0
            rect.right = dw.coerceAtMost(bitmap.width)
        }
        if (rect.right > bitmap.width) {
            rect.right = bitmap.width
            rect.left = (bitmap.width - dw).coerceAtLeast(0)
        }
        if (rect.top < 0) {
            rect.top = 0
            rect.bottom = dh.coerceAtMost(bitmap.height)
        }
        if (rect.bottom > bitmap.height) {
            rect.bottom = bitmap.height
            rect.top = (bitmap.height - dh).coerceAtLeast(0)
        }
        return rect
    }
}