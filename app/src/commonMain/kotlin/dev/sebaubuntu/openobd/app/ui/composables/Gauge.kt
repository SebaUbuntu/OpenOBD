/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.composables

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import dev.sebaubuntu.openobd.core.ext.degreesToRadians
import kotlin.math.cos
import kotlin.math.sin

/**
 * Thumb size percentage.
 */
private const val THUMB_SIZE = 24f / 196f

/**
 * Needle length percentage.
 */
private const val NEEDLE_LENGTH = 160f / 196f

/**
 * Needle base width percentage.
 */
private const val NEEDLE_BASE_WIDTH = 10f / 196f

/**
 * Indicator text distance percentage.
 */
private const val INDICATOR_TEXT_DISTANCE = 180f / 196f

/**
 * Gauge.
 *
 * [Original source](https://proandroiddev.com/creating-a-custom-gauge-speedometer-in-jetpack-compose-bd3c3d72074b)
 *
 * @param value The percentage value to display on the needle, 0.0 to 1.0
 * @param labels The labels to display on the needle, keyed by the value to display
 * @param modifier The modifier to apply to this layout
 * @param color The color of the needle
 * @param rotationAngle The base angle of the needle
 * @param startAngle The start angle of the needle
 * @param sweepAngle The sweep angle of the needle
 * @param invert Whether to invert the sweep and start angles
 * @param animationSpec The animation spec to use when animating the [value]
 */
@Composable
fun Gauge(
    value: Float,
    labels: Map<Float, String> = mapOf(),
    labelsTextSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    @androidx.annotation.FloatRange(from = 0.0, to = 360.0) rotationAngle: Float = 0f,
    @androidx.annotation.FloatRange(from = 0.0, to = 360.0) startAngle: Float = 150f,
    @androidx.annotation.FloatRange(from = 0.0, to = 360.0) sweepAngle: Float = 240f,
    invert: Boolean = false,
    animationSpec: AnimationSpec<Float> = spring(),
) {
    Box(
        modifier = modifier,
    ) {
        NeedleCanvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            value = value,
            startAngle = startAngle + rotationAngle,
            sweepAngle = sweepAngle,
            invert = invert,
            color = color,
            animationSpec = animationSpec,
        )

        LabelsCanvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            labels = labels,
            labelsTextSize = labelsTextSize,
            color = color,
            startAngle = startAngle + rotationAngle,
            sweepAngle = sweepAngle,
            invert = invert,
        )
    }
}

@Composable
private fun NeedleCanvas(
    modifier: Modifier = Modifier,
    value: Float,
    @androidx.annotation.FloatRange(from = 0.0, to = 360.0) startAngle: Float,
    @androidx.annotation.FloatRange(from = 0.0, to = 360.0) sweepAngle: Float,
    invert: Boolean,
    color: Color,
    animationSpec: AnimationSpec<Float>,
) {
    val coercedValue = value.coerceIn(0f, 1f)

    val animatedValue by animateFloatAsState(
        targetValue = coercedValue,
        animationSpec = animationSpec,
        label = "gaugeValue",
    )

    Canvas(modifier = modifier) {
        val dimension = size.minDimension

        val center = center

        drawCircle(
            color = color,
            radius = dimension * THUMB_SIZE / 1.2f,
            center = center,
        )

        val needleLength = dimension * NEEDLE_LENGTH / 2
        val needleBaseWidth = dimension * NEEDLE_BASE_WIDTH

        // Calculate needle angle based on inputValue
        val needleAngle = when (invert) {
            true -> sweepAngle - (animatedValue * sweepAngle)
            false -> (animatedValue * sweepAngle)
        } + startAngle

        val needlePath = Path().apply {
            // Calculate the top point of the needle
            val topX = center.x + needleLength * cos(
                needleAngle.toDouble().degreesToRadians().toFloat()
            )
            val topY = center.y + needleLength * sin(
                needleAngle.toDouble().degreesToRadians().toFloat()
            )

            // Calculate the base points of the needle
            val baseLeftX = center.x + needleBaseWidth * cos(
                (needleAngle - 90).toDouble().degreesToRadians().toFloat()
            )
            val baseLeftY = center.y + needleBaseWidth * sin(
                (needleAngle - 90).toDouble().degreesToRadians().toFloat()
            )
            val baseRightX = center.x + needleBaseWidth * cos(
                (needleAngle + 90).toDouble().degreesToRadians().toFloat()
            )
            val baseRightY = center.y + needleBaseWidth * sin(
                (needleAngle + 90).toDouble().degreesToRadians().toFloat()
            )

            moveTo(topX, topY)
            lineTo(baseLeftX, baseLeftY)
            lineTo(baseRightX, baseRightY)
            close()
        }

        drawPath(
            color = color,
            path = needlePath,
        )
    }
}

@Composable
private fun LabelsCanvas(
    modifier: Modifier = Modifier,
    labels: Map<Float, String>,
    labelsTextSize: TextUnit,
    color: Color,
    @androidx.annotation.FloatRange(from = 0.0, to = 360.0) startAngle: Float,
    @androidx.annotation.FloatRange(from = 0.0, to = 360.0) sweepAngle: Float,
    invert: Boolean,
) {
    val textMeasurer = rememberTextMeasurer()

    val labelsToTextLayoutResult = remember {
        labels.mapValues {
            textMeasurer.measure(
                text = it.value,
                style = TextStyle.Default.copy(fontSize = labelsTextSize),
            )
        }
    }

    Canvas(
        modifier = modifier,
    ) {
        val dimension = size.minDimension

        val center = center

        val distance = dimension * INDICATOR_TEXT_DISTANCE / 2

        labels.forEach { (value, _) ->
            // Calculate needle angle based on inputValue
            val angle = when (invert) {
                true -> sweepAngle - (value * sweepAngle)
                false -> (value * sweepAngle)
            } + startAngle

            val textLayoutResult = labelsToTextLayoutResult[value]!!

            val baseX = center.x + distance * cos(
                angle.toDouble().degreesToRadians().toFloat()
            )
            val baseY = center.y + distance * sin(
                angle.toDouble().degreesToRadians().toFloat()
            )

            val x = (baseX - (textLayoutResult.size.width / 2))
                .coerceIn(0f, (center.x * 2) - textLayoutResult.size.width)
            val y = (baseY - (textLayoutResult.size.height / 2))
                .coerceIn(0f, (center.y * 2) - textLayoutResult.size.height)

            drawText(
                textLayoutResult = textLayoutResult,
                color = color,
                topLeft = Offset(x, y),
            )
        }
    }
}
