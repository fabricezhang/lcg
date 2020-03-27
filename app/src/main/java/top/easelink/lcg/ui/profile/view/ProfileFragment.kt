package top.easelink.lcg.ui.profile.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import top.easelink.framework.topbase.TopActivity
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.profile.viewmodel.ProfileViewModel

class ProfileFragment: TopFragment() {

    private lateinit var mViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProvider(mContext as TopActivity)[ProfileViewModel::class.java]
    }

    companion object {
        private const val PROFILE_URL = "profile_url"
        @JvmStatic
        fun newInstance(profileUrl: String): ProfileFragment {
            return ProfileFragment().also {
                it.arguments = Bundle().apply {
                    putString(PROFILE_URL, profileUrl)
                }
            }
        }
    }
}