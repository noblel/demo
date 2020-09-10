package cn.noblel.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.noblel.demo.bitmap.BitmapActivity
import cn.noblel.demo.ffmpeg.FFmpegActivity
import cn.noblel.demo.gl.GLViewActivity
import cn.noblel.demo.gl.preview.GLPreviewActivity
import cn.noblel.demo.layout.LayoutActivity
import cn.noblel.demo.mediacodec.VideoDecodeActivity
import cn.noblel.demo.surface.SurfaceViewActivity
import cn.noblel.demo.texture.CameraTextureActivity
import cn.noblel.demo.texture.TextureViewActivity
import cn.noblel.demo.thread.ThreadHookActivity
import cn.noblel.demo.viewpager.ViewPagerActivity
import java.util.*

/**
 * @author noblel
 * @date 2020/9/8
 */
class MainActivity : AppCompatActivity() {
    private val mActivityInfoItems: ArrayList<ActivityInfoItem?> = object : ArrayList<ActivityInfoItem?>() {
        init {
            add(ActivityInfoItem("Bitmap", BitmapActivity::class.java))
            add(ActivityInfoItem("FFmpeg", FFmpegActivity::class.java))
            add(ActivityInfoItem("GLPreviewActivity", GLPreviewActivity::class.java))
            add(ActivityInfoItem("GLViewActivity", GLViewActivity::class.java))
            add(ActivityInfoItem("Layout", LayoutActivity::class.java))
            add(ActivityInfoItem("SurfaceView", SurfaceViewActivity::class.java))
            add(ActivityInfoItem("ThreadHook", ThreadHookActivity::class.java))
            add(ActivityInfoItem("CameraTexture", CameraTextureActivity::class.java))
            add(ActivityInfoItem("TextureView", TextureViewActivity::class.java))
            add(ActivityInfoItem("ViewPager", ViewPagerActivity::class.java))
            add(ActivityInfoItem("VideoDecode", VideoDecodeActivity::class.java))
        }
    }

    internal class ActivityInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextView: TextView = itemView.findViewById(R.id.tv_info)

        fun bindData(infoItem: ActivityInfoItem) {
            mTextView.text = infoItem.info
            mTextView.setOnClickListener { mTextView.context.startActivity(Intent(mTextView.context, infoItem.activity)) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_activity)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = object : RecyclerView.Adapter<ActivityInfoViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityInfoViewHolder {
                val inflate = LayoutInflater.from(this@MainActivity)
                        .inflate(R.layout.item_acitivity_info, parent, false)
                return ActivityInfoViewHolder(inflate)
            }

            override fun onBindViewHolder(holder: ActivityInfoViewHolder, position: Int) {
                holder.bindData(mActivityInfoItems[position]!!)
            }

            override fun getItemCount(): Int {
                return mActivityInfoItems.size
            }
        }
    }

    internal class ActivityInfoItem(val info: String, activity: Class<out Activity?>) {
        val activity: Class<*>

        init {
            this.activity = activity
        }
    }
}