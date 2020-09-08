package cn.noblel.demo.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import cn.noblel.demo.R
import com.google.android.material.tabs.TabLayout
import java.util.*

/**
 * @author noblel
 * @date 2020/9/8
 */
class ViewPagerActivity : AppCompatActivity() {
    private var demoCollectionPagerAdapter: DemoCollectionPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        for (i in 0..9) {
            val tab = tabLayout.newTab()
            val title = "测试 -$i"
            tab.text = title
            tabLayout.addTab(tab)
        }
        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        tabLayout.setupWithViewPager(viewPager)
        demoCollectionPagerAdapter = DemoCollectionPagerAdapter(supportFragmentManager)
        viewPager.adapter = demoCollectionPagerAdapter
    }

    class DemoCollectionPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(i: Int): Fragment {
            return DemoObjectFragment()
        }

        override fun getCount(): Int {
            return 100
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "OBJECT " + (position + 1)
        }
    }

    class DemoObjectFragment : Fragment() {
        private val mTextInfo: MutableList<TextInfoItem> = ArrayList()
        override fun onCreateView(inflater: LayoutInflater,
                                  container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_collection_object, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            for (i in 0..9) {
                mTextInfo.add(TextInfoItem("index-$i"))
            }
            val recyclerView: RecyclerView = view.findViewById(R.id.recycler)
            recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
            recyclerView.adapter = object : RecyclerView.Adapter<SimpleInfoViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleInfoViewHolder {
                    return SimpleInfoViewHolder(layoutInflater.inflate(R.layout.item_text_info, null))
                }

                override fun onBindViewHolder(holder: SimpleInfoViewHolder, position: Int) {
                    holder.bindData(mTextInfo[position])
                }

                override fun getItemCount(): Int {
                    return mTextInfo.size
                }
            }
        }

        internal class SimpleInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val mTextView: TextView = itemView.findViewById(R.id.tv_info)
            fun bindData(infoItem: TextInfoItem) {
                mTextView.text = infoItem.info
            }
        }

        internal class TextInfoItem(val info: String)
    }
}