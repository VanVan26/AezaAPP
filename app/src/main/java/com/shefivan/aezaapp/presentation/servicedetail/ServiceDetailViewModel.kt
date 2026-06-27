package com.shefivan.aezaapp.presentation.servicedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.shefivan.aezaapp.domain.model.Ipv4Address
import com.shefivan.aezaapp.domain.model.Ipv6Address
import com.shefivan.aezaapp.domain.model.ReinstallServiceRequest
import com.shefivan.aezaapp.domain.model.Service
import com.shefivan.aezaapp.domain.model.ServiceBackup
import com.shefivan.aezaapp.domain.model.ServiceBackupSchedule
import com.shefivan.aezaapp.domain.model.ServiceBackupScheduleType
import com.shefivan.aezaapp.domain.model.ServiceBackupStatus
import com.shefivan.aezaapp.domain.model.ServiceCapability
import com.shefivan.aezaapp.domain.model.ServiceStatsRequest
import com.shefivan.aezaapp.domain.model.ServiceStatus
import com.shefivan.aezaapp.domain.model.ServiceTask
import com.shefivan.aezaapp.domain.model.ServiceTaskStatus
import com.shefivan.aezaapp.domain.model.ServiceTerm
import com.shefivan.aezaapp.domain.usecase.account.GetAccountUseCase
import com.shefivan.aezaapp.domain.usecase.service.ChangeServicePasswordUseCase
import com.shefivan.aezaapp.domain.usecase.service.ConnectRemoteVncUseCase
import com.shefivan.aezaapp.domain.usecase.service.EnterRescueModeUseCase
import com.shefivan.aezaapp.domain.model.ServiceTransaction
import com.shefivan.aezaapp.domain.usecase.service.GetServiceStatsUseCase
import com.shefivan.aezaapp.domain.usecase.service.GetServiceTransactionsUseCase
import com.shefivan.aezaapp.domain.usecase.service.GetServiceUseCase
import com.shefivan.aezaapp.domain.usecase.service.LeaveRescueModeUseCase
import com.shefivan.aezaapp.domain.usecase.service.ReinstallServiceUseCase
import com.shefivan.aezaapp.domain.usecase.service.RequestServiceDeletionUseCase
import com.shefivan.aezaapp.domain.usecase.service.RestartServiceUseCase
import com.shefivan.aezaapp.domain.usecase.service.ResumeServiceUseCase
import com.shefivan.aezaapp.domain.usecase.service.SuspendServiceUseCase
import com.shefivan.aezaapp.domain.usecase.servicebackup.CreateServiceBackupUseCase
import com.shefivan.aezaapp.domain.usecase.servicebackup.DeleteServiceBackupScheduleUseCase
import com.shefivan.aezaapp.domain.usecase.servicebackup.DeleteServiceBackupUseCase
import com.shefivan.aezaapp.domain.usecase.servicebackup.GetServiceBackupsUseCase
import com.shefivan.aezaapp.domain.usecase.servicebackup.RestoreServiceBackupUseCase
import com.shefivan.aezaapp.domain.usecase.servicebackup.SetServiceBackupScheduleUseCase
import com.shefivan.aezaapp.domain.usecase.servicenetwork.EditIpv4PtrUseCase
import com.shefivan.aezaapp.domain.usecase.servicenetwork.GetIpv4AddressesUseCase
import com.shefivan.aezaapp.domain.usecase.servicenetwork.GetIpv6AddressesUseCase
import com.shefivan.aezaapp.domain.usecase.servicenetwork.MakeMainIpv4UseCase
import com.shefivan.aezaapp.presentation.navigation.Screen
import com.shefivan.aezaapp.presentation.services.ServicesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject


enum class ServiceDetailTab(val label: String) {
    INFO("Информация"),
    HISTORY("История"),
    NETWORK("IP-Адреса"),
    BACKUPS("Бэкапы"),
    STATS("Статистика"),
    VNC("VNC"),
}

@HiltViewModel
class ServiceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getService: GetServiceUseCase,
    private val getAccount: GetAccountUseCase,
    private val restartService: RestartServiceUseCase,
    private val suspendService: SuspendServiceUseCase,
    private val resumeService: ResumeServiceUseCase,
    private val changePasswordUseCase: ChangeServicePasswordUseCase,
    private val enterRescueUseCase: EnterRescueModeUseCase,
    private val leaveRescueUseCase: LeaveRescueModeUseCase,
    private val reinstallUseCase: ReinstallServiceUseCase,
    private val requestDeletionUseCase: RequestServiceDeletionUseCase,
    private val getIpv4UseCase: GetIpv4AddressesUseCase,
    private val getIpv6UseCase: GetIpv6AddressesUseCase,
    private val editPtrUseCase: EditIpv4PtrUseCase,
    private val makeMainIpUseCase: MakeMainIpv4UseCase,
    private val getBackupsUseCase: GetServiceBackupsUseCase,
    private val createBackupUseCase: CreateServiceBackupUseCase,
    private val deleteBackupUseCase: DeleteServiceBackupUseCase,
    private val restoreBackupUseCase: RestoreServiceBackupUseCase,
    private val setScheduleUseCase: SetServiceBackupScheduleUseCase,
    private val deleteScheduleUseCase: DeleteServiceBackupScheduleUseCase,
    private val connectVncUseCase: ConnectRemoteVncUseCase,
    private val getStatsUseCase: GetServiceStatsUseCase,
    private val getTransactionsUseCase: GetServiceTransactionsUseCase,
) : ViewModel() {

    private var currencySymbol: String = "€"

    private val serviceId: Long = savedStateHandle.toRoute<Screen.ServiceDetail>().serviceId

    data class Ipv4UiItem(
        val key: String,
        val address: String,
        val gateway: String,
        val mask: String,
        val domain: String,
    )

    data class Ipv6UiItem(
        val key: String,
        val address: String,
        val prefix: Int,
        val gateway: String,
        val additionalIps: List<String>,
    )

    data class TaskUiItem(
        val id: String,
        val name: String,
        val createdDate: String,
        val statusLabel: String,
        val statusColor: Int,
    )

    data class BackupUiItem(
        val id: Long,
        val name: String,
        val sizeLabel: String,
        val createdDate: String,
        val statusLabel: String,
        val isActive: Boolean,
    )

    data class UiState(
        // Service
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val isActionInProgress: Boolean = false,

        // Информация
        val name: String = "",
        val flag: String = "",
        val typeLabel: String = "",
        val planName: String = "",
        val ip: String = "",
        val login: String = "",
        val password: String = "",
        val price: String = "",
        val priceTerm: String = "",
        val statusLabel: String = "",
        val isActive: Boolean = false,
        val isInRescue: Boolean = false,
        val expiresDate: String = "",
        val createdDate: String = "",
        val autoProlong: Boolean = false,
        val specs: List<Pair<String, String>> = emptyList(),
        val currentTaskName: String? = null,
        val currentTaskStatus: String? = null,

        // Capabilities / available tabs
        val availableTabs: List<ServiceDetailTab> = listOf(ServiceDetailTab.INFO),
        val canRestart: Boolean = false,
        val canSuspend: Boolean = false,
        val canResume: Boolean = false,
        val canChangePassword: Boolean = false,
        val canRescue: Boolean = false,
        val canReinstall: Boolean = false,
        val canDelete: Boolean = false,
        val canBackups: Boolean = false,
        val canCharts: Boolean = false,

        // History
        val isHistoryLoading: Boolean = false,
        val isHistoryRefreshing: Boolean = false,
        val tasks: List<TaskUiItem> = emptyList(),

        // VNC
        val canVnc: Boolean = false,
        val isVncLoading: Boolean = false,
        val showVncDialog: Boolean = false,
        val vncAddress: String = "",
        val vncPassword: String = "",

        // Stats
        val isStatsLoading: Boolean = false,
        val isStatsRefreshing: Boolean = false,
        val statsData: List<String> = emptyList(),
        val statsType: String = "cpu",

        // Network / IP
        val isNetworkLoading: Boolean = false,
        val isNetworkRefreshing: Boolean = false,
        val ipv4List: List<Ipv4UiItem> = emptyList(),
        val ipv6List: List<Ipv6UiItem> = emptyList(),
        val editingPtrKey: String? = null,
        val editingPtrDomain: String = "",

        // Backups
        val isBackupsLoading: Boolean = false,
        val isBackupsRefreshing: Boolean = false,
        val backups: List<BackupUiItem> = emptyList(),
        val isCreatingBackup: Boolean = false,
        val deletingBackupIds: Set<Long> = emptySet(),
        val restoringBackupId: Long? = null,
        val backupScheduleType: String? = null,
        val backupScheduleLimit: Int = 3,
        val backupScheduleWeekDay: Int? = null,
        val backupScheduleMonthDay: Int? = null,

        // Dialogs
        val showChangePasswordDialog: Boolean = false,
        val showReinstallDialog: Boolean = false,
        val showDeleteConfirmDialog: Boolean = false,
        val showCreateBackupDialog: Boolean = false,
        val showScheduleDialog: Boolean = false,
    )

    sealed interface Command {
        // General
        data object Refresh : Command
        // Power
        data object Restart : Command
        data object Suspend : Command
        data object Resume : Command
        // Rescue
        data object EnterRescueMode : Command
        data object LeaveRescueMode : Command
        // Change password
        data object OpenChangePasswordDialog : Command
        data object DismissChangePasswordDialog : Command
        data class ConfirmChangePassword(val newPassword: String) : Command
        // Reinstall
        data object OpenReinstallDialog : Command
        data object DismissReinstallDialog : Command
        data class ConfirmReinstall(val os: String, val recipe: String, val password: String) : Command
        // Delete
        data object OpenDeleteConfirmDialog : Command
        data object DismissDeleteConfirmDialog : Command
        data object ConfirmDelete : Command
        // Network
        data object LoadNetworkIfNeeded : Command
        data object RefreshNetwork : Command
        data class OpenEditPtrDialog(val key: String, val currentDomain: String) : Command
        data object DismissEditPtrDialog : Command
        data class ConfirmEditPtr(val domain: String) : Command
        data class MakeMainIp(val externalId: String) : Command
        // Backups
        data object RefreshBackups : Command
        data object LoadBackupsIfNeeded : Command
        data object OpenCreateBackupDialog : Command
        data object DismissCreateBackupDialog : Command
        data class ConfirmCreateBackup(val name: String) : Command
        data class DeleteBackup(val backupId: Long) : Command
        data class RestoreBackup(val backupId: Long) : Command
        data object OpenScheduleDialog : Command
        data object DismissScheduleDialog : Command
        data class ConfirmSetSchedule(
            val type: ServiceBackupScheduleType,
            val limit: Int,
            val weekDay: Int?,
            val monthDay: Int?,
        ) : Command
        data object DeleteSchedule : Command
        // History
        data object LoadHistoryIfNeeded : Command
        data object RefreshHistory : Command
        // Stats
        data object LoadStatsIfNeeded : Command
        data object RefreshStats : Command
        data class SelectStatType(val type: String) : Command
        // VNC
        data object ConnectVnc : Command
        data object DismissVncDialog : Command
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { loadService() }
    }

    fun processCommand(command: Command) {
        when (command) {
            // General
            is Command.Refresh -> viewModelScope.launch {
                _uiState.update { it.copy(isRefreshing = true) }
                loadService()
            }
            // Power
            is Command.Restart -> launchAction { restartService(serviceId) }
            is Command.Suspend -> launchAction { suspendService(serviceId) }
            is Command.Resume -> launchAction { resumeService(serviceId) }
            // Rescue
            is Command.EnterRescueMode -> launchAction { enterRescueUseCase(serviceId) }
            is Command.LeaveRescueMode -> launchAction { leaveRescueUseCase(serviceId) }
            // Change password
            is Command.OpenChangePasswordDialog ->
                _uiState.update { it.copy(showChangePasswordDialog = true) }
            is Command.DismissChangePasswordDialog ->
                _uiState.update { it.copy(showChangePasswordDialog = false) }
            is Command.ConfirmChangePassword -> {
                _uiState.update { it.copy(showChangePasswordDialog = false) }
                launchAction { changePasswordUseCase(serviceId, command.newPassword) }
            }
            // Reinstall
            is Command.OpenReinstallDialog ->
                _uiState.update { it.copy(showReinstallDialog = true) }
            is Command.DismissReinstallDialog ->
                _uiState.update { it.copy(showReinstallDialog = false) }
            is Command.ConfirmReinstall -> {
                _uiState.update { it.copy(showReinstallDialog = false) }
                launchAction {
                    reinstallUseCase(
                        serviceId,
                        ReinstallServiceRequest(
                            os = command.os,
                            recipe = command.recipe,
                            password = command.password,
                        ),
                    )
                }
            }
            // Delete
            is Command.OpenDeleteConfirmDialog ->
                _uiState.update { it.copy(showDeleteConfirmDialog = true) }
            is Command.DismissDeleteConfirmDialog ->
                _uiState.update { it.copy(showDeleteConfirmDialog = false) }
            is Command.ConfirmDelete -> {
                _uiState.update { it.copy(showDeleteConfirmDialog = false) }
                launchAction { requestDeletionUseCase(serviceId) }
            }
            // Network
            is Command.LoadNetworkIfNeeded -> {
                if (_uiState.value.ipv4List.isEmpty() && !_uiState.value.isNetworkLoading) {
                    _uiState.update { it.copy(isNetworkLoading = true) }
                    viewModelScope.launch { loadNetwork() }
                }
            }
            is Command.RefreshNetwork -> viewModelScope.launch {
                _uiState.update { it.copy(isNetworkRefreshing = true) }
                loadNetwork()
            }
            is Command.OpenEditPtrDialog ->
                _uiState.update { it.copy(editingPtrKey = command.key, editingPtrDomain = command.currentDomain) }
            is Command.DismissEditPtrDialog ->
                _uiState.update { it.copy(editingPtrKey = null, editingPtrDomain = "") }
            is Command.ConfirmEditPtr -> {
                val key = _uiState.value.editingPtrKey ?: return
                _uiState.update { it.copy(editingPtrKey = null, editingPtrDomain = "") }
                viewModelScope.launch {
                    editPtrUseCase(serviceId, key, command.domain)
                    _uiState.update { it.copy(isNetworkRefreshing = true) }
                    loadNetwork()
                }
            }
            is Command.MakeMainIp -> viewModelScope.launch {
                _uiState.update { it.copy(isNetworkLoading = true) }
                makeMainIpUseCase(serviceId, command.externalId)
                loadNetwork()
            }
            // Backups
            is Command.RefreshBackups -> viewModelScope.launch {
                _uiState.update { it.copy(isBackupsRefreshing = true) }
                loadBackups()
            }
            is Command.LoadBackupsIfNeeded -> {
                if (_uiState.value.backups.isEmpty() && !_uiState.value.isBackupsLoading) {
                    _uiState.update { it.copy(isBackupsLoading = true) }
                    viewModelScope.launch { loadBackups() }
                }
            }
            is Command.OpenCreateBackupDialog ->
                _uiState.update { it.copy(showCreateBackupDialog = true) }
            is Command.DismissCreateBackupDialog ->
                _uiState.update { it.copy(showCreateBackupDialog = false) }
            is Command.ConfirmCreateBackup -> {
                _uiState.update { it.copy(showCreateBackupDialog = false, isCreatingBackup = true) }
                viewModelScope.launch {
                    createBackupUseCase(serviceId, command.name)
                    _uiState.update { it.copy(isCreatingBackup = false) }
                    loadBackups()
                }
            }
            is Command.DeleteBackup -> viewModelScope.launch {
                _uiState.update { it.copy(deletingBackupIds = it.deletingBackupIds + command.backupId) }
                deleteBackupUseCase(serviceId, command.backupId)
                _uiState.update { it.copy(deletingBackupIds = it.deletingBackupIds - command.backupId) }
                loadBackups()
            }
            is Command.RestoreBackup -> viewModelScope.launch {
                _uiState.update { it.copy(restoringBackupId = command.backupId) }
                restoreBackupUseCase(serviceId, command.backupId)
                _uiState.update { it.copy(restoringBackupId = null) }
                loadService()
            }
            is Command.OpenScheduleDialog ->
                _uiState.update { it.copy(showScheduleDialog = true) }
            is Command.DismissScheduleDialog ->
                _uiState.update { it.copy(showScheduleDialog = false) }
            is Command.ConfirmSetSchedule -> {
                _uiState.update { it.copy(showScheduleDialog = false) }
                viewModelScope.launch {
                    setScheduleUseCase(
                        serviceId,
                        ServiceBackupSchedule(
                            limit = command.limit,
                            type = command.type,
                            weekDay = command.weekDay,
                            monthDay = command.monthDay,
                        ),
                    )
                    loadService()
                }
            }
            is Command.DeleteSchedule -> viewModelScope.launch {
                deleteScheduleUseCase(serviceId)
                loadService()
            }
            // History
            is Command.LoadHistoryIfNeeded -> {
                if (_uiState.value.tasks.isEmpty() && !_uiState.value.isHistoryLoading) {
                    _uiState.update { it.copy(isHistoryLoading = true) }
                    viewModelScope.launch { loadHistory() }
                }
            }
            is Command.RefreshHistory -> viewModelScope.launch {
                _uiState.update { it.copy(isHistoryRefreshing = true) }
                loadHistory()
            }
            // Stats
            is Command.LoadStatsIfNeeded -> {
                if (_uiState.value.statsData.isEmpty() && !_uiState.value.isStatsLoading) {
                    _uiState.update { it.copy(isStatsLoading = true) }
                    viewModelScope.launch { loadStats() }
                }
            }
            is Command.RefreshStats -> viewModelScope.launch {
                _uiState.update { it.copy(isStatsRefreshing = true) }
                loadStats()
            }
            is Command.SelectStatType -> {
                _uiState.update { it.copy(statsType = command.type, statsData = emptyList(), isStatsLoading = false) }
                processCommand(Command.LoadStatsIfNeeded)
            }
            // VNC
            is Command.ConnectVnc -> viewModelScope.launch {
                _uiState.update { it.copy(isVncLoading = true) }
                val session = connectVncUseCase(serviceId)
                _uiState.update {
                    it.copy(
                        isVncLoading = false,
                        showVncDialog = session != null,
                        vncAddress = session?.address ?: "",
                        vncPassword = session?.password ?: "",
                    )
                }
            }
            is Command.DismissVncDialog ->
                _uiState.update { it.copy(showVncDialog = false) }
        }
    }

    private fun launchAction(block: suspend () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isActionInProgress = true) }
            block()
            loadService()
            _uiState.update { it.copy(isActionInProgress = false) }
        }
    }

    private suspend fun loadService() {
        val accountDeferred = viewModelScope.async { getAccount() }
        val serviceDeferred = viewModelScope.async { getService(serviceId) }

        var symbol = "€"
        accountDeferred.await()?.let { acc ->
            symbol = currencySymbols[acc.currency.uppercase()] ?: acc.currency.uppercase()
            currencySymbol = symbol
        }

        val service = serviceDeferred.await() ?: run {
            _uiState.update { it.copy(isLoading = false, isRefreshing = false) }
            return
        }

        _uiState.update { prev -> service.toUiState(symbol, prev) }
    }

    private suspend fun loadBackups() {
        val page = getBackupsUseCase(serviceId)
        _uiState.update {
            it.copy(
                isBackupsLoading = false,
                isBackupsRefreshing = false,
                backups = page?.items?.map { b -> b.toUiItem() } ?: emptyList(),
            )
        }
    }

    private suspend fun loadNetwork() {
        val ipv4 = getIpv4UseCase(serviceId)
        val ipv6 = getIpv6UseCase(serviceId)
        _uiState.update {
            it.copy(
                isNetworkLoading = false,
                isNetworkRefreshing = false,
                ipv4List = ipv4?.items?.map { a -> a.toUiItem() } ?: emptyList(),
                ipv6List = ipv6?.items?.map { a -> a.toUiItem() } ?: emptyList(),
            )
        }
    }

    private suspend fun loadHistory() {
        val page = getTransactionsUseCase(serviceId)
        _uiState.update {
            it.copy(
                isHistoryLoading = false,
                isHistoryRefreshing = false,
                tasks = page?.items?.map { t -> t.toTaskUiItem(currencySymbol) } ?: emptyList(),
            )
        }
    }

    private suspend fun loadStats() {
        val request = ServiceStatsRequest(
            statType = _uiState.value.statsType,
            resolution = 60,
            fromDate = Instant.now().minusSeconds(86_400),
            toDate = Instant.now(),
        )
        val stats = getStatsUseCase(serviceId, request)
        _uiState.update {
            it.copy(
                isStatsLoading = false,
                isStatsRefreshing = false,
                statsData = stats?.data ?: emptyList(),
            )
        }
    }

    private fun Service.toUiState(symbol: String, prev: UiState) = prev.copy(
        isLoading = false,
        isRefreshing = false,
        name = name,
        flag = ServicesViewModel.locationCodeToFlag(locationCode),
        typeLabel = product.typeName,
        planName = productName,
        ip = ip,
        login = parameters["login"] as? String ?: "",
        password = secureParameters["password"] as? String ?: "",
        price = "$symbol ${price.divide(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)}",
        priceTerm = paymentTerm.toTermLabel(),
        statusLabel = status.toLabel(),
        isActive = status == ServiceStatus.ACTIVE,
        isInRescue = status == ServiceStatus.RESCUE,
        expiresDate = expiresAt.toDisplayDate(),
        createdDate = createdAt.toDisplayDate(),
        autoProlong = autoProlong,
        specs = product.localizedPayload.entries
            .filter { (_, v) -> v != null }
            .map { (k, v) -> k to v.toString() },
        currentTaskName = currentTask?.name,
        currentTaskStatus = currentTask?.status?.toLabel(),
        canRestart = ServiceCapability.RESTART in capabilities,
        canSuspend = ServiceCapability.CONTROL in capabilities && status == ServiceStatus.ACTIVE,
        canResume = ServiceCapability.CONTROL in capabilities && status == ServiceStatus.SUSPENDED,
        canChangePassword = ServiceCapability.CHANGE_PASSWORD in capabilities,
        canRescue = ServiceCapability.RESCUE in capabilities,
        canReinstall = ServiceCapability.REINSTALL in capabilities,
        canDelete = ServiceCapability.MANUAL_DELETE in capabilities,
        canBackups = ServiceCapability.BACKUPS in capabilities,
        canCharts = ServiceCapability.CHARTS in capabilities,
        canVnc = ServiceCapability.VNC in capabilities,
        backupScheduleType = schedule["type"] as? String,
        backupScheduleLimit = (schedule["limit"] as? Number)?.toInt() ?: 3,
        backupScheduleWeekDay = (schedule["week_day"] as? Number)?.toInt(),
        backupScheduleMonthDay = (schedule["month_day"] as? Number)?.toInt(),
        availableTabs = buildList {
            add(ServiceDetailTab.INFO)
            add(ServiceDetailTab.HISTORY)
            if (ServiceCapability.IP in capabilities) add(ServiceDetailTab.NETWORK)
            if (ServiceCapability.BACKUPS in capabilities) add(ServiceDetailTab.BACKUPS)
            if (ServiceCapability.CHARTS in capabilities) add(ServiceDetailTab.STATS)
            if (ServiceCapability.VNC in capabilities) add(ServiceDetailTab.VNC)
        },
    )

    private fun Ipv4Address.toUiItem() = Ipv4UiItem(
        key = key,
        address = value,
        gateway = gateway,
        mask = mask,
        domain = domain,
    )

    private fun Ipv6Address.toUiItem() = Ipv6UiItem(
        key = key,
        address = value,
        prefix = prefix,
        gateway = gateway,
        additionalIps = ips,
    )

    private fun ServiceTask.toUiItem() = TaskUiItem(
        id = id,
        name = name,
        createdDate = taskDateFormatter.format(createdAt),
        statusLabel = status.toLabel(),
        statusColor = when (status) {
            ServiceTaskStatus.SUCCESS -> 1
            ServiceTaskStatus.FAILED -> 2
            ServiceTaskStatus.RUNNING -> 3
            else -> 0
        },
    )

    private fun ServiceBackup.toUiItem() = BackupUiItem(
        id = id,
        name = name,
        sizeLabel = size?.let { formatSize(it) } ?: "—",
        createdDate = dateFormatter.format(createdAt),
        statusLabel = status.toLabel(),
        isActive = status == ServiceBackupStatus.ACTIVE,
    )

    companion object {
        private val currencySymbols = mapOf("EUR" to "€", "USD" to "$", "RUB" to "₽", "GBP" to "£")
        private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault())
        private val taskDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

        private fun Instant.toDisplayDate(): String = dateFormatter.format(this)

        private fun formatSize(bytes: Long): String = when {
            bytes >= 1_073_741_824 -> "%.1f GB".format(bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
            bytes >= 1_024 -> "%.1f KB".format(bytes / 1_024.0)
            else -> "$bytes B"
        }

        private fun ServiceTerm.toTermLabel() = when (this) {
            ServiceTerm.HOUR -> "/ час"
            ServiceTerm.HALF_DAY -> "/ полдня"
            ServiceTerm.DAY -> "/ день"
            ServiceTerm.WEEK -> "/ неделю"
            ServiceTerm.MONTH -> "/ месяц"
            ServiceTerm.QUARTER_YEAR -> "/ квартал"
            ServiceTerm.HALF_YEAR -> "/ полгода"
            ServiceTerm.YEAR -> "/ год"
            ServiceTerm.ETERNAL, ServiceTerm.UNKNOWN -> ""
        }

        private fun ServiceStatus.toLabel() = when (this) {
            ServiceStatus.ACTIVE -> "работает"
            ServiceStatus.ACTIVATION_WAIT -> "активация..."
            ServiceStatus.SUSPENDED -> "приостановлен"
            ServiceStatus.PROLONG_WAIT -> "продление..."
            ServiceStatus.DELETED -> "удалён"
            ServiceStatus.BLOCKED -> "заблокирован"
            ServiceStatus.RESCUE -> "rescue"
            ServiceStatus.UNKNOWN -> "неизвестно"
        }

        private fun ServiceTaskStatus.toLabel() = when (this) {
            ServiceTaskStatus.QUEUED -> "в очереди"
            ServiceTaskStatus.RUNNING -> "выполняется"
            ServiceTaskStatus.FAILED -> "ошибка"
            ServiceTaskStatus.SUCCESS -> "выполнено"
            ServiceTaskStatus.CANCELLED -> "отменено"
            ServiceTaskStatus.WAIT_CHILD -> "ожидание"
            ServiceTaskStatus.MANUAL -> "вручную"
            ServiceTaskStatus.UNKNOWN -> "неизвестно"
        }

        private fun ServiceBackupStatus.toLabel() = when (this) {
            ServiceBackupStatus.CREATING -> "создаётся"
            ServiceBackupStatus.ACTIVE -> "готов"
            ServiceBackupStatus.DELETED -> "удалён"
            ServiceBackupStatus.UNKNOWN -> "неизвестно"
        }

        private fun ServiceTransaction.toTaskUiItem(symbol: String): TaskUiItem {
            val displayDate = (performedAt ?: createdAt).let { taskDateFormatter.format(it) }
            val absValue = java.math.BigDecimal(amount).abs()
                .divide(java.math.BigDecimal(100))
                .setScale(2, java.math.RoundingMode.HALF_UP)
            val amountLabel = if (amount < 0) "−$symbol $absValue" else "+$symbol $absValue"
            return TaskUiItem(
                id = id.toString(),
                name = type.toTransactionTypeLabel(),
                createdDate = displayDate,
                statusLabel = amountLabel,
                statusColor = if (amount >= 0) 1 else 0,
            )
        }

        private fun String.toTransactionTypeLabel() = when (this) {
            "prolong" -> "Продление"
            "order" -> "Заказ"
            "refund" -> "Возврат"
            "correction" -> "Корректировка"
            "bonus" -> "Бонус"
            "penalty" -> "Штраф"
            else -> this.replaceFirstChar { it.uppercaseChar() }
        }
    }
}
