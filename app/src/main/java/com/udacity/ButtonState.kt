package com.udacity


sealed class ButtonState {
    object Initial : ButtonState()
    object Loading : ButtonState()
    object Completed : ButtonState()
}