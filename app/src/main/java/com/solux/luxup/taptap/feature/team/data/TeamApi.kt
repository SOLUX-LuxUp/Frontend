package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.core.network.BaseResponse
import kotlinx.serialization.json.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TeamApi {

    @GET("api/teams")
    suspend fun listTeams(): Response<BaseResponse<List<TeamListItemDto>>>

    @POST("api/teams")
    suspend fun createTeam(@Body request: CreateTeamRequestDto): Response<BaseResponse<TeamResponseDto>>

    @POST("api/teams/join")
    suspend fun joinTeam(@Body request: JoinTeamRequestDto): Response<BaseResponse<JoinTeamResponseDto>>

    @GET("api/teams/{team_id}/settings")
    suspend fun getSettings(@Path("team_id") teamId: Long): Response<BaseResponse<TeamSettingsResponseDto>>

    @PATCH("api/teams/{team_id}/settings")
    suspend fun updateSettings(
        @Path("team_id") teamId: Long,
        @Body request: UpdateTeamSettingsRequestDto,
    ): Response<BaseResponse<UpdateTeamSettingsResponseDto>>

    @PATCH("api/teams/{team_id}/notification")
    suspend fun toggleNotification(@Path("team_id") teamId: Long): Response<BaseResponse<TeamNotificationToggleResponseDto>>

    @PATCH("api/teams/{team_id}/favorite")
    suspend fun toggleFavorite(@Path("team_id") teamId: Long): Response<BaseResponse<FavoriteResponseDto>>

    @GET("api/teams/{team_id}/members")
    suspend fun listMembers(@Path("team_id") teamId: Long): Response<BaseResponse<List<TeamMemberListItemDto>>>

    @DELETE("api/teams/{team_id}")
    suspend fun deleteTeam(@Path("team_id") teamId: Long): Response<BaseResponse<DeleteTeamResponseDto>>

    @DELETE("api/teams/{team_id}/members/{user_id}")
    suspend fun kickMember(
        @Path("team_id") teamId: Long,
        @Path("user_id") userId: Long,
    ): Response<BaseResponse<KickMemberResponseDto>>

    @DELETE("api/teams/{team_id}/leave")
    suspend fun leaveTeam(@Path("team_id") teamId: Long): Response<BaseResponse<LeaveTeamResponseDto>>

    // ---- team-template-controller ----

    @GET("api/team-templates")
    suspend fun listTeamTemplates(): Response<BaseResponse<List<TeamTemplateDto>>>

    @GET("api/teams/{team_id}/template")
    suspend fun getTemplateStatus(@Path("team_id") teamId: Long): Response<BaseResponse<TeamTemplateStatusResponseDto>>

    @POST("api/teams/{team_id}/template")
    suspend fun selectTemplate(
        @Path("team_id") teamId: Long,
        @Body request: ApplyTeamTemplateRequestDto,
    ): Response<BaseResponse<ApplyTeamTemplateResponseDto>>

    @POST("api/teams/{team_id}/template/skip")
    suspend fun skipTemplate(@Path("team_id") teamId: Long): Response<BaseResponse<SkipTeamTemplateResponseDto>>

    @GET("api/teams/{team_id}/template/suggestions")
    suspend fun getTemplateSuggestions(@Path("team_id") teamId: Long): Response<BaseResponse<List<TemplateSuggestionDto>>>

    // ---- team-button-controller ----

    @GET("api/teams/{team_id}/buttons")
    suspend fun listButtons(@Path("team_id") teamId: Long): Response<BaseResponse<List<TeamButtonListItemDto>>>

    @POST("api/teams/{team_id}/buttons")
    suspend fun createButton(
        @Path("team_id") teamId: Long,
        @Body request: CreateTeamButtonRequestDto,
    ): Response<BaseResponse<TeamButtonResponseDto>>

    @GET("api/teams/{team_id}/buttons/{team_button_id}")
    suspend fun getButtonDetail(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
    ): Response<BaseResponse<TeamButtonDetailResponseDto>>

    @DELETE("api/teams/{team_id}/buttons/{team_button_id}")
    suspend fun deleteButton(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
    ): Response<BaseResponse<DeleteTeamButtonResponseDto>>

    @PATCH("api/teams/{team_id}/buttons/{team_button_id}")
    suspend fun updateButton(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
        @Body request: UpdateTeamButtonRequestDto,
    ): Response<BaseResponse<UpdateTeamButtonResponseDto>>

    @PATCH("api/teams/{team_id}/buttons/{team_button_id}/notification")
    suspend fun toggleButtonNotification(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
    ): Response<BaseResponse<ButtonNotificationToggleResponseDto>>

    @GET("api/teams/{team_id}/buttons/{team_button_id}/records")
    suspend fun getTimeline(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
        @Query("cursor") cursor: Long? = null,
        @Query("limit") limit: Int? = null,
    ): Response<BaseResponse<TeamButtonTimelineResponseDto>>

    @POST("api/teams/{team_id}/buttons/{team_button_id}/records")
    suspend fun createRecord(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
        @Body request: CreateTeamButtonRecordRequestDto,
    ): Response<BaseResponse<CreateTeamButtonRecordResponseDto>>

    @GET("api/teams/{team_id}/buttons/{team_button_id}/records/latest")
    suspend fun getLatestRecord(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
    ): Response<BaseResponse<LatestRecordResponseDto>>

    @PATCH("api/teams/{team_id}/buttons/{team_button_id}/records/{record_id}/detail")
    suspend fun updateRecordDetail(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
        @Path("record_id") recordId: Long,
        @Body request: UpdateTeamButtonRecordDetailRequestDto,
    ): Response<BaseResponse<UpdateTeamButtonRecordDetailResponseDto>>

    @DELETE("api/teams/{team_id}/buttons/{team_button_id}/records/{record_id}")
    suspend fun deleteRecord(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
        @Path("record_id") recordId: Long,
    ): Response<BaseResponse<JsonElement>>

    @GET("api/teams/{team_id}/buttons/categories")
    suspend fun getButtonCategories(@Path("team_id") teamId: Long): Response<BaseResponse<List<TeamButtonCategoryResponseDto>>>

    // ---- team-button-permission-controller ----

    @POST("api/teams/{team_id}/buttons/{team_button_id}/permission/request")
    suspend fun requestPermission(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
    ): Response<BaseResponse<TapPermissionRequestResponseDto>>

    @PATCH("api/teams/{team_id}/buttons/{team_button_id}/permission/{user_id}")
    suspend fun decidePermission(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
        @Path("user_id") userId: Long,
        @Body request: TapPermissionDecisionRequestDto,
    ): Response<BaseResponse<TapPermissionDecisionResponseDto>>

    @GET("api/teams/{team_id}/buttons/{team_button_id}/permission/requests")
    suspend fun listPendingRequests(
        @Path("team_id") teamId: Long,
        @Path("team_button_id") teamButtonId: Long,
    ): Response<BaseResponse<List<TapPermissionRequestListItemDto>>>

    // ---- team-button-controller: 카테고리 생성/수정/삭제 ----

    @POST("api/teams/{team_id}/buttons/categories")
    suspend fun createButtonCategory(
        @Path("team_id") teamId: Long,
        @Body request: CreateTeamButtonCategoryRequestDto,
    ): Response<BaseResponse<CreateTeamButtonCategoryResponseDto>>

    @PATCH("api/teams/{team_id}/buttons/categories/{category_id}")
    suspend fun updateButtonCategory(
        @Path("team_id") teamId: Long,
        @Path("category_id") categoryId: Long,
        @Body request: UpdateTeamButtonCategoryRequestDto,
    ): Response<BaseResponse<UpdateTeamButtonCategoryResponseDto>>

    @DELETE("api/teams/{team_id}/buttons/categories/{category_id}")
    suspend fun deleteButtonCategory(
        @Path("team_id") teamId: Long,
        @Path("category_id") categoryId: Long,
        @Query("delete_buttons") deleteButtons: Boolean,
    ): Response<BaseResponse<JsonElement>>
}
