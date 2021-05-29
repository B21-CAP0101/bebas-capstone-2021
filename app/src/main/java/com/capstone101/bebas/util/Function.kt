package com.capstone101.bebas.util

import android.app.Activity
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.FragmentActivity
import com.capstone101.bebas.R
import com.capstone101.bebas.main.MainActivity
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

    fun clearWelcomeActivityAndCreateMainActivity(activity: FragmentActivity): Intent {
        val i = Intent(activity, MainActivity::class.java)
        // set the new task and clear flags
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


        return i
    }
}