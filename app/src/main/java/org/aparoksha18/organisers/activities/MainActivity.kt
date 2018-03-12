package org.aparoksha18.organisers.activities

import android.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_main.*
import org.aparoksha18.organisers.fragments.NewFragment
import org.aparoksha18.organisers.fragments.Old_fragment
import org.aparoksha18.organisers.R
import org.aparoksha18.organisers.fragments.ApproveFragment

class MainActivity : AppCompatActivity() {

    private var admin = false

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.new_notification -> {
                val newFragment = Fragment.instantiate(this, NewFragment::class.java.name)
                        as NewFragment
                fragmentManager.beginTransaction().replace(R.id.fragment,newFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.sent_notifications -> {
                val oldFragment = Fragment.instantiate(this, Old_fragment::class.java.name)
                        as Old_fragment
                fragmentManager.beginTransaction().replace(R.id.fragment,oldFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private val mOnAdminNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.new_notification -> {
                val newFragment = Fragment.instantiate(this, NewFragment::class.java.name)
                        as NewFragment
                fragmentManager.beginTransaction().replace(R.id.fragment,newFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.sent_notifications -> {
                val oldFragment = Fragment.instantiate(this, Old_fragment::class.java.name)
                        as Old_fragment
                fragmentManager.beginTransaction().replace(R.id.fragment,oldFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.approve_notifications -> {
                val approveFragment = Fragment.instantiate(this, ApproveFragment::class.java.name)
                        as ApproveFragment
                fragmentManager.beginTransaction().replace(R.id.fragment,approveFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "Organisers App"

        admin = intent.getBooleanExtra("admin",false)

        if (admin) {
            navigation.visibility = View.GONE
            admin_navigation.visibility = View.VISIBLE
            admin_navigation.setOnNavigationItemSelectedListener(mOnAdminNavigationItemSelectedListener)
        } else {
            navigation.visibility = View.VISIBLE
            admin_navigation.visibility = View.GONE
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        }

        val newFragment = Fragment.instantiate(this, NewFragment::class.java.name)
                as NewFragment
        fragmentManager.beginTransaction().replace(R.id.fragment,newFragment).commit()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.sign_out) {
            AuthUI.getInstance().signOut(this)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
