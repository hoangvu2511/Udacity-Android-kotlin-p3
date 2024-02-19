package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlinx.coroutines.delay
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f

    private val valueAnimator = ValueAnimator().apply {
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        duration = 3_000L
    }

    private var initText: String = ""
    private var onLoadingText: String = ""
    private var onDoneText: String = ""

    private var btnText = initText

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Initial) { _, old, new ->
        btnText = when (new) {
            ButtonState.Loading -> onLoadingText
            ButtonState.Completed -> onDoneText
            else -> initText
        }
        if (old == ButtonState.Initial && new == ButtonState.Loading) {
            valueAnimator.start()
        }
        if (old == ButtonState.Loading && new == ButtonState.Completed) {
            widthAnim = widthSize
            valueAnimator.cancel()
        }

        isEnabled = new == ButtonState.Initial
        invalidate()
    }

    private val backgroundPaint = Paint().apply {
        color = 0xFF6EA4FC.toInt()
    }
    private var textColor: Int = Color.WHITE
    private val textPaint = Paint().apply {
        textSize = 48f
        color = textColor
    }
    private val textBound = Rect()


    private var widthAnim = 0f

    private val circlePaint = Paint().apply {
        color = 0xFF7466CE.toInt()
    }
    private var circlePercentage = 0f

    init {
        isEnabled = true
        valueAnimator.addUpdateListener {
            widthAnim = it.animatedValue as Float
            circlePercentage = widthAnim / widthSize
            invalidate()
        }
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            initText = getString(R.styleable.LoadingButton_initialText) ?: context.getString(R.string.button_name)
            onLoadingText = getString(R.styleable.LoadingButton_onLoadingText) ?: context.getString(R.string.button_loading)
            onDoneText = getString(R.styleable.LoadingButton_onDoneText) ?: context.getString(R.string.button_completed)
            textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                buttonState = ButtonState.Completed
            }
        })
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        with(canvas ?: return) {
            backgroundPaint.color = 0xFF6EA4FC.toInt()
            drawRect(0f, 0f, widthSize, heightSize, backgroundPaint)
            textPaint.getTextBounds(btnText, 0, btnText.length, textBound)

            if (buttonState != ButtonState.Initial) {
                backgroundPaint.color = 0xFF82DFEE.toInt()
                drawRect(0f, 0f, widthAnim, heightSize, backgroundPaint)
            }

            if (buttonState == ButtonState.Loading) {
                val radius = textBound.height()
                val left = (widthSize + textBound.width()) / 2 + 20f
                val top = heightSize / 2 - radius / 2
                drawArc(
                    left,
                    top,
                    left + radius,
                    top + radius,
                    -90f,
                    360 * circlePercentage,
                    true,
                    circlePaint
                )
            }

            drawText(
                btnText,
                (widthSize - textBound.width()) / 2,
                (heightSize - textBound.height()) / 2 + textBound.height(),
                textPaint
            )
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        valueAnimator.setFloatValues(widthSize)
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