package cn.noblel.demo.layout

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import cn.noblel.demo.R

/**
 * @author noblel
 * @date 2020/9/8
 */
class LayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)
        findViewById<View>(R.id.button).setOnClickListener { v: View? ->
            val constraintSet = ConstraintSet()
            val constraintLayout = findViewById<ConstraintLayout>(R.id.root)
            constraintSet.clone(constraintLayout)
            constraintSet.clear(R.id.button2, ConstraintSet.START)
            constraintSet.constrainWidth(R.id.button2, 0)
            TransitionManager.beginDelayedTransition(constraintLayout, AutoTransition().setDuration(5000))
            constraintSet.applyTo(constraintLayout)
        }
    }
}