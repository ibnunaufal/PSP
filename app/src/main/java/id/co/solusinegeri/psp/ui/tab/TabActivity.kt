package id.co.solusinegeri.psp.ui.tab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import id.co.solusinegeri.psp.R
import id.co.solusinegeri.psp.ui.home.HomeFragment
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.tab_activity.*
import kotlinx.android.synthetic.main.tab_fragment.*

class TabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tab_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TabFragment.newInstance())
                .commitNow()
        }

        val homeFragment= HomeFragment()

        setCurrentFragment(homeFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.information -> setCurrentFragment(homeFragment)
                R.id.message -> setCurrentFragment(homeFragment)
                R.id.profile -> setCurrentFragment(homeFragment)
            }
            true
        }


    }
    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}