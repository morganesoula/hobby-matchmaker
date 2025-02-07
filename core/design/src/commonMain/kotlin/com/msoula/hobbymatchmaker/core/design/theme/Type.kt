package com.msoula.hobbymatchmaker.core.design.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.msoula.hobbymatchmaker.core.design.Res
import com.msoula.hobbymatchmaker.core.design.quicksand_bold
import com.msoula.hobbymatchmaker.core.design.quicksand_light
import com.msoula.hobbymatchmaker.core.design.quicksand_medium
import com.msoula.hobbymatchmaker.core.design.quicksand_regular
import com.msoula.hobbymatchmaker.core.design.quicksand_semibold
import org.jetbrains.compose.resources.Font

@Composable
fun getTypography(): Typography {
    val quickSand = FontFamily(
        Font(Res.font.quicksand_semibold, weight = FontWeight.SemiBold),
        Font(Res.font.quicksand_bold, weight = FontWeight.Bold),
        Font(Res.font.quicksand_regular, weight = FontWeight.Normal),
        Font(Res.font.quicksand_light, weight = FontWeight.Light),
        Font(Res.font.quicksand_medium, weight = FontWeight.Medium)
    )

    return Typography(
        headlineLarge = TextStyle(
            fontFamily = quickSand,
            fontWeight = FontWeight.Bold,
            fontSize = 96.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = quickSand,
            fontWeight = FontWeight.SemiBold,
            fontSize = 60.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = quickSand,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = quickSand,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = quickSand,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp
        )
    )
}
