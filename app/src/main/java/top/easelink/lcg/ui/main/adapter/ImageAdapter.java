package top.easelink.lcg.ui.main.adapter;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;

import top.easelink.lcg.R;

public class ImageAdapter {

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_noavatar_middle)
                .into(imageView);
    }

    @BindingAdapter("avatarUrl")
    public static void loadAvatar(ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.ic_noavatar_middle_gray)
                .into(imageView);
    }

    @BindingAdapter("imageUrlOnly")
    public static void loadImageNoHolder(ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .load(url)
                .into(imageView);
    }
}
