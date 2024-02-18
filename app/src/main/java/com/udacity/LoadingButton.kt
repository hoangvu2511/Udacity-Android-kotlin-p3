package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.delay
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val valueAnimator = ValueAnimator()

    private var btnText = context.getString(R.string.button_name)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Initial) { _, _, new ->
        btnText = when (new) {
            ButtonState.Loading -> context.getString(R.string.button_loading)
            ButtonState.Completed -> context.getString(R.string.button_completed)
            else -> context.getString(R.string.button_name)
        }
        isEnabled = new == ButtonState.Initial
        invalidate()
    }

    private val btnPaint = Paint()
    private val textPaint = Paint().apply {
        textSize = 48f
        color = Color.WHITE
    }
    private val textBound = Rect()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        btnPaint.color = Color.CYAN
        canvas?.drawRect(
            0f, 0f, width.toFloat(), height.toFloat(),
            btnPaint
        )

        textPaint.getTextBounds(btnText, 0, btnText.length, textBound)
        canvas?.drawText(
            btnText,
            (width - textBound.width()) / 2f,
            (height - textBound.height()) / 2f + textBound.height(),
            textPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun onHasDownloadId(id: Long = 0L) {
        buttonState = if (id == 0L) {
            ButtonState.Completed
        } else {
            ButtonState.Loading
        }
    }

    fun reset() {
        buttonState = ButtonState.Initial
    }

}