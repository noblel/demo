package cn.noblel.demo.mediacodec

import android.media.*

/**
 * @author noblel
 * @date 2020/9/10
 */
class AudioMediaCodecWorker(private val mVideoPath: String) : Thread() {
    private var mExtractor: MediaExtractor? = null
    private var mCodec: MediaCodec? = null
    private var mAudioTrack: AudioTrack? = null
    override fun run() {
        mExtractor = MediaExtractor()
        mExtractor!!.setDataSource(mVideoPath)
        for (index in 0 until mExtractor!!.trackCount) {
            val format = mExtractor!!.getTrackFormat(index)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("audio/")) {
                mExtractor!!.selectTrack(index)
                mCodec = MediaCodec.createDecoderByType(mime)
                mCodec!!.configure(format, null, null, 0)
                val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                val audioChannel = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                mAudioTrack = AudioTrack(
                        AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                                .build(),
                        AudioFormat.Builder()
                                .setChannelMask(if (audioChannel == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO)
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(sampleRate)
                                .build(),
                        AudioRecord.getMinBufferSize(
                                sampleRate,
                                if (audioChannel == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_IN_STEREO,
                                AudioFormat.ENCODING_PCM_16BIT
                        ),
                        AudioTrack.MODE_STREAM,
                        AudioManager.AUDIO_SESSION_ID_GENERATE
                )
                break
            }
        }
        if (mCodec == null) {
            mExtractor?.release()
            return
        }
        mCodec!!.start()
        mAudioTrack!!.play()
        val bufferInfo = MediaCodec.BufferInfo()
        val start = System.currentTimeMillis()
        while (!interrupted()) {
            val inIndex = mCodec!!.dequeueInputBuffer(10 * 1000.toLong()) //10ms
            if (inIndex >= 0) {
                val buffer = mCodec?.getInputBuffer(inIndex)
                val sampleSize = mExtractor!!.readSampleData(buffer!!, 0)
                if (sampleSize < 0) {
                    mCodec!!.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                } else {
                    mCodec!!.queueInputBuffer(inIndex, 0, sampleSize, mExtractor!!.sampleTime, 0)
                    mExtractor!!.advance()
                }
            }
            when (val outIndex = mCodec!!.dequeueOutputBuffer(bufferInfo, 10 * 1000.toLong())) {
                MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> {
                }
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                }
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                }
                else -> {
                    val buffer = mCodec!!.getOutputBuffer(outIndex)
                    buffer!!.position(0)
                    val outData = ByteArray(bufferInfo.size)
                    buffer.get(outData)
                    buffer.clear()
                    mAudioTrack?.write(outData, bufferInfo.offset, bufferInfo.offset + outData.size)
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
        mAudioTrack?.stop()
        mAudioTrack?.release()
    }

    companion object {
        private const val TAG = "AudioMediaCodecWorker"
    }
}