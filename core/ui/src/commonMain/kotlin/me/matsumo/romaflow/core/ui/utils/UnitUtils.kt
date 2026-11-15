package me.matsumo.romaflow.core.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

@Composable
@ReadOnlyComposable
fun pxToMm(px: Int): Float = with(LocalDensity.current) {
    val dpi = density * 160f
    px / dpi * 25.4f
}

@Composable
@ReadOnlyComposable
fun mmToPx(mm: Float): Int = with(LocalDensity.current) {
    val dpi = density * 160f
    (mm / 25.4f * dpi).roundToInt()
}

@Composable
@ReadOnlyComposable
fun Int.toDp(): Dp = with(LocalDensity.current) {
    toDp()
}

@Composable
@ReadOnlyComposable
fun Dp.toPx(): Float = with(LocalDensity.current) {
    toPx()
}
