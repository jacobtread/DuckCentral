package com.jacobtread.duck.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * DuckCentralTheme Composable element for a custom version of the
 * MaterialTheme with the specific parameters set to match the theme
 * of DuckCentral.
 *
 * @param content The inner content to place inside the theme element
 */
@Composable
fun DuckCentralTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        darkColors(
            primary = Primary,
            primaryVariant = PrimaryDark,
            onPrimary = TextOn,

            secondary = Secondary,
            secondaryVariant = SecondaryDark,
            onSecondary = TextOn,
        ),
        Typography(
            body1 = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            button = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
            ),
            caption = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            )
        ),
        Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(4.dp),
            large = RoundedCornerShape(0.dp)
        ),
        content
    )
}
