package com.muflidevs.paradisata.view.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.muflidevs.paradisata.R

class CustomButtonGoogle : AppCompatButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    private var txtColor: Int = 0
    private var enabledBackground: Drawable
    init {
        txtColor = ContextCompat.getColor(context, R.color.md_theme_onSecondaryContainer)
        enabledBackground = ContextCompat.getDrawable(context,R.drawable.custom_bg_btn_google) as Drawable

    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = enabledBackground
        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
        text = context.getString(R.string.login_dengan_menggunakan_google)
    }
}