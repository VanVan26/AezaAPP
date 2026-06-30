package com.shefivan.aezaapp.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val DialogShape = RoundedCornerShape(20.dp)
private val DialogTitleColor = Color(0xFF333333)

@Composable
fun AezaDialog(
    title: String,
    onDismiss: () -> Unit,
    confirmText: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    confirmLoading: Boolean = false,
    dismissText: String? = "Отмена",
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = DialogShape,
            color = Color.White,
            modifier = modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DialogTitleColor,
                )
                content()
                if (dismissText == null) {
                    AezaPrimaryButton(
                        text = confirmText,
                        onClick = onConfirm,
                        enabled = confirmEnabled,
                        loading = confirmLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    AezaButtonRow(
                        primaryText = confirmText,
                        onPrimaryClick = onConfirm,
                        primaryEnabled = confirmEnabled,
                        primaryLoading = confirmLoading,
                        secondaryText = dismissText,
                        onSecondaryClick = onDismiss,
                    )
                }
            }
        }
    }
}
