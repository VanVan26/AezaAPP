package com.shefivan.aezaapp.presentation.domains

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.shefivan.aezaapp.presentation.ui.components.AezaDialog
import com.shefivan.aezaapp.presentation.ui.components.AezaTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
fun DomainsScreen(
    onBack: () -> Unit = {},
    viewModel: DomainsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val inDetail = uiState.selectedDomain != null

    BackHandler(enabled = inDetail) {
        viewModel.processCommand(DomainsViewModel.Command.BackToList)
    }

    if (uiState.showAddDomainDialog) {
        AddDomainDialog(
            isCreating = uiState.isCreatingDomain,
            onDismiss = { viewModel.processCommand(DomainsViewModel.Command.DismissAddDomainDialog) },
            onConfirm = { viewModel.processCommand(DomainsViewModel.Command.ConfirmAddDomain(it)) },
        )
    }

    if (uiState.showAddRecordDialog) {
        AddRecordDialog(
            isCreating = uiState.isCreatingRecord,
            recordTypes = uiState.recordTypes,
            onDismiss = { viewModel.processCommand(DomainsViewModel.Command.DismissAddRecordDialog) },
            onConfirm = { type, name, content, ttl ->
                viewModel.processCommand(DomainsViewModel.Command.ConfirmAddRecord(type, name, content, ttl))
            },
        )
    }

    uiState.editingRecord?.let { record ->
        EditRecordDialog(
            record = record,
            isEditing = uiState.isEditingRecord,
            onDismiss = { viewModel.processCommand(DomainsViewModel.Command.DismissEditRecordDialog) },
            onConfirm = { content, ttl, isEnabled ->
                viewModel.processCommand(DomainsViewModel.Command.ConfirmEditRecord(content, ttl, isEnabled))
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        DomainsTopBar(
            title = uiState.selectedDomain?.name ?: "Домены",
            inDetail = inDetail,
            onBack = if (inDetail) {
                { viewModel.processCommand(DomainsViewModel.Command.BackToList) }
            } else {
                onBack
            },
            onAdd = if (inDetail) {
                { viewModel.processCommand(DomainsViewModel.Command.OpenAddRecordDialog) }
            } else {
                { viewModel.processCommand(DomainsViewModel.Command.OpenAddDomainDialog) }
            },
        )
        HorizontalDivider(color = BorderColor)

        if (!inDetail) {
            DomainsList(uiState = uiState, viewModel = viewModel)
        } else {
            RecordsList(uiState = uiState, viewModel = viewModel)
        }
    }
}

@Composable
private fun DomainsTopBar(
    title: String,
    inDetail: Boolean,
    onBack: () -> Unit,
    onAdd: () -> Unit,
) {
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
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        IconButton(onClick = onAdd) {
            Icon(Icons.Outlined.Add, contentDescription = "Добавить", tint = TextPrimary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DomainsList(uiState: DomainsViewModel.UiState, viewModel: DomainsViewModel) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TextPrimary)
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { if (!uiState.isRefreshing) viewModel.processCommand(DomainsViewModel.Command.Refresh) },
        modifier = Modifier.fillMaxSize(),
    ) {
        if (uiState.domains.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет доменов", fontSize = 15.sp, color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(uiState.domains, key = { it.id }) { domain ->
                    DomainCard(
                        item = domain,
                        isExpanded = domain.id in uiState.expandedDomainIds,
                        isLoadingDetail = domain.id in uiState.loadingDomainDetailIds,
                        detail = uiState.domainDetails[domain.id],
                        onClick = { viewModel.processCommand(DomainsViewModel.Command.SelectDomain(domain)) },
                        onToggleExpand = { viewModel.processCommand(DomainsViewModel.Command.ToggleDomainExpand(domain.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DomainCard(
    item: DomainsViewModel.DomainUiItem,
    isExpanded: Boolean,
    isLoadingDetail: Boolean,
    detail: DomainsViewModel.DomainDetailUiItem?,
    onClick: () -> Unit,
    onToggleExpand: () -> Unit,
) {
    val chevronAngle by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "chevron")
    val isActive = item.status == "active"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = item.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(text = item.createdDate, fontSize = 12.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isActive) AccentGreen else TextSecondary),
                )
                Text(
                    text = item.status,
                    fontSize = 12.sp,
                    color = if (isActive) AccentGreen else TextSecondary,
                    fontWeight = FontWeight.Medium,
                )
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
            Text(text = "Подробнее", fontSize = 12.sp, color = TextSecondary)
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
                if (isLoadingDetail) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = TextPrimary)
                    }
                } else if (detail != null) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        if (detail.statusReason != null) {
                            DomainDetailRow(label = "Причина статуса", value = detail.statusReason)
                            HorizontalDivider(color = BorderColor)
                        }
                        if (detail.observedNameservers.isNotEmpty()) {
                            detail.observedNameservers.forEachIndexed { index, ns ->
                                DomainDetailRow(label = "Текущий NS ${index + 1}", value = ns, monospace = true)
                                if (index < detail.observedNameservers.lastIndex) HorizontalDivider(color = BorderColor)
                            }
                            HorizontalDivider(color = BorderColor)
                        }
                        if (detail.nsCheckedAt != null) {
                            DomainDetailRow(label = "NS проверен", value = detail.nsCheckedAt)
                            HorizontalDivider(color = BorderColor)
                        }
                        DomainDetailRow(label = "Обновлён", value = detail.updatedDate)
                    }
                }
            }
        }
    }
}

@Composable
private fun DomainDetailRow(label: String, value: String, monospace: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextPrimary,
            fontFamily = if (monospace) FontFamily.Monospace else null,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordsList(uiState: DomainsViewModel.UiState, viewModel: DomainsViewModel) {
    if (uiState.isRecordsLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TextPrimary)
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRecordsRefreshing,
        onRefresh = { if (!uiState.isRecordsRefreshing) viewModel.processCommand(DomainsViewModel.Command.RefreshRecords) },
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (uiState.expectedNameservers.isNotEmpty()) {
                item {
                    NameserversCard(nameservers = uiState.expectedNameservers)
                }
            }
            if (uiState.records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("Нет DNS-записей", fontSize = 15.sp, color = TextSecondary)
                    }
                }
            } else {
                items(uiState.records, key = { it.id }) { record ->
                    RecordCard(
                        item = record,
                        isDeleting = record.id in uiState.deletingRecordIds,
                        onEdit = { viewModel.processCommand(DomainsViewModel.Command.OpenEditRecordDialog(record)) },
                        onDelete = { viewModel.processCommand(DomainsViewModel.Command.DeleteRecord(record.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NameserversCard(nameservers: List<String>) {
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
            text = "Ожидаемые NS-серверы",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
        )
        HorizontalDivider(color = BorderColor)
        nameservers.forEachIndexed { index, ns ->
            if (index > 0) HorizontalDivider(color = BorderColor)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "NS ${index + 1}", fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = ns,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Composable
private fun RecordCard(
    item: DomainsViewModel.RecordUiItem,
    isDeleting: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TextPrimary)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(text = item.type, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
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
        Text(text = item.content, fontSize = 12.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
        HorizontalDivider(color = BorderColor)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "TTL", fontSize = 12.sp, color = TextSecondary)
            Text(text = "${item.ttl}", fontSize = 12.sp, color = TextPrimary)
        }
    }
}

@Composable
private fun AddDomainDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    AezaDialog(
        title = "Добавить домен",
        onDismiss = onDismiss,
        confirmText = "Добавить",
        confirmEnabled = name.isNotBlank(),
        confirmLoading = isCreating,
        onConfirm = { onConfirm(name.trim()) },
    ) {
        AezaTextField(
            value = name,
            onValueChange = { name = it },
            label = "Имя домена",
            placeholder = "example.com",
        )
    }
}

@Composable
private fun AddRecordDialog(
    isCreating: Boolean,
    recordTypes: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (type: String, name: String, content: String, ttl: Int) -> Unit,
) {
    var type by remember { mutableStateOf(recordTypes.firstOrNull() ?: "A") }
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var ttl by remember { mutableStateOf("3600") }

    AezaDialog(
        title = "Добавить DNS-запись",
        onDismiss = onDismiss,
        confirmText = "Добавить",
        confirmEnabled = type.isNotBlank() && name.isNotBlank() && content.isNotBlank(),
        confirmLoading = isCreating,
        onConfirm = { onConfirm(type, name, content, ttl.toIntOrNull() ?: 3600) },
    ) {
        AezaTextField(
            value = type,
            onValueChange = { type = it.uppercase() },
            label = "Тип (A, AAAA, CNAME, MX…)",
        )
        AezaTextField(
            value = name,
            onValueChange = { name = it },
            label = "Имя",
        )
        AezaTextField(
            value = content,
            onValueChange = { content = it },
            label = "Значение",
        )
        AezaTextField(
            value = ttl,
            onValueChange = { ttl = it.filter { c -> c.isDigit() } },
            label = "TTL (сек.)",
        )
    }
}

@Composable
private fun EditRecordDialog(
    record: DomainsViewModel.RecordUiItem,
    isEditing: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (content: String, ttl: Int, isEnabled: Boolean) -> Unit,
) {
    var content by remember(record.id) { mutableStateOf(record.content) }
    var ttl by remember(record.id) { mutableStateOf(record.ttl.toString()) }
    var isEnabled by remember(record.id) { mutableStateOf(record.isEnabled) }

    AezaDialog(
        title = "Редактировать ${record.type}-запись",
        onDismiss = onDismiss,
        confirmText = "Сохранить",
        confirmEnabled = content.isNotBlank(),
        confirmLoading = isEditing,
        onConfirm = { onConfirm(content.trim(), ttl.toIntOrNull() ?: 3600, isEnabled) },
    ) {
        AezaTextField(
            value = content,
            onValueChange = { content = it },
            label = "Значение",
        )
        AezaTextField(
            value = ttl,
            onValueChange = { ttl = it.filter { c -> c.isDigit() } },
            label = "TTL (сек.)",
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Активна", fontSize = 14.sp, color = TextPrimary)
            Switch(
                checked = isEnabled,
                onCheckedChange = { isEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AccentGreen,
                ),
            )
        }
    }
}
