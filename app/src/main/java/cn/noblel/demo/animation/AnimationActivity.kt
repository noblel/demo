package cn.noblel.demo.animation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.transition.*
import cn.noblel.demo.R
import cn.noblel.demo.base.BaseActivity

class AnimationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation)
        supportFragmentManager.beginTransaction().apply {
            replace(android.R.id.content, FragmentFirst())
            commit()
        }
    }

    class FragmentFirst : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_first, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val tvPhoto = view.findViewById<ImageView>(R.id.iv_photo)
            tvPhoto.setOnClickListener {
                parentFragmentManager.beginTransaction().apply {
                    addSharedElement(it, it.transitionName)
                    replace(android.R.id.content, FragmentSecond())
                    commit()
                }
            }
        }
    }

    class FragmentSecond : Fragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val fade = Fade()
            fade.excludeTarget(R.id.tv_photo, true)
            fade.excludeTarget(R.id.tv_launcher, true)
            fade.duration = 1000
            enterTransition = fade
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move)
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_second, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            val content = view.findViewById<FrameLayout>(R.id.test_content)
            val tvTest = view.findViewById<ImageView>(R.id.tv_launcher)
            val btn = view.findViewById<Button>(R.id.tv_change)
            btn.setOnClickListener {
                val fade = Fade()
                fade.duration = 1000
                TransitionManager.beginDelayedTransition(content, fade)
                if (tvTest.visibility == GONE) {
                    tvTest.visibility = VISIBLE
                } else {
                    tvTest.visibility = GONE
                }
            }
        }
    }
}