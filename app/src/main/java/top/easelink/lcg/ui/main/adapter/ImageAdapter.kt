package top.easelink.lcg.ui.main.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.RoundedCornersTransformation
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R


@BindingAdapter("avatarUrl")
fun loadAvatar(imageView: ImageView, url: String?) {
    imageView.load(url) {
        transformations(RoundedCornersTransformation(2.dpToPx(imageView.context)))
        placeholder(R.drawable.ic_noavatar_middle_gray)
    }
}