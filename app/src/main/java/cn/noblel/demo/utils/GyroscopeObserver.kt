package cn.noblel.demo.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * @author noblel
 * @date 2020/9/8
 */

class GyroscopeObserver(context: Context?) : SensorEventListener {
    private var mListener: GyroDataChangeListener? = null
    private var mTimestamp = 0f
    private val mSensorManager: SensorManager?
    private val mAngles = FloatArray(3)
    private var mPreviousY = 0f //记录xy坐标位置
    private var mPreviousX = 0f
    private var angleX = 0f
    private var angleY = 90f
    private val angleZ = 0f

    interface GyroDataChangeListener {
        fun onCoordinateUpdate(x: Float, y: Float, z: Float)
    }

    fun subscribe(listener: GyroDataChangeListener?) {
        mListener = listener
        if (mSensorManager != null) {
            val gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            mSensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    fun unSubscribe() {
        mListener = null
        mSensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (Sensor.TYPE_GYROSCOPE == event.sensor.type) {
            if (mTimestamp != 0f) {
                val dT = (event.timestamp - mTimestamp) * 1.0f / 1000000000.0f
                mAngles[0] += event.values[0] * dT
                mAngles[1] += event.values[1] * dT
                mAngles[2] += event.values[2] * dT
                val x = Math.toDegrees(mAngles[1].toDouble()).toFloat()
                val y = Math.toDegrees(mAngles[0].toDouble()).toFloat()
                val z = Math.toDegrees(mAngles[2].toDouble()).toFloat()
                val dx = x - mPreviousX
                val dy = y - mPreviousY
                angleY += dx * 2.0f
                angleX += dy * 0.5f
                if (angleX < -50f) {
                    angleX = -50f
                } else if (angleX > 50f) {
                    angleX = 50f
                }
                mPreviousY = y
                mPreviousX = x
                if (mListener != null) {
                    mListener!!.onCoordinateUpdate(angleX, angleY, angleZ)
                }
            }
            mTimestamp = event.timestamp.toFloat()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //empty
    }

    init {
        requireNotNull(context) { "context is null" }
        mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}
