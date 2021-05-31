package com.capstone101.bebas.util

import android.app.Activity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat.getColor
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.capstone101.bebas.R
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

    fun View.glide(url: String, imgView: ImageView) {
        Glide.with(this).setDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_guard_small)
                .error(R.drawable.ic_guard_small)
                .centerInside()
        ).load(url)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imgView)
    }
}