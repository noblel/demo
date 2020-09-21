package cn.noblel.demo.custom.seekbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.noblel.demo.R

/**
 * @author noblel
 * @date 2020/9/11
 */
class SeekBarFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_seek_bar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<StepSeekBar>(R.id.step_seek_bar).setData(mutableListOf("1","2","3","4","5","6"))
    }
}