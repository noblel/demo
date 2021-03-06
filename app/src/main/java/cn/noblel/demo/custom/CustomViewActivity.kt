package cn.noblel.demo.custom

import android.os.Bundle
import android.widget.Button
import cn.noblel.demo.R
import cn.noblel.demo.base.BaseActivity
import cn.noblel.demo.custom.seekbar.SeekBarFragment

class CustomViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custorm_view)
        findViewById<Button>(R.id.btn_seek_bar).setOnClickListener {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SeekBarFragment())
                    .commitAllowingStateLoss()
        }
    }
}