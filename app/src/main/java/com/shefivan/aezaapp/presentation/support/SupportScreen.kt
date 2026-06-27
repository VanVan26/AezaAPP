package com.shefivan.aezaapp.presentation.support

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Archive
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val Background = Color(0xFFF4F4F4)
private val BorderColor = Color(0xFFE1E1E1)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)
private val CardShape = RoundedCornerShape(16.dp)
private val BubbleUser = Color(0xFF333333)
private val BubbleSupport = Color.White
private val UnreadBadge = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBack: () -> Unit = {},
    viewModel: SupportViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val inChat = uiState.selectedTicket != null

    BackHandler(enabled = inChat) {
        viewModel.processCommand(SupportViewModel.Command.BackToList)
    }

    if (uiState.showCreateDialog) {
        CreateTicketDialog(
            isCreating = uiState.isCreating,
            onDismiss = { viewModel.processCommand(SupportViewModel.Command.DismissCreateDialog) },
            onConfirm = { name, body -> viewModel.processCommand(SupportViewModel.Command.ConfirmCreate(name, body)) },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        SupportTopBar(
            title = uiState.selectedTicket?.name ?: "Поддержка",
            inChat = inChat,
            showUnread = !inChat && uiState.totalUnread > 0,
            unreadCount = uiState.totalUnread,
            onBack = if (inChat) {
                { viewModel.processCommand(SupportViewModel.Command.BackToList) }
            } else {
                onBack
            },
            onAdd = if (!inChat) {
                { viewModel.processCommand(SupportViewModel.Command.OpenCreateDialog) }
            } else null,
            onArchive = if (inChat) {
                { uiState.selectedTicket?.id?.let { viewModel.processCommand(SupportViewModel.Command.ArchiveTicket(it)) } }
            } else null,
        )
        HorizontalDivider(color = BorderColor)

        if (!inChat) {
            TicketsList(uiState = uiState, viewModel = viewModel)
        } else {
            ChatView(uiState = uiState, viewModel = viewModel)
        }
    }
}

@Composable
private fun SupportTopBar(
    title: String,
    inChat: Boolean,
    showUnread: Boolean,
    unreadCount: Int,
    onBack: () -> Unit,
    onAdd: (() -> Unit)?,
    onArchive: (() -> Unit)?,
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
        if (showUnread) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(UnreadBadge),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "$unreadCount", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        if (onAdd != null) {
            IconButton(onClick = onAdd) {
                Icon(Icons.Outlined.Add, contentDescription = "Создать тикет", tint = TextPrimary)
            }
        }
        if (onArchive != null) {
            IconButton(onClick = { onArchive() }) {
                Icon(Icons.Outlined.Archive, contentDescription = "Архивировать", tint = TextSecondary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketsList(uiState: SupportViewModel.UiState, viewModel: SupportViewModel) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = TextPrimary)
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = { if (!uiState.isRefreshing) viewModel.processCommand(SupportViewModel.Command.Refresh) },
        modifier = Modifier.fillMaxSize(),
    ) {
        val allTickets = uiState.openTickets + uiState.closedTickets
        if (allTickets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет обращений", fontSize = 15.sp, color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (uiState.openTickets.isNotEmpty()) {
                    item {
                        Text("Открытые", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
                    }
                    items(uiState.openTickets, key = { it.id }) { ticket ->
                        TicketCard(
                            item = ticket,
                            onClick = { viewModel.processCommand(SupportViewModel.Command.SelectTicket(ticket)) },
                        )
                    }
                }
                if (uiState.closedTickets.isNotEmpty()) {
                    item {
                        Text(
                            "Закрытые",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                    }
                    items(uiState.closedTickets, key = { it.id }) { ticket ->
                        TicketCard(
                            item = ticket,
                            onClick = { viewModel.processCommand(SupportViewModel.Command.SelectTicket(ticket)) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketCard(item: SupportViewModel.TicketUiItem, onClick: () -> Unit) {
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
        Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            modifier = Modifier.weight(1f),
        )
        if (item.unreadCount > 0) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(UnreadBadge),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "${item.unreadCount}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ChatView(uiState: SupportViewModel.UiState, viewModel: SupportViewModel) {
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        if (uiState.isMessagesLoading) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextPrimary)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = true,
            ) {
                items(uiState.messages, key = { it.id }) { message ->
                    MessageBubble(item = message)
                }
            }
        }

        HorizontalDivider(color = BorderColor)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = uiState.messageInput,
                onValueChange = { viewModel.processCommand(SupportViewModel.Command.MessageInputChanged(it)) },
                placeholder = { Text("Сообщение…", fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
            )
            IconButton(
                onClick = { viewModel.processCommand(SupportViewModel.Command.SendMessage) },
                enabled = uiState.messageInput.isNotBlank() && !uiState.isSending,
            ) {
                if (uiState.isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = TextPrimary)
                } else {
                    Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Отправить", tint = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(item: SupportViewModel.MessageUiItem) {
    val alignment = if (item.isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (item.isUser) BubbleUser else BubbleSupport
    val textColor = if (item.isUser) Color.White else TextPrimary
    val bubbleShape = if (item.isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        if (!item.isUser) {
            Text(text = item.authorName, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
        }
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleColor)
                .border(if (!item.isUser) 1.dp else 0.dp, BorderColor, bubbleShape)
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(text = item.body, fontSize = 14.sp, color = textColor, lineHeight = 20.sp)
            Text(
                text = item.date,
                fontSize = 11.sp,
                color = if (item.isUser) Color.White.copy(alpha = 0.6f) else TextSecondary,
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun CreateTicketDialog(
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (name: String, body: String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новое обращение") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Тема") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Описание") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank() && body.isNotBlank()) onConfirm(name, body) },
                enabled = !isCreating,
            ) {
                if (isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = TextPrimary)
                } else {
                    Text("Отправить")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}
