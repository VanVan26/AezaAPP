package com.shefivan.aezaapp.presentation.servicedetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shefivan.aezaapp.domain.model.ServiceBackupScheduleType
import com.shefivan.aezaapp.presentation.theme.NotoColorEmojiFamily

private val Background = Color(0xFFF5F5F5)
private val BorderColor = Color(0xFFF2F2F2)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val StatusActive = Color(0xFF4CAF50)
private val StatusInactive = Color(0xFFFF5722)
private val DangerColor = Color(0xFFD32F2F)
private val CardShape = RoundedCornerShape(12.dp)
private val TaskBackground = Color(0xFFFFF8E1)
private val TaskBorder = Color(0xFFFFE082)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    onBack: () -> Unit,
    viewModel: ServiceDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    var tabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val tabCount = uiState.availableTabs.size
    val safeIndex = selectedTabIndex.coerceAtMost((tabCount - 1).coerceAtLeast(0))

    // ── Dialogs ──────────────────────────────────────────────────────────────

    if (uiState.showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { viewModel.processCommand(ServiceDetailViewModel.Command.DismissChangePasswordDialog) },
            onConfirm = { viewModel.processCommand(ServiceDetailViewModel.Command.ConfirmChangePassword(it)) },
        )
    }
    if (uiState.showReinstallDialog) {
        ReinstallDialog(
            onDismiss = { viewModel.processCommand(ServiceDetailViewModel.Command.DismissReinstallDialog) },
            onConfirm = { os, recipe, password ->
                viewModel.processCommand(ServiceDetailViewModel.Command.ConfirmReinstall(os, recipe, password))
            },
        )
    }
    if (uiState.showDeleteConfirmDialog) {
        DeleteConfirmDialog(
            name = uiState.name,
            onDismiss = { viewModel.processCommand(ServiceDetailViewModel.Command.DismissDeleteConfirmDialog) },
            onConfirm = { viewModel.processCommand(ServiceDetailViewModel.Command.ConfirmDelete) },
        )
    }
    if (uiState.showCreateBackupDialog) {
        CreateBackupDialog(
            onDismiss = { viewModel.processCommand(ServiceDetailViewModel.Command.DismissCreateBackupDialog) },
            onConfirm = { viewModel.processCommand(ServiceDetailViewModel.Command.ConfirmCreateBackup(it)) },
        )
    }
    if (uiState.showScheduleDialog) {
        SetScheduleDialog(
            currentType = uiState.backupScheduleType,
            currentLimit = uiState.backupScheduleLimit,
            onDismiss = { viewModel.processCommand(ServiceDetailViewModel.Command.DismissScheduleDialog) },
            onConfirm = { type, limit, weekDay, monthDay ->
                viewModel.processCommand(ServiceDetailViewModel.Command.ConfirmSetSchedule(type, limit, weekDay, monthDay))
            },
            onDelete = {
                viewModel.processCommand(ServiceDetailViewModel.Command.DeleteSchedule)
                viewModel.processCommand(ServiceDetailViewModel.Command.DismissScheduleDialog)
            },
        )
    }
    if (uiState.editingPtrKey != null) {
        EditPtrDialog(
            currentDomain = uiState.editingPtrDomain,
            onDismiss = { viewModel.processCommand(ServiceDetailViewModel.Command.DismissEditPtrDialog) },
            onConfirm = { viewModel.processCommand(ServiceDetailViewModel.Command.ConfirmEditPtr(it)) },
        )
    }
    // ── Layout ───────────────────────────────────────────────────────────────

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        DetailTopBar(title = if (uiState.isLoading) "Услуга" else uiState.name, onBack = onBack)
        HorizontalDivider(color = BorderColor)

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextPrimary)
            }
            return@Column
        }

        val contentBottomPadding = if (tabCount > 1) 88.dp else 32.dp
        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState.availableTabs.getOrNull(safeIndex)) {
                ServiceDetailTab.INFO, null -> InfoTab(
                    uiState = uiState,
                    viewModel = viewModel,
                    bottomPadding = contentBottomPadding
                )

                ServiceDetailTab.HISTORY -> HistoryTab(
                    uiState = uiState,
                    viewModel = viewModel,
                    bottomPadding = contentBottomPadding,
                )
                ServiceDetailTab.NETWORK -> NetworkTab(
                    uiState = uiState,
                    viewModel = viewModel,
                    bottomPadding = contentBottomPadding
                )

                ServiceDetailTab.BACKUPS -> BackupsTab(
                    uiState = uiState,
                    viewModel = viewModel,
                    bottomPadding = contentBottomPadding
                )

                ServiceDetailTab.STATS -> StatsTab(
                    uiState = uiState,
                    viewModel = viewModel,
                    bottomPadding = contentBottomPadding,
                )

                ServiceDetailTab.VNC -> VncTab(
                    uiState = uiState,
                    viewModel = viewModel,
                )
            }

            if (tabCount > 1) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 24.dp, end = 8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .border(1.dp, BorderColor, RoundedCornerShape(100)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FilledTonalButton(
                            onClick = {},
                            shape = RoundedCornerShape(
                                topStartPercent = 50, bottomStartPercent = 50,
                                topEndPercent = 0, bottomEndPercent = 0,
                            ),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color.White,
                                contentColor = TextPrimary,
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            elevation = null,
                        ) {
                            Text(
                                text = uiState.availableTabs.getOrNull(safeIndex)?.label ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Box(modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(BorderColor))
                        Box {
                            FilledTonalButton(
                                onClick = { tabMenuExpanded = !tabMenuExpanded },
                                shape = RoundedCornerShape(
                                    topStartPercent = 0, bottomStartPercent = 0,
                                    topEndPercent = 50, bottomEndPercent = 50,
                                ),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.White,
                                    contentColor = TextPrimary,
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                elevation = null,
                            ) {
                                val rotation by animateFloatAsState(
                                    targetValue = if (tabMenuExpanded) 180f else 0f,
                                    label = "arrow",
                                )
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .graphicsLayer { rotationZ = rotation },
                                )
                            }
                            DropdownMenu(
                                expanded = tabMenuExpanded,
                                onDismissRequest = { tabMenuExpanded = false },
                            ) {
                                uiState.availableTabs.forEachIndexed { index, tab ->
                                    DropdownMenuItem(
                                        text = { Text(tab.label) },
                                        onClick = {
                                            selectedTabIndex = index
                                            tabMenuExpanded = false
                                            when (tab) {
                                                ServiceDetailTab.HISTORY -> viewModel.processCommand(ServiceDetailViewModel.Command.LoadHistoryIfNeeded)
                                                ServiceDetailTab.NETWORK -> viewModel.processCommand(ServiceDetailViewModel.Command.LoadNetworkIfNeeded)
                                                ServiceDetailTab.BACKUPS -> viewModel.processCommand(ServiceDetailViewModel.Command.LoadBackupsIfNeeded)
                                                ServiceDetailTab.STATS -> viewModel.processCommand(ServiceDetailViewModel.Command.LoadStatsIfNeeded)
                                                else -> Unit
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Info tab ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InfoTab(
    uiState: ServiceDetailViewModel.UiState,
    viewModel: ServiceDetailViewModel,
    bottomPadding: Dp = 32.dp,
) {
    val context = LocalContext.current

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { if (!uiState.isRefreshing) viewModel.processCommand(ServiceDetailViewModel.Command.Refresh) },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = bottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { ServiceHeaderCard(uiState) }
            item { CredentialsCard(uiState, context) }
            if (uiState.specs.isNotEmpty()) {
                item { SpecsCard(uiState.specs) }
            }
            uiState.currentTaskName?.let { taskName ->
                item {
                    CurrentTaskCard(
                        taskName = taskName,
                        taskStatus = uiState.currentTaskStatus ?: "",
                    )
                }
            }
            item { ActionsCard(uiState = uiState, viewModel = viewModel) }
        }
    }
}

@Composable
private fun ServiceHeaderCard(uiState: ServiceDetailViewModel.UiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = uiState.flag, fontSize = 36.sp, fontFamily = NotoColorEmojiFamily)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = uiState.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(text = uiState.typeLabel, fontSize = 13.sp, color = TextSecondary)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (uiState.isActive) StatusActive else StatusInactive),
            )
            Text(
                text = uiState.statusLabel,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (uiState.isActive) StatusActive else StatusInactive,
            )
        }
    }
}

@Composable
private fun CredentialsCard(uiState: ServiceDetailViewModel.UiState, context: Context) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (uiState.ip.isNotBlank()) {
            CopyableRow(
                label = "IP",
                value = uiState.ip,
                onCopy = { copyToClipboard(context, "IP", uiState.ip) })
        }
        if (uiState.login.isNotBlank()) {
            CopyableRow(
                label = "Логин",
                value = uiState.login,
                onCopy = { copyToClipboard(context, "Логин", uiState.login) })
        }
        if (uiState.password.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Пароль", fontSize = 12.sp, color = TextSecondary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (passwordVisible) uiState.password else "••••••••",
                        fontSize = 14.sp,
                        color = TextPrimary,
                    )
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                    IconButton(
                        onClick = { copyToClipboard(context, "Пароль", uiState.password) },
                        modifier = Modifier.size(28.dp),
                    ) {
                        Icon(
                            Icons.Outlined.ContentCopy,
                            contentDescription = "Скопировать пароль",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        InfoRow(label = "Статус", value = uiState.statusLabel)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Стоимость", fontSize = 12.sp, color = TextSecondary)
            Row {
                Text(text = uiState.price, fontSize = 14.sp, color = TextPrimary)
                if (uiState.priceTerm.isNotBlank()) {
                    Text(text = " ${uiState.priceTerm}", fontSize = 14.sp, color = TextSecondary)
                }
            }
        }
        InfoRow(label = "Действует до", value = uiState.expiresDate)
        InfoRow(label = "Создан", value = uiState.createdDate)
    }
}

@Composable
private fun SpecsCard(specs: List<Pair<String, String>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        specs.forEach { (key, value) ->
            InfoRow(label = key, value = value)
        }
    }
}

@Composable
private fun CurrentTaskCard(taskName: String, taskStatus: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, TaskBorder, CardShape)
            .background(TaskBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "Выполняется задача",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        InfoRow(label = taskName, value = taskStatus)
    }
}

@Composable
private fun ActionsCard(
    uiState: ServiceDetailViewModel.UiState,
    viewModel: ServiceDetailViewModel,
) {
    val busy = uiState.isActionInProgress

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Управление",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        // Power toggle
        if (uiState.canSuspend || uiState.canResume) {
            ActionToggleRow(
                label = uiState.statusLabel,
                checked = uiState.isActive,
                onCheckedChange = { on ->
                    if (on) viewModel.processCommand(ServiceDetailViewModel.Command.Resume)
                    else viewModel.processCommand(ServiceDetailViewModel.Command.Suspend)
                },
                enabled = !busy,
                labelColor = if (uiState.isActive) StatusActive else TextSecondary,
                trackColor = StatusActive,
            )
        }

        ActionToggleRow(
            label = "Автопродление",
            checked = uiState.autoProlong,
            onCheckedChange = null,
            trackColor = StatusActive,
        )

        if (uiState.canRescue) {
            ActionToggleRow(
                label = "Режим восстановления",
                checked = uiState.isInRescue,
                onCheckedChange = { on ->
                    if (on) viewModel.processCommand(ServiceDetailViewModel.Command.EnterRescueMode)
                    else viewModel.processCommand(ServiceDetailViewModel.Command.LeaveRescueMode)
                },
                enabled = !busy,
                trackColor = StatusInactive,
            )
        }

        HorizontalDivider(color = BorderColor)

        if (uiState.canRestart) {
            ActionListItem(
                label = "Перезагрузить",
                icon = Icons.Outlined.Refresh,
                enabled = !busy,
                onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.Restart) },
            )
        }

        if (uiState.canChangePassword) {
            ActionListItem(
                label = "Сменить пароль",
                icon = Icons.Outlined.Lock,
                enabled = !busy,
                onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.OpenChangePasswordDialog) },
            )
        }


        if (uiState.canReinstall || uiState.canDelete) {
            HorizontalDivider(color = BorderColor)
        }

        if (uiState.canReinstall) {
            ActionListItem(
                label = "Переустановить",
                icon = Icons.Outlined.Build,
                enabled = !busy,
                onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.OpenReinstallDialog) },
            )
        }

        if (uiState.canDelete) {
            ActionListItem(
                label = "Удалить",
                icon = Icons.Outlined.Delete,
                enabled = !busy,
                onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.OpenDeleteConfirmDialog) },
                isDestructive = true,
            )
        }

        if (busy) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = TextSecondary
                )
            }
        }
    }
}

// ── Backups tab ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackupsTab(
    uiState: ServiceDetailViewModel.UiState,
    viewModel: ServiceDetailViewModel,
    bottomPadding: Dp = 32.dp,
) {
    PullToRefreshBox(
        isRefreshing = uiState.isBackupsRefreshing,
        onRefresh = { if (!uiState.isBackupsRefreshing) viewModel.processCommand(ServiceDetailViewModel.Command.RefreshBackups) },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = bottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.OpenCreateBackupDialog) },
                        enabled = !uiState.isCreatingBackup,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextPrimary, contentColor = Color.White),
                    ) {
                        if (uiState.isCreatingBackup) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text("Создать бэкап", fontSize = 14.sp)
                        }
                    }
                    ActionListItem(
                        label = if (uiState.backupScheduleType != null) "Расписание ✓" else "Расписание",
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.OpenScheduleDialog) },
                    )
                }
            }

            if (uiState.isBackupsLoading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize().padding(bottom = 80.dp),
                        contentAlignment = Alignment.Center,
                    ) { CircularProgressIndicator(color = TextPrimary) }
                }
            } else if (uiState.backups.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("Нет бэкапов", fontSize = 15.sp, color = TextSecondary) }
                }
            } else {
                items(uiState.backups, key = { it.id }) { backup ->
                    BackupCard(
                        item = backup,
                        isDeleting = backup.id in uiState.deletingBackupIds,
                        isRestoring = backup.id == uiState.restoringBackupId,
                        onDelete = { viewModel.processCommand(ServiceDetailViewModel.Command.DeleteBackup(backup.id)) },
                        onRestore = { viewModel.processCommand(ServiceDetailViewModel.Command.RestoreBackup(backup.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun BackupCard(
    item: ServiceDetailViewModel.BackupUiItem,
    isDeleting: Boolean,
    isRestoring: Boolean,
    onDelete: () -> Unit,
    onRestore: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            when {
                isDeleting || isRestoring -> CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = TextSecondary,
                )
                item.isActive -> IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Удалить", tint = DangerColor, modifier = Modifier.size(18.dp))
                }
            }
        }
        HorizontalDivider(color = BorderColor)
        InfoRow(label = "Размер", value = item.sizeLabel)
        HorizontalDivider(color = BorderColor)
        InfoRow(label = "Создан", value = item.createdDate)
        HorizontalDivider(color = BorderColor)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Статус", fontSize = 13.sp, color = TextSecondary)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (item.isActive) StatusActive else TextSecondary),
                )
                Text(
                    text = item.statusLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (item.isActive) StatusActive else TextSecondary,
                )
            }
        }
        if (item.isActive) {
            HorizontalDivider(color = BorderColor)
            ActionListItem(
                label = "Восстановить",
                icon = Icons.Outlined.Refresh,
                enabled = !isDeleting && !isRestoring,
                onClick = onRestore,
            )
        }
    }
}

// ── History tab ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTab(
    uiState: ServiceDetailViewModel.UiState,
    viewModel: ServiceDetailViewModel,
    bottomPadding: Dp = 32.dp,
) {
    PullToRefreshBox(
        isRefreshing = uiState.isHistoryRefreshing,
        onRefresh = { viewModel.processCommand(ServiceDetailViewModel.Command.RefreshHistory) },
        modifier = Modifier.fillMaxSize(),
    ) {
        when {
            uiState.isHistoryLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator(color = TextPrimary) }

            uiState.tasks.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) { Text("Нет задач", fontSize = 15.sp, color = TextSecondary) }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.tasks, key = { it.id }) { task ->
                    TaskCard(task)
                }
            }
        }
    }
}

@Composable
private fun TaskCard(task: ServiceDetailViewModel.TaskUiItem) {
    val statusColor = when (task.statusColor) {
        1 -> StatusActive
        2 -> DangerColor
        3 -> Color(0xFF1976D2)
        else -> TextSecondary
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = task.name, fontSize = 14.sp, color = TextPrimary)
            Text(text = task.createdDate, fontSize = 12.sp, color = TextSecondary)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(text = task.statusLabel, fontSize = 12.sp, color = statusColor)
        }
    }
}

// ── Network tab ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkTab(
    uiState: ServiceDetailViewModel.UiState,
    viewModel: ServiceDetailViewModel,
    bottomPadding: Dp = 32.dp,
) {
    PullToRefreshBox(
        isRefreshing = uiState.isNetworkRefreshing,
        onRefresh = { if (!uiState.isNetworkRefreshing) viewModel.processCommand(ServiceDetailViewModel.Command.RefreshNetwork) },
        modifier = Modifier.fillMaxSize(),
    ) {
        if (uiState.isNetworkLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextPrimary)
            }
            return@PullToRefreshBox
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = bottomPadding
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (uiState.ipv4List.isNotEmpty()) {
                item {
                    IpSectionCard(title = "IPv4") {
                        uiState.ipv4List.forEachIndexed { i, ip ->
                            if (i > 0) HorizontalDivider(color = BorderColor)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                IpAddressBlock(
                                    rows = listOfNotNull(
                                        "Адрес" to ip.address,
                                        "Шлюз" to ip.gateway,
                                        "Маска" to ip.mask,
                                        if (ip.domain.isNotBlank()) "PTR" to ip.domain else "PTR" to "—",
                                    ),
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    ActionListItem(
                                        label = "Изменить PTR",
                                        onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.OpenEditPtrDialog(ip.key, ip.domain)) },
                                        modifier = Modifier.weight(1f),
                                    )
                                    if (ip.address != uiState.ip) {
                                        ActionListItem(
                                            label = "Основной",
                                            onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.MakeMainIp(ip.key)) },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (uiState.ipv6List.isNotEmpty()) {
                item {
                    IpSectionCard(title = "IPv6") {
                        uiState.ipv6List.forEachIndexed { i, ip ->
                            if (i > 0) HorizontalDivider(color = BorderColor)
                            IpAddressBlock(
                                rows = buildList {
                                    add("Адрес" to "${ip.address}/${ip.prefix}")
                                    add("Шлюз" to ip.gateway)
                                    ip.additionalIps.forEachIndexed { idx, a -> add("IP ${idx + 1}" to a) }
                                },
                            )
                        }
                    }
                }
            }
            if (uiState.ipv4List.isEmpty() && uiState.ipv6List.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center,
                    ) { Text("Нет данных", fontSize = 15.sp, color = TextSecondary) }
                }
            }
        }
    }
}

@Composable
private fun IpSectionCard(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )
        HorizontalDivider(color = BorderColor)
        content()
    }
}

@Composable
private fun IpAddressBlock(rows: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEachIndexed { i, (label, value) ->
            InfoRow(label = label, value = value)
            if (i < rows.lastIndex) HorizontalDivider(color = BorderColor)
        }
    }
}

// ── Stats tab ─────────────────────────────────────────────────────────────────

private val statTypes = listOf("cpu" to "CPU", "ram" to "RAM", "disk" to "Диск", "net" to "Сеть")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsTab(
    uiState: ServiceDetailViewModel.UiState,
    viewModel: ServiceDetailViewModel,
    bottomPadding: Dp = 32.dp,
) {
    PullToRefreshBox(
        isRefreshing = uiState.isStatsRefreshing,
        onRefresh = { viewModel.processCommand(ServiceDetailViewModel.Command.RefreshStats) },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = bottomPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    statTypes.forEach { (type, label) ->
                        val selected = uiState.statsType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (selected) TextPrimary else Background)
                                .clickable { viewModel.processCommand(ServiceDetailViewModel.Command.SelectStatType(type)) }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                color = if (selected) Color.White else TextPrimary,
                            )
                        }
                    }
                }
            }

            when {
                uiState.isStatsLoading -> item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), Alignment.Center) {
                        CircularProgressIndicator(color = TextSecondary)
                    }
                }
                uiState.statsData.isEmpty() -> item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), Alignment.Center) {
                        Text("Нет данных", fontSize = 14.sp, color = TextSecondary)
                    }
                }
                else -> items(uiState.statsData) { point ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CardShape)
                            .background(Color.White)
                            .border(1.dp, BorderColor, CardShape)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = point, fontSize = 13.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

// ── VNC tab ───────────────────────────────────────────────────────────────────

@Composable
private fun VncTab(
    uiState: ServiceDetailViewModel.UiState,
    viewModel: ServiceDetailViewModel,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CardShape)
                .border(1.dp, BorderColor, CardShape)
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Удалённый рабочий стол", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(
                "Получите временные учётные данные для подключения через VNC-клиент.",
                fontSize = 13.sp,
                color = TextSecondary,
            )
            if (uiState.isVncLoading) {
                Box(Modifier.fillMaxWidth(), Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = TextSecondary)
                }
            } else {
                ActionListItem(
                    label = "Получить сессию VNC",
                    icon = Icons.Outlined.Visibility,
                    onClick = { viewModel.processCommand(ServiceDetailViewModel.Command.ConnectVnc) },
                )
            }
        }
    }

    if (uiState.showVncDialog) {
        VncSessionDialog(
            address = uiState.vncAddress,
            password = uiState.vncPassword,
            onDismiss = { viewModel.processCommand(ServiceDetailViewModel.Command.DismissVncDialog) },
        )
    }
}

// ── Dialogs ───────────────────────────────────────────────────────────────────

@Composable
private fun EditPtrDialog(
    currentDomain: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var domain by remember { mutableStateOf(currentDomain) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Изменить PTR") },
        text = {
            OutlinedTextField(
                value = domain,
                onValueChange = { domain = it },
                label = { Text("Домен (PTR-запись)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { if (domain.isNotBlank()) onConfirm(domain) }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

@Composable
private fun SetScheduleDialog(
    currentType: String?,
    currentLimit: Int,
    onDismiss: () -> Unit,
    onConfirm: (ServiceBackupScheduleType, Int, Int?, Int?) -> Unit,
    onDelete: () -> Unit,
) {
    var selectedType by remember {
        mutableStateOf(
            when (currentType) {
                "weekly" -> ServiceBackupScheduleType.WEEKLY
                "monthly" -> ServiceBackupScheduleType.MONTHLY
                else -> ServiceBackupScheduleType.DAILY
            }
        )
    }
    var limit by remember { mutableStateOf(currentLimit.toString()) }
    var weekDay by remember { mutableStateOf("1") }
    var monthDay by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Расписание бэкапов") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Тип расписания", fontSize = 12.sp, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        ServiceBackupScheduleType.DAILY to "Ежедневно",
                        ServiceBackupScheduleType.WEEKLY to "Еженедельно",
                        ServiceBackupScheduleType.MONTHLY to "Ежемесячно",
                    ).forEach { (type, label) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedType == type) TextPrimary else Background)
                                .clickable { selectedType = type }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(label, fontSize = 12.sp, color = if (selectedType == type) Color.White else TextPrimary)
                        }
                    }
                }
                OutlinedTextField(
                    value = limit,
                    onValueChange = { limit = it.filter { c -> c.isDigit() } },
                    label = { Text("Хранить (шт.)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (selectedType == ServiceBackupScheduleType.WEEKLY) {
                    OutlinedTextField(
                        value = weekDay,
                        onValueChange = { weekDay = it.filter { c -> c.isDigit() } },
                        label = { Text("День недели (1=Пн … 7=Вс)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (selectedType == ServiceBackupScheduleType.MONTHLY) {
                    OutlinedTextField(
                        value = monthDay,
                        onValueChange = { monthDay = it.filter { c -> c.isDigit() } },
                        label = { Text("День месяца (1–31)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (currentType != null) {
                    TextButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                        Text("Отключить расписание", color = DangerColor, fontSize = 13.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    selectedType,
                    limit.toIntOrNull() ?: 3,
                    if (selectedType == ServiceBackupScheduleType.WEEKLY) weekDay.toIntOrNull() else null,
                    if (selectedType == ServiceBackupScheduleType.MONTHLY) monthDay.toIntOrNull() else null,
                )
            }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

@Composable
private fun VncSessionDialog(address: String, password: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("VNC сессия") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CopyableRow(label = "Адрес", value = address) {
                    copyToClipboard(context, "Адрес VNC", address)
                }
                HorizontalDivider(color = BorderColor)
                CopyableRow(label = "Пароль", value = password) {
                    copyToClipboard(context, "Пароль VNC", password)
                }
                Text(
                    "Используйте VNC-клиент для подключения. Сессия действительна ограниченное время.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Закрыть") } },
    )
}

@Composable
private fun ChangePasswordDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var password by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Сменить пароль") },
        text = {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Новый пароль") },
                singleLine = true,
                visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { visible = !visible }) {
                        Icon(
                            if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { if (password.isNotBlank()) onConfirm(password) }) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

@Composable
private fun ReinstallDialog(
    onDismiss: () -> Unit,
    onConfirm: (os: String, recipe: String, password: String) -> Unit
) {
    var os by remember { mutableStateOf("") }
    var recipe by remember { mutableStateOf("default") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Переустановить систему") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = os,
                    onValueChange = { os = it },
                    label = { Text("Образ ОС (slug)") },
                    placeholder = { Text("ubuntu-24.04") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = recipe,
                    onValueChange = { recipe = it },
                    label = { Text("Рецепт") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль root") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (os.isNotBlank() && password.isNotBlank()) onConfirm(
                    os,
                    recipe,
                    password
                )
            }) {
                Text("Переустановить", color = DangerColor)
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

@Composable
private fun DeleteConfirmDialog(name: String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить услугу?") },
        text = { Text("Вы собираетесь запросить удаление «$name». Это действие необратимо.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Удалить", color = DangerColor) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

@Composable
private fun CreateBackupDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Создать бэкап") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name) }) { Text("Создать") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}

// ── Shared components ─────────────────────────────────────────────────────────

@Composable
private fun DetailTopBar(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Назад",
                tint = TextPrimary
            )
        }
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
    }
}

@Composable
private fun ActionListItem(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    val textColor = when {
        !enabled -> TextSecondary
        isDestructive -> DangerColor
        else -> TextPrimary
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Background)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 14.sp, color = textColor)
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun ActionToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    enabled: Boolean = true,
    labelColor: Color = TextPrimary,
    trackColor: Color = StatusActive,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 14.sp, color = labelColor)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = trackColor
            ),
        )
    }
}

@Composable
private fun CopyableRow(label: String, value: String, onCopy: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, fontSize = 14.sp, color = TextPrimary)
            Spacer(Modifier.width(2.dp))
            IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                Icon(
                    Icons.Outlined.ContentCopy,
                    contentDescription = "Скопировать",
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 14.sp, color = TextPrimary)
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
}
