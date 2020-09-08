package cn.noblel.demo.thread

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.noblel.demo.R
import cn.noblel.threadhook.ThreadHook

/**
 * @author noblel
 * @date 2020/9/9
 */
class ThreadHookActivity : AppCompatActivity() {
    init {
        System.loadLibrary("thread-hook")
    }

    private var threadId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thread_hook)
        ThreadHook.doHook()
    }

    override fun onDestroy() {
        super.onDestroy()
        ThreadHook.doHook()
    }

    fun createThread(view: View) {
        threadId++
        object : Thread("test$threadId") {
            override fun run() {
                Log.e(TAG, "thread id is " + currentThread().id)
            }
        }.start()
    }

    companion object {
        private const val TAG = "ThreadHookActivity"
    }
}