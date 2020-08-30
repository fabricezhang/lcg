package top.easelink.lcg.ui.profile.view

import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_settings.*
import top.easelink.framework.topbase.TopActivity
import top.easelink.framework.topbase.TopFragment
import top.easelink.framework.utils.addFragmentInActivity
import top.easelink.lcg.R
import top.easelink.lcg.ui.profile.viewmodel.ProfileViewModel

const val KEY_PROFILE_URL = "profile_url"

class ProfileActivity : TopActivity() {

    private lateinit var mViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        setContentView(R.layout.activity_profile)
        setUp()
    }

    private fun setUp() {
        setupToolBar()
        showFragment(ProfileFragment.newInstance(intent.getStringExtra(KEY_PROFILE_URL)))
    }

    private fun showFragment(fragment: TopFragment) {
        addFragmentInActivity(
            supportFragmentManager,
            fragment,
            R.id.profile_root
        )
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
