package cn.noblel.demo.custom.seekbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import cn.noblel.demo.utils.px
import cn.noblel.demo.utils.sp
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * @author noblel
 * @date 2020/9/21
 */
class StepSeekBar @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val mRoundRectPaint: Paint
    private val mHighlightTextPaint: Paint
    private val mDefaultTextPaint: Paint
    private val mHighlightLinePaint: Paint
    private val mDefaultLinePaint: Paint
    private val mRoundRectF: RectF
    private val mRoundRectFHeight = 20.px
    private val mRoundRectFWidth = 5.px
    private var mProgress = 0f
    private val mRoundRectFRadius = 4.px
    private val mTextRect = Rect()
    private var mAnim:ValueAnimator? = null
    private var stepSelected:Int = 0
    private var mBoundaryTextList: MutableList<String>?

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return super.onTouchEvent(event)
        }
        val x = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN-> {
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mProgress = when {
                    x < 0 -> {
                        0f
                    }
                    x >= width -> {
                        1f
                    }
                    else -> {
                        x / width
                    }
                }
                val size = mBoundaryTextList!!.size
                val stepPercent = 1.0f / (size - 1)
                stepSelected = BigDecimal.valueOf(mProgress / stepPercent * 1.0).setScale(0, RoundingMode.HALF_UP).toInt()
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val size = mBoundaryTextList!!.size
                val stepPercent = 1.0f / (size - 1)
                stepSelected = BigDecimal.valueOf(mProgress / stepPercent * 1.0).setScale(0, RoundingMode.HALF_UP).toInt()
                animate(stepPercent, stepSelected)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun animate(stepPercent: Float, stepSelected: Int) {
        if (mAnim != null) mAnim!!.cancel()
        mAnim = ValueAnimator.ofFloat(mProgress, stepPercent * stepSelected).setDuration(300)
        mAnim!!.addUpdateListener {
            val value = it.animatedValue
            mProgress = value as Float
            invalidate()
        }
        mAnim!!.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawLine(canvas)
        drawRoundRect(canvas)
        drawText(canvas)
    }

    private fun drawLine(canvas: Canvas) {
        val drawWidth = width - mRoundRectFWidth
        canvas.drawLine(mRoundRectFWidth / 2, height * 0.5f, mProgress * drawWidth + mRoundRectFWidth / 2, height * 0.5f, mHighlightLinePaint)
        canvas.drawLine(mProgress * drawWidth + mRoundRectFWidth / 2, height * 0.5f, drawWidth + mRoundRectFWidth / 2, height * 0.5f, mDefaultLinePaint)
    }

    private fun drawRoundRect(canvas: Canvas) {
        val drawWidth = width - mRoundRectFWidth
        val left = mProgress * drawWidth
        val top = height * 0.5f - mRoundRectFHeight / 2
        val right = mProgress * drawWidth + mRoundRectFWidth
        val bottom = height * 0.5f + mRoundRectFHeight / 2
        mRoundRectF.set(left, top, right, bottom)
        canvas.drawRoundRect(mRoundRectF, mRoundRectFRadius, mRoundRectFRadius, mRoundRectPaint)
    }

    private fun drawText(canvas: Canvas) {
        if (mBoundaryTextList == null || mBoundaryTextList!!.isEmpty()) {
            return
        }
        val drawWidth = width - mRoundRectFWidth
        for (i in mBoundaryTextList!!.indices) {
            val paint: Paint = if (stepSelected == i) {
                mHighlightTextPaint
            } else {
                mDefaultTextPaint
            }
            val text = mBoundaryTextList!![i]
            paint.getTextBounds(text, 0, text.length, mTextRect)
            val textWidth = paint.measureText(text)
            val y = height * 0.5f + mRoundRectFHeight / 2 + mTextRect.height()
            if (i != mBoundaryTextList!!.size - 1) {
                if (i == 0) {
                    canvas.drawText(text, mRoundRectFWidth / 2, y, paint)
                } else {
                    canvas.drawText(text, i * drawWidth * 1.0f / (mBoundaryTextList!!.size - 1) - textWidth / 2 + mRoundRectFWidth / 2, y, paint)
                }
            } else {
                canvas.drawText(text, width - textWidth - mRoundRectFWidth / 2, y, paint)
            }
        }
    }

    init {
        val highlightColor = Color.parseColor("#FF007AFF")
        val defaultColor = Color.parseColor("#66000000")
        mRoundRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mRoundRectPaint.style = Paint.Style.FILL
        mRoundRectPaint.color = highlightColor

        mHighlightTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHighlightTextPaint.style = Paint.Style.FILL
        mHighlightTextPaint.textSize = 10.sp
        mHighlightTextPaint.color = highlightColor

        mDefaultTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDefaultTextPaint.textSize = 10.sp
        mDefaultTextPaint.style = Paint.Style.FILL
        mDefaultTextPaint.color = defaultColor

        mHighlightLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mHighlightLinePaint.strokeWidth = 2.px
        mHighlightLinePaint.color = highlightColor
        mHighlightLinePaint.style = Paint.Style.STROKE

        mDefaultLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mDefaultLinePaint.strokeWidth = 2.px
        mDefaultLinePaint.style = Paint.Style.STROKE

        mRoundRectF = RectF()
        mBoundaryTextList = ArrayList()
    }

    fun setData(data: MutableList<String>) {
        mBoundaryTextList = data
        invalidate()
    }
}