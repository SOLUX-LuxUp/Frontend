package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.core.network.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

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
}
