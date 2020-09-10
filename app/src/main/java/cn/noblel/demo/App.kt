package cn.noblel.demo

import android.app.Application
import com.kwai.koom.javaoom.KOOM

/**
 * @author noblel
 * @date 2020/9/8
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        KOOM.init(this)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        KOOM.getInstance().manualTrigger()
    }
}