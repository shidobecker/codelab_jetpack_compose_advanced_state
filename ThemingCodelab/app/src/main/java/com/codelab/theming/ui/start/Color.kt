package com.codelab.theming.ui.start

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val Red200 = Color(0xfff297a2)
val Red300 = Color(0xffea6d7e)

val Red700 = Color(0xffdd0d3c)
val Red800 = Color(0xffd00036)
val Red900 = Color(0xffc20029)

/**
Note: To convert from the common ‘#dd0d3c' format for specifying colors, replace the ‘#' with ‘0xff' i.e. Color(0xffdd0d3c) where ‘ff' means full alpha.
        **/


/**
 * Note: When defining colors, we name them "literally", based on the color value, rather than "semantically" e.g. Red500 not primary. This enables us to define multiple themes e.g. another color might be considered primary in dark theme or on a differently styled screen.
        **/


val LightColors = lightColors(
    primary = Red700,
    primaryVariant = Red900,
    onPrimary = Color.White,
    secondary = Red700,
    secondaryVariant = Red900,
    onSecondary = Color.White,
    error = Red800
)


val DarkColors = darkColors(
    primary = Red300,
    primaryVariant = Red700,
    onPrimary = Color.Black,
    secondary = Red300,
    onSecondary = Color.Black,
    error = Red200
)