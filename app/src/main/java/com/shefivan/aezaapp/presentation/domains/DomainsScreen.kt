package com.shefivan.aezaapp.presentation.domains

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
            onDismiss = { viewModel.processCommand(DomainsViewModel.Command.DismissAddRecordDialog) },
            onConfirm = { type, name, content, ttl ->
                viewModel.processCommand(DomainsViewModel.Command.ConfirmAddRecord(type, name, content, ttl))
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
                        onClick = { viewModel.processCommand(DomainsViewModel.Command.SelectDomain(domain)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DomainCard(item: DomainsViewModel.DomainUiItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .border(1.dp, BorderColor, CardShape)
            .background(Color.White)
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
            val isActive = item.status == "active"
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
        if (uiState.records.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет DNS-записей", fontSize = 15.sp, color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.records, key = { it.id }) { record ->
                    RecordCard(
                        item = record,
                        isDeleting = record.id in uiState.deletingRecordIds,
                        onDelete = { viewModel.processCommand(DomainsViewModel.Command.DeleteRecord(record.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordCard(
    item: DomainsViewModel.RecordUiItem,
    isDeleting: Boolean,
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TextPrimary)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(text = item.type, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Text(text = item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.weight(1f))
            }
            if (isDeleting) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = TextSecondary)
            } else {
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Удалить", tint = DangerColor, modifier = Modifier.size(18.dp))
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить домен") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя домена") },
                placeholder = { Text("example.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }, enabled = !isCreating) {
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
private fun AddRecordDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (type: String, name: String, content: String, ttl: Int) -> Unit,
) {
    var type by remember { mutableStateOf("A") }
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var ttl by remember { mutableStateOf("3600") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить DNS-запись") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it.uppercase() },
                    label = { Text("Тип (A, AAAA, CNAME, MX…)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Значение") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = ttl,
                    onValueChange = { ttl = it.filter { c -> c.isDigit() } },
                    label = { Text("TTL (сек.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (type.isNotBlank() && name.isNotBlank() && content.isNotBlank()) {
                        onConfirm(type, name, content, ttl.toIntOrNull() ?: 3600)
                    }
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
