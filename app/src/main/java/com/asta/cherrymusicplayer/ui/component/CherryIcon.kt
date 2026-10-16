package com.asta.cherrymusicplayer.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.asta.cherrymusicplayer.ui.theme.CherryPrimary
import com.asta.cherrymusicplayer.ui.theme.CherryTextSecondary

/**
 * Custom cherry-shaped icon for the favorite/like button.
 * Draws a pair of cherries with stems and a leaf.
 *
 * @param filled Whether the cherry is filled (favorited) or outlined (not favorited)
 * @param modifier Modifier for sizing and layout
 * @param filledColor Color when filled
 * @param outlineColor Color when outlined
 * @param size Size of the icon
 */
@Composable
fun CherryIcon(
    filled: Boolean,
    modifier: Modifier = Modifier,
    filledColor: Color = CherryPrimary,
    outlineColor: Color = CherryTextSecondary,
    size: Dp = 24.dp
) {
    val animatedColor by animateColorAsState(
        targetValue = if (filled) filledColor else outlineColor,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "CherryColorAnimation"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (filled) 1f else 0.95f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "CherryScaleAnimation"
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        val w = this.size.width * animatedScale
        val h = this.size.height * animatedScale
        val offsetX = (this.size.width - w) / 2f
        val offsetY = (this.size.height - h) / 2f

        // Cherry fruit radii - two circles
        val cherryRadius = w * 0.18f
        val leftCherryCenter = Offset(offsetX + w * 0.32f, offsetY + h * 0.72f)
        val rightCherryCenter = Offset(offsetX + w * 0.68f, offsetY + h * 0.78f)

        // Draw style
        val strokeWidth = w * 0.07f
        val drawStyle = if (filled) Fill else Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // Draw the two cherry fruits
        drawCircle(
            color = animatedColor,
            radius = cherryRadius,
            center = leftCherryCenter,
            style = drawStyle
        )
        drawCircle(
            color = animatedColor,
            radius = cherryRadius,
            center = rightCherryCenter,
            style = drawStyle
        )

        // Draw highlights on filled cherries (small white arc for shine)
        if (filled) {
            val highlightRadius = cherryRadius * 0.35f
            val highlightOffset = cherryRadius * 0.3f
            drawCircle(
                color = Color.White.copy(alpha = 0.35f),
                radius = highlightRadius,
                center = Offset(
                    leftCherryCenter.x - highlightOffset,
                    leftCherryCenter.y - highlightOffset
                )
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.35f),
                radius = highlightRadius,
                center = Offset(
                    rightCherryCenter.x - highlightOffset,
                    rightCherryCenter.y - highlightOffset
                )
            )
        }

        // Stem junction point (where both stems meet)
        val junctionPoint = Offset(offsetX + w * 0.52f, offsetY + h * 0.18f)

        // Stems - curved lines from each cherry to the junction
        val stemColor = if (filled) Color(0xFF2E7D32) else animatedColor
        val stemStroke = Stroke(
            width = strokeWidth * 0.8f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // Left stem
        val leftStemPath = Path().apply {
            moveTo(leftCherryCenter.x, leftCherryCenter.y - cherryRadius)
            quadraticTo(
                offsetX + w * 0.28f, offsetY + h * 0.35f,
                junctionPoint.x, junctionPoint.y
            )
        }
        drawPath(leftStemPath, stemColor, style = stemStroke)

        // Right stem
        val rightStemPath = Path().apply {
            moveTo(rightCherryCenter.x, rightCherryCenter.y - cherryRadius)
            quadraticTo(
                offsetX + w * 0.72f, offsetY + h * 0.35f,
                junctionPoint.x, junctionPoint.y
            )
        }
        drawPath(rightStemPath, stemColor, style = stemStroke)

        // Leaf at the junction point
        val leafColor = if (filled) Color(0xFF4CAF50) else animatedColor
        drawLeaf(
            center = Offset(junctionPoint.x + w * 0.08f, junctionPoint.y + h * 0.02f),
            width = w * 0.22f,
            height = h * 0.14f,
            color = leafColor,
            filled = filled,
            strokeWidth = strokeWidth * 0.7f
        )
    }
}

/**
 * Draws a small leaf shape using bezier curves.
 */
private fun DrawScope.drawLeaf(
    center: Offset,
    width: Float,
    height: Float,
    color: Color,
    filled: Boolean,
    strokeWidth: Float
) {
    val leafPath = Path().apply {
        // Start at the left tip of the leaf
        moveTo(center.x, center.y)
        // Upper curve
        quadraticTo(
            center.x + width * 0.5f, center.y - height,
            center.x + width, center.y
        )
        // Lower curve back to start
        quadraticTo(
            center.x + width * 0.5f, center.y + height * 0.6f,
            center.x, center.y
        )
        close()
    }

    val style = if (filled) Fill else Stroke(
        width = strokeWidth,
        cap = StrokeCap.Round,
        join = StrokeJoin.Round
    )
    drawPath(leafPath, color, style = style)

    // Leaf vein (center line)
    if (filled) {
        val veinPath = Path().apply {
            moveTo(center.x + width * 0.1f, center.y)
            quadraticTo(
                center.x + width * 0.5f, center.y - height * 0.25f,
                center.x + width * 0.85f, center.y
            )
        }
        drawPath(
            veinPath,
            Color(0xFF1B5E20).copy(alpha = 0.5f),
            style = Stroke(width = strokeWidth * 0.4f, cap = StrokeCap.Round)
        )
    }
}
