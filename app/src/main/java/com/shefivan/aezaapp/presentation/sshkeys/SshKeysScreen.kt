package com.shefivan.aezaapp.presentation.sshkeys

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val Background = Color(0xFFF4F4F4)
private val BorderColor = Color(0xFFE1E1E1)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val DangerColor = Color(0xFFD32F2F)
private val AccentGreen = Color(0xFF4CAF50)
private val CardShape = RoundedCornerShape(16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SshKeysScreen(
    onBack: () -> Unit = {},
    viewModel: SshKeysViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.showAddDialog) {
        AddSshKeyDialog(
            isCreating = uiState.isCreating,
            onDismiss = { viewModel.processCommand(SshKeysViewModel.Command.DismissAddDialog) },
            onConfirm = { name, key, autoAssign ->
                viewModel.processCommand(SshKeysViewModel.Command.ConfirmAdd(name, key, autoAssign))
            },
        )
    }

    uiState.editingKey?.let { key ->
        EditSshKeyDialog(
            key = key,
            isEditing = uiState.isEditing,
            onDismiss = { viewModel.processCommand(SshKeysViewModel.Command.DismissEditDialog) },
            onConfirm = { name, autoAssign ->
                viewModel.processCommand(SshKeysViewModel.Command.ConfirmEdit(name, autoAssign))
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        SshKeysTopBar(
            onBack = onBack,
            onAdd = { viewModel.processCommand(SshKeysViewModel.Command.OpenAddDialog) },
        )
        HorizontalDivider(color = BorderColor)

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextPrimary)
            }
            return@Column
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { if (!uiState.isRefreshing) viewModel.processCommand(SshKeysViewModel.Command.Refresh) },
            modifier = Modifier.fillMaxSize(),
        ) {
            if (uiState.keys.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет SSH-ключей", fontSize = 15.sp, color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.keys, key = { it.id }) { key ->
                        SshKeyCard(
                            item = key,
                            isDeleting = key.id in uiState.deletingIds,
                            isExpanded = key.id in uiState.expandedIds,
                            isLoadingKey = key.id in uiState.loadingKeyIds,
                            fullKey = uiState.fullKeys[key.id],
                            onEdit = { viewModel.processCommand(SshKeysViewModel.Command.OpenEditDialog(key)) },
                            onDelete = { viewModel.processCommand(SshKeysViewModel.Command.Delete(key.id)) },
                            onToggleExpand = { viewModel.processCommand(SshKeysViewModel.Command.ToggleExpand(key.id)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SshKeysTopBar(onBack: () -> Unit, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
        }
        Text(
            text = "SSH-ключи",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onAdd) {
            Icon(Icons.Outlined.Add, contentDescription = "Добавить", tint = TextPrimary)
        }
    }
}

@Composable
private fun SshKeyCard(
    item: SshKeysViewModel.SshKeyUiItem,
    isDeleting: Boolean,
    isExpanded: Boolean,
    isLoadingKey: Boolean,
    fullKey: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleExpand: () -> Unit,
) {
    val chevronAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "chevron")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                )
                if (isDeleting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = TextSecondary)
                } else {
                    Row {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Редактировать", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Удалить", tint = DangerColor, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
            HorizontalDivider(color = BorderColor)
            Text(
                text = item.publicKeyPreview,
                fontSize = 11.sp,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
            )
            HorizontalDivider(color = BorderColor)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Добавлен", fontSize = 12.sp, color = TextSecondary)
                Text(text = item.createdDate, fontSize = 12.sp, color = TextPrimary)
            }
            if (item.autoAssign) {
                HorizontalDivider(color = BorderColor)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Автоназначение", fontSize = 12.sp, color = TextSecondary)
                    Text(text = "включено", fontSize = 12.sp, color = AccentGreen, fontWeight = FontWeight.Medium)
                }
            }
        }

        HorizontalDivider(color = BorderColor)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Полный ключ", fontSize = 12.sp, color = TextSecondary)
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp).rotate(chevronAngle),
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                HorizontalDivider(color = BorderColor)
                if (isLoadingKey) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = TextPrimary)
                    }
                } else if (fullKey != null) {
                    Text(
                        text = fullKey,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun AddSshKeyDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, publicKey: String, autoAssign: Boolean) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var publicKey by remember { mutableStateOf("") }
    var autoAssign by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить SSH-ключ") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = publicKey,
                    onValueChange = { publicKey = it },
                    label = { Text("Публичный ключ") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Автоназначение", fontSize = 14.sp, color = TextPrimary)
                    Switch(
                        checked = autoAssign,
                        onCheckedChange = { autoAssign = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentGreen,
                        ),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && publicKey.isNotBlank()) onConfirm(name, publicKey.trim(), autoAssign)
                },
                enabled = !isCreating,
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = TextPrimary)
                } else {
                    Text("Добавить")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

@Composable
private fun EditSshKeyDialog(
    key: SshKeysViewModel.SshKeyUiItem,
    isEditing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, autoAssign: Boolean) -> Unit,
) {
    var name by remember(key.id) { mutableStateOf(key.name) }
    var autoAssign by remember(key.id) { mutableStateOf(key.autoAssign) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать SSH-ключ") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Автоназначение", fontSize = 14.sp, color = TextPrimary)
                    Switch(
                        checked = autoAssign,
                        onCheckedChange = { autoAssign = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentGreen,
                        ),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), autoAssign) },
                enabled = !isEditing,
            ) {
                if (isEditing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = TextPrimary)
                } else {
                    Text("Сохранить")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}
