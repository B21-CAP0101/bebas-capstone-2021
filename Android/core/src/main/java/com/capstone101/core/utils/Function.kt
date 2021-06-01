package com.capstone101.core.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.capstone101.core.R
import com.google.android.material.snackbar.Snackbar

object Function {
    fun View.createSnackBar(message: String, duration: Int) {
        Snackbar.make(this, message, duration)
            .setBackgroundTint(getColor(resources, R.color.white, resources.newTheme()))
            .setTextColor(getColor(resources, R.color.black_40off, resources.newTheme()))
            .show()
    }


    fun View.hideKeyboard(): Boolean =
        (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)


    fun View.showKeyboard() =
        (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, 0)

    fun setOnPressEnter(
        editText: EditText,
        btn: View
    ) {
        editText.setOnEditorActionListener { _, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                btn.performClick()
            }
            return@setOnEditorActionListener false
        }
    }

    fun View.glide(url: String, imgView: ImageView, placeHolder: Int) {
        Glide.with(this).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(placeHolder)
                .error(placeHolder)
                .centerInside()
        ).load(url).transition(DrawableTransitionOptions.withCrossFade())
            .into(imgView)
    }

    fun View.glideWithLoading(
        url: String,
        imgView: ImageView,
        placeHolder: Int,
        loading: View,
        allView: List<View>? = null
    ) {
        Glide.with(this).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(placeHolder)
                .error(placeHolder)
                .centerInside()
        ).load(url).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                loading.isVisible = false
                allView?.forEach { view ->
                    view.isVisible = true
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                loading.isVisible = false

                allView?.forEach { view ->
                    view.isVisible = true
                }
                return false
            }
        }).transition(DrawableTransitionOptions.withCrossFade())
            .into(imgView)
    }

    fun Context.createToast(message: String, duration: Int) {
        val mToast = Toast.makeText(this, "", duration)
        mToast.setText(message)
        mToast.show()
    }
}