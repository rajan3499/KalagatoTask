package com.app.kalagatotask.utils

import android.view.MotionEvent
import androidx.annotation.FloatRange
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

fun scaleIntoContainer(
    direction: LayoutDirection = LayoutDirection.Ltr,
    initialScale: Float = if (direction == LayoutDirection.Rtl) 1.1f else 0.9f
): EnterTransition {
    return scaleIn(
        animationSpec = tween(220, delayMillis = 90),
        initialScale = initialScale
    ) + fadeIn(animationSpec = tween(220, delayMillis = 90))
}

fun scaleOutOfContainer(
    direction: LayoutDirection = LayoutDirection.Rtl,
    targetScale: Float = if (direction == LayoutDirection.Ltr) 1.1f else 0.9f
): ExitTransition {
    return scaleOut(
        animationSpec = tween(
            durationMillis = 220,
            delayMillis = 90
        ), targetScale = targetScale
    ) + fadeOut(tween(delayMillis = 90))
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> CircularReveal(
    targetState: T,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    content: @Composable (T) -> Unit
) {
    val items = remember { mutableStateListOf<CircularRevealAnimationItem<T>>() }
    val transitionState = remember { MutableTransitionState(targetState) }
    val targetChanged = (targetState != transitionState.targetState)
    var offset: Offset? by remember { mutableStateOf(null) }
    transitionState.targetState = targetState
    val transition = rememberTransition(transitionState, label = "transition")
    if (targetChanged || items.isEmpty()) {
        // Only manipulate the list when the state is changed, or in the first run.
        val keys = items.map { it.key }.run {
            if (!contains(targetState)) {
                toMutableList().also { it.add(targetState) }
            } else {
                this
            }
        }
        items.clear()
        keys.mapIndexedTo(items) { index, key ->
            CircularRevealAnimationItem(key) {
                val progress by transition.animateFloat(
                    transitionSpec = { animationSpec }, label = ""
                ) {
                    if (index == keys.size - 1) {
                        if (it == key) 1f else 0f
                    } else 1f
                }
                Box(Modifier.circularReveal(progress = progress, offset = offset)) {
                    content(key)
                }
            }
        }
    } else if (transitionState.currentState == transitionState.targetState) {
        // Remove all the intermediate items from the list once the animation is finished.
        items.removeAll { it.key != transitionState.targetState }
    }

    Box(modifier.pointerInteropFilter {
        offset = when (it.action) {
            MotionEvent.ACTION_DOWN -> Offset(it.x, it.y)
            else -> null
        }!!
        false
    }) {
        items.forEach {
            key(it.key) {
                it.content()
            }
        }
    }
}

private data class CircularRevealAnimationItem<T>(
    val key: T,
    val content: @Composable () -> Unit
)

fun Modifier.circularReveal(
    @FloatRange(from = 0.0, to = 1.0)
    progress: Float, offset: Offset? = null
) = clip(CircularRevealShape(progress, offset))

private class CircularRevealShape(
    @FloatRange(from = 0.0, to = 1.0)
    private val progress: Float,
    private val offset: Offset? = null
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(Path().apply {
            val radius = (size.width.coerceAtLeast(size.height) * progress)
            addOval(
                Rect(
                    left = (offset?.x ?: (size.width / 2f)) - radius,
                    top = (offset?.y ?: (size.height / 2f)) - radius,
                    right = (offset?.x ?: (size.width / 2f)) + radius,
                    bottom = (offset?.y ?: (size.height / 2f)) + radius
                ),

                Path.Direction.Clockwise
            )
        })
    }
}