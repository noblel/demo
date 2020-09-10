package cn.noblel.demo.mediacodec

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import android.view.Surface

class MediaCodecThread(private val mSurface: Surface, private val mVideoPath: String) : Thread() {
    private var mExtractor: MediaExtractor? = null
    private var mCodec: MediaCodec? = null
    override fun run() {
        mExtractor = MediaExtractor()
        mExtractor!!.setDataSource(mVideoPath)
        for (index in 0 until mExtractor!!.trackCount) {
            val format = mExtractor!!.getTrackFormat(index)
            Log.i(TAG, "index=$index, format=$format")
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("video/")) {
                mExtractor!!.selectTrack(index)
                mCodec = MediaCodec.createDecoderByType(mime)
                mCodec!!.configure(format, mSurface, null, 0)
                break
            }
        }
        if (mCodec == null) {
            mExtractor?.release()
            return
        }
        mCodec!!.start()
        val bufferInfo = MediaCodec.BufferInfo()
        var isEOS = false
        val start = System.currentTimeMillis()
        while (!interrupted()) {
            if (!isEOS) {
                val inIndex = mCodec!!.dequeueInputBuffer(10 * 1000.toLong()) //10ms
                if (inIndex >= 0) {
                    val buffer = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        mCodec?.getInputBuffer(inIndex)
                    } else {
                        mCodec!!.inputBuffers[inIndex]
                    }
                    val sampleSize = mExtractor!!.readSampleData(buffer!!, 0)
                    if (sampleSize < 0) {
                        mCodec!!.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        isEOS = true
                    } else {
                        mCodec!!.queueInputBuffer(inIndex, 0, sampleSize, mExtractor!!.sampleTime, 0)
                        mExtractor!!.advance()
                    }
                }
            }
            when (val outIndex = mCodec!!.dequeueOutputBuffer(bufferInfo, 10 * 1000.toLong())) {
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                }
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                }
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                } else -> {
                    while (bufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - start) {
                        try {
                            sleep(10)
                        } catch (e: Exception) {
                            if (e is InterruptedException) {
                                finish()
                                return
                            }
                            e.printStackTrace()
                        }
                    }
                    mCodec!!.releaseOutputBuffer(outIndex, true)
                }
            }
            if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                break
            }
        }
        finish()
    }

    private fun finish() {
        mCodec!!.stop()
        mCodec!!.release()
        mExtractor!!.release()
    }

    companion object {
        private const val TAG = "MediaCodecThread"
    }
}