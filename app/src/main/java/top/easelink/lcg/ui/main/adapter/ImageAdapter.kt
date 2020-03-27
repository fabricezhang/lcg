package top.easelink.lcg.ui.main.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R


@BindingAdapter("avatarUrl")
fun loadAvatar(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url)
        .transform(RoundedCorners(2.dpToPx(imageView.context).toInt()))
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.drawable.ic_noavatar_middle_gray)
        .into(imageView)
}

@BindingAdapter("imageUrlOnly")
fun loadImageNoHolder(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url)
        .transform(RoundedCorners(2.dpToPx(imageView.context).toInt()))
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(imageView)
}