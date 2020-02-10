package top.easelink.lcg.ui.setting.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_settings.*
import top.easelink.framework.topbase.TopActivity
import top.easelink.lcg.R
import top.easelink.lcg.config.AppConfig
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.ui.setting.viewmodel.SettingViewModel


class SettingActivity : TopActivity() {

    private lateinit var mViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        setContentView(R.layout.activity_settings)
        mViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        setUp()
    }

    override fun onResume() {
        super.onResume()
        val configuration = resources.configuration
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    private fun setUp() {
        if (!UserData.loggedInState) {
            sync_favorites_switch.isEnabled = false
            auto_sign_switch.isEnabled = false
            AppConfig.autoSignEnable = false
            AppConfig.syncFavorites = false
        }
        setupToolBar()
        setupListeners()
        setupObserver()
        mViewModel.init()
    }

    private fun setupToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupListeners() {
        sync_favorites_switch.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.scheduleJob(isChecked)
        }
        auto_sign_switch.setOnCheckedChangeListener { _, isChecked ->
            mViewModel.setSyncFavorite(isChecked)
        }
        search_engine_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                AppConfig.defaultSearchEngine = position
            }

        }
        show_search_result_in_webview.setOnCheckedChangeListener { _, isChecked ->
            AppConfig.searchResultShowInWebView = isChecked
        }
    }

    private fun setupObserver() {
        mViewModel.syncFavoriteEnable.observe(this, Observer {
            sync_favorites_switch.isChecked = it
        })
        mViewModel.autoSignInEnable.observe(this, Observer {
            auto_sign_switch.isChecked = it
        })
        mViewModel.searchEngineSelected.observe(this, Observer {
            search_engine_spinner.setSelection(it, true)
        })
        mViewModel.openSearchResultInWebView.observe(this, Observer {
            show_search_result_in_webview.isChecked = it
        })
    }
}
