package com.example.fido_demo.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun setupSystemBar(window: Window, statusBarColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val decorView = window.decorView as ViewGroup

            // -- STATUS BAR VIEW SETUP --
            var statusBarView = decorView.findViewWithTag<View>("customStatusBarView")
            if (statusBarView == null) {
                statusBarView = View(window.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0
                    )
                    tag = "customStatusBarView"
                    setBackgroundColor(statusBarColor)
                }
                decorView.addView(statusBarView)
            }

            ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
                val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                val isImeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

                // Set status bar height
                statusBarView.layoutParams.height = systemInsets.top
                statusBarView.requestLayout()

                // Only apply bottom padding to content — NOT to BottomAppBar
                val contentView = decorView.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)

                contentView?.setPadding(
                    0,
                    systemInsets.top,
                    0,
                    if (isImeVisible) imeInsets.bottom else systemInsets.bottom
                )

                WindowInsetsCompat.CONSUMED
            }
            ViewCompat.requestApplyInsets(decorView)

        }
        else{

            // For Android 14 and below
            window.statusBarColor = statusBarColor
        }

    }
}
