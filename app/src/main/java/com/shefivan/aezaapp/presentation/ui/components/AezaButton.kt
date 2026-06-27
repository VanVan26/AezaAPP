package com.shefivan.aezaapp.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ButtonShape = RoundedCornerShape(12.dp)
private val ButtonPadding = PaddingValues(16.dp)

@Composable
fun AezaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF666666),
            disabledContentColor = Color(0xFFAAAAAA),
        ),
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium, lineHeight = 24.sp)
    }
}

@Composable
fun AezaSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF5F5F5),
            contentColor = Color(0xFF333333),
            disabledContainerColor = Color(0xFFE0E0E0),
            disabledContentColor = Color(0xFF999999),
        ),
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium, lineHeight = 24.sp)
    }
}

@Composable
fun AezaButtonRow(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    secondaryText: String,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        AezaSecondaryButton(
            text = secondaryText,
            onClick = onSecondaryClick,
            enabled = secondaryEnabled,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        AezaPrimaryButton(
            text = primaryText,
            onClick = onPrimaryClick,
            enabled = primaryEnabled,
            modifier = Modifier.weight(1f),
        )
    }
}
