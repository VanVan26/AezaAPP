package com.shefivan.aezaapp.data.remote.api

import com.shefivan.aezaapp.data.remote.dto.AccountResponseDto
import com.shefivan.aezaapp.data.remote.dto.ChangePasswordRequestDto
import com.shefivan.aezaapp.data.remote.dto.CreateDomainRecordRequestDto
import com.shefivan.aezaapp.data.remote.dto.CreateDomainRequestDto
import com.shefivan.aezaapp.data.remote.dto.CreateFileUploadLinkRequestDto
import com.shefivan.aezaapp.data.remote.dto.CreateServiceBackupRequestDto
import com.shefivan.aezaapp.data.remote.dto.CreateSshKeyRequestDto
import com.shefivan.aezaapp.data.remote.dto.CreateTicketRequestDto
import com.shefivan.aezaapp.data.remote.dto.DomainListResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainRecordListResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainRecordResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainRecordTypeResponseDto
import com.shefivan.aezaapp.data.remote.dto.DomainResponseDto
import com.shefivan.aezaapp.data.remote.dto.EditDomainRecordRequestDto
import com.shefivan.aezaapp.data.remote.dto.EditSshKeyRequestDto
import com.shefivan.aezaapp.data.remote.dto.FileAssetResponseDto
import com.shefivan.aezaapp.data.remote.dto.FileUploadLinkResponseDto
import com.shefivan.aezaapp.data.remote.dto.Ipv4ListResponseDto
import com.shefivan.aezaapp.data.remote.dto.Ipv6ListResponseDto
import com.shefivan.aezaapp.data.remote.dto.NotificationListResponseDto
import com.shefivan.aezaapp.data.remote.dto.NotificationResponseDto
import com.shefivan.aezaapp.data.remote.dto.ReinstallServiceRequestDto
import com.shefivan.aezaapp.data.remote.dto.RemoteVncResponseDto
import com.shefivan.aezaapp.data.remote.dto.SaveUploadedFileRequestDto
import com.shefivan.aezaapp.data.remote.dto.SendTicketMessageRequestDto
import com.shefivan.aezaapp.data.remote.dto.ServiceBackupListResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceTaskListResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceBackupResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceStatsResponseDto
import com.shefivan.aezaapp.data.remote.dto.ServiceSuspendRequestDto
import com.shefivan.aezaapp.data.remote.dto.ServicesNetworksEditPtrRequestDto
import com.shefivan.aezaapp.data.remote.dto.ServicesListResponseDto
import com.shefivan.aezaapp.data.remote.dto.SetServiceBackupScheduleRequestDto
import com.shefivan.aezaapp.data.remote.dto.SetTicketReactionRequestDto
import com.shefivan.aezaapp.data.remote.dto.SshKeyListResponseDto
import com.shefivan.aezaapp.data.remote.dto.SshKeyResponseDto
import com.shefivan.aezaapp.data.remote.dto.SupportTicketsResponseDto
import com.shefivan.aezaapp.data.remote.dto.SystemAlertResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketMessageListResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketMessageResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketRateRequestDto
import com.shefivan.aezaapp.data.remote.dto.TicketRateResponseDto
import com.shefivan.aezaapp.data.remote.dto.TicketResponseDto
import com.shefivan.aezaapp.data.remote.dto.TransactionListResponseDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AezaApiService {
    @GET("health")
    suspend fun getHealth(): ResponseBody

    @GET("version")
    suspend fun getVersion(): String

    @GET("system/alerts")
    suspend fun getSystemAlerts(@Query("slots") slots: String): List<SystemAlertResponseDto>

    @GET("accounts/me")
    suspend fun getAccount(): AccountResponseDto

    @GET("services")
    suspend fun getServices(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("sort") sort: String?,
        @Query("filter") filter: String?,
    ): ServicesListResponseDto

    @GET("services/{id}")
    suspend fun getService(@Path("id") id: Long): ServiceResponseDto

    @DELETE("services/{id}")
    suspend fun requestServiceDeletion(@Path("id") id: Long)

    @POST("services/{id}/ctl/resume")
    suspend fun resumeService(@Path("id") id: Long)

    @POST("services/{id}/ctl/suspend")
    suspend fun suspendService(
        @Path("id") id: Long,
        @Body body: ServiceSuspendRequestDto,
    )

    @POST("services/{id}/ctl/restart")
    suspend fun restartService(@Path("id") id: Long)

    @POST("services/{id}/rescue")
    suspend fun enterRescueMode(@Path("id") id: Long)

    @POST("services/{id}/rescue/leave")
    suspend fun leaveRescueMode(@Path("id") id: Long)

    @POST("services/{id}/remote/vnc")
    suspend fun connectRemoteVnc(@Path("id") id: Long): RemoteVncResponseDto

    @POST("services/{id}/change-password")
    suspend fun changeServicePassword(
        @Path("id") id: Long,
        @Body body: ChangePasswordRequestDto,
    )

    @POST("services/{id}/reinstall")
    suspend fun reinstallService(
        @Path("id") id: Long,
        @Body body: ReinstallServiceRequestDto,
    )

    @GET("services/{id}/stats/{statType}")
    suspend fun getServiceStats(
        @Path("id") id: Long,
        @Path("statType") statType: String,
        @Query("resolution") resolution: Int,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String,
    ): ServiceStatsResponseDto

    @GET("services/{id}/tasks")
    suspend fun getServiceTasks(
        @Path("id") id: Long,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
    ): ServiceTaskListResponseDto

    @GET("services/ssh-keys")
    suspend fun getSshKeys(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("sort") sort: String?,
        @Query("filter") filter: String?,
    ): SshKeyListResponseDto

    @POST("services/ssh-keys")
    suspend fun createSshKey(@Body body: CreateSshKeyRequestDto): SshKeyResponseDto

    @GET("services/ssh-keys/{sshKeyId}")
    suspend fun getSshKey(@Path("sshKeyId") id: Long): SshKeyResponseDto

    @PATCH("services/ssh-keys/{sshKeyId}")
    suspend fun editSshKey(
        @Path("sshKeyId") id: Long,
        @Body body: EditSshKeyRequestDto,
    ): SshKeyResponseDto

    @DELETE("services/ssh-keys/{sshKeyId}")
    suspend fun deleteSshKey(@Path("sshKeyId") id: Long)

    @GET("notifications")
    suspend fun getNotifications(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("sort") sort: String?,
        @Query("filter") filter: String?,
    ): NotificationListResponseDto

    @GET("notifications/{id}")
    suspend fun getNotification(@Path("id") id: Long): NotificationResponseDto

    @POST("notifications/read")
    suspend fun markAllNotificationsAsRead()

    @POST("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Long)

    @GET("services/{serviceId}/networks/ipv4")
    suspend fun getIpv4Addresses(@Path("serviceId") serviceId: Long): Ipv4ListResponseDto

    @GET("services/{serviceId}/networks/ipv6")
    suspend fun getIpv6Addresses(@Path("serviceId") serviceId: Long): Ipv6ListResponseDto

    @POST("services/{serviceId}/networks/ipv4/{externalId}/edit-ptr")
    suspend fun editIpv4Ptr(
        @Path("serviceId") serviceId: Long,
        @Path("externalId") externalId: String,
        @Body body: ServicesNetworksEditPtrRequestDto,
    )

    @POST("services/{serviceId}/networks/ipv4/{externalId}/make-main")
    suspend fun makeMainIpv4(
        @Path("serviceId") serviceId: Long,
        @Path("externalId") externalId: String,
    )

    @GET("services/{serviceId}/backups")
    suspend fun getServiceBackups(
        @Path("serviceId") serviceId: Long,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("sort") sort: String?,
        @Query("filter") filter: String?,
    ): ServiceBackupListResponseDto

    @POST("services/{serviceId}/backups")
    suspend fun createServiceBackup(
        @Path("serviceId") serviceId: Long,
        @Body body: CreateServiceBackupRequestDto,
    ): ServiceBackupResponseDto

    @DELETE("services/{serviceId}/backups/{backupId}")
    suspend fun deleteServiceBackup(
        @Path("serviceId") serviceId: Long,
        @Path("backupId") backupId: Long,
    )

    @POST("services/{serviceId}/backups/{backupId}/restore")
    suspend fun restoreServiceBackup(
        @Path("serviceId") serviceId: Long,
        @Path("backupId") backupId: Long,
    )

    @POST("services/{serviceId}/backups/schedule")
    suspend fun setServiceBackupSchedule(
        @Path("serviceId") serviceId: Long,
        @Body body: SetServiceBackupScheduleRequestDto,
    )

    @DELETE("services/{serviceId}/backups/schedule")
    suspend fun deleteServiceBackupSchedule(@Path("serviceId") serviceId: Long)

    // Domain
    @GET("domains")
    suspend fun getDomains(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("sort") sort: String?,
        @Query("filter") filter: String?,
    ): DomainListResponseDto

    @POST("domains")
    suspend fun createDomain(@Body body: CreateDomainRequestDto): DomainResponseDto

    @GET("domains/{id}")
    suspend fun getDomain(@Path("id") id: Long): DomainResponseDto

    @GET("domains/nameservers")
    suspend fun getExpectedNameservers(): List<String>

    @GET("domains/record-types")
    suspend fun getRecordTypes(): List<DomainRecordTypeResponseDto>

    @GET("domains/{domainId}/records")
    suspend fun getDomainRecords(
        @Path("domainId") domainId: Long,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
        @Query("sort") sort: String?,
        @Query("filter") filter: String?,
    ): DomainRecordListResponseDto

    @POST("domains/{domainId}/records")
    suspend fun createDomainRecord(
        @Path("domainId") domainId: Long,
        @Body body: CreateDomainRecordRequestDto,
    ): DomainRecordResponseDto

    @PATCH("domains/{domainId}/records/{recordId}")
    suspend fun editDomainRecord(
        @Path("domainId") domainId: Long,
        @Path("recordId") recordId: Long,
        @Body body: EditDomainRecordRequestDto,
    ): DomainRecordResponseDto

    @DELETE("domains/{domainId}/records/{recordId}")
    suspend fun deleteDomainRecord(
        @Path("domainId") domainId: Long,
        @Path("recordId") recordId: Long,
    )

    // File
    @POST("files/upload-link")
    suspend fun createFileUploadLink(@Body body: CreateFileUploadLinkRequestDto): FileUploadLinkResponseDto

    @POST("files")
    suspend fun saveUploadedFile(@Body body: SaveUploadedFileRequestDto): FileAssetResponseDto

    // Support
    @GET("support/tickets")
    suspend fun getSupportTickets(): SupportTicketsResponseDto

    @POST("support/tickets")
    suspend fun createSupportTicket(@Body body: CreateTicketRequestDto): TicketResponseDto

    @GET("support/tickets/{id}")
    suspend fun getSupportTicket(@Path("id") id: Long): TicketResponseDto

    @POST("support/tickets/{id}/archive")
    suspend fun archiveSupportTicket(@Path("id") id: Long)

    @GET("support/tickets/{ticketId}/messages")
    suspend fun getSupportTicketMessages(
        @Path("ticketId") ticketId: Long,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
    ): TicketMessageListResponseDto

    @POST("support/tickets/{ticketId}/messages")
    suspend fun sendSupportTicketMessage(
        @Path("ticketId") ticketId: Long,
        @Body body: SendTicketMessageRequestDto,
    ): TicketMessageResponseDto

    @POST("support/tickets/{ticketId}/read")
    suspend fun markSupportTicketAsRead(@Path("ticketId") ticketId: Long)

    @POST("support/tickets/{ticketId}/messages/{messageId}/reaction")
    suspend fun setTicketMessageReaction(
        @Path("ticketId") ticketId: Long,
        @Path("messageId") messageId: Long,
        @Body body: SetTicketReactionRequestDto,
    ): TicketMessageResponseDto

    @GET("support/tickets/{ticketId}/rate")
    suspend fun getSupportTicketRate(@Path("ticketId") ticketId: Long): TicketRateResponseDto

    @POST("support/tickets/{ticketId}/rate")
    suspend fun rateSupportTicket(
        @Path("ticketId") ticketId: Long,
        @Body body: TicketRateRequestDto,
    ): TicketRateResponseDto

    // Billing
    @GET("billing/transactions/{serviceId}")
    suspend fun getServiceTransactions(
        @Path("serviceId") serviceId: Long,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
    ): TransactionListResponseDto
}
