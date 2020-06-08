package com.gmail.kenzhang0.binding

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl


object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visibleGone")
    fun showHide(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadBackgroundImage(view: ImageView, imageUrl: String?) {
        imageUrl?.apply {
            // For known issue with placeholder.com not allowing android user agent. SEE https://github.com/bumptech/glide/issues/4074
            val url = GlideUrl(
                imageUrl
            )
            Glide.with(view.context).load(url).into(view)
        }
    }
}
